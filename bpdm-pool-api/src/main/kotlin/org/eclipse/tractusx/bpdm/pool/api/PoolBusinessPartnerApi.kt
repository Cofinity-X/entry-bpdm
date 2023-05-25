/*******************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.bpdm.pool.api


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.eclipse.tractusx.bpdm.common.dto.request.PaginationRequest
import org.eclipse.tractusx.bpdm.common.dto.response.PageResponse
import org.eclipse.tractusx.bpdm.pool.api.model.response.ChangelogEntryResponse
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import java.time.Instant

@RequestMapping("/api/catena/business-partners", produces = [MediaType.APPLICATION_JSON_VALUE])
@HttpExchange("/api/catena/business-partners")
interface PoolBusinessPartnerApi {

    @Operation(
        summary = "Get business partner changelog entries by bpn",
        description = "Get business partner changelog entries by bpn ignoring case."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "The changelog entries for the specified bpn"),
            ApiResponse(responseCode = "400", description = "On malformed pagination request", content = [Content()]),
            ApiResponse(responseCode = "404", description = "No business partner found for specified bpn", content = [Content()])
        ]
    )
    @GetMapping("/changelog")
    @GetExchange("/changelog")
    fun getChangelogEntries(
        @Parameter(description = "BPN values") @RequestParam(required = false) bpn: Array<String>?,
        @Parameter(description = "Modified after") @RequestParam(required = false) modifiedAfter: Instant?,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageResponse<ChangelogEntryResponse>
}