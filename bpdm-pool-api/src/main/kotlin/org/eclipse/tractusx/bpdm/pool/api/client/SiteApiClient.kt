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

package org.eclipse.tractusx.bpdm.pool.api.client

import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.pool.api.PoolSiteApi
import org.eclipse.tractusx.bpdm.pool.api.model.request.SiteBpnSearchRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.SitePartnerCreateRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.SitePartnerUpdateRequest
import org.eclipse.tractusx.bpdm.pool.api.model.response.*
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange

@HttpExchange("/api/catena/sites")
interface SiteApiClient : PoolSiteApi {
    @PostExchange
    override fun createSite(
        @RequestBody requests: Collection<SitePartnerCreateRequest>
    ): SitePartnerCreateResponseWrapper

    @GetExchange("/{bpns}")
    override fun getSite(
        @PathVariable bpns: String
    ): SiteWithMainAddressVerboseDto

    @GetExchange
    override fun getSitesPaginated(
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<SiteMatchVerboseDto>

    @PostExchange("/main-addresses/search")
    override fun searchMainAddresses(
        @RequestBody bpnS: Collection<String>
    ): Collection<MainAddressVerboseDto>

    @PostExchange("/search")
    override fun searchSites(
        @RequestBody siteSearchRequest: SiteBpnSearchRequest,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<SiteWithMainAddressVerboseDto>

    @PutExchange
    override fun updateSite(
        @RequestBody requests: Collection<SitePartnerUpdateRequest>
    ): SitePartnerUpdateResponseWrapper


}