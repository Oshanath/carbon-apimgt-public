/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.apimgt.rest.api.publisher.v1.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.model.APIEndpointInfo;
import org.wso2.carbon.apimgt.api.model.Environment;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.SubtypeConfigurationDTO;
import org.wso2.carbon.apimgt.rest.api.util.exception.BadRequestException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Covers the endpoint check that guards deploying a revision to a gateway environment serving one stage only.
 * <p>
 * An AI API keeps its endpoints as named endpoints outside its endpoint configuration, so a configuration holding no
 * production endpoint does not mean the API has none. The check must accept such an API on a named endpoint of the
 * stage, as the generation-time guard (TemplateBuilderUtil#hasEndpoint) does, while still rejecting every other API
 * whose configuration lacks that stage.
 */
public class ApisApiServiceImplDeployEndpointCheckTestCase {

    private static final String REVISION_ID = "c7a9f2d4-1b3e-4f6a-8d5c-2e9b0a7f4c13";
    private static final String ORGANIZATION = "carbon.super";

    /**
     * Builds an API carrying an endpoint configuration with no production or sandbox endpoint, which is what an AI
     * API is left with once the endpoint it was created with is removed.
     *
     * @param subtype subtype of the API, or null for an API without one
     * @return the API
     */
    private static APIDTO apiWithoutConfiguredEndpoints(String subtype) {

        APIDTO apiDto = new APIDTO();
        apiDto.setId(REVISION_ID);
        Map<String, Object> endpointConfig = new HashMap<>();
        endpointConfig.put(APIConstants.API_ENDPOINT_CONFIG_PROTOCOL_TYPE, "http");
        apiDto.setEndpointConfig(endpointConfig);
        if (subtype != null) {
            SubtypeConfigurationDTO subtypeConfiguration = new SubtypeConfigurationDTO();
            subtypeConfiguration.setSubtype(subtype);
            apiDto.setSubtypeConfiguration(subtypeConfiguration);
        }
        return apiDto;
    }

    private static Environment productionOnlyEnvironment() {

        Environment environment = new Environment();
        environment.setName("Default");
        environment.setType(APIConstants.GATEWAY_ENV_TYPE_PRODUCTION);
        return environment;
    }

    private static APIProvider providerWithNamedEndpointOfStage(String deploymentStage) throws Exception {

        APIEndpointInfo endpoint = new APIEndpointInfo();
        endpoint.setId("f2c6d3a1-7b4e-4d90-9a11-6c8e5b2d7f43");
        endpoint.setName("Named " + deploymentStage + " Endpoint");
        endpoint.setDeploymentStage(deploymentStage);
        List<APIEndpointInfo> endpoints = Collections.singletonList(endpoint);
        APIProvider apiProvider = Mockito.mock(APIProvider.class);
        Mockito.when(apiProvider.getAllAPIEndpointsByUUID(REVISION_ID, ORGANIZATION)).thenReturn(endpoints);
        return apiProvider;
    }

    private static void validate(Environment environment, APIDTO apiDto, APIProvider apiProvider) throws Exception {

        Method validate = ApisApiServiceImpl.class.getDeclaredMethod("validateEndpointsForGatewayEnvironmentType",
                Environment.class, APIDTO.class, String.class, APIProvider.class, String.class);
        validate.setAccessible(true);
        try {
            validate.invoke(new ApisApiServiceImpl(), environment, apiDto, REVISION_ID, apiProvider, ORGANIZATION);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }

    @Test
    public void testAiApiIsDeployableOnANamedEndpointOfTheRequiredStage() throws Exception {

        validate(productionOnlyEnvironment(), apiWithoutConfiguredEndpoints(APIConstants.API_SUBTYPE_AI_API),
                providerWithNamedEndpointOfStage(APIConstants.APIEndpoint.PRODUCTION));
    }

    @Test
    public void testAiApiWithOnlyANamedEndpointOfTheOtherStageIsRejected() throws Exception {

        try {
            validate(productionOnlyEnvironment(), apiWithoutConfiguredEndpoints(APIConstants.API_SUBTYPE_AI_API),
                    providerWithNamedEndpointOfStage(APIConstants.APIEndpoint.SANDBOX));
            Assert.fail("An AI API with no production endpoint should not deploy to a production-only gateway");
        } catch (BadRequestException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("'production' endpoint"));
        }
    }

    @Test
    public void testOtherApiIsRejectedWhateverItsNamedEndpoints() throws Exception {

        APIProvider apiProvider = providerWithNamedEndpointOfStage(APIConstants.APIEndpoint.PRODUCTION);
        try {
            validate(productionOnlyEnvironment(), apiWithoutConfiguredEndpoints(null), apiProvider);
            Assert.fail("An API with no production endpoint configured should not deploy to a production-only gateway");
        } catch (BadRequestException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("'production' endpoint"));
        }
        Mockito.verifyZeroInteractions(apiProvider);
    }
}
