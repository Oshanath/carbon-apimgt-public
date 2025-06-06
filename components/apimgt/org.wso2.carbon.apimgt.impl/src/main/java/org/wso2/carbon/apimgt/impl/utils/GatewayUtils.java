/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.impl.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIConstants.AIAPIConstants;
import org.wso2.carbon.apimgt.api.gateway.GatewayAPIDTO;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIProductIdentifier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GatewayUtils {

    private static final Log log = LogFactory.getLog(GatewayUtils.class);

    public static void setCustomSequencesToBeRemoved(API api, GatewayAPIDTO gatewayAPIDTO) {

        String inSequence = APIUtil.getSequenceExtensionName(api) + APIConstants.API_CUSTOM_SEQ_IN_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(inSequence, gatewayAPIDTO.getSequencesToBeRemove()));
        String outSequence = APIUtil.getSequenceExtensionName(api) + APIConstants.API_CUSTOM_SEQ_OUT_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(outSequence, gatewayAPIDTO.getSequencesToBeRemove()));
        String faultSequence = APIUtil.getSequenceExtensionName(api) + APIConstants.API_CUSTOM_SEQ_FAULT_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(faultSequence, gatewayAPIDTO.getSequencesToBeRemove()));
    }

    public static void setCustomBackendToBeRemoved(GatewayAPIDTO gatewayAPIDTO) {
        String sandBoxBackend = APIUtil.getCustomBackendName(gatewayAPIDTO.getApiId(),
                APIConstants.API_KEY_TYPE_SANDBOX);
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(sandBoxBackend, gatewayAPIDTO.getSequencesToBeRemove()));
        String productionBackend = APIUtil.getCustomBackendName(gatewayAPIDTO.getApiId(),
                APIConstants.API_KEY_TYPE_PRODUCTION);
        gatewayAPIDTO.setSequencesToBeRemove(
                addStringToList(productionBackend, gatewayAPIDTO.getSequencesToBeRemove()));
    }

    public static void setCustomBackendToBeRemoved(APIProductIdentifier apiProductIdentifier, String apiUUID,
            GatewayAPIDTO gatewayAPIDTO) {
        String sandBoxBackend = APIUtil.getCustomBackendName(apiProductIdentifier.getUUID().concat("-" + apiUUID),
                APIConstants.API_KEY_TYPE_SANDBOX);
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(sandBoxBackend, gatewayAPIDTO.getSequencesToBeRemove()));
        String productionBackend = APIUtil.getCustomBackendName(apiProductIdentifier.getUUID().concat("-" + apiUUID),
                APIConstants.API_KEY_TYPE_PRODUCTION);
        gatewayAPIDTO.setSequencesToBeRemove(
                addStringToList(productionBackend, gatewayAPIDTO.getSequencesToBeRemove()));
    }

    public static String[] addStringToList(String key, String[] keys) {

        if (keys == null) {
            return new String[]{key};
        } else {
            Set<String> keyList = new HashSet<>();
            Collections.addAll(keyList, keys);
            keyList.add(key);
            return keyList.toArray(new String[keyList.size()]);
        }
    }

    public static void setEndpointsToBeRemoved(APIProductIdentifier apiProductIdentifier, String apiUUID,
                                               GatewayAPIDTO gatewayAPIDTO) {

        String endpointName =
                apiProductIdentifier.getName() + "--v" + apiProductIdentifier.getVersion().concat("--").concat(apiUUID);
        gatewayAPIDTO.setEndpointEntriesToBeRemove(addStringToList(
                endpointName + "_API" + APIConstants.API_DATA_SANDBOX_ENDPOINTS.replace("_endpoints", "") + "Endpoint",
                gatewayAPIDTO.getEndpointEntriesToBeRemove()));
        gatewayAPIDTO.setEndpointEntriesToBeRemove(addStringToList(
                endpointName + "_API" + APIConstants.API_DATA_PRODUCTION_ENDPOINTS.replace("_endpoints", "") +
                        "Endpoint", gatewayAPIDTO.getEndpointEntriesToBeRemove()));
    }

    public static void setEndpointsToBeRemoved(String apiName, String version, GatewayAPIDTO gatewayAPIDTO) {

        String endpointName = apiName + "--v" + version;
        gatewayAPIDTO.setEndpointEntriesToBeRemove(addStringToList(
                endpointName + "_API" + APIConstants.API_DATA_SANDBOX_ENDPOINTS
                        .replace("_endpoints", "") + "Endpoint",
                gatewayAPIDTO.getEndpointEntriesToBeRemove()));
        gatewayAPIDTO.setEndpointEntriesToBeRemove(addStringToList(
                endpointName + "_API" + APIConstants.API_DATA_PRODUCTION_ENDPOINTS
                        .replace("_endpoints", "") + "Endpoint",
                gatewayAPIDTO.getEndpointEntriesToBeRemove()));
        gatewayAPIDTO.setEndpointEntriesToBeRemove(addStringToList(
                endpointName + "_API_LLMEndpoint_*", gatewayAPIDTO.getEndpointEntriesToBeRemove()));
    }

    /**
     * Returns the SHA-256 hash of a given string.
     *
     * @param str the byte[] input to be hashed
     * @return hashed string
     */
    public static String hashString(byte[] str) {

        if (str == null) {
            return "";
        }
        String generatedHash = null;
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance(APIConstants.SHA_256);
            //Add str bytes to digest
            md.update(str);
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed str in hex format
            generatedHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        return generatedHash;
    }

    public static void setCustomSequencesToBeRemoved(APIProductIdentifier apiProductIdentifier, String apiUUID,
                                                     GatewayAPIDTO gatewayAPIDTO) {

        String inSequence = APIUtil.getSequenceExtensionName(apiProductIdentifier.getName(),
                apiProductIdentifier.getVersion()).concat("--").concat(apiUUID) + APIConstants.API_CUSTOM_SEQ_IN_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(inSequence, gatewayAPIDTO.getSequencesToBeRemove()));
        String outSequence = APIUtil.getSequenceExtensionName(apiProductIdentifier.getName(),
                apiProductIdentifier.getVersion()).concat("--").concat(apiUUID) + APIConstants.API_CUSTOM_SEQ_OUT_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(outSequence, gatewayAPIDTO.getSequencesToBeRemove()));
        String faultSequence = APIUtil.getSequenceExtensionName(apiProductIdentifier.getName(),
                apiProductIdentifier.getVersion()).concat("--").concat(apiUUID) + APIConstants.API_CUSTOM_SEQ_FAULT_EXT;
        gatewayAPIDTO.setSequencesToBeRemove(addStringToList(faultSequence, gatewayAPIDTO.getSequencesToBeRemove()));
    }

    public static String retrieveOauthClientSecretAlias(String name, String version, String type) {

        return name.concat("--v").concat(version).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_TYPE_OAUTH).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_CLIENT_SECRET).concat("--").concat(type);
    }

    public static String retrieveOAuthPasswordAlias(String name, String version, String type) {

        return name.concat("--v").concat(version).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_TYPE_OAUTH).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_PASSWORD).concat("--").concat(type);
    }

    public static String retrieveOAuthProxyPasswordAlias(String name, String version, String type) {

        return name.concat("--v").concat(version).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_TYPE_OAUTH).concat("--")
                .concat(APIConstants.ENDPOINT_SECURITY_PROXY_PASSWORD).concat("--").concat(type);
    }

    public static String retrieveBasicAuthAlias(String name, String version, String type) {

        return name.concat("--v").concat(version).concat("--").concat(type);
    }

    public static String retrieveAWSCredAlias(String name, String version, String type) {

        return name.concat("--v").concat(version).concat("--").concat(type);
    }

    public static String retrieveUniqueIdentifier(String apiId, String type) {

        return apiId.concat("--").concat(type);
    }

    public static long getTtl() {
        long ttl;
        APIManagerConfiguration config = ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().getAPIManagerConfiguration();
        String gwTokenCacheConfig = config.getFirstProperty(APIConstants.GATEWAY_TOKEN_CACHE_ENABLED);
        boolean isGWTokenCacheEnabled = Boolean.parseBoolean(gwTokenCacheConfig);

        if (isGWTokenCacheEnabled) {
            String apimKeyCacheExpiry = config.getFirstProperty(APIConstants.TOKEN_CACHE_EXPIRY);

            if (apimKeyCacheExpiry != null) {
                ttl = Long.parseLong(apimKeyCacheExpiry);
            } else {
                ttl = Long.valueOf(900);
            }
        } else {
            String ttlValue = config.getFirstProperty(APIConstants.JWT_EXPIRY_TIME);
            if (ttlValue != null) {
                ttl = Long.parseLong(ttlValue);
            } else {
                // 15 * 60 (convert 15 minutes to seconds)
                ttl = Long.valueOf(900);
            }
        }
        return ttl;
    }

    /**
     * Sets the endpoint sequences to be removed for a given API in the Gateway API DTO.
     *
     * @param api           The API for which endpoint sequences are being removed.
     * @param gatewayAPIDTO The DTO containing gateway API configurations, where the sequences
     *                      to be removed will be updated.
     */
    public static void setEndpointSequencesToBeRemoved(API api, GatewayAPIDTO gatewayAPIDTO) {

        String productionEndpointSequence =
                APIUtil.getEndpointSequenceName(api) + AIAPIConstants.ENDPOINT_SEQUENCE
                        + APIConstants.APIEndpoint.PRODUCTION;
        gatewayAPIDTO.setSequencesToBeRemove(
                addStringToList(productionEndpointSequence, gatewayAPIDTO.getSequencesToBeRemove()));
        String sandboxEndpointSequence =
                APIUtil.getEndpointSequenceName(api) + AIAPIConstants.ENDPOINT_SEQUENCE
                        + APIConstants.APIEndpoint.SANDBOX;
        gatewayAPIDTO.setSequencesToBeRemove(
                addStringToList(sandboxEndpointSequence, gatewayAPIDTO.getSequencesToBeRemove()));
    }
}
