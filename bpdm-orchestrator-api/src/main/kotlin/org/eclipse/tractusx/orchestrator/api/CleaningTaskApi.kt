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

package org.eclipse.tractusx.orchestrator.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.eclipse.tractusx.orchestrator.api.model.TaskCreateRequest
import org.eclipse.tractusx.orchestrator.api.model.TaskCreateResponse
import org.eclipse.tractusx.orchestrator.api.model.TaskStateRequest
import org.eclipse.tractusx.orchestrator.api.model.TaskStateResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@RequestMapping("/api/cleaning-tasks", produces = [MediaType.APPLICATION_JSON_VALUE])
@HttpExchange("/api/cleaning-tasks")
interface CleaningTaskApi {

    @Operation(
        summary = "Create new cleaning tasks for given business partner data",
        description = "Create cleaning tasks for given business partner data in given cleaning mode. " +
                "The mode decides through which cleaning steps the given business partner data will go through. " +
                "The response contains the states of the created cleaning tasks in the order of given business partner data." +
                "If there is an error in the request no cleaning tasks are created (all or nothing). " +
                "For a single request, the maximum number of business partners in the request is limited to \${bpdm.api.upsert-limit} entries."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The states of successfully created cleaning tasks including the task identifier for tracking purposes."
            ),
            ApiResponse(responseCode = "400", description = "On malformed task create requests or reaching upsert limit", content = [Content()]),
        ]
    )
    @Tag(name = "Requester")
    @PostMapping
    @PostExchange
    fun createCleaningTasks(@RequestBody createRequest: TaskCreateRequest): TaskCreateResponse


    @Operation(
        summary = "Search for the state of cleaning tasks by task identifiers",
        description = "Returns the state of finished cleaning tasks based on the provided task identifiers."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The state of the finished cleaning tasks for the provided task identifiers."
            ),
            ApiResponse(responseCode = "400", description = "On malformed task search requests", content = [Content()]),
        ]
    )
    @PostMapping("/cleaning-tasks/state/search")
    fun searchCleaningTaskState(@RequestBody searchTaskIdRequest: TaskStateRequest): TaskStateResponse

}