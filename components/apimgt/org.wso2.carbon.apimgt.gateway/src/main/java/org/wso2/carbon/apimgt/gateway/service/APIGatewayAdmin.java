/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.gateway.service;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.gateway.CredentialDto;
import org.wso2.carbon.apimgt.api.gateway.GatewayAPIDTO;
import org.wso2.carbon.apimgt.api.gateway.GatewayContentDTO;
import org.wso2.carbon.apimgt.api.gateway.GatewayPolicyDTO;
import org.wso2.carbon.apimgt.gateway.utils.EndpointAdminServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.GatewayUtils;
import org.wso2.carbon.apimgt.gateway.utils.LocalEntryServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.MediationSecurityAdminServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.RESTAPIAdminServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.SequenceAdminServiceProxy;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.certificatemgt.CertificateManager;
import org.wso2.carbon.apimgt.impl.certificatemgt.CertificateManagerImpl;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.rest.api.APIData;
import org.wso2.carbon.rest.api.ResourceData;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.apimgt.api.APIConstants.AIAPIConstants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

public class APIGatewayAdmin extends org.wso2.carbon.core.AbstractAdmin {

    private static Log log = LogFactory.getLog(APIGatewayAdmin.class);

    public APIGatewayAdmin() {

    }

    /**
     * Add the API to the gateway
     *
     * @param apiProviderName
     * @param apiName
     * @param version
     * @param apiConfig
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean addApiForTenant(String apiProviderName, String apiName, String version, String apiConfig,
                                   String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        return restClient.addApi(apiConfig);
    }

    protected RESTAPIAdminServiceProxy getRestapiAdminClient(String tenantDomain) throws AxisFault {

        return new RESTAPIAdminServiceProxy(tenantDomain);
    }

    public boolean addApi(String apiProviderName, String apiName, String version, String apiConfig) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return restClient.addApi(apiConfig);
    }

    /**
     * Add the API to the gateway
     *
     * @param apiProviderName
     * @param apiName
     * @param version
     * @param apiConfig
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean addPrototypeApiScriptImplForTenant(String apiProviderName, String apiName, String version,
                                                      String apiConfig, String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        return restClient.addApi(apiConfig);
    }

    public boolean addPrototypeApiScriptImpl(String apiProviderName, String apiName, String version, String apiConfig)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return restClient.addApi(apiConfig);
    }

    public boolean addDefaultAPIForTenant(String apiProviderName, String apiName, String version, String apiConfig,
                                          String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        return restClient.addApi(apiConfig);
    }

    public boolean addDefaultAPI(String apiProviderName, String apiName, String version, String apiConfig)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return restClient.addApi(apiConfig);
    }

    /**
     * Get API from the gateway
     *
     * @param tenantDomain
     * @return
     * @throws AxisFault
     */
    public org.wso2.carbon.apimgt.gateway.dto.APIData getApiForTenant(String apiName,
                                                                      String version, String tenantDomain)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        APIData apiData = restClient.getApi(qualifiedName);
        return convert(apiData);
    }

    public org.wso2.carbon.apimgt.gateway.dto.APIData getApi(String apiName, String version)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        APIData apiData = restClient.getApi(qualifiedName);
        return convert(apiData);
    }

    public org.wso2.carbon.apimgt.gateway.dto.APIData getDefaultApiForTenant(String apiName,
                                                                             String version, String tenantDomain)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        APIData apiData = restClient.getApi(qualifiedName);
        return convert(apiData);
    }

    public org.wso2.carbon.apimgt.gateway.dto.APIData getDefaultApi(String apiName,
                                                                    String version) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        APIData apiData = restClient.getApi(qualifiedName);
        return convert(apiData);
    }

    /**
     * Update the API in the Gateway
     *
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean updateApiForTenant(String apiName, String version, String apiConfig,
                                      String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    public boolean updateApi(String apiName, String version, String apiConfig) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    /**
     * Update the API in the Gateway
     *
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean updateApiForInlineScriptForTenant(String apiName, String version,
                                                     String apiConfig, String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    public boolean updateApiForInlineScript(String apiName, String version, String apiConfig)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    public boolean updateDefaultApiForTenant(String apiName, String version, String apiConfig,
                                             String tenantDomain) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    public boolean updateDefaultApi(String apiName, String version, String apiConfig)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        return restClient.updateApi(qualifiedName, apiConfig);
    }

    /**
     * Delete the API from Gateway
     *
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean deleteApiForTenant(String apiProviderName, String apiName, String version, String tenantDomain)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        // Delete secure vault alias properties if exists
        deleteRegistryProperty(apiProviderName, apiName, version, tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.deleteApi(qualifiedName);
    }

    protected void deleteRegistryProperty(String apiProviderName, String apiName, String version,
                                          String tenantDomain) throws AxisFault {

        GatewayUtils.deleteRegistryProperty(GatewayUtils.getAPIEndpointSecretAlias(apiProviderName, apiName,
                version),
                APIConstants.API_SYSTEM_CONFIG_SECURE_VAULT_LOCATION, tenantDomain);
    }

    public boolean deleteApi(String apiProviderName, String apiName, String version) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        // Delete secure vault alias properties if exists
        deleteRegistryProperty(apiProviderName, apiName, version, MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedApiName(apiName, version);
        return restClient.deleteApi(qualifiedName);
    }

    public boolean deleteDefaultApiForTenant(String apiName, String version,
                                             String tenantDomain)
            throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(tenantDomain);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        return restClient.deleteApi(qualifiedName);
    }

    public boolean deleteDefaultApi(String apiName, String version) throws AxisFault {

        RESTAPIAdminServiceProxy restClient = getRestapiAdminClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        String qualifiedName = GatewayUtils.getQualifiedDefaultApiName(apiName);
        return restClient.deleteApi(qualifiedName);
    }

    private org.wso2.carbon.apimgt.gateway.dto.APIData convert(APIData data) {

        if (data == null) {
            return null;
        }
        org.wso2.carbon.apimgt.gateway.dto.APIData apiData = new org.wso2.carbon.apimgt.gateway.dto.APIData();
        apiData.setContext(data.getContext());
        apiData.setFileName(data.getFileName());
        apiData.setHost(data.getHost());
        apiData.setName(data.getName());
        apiData.setPort(data.getPort());
        ResourceData[] resources = data.getResources();
        List<org.wso2.carbon.apimgt.gateway.dto.ResourceData> resList =
                new ArrayList<org.wso2.carbon.apimgt.gateway.dto.ResourceData>();
        if (resources != null && resources.length > 0) {
            for (ResourceData res : resources) {
                if (res == null) {
                    continue;
                }
                org.wso2.carbon.apimgt.gateway.dto.ResourceData resource = convert(res);
                resList.add(resource);
            }
            apiData.setResources(resList.toArray(new org.wso2.carbon.apimgt.gateway.dto.ResourceData[0]));
        }

        return apiData;
    }

    private org.wso2.carbon.apimgt.gateway.dto.ResourceData convert(ResourceData data) {

        org.wso2.carbon.apimgt.gateway.dto.ResourceData resource =
                new org.wso2.carbon.apimgt.gateway.dto.ResourceData();
        resource.setContentType(data.getContentType());
        resource.setFaultSequenceKey(data.getFaultSequenceKey());
        resource.setFaultSeqXml(data.getFaultSeqXml());
        resource.setInSequenceKey(data.getInSequenceKey());
        resource.setInSeqXml(data.getInSeqXml());
        resource.setMethods(data.getMethods());
        resource.setOutSequenceKey(data.getOutSequenceKey());
        resource.setOutSeqXml(data.getOutSeqXml());
        resource.setProtocol(data.getProtocol());
        resource.setUriTemplate(data.getUriTemplate());
        resource.setUrlMapping(data.getUrlMapping());
        resource.setUserAgent(data.getUserAgent());
        return resource;

    }

    /**
     * Add the endpoint to the gateway
     *
     * @param endpointData Content of the endpoint file
     * @return True if the endpoint file is added
     * @throws AxisFault Thrown if an error occurs
     */
    public boolean addEndpoint(String endpointData) throws AxisFault {

        EndpointAdminServiceProxy endpointAdminServiceProxy =
                getEndpointAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return endpointAdminServiceProxy.addEndpoint(endpointData);
    }

    /**
     * Add the endpoint to the tenant
     *
     * @param endpointData Content of the endpoint file
     * @param tenantDomain Domain of the logged tensnt
     * @return True if the endpoint file is added
     * @throws AxisFault Thrown if an error occurred
     */
    public boolean addEndpointForTenant(String endpointData, String tenantDomain) throws AxisFault {

        EndpointAdminServiceProxy endpointAdminServiceProxy = getEndpointAdminServiceClient(tenantDomain);
        return endpointAdminServiceProxy.addEndpoint(endpointData);
    }

    /**
     * Delete the endpoint file from the gateway
     *
     * @param endpointName Name of the endpoint to be deleted
     * @return True if the endpoint file is deleted
     * @throws AxisFault Thrown if an error occurred
     */
    public boolean deleteEndpoint(String endpointName) throws AxisFault {

        EndpointAdminServiceProxy endpointAdminServiceProxy =
                getEndpointAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return endpointAdminServiceProxy.deleteEndpoint(endpointName);
    }

    /**
     * Delete the endpoint file from the tenant
     *
     * @param endpointName Name of the endpoint file to br deleted
     * @param tenantDomain Domain of the logged tenant
     * @return True if the endpoint file is deleted
     * @throws AxisFault Thrown if an error occurred
     */
    public boolean deleteEndpointForTenant(String endpointName, String tenantDomain) throws AxisFault {

        EndpointAdminServiceProxy endpointAdminServiceProxy = getEndpointAdminServiceClient(tenantDomain);
        return endpointAdminServiceProxy.deleteEndpoint(endpointName);
    }

    /**
     * Removes the existing endpoints of synapse config for updating them
     *
     * @param apiName      Name of the API
     * @param apiVersion   Version of the API
     * @param tenantDomain Domain of the logged tenant
     * @return True if endpoints are successfully removed for updating
     * @throws AxisFault Thrown if an error occurred
     */
    public boolean removeEndpointsToUpdate(String apiName, String apiVersion, String tenantDomain) throws AxisFault {

        EndpointAdminServiceProxy endpointAdminServiceProxy = getEndpointAdminServiceClient(tenantDomain);
        return endpointAdminServiceProxy.removeEndpointsToUpdate(apiName, apiVersion);
    }

    /**
     * Returns an instance of EndpointAdminServiceProxy
     *
     * @return An instance of EndpointAdminServiceProxy
     * @throws AxisFault Thrown if an error occurred
     */
    protected EndpointAdminServiceProxy getEndpointAdminServiceClient(String tenantDomain) throws AxisFault {

        return new EndpointAdminServiceProxy(tenantDomain);
    }

    /**
     * Deploy the sequence to the gateway
     *
     * @param sequence - The sequence element , which to be deployed in synapse
     * @throws AxisFault
     */
    public boolean addSequence(String sequence) throws AxisFault {

        SequenceAdminServiceProxy client =
                getSequenceAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        if (sequence != null && !sequence.isEmpty()) {
            OMElement element = null;
            try {
                element = AXIOMUtil.stringToOM(sequence);
                client.addSequence(element);
                return true;
            } catch (XMLStreamException e) {
                log.error("Exception occurred while converting String to an OM.", e);
            }
        }
        return false;
    }

    protected SequenceAdminServiceProxy getSequenceAdminServiceClient(String tenantDomain) throws AxisFault {

        return new SequenceAdminServiceProxy(tenantDomain);
    }

    /**
     * Deploy the sequence to the gateway
     *
     * @param sequence
     * @param tenantDomain
     * @throws AxisFault
     */
    public boolean addSequenceForTenant(String sequence, String tenantDomain) throws AxisFault {

        SequenceAdminServiceProxy client = getSequenceAdminServiceClient(tenantDomain);
        if (sequence != null && !sequence.isEmpty()) {
            OMElement element = null;
            try {
                element = AXIOMUtil.stringToOM(sequence);
                client.addSequence(element);
                return true;
            } catch (XMLStreamException e) {
                log.error("Exception occurred while converting String to an OM.", e);
            }
        }
        return false;
    }

    /**
     * Undeploy the sequence from gateway
     *
     * @param sequenceName -The sequence name, which need to be undeployed from synapse configuration
     * @throws AxisFault
     */
    public boolean deleteSequence(String sequenceName) throws AxisFault {

        SequenceAdminServiceProxy client =
                getSequenceAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        client.deleteSequence(sequenceName);
        return true;
    }

    public boolean deleteSequenceForTenant(String sequenceName, String tenantDomain) throws AxisFault {

        SequenceAdminServiceProxy client = getSequenceAdminServiceClient(tenantDomain);
        client.deleteSequence(sequenceName);
        return true;
    }

    /**
     * get the sequence from gateway
     *
     * @param sequenceName -The sequence name
     * @throws AxisFault
     */
    public OMElement getSequence(String sequenceName) throws AxisFault {

        SequenceAdminServiceProxy client =
                getSequenceAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return (OMElement) client.getSequence(sequenceName);
    }

    public OMElement getSequenceForTenant(String sequenceName, String tenantDomain) throws AxisFault {

        SequenceAdminServiceProxy client = getSequenceAdminServiceClient(tenantDomain);
        return (OMElement) client.getSequence(sequenceName);
    }

    public boolean isExistingSequence(String sequenceName) throws AxisFault {

        SequenceAdminServiceProxy client =
                getSequenceAdminServiceClient(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        return client.isExistingSequence(sequenceName);
    }

    public boolean isExistingSequenceForTenant(String sequenceName, String tenantDomain) throws AxisFault {

        SequenceAdminServiceProxy client = getSequenceAdminServiceClient(tenantDomain);
        return client.isExistingSequence(sequenceName);
    }

    /**
     * encrypt the plain text password
     *
     * @param plainTextPass plain text password
     * @return encrypted password
     * @throws APIManagementException
     */
    public String doEncryption(String tenantDomain, String secureVaultAlias, String plainTextPass) throws AxisFault {

        MediationSecurityAdminServiceProxy client = getMediationSecurityAdminServiceProxy(tenantDomain);
        String encodedValue;
        try {
            encodedValue = client.doEncryption(plainTextPass);
            setRegistryProperty(tenantDomain, secureVaultAlias, encodedValue);
        } catch (APIManagementException e) {
            String msg = "Failed to encrypt and store the secured endpoint password, " + e.getMessage();
            throw new AxisFault(msg, e);
        }
        return encodedValue;
    }

    protected void setRegistryProperty(String tenantDomain, String secureVaultAlias, String encodedValue) throws
            APIManagementException {

        GatewayUtils.setRegistryProperty(secureVaultAlias, encodedValue,
                APIConstants.API_SYSTEM_CONFIG_SECURE_VAULT_LOCATION, tenantDomain);
    }

    protected MediationSecurityAdminServiceProxy getMediationSecurityAdminServiceProxy(String tenantDomain) {

        return new MediationSecurityAdminServiceProxy(tenantDomain);
    }

    /**
     * policy is writtent in to files
     *
     * @param content  content to be written
     * @param fileName name of the file
     * @throws AxisFault
     */
    public boolean deployPolicy(String content, String fileName) throws AxisFault {
        // Do nothing
        return false;
    }

    /**
     * policy undeploy
     *
     * @param fileNames file names to be deleted
     */
    public boolean undeployPolicy(String[] fileNames) {
        // Do nothing
        return false;
    }

    /**
     * Imports the given certificate to the trust store.
     *
     * @param certificate : The client certificate that needs to be added.
     * @param alias       : The alias for the certificate.
     */
    public boolean addCertificate(String certificate, String alias) {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        return certificateManager.addCertificateToGateway(certificate, alias);
    }

    /**
     * Imports the given certificate to the trust store.
     *
     * @param certificate : The client certificate that needs to be added.
     * @param alias       : The alias for the certificate.
     */
    public boolean addClientCertificate(String certificate, String alias) {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        return certificateManager.addClientCertificateToGateway(certificate, alias);
    }

    /**
     * Removes the certificate for the given alias from the trust store.
     *
     * @param alias : Alias of the certificate that needs to be removed.
     */
    public boolean deleteCertificate(String alias) {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        return certificateManager.deleteCertificateFromGateway(alias);
    }

    /**
     * Removes the certificate for the given alias from the trust store.
     *
     * @param alias : Alias of the certificate that needs to be removed.
     */
    public boolean deleteClientCertificate(String alias) {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        return certificateManager.deleteClientCertificateFromGateway(alias);
    }

    /**
     * Checks if the registry should be updated with the new encrypted value.
     *
     * @param gatewayAPIDTO                      GatewayAPIDTO object
     * @param propertyName                       Property to be updated in the secure vault
     * @param mediationSecurityAdminServiceProxy MediationSecurityAdminServiceProxy object
     * @param encryptedValue                     Newly encrypted value to be set in the registry
     * @return true if the registry should be updated, false otherwise
     * @throws APIManagementException When the registry cannot be accessed
     */
    private boolean shouldUpdateRegistry(GatewayAPIDTO gatewayAPIDTO, String propertyName,
                                         MediationSecurityAdminServiceProxy mediationSecurityAdminServiceProxy,
                                         String encryptedValue) throws APIManagementException {
        String tenantDomain = gatewayAPIDTO.getTenantDomain();
        UserRegistry registry = GatewayUtils.getRegistry(tenantDomain);
        String path = APIConstants.API_SYSTEM_CONFIG_SECURE_VAULT_LOCATION;

        PrivilegedCarbonContext.startTenantFlow();
        if (tenantDomain != null && StringUtils.isNotEmpty(tenantDomain)) {
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
        } else {
            PrivilegedCarbonContext.getThreadLocalCarbonContext()
                    .setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);
        }
        try {
            Resource resource = registry.get(path);
            if (resource.getProperty(propertyName) != null) {
                if (!mediationSecurityAdminServiceProxy.doDecryption(encryptedValue).equals(
                        mediationSecurityAdminServiceProxy.doDecryption(resource.getProperty(propertyName)))) {
                    // Property plain text value has been changed. Should update the registry.
                    return true;
                } else {
                    // Property plain text value has not been changed. No need to update the registry.
                    return false;
                }
            } else {
                // The secure vault doesn't exist in the registry. Should update the registry.
                return true;
            }
        } catch (RegistryException e) {
            throw new APIManagementException("Error while reading registry resource " + path + " for tenant " +
                    tenantDomain, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    public boolean deployAPI(GatewayAPIDTO gatewayAPIDTO) throws AxisFault {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        SequenceAdminServiceProxy sequenceAdminServiceProxy =
                getSequenceAdminServiceClient(gatewayAPIDTO.getTenantDomain());
        RESTAPIAdminServiceProxy restapiAdminServiceProxy = getRestapiAdminClient(gatewayAPIDTO.getTenantDomain());
        LocalEntryServiceProxy localEntryServiceProxy = new LocalEntryServiceProxy(gatewayAPIDTO.getTenantDomain());
        EndpointAdminServiceProxy endpointAdminServiceProxy =
                new EndpointAdminServiceProxy(gatewayAPIDTO.getTenantDomain());
        MediationSecurityAdminServiceProxy mediationSecurityAdminServiceProxy =
                new MediationSecurityAdminServiceProxy(gatewayAPIDTO.getTenantDomain());
        if (log.isDebugEnabled()) {
            log.debug("Start to undeploy API" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        unDeployAPI(sequenceAdminServiceProxy, restapiAdminServiceProxy, localEntryServiceProxy,
                endpointAdminServiceProxy, gatewayAPIDTO, mediationSecurityAdminServiceProxy);
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " undeployed");
            log.debug("Start to deploy Local entries" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }
        // Add Local Entries
        if (gatewayAPIDTO.getLocalEntriesToBeAdd() != null) {
            for (GatewayContentDTO localEntry : gatewayAPIDTO.getLocalEntriesToBeAdd()) {
                if (localEntryServiceProxy.isEntryExists(localEntry.getName())) {
                    localEntryServiceProxy.deleteEntry(localEntry.getName());
                    localEntryServiceProxy.addLocalEntry(localEntry.getContent());
                } else {
                    localEntryServiceProxy.addLocalEntry(localEntry.getContent());
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Local Entries deployed");
            log.debug("Start to deploy Endpoint entries" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Add Endpoints
        if (gatewayAPIDTO.getEndpointEntriesToBeAdd() != null) {
            for (GatewayContentDTO endpointEntry : gatewayAPIDTO.getEndpointEntriesToBeAdd()) {
                if (endpointAdminServiceProxy.isEndpointExist(endpointEntry.getName())) {
                    endpointAdminServiceProxy.deleteEndpoint(endpointEntry.getName());
                    endpointAdminServiceProxy.addEndpoint(endpointEntry.getContent());
                } else {
                    endpointAdminServiceProxy.addEndpoint(endpointEntry.getContent());
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Endpoints deployed");
            log.debug("Start to deploy Client certificates" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Add Client Certificates
        if (gatewayAPIDTO.getClientCertificatesToBeAdd() != null) {
            synchronized (certificateManager) {
                for (GatewayContentDTO certificate : gatewayAPIDTO.getClientCertificatesToBeAdd()) {
                    certificateManager.addClientCertificateToGateway(certificate.getContent(), certificate.getName());
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " client certificates deployed");
            log.debug("Start to add vault entries " + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Add vault entries
        if (gatewayAPIDTO.getCredentialsToBeAdd() != null) {
            for (CredentialDto certificate : gatewayAPIDTO.getCredentialsToBeAdd()) {
                try {
                    String encryptedValue = mediationSecurityAdminServiceProxy.doEncryption(certificate.getPassword());
                    if (shouldUpdateRegistry(gatewayAPIDTO, certificate.getAlias(), mediationSecurityAdminServiceProxy,
                            encryptedValue)) {
                        setRegistryProperty(gatewayAPIDTO.getTenantDomain(), certificate.getAlias(), encryptedValue);
                    }
                } catch (APIManagementException e) {
                    log.error("Exception occurred while encrypting password.", e);
                    throw new AxisFault(e.getMessage());
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Vault Entries Added successfully");
            log.debug("Start to deploy custom sequences" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Add Sequences
        if (gatewayAPIDTO.getSequenceToBeAdd() != null) {
            for (GatewayContentDTO sequence : gatewayAPIDTO.getSequenceToBeAdd()) {
                OMElement element;
                try {
                    element = AXIOMUtil.stringToOM(sequence.getContent());
                } catch (XMLStreamException e) {
                    log.error("Exception occurred while converting String to an OM.", e);
                    throw new AxisFault(e.getMessage());
                }
                if (sequenceAdminServiceProxy.isExistingSequence(sequence.getName())) {
                    sequenceAdminServiceProxy.deleteSequence(sequence.getName());
                    sequenceAdminServiceProxy.addSequence(element);
                } else {
                    sequenceAdminServiceProxy.addSequence(element);
                }
                APIUtil.logAuditMessage(APIConstants.AuditLogConstants.OPERATION_POLICY, sequence.getName(),
                        APIConstants.AuditLogConstants.DEPLOYED, APIConstants.AuditLogConstants.SYSTEM +
                                ": " + gatewayAPIDTO.getTenantDomain());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " custom sequences deployed");
            log.debug("Start to deploy API Definition" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }
        // Add API
        if (StringUtils.isNotEmpty(gatewayAPIDTO.getApiDefinition())) {
            restapiAdminServiceProxy.addApi(gatewayAPIDTO.getApiDefinition());
            APIUtil.logAuditMessage(APIConstants.AuditLogConstants.API, gatewayAPIDTO.getApiId(),
                    APIConstants.AuditLogConstants.DEPLOYED, APIConstants.AuditLogConstants.SYSTEM +
                            ": " + gatewayAPIDTO.getTenantDomain());
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " API Definition deployed");
            log.debug("Start to deploy Default API Definition" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Default API Definition deployed");
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + "Deployed successfully");
        }

        return true;
    }

    private void unDeployAPI(SequenceAdminServiceProxy sequenceAdminServiceProxy,
                             RESTAPIAdminServiceProxy restapiAdminServiceProxy,
                             LocalEntryServiceProxy localEntryServiceProxy,
                             EndpointAdminServiceProxy endpointAdminServiceProxy, GatewayAPIDTO gatewayAPIDTO,
                             MediationSecurityAdminServiceProxy mediationSecurityAdminServiceProxy) throws AxisFault {

        CertificateManager certificateManager = CertificateManagerImpl.getInstance();
        if (log.isDebugEnabled()) {
            log.debug("Start to undeploy default api " + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }
        // Delete Default API
        String qualifiedDefaultApiName = GatewayUtils.getQualifiedDefaultApiName(
                gatewayAPIDTO.getName());
        if (restapiAdminServiceProxy.getApi(qualifiedDefaultApiName) != null) {
            restapiAdminServiceProxy.deleteApi(qualifiedDefaultApiName);
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Default API Definition " +
                    "undeployed successfully");
            log.debug("Start to undeploy API Definition" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Delete API
        String qualifiedName = GatewayUtils.getQualifiedApiName(
                gatewayAPIDTO.getName(), gatewayAPIDTO.getVersion());
        if (restapiAdminServiceProxy.getApi(qualifiedName) != null) {
            restapiAdminServiceProxy.deleteApi(qualifiedName);
            APIUtil.logAuditMessage(APIConstants.AuditLogConstants.API, gatewayAPIDTO.getApiId(),
                    APIConstants.AuditLogConstants.UNDEPLOYED, APIConstants.AuditLogConstants.SYSTEM +
                            ": " + gatewayAPIDTO.getTenantDomain());
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " API Definition undeployed " +
                    "successfully");
            log.debug("Start to undeploy custom sequences" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Remove Sequences to be remove.
        if (gatewayAPIDTO.getSequencesToBeRemove() != null) {
            for (String sequenceName : gatewayAPIDTO.getSequencesToBeRemove()) {
                if (sequenceAdminServiceProxy.isExistingSequence(sequenceName)) {
                    sequenceAdminServiceProxy.deleteSequence(sequenceName);
                    APIUtil.logAuditMessage(APIConstants.AuditLogConstants.OPERATION_POLICY, sequenceName,
                            APIConstants.AuditLogConstants.UNDEPLOYED,
                            APIConstants.AuditLogConstants.SYSTEM + ": " + gatewayAPIDTO.getTenantDomain());
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " custom sequences undeployed " +
                    "successfully");
            log.debug("Start to undeploy endpoints" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Remove endpoints
        if (gatewayAPIDTO.getEndpointEntriesToBeRemove() != null) {
            for (String endpoint : gatewayAPIDTO.getEndpointEntriesToBeRemove()) {
                if (endpointAdminServiceProxy.isEndpointExist(endpoint)) {
                    endpointAdminServiceProxy.deleteEndpoint(endpoint);
                } else if (endpoint.contains(AIAPIConstants.API_LLM_ENDPOINT + "*")) {
                    String prefix = endpoint.replace("*", ".*");
                    String[] allEndpoints = endpointAdminServiceProxy.getEndpoints();
                    for (String existingEndpoint : allEndpoints) {
                        if (existingEndpoint.matches(prefix)) {
                            endpointAdminServiceProxy.deleteEndpoint(existingEndpoint);
                        }
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " endpoints undeployed " +
                    "successfully");
            log.debug("Start to undeploy client certificates" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }

        // Remove clientCertificates
        if (gatewayAPIDTO.getClientCertificatesToBeRemove() != null) {
            synchronized (certificateManager) {
                for (String alias : gatewayAPIDTO.getClientCertificatesToBeRemove()) {
                    certificateManager.deleteClientCertificateFromGateway(alias);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " client certificates undeployed " +
                    "successfully");
            log.debug("Start to undeploy local entries" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }
        // Remove Local Entries if Exist
        if (gatewayAPIDTO.getLocalEntriesToBeRemove() != null) {
            for (String localEntryKey : gatewayAPIDTO.getLocalEntriesToBeRemove()) {
                if (localEntryServiceProxy.isEntryExists(localEntryKey)) {
                    localEntryServiceProxy.deleteEntry(localEntryKey);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Local entries undeployed " +
                    "successfully");
            log.debug("Start to remove vault entries" + gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion());
        }
        if (gatewayAPIDTO.getCredentialsToBeRemove() != null) {
            for (String alias : gatewayAPIDTO.getCredentialsToBeRemove()) {
                try {
                    if (mediationSecurityAdminServiceProxy.isAliasExist(alias)) {
                        GatewayUtils.deleteRegistryProperty(alias, APIConstants.API_SYSTEM_CONFIG_SECURE_VAULT_LOCATION,
                                gatewayAPIDTO.getTenantDomain());
                    }
                } catch (APIManagementException e) {
                    String msg = "Error while checking existence of vault entry";
                    log.error(msg, e);
                    throw new AxisFault(msg, e);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + " Vault entries removed " +
                    "successfully");
            log.debug(gatewayAPIDTO.getName() + ":" + gatewayAPIDTO.getVersion() + "undeployed successfully");
        }
    }

    public boolean unDeployAPI(GatewayAPIDTO gatewayAPIDTO) throws AxisFault {

        SequenceAdminServiceProxy sequenceAdminServiceProxy =
                getSequenceAdminServiceClient(gatewayAPIDTO.getTenantDomain());
        RESTAPIAdminServiceProxy restapiAdminServiceProxy = getRestapiAdminClient(gatewayAPIDTO.getTenantDomain());
        LocalEntryServiceProxy localEntryServiceProxy = new LocalEntryServiceProxy(gatewayAPIDTO.getTenantDomain());
        EndpointAdminServiceProxy endpointAdminServiceProxy =
                new EndpointAdminServiceProxy(gatewayAPIDTO.getTenantDomain());
        MediationSecurityAdminServiceProxy mediationSecurityAdminServiceProxy =
                new MediationSecurityAdminServiceProxy(gatewayAPIDTO.getTenantDomain());

        unDeployAPI(sequenceAdminServiceProxy, restapiAdminServiceProxy, localEntryServiceProxy,
                endpointAdminServiceProxy, gatewayAPIDTO, mediationSecurityAdminServiceProxy);
        return true;
    }

    /**
     * Deploy gateway policy sequences to gateway.
     *
     * @param gatewayPolicyDTO Policy sequences data object
     * @throws AxisFault
     */
    public void deployGatewayPolicy(GatewayPolicyDTO gatewayPolicyDTO) throws AxisFault {

        SequenceAdminServiceProxy sequenceAdminServiceProxy =
                getSequenceAdminServiceClient(gatewayPolicyDTO.getTenantDomain());
        if (gatewayPolicyDTO.getGatewayPolicySequenceToBeAdded() != null) {
            for (GatewayContentDTO sequence : gatewayPolicyDTO.getGatewayPolicySequenceToBeAdded()) {
                OMElement element;
                try {
                    element = AXIOMUtil.stringToOM(sequence.getContent());
                } catch (XMLStreamException e) {
                    log.error("Exception occurred while converting String to an OM.", e);
                    throw new AxisFault(e.getMessage());
                }
                if (sequenceAdminServiceProxy.isExistingSequence(sequence.getName())) {
                    sequenceAdminServiceProxy.deleteSequence(sequence.getName());
                    sequenceAdminServiceProxy.addSequence(element);
                } else {
                    sequenceAdminServiceProxy.addSequence(element);
                }
            }
        } else {
            log.error("No gateway policy sequences found to be deployed");
        }
    }

    /**
     * Undeploy gateway policy sequences from gateway.
     *
     * @param gatewayPolicyDTO Policy sequences data object
     * @throws AxisFault
     */
    public void unDeployGatewayPolicy(GatewayPolicyDTO gatewayPolicyDTO) throws AxisFault {

        SequenceAdminServiceProxy sequenceAdminServiceProxy = getSequenceAdminServiceClient(
                gatewayPolicyDTO.getTenantDomain());
        if (gatewayPolicyDTO.getGatewayPolicySequenceToBeAdded() != null) {
            for (GatewayContentDTO sequence : gatewayPolicyDTO.getGatewayPolicySequenceToBeAdded()) {
                if (sequenceAdminServiceProxy.isExistingSequence(sequence.getName())) {
                    sequenceAdminServiceProxy.deleteSequence(sequence.getName());
                }
            }
        }
    }
}
