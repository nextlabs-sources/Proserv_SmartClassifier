package com.nextlabs.smartclassifier.controller.v1;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.ExecutionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.IndexQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.request.ReportQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;

public class Controller {

  @Context protected ServletContext servletContext;

  @Context protected HttpServletRequest httpServletRequest;

  @Context protected HttpServletResponse httpServletResponse;

  protected static Gson gson;

  public Controller() {
    super();
    gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  }

  protected CreationRequest getCreationRequest(
      String requestBody, Class<? extends BaseDTO> dtoClass) {
    CreationRequest creationRequest = new CreationRequest();
    creationRequest.setData(gson.fromJson(requestBody, dtoClass));
    return creationRequest;
  }

  protected RetrievalRequest getRetrievalRequest(String requestBody) {
    return gson.fromJson(requestBody, RetrievalRequest.class);
  }

  protected IndexQueryRequest getIndexQueryRequest(String requestBody) {
    return gson.fromJson(requestBody, IndexQueryRequest.class);
  }

  protected ReportQueryRequest getReportQueryRequest(String requestBody) {
    return gson.fromJson(requestBody, ReportQueryRequest.class);
  }

  protected UpdateRequest getUpdateRequest(String requestBody, Class<? extends BaseDTO> dtoClass) {
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setData(gson.fromJson(requestBody, dtoClass));
    return updateRequest;
  }

  protected ExecutionRequest getExecutionRequest(
      String requestBody, Class<? extends BaseDTO> dtoClass) {
    ExecutionRequest executionRequest = new ExecutionRequest();
    executionRequest.setData(gson.fromJson(requestBody, dtoClass));
    return executionRequest;
  }

  protected Long toLong(String value) throws NumberFormatException {
    if (StringUtils.isNotBlank(value)) {
      return Long.parseLong(value);
    }

    return new Long(0);
  }

  protected String error(String errorCode, String errorMessage) {
    ServiceResponse response = new ServiceResponse();
    response.setStatusCode(errorCode);
    response.setMessage(errorMessage);

    return response.toJson();
  }
}
