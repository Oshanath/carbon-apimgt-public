/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.apimgt.gateway.handlers.throttling;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.neethi.PolicyEngine;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.commons.throttle.core.AccessInformation;
import org.apache.synapse.commons.throttle.core.CallerConfiguration;
import org.apache.synapse.commons.throttle.core.RoleBasedAccessRateController;
import org.apache.synapse.commons.throttle.core.Throttle;
import org.apache.synapse.commons.throttle.core.ThrottleConfiguration;
import org.apache.synapse.commons.throttle.core.ThrottleConstants;
import org.apache.synapse.commons.throttle.core.ThrottleContext;
import org.apache.synapse.commons.throttle.core.ThrottleException;
import org.apache.synapse.commons.throttle.core.ThrottleFactory;
import org.apache.synapse.commons.throttle.core.CallerContext;
import org.apache.synapse.commons.throttle.core.factory.ThrottleContextFactory;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.apache.synapse.rest.RESTConstants;
import org.apache.synapse.transport.passthru.PassThroughConstants;
import org.apache.synapse.transport.passthru.util.RelayUtils;
import org.wso2.carbon.apimgt.api.dto.ConditionGroupDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionType;
import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;
import org.wso2.carbon.apimgt.gateway.MethodStats;
import org.wso2.carbon.apimgt.gateway.handlers.Utils;
import org.wso2.carbon.apimgt.gateway.handlers.ext.listener.ExtensionListenerUtil;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityUtils;
import org.wso2.carbon.apimgt.gateway.handlers.security.AuthenticationContext;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.gateway.throttling.ThrottleDataHolder;
import org.wso2.carbon.apimgt.gateway.utils.GatewayUtils;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.ConditionDto;
import org.wso2.carbon.apimgt.impl.dto.VerbInfoDTO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.tracing.TracingSpan;
import org.wso2.carbon.apimgt.tracing.TracingTracer;
import org.wso2.carbon.apimgt.tracing.Util;
import org.wso2.carbon.apimgt.tracing.telemetry.TelemetrySpan;
import org.wso2.carbon.apimgt.tracing.telemetry.TelemetryTracer;
import org.wso2.carbon.apimgt.tracing.telemetry.TelemetryUtil;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.metrics.manager.Level;
import org.wso2.carbon.metrics.manager.MetricManager;
import org.wso2.carbon.metrics.manager.Timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.xml.stream.XMLStreamException;

import static org.wso2.carbon.apimgt.api.APIConstants.AIAPIConstants.*;


/**
 * This class is Handling new throttling check. This class will use inside each API as throttle handler.
 * It will fetch some of data from incoming message and use them to take throttling decisions.
 * To execute this handler requests must go through authentication handler and auth context should be present
 * in message context.
 */
public class ThrottleHandler extends AbstractHandler implements ManagedLifecycle {

    private static final Log log = LogFactory.getLog(ThrottleHandler.class);
    private volatile Throttle throttle;
    private volatile Map<String, Throttle> throttleMap = new HashMap<>();
    private String policyKeyApplication = null;
    private static final String THROTTLE_MAIN = "THROTTLE_MAIN";
    private static final String INIT_SPIKE_ARREST = "INIT_SPIKE_ARREST";
    private static final String INIT_APPLICATION_SPIKE_ARREST = "INIT_APPLICATION_SPIKE_ARREST";
    private static final String CEP_THROTTLE = "CEP_THROTTLE";
    private static final String HANDLE_THROTTLE_OUT = "HANDLE_THROTTLE_OUT";
    private static final String RESOURCE_THROTTLE = "RESOURCE_THROTTLE";
    private static final String BLOCKED_TEST = "BLOCKED_TEST";
    private final String type = ExtensionType.THROTTLING.toString();

    /**
     * The key for getting the throttling policy - key refers to a/an [registry] Resource entry
     */
    private String policyKeyResource = null;
    /**
     * The key for getting the throttling policy - key refers to a/an [registry] entry
     */
    private String policyKey = null;
    /**
     * The concurrent access control group id
     */
    private String id;
    /**
     * Version number of the throttle policy
     */

    /**
     * Created throttle handler object.
     */
    private String sandboxUnitTime = "1000";
    private String productionUnitTime = "1000";
    private String sandboxMaxCount;
    private String productionMaxCount;
    private String isTokenBasedThrottlingEnabled = "false";
    private String productionMaxPromptTokenCount;
    private String productionMaxCompletionTokenCount;
    private String productionMaxTotalTokenCount;
    private String sandboxMaxPromptTokenCount;
    private String sandboxMaxCompletionTokenCount;
    private String sandboxMaxTotalTokenCount;
    private RoleBasedAccessRateController roleBasedAccessController;

    public ThrottleHandler() {
        if (log.isDebugEnabled()) {
            log.debug("Throttle Handler initialized");
        }
        this.roleBasedAccessController = new RoleBasedAccessRateController();

    }


    /**
     * This method is responsible for throttle incoming messages. This method will perform Application, Subscription
     * and Resource level throttling.
     *
     * @param synCtx Synapse message context that contains message details.
     * @param cc     Configuration context which holds current configuration context.
     * @return
     */
    private boolean doRoleBasedAccessThrottlingWithCEP(MessageContext synCtx, ConfigurationContext cc,
                                                       AuthenticationContext authenticationContext) {

        //Throttle Keys
        //applicationLevelThrottleKey key is combination of {applicationId}:{authorizedUser}
        String applicationLevelThrottleKey;
        //subscriptionLevelThrottleKey key for an api subscription is combination of {applicationId}:{apiContext}:{apiVersion}
        //subscriptionLevelThrottleKey key for an api subscription is combination of {applicationId}:{productName}:{productProvider}
        //Todo: add product version to key when versioning is supported
        String subscriptionLevelThrottleKey;
        // The key is combination of {apiContext}/ {apiVersion}{resourceUri}:{httpMethod} if policy is user level then authorized user will append at end
        String resourceLevelThrottleKey = "";
        //apiLevelThrottleKey key is combination of {apiContext}:{apiVersion}
        String apiLevelThrottleKey;

        //Throttle Tiers
        String applicationLevelTier;
        String subscriptionLevelTier;
        String resourceLevelTier = "";
        String apiLevelTier;

        //Other Relevant parameters
        AuthenticationContext authContext = authenticationContext;
        String authorizedUser;

        //Throttled decisions
        boolean isThrottled = false;
        boolean isResourceLevelThrottled = false;
        boolean isApplicationLevelThrottled;
        boolean isSubscriptionLevelThrottled;
        boolean isSubscriptionLevelSpikeThrottled = false;
        boolean isApiLevelThrottled = false;
        boolean isBlockedRequest = false;
        boolean apiLevelThrottledTriggered = false;
        boolean policyLevelUserTriggered = false;
        String appLevelBlockingKey = "";
        String subscriptionLevelBlockingKey = "";
        boolean stopOnQuotaReach = true;
        String apiContext = (String) synCtx.getProperty(RESTConstants.REST_API_CONTEXT);
        String apiVersion = (String) synCtx.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);
        apiContext = apiContext != null ? apiContext : "";
        apiVersion = apiVersion != null ? apiVersion : "";
        String clientIp = GatewayUtils.getClientIp(synCtx);
        String subscriberTenantDomain = "";
        String apiTenantDomain = getTenantDomain();
        ConditionGroupDTO[] conditionGroupDTOs;
        String applicationId = authContext.getApplicationId();
        //If Authz context is not null only we can proceed with throttling
        if (authContext != null) {
            authorizedUser = authContext.getUsername();

            //Check if the tenant domain is appended with authorizedUser and append if it is not there
            if (!StringUtils.contains(authorizedUser, apiTenantDomain)) {
                authorizedUser = authContext.getUsername() + "@" + apiTenantDomain;
            }

            //Check if request is blocked. If request is blocked then will not proceed further and
            //inform to client.

            //Do blocking if there are blocking conditions present
            if (getThrottleDataHolder().isBlockingConditionsPresent()) {
                appLevelBlockingKey = authContext.getSubscriber() + ":" + authContext.getApplicationName();
                subscriptionLevelBlockingKey = apiContext + ":" + apiVersion + ":" + authContext.getSubscriber()
                        + "-" + authContext.getApplicationName() + ":" + authContext.getKeyType();
                Timer timer = getTimer(MetricManager.name(
                        APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), BLOCKED_TEST));
                Timer.Context context = timer.start();
                isBlockedRequest = getThrottleDataHolder()
                        .isRequestBlocked(apiContext, appLevelBlockingKey, authorizedUser, clientIp, apiTenantDomain,
                                subscriptionLevelBlockingKey);
                context.stop();
            }

            if (isBlockedRequest) {
                String msg = "Request blocked as it violates defined blocking conditions, for API: " + apiContext +
                             " ,application:" + appLevelBlockingKey + " ,user:" + authorizedUser;
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
                synCtx.setProperty(APIThrottleConstants.BLOCKED_REASON, msg);
                synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.REQUEST_BLOCKED);
                isThrottled = true;
            } else {
                subscriberTenantDomain = authContext.getSubscriberTenantDomain();
                applicationLevelThrottleKey = applicationId + ":" + authorizedUser;
                apiLevelThrottleKey = apiContext + ":" + apiVersion;
                stopOnQuotaReach = authContext.isStopOnQuotaReach();
                applicationLevelTier = authContext.getApplicationTier();
                subscriptionLevelTier = authContext.getTier();
                apiLevelTier = authContext.getApiTier();
                VerbInfoDTO throttledResource = new VerbInfoDTO();
                //If request is not blocked then only we perform throttling.
                List<VerbInfoDTO> verbInfoDTOList = (List<VerbInfoDTO>) synCtx.getProperty(APIConstants.VERB_INFO_DTO);
                //If API level tier is not present only we should move to resource level tiers.
                if (verbInfoDTOList == null) {
                    log.warn("Error while getting throttling information for resource and http verb");
                    return false;
                }
                for (VerbInfoDTO verbInfoDTO : verbInfoDTOList) {
                    boolean isUnlimittedTier = false;
                    resourceLevelThrottleKey = verbInfoDTO.getRequestKey();
                    resourceLevelTier = verbInfoDTO.getThrottling();
                    if (APIConstants.UNLIMITED_TIER.equalsIgnoreCase(resourceLevelTier)) {
                        isUnlimittedTier = true;
                    }
                    //If API level throttle policy is present then it will apply and no resource level policy will apply for it
                    if (!StringUtils.isEmpty(apiLevelTier) && !APIConstants.UNLIMITED_TIER.equalsIgnoreCase(apiLevelTier)) {
                        resourceLevelThrottleKey = apiLevelThrottleKey;
                        apiLevelThrottledTriggered = true;
                    }

                    //If verbInfo is present then only we will do resource level throttling
                    if (isUnlimittedTier && !apiLevelThrottledTriggered) {
                        //If unlimited tier throttling will not apply at resource level and pass it
                        if (log.isDebugEnabled()) {
                            log.debug("Resource level throttling set as unlimited and request will pass " +
                                    "resource level");
                        }
                    } else {
                        if (APIConstants.API_POLICY_USER_LEVEL.equalsIgnoreCase(verbInfoDTO.getApplicableLevel())) {
                            resourceLevelThrottleKey = resourceLevelThrottleKey + "_" + authorizedUser;
                            policyLevelUserTriggered = true;
                        }
                        //If tier is not unlimited only throttling will apply.
                        List<String> resourceLevelThrottleConditions = new ArrayList<>();
                        resourceLevelThrottleConditions = verbInfoDTO.getThrottlingConditions();
                        conditionGroupDTOs = verbInfoDTO.getConditionGroups();

                        Timer timer1 = getTimer(MetricManager.name(
                                APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), RESOURCE_THROTTLE));
                        Timer.Context context1 = timer1.start();

                        if (getThrottleDataHolder().isAPIThrottled
                                (resourceLevelThrottleKey)) {
                            if (getThrottleDataHolder().isConditionsAvailable(resourceLevelThrottleKey)) {
                                Map<String, List<ConditionDto>> conditionDtoMap = getThrottleDataHolder()
                                        .getConditionDtoMap(resourceLevelThrottleKey);
                                if (log.isDebugEnabled()) {
                                    log.debug("Conditions available" + conditionDtoMap.size());
                                }
                                String throttledCondition = getThrottleConditionEvaluator().getThrottledInCondition
                                        (synCtx, authContext, conditionDtoMap);
                                if (StringUtils.isNotEmpty(throttledCondition)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Throttled with Condition :" + throttledCondition);
                                    }
                                    String combinedResourceLevelThrottleKey = resourceLevelThrottleKey + "_" +
                                            throttledCondition;

                                    if (log.isDebugEnabled()) {
                                        log.debug("Checking condition : " + combinedResourceLevelThrottleKey);
                                    }

                                    if (getThrottleDataHolder().isThrottled(combinedResourceLevelThrottleKey)) {
                                        if (!apiLevelThrottledTriggered) {
                                            isResourceLevelThrottled = isThrottled = true;
                                        } else {
                                            isApiLevelThrottled = isThrottled = true;
                                        }
                                        long timestamp = getThrottleDataHolder().
                                                getThrottleNextAccessTimestamp(combinedResourceLevelThrottleKey);
                                        synCtx.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP,
                                                timestamp);
                                    }
                                }
                            } else {
                                if (conditionGroupDTOs != null && conditionGroupDTOs.length > 0) {

                                    if (log.isDebugEnabled()) {
                                        log.debug("Evaluating Conditional Groups for " + apiLevelThrottleKey);
                                    }
                                    //Then we will apply resource level throttling
                                    List<ConditionGroupDTO> applicableConditions = getThrottleConditionEvaluator()
                                            .getApplicableConditions(synCtx, authContext, conditionGroupDTOs);
                                    for (ConditionGroupDTO conditionGroup : applicableConditions) {
                                        String combinedResourceLevelThrottleKey = resourceLevelThrottleKey +
                                                conditionGroup.getConditionGroupId();
                                        if (log.isDebugEnabled()) {
                                            log.debug("Checking condition : " + combinedResourceLevelThrottleKey);
                                        }

                                        if (getThrottleDataHolder().
                                                isThrottled(combinedResourceLevelThrottleKey)) {
                                            if (!apiLevelThrottledTriggered) {
                                                isResourceLevelThrottled = isThrottled = true;
                                            } else {
                                                isApiLevelThrottled = isThrottled = true;
                                            }
                                            long timestamp = getThrottleDataHolder().
                                                    getThrottleNextAccessTimestamp(combinedResourceLevelThrottleKey);
                                            synCtx.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP,
                                                    timestamp);
                                            break;
                                        }
                                    }
                                } else {
                                    log.warn("Unable to find throttling information for resource and http verb. Throttling "
                                            + "will not be applied");
                                }
                            }
                        }

                        context1.stop();
                        if (isThrottled) {
                            throttledResource = verbInfoDTO;
                            break;
                        }
                    }
                }

                if (!isApiLevelThrottled) {
                    Timer timer2 = getTimer(MetricManager.name(
                            APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), RESOURCE_THROTTLE));
                    Timer.Context context2 = timer2.start();

                    //Here check resource level throttled. If throttled then call handler throttled and pass.
                    //Else go for subscription level and application level throttling
                    //if resource level not throttled then move to subscription level
                    if (!isResourceLevelThrottled) {
                        //Subscription Level Throttling
                        subscriptionLevelThrottleKey = getSubscriptionLevelThrottleKey(subscriptionLevelTier,
                                authContext, apiContext, apiVersion);
                        isSubscriptionLevelThrottled = getThrottleDataHolder().
                                isThrottled(subscriptionLevelThrottleKey);
                        if (!isSubscriptionLevelThrottled && authContext.getSpikeArrestLimit() > 0) {
                            isSubscriptionLevelSpikeThrottled = isSubscriptionLevelSpike(synCtx, subscriptionLevelThrottleKey);
                        }
                        //if subscription level not throttled then move to application level
                        //Stop on quata reach
                        if (!isSubscriptionLevelThrottled && !isSubscriptionLevelSpikeThrottled) {
                            boolean isApplicationLevelSpikeThrottled = false;
                            //Application Level Throttling
                            isApplicationLevelThrottled = getThrottleDataHolder().
                                    isThrottled(applicationLevelThrottleKey);
                            //if application level not throttled means it does not throttled at any level.
                            if (!isApplicationLevelThrottled) {
                                if (authenticationContext.getApplicationSpikesArrestLimit() > 0) {
                                    isApplicationLevelSpikeThrottled = isApplicationLevelSpike(synCtx,
                                            applicationLevelThrottleKey);
                                }
                                if (!isApplicationLevelSpikeThrottled) {
                                    for (VerbInfoDTO verbInfo : verbInfoDTOList) {
                                        resourceLevelThrottleKey = verbInfo.getRequestKey();
                                        resourceLevelTier = verbInfo.getThrottling();
                                        boolean keyTemplatesAvailable = getThrottleDataHolder().isKeyTemplatesPresent();
                                        if (!keyTemplatesAvailable || !validateCustomPolicy(authorizedUser,
                                                applicationLevelThrottleKey, resourceLevelThrottleKey,
                                                apiLevelThrottleKey, subscriptionLevelThrottleKey, apiContext,
                                                apiVersion, subscriberTenantDomain, apiTenantDomain, applicationId,
                                                clientIp, getThrottleDataHolder().getKeyTemplateMap(), synCtx)) {
                                            //Pass message context and continue to avoid performance issue.
                                            //Did not throttled at any level. So let message go and publish event.
                                            //publish event to Global Policy Server
                                            if (isHardLimitThrottled(synCtx, authContext, apiContext, apiVersion)) {
                                                isThrottled = true;

                                            } else if (((Axis2MessageContext)synCtx).getAxis2MessageContext()
                                                    .getProperty(AI_API_REQUEST_METADATA) == null) {
                                                ServiceReferenceHolder.getInstance().getThrottleDataPublisher().
                                                        publishNonThrottledEvent(applicationLevelThrottleKey,
                                                                applicationLevelTier, apiLevelThrottleKey, apiLevelTier,
                                                                subscriptionLevelThrottleKey, subscriptionLevelTier,
                                                                resourceLevelThrottleKey, resourceLevelTier,
                                                                authorizedUser, apiContext,
                                                                apiVersion, subscriberTenantDomain, apiTenantDomain,
                                                                applicationId,
                                                                synCtx, authContext);
                                            }
                                        } else {
                                            log.debug("Request throttled at custom throttling");
                                            synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                                    APIThrottleConstants.CUSTOM_POLICY_LIMIT_EXCEED);
                                            isThrottled = true;

                                        }
                                    }
                                } else {
                                    log.debug("Request throttled at application level for burst limit");
                                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                            APIThrottleConstants.APPLICATION_BURST_LIMIT_EXCEEDED);
                                    isThrottled = true;
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Request throttled at application level for throttle key" +
                                              applicationLevelThrottleKey);
                                }
                                synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                                   APIThrottleConstants.APPLICATION_LIMIT_EXCEEDED);
                                long timestamp = getThrottleDataHolder().getThrottleNextAccessTimestamp(
                                        applicationLevelThrottleKey);
                                synCtx.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP, timestamp);
                                isThrottled = isApplicationLevelThrottled = true;
                            }
                        } else {
                            if (!stopOnQuotaReach) {
                                // This means that we are allowing the requests to continue even after the throttling
                                // limit has reached.
                                if (synCtx.getProperty(APIConstants.API_USAGE_THROTTLE_OUT_PROPERTY_KEY) == null) {
                                    synCtx.setProperty(APIConstants.API_USAGE_THROTTLE_OUT_PROPERTY_KEY, Boolean.TRUE);
                                }
                                isThrottled = false;
                                if (log.isDebugEnabled()) {
                                    log.debug("Request throttled at subscription level for throttle key" +
                                              subscriptionLevelThrottleKey + ". But subscription policy " +
                                            subscriptionLevelTier + " allows to continue to serve requests");
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Request throttled at subscription level for throttle key" +
                                              subscriptionLevelThrottleKey);
                                }
                                if (!isSubscriptionLevelSpikeThrottled) {
                                    long timestamp = getThrottleDataHolder().getThrottleNextAccessTimestamp(subscriptionLevelThrottleKey);
                                    synCtx.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP, timestamp);
                                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.API_LIMIT_EXCEEDED);
                                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                                       APIThrottleConstants.SUBSCRIPTION_LIMIT_EXCEEDED);
                                }
                                isThrottled = true;
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Request throttled at resource level for throttle key" +
                                    throttledResource.getRequestKey());
                        }
                        //is throttled and resource level throttling
                        synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                           APIThrottleConstants.RESOURCE_LIMIT_EXCEEDED);
                    }
                    context2.stop();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Request throttled at api level for throttle key" + apiLevelThrottleKey);
                        if (policyLevelUserTriggered) {
                            log.debug("Request has throttled out in the user level for the throttle key" + apiLevelThrottleKey);
                        }
                    }
                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                                       APIThrottleConstants.API_LIMIT_EXCEEDED);
                }
            }

        }

        //if we need to publish throttled level or some other information we can do it here. Just before return.
        return isThrottled;
    }

    /**
     * This method is responsible for sending non-throttle events to the throttling engine. This method will send
     * backend throttling events to the synapse throttler and Subscription level throttling events to the CEP
     *
     * @param synCtx Synapse message context that contains message details.
     * @return
     */
    private void sendNonThrottleEventToThrottlingEngine(MessageContext synCtx) {
        String resourceLevelThrottleKey = "";
        String resourceLevelTier = "";
        String applicationLevelThrottleKey;
        String applicationLevelTier;
        String apiLevelThrottleKey;
        String apiLevelTier;
        String subscriptionLevelThrottleKey;
        String subscriptionLevelTier;
        String authorizedUser;

        String subscriberTenantDomain = "";
        String apiTenantDomain = getTenantDomain();


        String apiContext = (String) synCtx.getProperty(RESTConstants.REST_API_CONTEXT);
        String apiVersion = (String) synCtx.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);
        apiContext = apiContext != null ? apiContext : "";
        apiVersion = apiVersion != null ? apiVersion : "";

        List<VerbInfoDTO> verbInfoDTOList = (List<VerbInfoDTO>) synCtx.getProperty(APIConstants.VERB_INFO_DTO);
        AuthenticationContext authenticationContext = APISecurityUtils.getAuthenticationContext(synCtx);

        if (authenticationContext != null) {
            String applicationId = authenticationContext.getApplicationId();;
            authorizedUser = authenticationContext.getUsername();

            if (!StringUtils.contains(authorizedUser, apiTenantDomain)) {
                authorizedUser = authenticationContext.getUsername() + "@" + apiTenantDomain;
            }
            subscriberTenantDomain = authenticationContext.getSubscriberTenantDomain();
            applicationLevelThrottleKey = applicationId + ":" + authorizedUser;
            apiLevelThrottleKey = apiContext + ":" + apiVersion;
            applicationLevelTier = authenticationContext.getApplicationTier();
            subscriptionLevelTier = authenticationContext.getTier();
            apiLevelTier = authenticationContext.getApiTier();
            subscriptionLevelThrottleKey = getSubscriptionLevelThrottleKey(subscriptionLevelTier,
                    authenticationContext, apiContext, apiVersion);

            if (Boolean.parseBoolean(isTokenBasedThrottlingEnabled)) {
                //If backend token based throttling is enabled for AI APIs, we need to publish the throttling events
                // to synapse throttler
                isHardLimitThrottled(synCtx, authenticationContext, apiContext, apiVersion);
            }
            for (VerbInfoDTO verbInfo : verbInfoDTOList) {
                resourceLevelThrottleKey = verbInfo.getRequestKey();
                resourceLevelTier = verbInfo.getThrottling();
                ServiceReferenceHolder.getInstance().getThrottleDataPublisher().
                        publishNonThrottledEvent(applicationLevelThrottleKey,
                                applicationLevelTier, apiLevelThrottleKey, apiLevelTier,
                                subscriptionLevelThrottleKey, subscriptionLevelTier,
                                resourceLevelThrottleKey, resourceLevelTier,
                                authorizedUser, apiContext,
                                apiVersion, subscriberTenantDomain, apiTenantDomain,
                                applicationId,
                                synCtx, authenticationContext);
            }
        }
    }


    private String getSubscriptionLevelThrottleKey(String subscriptionLevelTier, AuthenticationContext authContext,
                                                   String apiContext, String apiVersion) {

        String subscriptionLevelThrottleKey;
        if (authContext.getProductName() != null && authContext.getProductProvider() != null) {
            subscriptionLevelThrottleKey = authContext.getApplicationId() + ":" + authContext.getProductName() + ":"
                    + authContext.getProductProvider() + ":" + subscriptionLevelTier;
        } else {
            subscriptionLevelThrottleKey = authContext.getApplicationId() + ":" + apiContext + ":" + apiVersion + ":"
                    + subscriptionLevelTier;
        }
        return subscriptionLevelThrottleKey;
    }

    protected ThrottleConditionEvaluator getThrottleConditionEvaluator() {
        return ThrottleConditionEvaluator.getInstance();
    }

    protected ThrottleDataHolder getThrottleDataHolder() {
        return ServiceReferenceHolder.getInstance().getThrottleDataHolder();
    }

    protected String getTenantDomain() {
        return CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    }


    /**
     * Handle incoming requests and call throttling method to perform throttling.
     *
     * @param messageContext message context object which contains message details.
     * @return return true if message flow need to continue and pass requests to next handler in chain. Else return
     * false to notify error with handler
     */
    public boolean handleRequest(MessageContext messageContext) {

//        if (GatewayUtils.isAPIStatusPrototype(messageContext)) {
//            return true;
//        }

        if (Utils.isGraphQLSubscriptionRequest(messageContext)) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping GraphQL subscription handshake request.");
            }
            return true;
        }

        if (ServiceReferenceHolder.getInstance().getThrottleDataPublisher() == null) {
            log.error("Cannot publish events to traffic manager because ThrottleDataPublisher " +
                    "has not been initialised");
            return true;
        }

        Timer timer3 = getTimer(MetricManager.name(
                APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), THROTTLE_MAIN));
        Timer.Context context3 = timer3.start();
        TracingSpan throttleLatencyTracingSpan = null;
        TelemetrySpan throttleLatencySpan = null;
        if (TelemetryUtil.telemetryEnabled()) {
            TelemetrySpan responseLatencySpan =
                    (TelemetrySpan) messageContext.getProperty(APIMgtGatewayConstants.RESOURCE_SPAN);
            TelemetryTracer tracer = ServiceReferenceHolder.getInstance().getTelemetryTracer();
            throttleLatencySpan = TelemetryUtil.startSpan(APIMgtGatewayConstants.THROTTLE_LATENCY,
                        responseLatencySpan, tracer);
        } else if (Util.tracingEnabled()) {
            TracingSpan responseLatencySpan =
                    (TracingSpan) messageContext.getProperty(APIMgtGatewayConstants.RESOURCE_SPAN);
            TracingTracer tracer = Util.getGlobalTracer();
            throttleLatencyTracingSpan = Util.startSpan(APIMgtGatewayConstants.THROTTLE_LATENCY,
                    responseLatencySpan, tracer);
        }
        long executionStartTime = System.currentTimeMillis();
        if (!ExtensionListenerUtil.preProcessRequest(messageContext, type)) {
            return false;
        }
        try {
            boolean throttleResponse = doThrottle(messageContext);
            if (!ExtensionListenerUtil.postProcessRequest(messageContext, type)) {
                return false;
            }
            return throttleResponse;
        } catch (Exception e) {
            if (TelemetryUtil.telemetryEnabled()) {
                TelemetryUtil.setTag(throttleLatencySpan, APIMgtGatewayConstants.ERROR,
                        APIMgtGatewayConstants.THROTTLE_HANDLER_ERROR);
            } else if (Util.tracingEnabled()) {
                Util.setTag(throttleLatencyTracingSpan, APIMgtGatewayConstants.ERROR,
                        APIMgtGatewayConstants.THROTTLE_HANDLER_ERROR);
            }
            throw e;
        } finally {
            messageContext.setProperty(APIMgtGatewayConstants.THROTTLING_LATENCY,
                    System.currentTimeMillis() - executionStartTime);
            context3.stop();
            if (TelemetryUtil.telemetryEnabled()) {
                TelemetryUtil.finishSpan(throttleLatencySpan);
            } else if (Util.tracingEnabled()) {
                Util.finishSpan(throttleLatencyTracingSpan);
            }
        }
    }

    /**
     * This method will handle responses. Usually we do not perform throttling for responses going back to clients.
     * However if we consider bandwidth scenarios we may need to consider handle response and response patch as well
     * because that also contribute data amount pass through server.
     *
     * @param messageContext message context holds message details.
     * @return return true if message flow need to continue and pass requests to next handler in chain. Else return
     * false to notify error with handler
     */
    @MethodStats
    public boolean handleResponse(MessageContext messageContext) {

        if (messageContext.getProperty(AI_API_RESPONSE_METADATA) != null) {
            Timer timer3 = getTimer(MetricManager.name(
                    APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), THROTTLE_MAIN));
            Timer.Context context3 = timer3.start();
            TracingSpan throttleLatencyTracingSpan = null;
            TelemetrySpan throttleLatencySpan = null;
            if (TelemetryUtil.telemetryEnabled()) {
                TelemetrySpan responseLatencySpan =
                        (TelemetrySpan) messageContext.getProperty(APIMgtGatewayConstants.RESOURCE_SPAN);
                TelemetryTracer tracer = ServiceReferenceHolder.getInstance().getTelemetryTracer();
                throttleLatencySpan = TelemetryUtil.startSpan(APIMgtGatewayConstants.THROTTLE_LATENCY,
                        responseLatencySpan, tracer);
            } else if (Util.tracingEnabled()) {
                TracingSpan responseLatencySpan =
                        (TracingSpan) messageContext.getProperty(APIMgtGatewayConstants.RESOURCE_SPAN);
                TracingTracer tracer = Util.getGlobalTracer();
                throttleLatencyTracingSpan = Util.startSpan(APIMgtGatewayConstants.THROTTLE_LATENCY,
                        responseLatencySpan, tracer);
            }
            long executionStartTime = System.currentTimeMillis();
            if (!ExtensionListenerUtil.preProcessResponse(messageContext, type)) {
                return false;
            }
            try {
                sendNonThrottleEventToThrottlingEngine(messageContext);
                return ExtensionListenerUtil.postProcessResponse(messageContext, type);
            } catch (Exception e) {
                if (TelemetryUtil.telemetryEnabled()) {
                    TelemetryUtil.setTag(throttleLatencySpan, APIMgtGatewayConstants.ERROR,
                            APIMgtGatewayConstants.THROTTLE_HANDLER_ERROR);
                } else if (Util.tracingEnabled()) {
                    Util.setTag(throttleLatencyTracingSpan, APIMgtGatewayConstants.ERROR,
                            APIMgtGatewayConstants.THROTTLE_HANDLER_ERROR);
                }
                throw e;
            } finally {
                messageContext.setProperty(APIMgtGatewayConstants.THROTTLING_LATENCY,
                        System.currentTimeMillis() - executionStartTime);
                context3.stop();
                if (TelemetryUtil.telemetryEnabled()) {
                    TelemetryUtil.finishSpan(throttleLatencySpan);
                } else if (Util.tracingEnabled()) {
                    Util.finishSpan(throttleLatencyTracingSpan);
                }
            }

        } else {
            if (ExtensionListenerUtil.preProcessResponse(messageContext, type)) {
                return ExtensionListenerUtil.postProcessResponse(messageContext, type);
            }
        }
        return false;
    }


    /**
     * Do Throttle method will initialize throttle flow.
     *
     * @param messageContext message context object which contains message details.
     * @return return true if message flow need to continue(message not throttled) and pass requests to next
     * handler in chain. Else return false to notify throttled message.
     */
    @MethodStats
    private boolean doThrottle(MessageContext messageContext) {

        org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) messageContext).
                getAxis2MessageContext();
        ConfigurationContext cc = axis2MC.getConfigurationContext();
        AuthenticationContext authenticationContext = APISecurityUtils.getAuthenticationContext(messageContext);

        if (authenticationContext != null && authenticationContext.getSpikeArrestLimit() > 0) {
            Timer timer = getTimer(MetricManager.name(
                    APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), INIT_SPIKE_ARREST));
            Timer.Context context = timer.start();
            initThrottleForSubscriptionLevelSpikeArrest(messageContext, authenticationContext);
            context.stop();
        }

        if (authenticationContext != null && authenticationContext.getApplicationSpikesArrestLimit() > 0) {
            Timer timer = getTimer(MetricManager.name(
                    APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), INIT_APPLICATION_SPIKE_ARREST));
            Timer.Context context = timer.start();
            initThrottleForApplicationLevelSpikeArrest(messageContext, authenticationContext);
            context.stop();
        }

        boolean isThrottled = false;

        if (!messageContext.isResponse()) {
            //org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) messageContext).
            //      getAxis2MessageContext();
            //ConfigurationContext cc = axis2MC.getConfigurationContext();
            Timer timer = getTimer(MetricManager.name(
                    APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), CEP_THROTTLE));
            Timer.Context context = timer.start();
            isThrottled = doRoleBasedAccessThrottlingWithCEP(messageContext, cc, authenticationContext);
            context.stop();
        }
        if (isThrottled) {
            Timer timer = getTimer(MetricManager.name(
                    APIConstants.METRICS_PREFIX, this.getClass().getSimpleName(), HANDLE_THROTTLE_OUT));
            Timer.Context context = timer.start();
            handleThrottleOut(messageContext);
            context.stop();
            return false;
        }
        return true;
    }

    protected Timer getTimer(String name) {
        return MetricManager.timer(Level.INFO, name);
    }


    private OMElement getFaultPayload(int throttleErrorCode, String message, String description,
                                      String nextAccessTimeValue) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace(APIThrottleConstants.API_THROTTLE_NS,
                                               APIThrottleConstants.API_THROTTLE_NS_PREFIX);
        OMElement payload = fac.createOMElement("fault", ns);

        OMElement errorCode = fac.createOMElement("code", ns);
        errorCode.setText(String.valueOf(throttleErrorCode));
        OMElement errorMessage = fac.createOMElement("message", ns);
        errorMessage.setText(message);
        OMElement errorDetail = fac.createOMElement("description", ns);
        errorDetail.setText(description);
        payload.addChild(errorCode);
        payload.addChild(errorMessage);
        payload.addChild(errorDetail);
        if (!StringUtils.isEmpty(nextAccessTimeValue)) {
            OMElement nextAccessTime = fac.createOMElement("nextAccessTime", ns);
            nextAccessTime.setText(nextAccessTimeValue);
            payload.addChild(nextAccessTime);
        }
        return payload;
    }

    private void handleThrottleOut(MessageContext messageContext) {

        String errorMessage = null;
        String errorDescription = null;
        int errorCode = -1;
        int httpErrorCode = -1;
        long timestamp = 0;
        String nextAccessTimeString = "";
        if (APIThrottleConstants.HARD_LIMIT_EXCEEDED.equals(
                messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.HARD_LIMIT_EXCEEDED_ERROR_CODE;
            errorMessage = "API Limit Reached";
            errorDescription = "API not accepting requests";
            // It it's a hard limit exceeding, we tell it as service not being available.
            httpErrorCode = HttpStatus.SC_SERVICE_UNAVAILABLE;
        } else if (APIThrottleConstants.REQUEST_BLOCKED.equals(
                messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.BLOCKED_ERROR_CODE;
            errorMessage = "Message blocked";
            // By default we send a 429 response back
            httpErrorCode = HttpStatus.SC_FORBIDDEN;
            errorDescription = "You have been blocked from accessing the resource";
        } else if (APIThrottleConstants.API_LIMIT_EXCEEDED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.API_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else if (APIThrottleConstants.RESOURCE_LIMIT_EXCEEDED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.RESOURCE_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else if (APIThrottleConstants.CUSTOM_POLICY_LIMIT_EXCEED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.CUSTOM_POLICY_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else if (APIThrottleConstants.SUBSCRIPTION_LIMIT_EXCEEDED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.SUBSCRIPTION_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else if (APIThrottleConstants.SUBSCRIPTON_BURST_LIMIT_EXCEEDED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.SUBSCRIPTION_BURST_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else if (APIThrottleConstants.APPLICATION_BURST_LIMIT_EXCEEDED
                .equals(messageContext.getProperty(APIThrottleConstants.THROTTLED_OUT_REASON))) {
            errorCode = APIThrottleConstants.APPLICATION_BURST_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your application burst limit quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        } else {
            errorCode = APIThrottleConstants.APPLICATION_THROTTLE_OUT_ERROR_CODE;
            errorMessage = "Message throttled out";
            // By default we send a 429 response back
            httpErrorCode = APIThrottleConstants.SC_TOO_MANY_REQUESTS;
            errorDescription = "You have exceeded your quota";
            nextAccessTimeString = getNextAccessTimeString(messageContext);
        }

        messageContext.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, errorMessage);
        if (!StringUtils.isEmpty(nextAccessTimeString)) {
            errorDescription = errorDescription + " .You can access API after " + nextAccessTimeString;
        }
        messageContext.setProperty(SynapseConstants.ERROR_DETAIL, errorDescription);
        messageContext.setProperty(APIMgtGatewayConstants.HTTP_RESPONSE_STATUS_CODE, httpErrorCode);

        setRetryAfterHeader(messageContext);
        org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) messageContext).
                getAxis2MessageContext();
        // This property need to be set to avoid sending the content in pass-through pipe (request message)
        // as the response.
        axis2MC.setProperty(PassThroughConstants.MESSAGE_BUILDER_INVOKED, Boolean.TRUE);
        try {
            RelayUtils.consumeAndDiscardMessage(axis2MC);
        } catch (AxisFault axisFault) {
            //In case of an error it is logged and the process is continued because we're setting a fault message in the payload.
            log.error("Error occurred while consuming and discarding the message", axisFault);
        }
        Mediator sequence = messageContext.getSequence(APIThrottleConstants.API_THROTTLE_OUT_HANDLER);

        // Invoke the custom error handler specified by the user
        if (sequence != null && !sequence.mediate(messageContext)) {
            // If needed user should be able to prevent the rest of the fault handling
            // logic from getting executed
            return;
        }
        sendFault(messageContext, httpErrorCode);
    }

    protected void sendFault(MessageContext messageContext, int httpErrorCode) {
        Utils.sendFault(messageContext, httpErrorCode);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPolicyKey(String policyKey) {
        this.policyKey = policyKey;
    }

    public String gePolicyKey() {
        return policyKey;
    }

    public void setPolicyKeyApplication(String policyKeyApplication) {
        this.policyKeyApplication = policyKeyApplication;
    }

    public String gePolicyKeyApplication() {
        return policyKeyApplication;
    }

    public void setPolicyKeyResource(String policyKeyResource) {
        this.policyKeyResource = policyKeyResource;
    }

    public String gePolicyKeyResource() {
        return policyKeyResource;
    }

    private OMElement createSpikeArrestSubscriptionLevelPolicy(String policyName, int maxCount, int unitTime) {

        OMElement parsedPolicy = null;
        StringBuilder policy = new StringBuilder(APIThrottleConstants.WS_THROTTLE_POLICY_HEADER);
        if (maxCount != 0 && unitTime != 0) {
            policy.append(createPolicyForRole(policyName, Integer.toString(unitTime), Integer.toString(maxCount)));
        }
        policy.append(APIThrottleConstants.WS_THROTTLE_POLICY_BOTTOM);
        try {
            parsedPolicy = AXIOMUtil.stringToOM(policy.toString());
        } catch (XMLStreamException e) {
            log.error("Error occurred while creating policy file for Hard Throttling.", e);
        }
        return parsedPolicy;
    }

    private OMElement createSpikeArrestApplicationLevelPolicy(String policyName, int maxCount, int unitTime) {

        OMElement parsedPolicy = null;
        StringBuilder policy = new StringBuilder(APIThrottleConstants.WS_THROTTLE_POLICY_HEADER);
        if (maxCount != 0 && unitTime != 0) {
            policy.append(createPolicyForRole(policyName, Integer.toString(unitTime), Integer.toString(maxCount)));
        }
        policy.append(APIThrottleConstants.WS_THROTTLE_POLICY_BOTTOM);
        try {
            parsedPolicy = AXIOMUtil.stringToOM(policy.toString());
        } catch (XMLStreamException e) {
            // runtime flow is not interrupted even if the policy creation fails
            log.error("Error occurred while creating policy file for Hard Throttling.", e);
        }
        return parsedPolicy;
    }
    private void initThrottleForApplicationLevelSpikeArrest(MessageContext synCtx,
                                                            AuthenticationContext authenticationContext) {
        policyKeyApplication = authenticationContext.getApplicationTier();
        String applicationLevelThrottleKey = authenticationContext.getApplicationId() + ":" + authenticationContext.getUsername();
        int maxRequestCount = authenticationContext.getApplicationSpikesArrestLimit();
        int applicationSpikeArrestWindowUnitTime = 0;
        if (maxRequestCount != 0) {
            String unitTime = authenticationContext.getApplicationSpikesArrestUnit();
            if (APIThrottleConstants.MIN.equalsIgnoreCase(unitTime)) {
                applicationSpikeArrestWindowUnitTime = 60000;
            } else {
                applicationSpikeArrestWindowUnitTime = 1000;
            }
        }
        try {
            if (throttle == null) {
                synchronized (this) {
                    OMElement spikeArrestApplicationLevelPolicy = createSpikeArrestApplicationLevelPolicy(
                            applicationLevelThrottleKey, maxRequestCount, applicationSpikeArrestWindowUnitTime);
                    if (spikeArrestApplicationLevelPolicy != null) {
                        throttle = ThrottleFactory.createMediatorThrottle(
                                PolicyEngine.getPolicy(spikeArrestApplicationLevelPolicy));
                    }
                }
            } else {
                boolean createApplicationLevelSpikeArrestPolicy = false;
                if (throttle.getThrottleContext(applicationLevelThrottleKey) == null) {
                    createApplicationLevelSpikeArrestPolicy = true;
                } else {
                    CallerConfiguration existingCallerConfig =
                            throttle.getThrottleContext(applicationLevelThrottleKey).getThrottleConfiguration()
                                    .getCallerConfiguration(applicationLevelThrottleKey);
                    if (existingCallerConfig.getMaximumRequestPerUnitTime() != maxRequestCount ||
                            existingCallerConfig.getUnitTime() != applicationSpikeArrestWindowUnitTime) {
                        createApplicationLevelSpikeArrestPolicy = true;
                    }
                }

                if (createApplicationLevelSpikeArrestPolicy) {
                    synchronized (authenticationContext.getSubscriberTenantDomain() +
                            authenticationContext.getApplicationTier().intern() + "_appSpikeArrest") {
                        if (throttle.getThrottleContext(applicationLevelThrottleKey) != null) {
                            return;
                        }
                        OMElement spikeArrestApplicationLevelPolicy = createSpikeArrestApplicationLevelPolicy(
                                applicationLevelThrottleKey, maxRequestCount, applicationSpikeArrestWindowUnitTime);
                        if (spikeArrestApplicationLevelPolicy != null) {
                            Throttle tempThrottle = ThrottleFactory.createMediatorThrottle(
                                    PolicyEngine.getPolicy(spikeArrestApplicationLevelPolicy));
                            ThrottleConfiguration newThrottleConfig = tempThrottle.
                                    getThrottleConfiguration(ThrottleConstants.ROLE_BASED_THROTTLE_KEY);
                            ThrottleContext applicationLevelSpikeThrottle = ThrottleContextFactory.
                                    createThrottleContext(ThrottleConstants.ROLE_BASE, newThrottleConfig);
                            throttle.addThrottleContext(applicationLevelThrottleKey, applicationLevelSpikeThrottle);

                        }
                    }
                }
            }
        } catch (ThrottleException e) {
            log.error("Error while initializing throttling object for application level spike arrest policy" +
                    e.getMessage());
        }
    }

    /**
     * This method will intialize subscription level throttling context and throttle object.
     * This method need to be called for each and every request of spike arrest is enabled.
     * If throttle context for incoming message is already created method will do nothing. Else
     * it will create throttle object and context.
     *
     * @param synCtx synapse messaginitThrottleForSubscriptionLevelSpikeArreste context which contains message data
     */
    private void initThrottleForSubscriptionLevelSpikeArrest(MessageContext synCtx,
                                                             AuthenticationContext authenticationContext) {

        policyKey = authenticationContext.getTier();
        String apiContext = (String) synCtx.getProperty(RESTConstants.REST_API_CONTEXT);
        String apiVersion = (String) synCtx.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);
        String subscriptionLevelThrottleKey = getSubscriptionLevelThrottleKey(policyKey, authenticationContext,
                apiContext, apiVersion);
        int maxRequestCount = authenticationContext.getSpikeArrestLimit();
        if (maxRequestCount != 0) {
            String unitTime = authenticationContext.getSpikeArrestUnit();
            int spikeArrestWindowUnitTime;
            if (APIThrottleConstants.MIN.equalsIgnoreCase(unitTime)) {
                spikeArrestWindowUnitTime = 60000;
            } else {
                spikeArrestWindowUnitTime = 1000;
            }
            try {
                synchronized (this) {
                    if (throttle == null) {
                        OMElement spikeArrestSubscriptionLevelPolicy = createSpikeArrestSubscriptionLevelPolicy(
                                subscriptionLevelThrottleKey, maxRequestCount, spikeArrestWindowUnitTime);
                        if (spikeArrestSubscriptionLevelPolicy != null) {
                            throttle = ThrottleFactory.createMediatorThrottle(
                                    PolicyEngine.getPolicy(spikeArrestSubscriptionLevelPolicy));
                        }
                    } else {
                        boolean createSpikeArrestSubscriptionLevelPolicy = false;
                        if (throttle.getThrottleContext(subscriptionLevelThrottleKey) == null) {
                            createSpikeArrestSubscriptionLevelPolicy = true;
                        } else {
                            CallerConfiguration existingCallerConfig =
                                    throttle.getThrottleContext(subscriptionLevelThrottleKey).getThrottleConfiguration()
                                    .getCallerConfiguration(subscriptionLevelThrottleKey);
                            if (existingCallerConfig.getMaximumRequestPerUnitTime() != maxRequestCount ||
                                    existingCallerConfig.getUnitTime() != spikeArrestWindowUnitTime) {
                                createSpikeArrestSubscriptionLevelPolicy = true;
                            }
                        }

                        if (createSpikeArrestSubscriptionLevelPolicy) {
                            OMElement spikeArrestSubscriptionLevelPolicy = createSpikeArrestSubscriptionLevelPolicy(
                                    subscriptionLevelThrottleKey, maxRequestCount, spikeArrestWindowUnitTime);
                            if (spikeArrestSubscriptionLevelPolicy != null) {
                                Throttle tempThrottle = ThrottleFactory.createMediatorThrottle(
                                        PolicyEngine.getPolicy(spikeArrestSubscriptionLevelPolicy));
                                ThrottleConfiguration newThrottleConfig = tempThrottle.
                                        getThrottleConfiguration(ThrottleConstants.ROLE_BASED_THROTTLE_KEY);
                                ThrottleContext subscriptionLevelSpikeThrottle = ThrottleContextFactory.
                                        createThrottleContext(ThrottleConstants.ROLE_BASE, newThrottleConfig);
                                throttle.addThrottleContext(subscriptionLevelThrottleKey,
                                        subscriptionLevelSpikeThrottle);
                            }
                        }
                    }
                }
            } catch (ThrottleException e) {
                log.error("Error while initializing throttling object for subscription level spike arrest policy" +
                          e.getMessage());
            }
        }
    }

    /**
     * This method will intialize subscription level throttling context and throttle object.
     * This method need to be called for each and every request of spike arrest is enabled.
     * If throttle context for incoming message is already created method will do nothing. Else
     * it will create throttle object and context.
     */
    private void initThrottleForHardLimitThrottling() {
        Map<String, OMElement> hardThrottlingPolicy = createHardThrottlingPolicy();
        if (hardThrottlingPolicy != null) {
            for (Map.Entry<String, OMElement> entry: hardThrottlingPolicy.entrySet()) {
                String hardThrottlingPolicyKey = entry.getKey();
                OMElement hardThrottlingPolicyValue = entry.getValue();
                Throttle tempThrottle;
                try {
                    tempThrottle = ThrottleFactory.createMediatorThrottle(
                            PolicyEngine.getPolicy(hardThrottlingPolicyValue));
                    ThrottleConfiguration newThrottleConfig = tempThrottle.getThrottleConfiguration(ThrottleConstants
                            .ROLE_BASED_THROTTLE_KEY);
                    ThrottleContext hardThrottling = ThrottleContextFactory.
                            createThrottleContext(ThrottleConstants.ROLE_BASE, newThrottleConfig);
                    tempThrottle.addThrottleContext(hardThrottlingPolicyKey, hardThrottling);
                    throttleMap.put(hardThrottlingPolicyKey, tempThrottle);
                } catch (ThrottleException e) {
                    log.error("Error while initializing throttling object for hard throttling policy" +
                            e.getMessage(), e);
                }

            }
        }
    }

    public boolean isApplicationLevelSpike(MessageContext synCtx, String throttleKey) {
        ThrottleContext applicationLevelSpikeArrestThrottleContext = throttle.getThrottleContext(throttleKey);
        try {
            AuthenticationContext authContext = APISecurityUtils.getAuthenticationContext(synCtx);

            if (applicationLevelSpikeArrestThrottleContext != null && authContext.getKeyType() != null) {
                org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) synCtx).
                        getAxis2MessageContext();
                ConfigurationContext cc = axis2MC.getConfigurationContext();
                applicationLevelSpikeArrestThrottleContext.setConfigurationContext(cc);

                applicationLevelSpikeArrestThrottleContext.setThrottleId(id +
                        APIThrottleConstants.APPLICATION_BURST_LIMIT);
                AccessInformation info = getAccessInformation(applicationLevelSpikeArrestThrottleContext,
                        throttleKey, throttleKey);
                if (log.isDebugEnabled()) {
                    log.debug("Throttle by Application level burst limit " + throttleKey);
                    log.debug("Allowed = " + (info != null ? info.isAccessAllowed() : "false"));
                }

                if (info != null && !info.isAccessAllowed()) {
                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON,
                            APIThrottleConstants.APPLICATION_BURST_LIMIT_EXCEEDED);
                    log.debug("Application level burst control limit exceeded for key " + throttleKey);
                    return true;
                }
            }

        } catch (ThrottleException e) {
            log.warn("Exception occurred while performing role " +
                    "based throttling", e);
            synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.HARD_LIMIT_EXCEEDED);
            return false;
        }
        return false;
    }

    /**
     * This method will check if coming request is hitting subscription level spikes.
     *
     * @param synCtx      synapse message context which contains message data
     * @param throttleKey subscription level throttle key.
     * @return true if message is throttled else false
     */
    public boolean isSubscriptionLevelSpike(MessageContext synCtx, String throttleKey) {
        ThrottleContext subscriptionLevelSpikeArrestThrottleContext = throttle.getThrottleContext(throttleKey);
        try {
            AuthenticationContext authContext = APISecurityUtils.getAuthenticationContext(synCtx);

            if (subscriptionLevelSpikeArrestThrottleContext != null && authContext.getKeyType() != null) {
                org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) synCtx).
                        getAxis2MessageContext();
                ConfigurationContext cc = axis2MC.getConfigurationContext();
                subscriptionLevelSpikeArrestThrottleContext.setConfigurationContext(cc);

                subscriptionLevelSpikeArrestThrottleContext.setThrottleId(id + APIThrottleConstants.SUBSCRIPTION_BURST_LIMIT);
                AccessInformation info = getAccessInformation(subscriptionLevelSpikeArrestThrottleContext,
                        throttleKey, throttleKey);
                if (log.isDebugEnabled()) {
                    log.debug("Throttle by subscription level burst limit " + throttleKey);
                    log.debug("Allowed = " + (info != null ? info.isAccessAllowed() : "false"));
                }

                if (info != null && !info.isAccessAllowed()) {
                    CallerContext callerContext = subscriptionLevelSpikeArrestThrottleContext.getCallerContext(throttleKey);
                    long timestamp = callerContext.getNextAccessTime();
                    synCtx.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP, timestamp);
                    synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.SUBSCRIPTON_BURST_LIMIT_EXCEEDED);
                    log.debug("Subscription level burst control limit exceeded for key " + throttleKey);
                    return true;
                }
            }

        } catch (ThrottleException e) {
            log.warn("Exception occurred while performing role " +
                     "based throttling", e);
            synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.HARD_LIMIT_EXCEEDED);
            return false;
        }
        return false;
    }

    protected boolean isClusteringEnabled() {
        return GatewayUtils.isClusteringEnabled();
    }

    /**
     * Validate custom policy is handle by this method. This method call is an expensive operation
     * and should not enabled by default. If we enabled this policy then all APIs available in system
     * will have to go through this check.
     *
     * @return
     */
    public boolean validateCustomPolicy(String userID, String appKey, String resourceKey, String apiKey,
                                        String subscriptionKey, String apiContext, String apiVersion, String appTenant,
                                        String apiTenant, String appId, String clientIp,
                                        Map<String, String> keyTemplateMap,
                                        MessageContext messageContext) {
        if (keyTemplateMap != null && keyTemplateMap.size() > 0) {
            for (String key : keyTemplateMap.keySet()) {
                key = key.replaceAll("\\$resourceKey", resourceKey);
                key = key.replaceAll("\\$userId", userID);
                key = key.replaceAll("\\$apiContext", apiContext);
                key = key.replaceAll("\\$apiVersion", apiVersion);
                key = key.replaceAll("\\$appTenant", appTenant);
                key = key.replaceAll("\\$apiTenant", apiTenant);
                key = key.replaceAll("\\$appId", appId);
                if (clientIp != null) {
                    key = key.replaceAll("\\$clientIp", APIUtil.ipToBigInteger(clientIp).toString());
                }
                Object customPropertyObj = messageContext.getProperty(APIMgtGatewayConstants.CUSTOM_PROPERTY);
                if (customPropertyObj != null) {
                    Map<String, Object> customProperties = (Map<String, Object>) customPropertyObj;
                    for (Map.Entry<String, Object> entry : customProperties.entrySet()) {
                        String customKey = "\\$customProperty\\." + entry.getKey();
                        String customValue = entry.getValue() != null ? entry.getValue().toString() : "";
                        key = key.replaceAll(customKey, customValue);
                    }
                }
                if (getThrottleDataHolder().isThrottled(key)) {
                    long timestamp = getThrottleDataHolder().getThrottleNextAccessTimestamp(key);
                    messageContext.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP, timestamp);
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, OMElement> createHardThrottlingPolicy() {
        Map<String, OMElement> throttlingPolicyMap = new HashMap<>();

        if (isPolicyNotRequired()) {
            return null;
        }

        addProductionThrottlingPolicies(throttlingPolicyMap);
        addSandboxThrottlingPolicies(throttlingPolicyMap);

        return throttlingPolicyMap;
    }

    private boolean isPolicyNotRequired() {
        return StringUtils.isEmpty(productionMaxCount) &&
                StringUtils.isEmpty(sandboxMaxCount) &&
                !Boolean.getBoolean(isTokenBasedThrottlingEnabled);
    }

    private void addProductionThrottlingPolicies(Map<String, OMElement> throttlingPolicyMap) {
        if (productionUnitTime != null) {
            addThrottlingPolicy(throttlingPolicyMap, APIThrottleConstants.PRODUCTION_HARD_LIMIT,
                    productionUnitTime, productionMaxCount);

            if (Boolean.parseBoolean(isTokenBasedThrottlingEnabled)) {
                addTokenBasedPolicies(throttlingPolicyMap, productionUnitTime,
                        productionMaxTotalTokenCount, productionMaxCompletionTokenCount, productionMaxPromptTokenCount,
                        APIThrottleConstants.PRODUCTION_HARD_LIMIT_TOTAL_TOKEN,
                        APIThrottleConstants.PRODUCTION_HARD_LIMIT_COMPLETION_TOKEN,
                        APIThrottleConstants.PRODUCTION_HARD_LIMIT_PROMPT_TOKEN);
            }
        }
    }

    private void addSandboxThrottlingPolicies(Map<String, OMElement> throttlingPolicyMap) {
        if (sandboxUnitTime != null) {
            addThrottlingPolicy(throttlingPolicyMap, APIThrottleConstants.SANDBOX_HARD_LIMIT,
                    sandboxUnitTime, sandboxMaxCount);

            if (Boolean.parseBoolean(isTokenBasedThrottlingEnabled)) {
                addTokenBasedPolicies(throttlingPolicyMap, sandboxUnitTime,
                        sandboxMaxTotalTokenCount, sandboxMaxCompletionTokenCount, sandboxMaxPromptTokenCount,
                        APIThrottleConstants.SANDBOX_HARD_LIMIT_TOTAL_TOKEN,
                        APIThrottleConstants.SANDBOX_HARD_LIMIT_COMPLETION_TOKEN,
                        APIThrottleConstants.SANDBOX_HARD_LIMIT_PROMPT_TOKEN);
            }
        }
    }

    private void addThrottlingPolicy(Map<String, OMElement> throttlingPolicyMap, String policyType,
                                     String unitTime, String maxCount) {
        if (maxCount != null) {
            String policy = createPolicyWithHeaderAndFooter(policyType, unitTime, maxCount);
            throttlingPolicyMap.put(policyType, parseThrottlingPolicy(policy));
        }
    }

    private void addTokenBasedPolicies(Map<String, OMElement> throttlingPolicyMap, String unitTime,
                                       String totalTokenCount, String completionTokenCount, String promptTokenCount,
                                       String totalTokenType, String completionTokenType, String promptTokenType) {
        addThrottlingPolicy(throttlingPolicyMap, totalTokenType, unitTime, totalTokenCount);
        addThrottlingPolicy(throttlingPolicyMap, completionTokenType, unitTime, completionTokenCount);
        addThrottlingPolicy(throttlingPolicyMap, promptTokenType, unitTime, promptTokenCount);
    }

    private String createPolicyWithHeaderAndFooter(String policyType, String unitTime, String maxCount) {
        StringBuilder policy = new StringBuilder(APIThrottleConstants.WS_THROTTLE_POLICY_HEADER);
        policy.append(createPolicyForRole(policyType, unitTime, maxCount));
        policy.append(APIThrottleConstants.WS_THROTTLE_POLICY_BOTTOM);
        return policy.toString();
    }

    private OMElement parseThrottlingPolicy(String policy) {
        try {
            return AXIOMUtil.stringToOM(policy);
        } catch (XMLStreamException e) {
            log.error("Error occurred while creating policy file for Hard Throttling.", e);
            return null;
        }
    }

    private String createPolicyForRole(String roleId, String unitTime, String maxCount) {
        return "<wsp:Policy>\n" +
               "     <throttle:ID throttle:type=\"ROLE\">" + roleId + "</throttle:ID>\n" +
               "            <wsp:Policy>\n" +
               "                <throttle:Control>\n" +
               "                    <wsp:Policy>\n" +
               "                        <throttle:MaximumCount>" + maxCount + "</throttle:MaximumCount>\n" +
               "                        <throttle:UnitTime>" + unitTime + "</throttle:UnitTime>\n" +
               "                    </wsp:Policy>\n" +
               "                </throttle:Control>\n" +
               "            </wsp:Policy>\n" +
               " </wsp:Policy>\n";
    }

    /**
     * This method will check if coming request is hitting hard limits.
     *
     * @param synCtx      synapse message context which contains message data
     * @param authContext authentication context which contains authentication data
     * @param apiContext  api context of the request
     * @param apiVersion  api version of the request
     * @return true if message is throttled else false
     */
    private boolean isHardLimitThrottled(MessageContext synCtx, AuthenticationContext authContext, String apiContext,
                                         String apiVersion) {
        if (StringUtils.isEmpty(sandboxMaxCount) && StringUtils.isEmpty(productionMaxCount)) {
            return false;
        }

        if (!throttleMap.isEmpty() && authContext.getKeyType() == null) {
            return false;
        }

        try {
            String throttleKey = generateThrottleKey(apiContext, apiVersion, authContext.getKeyType());
            Map<String, String> llmMetadata = (Map<String, String>) synCtx.getProperty(AI_API_RESPONSE_METADATA);

            if (APIConstants.API_KEY_TYPE_PRODUCTION.equals(authContext.getKeyType())) {
                return checkProductionLimit(synCtx, throttleKey, llmMetadata);
            } else if (APIConstants.API_KEY_TYPE_SANDBOX.equals(authContext.getKeyType())) {
                return checkSandboxLimit(synCtx, throttleKey, llmMetadata);
            }
        } catch (ThrottleException e) {
            handleThrottleException(synCtx, e);
            return true;
        }

        return false;
    }

    /**
     * Generates a throttle key based on API context, version, and key type.
     */
    private String generateThrottleKey(String apiContext, String apiVersion, String keyType) {
        return (apiContext != null ? apiContext : "") + ':' +
                (apiVersion != null ? apiVersion : "") + ':' + keyType;
    }

    /**
     * Checks hard limits for production key type.
     * @param synCtx synapse message context which contains message data
     * @param throttleKey throttle key
     * @param llmMetadata metadata from LLM provider
     * @return true if message is throttled else false
     */
    private boolean checkProductionLimit(MessageContext synCtx,
                                         String throttleKey,  Map<String, String> llmMetadata) throws ThrottleException {
        log.debug("is Response flow : " + synCtx.isResponse());
        if (productionMaxCount != null && !synCtx.isResponse()
                && throttleMap.containsKey(APIThrottleConstants.PRODUCTION_HARD_LIMIT) &&
                isAccessBlocked(synCtx, throttleKey, APIThrottleConstants.PRODUCTION_HARD_LIMIT, 1L)) {
            log.debug("Production Hard Limit Throttled by request count. throttleKey: " + throttleKey);
            return true;
        }
        return checkLlmMetadataLimits(synCtx, throttleKey, llmMetadata,
                APIThrottleConstants.PRODUCTION_HARD_LIMIT_PROMPT_TOKEN,
                APIThrottleConstants.PRODUCTION_HARD_LIMIT_COMPLETION_TOKEN,
                APIThrottleConstants.PRODUCTION_HARD_LIMIT_TOTAL_TOKEN);
    }

    /**
     * Checks hard limits for sandbox key type.
     * @param synCtx synapse message context which contains message data
     * @param throttleKey throttle key
     * @param llmMetadata metadata from LLM provider
     * @return true if message is throttled else false
     */
    private boolean checkSandboxLimit(MessageContext synCtx, String throttleKey,
                                      Map<String, String> llmMetadata) throws ThrottleException {
        if (sandboxMaxCount != null && !synCtx.isResponse() && isAccessBlocked(synCtx, throttleKey,
                APIThrottleConstants.SANDBOX_HARD_LIMIT, 1L)) {
            log.debug("Sandbox Hard Limit Throttled by request count. throttleKey: " + throttleKey);
            return true;
        }
        return checkLlmMetadataLimits(synCtx, throttleKey, llmMetadata,
                APIThrottleConstants.SANDBOX_HARD_LIMIT_PROMPT_TOKEN,
                APIThrottleConstants.SANDBOX_HARD_LIMIT_COMPLETION_TOKEN,
                APIThrottleConstants.SANDBOX_HARD_LIMIT_TOTAL_TOKEN);
    }

    /**
     * Checks LLM metadata limits for prompt, completion, and total tokens.
     */
    private boolean checkLlmMetadataLimits(MessageContext synCtx, String throttleKey,
                                           Map<String, String> llmMetadata, String promptTokenLimit, String completionTokenLimit,
                                           String totalTokenLimit) throws ThrottleException {
        if (llmMetadata != null && synCtx.isResponse()) {
            if (Objects.nonNull(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_PROMPT_TOKEN_COUNT))
                    && throttleMap.containsKey(promptTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, promptTokenLimit,
                            Long.valueOf(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_PROMPT_TOKEN_COUNT)))) {
                log.debug("Hard throttling limit reached due to exceeding prompt token count.");
                return true;
            }
            if (Objects.nonNull(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_COMPLETION_TOKEN_COUNT))
                    && throttleMap.containsKey(completionTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, completionTokenLimit,
                            Long.valueOf(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_COMPLETION_TOKEN_COUNT)))) {
                log.debug("Hard throttling limit reached due to exceeding completion token count.");
                return true;
            }
            if (Objects.nonNull(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_TOTAL_TOKEN_COUNT))
                    && throttleMap.containsKey(totalTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, totalTokenLimit,
                            Long.valueOf(llmMetadata.get(LLM_PROVIDER_SERVICE_METADATA_TOTAL_TOKEN_COUNT)))) {
                log.debug("Hard throttling limit reached due to exceeding total token count.");
                return true;
            }
        } else if (!synCtx.isResponse()) {
            if (throttleMap.containsKey(promptTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, promptTokenLimit, 0L)) {
                log.debug("Hard throttling limit reached due to exceeding prompt token count.");
                return true;
            } else if (throttleMap.containsKey(completionTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, completionTokenLimit, 0L)) {
                log.debug("Hard throttling limit reached due to exceeding completion token count.");
                return true;
            } else if (throttleMap.containsKey(totalTokenLimit)
                    && isAccessBlocked(synCtx, throttleKey, totalTokenLimit, 0L)) {
                log.debug("Hard throttling limit reached due to exceeding total token count.");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if access is allowed based on the throttle context and throttle key.
     */
    private boolean isAccessBlocked(MessageContext synCtx, String throttleKey,
                                    String throttleLimit, Long tokenCount) throws ThrottleException {
        ThrottleContext hardThrottleContext = throttleMap.get(throttleLimit).getThrottleContext(throttleLimit);
        hardThrottleContext.setThrottleId(id + throttleLimit);
        hardThrottleContext.setConfigurationContext(((Axis2MessageContext) synCtx).getAxis2MessageContext()
                .getConfigurationContext());
        AccessInformation info = getAccessInformation(hardThrottleContext, throttleKey + ":" + throttleLimit,
                throttleLimit, tokenCount);
        if (info != null && !info.isAccessAllowed()) {
            synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.HARD_LIMIT_EXCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Handles throttle exceptions.
     */
    private void handleThrottleException(MessageContext synCtx, ThrottleException e) {
        log.warn("Exception occurred while performing role-based throttling", e);
        synCtx.setProperty(APIThrottleConstants.THROTTLED_OUT_REASON, APIThrottleConstants.HARD_LIMIT_EXCEEDED);
    }

    protected AccessInformation getAccessInformation(ThrottleContext hardThrottleContext, String throttleKey,
                                                     String productionHardLimit) throws ThrottleException {
        return roleBasedAccessController.canAccess(hardThrottleContext, throttleKey, productionHardLimit);
    }

    protected AccessInformation getAccessInformation(ThrottleContext hardThrottleContext, String throttleKey,
                                                     String productionHardLimit, Long tokenCount) throws ThrottleException {
        return roleBasedAccessController.canAccess(hardThrottleContext, throttleKey, productionHardLimit, tokenCount);
    }

    public String getSandboxMaxCount() {
        return sandboxMaxCount;
    }

    public void setSandboxMaxCount(String sandboxMaxCount) {
        this.sandboxMaxCount = sandboxMaxCount;
    }

    public String getProductionMaxCount() {
        return productionMaxCount;
    }

    public void setProductionMaxCount(String productionMaxCount) {
        this.productionMaxCount = productionMaxCount;
    }

    public String getSandboxUnitTime() {
        return sandboxUnitTime;
    }

    public void setSandboxUnitTime(String sandboxUnitTime) {
        this.sandboxUnitTime = sandboxUnitTime;
    }

    public String getProductionUnitTime() {
        return productionUnitTime;
    }

    public void setProductionUnitTime(String productionUnitTime) {
        this.productionUnitTime = productionUnitTime;
    }

    public String getIsTokenBasedThrottlingEnabled() {
        return isTokenBasedThrottlingEnabled;
    }

    public void setIsTokenBasedThrottlingEnabled(String isTokenBasedThrottlingEnabled) {
        this.isTokenBasedThrottlingEnabled = isTokenBasedThrottlingEnabled;
    }

    public String getProductionMaxPromptTokenCount() {
        return productionMaxPromptTokenCount;
    }

    public void setProductionMaxPromptTokenCount(String productionMaxPromptTokenCount) {
        this.productionMaxPromptTokenCount = productionMaxPromptTokenCount;
    }

    public String getProductionMaxCompletionTokenCount() {
        return productionMaxCompletionTokenCount;
    }

    public void setProductionMaxCompletionTokenCount(String productionMaxCompletionTokenCount) {
        this.productionMaxCompletionTokenCount = productionMaxCompletionTokenCount;
    }

    public String getProductionMaxTotalTokenCount() {
        return productionMaxTotalTokenCount;
    }

    public void setProductionMaxTotalTokenCount(String productionMaxTotalTokenCount) {
        this.productionMaxTotalTokenCount = productionMaxTotalTokenCount;
    }

    public String getSandboxMaxPromptTokenCount() {
        return sandboxMaxPromptTokenCount;
    }

    public void setSandboxMaxPromptTokenCount(String sandboxMaxPromptTokenCount) {
        this.sandboxMaxPromptTokenCount = sandboxMaxPromptTokenCount;
    }

    public String getSandboxMaxCompletionTokenCount() {
        return sandboxMaxCompletionTokenCount;
    }

    public void setSandboxMaxCompletionTokenCount(String sandboxMaxCompletionTokenCount) {
        this.sandboxMaxCompletionTokenCount = sandboxMaxCompletionTokenCount;
    }

    public String getSandboxMaxTotalTokenCount() {
        return sandboxMaxTotalTokenCount;
    }

    public void setSandboxMaxTotalTokenCount(String sandboxMaxTotalTokenCount) {
        this.sandboxMaxTotalTokenCount = sandboxMaxTotalTokenCount;
    }


    public void init(SynapseEnvironment synapseEnvironment) {
        initThrottleForHardLimitThrottling();
    }

    private String getNextAccessTimeString(MessageContext messageContext) {
        Object timestampOb = messageContext.getProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP);
        if (timestampOb != null) {
            long timestamp = (Long) timestampOb;
            SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ");
            formatUTC.setTimeZone(TimeZone.getTimeZone(APIThrottleConstants.UTC));
            Date date = new Date(timestamp);
            String nextAccessTimeString = formatUTC.format(date) + " " + APIThrottleConstants.UTC;
            messageContext.setProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIME, nextAccessTimeString);
            return nextAccessTimeString;
        }
        return null;
    }
    /**
     * When sent with a 429 (Too Many Requests) response, this indicates how long to wait before making a new request.
     * Retry-After: <http-date> format header will be set. ex: Retry-After: Fri, 31 Dec 1999 23:59:59 GMT
     * @param messageContext
     */
    private void setRetryAfterHeader(MessageContext messageContext) {
        Object timestampOb = messageContext.getProperty(APIThrottleConstants.THROTTLED_NEXT_ACCESS_TIMESTAMP);
        if (timestampOb != null) {
            long timestamp = (Long) timestampOb;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            dateFormat.setTimeZone(TimeZone.getTimeZone(APIThrottleConstants.GMT));
            Date date = new Date(timestamp);
            String retryAfterValue = dateFormat.format(date);
            Map headers = (Map) ((Axis2MessageContext) messageContext).getAxis2MessageContext()
                    .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
            headers.put(APIThrottleConstants.HEADER_RETRY_AFTER, retryAfterValue);
        }
    }
    

    public void destroy() {

    }
}
