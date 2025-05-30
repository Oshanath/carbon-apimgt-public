package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIBusinessInformationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APICorsConfigurationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIInfoAdditionalPropertiesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIInfoAdditionalPropertiesMapDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIMaxTpsDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIMonetizationInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIOperationPoliciesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIOperationsDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIScopeDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIServiceInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APISubtypeConfigurationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIThreatProtectionPoliciesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.AdvertiseInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.MediationPolicyDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.OrganizationPoliciesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.WSDLInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.WebsubSubscriptionConfigurationDTO;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;



@Scope(name = "apim:api_create", description="", value ="")
@Scope(name = "apim:api_import_export", description="", value ="")
@Scope(name = "apim:api_manage", description="", value ="")
public class APIDTO   {
  
    private String id = null;
    private String name = null;
    private String description = null;
    private String context = null;
    private String version = null;
    private String provider = null;
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private String lifeCycleStatus = null;
    private WSDLInfoDTO wsdlInfo = null;
    private String wsdlUrl = null;
    private Boolean responseCachingEnabled = null;
    private Integer cacheTimeout = null;
    private Boolean hasThumbnail = null;
    private Boolean isDefaultVersion = null;
    private Boolean isRevision = null;
    private String revisionedApiId = null;
    private Integer revisionId = null;
    private Boolean enableSchemaValidation = null;
    private Boolean enableSubscriberVerification = null;

    @XmlType(name="TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        HTTP("HTTP"),
        WS("WS"),
        SOAPTOREST("SOAPTOREST"),
        SOAP("SOAP"),
        GRAPHQL("GRAPHQL"),
        WEBSUB("WEBSUB"),
        SSE("SSE"),
        WEBHOOK("WEBHOOK"),
        ASYNC("ASYNC");
        private String value;

        TypeEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String v) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private TypeEnum type = TypeEnum.HTTP;

    @XmlType(name="AudienceEnum")
    @XmlEnum(String.class)
    public enum AudienceEnum {
        PUBLIC("PUBLIC"),
        SINGLE("SINGLE");
        private String value;

        AudienceEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AudienceEnum fromValue(String v) {
            for (AudienceEnum b : AudienceEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private AudienceEnum audience = null;
    private List<String> audiences = new ArrayList<String>();
    private List<String> transport = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<String> tags = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<String> policies = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<OrganizationPoliciesDTO> organizationPolicies = new ArrayList<OrganizationPoliciesDTO>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private String apiThrottlingPolicy = null;
    private String authorizationHeader = null;
    private String apiKeyHeader = null;
    private List<String> securityScheme = new ArrayList<String>();
    private APIMaxTpsDTO maxTps = null;

    @XmlType(name="VisibilityEnum")
    @XmlEnum(String.class)
    public enum VisibilityEnum {
        PUBLIC("PUBLIC"),
        PRIVATE("PRIVATE"),
        RESTRICTED("RESTRICTED");
        private String value;

        VisibilityEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static VisibilityEnum fromValue(String v) {
            for (VisibilityEnum b : VisibilityEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private VisibilityEnum visibility = VisibilityEnum.PUBLIC;
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<String> visibleRoles = new ArrayList<String>();
    private List<String> visibleTenants = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<String> visibleOrganizations = new ArrayList<String>();
    private List<MediationPolicyDTO> mediationPolicies = new ArrayList<MediationPolicyDTO>();
    private APIOperationPoliciesDTO apiPolicies = null;

    @XmlType(name="SubscriptionAvailabilityEnum")
    @XmlEnum(String.class)
    public enum SubscriptionAvailabilityEnum {
        CURRENT_TENANT("CURRENT_TENANT"),
        ALL_TENANTS("ALL_TENANTS"),
        SPECIFIC_TENANTS("SPECIFIC_TENANTS");
        private String value;

        SubscriptionAvailabilityEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SubscriptionAvailabilityEnum fromValue(String v) {
            for (SubscriptionAvailabilityEnum b : SubscriptionAvailabilityEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private SubscriptionAvailabilityEnum subscriptionAvailability = SubscriptionAvailabilityEnum.CURRENT_TENANT;
    private List<String> subscriptionAvailableTenants = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private List<APIInfoAdditionalPropertiesDTO> additionalProperties = new ArrayList<APIInfoAdditionalPropertiesDTO>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private Map<String, APIInfoAdditionalPropertiesMapDTO> additionalPropertiesMap = new HashMap<String, APIInfoAdditionalPropertiesMapDTO>();
    private APIMonetizationInfoDTO monetization = null;

    @XmlType(name="AccessControlEnum")
    @XmlEnum(String.class)
    public enum AccessControlEnum {
        NONE("NONE"),
        RESTRICTED("RESTRICTED");
        private String value;

        AccessControlEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AccessControlEnum fromValue(String v) {
            for (AccessControlEnum b : AccessControlEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private AccessControlEnum accessControl = AccessControlEnum.NONE;
    private List<String> accessControlRoles = new ArrayList<String>();
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private APIBusinessInformationDTO businessInformation = null;
    private APICorsConfigurationDTO corsConfiguration = null;
    private WebsubSubscriptionConfigurationDTO websubSubscriptionConfiguration = null;
    private String workflowStatus = null;
    private String createdTime = null;
    private String lastUpdatedTimestamp = null;
    @Scope(name = "apim:api_publish", description="", value ="")
    @Scope(name = "apim:api_manage", description="", value ="")
    private String lastUpdatedTime = null;
    private Object endpointConfig = null;
    private String primaryProductionEndpointId = null;
    private String primarySandboxEndpointId = null;

    @XmlType(name="EndpointImplementationTypeEnum")
    @XmlEnum(String.class)
    public enum EndpointImplementationTypeEnum {
        INLINE("INLINE"),
        ENDPOINT("ENDPOINT"),
        MOCKED_OAS("MOCKED_OAS");
        private String value;

        EndpointImplementationTypeEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EndpointImplementationTypeEnum fromValue(String v) {
            for (EndpointImplementationTypeEnum b : EndpointImplementationTypeEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private EndpointImplementationTypeEnum endpointImplementationType = EndpointImplementationTypeEnum.ENDPOINT;
    private APISubtypeConfigurationDTO subtypeConfiguration = null;
    private List<APIScopeDTO> scopes = new ArrayList<APIScopeDTO>();
    private List<APIOperationsDTO> operations = new ArrayList<APIOperationsDTO>();
    private APIThreatProtectionPoliciesDTO threatProtectionPolicies = null;
    @Scope(name = "apim:api_publish", description="", value ="")
    private List<String> categories = new ArrayList<String>();
    private Object keyManagers = null;
    private APIServiceInfoDTO serviceInfo = null;
    private AdvertiseInfoDTO advertiseInfo = null;
    private String gatewayVendor = null;
    private String gatewayType = "wso2/synapse";
    private List<String> asyncTransportProtocols = new ArrayList<String>();
    private Boolean egress = false;

  /**
   * UUID of the api registry artifact 
   **/
  public APIDTO id(String id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(example = "01234567-0123-0123-0123-012345678901", value = "UUID of the api registry artifact ")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   **/
  public APIDTO name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(example = "PizzaShackAPI", required = true, value = "")
  @JsonProperty("name")
  @NotNull
 @Pattern(regexp="(^[^~!@#;:%^*()+={}|\\\\<>\"',&$\\[\\]/]*$)") @Size(min=1)  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  public APIDTO description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(example = "This is a simple API for Pizza Shack online pizza delivery store.", value = "")
  @JsonProperty("description")
 @Size(max=32766)  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public APIDTO context(String context) {
    this.context = context;
    return this;
  }

  
  @ApiModelProperty(example = "pizza", required = true, value = "")
  @JsonProperty("context")
  @NotNull
 @Size(min=1,max=232)  public String getContext() {
    return context;
  }
  public void setContext(String context) {
    this.context = context;
  }

  /**
   **/
  public APIDTO version(String version) {
    this.version = version;
    return this;
  }

  
  @ApiModelProperty(example = "1.0.0", required = true, value = "")
  @JsonProperty("version")
  @NotNull
 @Pattern(regexp="^[^~!@#;:%^*()+={}|\\\\<>\"',&/$\\[\\]\\s+/]+$") @Size(min=1,max=30)  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * If the provider value is not given user invoking the api will be used as the provider. 
   **/
  public APIDTO provider(String provider) {
    this.provider = provider;
    return this;
  }

  
  @ApiModelProperty(example = "admin", value = "If the provider value is not given user invoking the api will be used as the provider. ")
  @JsonProperty("provider")
 @Size(max=200)  public String getProvider() {
    return provider;
  }
  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   **/
  public APIDTO lifeCycleStatus(String lifeCycleStatus) {
    this.lifeCycleStatus = lifeCycleStatus;
    return this;
  }

  
  @ApiModelProperty(example = "CREATED", value = "")
  @JsonProperty("lifeCycleStatus")
  public String getLifeCycleStatus() {
    return lifeCycleStatus;
  }
  public void setLifeCycleStatus(String lifeCycleStatus) {
    this.lifeCycleStatus = lifeCycleStatus;
  }

  /**
   **/
  public APIDTO wsdlInfo(WSDLInfoDTO wsdlInfo) {
    this.wsdlInfo = wsdlInfo;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("wsdlInfo")
  public WSDLInfoDTO getWsdlInfo() {
    return wsdlInfo;
  }
  public void setWsdlInfo(WSDLInfoDTO wsdlInfo) {
    this.wsdlInfo = wsdlInfo;
  }

  /**
   **/
  public APIDTO wsdlUrl(String wsdlUrl) {
    this.wsdlUrl = wsdlUrl;
    return this;
  }

  
  @ApiModelProperty(example = "/apimgt/applicationdata/wsdls/admin--soap1.wsdl", value = "")
  @JsonProperty("wsdlUrl")
  public String getWsdlUrl() {
    return wsdlUrl;
  }
  public void setWsdlUrl(String wsdlUrl) {
    this.wsdlUrl = wsdlUrl;
  }

  /**
   **/
  public APIDTO responseCachingEnabled(Boolean responseCachingEnabled) {
    this.responseCachingEnabled = responseCachingEnabled;
    return this;
  }

  
  @ApiModelProperty(example = "true", value = "")
  @JsonProperty("responseCachingEnabled")
  public Boolean isResponseCachingEnabled() {
    return responseCachingEnabled;
  }
  public void setResponseCachingEnabled(Boolean responseCachingEnabled) {
    this.responseCachingEnabled = responseCachingEnabled;
  }

  /**
   **/
  public APIDTO cacheTimeout(Integer cacheTimeout) {
    this.cacheTimeout = cacheTimeout;
    return this;
  }

  
  @ApiModelProperty(example = "300", value = "")
  @JsonProperty("cacheTimeout")
  public Integer getCacheTimeout() {
    return cacheTimeout;
  }
  public void setCacheTimeout(Integer cacheTimeout) {
    this.cacheTimeout = cacheTimeout;
  }

  /**
   **/
  public APIDTO hasThumbnail(Boolean hasThumbnail) {
    this.hasThumbnail = hasThumbnail;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("hasThumbnail")
  public Boolean isHasThumbnail() {
    return hasThumbnail;
  }
  public void setHasThumbnail(Boolean hasThumbnail) {
    this.hasThumbnail = hasThumbnail;
  }

  /**
   **/
  public APIDTO isDefaultVersion(Boolean isDefaultVersion) {
    this.isDefaultVersion = isDefaultVersion;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("isDefaultVersion")
  public Boolean isIsDefaultVersion() {
    return isDefaultVersion;
  }
  public void setIsDefaultVersion(Boolean isDefaultVersion) {
    this.isDefaultVersion = isDefaultVersion;
  }

  /**
   **/
  public APIDTO isRevision(Boolean isRevision) {
    this.isRevision = isRevision;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("isRevision")
  public Boolean isIsRevision() {
    return isRevision;
  }
  public void setIsRevision(Boolean isRevision) {
    this.isRevision = isRevision;
  }

  /**
   * UUID of the api registry artifact 
   **/
  public APIDTO revisionedApiId(String revisionedApiId) {
    this.revisionedApiId = revisionedApiId;
    return this;
  }

  
  @ApiModelProperty(example = "01234567-0123-0123-0123-012345678901", value = "UUID of the api registry artifact ")
  @JsonProperty("revisionedApiId")
  public String getRevisionedApiId() {
    return revisionedApiId;
  }
  public void setRevisionedApiId(String revisionedApiId) {
    this.revisionedApiId = revisionedApiId;
  }

  /**
   **/
  public APIDTO revisionId(Integer revisionId) {
    this.revisionId = revisionId;
    return this;
  }

  
  @ApiModelProperty(example = "1", value = "")
  @JsonProperty("revisionId")
  public Integer getRevisionId() {
    return revisionId;
  }
  public void setRevisionId(Integer revisionId) {
    this.revisionId = revisionId;
  }

  /**
   **/
  public APIDTO enableSchemaValidation(Boolean enableSchemaValidation) {
    this.enableSchemaValidation = enableSchemaValidation;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("enableSchemaValidation")
  public Boolean isEnableSchemaValidation() {
    return enableSchemaValidation;
  }
  public void setEnableSchemaValidation(Boolean enableSchemaValidation) {
    this.enableSchemaValidation = enableSchemaValidation;
  }

  /**
   **/
  public APIDTO enableSubscriberVerification(Boolean enableSubscriberVerification) {
    this.enableSubscriberVerification = enableSubscriberVerification;
    return this;
  }

  
  @ApiModelProperty(example = "false", value = "")
  @JsonProperty("enableSubscriberVerification")
  public Boolean isEnableSubscriberVerification() {
    return enableSubscriberVerification;
  }
  public void setEnableSubscriberVerification(Boolean enableSubscriberVerification) {
    this.enableSubscriberVerification = enableSubscriberVerification;
  }

  /**
   * The api creation type to be used. Accepted values are HTTP, WS, SOAPTOREST, GRAPHQL, WEBSUB, SSE, WEBHOOK, ASYNC
   **/
  public APIDTO type(TypeEnum type) {
    this.type = type;
    return this;
  }

  
  @ApiModelProperty(example = "HTTP", value = "The api creation type to be used. Accepted values are HTTP, WS, SOAPTOREST, GRAPHQL, WEBSUB, SSE, WEBHOOK, ASYNC")
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }
  public void setType(TypeEnum type) {
    this.type = type;
  }

  /**
   * The audience of the API. Accepted values are PUBLIC, SINGLE
   **/
  public APIDTO audience(AudienceEnum audience) {
    this.audience = audience;
    return this;
  }

  
  @ApiModelProperty(example = "PUBLIC", value = "The audience of the API. Accepted values are PUBLIC, SINGLE")
  @JsonProperty("audience")
  public AudienceEnum getAudience() {
    return audience;
  }
  public void setAudience(AudienceEnum audience) {
    this.audience = audience;
  }

  /**
   * The audiences of the API for jwt validation. Accepted values are any String values
   **/
  public APIDTO audiences(List<String> audiences) {
    this.audiences = audiences;
    return this;
  }

  
  @ApiModelProperty(value = "The audiences of the API for jwt validation. Accepted values are any String values")
  @JsonProperty("audiences")
  public List<String> getAudiences() {
    return audiences;
  }
  public void setAudiences(List<String> audiences) {
    this.audiences = audiences;
  }

  /**
   * Supported transports for the API (http and/or https). 
   **/
  public APIDTO transport(List<String> transport) {
    this.transport = transport;
    return this;
  }

  
  @ApiModelProperty(example = "[\"http\",\"https\"]", value = "Supported transports for the API (http and/or https). ")
  @JsonProperty("transport")
  public List<String> getTransport() {
    return transport;
  }
  public void setTransport(List<String> transport) {
    this.transport = transport;
  }

  /**
   **/
  public APIDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  
  @ApiModelProperty(example = "[\"pizza\",\"food\"]", value = "")
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   **/
  public APIDTO policies(List<String> policies) {
    this.policies = policies;
    return this;
  }

  
  @ApiModelProperty(example = "[\"Unlimited\"]", value = "")
  @JsonProperty("policies")
  public List<String> getPolicies() {
    return policies;
  }
  public void setPolicies(List<String> policies) {
    this.policies = policies;
  }

  /**
   **/
  public APIDTO organizationPolicies(List<OrganizationPoliciesDTO> organizationPolicies) {
    this.organizationPolicies = organizationPolicies;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("organizationPolicies")
  public List<OrganizationPoliciesDTO> getOrganizationPolicies() {
    return organizationPolicies;
  }
  public void setOrganizationPolicies(List<OrganizationPoliciesDTO> organizationPolicies) {
    this.organizationPolicies = organizationPolicies;
  }

  /**
   * The API level throttling policy selected for the particular API
   **/
  public APIDTO apiThrottlingPolicy(String apiThrottlingPolicy) {
    this.apiThrottlingPolicy = apiThrottlingPolicy;
    return this;
  }

  
  @ApiModelProperty(example = "Unlimited", value = "The API level throttling policy selected for the particular API")
  @JsonProperty("apiThrottlingPolicy")
  public String getApiThrottlingPolicy() {
    return apiThrottlingPolicy;
  }
  public void setApiThrottlingPolicy(String apiThrottlingPolicy) {
    this.apiThrottlingPolicy = apiThrottlingPolicy;
  }

  /**
   * Name of the Authorization header used for invoking the API. If it is not set, Authorization header name specified in tenant or system level will be used. 
   **/
  public APIDTO authorizationHeader(String authorizationHeader) {
    this.authorizationHeader = authorizationHeader;
    return this;
  }

  
  @ApiModelProperty(example = "Authorization", value = "Name of the Authorization header used for invoking the API. If it is not set, Authorization header name specified in tenant or system level will be used. ")
  @JsonProperty("authorizationHeader")
 @Pattern(regexp="(^[^~!@#;:%^*()+={}|\\\\<>\"',&$\\s+]*$)")  public String getAuthorizationHeader() {
    return authorizationHeader;
  }
  public void setAuthorizationHeader(String authorizationHeader) {
    this.authorizationHeader = authorizationHeader;
  }

  /**
   * Name of the API key header used for invoking the API. If it is not set, default value &#x60;apiKey&#x60; will be used. 
   **/
  public APIDTO apiKeyHeader(String apiKeyHeader) {
    this.apiKeyHeader = apiKeyHeader;
    return this;
  }

  
  @ApiModelProperty(example = "apiKey", value = "Name of the API key header used for invoking the API. If it is not set, default value `apiKey` will be used. ")
  @JsonProperty("apiKeyHeader")
 @Pattern(regexp="(^[^~!@#;:%^*()+={}|\\\\<>\"',&$\\s+]*$)")  public String getApiKeyHeader() {
    return apiKeyHeader;
  }
  public void setApiKeyHeader(String apiKeyHeader) {
    this.apiKeyHeader = apiKeyHeader;
  }

  /**
   * Types of API security, the current API secured with. It can be either OAuth2 or mutual SSL or both. If it is not set OAuth2 will be set as the security for the current API. 
   **/
  public APIDTO securityScheme(List<String> securityScheme) {
    this.securityScheme = securityScheme;
    return this;
  }

  
  @ApiModelProperty(example = "[\"oauth2\"]", value = "Types of API security, the current API secured with. It can be either OAuth2 or mutual SSL or both. If it is not set OAuth2 will be set as the security for the current API. ")
  @JsonProperty("securityScheme")
  public List<String> getSecurityScheme() {
    return securityScheme;
  }
  public void setSecurityScheme(List<String> securityScheme) {
    this.securityScheme = securityScheme;
  }

  /**
   **/
  public APIDTO maxTps(APIMaxTpsDTO maxTps) {
    this.maxTps = maxTps;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("maxTps")
  public APIMaxTpsDTO getMaxTps() {
    return maxTps;
  }
  public void setMaxTps(APIMaxTpsDTO maxTps) {
    this.maxTps = maxTps;
  }

  /**
   * The visibility level of the API. Accepts one of the following. PUBLIC, PRIVATE, RESTRICTED.
   **/
  public APIDTO visibility(VisibilityEnum visibility) {
    this.visibility = visibility;
    return this;
  }

  
  @ApiModelProperty(example = "PUBLIC", value = "The visibility level of the API. Accepts one of the following. PUBLIC, PRIVATE, RESTRICTED.")
  @JsonProperty("visibility")
  public VisibilityEnum getVisibility() {
    return visibility;
  }
  public void setVisibility(VisibilityEnum visibility) {
    this.visibility = visibility;
  }

  /**
   * The user roles that are able to access the API in Developer Portal
   **/
  public APIDTO visibleRoles(List<String> visibleRoles) {
    this.visibleRoles = visibleRoles;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "The user roles that are able to access the API in Developer Portal")
  @JsonProperty("visibleRoles")
  public List<String> getVisibleRoles() {
    return visibleRoles;
  }
  public void setVisibleRoles(List<String> visibleRoles) {
    this.visibleRoles = visibleRoles;
  }

  /**
   **/
  public APIDTO visibleTenants(List<String> visibleTenants) {
    this.visibleTenants = visibleTenants;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "")
  @JsonProperty("visibleTenants")
  public List<String> getVisibleTenants() {
    return visibleTenants;
  }
  public void setVisibleTenants(List<String> visibleTenants) {
    this.visibleTenants = visibleTenants;
  }

  /**
   * The organizations that are able to access the API in Developer Portal
   **/
  public APIDTO visibleOrganizations(List<String> visibleOrganizations) {
    this.visibleOrganizations = visibleOrganizations;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "The organizations that are able to access the API in Developer Portal")
  @JsonProperty("visibleOrganizations")
  public List<String> getVisibleOrganizations() {
    return visibleOrganizations;
  }
  public void setVisibleOrganizations(List<String> visibleOrganizations) {
    this.visibleOrganizations = visibleOrganizations;
  }

  /**
   **/
  public APIDTO mediationPolicies(List<MediationPolicyDTO> mediationPolicies) {
    this.mediationPolicies = mediationPolicies;
    return this;
  }

  
  @ApiModelProperty(example = "[{\"name\":\"json_to_xml_in_message\",\"type\":\"in\"},{\"name\":\"xml_to_json_out_message\",\"type\":\"out\"},{\"name\":\"json_fault\",\"type\":\"fault\"}]", value = "")
      @Valid
  @JsonProperty("mediationPolicies")
  public List<MediationPolicyDTO> getMediationPolicies() {
    return mediationPolicies;
  }
  public void setMediationPolicies(List<MediationPolicyDTO> mediationPolicies) {
    this.mediationPolicies = mediationPolicies;
  }

  /**
   **/
  public APIDTO apiPolicies(APIOperationPoliciesDTO apiPolicies) {
    this.apiPolicies = apiPolicies;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("apiPolicies")
  public APIOperationPoliciesDTO getApiPolicies() {
    return apiPolicies;
  }
  public void setApiPolicies(APIOperationPoliciesDTO apiPolicies) {
    this.apiPolicies = apiPolicies;
  }

  /**
   * The subscription availability. Accepts one of the following. CURRENT_TENANT, ALL_TENANTS or SPECIFIC_TENANTS.
   **/
  public APIDTO subscriptionAvailability(SubscriptionAvailabilityEnum subscriptionAvailability) {
    this.subscriptionAvailability = subscriptionAvailability;
    return this;
  }

  
  @ApiModelProperty(example = "CURRENT_TENANT", value = "The subscription availability. Accepts one of the following. CURRENT_TENANT, ALL_TENANTS or SPECIFIC_TENANTS.")
  @JsonProperty("subscriptionAvailability")
  public SubscriptionAvailabilityEnum getSubscriptionAvailability() {
    return subscriptionAvailability;
  }
  public void setSubscriptionAvailability(SubscriptionAvailabilityEnum subscriptionAvailability) {
    this.subscriptionAvailability = subscriptionAvailability;
  }

  /**
   **/
  public APIDTO subscriptionAvailableTenants(List<String> subscriptionAvailableTenants) {
    this.subscriptionAvailableTenants = subscriptionAvailableTenants;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "")
  @JsonProperty("subscriptionAvailableTenants")
  public List<String> getSubscriptionAvailableTenants() {
    return subscriptionAvailableTenants;
  }
  public void setSubscriptionAvailableTenants(List<String> subscriptionAvailableTenants) {
    this.subscriptionAvailableTenants = subscriptionAvailableTenants;
  }

  /**
   * Map of custom properties of API
   **/
  public APIDTO additionalProperties(List<APIInfoAdditionalPropertiesDTO> additionalProperties) {
    this.additionalProperties = additionalProperties;
    return this;
  }

  
  @ApiModelProperty(value = "Map of custom properties of API")
      @Valid
  @JsonProperty("additionalProperties")
  public List<APIInfoAdditionalPropertiesDTO> getAdditionalProperties() {
    return additionalProperties;
  }
  public void setAdditionalProperties(List<APIInfoAdditionalPropertiesDTO> additionalProperties) {
    this.additionalProperties = additionalProperties;
  }

  /**
   **/
  public APIDTO additionalPropertiesMap(Map<String, APIInfoAdditionalPropertiesMapDTO> additionalPropertiesMap) {
    this.additionalPropertiesMap = additionalPropertiesMap;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("additionalPropertiesMap")
  public Map<String, APIInfoAdditionalPropertiesMapDTO> getAdditionalPropertiesMap() {
    return additionalPropertiesMap;
  }
  public void setAdditionalPropertiesMap(Map<String, APIInfoAdditionalPropertiesMapDTO> additionalPropertiesMap) {
    this.additionalPropertiesMap = additionalPropertiesMap;
  }

  /**
   **/
  public APIDTO monetization(APIMonetizationInfoDTO monetization) {
    this.monetization = monetization;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("monetization")
  public APIMonetizationInfoDTO getMonetization() {
    return monetization;
  }
  public void setMonetization(APIMonetizationInfoDTO monetization) {
    this.monetization = monetization;
  }

  /**
   * Is the API is restricted to certain set of publishers or creators or is it visible to all the publishers and creators. If the accessControl restriction is none, this API can be modified by all the publishers and creators, if not it can only be viewable/modifiable by certain set of publishers and creators,  based on the restriction. 
   **/
  public APIDTO accessControl(AccessControlEnum accessControl) {
    this.accessControl = accessControl;
    return this;
  }

  
  @ApiModelProperty(value = "Is the API is restricted to certain set of publishers or creators or is it visible to all the publishers and creators. If the accessControl restriction is none, this API can be modified by all the publishers and creators, if not it can only be viewable/modifiable by certain set of publishers and creators,  based on the restriction. ")
  @JsonProperty("accessControl")
  public AccessControlEnum getAccessControl() {
    return accessControl;
  }
  public void setAccessControl(AccessControlEnum accessControl) {
    this.accessControl = accessControl;
  }

  /**
   * The user roles that are able to view/modify as API publisher or creator.
   **/
  public APIDTO accessControlRoles(List<String> accessControlRoles) {
    this.accessControlRoles = accessControlRoles;
    return this;
  }

  
  @ApiModelProperty(example = "[]", value = "The user roles that are able to view/modify as API publisher or creator.")
  @JsonProperty("accessControlRoles")
  public List<String> getAccessControlRoles() {
    return accessControlRoles;
  }
  public void setAccessControlRoles(List<String> accessControlRoles) {
    this.accessControlRoles = accessControlRoles;
  }

  /**
   **/
  public APIDTO businessInformation(APIBusinessInformationDTO businessInformation) {
    this.businessInformation = businessInformation;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("businessInformation")
  public APIBusinessInformationDTO getBusinessInformation() {
    return businessInformation;
  }
  public void setBusinessInformation(APIBusinessInformationDTO businessInformation) {
    this.businessInformation = businessInformation;
  }

  /**
   **/
  public APIDTO corsConfiguration(APICorsConfigurationDTO corsConfiguration) {
    this.corsConfiguration = corsConfiguration;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("corsConfiguration")
  public APICorsConfigurationDTO getCorsConfiguration() {
    return corsConfiguration;
  }
  public void setCorsConfiguration(APICorsConfigurationDTO corsConfiguration) {
    this.corsConfiguration = corsConfiguration;
  }

  /**
   **/
  public APIDTO websubSubscriptionConfiguration(WebsubSubscriptionConfigurationDTO websubSubscriptionConfiguration) {
    this.websubSubscriptionConfiguration = websubSubscriptionConfiguration;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("websubSubscriptionConfiguration")
  public WebsubSubscriptionConfigurationDTO getWebsubSubscriptionConfiguration() {
    return websubSubscriptionConfiguration;
  }
  public void setWebsubSubscriptionConfiguration(WebsubSubscriptionConfigurationDTO websubSubscriptionConfiguration) {
    this.websubSubscriptionConfiguration = websubSubscriptionConfiguration;
  }

  /**
   **/
  public APIDTO workflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
    return this;
  }

  
  @ApiModelProperty(example = "APPROVED", value = "")
  @JsonProperty("workflowStatus")
  public String getWorkflowStatus() {
    return workflowStatus;
  }
  public void setWorkflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  /**
   **/
  public APIDTO createdTime(String createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("createdTime")
  public String getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  /**
   **/
  public APIDTO lastUpdatedTimestamp(String lastUpdatedTimestamp) {
    this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("lastUpdatedTimestamp")
  public String getLastUpdatedTimestamp() {
    return lastUpdatedTimestamp;
  }
  public void setLastUpdatedTimestamp(String lastUpdatedTimestamp) {
    this.lastUpdatedTimestamp = lastUpdatedTimestamp;
  }

  /**
   **/
  public APIDTO lastUpdatedTime(String lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
    return this;
  }

  
  @ApiModelProperty(value = "")
  @JsonProperty("lastUpdatedTime")
  public String getLastUpdatedTime() {
    return lastUpdatedTime;
  }
  public void setLastUpdatedTime(String lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }

  /**
   * Endpoint configuration of the API. This can be used to provide different types of endpoints including Simple REST Endpoints, Loadbalanced and Failover.  &#x60;Simple REST Endpoint&#x60;    {     \&quot;endpoint_type\&quot;: \&quot;http\&quot;,     \&quot;sandbox_endpoints\&quot;:       {        \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/\&quot;     },     \&quot;production_endpoints\&quot;:       {        \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/\&quot;     }   }  &#x60;Loadbalanced Endpoint&#x60;    {     \&quot;endpoint_type\&quot;: \&quot;load_balance\&quot;,     \&quot;algoCombo\&quot;: \&quot;org.apache.synapse.endpoints.algorithms.RoundRobin\&quot;,     \&quot;sessionManagement\&quot;: \&quot;\&quot;,     \&quot;sandbox_endpoints\&quot;:       [                 {           \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/1\&quot;        },                 {           \&quot;endpoint_type\&quot;: \&quot;http\&quot;,           \&quot;template_not_supported\&quot;: false,           \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/2\&quot;        }     ],     \&quot;production_endpoints\&quot;:       [                 {           \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/3\&quot;        },                 {           \&quot;endpoint_type\&quot;: \&quot;http\&quot;,           \&quot;template_not_supported\&quot;: false,           \&quot;url\&quot;: \&quot;https://localhost:9443/am/sample/pizzashack/v3/api/4\&quot;        }     ],     \&quot;sessionTimeOut\&quot;: \&quot;\&quot;,     \&quot;algoClassName\&quot;: \&quot;org.apache.synapse.endpoints.algorithms.RoundRobin\&quot;   }  &#x60;Failover Endpoint&#x60;    {     \&quot;production_failovers\&quot;:[        {           \&quot;endpoint_type\&quot;:\&quot;http\&quot;,           \&quot;template_not_supported\&quot;:false,           \&quot;url\&quot;:\&quot;https://localhost:9443/am/sample/pizzashack/v3/api/1\&quot;        }     ],     \&quot;endpoint_type\&quot;:\&quot;failover\&quot;,     \&quot;sandbox_endpoints\&quot;:{        \&quot;url\&quot;:\&quot;https://localhost:9443/am/sample/pizzashack/v3/api/2\&quot;     },     \&quot;production_endpoints\&quot;:{        \&quot;url\&quot;:\&quot;https://localhost:9443/am/sample/pizzashack/v3/api/3\&quot;     },     \&quot;sandbox_failovers\&quot;:[        {           \&quot;endpoint_type\&quot;:\&quot;http\&quot;,           \&quot;template_not_supported\&quot;:false,           \&quot;url\&quot;:\&quot;https://localhost:9443/am/sample/pizzashack/v3/api/4\&quot;        }     ]   }  &#x60;Default Endpoint&#x60;    {     \&quot;endpoint_type\&quot;:\&quot;default\&quot;,     \&quot;sandbox_endpoints\&quot;:{        \&quot;url\&quot;:\&quot;default\&quot;     },     \&quot;production_endpoints\&quot;:{        \&quot;url\&quot;:\&quot;default\&quot;     }   }  &#x60;Endpoint from Endpoint Registry&#x60;    {     \&quot;endpoint_type\&quot;: \&quot;Registry\&quot;,     \&quot;endpoint_id\&quot;: \&quot;{registry-name:entry-name:version}\&quot;,   }  &#x60;AWS Lambda as Endpoint&#x60;    {     \&quot;endpoint_type\&quot;:\&quot;awslambda\&quot;,     \&quot;access_method\&quot;:\&quot;role-supplied|stored\&quot;,     \&quot;assume_role\&quot;:true|false,     \&quot;amznAccessKey\&quot;:\&quot;access_method&#x3D;&#x3D;stored?&lt;accessKey&gt;:&lt;empty&gt;\&quot;,     \&quot;amznSecretKey\&quot;:\&quot;access_method&#x3D;&#x3D;stored?&lt;secretKey&gt;:&lt;empty&gt;\&quot;,     \&quot;amznRegion\&quot;:\&quot;access_method&#x3D;&#x3D;stored?&lt;region&gt;:&lt;empty&gt;\&quot;,     \&quot;amznRoleArn\&quot;:\&quot;assume_role&#x3D;&#x3D;true?&lt;roleArn&gt;:&lt;empty&gt;\&quot;,     \&quot;amznRoleSessionName\&quot;:\&quot;assume_role&#x3D;&#x3D;true?&lt;roleSessionName&gt;:&lt;empty&gt;\&quot;,     \&quot;amznRoleRegion\&quot;:\&quot;assume_role&#x3D;&#x3D;true?&lt;roleRegion&gt;:&lt;empty&gt;\&quot;   } 
   **/
  public APIDTO endpointConfig(Object endpointConfig) {
    this.endpointConfig = endpointConfig;
    return this;
  }

  
  @ApiModelProperty(example = "{\"endpoint_type\":\"http\",\"sandbox_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/\"},\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/\"}}", value = "Endpoint configuration of the API. This can be used to provide different types of endpoints including Simple REST Endpoints, Loadbalanced and Failover.  `Simple REST Endpoint`    {     \"endpoint_type\": \"http\",     \"sandbox_endpoints\":       {        \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/\"     },     \"production_endpoints\":       {        \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/\"     }   }  `Loadbalanced Endpoint`    {     \"endpoint_type\": \"load_balance\",     \"algoCombo\": \"org.apache.synapse.endpoints.algorithms.RoundRobin\",     \"sessionManagement\": \"\",     \"sandbox_endpoints\":       [                 {           \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/1\"        },                 {           \"endpoint_type\": \"http\",           \"template_not_supported\": false,           \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/2\"        }     ],     \"production_endpoints\":       [                 {           \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/3\"        },                 {           \"endpoint_type\": \"http\",           \"template_not_supported\": false,           \"url\": \"https://localhost:9443/am/sample/pizzashack/v3/api/4\"        }     ],     \"sessionTimeOut\": \"\",     \"algoClassName\": \"org.apache.synapse.endpoints.algorithms.RoundRobin\"   }  `Failover Endpoint`    {     \"production_failovers\":[        {           \"endpoint_type\":\"http\",           \"template_not_supported\":false,           \"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/1\"        }     ],     \"endpoint_type\":\"failover\",     \"sandbox_endpoints\":{        \"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/2\"     },     \"production_endpoints\":{        \"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/3\"     },     \"sandbox_failovers\":[        {           \"endpoint_type\":\"http\",           \"template_not_supported\":false,           \"url\":\"https://localhost:9443/am/sample/pizzashack/v3/api/4\"        }     ]   }  `Default Endpoint`    {     \"endpoint_type\":\"default\",     \"sandbox_endpoints\":{        \"url\":\"default\"     },     \"production_endpoints\":{        \"url\":\"default\"     }   }  `Endpoint from Endpoint Registry`    {     \"endpoint_type\": \"Registry\",     \"endpoint_id\": \"{registry-name:entry-name:version}\",   }  `AWS Lambda as Endpoint`    {     \"endpoint_type\":\"awslambda\",     \"access_method\":\"role-supplied|stored\",     \"assume_role\":true|false,     \"amznAccessKey\":\"access_method==stored?<accessKey>:<empty>\",     \"amznSecretKey\":\"access_method==stored?<secretKey>:<empty>\",     \"amznRegion\":\"access_method==stored?<region>:<empty>\",     \"amznRoleArn\":\"assume_role==true?<roleArn>:<empty>\",     \"amznRoleSessionName\":\"assume_role==true?<roleSessionName>:<empty>\",     \"amznRoleRegion\":\"assume_role==true?<roleRegion>:<empty>\"   } ")
      @Valid
  @JsonProperty("endpointConfig")
  public Object getEndpointConfig() {
    return endpointConfig;
  }
  public void setEndpointConfig(Object endpointConfig) {
    this.endpointConfig = endpointConfig;
  }

  /**
   **/
  public APIDTO primaryProductionEndpointId(String primaryProductionEndpointId) {
    this.primaryProductionEndpointId = primaryProductionEndpointId;
    return this;
  }

  
  @ApiModelProperty(example = "13092607-ed01-4fa1-bc64-5da0e2abe92c", value = "")
  @JsonProperty("primaryProductionEndpointId")
  public String getPrimaryProductionEndpointId() {
    return primaryProductionEndpointId;
  }
  public void setPrimaryProductionEndpointId(String primaryProductionEndpointId) {
    this.primaryProductionEndpointId = primaryProductionEndpointId;
  }

  /**
   **/
  public APIDTO primarySandboxEndpointId(String primarySandboxEndpointId) {
    this.primarySandboxEndpointId = primarySandboxEndpointId;
    return this;
  }

  
  @ApiModelProperty(example = "13092607-ed01-4fa1-bc64-5da0e2abe92c", value = "")
  @JsonProperty("primarySandboxEndpointId")
  public String getPrimarySandboxEndpointId() {
    return primarySandboxEndpointId;
  }
  public void setPrimarySandboxEndpointId(String primarySandboxEndpointId) {
    this.primarySandboxEndpointId = primarySandboxEndpointId;
  }

  /**
   **/
  public APIDTO endpointImplementationType(EndpointImplementationTypeEnum endpointImplementationType) {
    this.endpointImplementationType = endpointImplementationType;
    return this;
  }

  
  @ApiModelProperty(example = "INLINE", value = "")
  @JsonProperty("endpointImplementationType")
  public EndpointImplementationTypeEnum getEndpointImplementationType() {
    return endpointImplementationType;
  }
  public void setEndpointImplementationType(EndpointImplementationTypeEnum endpointImplementationType) {
    this.endpointImplementationType = endpointImplementationType;
  }

  /**
   **/
  public APIDTO subtypeConfiguration(APISubtypeConfigurationDTO subtypeConfiguration) {
    this.subtypeConfiguration = subtypeConfiguration;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("subtypeConfiguration")
  public APISubtypeConfigurationDTO getSubtypeConfiguration() {
    return subtypeConfiguration;
  }
  public void setSubtypeConfiguration(APISubtypeConfigurationDTO subtypeConfiguration) {
    this.subtypeConfiguration = subtypeConfiguration;
  }

  /**
   **/
  public APIDTO scopes(List<APIScopeDTO> scopes) {
    this.scopes = scopes;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("scopes")
  public List<APIScopeDTO> getScopes() {
    return scopes;
  }
  public void setScopes(List<APIScopeDTO> scopes) {
    this.scopes = scopes;
  }

  /**
   **/
  public APIDTO operations(List<APIOperationsDTO> operations) {
    this.operations = operations;
    return this;
  }

  
  @ApiModelProperty(example = "[{\"target\":\"/order/{orderId}\",\"verb\":\"POST\",\"authType\":\"Application & Application User\",\"throttlingPolicy\":\"Unlimited\"},{\"target\":\"/menu\",\"verb\":\"GET\",\"authType\":\"Application & Application User\",\"throttlingPolicy\":\"Unlimited\"}]", value = "")
      @Valid
  @JsonProperty("operations")
  public List<APIOperationsDTO> getOperations() {
    return operations;
  }
  public void setOperations(List<APIOperationsDTO> operations) {
    this.operations = operations;
  }

  /**
   **/
  public APIDTO threatProtectionPolicies(APIThreatProtectionPoliciesDTO threatProtectionPolicies) {
    this.threatProtectionPolicies = threatProtectionPolicies;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("threatProtectionPolicies")
  public APIThreatProtectionPoliciesDTO getThreatProtectionPolicies() {
    return threatProtectionPolicies;
  }
  public void setThreatProtectionPolicies(APIThreatProtectionPoliciesDTO threatProtectionPolicies) {
    this.threatProtectionPolicies = threatProtectionPolicies;
  }

  /**
   * API categories 
   **/
  public APIDTO categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  
  @ApiModelProperty(value = "API categories ")
  @JsonProperty("categories")
  public List<String> getCategories() {
    return categories;
  }
  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  /**
   * API Key Managers 
   **/
  public APIDTO keyManagers(Object keyManagers) {
    this.keyManagers = keyManagers;
    return this;
  }

  
  @ApiModelProperty(value = "API Key Managers ")
      @Valid
  @JsonProperty("keyManagers")
  public Object getKeyManagers() {
    return keyManagers;
  }
  public void setKeyManagers(Object keyManagers) {
    this.keyManagers = keyManagers;
  }

  /**
   **/
  public APIDTO serviceInfo(APIServiceInfoDTO serviceInfo) {
    this.serviceInfo = serviceInfo;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("serviceInfo")
  public APIServiceInfoDTO getServiceInfo() {
    return serviceInfo;
  }
  public void setServiceInfo(APIServiceInfoDTO serviceInfo) {
    this.serviceInfo = serviceInfo;
  }

  /**
   **/
  public APIDTO advertiseInfo(AdvertiseInfoDTO advertiseInfo) {
    this.advertiseInfo = advertiseInfo;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("advertiseInfo")
  public AdvertiseInfoDTO getAdvertiseInfo() {
    return advertiseInfo;
  }
  public void setAdvertiseInfo(AdvertiseInfoDTO advertiseInfo) {
    this.advertiseInfo = advertiseInfo;
  }

  /**
   **/
  public APIDTO gatewayVendor(String gatewayVendor) {
    this.gatewayVendor = gatewayVendor;
    return this;
  }

  
  @ApiModelProperty(example = "wso2 external", value = "")
  @JsonProperty("gatewayVendor")
  public String getGatewayVendor() {
    return gatewayVendor;
  }
  public void setGatewayVendor(String gatewayVendor) {
    this.gatewayVendor = gatewayVendor;
  }

  /**
   * The gateway type selected for the API policies. Accepts one of the following. wso2/synapse, wso2/apk.
   **/
  public APIDTO gatewayType(String gatewayType) {
    this.gatewayType = gatewayType;
    return this;
  }

  
  @ApiModelProperty(example = "wso2/synapse wso2/apk AWS", value = "The gateway type selected for the API policies. Accepts one of the following. wso2/synapse, wso2/apk.")
  @JsonProperty("gatewayType")
  public String getGatewayType() {
    return gatewayType;
  }
  public void setGatewayType(String gatewayType) {
    this.gatewayType = gatewayType;
  }

  /**
   * Supported transports for the async API (http and/or https). 
   **/
  public APIDTO asyncTransportProtocols(List<String> asyncTransportProtocols) {
    this.asyncTransportProtocols = asyncTransportProtocols;
    return this;
  }

  
  @ApiModelProperty(example = "[\"http\",\"https\"]", value = "Supported transports for the async API (http and/or https). ")
  @JsonProperty("asyncTransportProtocols")
  public List<String> getAsyncTransportProtocols() {
    return asyncTransportProtocols;
  }
  public void setAsyncTransportProtocols(List<String> asyncTransportProtocols) {
    this.asyncTransportProtocols = asyncTransportProtocols;
  }

  /**
   * Whether the API is EGRESS or not
   **/
  public APIDTO egress(Boolean egress) {
    this.egress = egress;
    return this;
  }

  
  @ApiModelProperty(example = "true", value = "Whether the API is EGRESS or not")
  @JsonProperty("egress")
  public Boolean isEgress() {
    return egress;
  }
  public void setEgress(Boolean egress) {
    this.egress = egress;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    APIDTO API = (APIDTO) o;
    return Objects.equals(id, API.id) &&
        Objects.equals(name, API.name) &&
        Objects.equals(description, API.description) &&
        Objects.equals(context, API.context) &&
        Objects.equals(version, API.version) &&
        Objects.equals(provider, API.provider) &&
        Objects.equals(lifeCycleStatus, API.lifeCycleStatus) &&
        Objects.equals(wsdlInfo, API.wsdlInfo) &&
        Objects.equals(wsdlUrl, API.wsdlUrl) &&
        Objects.equals(responseCachingEnabled, API.responseCachingEnabled) &&
        Objects.equals(cacheTimeout, API.cacheTimeout) &&
        Objects.equals(hasThumbnail, API.hasThumbnail) &&
        Objects.equals(isDefaultVersion, API.isDefaultVersion) &&
        Objects.equals(isRevision, API.isRevision) &&
        Objects.equals(revisionedApiId, API.revisionedApiId) &&
        Objects.equals(revisionId, API.revisionId) &&
        Objects.equals(enableSchemaValidation, API.enableSchemaValidation) &&
        Objects.equals(enableSubscriberVerification, API.enableSubscriberVerification) &&
        Objects.equals(type, API.type) &&
        Objects.equals(audience, API.audience) &&
        Objects.equals(audiences, API.audiences) &&
        Objects.equals(transport, API.transport) &&
        Objects.equals(tags, API.tags) &&
        Objects.equals(policies, API.policies) &&
        Objects.equals(organizationPolicies, API.organizationPolicies) &&
        Objects.equals(apiThrottlingPolicy, API.apiThrottlingPolicy) &&
        Objects.equals(authorizationHeader, API.authorizationHeader) &&
        Objects.equals(apiKeyHeader, API.apiKeyHeader) &&
        Objects.equals(securityScheme, API.securityScheme) &&
        Objects.equals(maxTps, API.maxTps) &&
        Objects.equals(visibility, API.visibility) &&
        Objects.equals(visibleRoles, API.visibleRoles) &&
        Objects.equals(visibleTenants, API.visibleTenants) &&
        Objects.equals(visibleOrganizations, API.visibleOrganizations) &&
        Objects.equals(mediationPolicies, API.mediationPolicies) &&
        Objects.equals(apiPolicies, API.apiPolicies) &&
        Objects.equals(subscriptionAvailability, API.subscriptionAvailability) &&
        Objects.equals(subscriptionAvailableTenants, API.subscriptionAvailableTenants) &&
        Objects.equals(additionalProperties, API.additionalProperties) &&
        Objects.equals(additionalPropertiesMap, API.additionalPropertiesMap) &&
        Objects.equals(monetization, API.monetization) &&
        Objects.equals(accessControl, API.accessControl) &&
        Objects.equals(accessControlRoles, API.accessControlRoles) &&
        Objects.equals(businessInformation, API.businessInformation) &&
        Objects.equals(corsConfiguration, API.corsConfiguration) &&
        Objects.equals(websubSubscriptionConfiguration, API.websubSubscriptionConfiguration) &&
        Objects.equals(workflowStatus, API.workflowStatus) &&
        Objects.equals(createdTime, API.createdTime) &&
        Objects.equals(lastUpdatedTimestamp, API.lastUpdatedTimestamp) &&
        Objects.equals(lastUpdatedTime, API.lastUpdatedTime) &&
        Objects.equals(endpointConfig, API.endpointConfig) &&
        Objects.equals(primaryProductionEndpointId, API.primaryProductionEndpointId) &&
        Objects.equals(primarySandboxEndpointId, API.primarySandboxEndpointId) &&
        Objects.equals(endpointImplementationType, API.endpointImplementationType) &&
        Objects.equals(subtypeConfiguration, API.subtypeConfiguration) &&
        Objects.equals(scopes, API.scopes) &&
        Objects.equals(operations, API.operations) &&
        Objects.equals(threatProtectionPolicies, API.threatProtectionPolicies) &&
        Objects.equals(categories, API.categories) &&
        Objects.equals(keyManagers, API.keyManagers) &&
        Objects.equals(serviceInfo, API.serviceInfo) &&
        Objects.equals(advertiseInfo, API.advertiseInfo) &&
        Objects.equals(gatewayVendor, API.gatewayVendor) &&
        Objects.equals(gatewayType, API.gatewayType) &&
        Objects.equals(asyncTransportProtocols, API.asyncTransportProtocols) &&
        Objects.equals(egress, API.egress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, context, version, provider, lifeCycleStatus, wsdlInfo, wsdlUrl, responseCachingEnabled, cacheTimeout, hasThumbnail, isDefaultVersion, isRevision, revisionedApiId, revisionId, enableSchemaValidation, enableSubscriberVerification, type, audience, audiences, transport, tags, policies, organizationPolicies, apiThrottlingPolicy, authorizationHeader, apiKeyHeader, securityScheme, maxTps, visibility, visibleRoles, visibleTenants, visibleOrganizations, mediationPolicies, apiPolicies, subscriptionAvailability, subscriptionAvailableTenants, additionalProperties, additionalPropertiesMap, monetization, accessControl, accessControlRoles, businessInformation, corsConfiguration, websubSubscriptionConfiguration, workflowStatus, createdTime, lastUpdatedTimestamp, lastUpdatedTime, endpointConfig, primaryProductionEndpointId, primarySandboxEndpointId, endpointImplementationType, subtypeConfiguration, scopes, operations, threatProtectionPolicies, categories, keyManagers, serviceInfo, advertiseInfo, gatewayVendor, gatewayType, asyncTransportProtocols, egress);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    context: ").append(toIndentedString(context)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    lifeCycleStatus: ").append(toIndentedString(lifeCycleStatus)).append("\n");
    sb.append("    wsdlInfo: ").append(toIndentedString(wsdlInfo)).append("\n");
    sb.append("    wsdlUrl: ").append(toIndentedString(wsdlUrl)).append("\n");
    sb.append("    responseCachingEnabled: ").append(toIndentedString(responseCachingEnabled)).append("\n");
    sb.append("    cacheTimeout: ").append(toIndentedString(cacheTimeout)).append("\n");
    sb.append("    hasThumbnail: ").append(toIndentedString(hasThumbnail)).append("\n");
    sb.append("    isDefaultVersion: ").append(toIndentedString(isDefaultVersion)).append("\n");
    sb.append("    isRevision: ").append(toIndentedString(isRevision)).append("\n");
    sb.append("    revisionedApiId: ").append(toIndentedString(revisionedApiId)).append("\n");
    sb.append("    revisionId: ").append(toIndentedString(revisionId)).append("\n");
    sb.append("    enableSchemaValidation: ").append(toIndentedString(enableSchemaValidation)).append("\n");
    sb.append("    enableSubscriberVerification: ").append(toIndentedString(enableSubscriberVerification)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    audience: ").append(toIndentedString(audience)).append("\n");
    sb.append("    audiences: ").append(toIndentedString(audiences)).append("\n");
    sb.append("    transport: ").append(toIndentedString(transport)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    policies: ").append(toIndentedString(policies)).append("\n");
    sb.append("    organizationPolicies: ").append(toIndentedString(organizationPolicies)).append("\n");
    sb.append("    apiThrottlingPolicy: ").append(toIndentedString(apiThrottlingPolicy)).append("\n");
    sb.append("    authorizationHeader: ").append(toIndentedString(authorizationHeader)).append("\n");
    sb.append("    apiKeyHeader: ").append(toIndentedString(apiKeyHeader)).append("\n");
    sb.append("    securityScheme: ").append(toIndentedString(securityScheme)).append("\n");
    sb.append("    maxTps: ").append(toIndentedString(maxTps)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    visibleRoles: ").append(toIndentedString(visibleRoles)).append("\n");
    sb.append("    visibleTenants: ").append(toIndentedString(visibleTenants)).append("\n");
    sb.append("    visibleOrganizations: ").append(toIndentedString(visibleOrganizations)).append("\n");
    sb.append("    mediationPolicies: ").append(toIndentedString(mediationPolicies)).append("\n");
    sb.append("    apiPolicies: ").append(toIndentedString(apiPolicies)).append("\n");
    sb.append("    subscriptionAvailability: ").append(toIndentedString(subscriptionAvailability)).append("\n");
    sb.append("    subscriptionAvailableTenants: ").append(toIndentedString(subscriptionAvailableTenants)).append("\n");
    sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
    sb.append("    additionalPropertiesMap: ").append(toIndentedString(additionalPropertiesMap)).append("\n");
    sb.append("    monetization: ").append(toIndentedString(monetization)).append("\n");
    sb.append("    accessControl: ").append(toIndentedString(accessControl)).append("\n");
    sb.append("    accessControlRoles: ").append(toIndentedString(accessControlRoles)).append("\n");
    sb.append("    businessInformation: ").append(toIndentedString(businessInformation)).append("\n");
    sb.append("    corsConfiguration: ").append(toIndentedString(corsConfiguration)).append("\n");
    sb.append("    websubSubscriptionConfiguration: ").append(toIndentedString(websubSubscriptionConfiguration)).append("\n");
    sb.append("    workflowStatus: ").append(toIndentedString(workflowStatus)).append("\n");
    sb.append("    createdTime: ").append(toIndentedString(createdTime)).append("\n");
    sb.append("    lastUpdatedTimestamp: ").append(toIndentedString(lastUpdatedTimestamp)).append("\n");
    sb.append("    lastUpdatedTime: ").append(toIndentedString(lastUpdatedTime)).append("\n");
    sb.append("    endpointConfig: ").append(toIndentedString(endpointConfig)).append("\n");
    sb.append("    primaryProductionEndpointId: ").append(toIndentedString(primaryProductionEndpointId)).append("\n");
    sb.append("    primarySandboxEndpointId: ").append(toIndentedString(primarySandboxEndpointId)).append("\n");
    sb.append("    endpointImplementationType: ").append(toIndentedString(endpointImplementationType)).append("\n");
    sb.append("    subtypeConfiguration: ").append(toIndentedString(subtypeConfiguration)).append("\n");
    sb.append("    scopes: ").append(toIndentedString(scopes)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    threatProtectionPolicies: ").append(toIndentedString(threatProtectionPolicies)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    keyManagers: ").append(toIndentedString(keyManagers)).append("\n");
    sb.append("    serviceInfo: ").append(toIndentedString(serviceInfo)).append("\n");
    sb.append("    advertiseInfo: ").append(toIndentedString(advertiseInfo)).append("\n");
    sb.append("    gatewayVendor: ").append(toIndentedString(gatewayVendor)).append("\n");
    sb.append("    gatewayType: ").append(toIndentedString(gatewayType)).append("\n");
    sb.append("    asyncTransportProtocols: ").append(toIndentedString(asyncTransportProtocols)).append("\n");
    sb.append("    egress: ").append(toIndentedString(egress)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

