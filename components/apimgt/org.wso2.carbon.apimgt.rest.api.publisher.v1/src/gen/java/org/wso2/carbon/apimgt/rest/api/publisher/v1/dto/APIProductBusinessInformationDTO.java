package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class APIProductBusinessInformationDTO  {
  
  
  
  private String businessOwner = null;
  
  
  private String businessOwnerEmail = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("businessOwner")
  public String getBusinessOwner() {
    return businessOwner;
  }
  public void setBusinessOwner(String businessOwner) {
    this.businessOwner = businessOwner;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("businessOwnerEmail")
  public String getBusinessOwnerEmail() {
    return businessOwnerEmail;
  }
  public void setBusinessOwnerEmail(String businessOwnerEmail) {
    this.businessOwnerEmail = businessOwnerEmail;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIProductBusinessInformationDTO {\n");
    
    sb.append("  businessOwner: ").append(businessOwner).append("\n");
    sb.append("  businessOwnerEmail: ").append(businessOwnerEmail).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
