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

package org.eclipse.tractusx.bpdm.pool.controller.v6

import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.pool.api.v6.PoolMembersApi
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LogisticAddressVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.model.request.AddressSearchRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.ChangelogSearchRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.LegalEntitySearchRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.SiteSearchRequest
import org.eclipse.tractusx.bpdm.pool.api.model.response.ChangelogEntryVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.LegalEntityWithLegalAddressVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.SiteWithMainAddressVerboseDto
import org.eclipse.tractusx.bpdm.pool.config.ControllerConfigProperties
import org.eclipse.tractusx.bpdm.pool.config.PermissionConfigProperties
import org.eclipse.tractusx.bpdm.pool.exception.BpdmRequestSizeException
import org.eclipse.tractusx.bpdm.pool.service.PartnerChangelogService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController("MemberControllerLegacy")
class MemberController(
    private val changelogService: PartnerChangelogService,
    private val controllerConfigProperties: ControllerConfigProperties,
    private val legalEntityLegacyServiceMapper: LegalEntityLegacyServiceMapper,
    private val siteLegacyServiceMapper: SiteLegacyServiceMapper,
    private val addressLegacyServiceMapper: AddressLegacyServiceMapper
) : PoolMembersApi {


    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_MEMBER_PARTNER})")
    override fun searchLegalEntities(
        searchRequest: LegalEntitySearchRequest,
        paginationRequest: PaginationRequest
    ): PageDto<LegalEntityWithLegalAddressVerboseDto> {
        return legalEntityLegacyServiceMapper.searchLegalEntities(
            LegalEntityLegacyServiceMapper.LegalEntitySearchRequest(
                bpnLs = searchRequest.bpnLs,
                legalName = searchRequest.legalName,
                isCatenaXMemberData = true
            ),
            paginationRequest
        )
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_MEMBER_PARTNER})")
    override fun postSiteSearch(searchRequest: SiteSearchRequest, paginationRequest: PaginationRequest): PageDto<SiteWithMainAddressVerboseDto> {
        return siteLegacyServiceMapper.searchSites(
            SiteLegacyServiceMapper.SiteSearchRequest(
                siteBpns =  searchRequest.siteBpns,
                legalEntityBpns = searchRequest.legalEntityBpns,
                name = searchRequest.name,
                isCatenaXMemberData = true
            ),
            paginationRequest
        )
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_MEMBER_PARTNER})")
    override fun searchAddresses(searchRequest: AddressSearchRequest, paginationRequest: PaginationRequest): PageDto<LogisticAddressVerboseDto> {
        return addressLegacyServiceMapper.searchAddresses(
            AddressLegacyServiceMapper.AddressSearchRequest(
                addressBpns = searchRequest.addressBpns,
                siteBpns = searchRequest.siteBpns,
                legalEntityBpns = searchRequest.legalEntityBpns,
                name = searchRequest.name,
                isCatenaXMemberData = true
            ),
            paginationRequest
        )
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_MEMBER_CHANGELOG})")
    override fun searchChangelogEntries(
        changelogSearchRequest: ChangelogSearchRequest,
        paginationRequest: PaginationRequest
    ): PageDto<ChangelogEntryVerboseDto> {
        changelogSearchRequest.bpns?.let { bpns ->
            if (bpns.size > controllerConfigProperties.searchRequestLimit) {
                throw BpdmRequestSizeException(bpns.size, controllerConfigProperties.searchRequestLimit)
            }
        }

        return changelogService.getChangeLogEntries(
            bpns = changelogSearchRequest.bpns,
            businessPartnerTypes = changelogSearchRequest.businessPartnerTypes,
            fromTime = changelogSearchRequest.timestampAfter,
            isCatenaXMemberData = true,
            pageIndex = paginationRequest.page,
            pageSize = paginationRequest.size
        )
    }

}