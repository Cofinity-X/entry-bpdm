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

import com.neovisionaries.i18n.CountryCode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.pool.api.ApiCommons
import org.eclipse.tractusx.bpdm.pool.api.model.IdentifierBusinessPartnerType
import org.eclipse.tractusx.bpdm.pool.api.v6.model.IdentifierTypeDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LegalFormDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.request.LegalFormRequest
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
interface PoolMetadataApi {

    companion object DescriptionObject {
        const val technicalKeyDisclaimer =
            "The technical key can be freely chosen but needs to be unique for the businessPartnerType as it is used as reference by the business partner records. " +
                    "A recommendation for technical keys: They should be short, descriptive and " +
                    "use a restricted common character set in order to ensure compatibility with older systems."
    }

    @Operation(
        summary = "Creates a new identifier type",
        description = "Create a new identifier type (including validity details) which can be referenced by business partner records. " +
                "Identifier types such as BPN or VAT determine with which kind of values a business partner can be identified with. " +
                "The actual name of the identifier type is free to choose and doesn't need to be unique. $technicalKeyDisclaimer"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "New identifier type successfully created"),
            ApiResponse(responseCode = "400", description = "On malformed request parameters", content = [Content()]),
            ApiResponse(responseCode = "409", description = "Identifier type with specified technical key already exists", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.METADATA_NAME, description = ApiCommons.METADATA_DESCRIPTION)
    @PostMapping(value = ["${ApiCommons.BASE_PATH_V6}/identifier-types"])
    fun createIdentifierType(@RequestBody identifierType: IdentifierTypeDto): IdentifierTypeDto

    @Operation(
        summary = "Returns all identifier types filtered by business partner type and country.",
        description = "Lists all matching identifier types including validity details in a paginated result"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page of existing identifier types, may be empty"),
            ApiResponse(responseCode = "400", description = "On malformed request parameters", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.METADATA_NAME, description = ApiCommons.METADATA_DESCRIPTION)
    @GetMapping(value = ["${ApiCommons.BASE_PATH_V6}/identifier-types"])
    fun getIdentifierTypes(
        @ParameterObject paginationRequest: PaginationRequest,
        @Parameter businessPartnerType: IdentifierBusinessPartnerType,
        @Parameter country: CountryCode?
    ):
            PageDto<IdentifierTypeDto>


    @Operation(
        summary = "Creates a new legal form",
        description = "Create a new legal form which can be referenced by business partner records. " +
                "The actual name of the legal form is free to choose and doesn't need to be unique. " + technicalKeyDisclaimer
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "New legal form successfully created"),
            ApiResponse(responseCode = "400", description = "On malformed request parameters", content = [Content()]),
            ApiResponse(responseCode = "409", description = "Legal form with specified technical key already exists", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.METADATA_NAME, description = ApiCommons.METADATA_DESCRIPTION)
    @PostMapping(value = ["${ApiCommons.BASE_PATH_V6}/legal-forms"])
    fun createLegalForm(@RequestBody type: LegalFormRequest): LegalFormDto

    @Operation(
        summary = "Returns all legal forms",
        description = "Lists all currently known legal forms in a paginated result"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Page of existing legal forms, may be empty"),
            ApiResponse(responseCode = "400", description = "On malformed request parameters", content = [Content()])
        ]
    )
    @Tag(name = ApiCommons.METADATA_NAME, description = ApiCommons.METADATA_DESCRIPTION)
    @GetMapping(value = ["${ApiCommons.BASE_PATH_V6}/legal-forms"])
    fun getLegalForms(@ParameterObject paginationRequest: PaginationRequest): PageDto<LegalFormDto>

}