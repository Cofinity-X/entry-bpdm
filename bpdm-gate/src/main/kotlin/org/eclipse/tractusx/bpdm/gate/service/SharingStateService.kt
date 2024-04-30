/*******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.eclipse.tractusx.bpdm.gate.service

import mu.KotlinLogging
import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.common.service.toPageDto
import org.eclipse.tractusx.bpdm.gate.api.exception.BusinessPartnerSharingError
import org.eclipse.tractusx.bpdm.gate.api.model.SharingStateType
import org.eclipse.tractusx.bpdm.gate.api.model.response.SharingStateDto
import org.eclipse.tractusx.bpdm.gate.config.GoldenRecordTaskConfigProperties
import org.eclipse.tractusx.bpdm.gate.entity.SharingStateDb
import org.eclipse.tractusx.bpdm.gate.exception.BpdmInvalidStateException
import org.eclipse.tractusx.bpdm.gate.exception.BpdmMissingPartnerException
import org.eclipse.tractusx.bpdm.gate.repository.SharingStateRepository
import org.eclipse.tractusx.bpdm.gate.repository.SharingStateRepository.Specs.byAssociatedOwnerBpnl
import org.eclipse.tractusx.bpdm.gate.repository.SharingStateRepository.Specs.byExternalIdsIn
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class SharingStateService(
    private val stateRepository: SharingStateRepository,
    private val goldenRecordTaskConfigProperties: GoldenRecordTaskConfigProperties
) {

    private val logger = KotlinLogging.logger { }

    fun findSharingStates(
        paginationRequest: PaginationRequest,
        externalIds: Collection<String>?,
        ownerBpnl: String?
    ): PageDto<SharingStateDto> {

        logger.info { "findSharingStates() called with $paginationRequest // $externalIds" }

        val pageRequest = PageRequest.of(paginationRequest.page, paginationRequest.size)
        val spec = Specification.allOf(byExternalIdsIn(externalIds), byAssociatedOwnerBpnl(ownerBpnl))
        val sharingStatePage = stateRepository.findAll(spec, pageRequest)

        return sharingStatePage.toPageDto {
            SharingStateDto(
                externalId = it.externalId,
                sharingStateType = it.sharingStateType,
                sharingErrorCode = it.sharingErrorCode,
                sharingErrorMessage = it.sharingErrorMessage,
                sharingProcessStarted = it.sharingProcessStarted,
                taskId = it.taskId
            )
        }
    }

    fun setInitial(sharingStateIds: List<String>, ownerBpnl: String?): List<SharingStateDb> {
        val sharingStates = getOrCreate(sharingStateIds, ownerBpnl)
        return sharingStates.map { setInitial(it) }
    }



    fun setSuccess(successRequests: List<SuccessRequest>, ownerBpnl: String?): List<SharingStateDb> {
        val sharingStates = getOrCreate(successRequests.map { it.externalId }, ownerBpnl)
        return sharingStates
            .zip(successRequests)
            .map { (sharingState, request) -> setSuccess(sharingState, request.startTimeOverwrite) }
    }

    fun setPending(pendingRequests: List<PendingRequest>, ownerBpnl: String?): List<SharingStateDb> {
        val sharingStates = getOrCreate(pendingRequests.map { it.externalId }, ownerBpnl)
        return sharingStates
            .zip(pendingRequests)
            .map { (sharingState, request) -> setPending(sharingState, request.taskId, request.startTimeOverwrite) }
    }

    fun setError(errorRequests: List<ErrorRequest>, ownerBpnl: String?): List<SharingStateDb> {
        val sharingStates = getOrCreate(errorRequests.map { it.externalId }, ownerBpnl)

        return sharingStates
            .zip(errorRequests)
            .map { (sharingState, request) -> setError(sharingState, request.errorCode, request.errorMessage, request.startTimeOverwrite) }
    }

    fun setReady(externalIds: List<String>, ownerBpnl: String?): List<SharingStateDb> {
        val existingSharingStates = stateRepository.findByExternalIdInAndAssociatedOwnerBpnl(externalIds, ownerBpnl)
        val existingIds = existingSharingStates.map { it.externalId }.toSet()
        val missingIds = externalIds.minus(existingIds)

        if (missingIds.isNotEmpty())
            throw BpdmMissingPartnerException(missingIds)


        val (correctStates, incorrectStates) = existingSharingStates.partition {
            it.sharingStateType == SharingStateType.Initial
                    || it.sharingStateType == SharingStateType.Error
        }

        if (incorrectStates.isNotEmpty())
            throw BpdmInvalidStateException(incorrectStates.map { BpdmInvalidStateException.InvalidState(it.externalId, it.sharingStateType) })

        return correctStates.map { setReady(it) }
    }

    private fun setInitial(sharingState: SharingStateDb): SharingStateDb {
        sharingState.sharingStateType =
                //If new business partner data should be immediately ready to be shared our initial state is ready instead
            if (goldenRecordTaskConfigProperties.creation.fromSharingMember.startsAsReady)
                SharingStateType.Ready
            else
                SharingStateType.Initial
        sharingState.sharingErrorCode = null
        sharingState.sharingErrorMessage = null
        sharingState.sharingProcessStarted = null
        sharingState.taskId = null

        return stateRepository.save(sharingState)
    }

    private fun setSuccess(sharingState: SharingStateDb, startTimeOverwrite: LocalDateTime? = null): SharingStateDb {

        sharingState.sharingStateType = SharingStateType.Success
        sharingState.sharingErrorCode = null
        sharingState.sharingErrorMessage = null
        sharingState.sharingProcessStarted = startTimeOverwrite ?: sharingState.sharingProcessStarted ?: LocalDateTime.now()

        return stateRepository.save(sharingState)
    }

    private fun setPending(sharingState: SharingStateDb, taskId: String, startTimeOverwrite: LocalDateTime? = null): SharingStateDb {
        sharingState.sharingStateType = SharingStateType.Pending
        sharingState.sharingErrorCode = null
        sharingState.sharingErrorMessage = null
        sharingState.sharingProcessStarted = startTimeOverwrite ?: sharingState.sharingProcessStarted ?: LocalDateTime.now()
        sharingState.taskId = taskId

        return stateRepository.save(sharingState)
    }

    private fun setError(
        sharingState: SharingStateDb,
        sharingErrorCode: BusinessPartnerSharingError,
        sharingErrorMessage: String? = null,
        startTimeOverwrite: LocalDateTime? = null
    ): SharingStateDb {
        sharingState.sharingStateType = SharingStateType.Error
        sharingState.sharingErrorCode = sharingErrorCode
        sharingState.sharingErrorMessage = sharingErrorMessage
        sharingState.sharingProcessStarted = startTimeOverwrite ?: sharingState.sharingProcessStarted ?: LocalDateTime.now()

        return stateRepository.save(sharingState)
    }

    private fun setReady(
        sharingState: SharingStateDb
    ): SharingStateDb {
        sharingState.sharingStateType = SharingStateType.Ready
        sharingState.sharingErrorCode = null
        sharingState.sharingErrorMessage = null
        sharingState.sharingProcessStarted = null
        sharingState.taskId = null

        return stateRepository.save(sharingState)
    }


    private fun getOrCreate(externalIds: List<String>, ownerBpnl: String?): List<SharingStateDb> {
        val sharingStates = stateRepository.findByExternalIdInAndAssociatedOwnerBpnl(externalIds, ownerBpnl)
        val sharingStatesByExternalId = sharingStates.associateBy { it.externalId }

        return externalIds.map { externalId ->
            sharingStatesByExternalId[externalId]
                ?: SharingStateDb(
                    externalId,
                    sharingStateType = SharingStateType.Ready,
                    sharingErrorCode = null,
                    sharingErrorMessage = null,
                    sharingProcessStarted = null,
                    associatedOwnerBpnl = ownerBpnl
                )
        }
    }

    data class PendingRequest(
        val externalId: String,
        val taskId: String,
        val startTimeOverwrite: LocalDateTime? = null
    )

    data class SuccessRequest(
        val externalId: String,
        val startTimeOverwrite: LocalDateTime? = null
    )

    data class ErrorRequest(
        val externalId: String,
        val errorCode: BusinessPartnerSharingError,
        val errorMessage: String?,
        val startTimeOverwrite: LocalDateTime? = null
    )
}