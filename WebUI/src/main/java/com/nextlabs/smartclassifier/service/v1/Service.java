package com.nextlabs.smartclassifier.service.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.google.gson.Gson;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.dto.v1.request.ReportQueryRequest;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.request.SearchCriteria;
import com.nextlabs.smartclassifier.dto.v1.request.SortField;
import com.nextlabs.smartclassifier.util.ComponentStatusUtil;

public abstract class Service {

  protected final Logger logger;
  protected static Gson gson = new Gson();
  protected ServletContext servletContext;
  protected ServletRequest servletRequest;
  protected ServletResponse servletResponse;

  public Service(ServletContext servletContext) {
    super();
    this.servletContext = servletContext;
    this.logger = LogManager.getLogger(getClass());
  }

  public Service(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super();
    this.servletContext = servletContext;
    this.servletRequest = servletRequest;
    this.servletResponse = servletResponse;
    this.logger = LogManager.getLogger(getClass());
  }

  protected SessionFactory getSessionFactory() {
    return (SessionFactory) servletContext.getAttribute("SessionFactory");
  }

  @SuppressWarnings("unchecked")
  protected String getSystemConfig(String identifier) {
    return ((Map<String, String>) servletContext.getAttribute("SystemConfigs")).get(identifier);
  }

  @SuppressWarnings("unchecked")
  protected Map<String, MetadataField> getMetadataFields() {
    return (Map<String, MetadataField>) servletContext.getAttribute("MetadataFields");
  }

  protected String getUserName() {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

    if (httpServletRequest != null
        && httpServletRequest.getSession().getAttribute("UserInfo") != null) {
      @SuppressWarnings("unchecked")
      Map<String, String> userInfo =
          (Map<String, String>) httpServletRequest.getSession().getAttribute("UserInfo");

      return composeDisplayName(userInfo.get("FirstName"), userInfo.get("LastName"));
    }

    return "Unknown";
  }

  public String getComponentStatus(Long lastHeartbeat, String heartbeatIntervalKey) {
    if (lastHeartbeat != null) {
      return ComponentStatusUtil.getComponentStatus(
          lastHeartbeat,
          Integer.parseInt(getSystemConfig(heartbeatIntervalKey)),
          Integer.parseInt(getSystemConfig(SystemConfigKey.HEARTBEAT_CRITICAL_FAILED_COUNT)));
    } else {
      return ComponentStatusUtil.getComponentStatus(
          0L,
          Integer.parseInt(getSystemConfig(heartbeatIntervalKey)),
          Integer.parseInt(getSystemConfig(SystemConfigKey.HEARTBEAT_CRITICAL_FAILED_COUNT)));
    }
  }

  protected PageInfo getPageInfo(RetrievalRequest request) {
    PageInfo pageInfo = new PageInfo();

    if (request != null) {
      if (request.getPageNo() == 0) {
        pageInfo.setPageNumber(1);
      } else {
        pageInfo.setPageNumber(request.getPageNo());
      }

      if (request.getPageSize() == 0) {
        pageInfo.setSize(
            Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
      } else {
        pageInfo.setSize(request.getPageSize());
      }

      pageInfo.setSkip((request.getPageNo() - 1) * request.getPageSize());
    } else {
      pageInfo.setPageNumber(1);
      pageInfo.setSize(Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
    }

    return pageInfo;
  }

  protected PageInfo getQueryPageInfo(ReportQueryRequest request) {
    PageInfo pageInfo = new PageInfo();

    if (request != null) {
      if (request.getPageNo() == 0) {
        pageInfo.setPageNumber(1);
      } else {
        pageInfo.setPageNumber(request.getPageNo());
      }

      if (request.getPageSize() == 0) {
        pageInfo.setSize(
            Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
      } else {
        pageInfo.setSize(request.getPageSize());
      }

      pageInfo.setSkip((request.getPageNo() - 1) * request.getPageSize());
    } else {
      pageInfo.setPageNumber(1);
      pageInfo.setSize(Integer.parseInt(getSystemConfig(SystemConfigKey.RESTFUL_RECORD_PAGE_SIZE)));
    }

    return pageInfo;
  }

  protected List<Criterion> getCriterion(RetrievalRequest request) {
    List<Criterion> criterionList = new ArrayList<>();

    if (request != null && request.getCriteria() != null) {
      for (SearchCriteria criteria : request.getCriteria()) {}
    }

    return criterionList;
  }

  protected List<Order> getOrder(RetrievalRequest request) {
    List<Order> orderList = new ArrayList<Order>();

    if (request != null && request.getSortFields() != null) {
      for (SortField sortField : request.getSortFields()) {
        if ("asc".equalsIgnoreCase(sortField.getOrder())) {
          orderList.add(Order.asc(sortField.getField()));
        } else {
          orderList.add(Order.desc(sortField.getField()));
        }
      }
    }

    return orderList;
  }

  protected List<Order> getOrder(ReportQueryRequest request) {
    List<Order> orderList = new ArrayList<Order>();

    if (request != null && request.getSortFields() != null) {
      for (SortField sortField : request.getSortFields()) {
        if ("asc".equalsIgnoreCase(sortField.getOrder())) {
          orderList.add(Order.asc(sortField.getField()));
        } else {
          orderList.add(Order.desc(sortField.getField()));
        }
      }
    }

    return orderList;
  }

  private String composeDisplayName(String firstName, String lastName) {
    if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
      return firstName + Punctuation.SPACE + lastName;
    }

    if (StringUtils.isNotBlank(lastName)) {
      return lastName;
    }

    if (StringUtils.isNotBlank(firstName)) {
      return firstName;
    }

    return "[NO NAME]";
  }
}
