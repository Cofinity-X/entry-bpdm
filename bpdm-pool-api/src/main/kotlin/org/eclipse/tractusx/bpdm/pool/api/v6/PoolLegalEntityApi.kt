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

package org.eclipse.tractusx.bpdm.pool.api.v6


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.pool.api.ApiCommons
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LogisticAddressVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.SiteVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.request.LegalEntityPartnerCreateRequest
import org.eclipse.tractusx.bpdm.pool.api.v6.model.request.LegalEntityPartnerUpdateRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.LegalEntitySearchRequest
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.LegalEntityPartnerCreateResponseWrapper
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.LegalEntityPartnerUpdateResponseWrapper
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.LegalEntityWithLegalAddressVerboseDto
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
interface PoolLegalEntityApi {

    @Operation(
        summary = "Returns legal entities by different search parameters",
        description = "This endpoint tries to find matches among all existing business partners of type legal entity, " +
                "filtering out partners which entirely do not match and ranking the remaining partners according to the accuracy of the match. "
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page of business partners matching the search criteria, may be empty"),
            ApiResponse(responseCode = "400", description = "On malformed search or pagination request", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @GetMapping(value = [ApiCommons.LEGAL_ENTITY_BASE_PATH_V6])
    fun getLegalEntities(
        @ParameterObject searchRequest: LegalEntitySearchRequest,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<LegalEntityWithLegalAddressVerboseDto>

    @Operation(
        summary = "Returns a legal entity by identifier, like BPN, DUNS or EU VAT ID, specified by the identifier type",
        description = "This endpoint tries to find a business partner by the specified identifier. " +
                "The identifier value is case insensitively compared but needs to be given exactly. " +
                "By default the value given is interpreted as a BPN. " +
                "By specifying the technical key of another identifier type" +
                "the value is matched against the identifiers of that given type."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found business partner with specified identifier"),
            ApiResponse(responseCode = "400", description = "On malformed request parameters", content = [Content()]),
            ApiResponse(
                responseCode = "404",
                description = "No business partner found under specified identifier or specified identifier type not found",
                content = [Content()]
            )
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @GetMapping(value = ["${ApiCommons.LEGAL_ENTITY_BASE_PATH_V6}/{idValue}"])
    fun getLegalEntity(
        @Parameter(description = "Identifier value") @PathVariable("idValue") idValue: String,
        @Parameter(description = "Type of identifier to use, defaults to BPN when omitted", schema = Schema(defaultValue = "BPN"))
        @RequestParam idType: String? = "BPN"
    ): LegalEntityWithLegalAddressVerboseDto

    @Operation(
        summary = "Returns legal entities by different search parameters",
        description = "Search legal entity partners by their BPNLs. " +
                "The response can contain less results than the number of BPNLs that were requested, if some of the BPNLs did not exist. " +
                "For a single request, the maximum number of BPNLs to search for is limited to \${bpdm.bpn.search-request-limit} entries."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found legal entites"),
            ApiResponse(
                responseCode = "400",
                description = "On malformed request parameters or if number of requested bpns exceeds limit",
                content = [Content()]
            )
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @PostMapping(value = ["${ApiCommons.LEGAL_ENTITY_BASE_PATH_V6}/search"])
    fun postLegalEntitySearch(
        @RequestBody searchRequest: LegalEntitySearchRequest,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<LegalEntityWithLegalAddressVerboseDto>

    @Operation(
        summary = "Returns all sites of a legal entity with a specific BPNL",
        description = "Get business partners of type site belonging to a business partner of type legal entity, identified by the business partner's bpnl ignoring case."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "The sites for the specified bpnl"),
            ApiResponse(responseCode = "400", description = "On malformed pagination request", content = [Content()]),
            ApiResponse(responseCode = "404", description = "No business partner found for specified bpnl", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @GetMapping(value = ["${ApiCommons.LEGAL_ENTITY_BASE_PATH_V6}/{bpnl}/sites"])
    fun getSites(
        @Parameter(description = "BPNL value") @PathVariable bpnl: String,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<SiteVerboseDto>

    @Operation(
        summary = "Returns all addresses of a legal entity with a specific BPNL",
        description = "Get business partners of type address belonging to a business partner of type legal entity, identified by the business partner's BPNL ignoring case."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "The addresses for the specified BPNL"),
            ApiResponse(responseCode = "400", description = "On malformed pagination request", content = [Content()]),
            ApiResponse(responseCode = "404", description = "No business partner found for specified BPNL", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @GetMapping(value = ["${ApiCommons.LEGAL_ENTITY_BASE_PATH_V6}/{bpnl}/addresses"])
    fun getAddresses(
        @Parameter(description = "BPNL value") @PathVariable bpnl: String,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<LogisticAddressVerboseDto>

    @Operation(
        summary = "Creates a new legal entity",
        description = "Create new business partners of type legal entity. " +
                "The given additional identifiers of a record need to be unique, otherwise they are ignored. " +
                "For matching purposes, on each record you can specify your own index value which will reappear in the corresponding record of the response."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "New legal entities request was processed successfully, possible errors are returned"),
            ApiResponse(responseCode = "400", description = "On malformed requests", content = [Content()]),
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @PostMapping(value = [ApiCommons.LEGAL_ENTITY_BASE_PATH_V6])
    fun createBusinessPartners(
        @RequestBody
        businessPartners: Collection<LegalEntityPartnerCreateRequest>
    ): LegalEntityPartnerCreateResponseWrapper


    @Operation(
        summary = "Updates an existing legal entity",
        description = "Update existing business partner records of type legal entity referenced via BPNL. " +
                "The endpoint expects to receive the full updated record, including values that didn't change."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update legal entities request was processed successfully, possible errors are returned"),
            ApiResponse(responseCode = "400", description = "On malformed requests", content = [Content()]),
        ]
    )
    @Tag(name = ApiCommons.LEGAL_ENTITIES_NAME, description = ApiCommons.LEGAL_ENTITIES_DESCRIPTION)
    @PutMapping(value = [ApiCommons.LEGAL_ENTITY_BASE_PATH_V6])
    fun updateBusinessPartners(
        @RequestBody
        businessPartners: Collection<LegalEntityPartnerUpdateRequest>
    ): LegalEntityPartnerUpdateResponseWrapper

}