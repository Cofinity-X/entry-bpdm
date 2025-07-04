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

package org.eclipse.tractusx.bpdm.cleaning.config

import jakarta.annotation.PostConstruct
import org.eclipse.tractusx.bpdm.cleaning.service.RelationCleaningServiceDummy
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger

@Configuration
class RelationCleaningTaskConfiguration(
    private val cleaningServiceConfigProperties: CleaningServiceConfigProperties,
    private val taskScheduler: TaskScheduler,
    private val relationCleaningServiceDummy: RelationCleaningServiceDummy
) {

    @PostConstruct
    fun scheduleRelationCleaningTask() {
        taskScheduler.scheduleIfEnabled(
            { relationCleaningServiceDummy.relationPollForCleanAndSyncTasks() },
            cleaningServiceConfigProperties.relationCleaning.cron
        )
    }

    private fun TaskScheduler.scheduleIfEnabled(task: Runnable, cronExpression: String) {
        if (cronExpression != "-") {
            schedule(task, CronTrigger(cronExpression))
        }
    }
}
