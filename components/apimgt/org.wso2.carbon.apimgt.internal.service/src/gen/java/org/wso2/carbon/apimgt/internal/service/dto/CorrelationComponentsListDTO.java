package org.wso2.carbon.apimgt.internal.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.apimgt.internal.service.dto.CorrelationComponentDTO;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;



public class CorrelationComponentsListDTO   {
  
    private List<CorrelationComponentDTO> components = new ArrayList<>();

  /**
   **/
  public CorrelationComponentsListDTO components(List<CorrelationComponentDTO> components) {
    this.components = components;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("components")
  public List<CorrelationComponentDTO> getComponents() {
    return components;
  }
  public void setComponents(List<CorrelationComponentDTO> components) {
    this.components = components;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CorrelationComponentsListDTO correlationComponentsList = (CorrelationComponentsListDTO) o;
    return Objects.equals(components, correlationComponentsList.components);
  }

  @Override
  public int hashCode() {
    return Objects.hash(components);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorrelationComponentsListDTO {\n");
    
    sb.append("    components: ").append(toIndentedString(components)).append("\n");
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

