################################################################################
# Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
################################################################################

FROM maven:3.9-eclipse-temurin-21 AS build
ENV HOME=/home/app
COPY .  $HOME
WORKDIR  $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -B -U clean package -pl bpdm-gate -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
ENV HOME=/home/app
COPY --from=build $HOME/bpdm-gate/target/bpdm-gate.jar /usr/local/lib/bpdm/app.jar
ARG USERNAME=bpdm
ARG USERID=10001
ARG GID=10001
RUN addgroup -g $GID -S $USERNAME
RUN adduser -u $USERID -S $USERNAME $USERNAME
USER $USERNAME
WORKDIR /usr/local/lib/bpdm
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
