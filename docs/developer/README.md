# Developer View

Documentation here concerns developers who want to contribute to this repository.

## Apps and Charts

The CICD pipeline tests new code contributions by deploying them in the current version of the BPDM chart.
In this way we can perform system tests in an environment that get close to actual productive environments as possible.
As a result, the BPDM charts always should be up-to-date with the newest code contributions.
If a breaking change of an app feature would lead to incompatibility with the current charts, the charts need to be updated.
You can say that changing the chart accordingly is also part of any app feature or fix. This has the following advantages and disadvantages.

Advantages:
- The whole code base is always up-to-date and compatible with each other
- Allows for more sophisticated testing leading to lower errors on the main branch

Disadvantages:
- Changes in the apps have direct impact on the charts (Leading to potentially bigger and more complicated pull requests)

## License Check

Licenses of all maven dependencies need to be approved by eclipse.
The Eclipse Dash License Tool can be used to check the license approval status of dependencies and to request reviews by the intellectual property team.

Generate summary of dependencies and their approval status:

```bash
mvn org.eclipse.dash:license-tool-plugin:license-check -Ddash.summary=DEPENDENCIES
```

Automatically create IP Team review requests:

```bash
mvn org.eclipse.dash:license-tool-plugin:license-check -Ddash.iplab.token=<token>
```

Check the [Eclipse Dash License Tool documentation](https://github.com/eclipse/dash-licenses) for more detailed information.

## Branching Strategy

```mermaid
---
title: BPDM Branching Strategy (App Tags)
---
gitGraph TB:
    commit tag: "v1.0.0-alpha.0"
    branch "app-feature A"
    branch "app-feature B"
    checkout "app-feature A"
    commit
    commit
    checkout "main"
    merge "app-feature A" tag:  "v1.0.0-alpha.1"
    checkout "app-feature B"
    commit
    checkout "main"
    merge "app-feature B" tag: "v1.0.0"
    branch "chart-feature C"
    commit
    checkout "main"
    merge "chart-feature C"
    branch "app-feature D"
    commit
    checkout "main"
    merge "app-feature D" tag: "v1.0.1-alpha.0"
    branch "app-feature E"
    commit
    checkout "main"
    merge "app-feature E" tag: "v1.1.0-alpha.0"
```

```mermaid
---
title: BPDM Branching Strategy (Chart Tags)
---
gitGraph TB:
    commit tag: "bpdm-1.0.0-alpha.0"
    branch "app-feature A"
    branch "app-feature B"
    checkout "app-feature A"
    commit
    commit
    checkout "main"
    merge "app-feature A" tag:  "bpdm-1.0.0-alpha.1"
    checkout "app-feature B"
    commit
    checkout "main"
    merge "app-feature B" tag: "bpdm-1.0.0"
    branch "chart-feature C"
    commit
    checkout "main"
    merge "chart-feature C" tag: "bpdm-1.0.1"
    branch "app-feature D"
    commit
    checkout "main"
    merge "app-feature D" tag: "bpdm-1.0.1-alpha.0"
    branch "app-feature E"
    commit
    checkout "main"
    merge "app-feature E" tag: "bpdm-1.1.0-alpha.0"
```

## NOTICE

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

- SPDX-License-Identifier: Apache-2.0
- SPDX-FileCopyrightText: 2023,2024 ZF Friedrichshafen AG
- SPDX-FileCopyrightText: 2023,2024 SAP SE
- SPDX-FileCopyrightText: 2023,2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
- SPDX-FileCopyrightText: 2023,2024 Mercedes Benz Group
- SPDX-FileCopyrightText: 2023,2024 Robert Bosch GmbH
- SPDX-FileCopyrightText: 2023,2024 Schaeffler AG
- SPDX-FileCopyrightText: 2023,2024 Contributors to the Eclipse Foundation
- Source URL: https://github.com/eclipse-tractusx/bpdm