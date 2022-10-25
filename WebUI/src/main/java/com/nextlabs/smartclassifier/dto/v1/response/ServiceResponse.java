package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class ServiceResponse {

  @Expose private String statusCode;
  @Expose private String message;

  protected static transient Gson gson;

  public ServiceResponse() {
    super();
    gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String toJson() {
    return gson.toJson(this);
  }
}
