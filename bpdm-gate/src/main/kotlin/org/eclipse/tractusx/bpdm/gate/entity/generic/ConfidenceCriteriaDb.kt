/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.bpdm.gate.entity.generic

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.eclipse.tractusx.bpdm.common.model.BaseEntity
import java.time.LocalDateTime


@Entity
@Table(name = "confidence_criteria")
class ConfidenceCriteriaDb(
    @Column(name = "shared_by_owner", nullable = false)
    var sharedByOwner: Boolean,
    @Column(name = "checked_by_external_data_source", nullable = false)
    var checkedByExternalDataSource: Boolean,
    @Column(name = "number_of_business_partners", nullable = false)
    var numberOfBusinessPartners: Int,
    @Column(name = "last_confidence_check_at", nullable = false)
    var lastConfidenceCheckAt: LocalDateTime,
    @Column(name = "next_confidence_check_at", nullable = false)
    var nextConfidenceCheckAt: LocalDateTime,
    @Column(name = "confidence_level", nullable = false)
    var confidenceLevel: Int,
) : BaseEntity()