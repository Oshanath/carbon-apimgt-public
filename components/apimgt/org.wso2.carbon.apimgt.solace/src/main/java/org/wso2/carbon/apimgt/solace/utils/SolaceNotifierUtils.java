package org.wso2.carbon.apimgt.solace.utils;

import com.hazelcast.aws.utility.StringUtil;
import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.asyncapi.models.AaiChannelItem;
import io.apicurio.datamodels.asyncapi.v2.models.Aai20Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIRevisionDeployment;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.api.model.Environment;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.solace.SolaceAdminApis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SolaceNotifierUtils {
    protected static ApiMgtDAO apiMgtDAO;
    private static final Log log = LogFactory.getLog(SolaceNotifierUtils.class);

    /**
     * Get and patch client id for Solace application
     *
     * @return SolaceAdminApis  object to invoke Solace
     * @throws APIManagementException If the Solace env configuration if not provided properly
     */
    public static SolaceAdminApis getSolaceAdminApis()
            throws APIManagementException {
        Map<String, Environment> thirdPartyEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        Environment solaceEnvironment = null;

        for (Map.Entry<String, Environment> entry : thirdPartyEnvironments.entrySet()) {
            if (APIConstants.SOLACE_ENVIRONMENT.equals(entry.getValue().getProvider())) {
                solaceEnvironment = entry.getValue();
            }
        }

        if (solaceEnvironment != null) {
            return new SolaceAdminApis(solaceEnvironment.getServerURL(), solaceEnvironment.
                    getUserName(), solaceEnvironment.getPassword(), solaceEnvironment.getAdditionalProperties().
                    get(APIConstants.SOLACE_ENVIRONMENT_DEV_NAME));
        } else {
            throw new APIManagementException("Solace Environment configurations are not provided properly");
        }
    }

    /**
     * Get available transport protocols for the Solace API
     *
     * @param definition Solace API Definition
     * @return List<String> List of available transport protocols
     * @throws APIManagementException If the Solace env configuration if not provided properly
     */
    public static List<String> getTransportProtocolsForSolaceAPI(String definition) throws APIManagementException {
        Aai20Document aai20Document = (Aai20Document) Library.readDocumentFromJSONString(definition);
        SolaceAdminApis solaceAdminApis = getSolaceAdminApis();
        HashSet<String> solaceTransportProtocols = new HashSet<>();
        for (AaiChannelItem channel : aai20Document.getChannels()) {
            solaceTransportProtocols.addAll(solaceAdminApis.getProtocols(channel));
        }
        ArrayList<String> solaceTransportProtocolsList = new ArrayList<>(solaceTransportProtocols);
        return solaceTransportProtocolsList;
    }

    /**
     * Check whether the Solace is Added as a third party environment
     *
     * @return true if Solace is Added as a third party environment
     */
    private boolean isSolaceEnvironmentAdded() {
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        if (gatewayEnvironments.isEmpty()){
            return false;
        }
        Environment solaceEnvironment = null;

        for (Map.Entry<String,Environment> entry: gatewayEnvironments.entrySet()) {
            if (APIConstants.SOLACE_ENVIRONMENT.equals(entry.getValue().getProvider())) {
                solaceEnvironment = entry.getValue();
            }
        }
        return solaceEnvironment != null;
    }

    /**
     * Check whether the Solace is Added as a third party environment with required additional properties
     *
     * @return true if Solace is Added as an environment with required additional properties
     */
    private boolean isSolaceGatewayDetailsAdded() {
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        if (gatewayEnvironments.isEmpty()) {
            return false;
        }
        Environment solaceEnvironment = null;

        for (Map.Entry<String, Environment> entry : gatewayEnvironments.entrySet()) {
            if (APIConstants.SOLACE_ENVIRONMENT.equals(entry.getValue().getProvider())) {
                solaceEnvironment = entry.getValue();
            }
        }
        if (solaceEnvironment != null) {
            Map<String, String> additionalProperties = solaceEnvironment.getAdditionalProperties();
            if (additionalProperties.isEmpty()) {
                return false;
            } else {
                if (StringUtil.isEmpty(additionalProperties.get(APIConstants.SOLACE_ENVIRONMENT_ORGANIZATION)) ||
                        StringUtil.isEmpty(additionalProperties.get(APIConstants.SOLACE_ENVIRONMENT_DEV_NAME)) ) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Rename the Solace application
     *
     * @param organization Name of the Organization
     * @param application  Solace application
     * @throws APIManagementException is error occurs when renaming the application
     */
    public static void renameSolaceApplication(String organization, Application application) throws APIManagementException {
        SolaceAdminApis solaceAdminApis = SolaceNotifierUtils.getSolaceAdminApis();
        log.info("Renaming solace application display name....");
        HttpResponse response = solaceAdminApis.renameApplication(organization, application);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            log.info("Renamed solace application display name into '" + application.getName() + "'");
        } else {
            log.error("Error while renaming solace Application display name....");
            throw new APIManagementException(response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().
                    getReasonPhrase());
        }
    }

    /**
     * Get and patch client id for Solace application
     *
     * @param organization   Name of the Organization
     * @param application    Solace application
     * @param consumerKey    Consumer key to be used when patching
     * @param consumerSecret Consumer secret to be used when patching
     * @throws APIManagementException If the Solace env configuration if not provided properly
     */
    public static void patchSolaceApplicationClientId(String organization, Application application, String consumerKey,
                                                      String consumerSecret) throws APIManagementException {
        SolaceAdminApis solaceAdminApis = SolaceNotifierUtils.getSolaceAdminApis();
        log.info("Identified as Solace Application. Patching CliendID and Secret in solace application.....");
        HttpResponse response = solaceAdminApis.patchClientIdForApplication(organization, application, consumerKey, consumerSecret);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            log.info("CliendID and Secret patched successfully for Solace application");
        } else {
            log.error("Error while patching clientID for Solace application");
        }
    }

    /**
     * Check whether the given API is already deployed in the Solace using revision
     *
     * @param api Name of the API
     * @return returns true if the given API is already deployed
     * @throws APIManagementException If an error occurs when checking API product availability
     */
    public static boolean checkWhetherAPIDeployedToSolaceUsingRevision(API api) throws APIManagementException {
        apiMgtDAO = ApiMgtDAO.getInstance();
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        List<APIRevisionDeployment> deployments = apiMgtDAO.getAPIRevisionDeploymentsByApiUUID(api.getUuid());
        for (APIRevisionDeployment deployment : deployments) {
            if (deployment.isDisplayOnDevportal()) {
                String environmentName = deployment.getDeployment();
                if (gatewayEnvironments.containsKey(environmentName)) {
                    Environment deployedEnvironment = gatewayEnvironments.get(environmentName);
                    if (APIConstants.SOLACE_ENVIRONMENT.equalsIgnoreCase(deployedEnvironment.getProvider())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get third party Solace broker organization Name for API deployment
     *
     * @param api Name of the API
     * @return String of the name of organization in Solace broker
     * @throws APIManagementException is error occurs when getting the name of the organization name
     */
    public static String getThirdPartySolaceBrokerOrganizationNameOfAPIDeployment(API api) throws APIManagementException {
        apiMgtDAO = ApiMgtDAO.getInstance();
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        List<APIRevisionDeployment> deployments = apiMgtDAO.getAPIRevisionDeploymentByApiUUID(api.getUuid());
        for (APIRevisionDeployment deployment : deployments) {
            if (deployment.isDisplayOnDevportal()) {
                String environmentName = deployment.getDeployment();
                if (gatewayEnvironments.containsKey(environmentName)) {
                    Environment deployedEnvironment = gatewayEnvironments.get(environmentName);
                    if (APIConstants.SOLACE_ENVIRONMENT.equalsIgnoreCase(deployedEnvironment.getProvider())) {
                        return deployedEnvironment.getAdditionalProperties().
                                get(APIConstants.SOLACE_ENVIRONMENT_ORGANIZATION);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Generate a name for the API product in Solace broker
     *
     * @param api             Name of the API
     * @param environmentName Name of the environment
     * @return APIProduct name for Solace broker
     */
    public static String generateApiProductNameForSolaceBroker(API api, String environmentName) {
        String[] apiContextParts = api.getContext().split("/");
        return environmentName + "-" + api.getId().getName() + "-" + apiContextParts[1] + "-" + apiContextParts[2];
    }

    /**
     * Check whether the given API product is already deployed in the Solace broker
     *
     * @param api          Name of the API
     * @param organization Name of the organization
     * @return returns true if the given API product is already deployed in the Solace
     * @throws APIManagementException If an error occurs when checking API product availability
     */
    private boolean checkApiProductAlreadyDeployedInSolace(API api, String organization) throws IOException,
            APIManagementException {

        Map<String, Environment> environmentMap = APIUtil.getReadOnlyGatewayEnvironments();
        Environment solaceEnvironment = environmentMap.get(APIConstants.SOLACE_ENVIRONMENT);
        if (solaceEnvironment != null) {
            SolaceAdminApis solaceAdminApis = new SolaceAdminApis(solaceEnvironment.getServerURL(), solaceEnvironment.getUserName(),
                    solaceEnvironment.getPassword(), solaceEnvironment.getAdditionalProperties().get(APIConstants.
                    SOLACE_ENVIRONMENT_DEV_NAME));
            String apiNameWithContext = generateApiProductNameForSolaceBroker(api,
                    getThirdPartySolaceBrokerEnvironmentNameOfAPIDeployment(api));
            HttpResponse response = solaceAdminApis.apiProductGet(organization, apiNameWithContext);

            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    log.info("API product found in Solace Broker");
                    return true;
                } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    log.error("API product not found in Solace broker");
                    log.error(EntityUtils.toString(response.getEntity()));
                    throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().
                            getReasonPhrase());
                } else {
                    log.error("Cannot find API product in Solace Broker");
                    log.error(EntityUtils.toString(response.getEntity()));
                    throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().
                            getReasonPhrase());
                }
            }
            return false;
        } else {
            throw new APIManagementException("Solace Environment configurations are not provided properly");
        }
    }

    /**
     * Check whether the given API product is already deployed in the Solace environment
     *
     * @param api          Name of the API
     * @param environments List of the environments
     * @return returns true if the given API product is already deployed in one of environments
     * @throws IOException            If an error occurs when checking API product availability
     * @throws APIManagementException if an error occurs when getting Solace config
     */
    public static boolean checkApiProductAlreadyDeployedIntoSolaceEnvironments(API api, List<Environment> environments)
            throws IOException, APIManagementException {
        int numberOfDeployedEnvironmentsInSolace = 0;
        for (Environment environment : environments) {
            String apiNameWithContext = generateApiProductNameForSolaceBroker(api, environment.getName());
            SolaceAdminApis solaceAdminApis = SolaceNotifierUtils.getSolaceAdminApis();
            HttpResponse response = solaceAdminApis.apiProductGet(environment.getAdditionalProperties().get(APIConstants.
                    SOLACE_ENVIRONMENT_ORGANIZATION), apiNameWithContext);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("API product found in Solace Broker");
                numberOfDeployedEnvironmentsInSolace++;
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                log.error("API product not found in Solace broker");
                log.error(EntityUtils.toString(response.getEntity()));
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().
                        getReasonPhrase());
            } else {
                log.error("Cannot find API product in Solace Broker");
                log.error(EntityUtils.toString(response.getEntity()));
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().
                        getReasonPhrase());
            }
        }
        return numberOfDeployedEnvironmentsInSolace == environments.size();
    }

    /**
     * Deploy an application to Solace broker
     *
     * @param application  Application to be deployed
     * @param apiProducts  Api products to be subscribed to Application
     * @param organization Name of the organization
     * @throws IOException            If an error occurs when deploying the application
     * @throws APIManagementException if an error occurs when getting Solace config
     */
    public static void deployApplicationToSolaceBroker(Application application, ArrayList<String> apiProducts, String organization)
            throws IOException, APIManagementException {

        SolaceAdminApis solaceAdminApis = SolaceNotifierUtils.getSolaceAdminApis();

        // check existence of the developer
        HttpResponse response1 = solaceAdminApis.developerGet(organization);
        if (response1.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            log.info("Developer found in Solace Broker");

            //check application status
            HttpResponse response2 = solaceAdminApis.applicationGet(organization, application.getUUID(), "default");
            if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // app already exists
                log.info("Solace application '" + application.getName() + "' already exists in Solace." +
                        " Updating Application......");
                HttpResponse response3 = solaceAdminApis.applicationPatchAddSubscription(organization, application,
                        apiProducts);
                if (response3.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    log.info("Solace application '" + application.getName() + "' updated successfully");
                } else {
                    log.error("Error while updating Solace application '" + application.getName() + "'");
                    throw new HttpResponseException(response3.getStatusLine().getStatusCode(), response3.getStatusLine()
                            .getReasonPhrase());
                }
            } else if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {

                String responseString = EntityUtils.toString(response2.getEntity());
                if (responseString.contains(String.valueOf(HttpStatus.SC_NOT_FOUND))) {
                    // create new app
                    log.info("Solace application '" + application.getName() + "' not found in Solace Broker." +
                            "Creating new application......");
                    HttpResponse response4 = solaceAdminApis.createApplication(organization, application,
                            apiProducts);
                    if (response4.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                        log.info("Solace application '" + application.getName() + "' created successfully");
                    } else {
                        log.error("Error while creating Solace application '" + application.getName() + "'");
                        throw new HttpResponseException(response4.getStatusLine().getStatusCode(), response4.
                                getStatusLine().getReasonPhrase());
                    }
                } else {
                    log.error("Error while searching for application '" + application.getName() + "'");
                    throw new HttpResponseException(response2.getStatusLine().getStatusCode(), response2.
                            getStatusLine().getReasonPhrase());
                }
            } else {
                log.error("Error while searching for application '" + application.getName() + "'");
                throw new HttpResponseException(response2.getStatusLine().getStatusCode(), response2.
                        getStatusLine().getReasonPhrase());
            }
        } else if (response1.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            log.error("Developer not found in Solace Broker");
            throw new HttpResponseException(response1.getStatusLine().getStatusCode(), response1.getStatusLine().
                    getReasonPhrase());
        } else {
            log.error("Error while finding developer in Solace Broker");
            throw new HttpResponseException(response1.getStatusLine().getStatusCode(), response1.getStatusLine().
                    getReasonPhrase());
        }
    }

    /**
     * Build the request body for Application creation request
     *
     * @param appName     Name of the application to be deployed
     * @param apiProducts Api products to be subscribed to Application
     * @return org.json.JSON Object of request body
     */
    private org.json.JSONObject buildRequestBodyForCreatingApp(String appName, ArrayList<String> apiProducts) {

        org.json.JSONObject requestBody = new org.json.JSONObject();
        requestBody.put("name", appName);
        requestBody.put("expiresIn", -1);

        //add api products
        org.json.JSONArray apiProductsArray = new org.json.JSONArray();
        for (String x : apiProducts) {
            apiProductsArray.put(x);
        }
        requestBody.put("apiProducts", apiProductsArray);

        //add credentials
        org.json.JSONObject credentialsBody = new org.json.JSONObject();
        credentialsBody.put("expiresAt", -1);
        org.json.JSONObject credentialsSecret = new org.json.JSONObject();
        credentialsSecret.put("consumerKey", "elevator-app-key");
        credentialsSecret.put("consumerSecret", "elevator-app-secret");
        credentialsBody.put("secret", credentialsSecret);
        requestBody.put("credentials", credentialsBody);

        return requestBody;
    }

    /**
     * Get third party Solace broker organization Name
     *
     * @param environments List of the environments
     * @return String of the name of organization in Solace broker
     */
    public static String getSolaceOrganizationName(List<Environment> environments) {
        HashSet<String> organizationNames = new HashSet<>();
        for (Environment environment : environments) {
            if (APIConstants.SOLACE_ENVIRONMENT.equalsIgnoreCase(environment.getProvider())) {
                organizationNames.add(environment.getAdditionalProperties().get(APIConstants.SOLACE_ENVIRONMENT_ORGANIZATION));
            }
        }
        if (organizationNames.size() == 1) {
            return organizationNames.toArray()[0].toString();
        } else {
            return null;
        }
    }

    /**
     * Get third party Solace broker environment Name for API deployment
     *
     * @param api Name of the API
     * @return String of the name of environment in Solace broker
     * @throws APIManagementException is error occurs when getting the name of the environment name
     */
    private String getThirdPartySolaceBrokerEnvironmentNameOfAPIDeployment(API api) throws APIManagementException {
        apiMgtDAO = ApiMgtDAO.getInstance();
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        List<APIRevisionDeployment> deployments = apiMgtDAO.getAPIRevisionDeploymentsByApiUUID(api.getUuid());

        for (APIRevisionDeployment deployment : deployments) {
            String environmentName = deployment.getDeployment();
            if (gatewayEnvironments.containsKey(environmentName)) {
                Environment deployedEnvironment = gatewayEnvironments.get(environmentName);
                if (APIConstants.SOLACE_ENVIRONMENT.equalsIgnoreCase(deployedEnvironment.getProvider())) {
                    return environmentName;
                }
            }
        }
        return null;
    }

    /**
     * Get deployed solace environment name form the revision deployments
     *
     * @param api Name of the API
     * @return List<ThirdPartyEnvironment> List of deployed solace environments
     * @throws APIManagementException is error occurs when getting the list of solace environments
     */
    public static List<Environment> getDeployedSolaceEnvironmentsFromRevisionDeployments(API api) throws
            APIManagementException {
        apiMgtDAO = ApiMgtDAO.getInstance();
        List<Environment> deployedSolaceEnvironments = new ArrayList<>();
        Map<String, Environment> gatewayEnvironments = APIUtil.getReadOnlyGatewayEnvironments();
        List<APIRevisionDeployment> deployments = apiMgtDAO.getAPIRevisionDeploymentsByApiUUID(api.getUuid());

        for (APIRevisionDeployment deployment : deployments) {
            String environmentName = deployment.getDeployment();
            if (gatewayEnvironments.containsKey(environmentName)) {
                Environment deployedEnvironment = gatewayEnvironments.get(environmentName);
                if (APIConstants.SOLACE_ENVIRONMENT.equalsIgnoreCase(deployedEnvironment.getProvider())) {
                    deployedSolaceEnvironments.add(deployedEnvironment);
                }
            }
        }
        return deployedSolaceEnvironments;
    }

    /**
     * Unsubscribe the given API product from the Solace application
     *
     * @param api         API object to be unsubscribed
     * @param application Solace application
     * @throws APIManagementException is error occurs when unsubscribing the API from application
     */
    public static void unsubscribeAPIProductFromSolaceApplication(API api, Application application) throws APIManagementException {
        List<Environment> deployedSolaceEnvironments = getDeployedSolaceEnvironmentsFromRevisionDeployments(api);
        String applicationOrganizationName = getSolaceOrganizationName(deployedSolaceEnvironments);
        ArrayList<String> solaceApiProducts = new ArrayList<>();
        if (applicationOrganizationName != null) {
            for (Environment environment : deployedSolaceEnvironments) {
                solaceApiProducts.add(generateApiProductNameForSolaceBroker(api, environment.getName()));
            }
            SolaceAdminApis solaceAdminApis = SolaceNotifierUtils.getSolaceAdminApis();
            HttpResponse response = solaceAdminApis.applicationPatchRemoveSubscription(applicationOrganizationName,
                    application, solaceApiProducts);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("API product unsubscribed from Solace application '" + application.getName() + "'");
                try {
                    String responseString = EntityUtils.toString(response.getEntity());
                    org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
                    if (jsonObject.getJSONArray("apiProducts") != null) {
                        if (jsonObject.getJSONArray("apiProducts").length() == 0) {
                            // delete application in Solace because of 0 number of api products
                            HttpResponse response2 = solaceAdminApis.deleteApplication(applicationOrganizationName,
                                    application.getUUID());
                            if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                                log.info("Successfully deleted application '" + application.getName() + "' in " +
                                        "Solace Broker");
                            } else {
                                log.error("Error while deleting application '" + application.getName() + "' in Solace");
                                throw new APIManagementException("Error while deleting application '" +
                                        application.getName() + "' in Solace");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.error("Error while unsubscribing API product from Solace Application '" + application.getName()
                        + "'");
                throw new APIManagementException(response.getStatusLine().getStatusCode() + "-" + response.getStatusLine()
                        .getReasonPhrase());
            }
        } else {
            throw new APIManagementException("Multiple Solace organizations found");
        }
    }

}
