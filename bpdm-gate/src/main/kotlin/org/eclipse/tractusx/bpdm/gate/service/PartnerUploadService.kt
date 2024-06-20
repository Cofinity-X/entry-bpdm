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
import org.eclipse.tractusx.bpdm.gate.api.model.response.BusinessPartnerInputDto
import org.eclipse.tractusx.bpdm.gate.model.PartnerUploadFileRow
import org.eclipse.tractusx.bpdm.gate.util.PartnerFileUtil
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class PartnerUploadService(
    private val businessPartnerService: BusinessPartnerService
) {

    private val logger = KotlinLogging.logger { }

    fun processFile(file: MultipartFile, ownerBpnl: String?): ResponseEntity<Collection<BusinessPartnerInputDto>> {
        val csvData: List<PartnerUploadFileRow> = PartnerFileUtil.parseCsv(file)
        val businessPartnerDtos = PartnerFileUtil.validateAndMapToBusinessPartnerInputRequests(csvData)
        val result = businessPartnerService.upsertBusinessPartnersInput(businessPartnerDtos, ownerBpnl)
        return ResponseEntity.ok(result)
    }

}