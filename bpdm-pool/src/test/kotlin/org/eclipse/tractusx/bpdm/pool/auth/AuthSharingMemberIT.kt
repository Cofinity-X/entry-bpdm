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

package org.eclipse.tractusx.bpdm.pool.auth

import org.eclipse.tractusx.bpdm.pool.Application
import org.eclipse.tractusx.bpdm.pool.api.client.PoolApiClient
import org.eclipse.tractusx.bpdm.test.containers.KeyCloakInitializer
import org.eclipse.tractusx.bpdm.test.containers.PostgreSQLContextInitializer
import org.eclipse.tractusx.bpdm.test.containers.SelfClientInitializer
import org.eclipse.tractusx.bpdm.test.util.AuthExpectationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
@ContextConfiguration(initializers = [
    PostgreSQLContextInitializer::class,
    KeyCloakInitializer::class,
    SelfClientAsSharingMemberInitializer::class
])
class AuthSharingMemberIT @Autowired constructor(
    poolApiClient: PoolApiClient,
): AuthTestBase(
    poolApiClient,
    AddressAuthExpectations(
        getAddresses = AuthExpectationType.Authorized,
        getAddress = AuthExpectationType.Authorized,
        postAddressSearch = AuthExpectationType.Authorized,
        postAddresses = AuthExpectationType.Forbidden,
        putAddresses = AuthExpectationType.Forbidden
    ),
    SiteAuthExpectations(
        getSites = AuthExpectationType.Authorized,
        getSite = AuthExpectationType.Authorized,
        postSiteSearch = AuthExpectationType.Authorized,
        postSites = AuthExpectationType.Forbidden,
        putSites = AuthExpectationType.Forbidden
    ),
    LegalEntityAuthExpectations(
        getLegalEntities = AuthExpectationType.Authorized,
        getLegalEntity = AuthExpectationType.Authorized,
        postLegalEntitySearch = AuthExpectationType.Authorized,
        postLegalEntities = AuthExpectationType.Forbidden,
        putLegalEntities = AuthExpectationType.Forbidden,
        getLegalEntityAddresses = AuthExpectationType.Authorized,
        getLegalEntitySites = AuthExpectationType.Authorized
    ),
    MetadataAuthExpectations(
        getLegalForm = AuthExpectationType.Authorized,
        postLegalForm = AuthExpectationType.Forbidden,
        getIdentifierType = AuthExpectationType.Authorized,
        postIdentifierType = AuthExpectationType.Forbidden,
        getAdminArea = AuthExpectationType.Authorized,
        getFieldQualityRules = AuthExpectationType.Authorized
    ),
    MembersAuthExpectations(
        postAddressSearch = AuthExpectationType.Forbidden,
        postSiteSearch = AuthExpectationType.Forbidden,
        postLegalEntitySearch = AuthExpectationType.Forbidden,
        postChangelogSearch = AuthExpectationType.Forbidden
    ),
    CxMembershipsAuthExpectations(
        getMemberships = AuthExpectationType.Forbidden,
        putMemberships = AuthExpectationType.Forbidden
    ),
    changelogAuthExpectation = AuthExpectationType.Authorized,
    bpnAuthExpectation = AuthExpectationType.Authorized
)

class SelfClientAsSharingMemberInitializer : SelfClientInitializer() {
    override val clientId: String
        get() = "sa-cl7-cx-1"
}