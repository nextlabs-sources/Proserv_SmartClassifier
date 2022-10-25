package com.nextlabs.smartclassifier.service.v1.common;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Created by pkalra on 11/30/2016. */
public class RepositoryTypeService extends Service {

  public RepositoryTypeService(ServletContext servletContext) {
    super(servletContext);
  }

  public RepositoryTypeService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getRepositoryTypes() {
    CollectionResponse response = new CollectionResponse();

    List<String> repositoryTypes = new ArrayList<>();

    for (RepositoryType repositoryType : RepositoryType.values()) {
      repositoryTypes.add(repositoryType.getName());
    }

    if (repositoryTypes.size() > 0) {
      response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
      response.setMessage(MessageUtil.getMessage("success.data.loaded"));
    } else {
      response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
      response.setMessage(MessageUtil.getMessage("no.data.found"));
    }

    response.setTotalNoOfRecords(repositoryTypes.size());
    response.setData(repositoryTypes);

    return response;
  }
}
