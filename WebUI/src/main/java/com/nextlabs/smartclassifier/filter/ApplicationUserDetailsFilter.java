package com.nextlabs.smartclassifier.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.constant.UserType;
import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.authentication.LDAPService;

public class ApplicationUserDetailsFilter 
		implements Filter {
	
	private static final Logger logger = LogManager.getLogger(ApplicationUserDetailsFilter.class);
	private static final String UNAUTHENTICATED_RESPONSE = "{\"statusCode\":\"6004\", \"message\":\"Unauthenticated service access.\"}";
	
	protected FilterConfig filterConfig;
	
	@Override
	public void destroy() {
		// Do nothing
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chainFilter)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		ServletContext servletContext = this.filterConfig.getServletContext();
		
		// Exclude file download path
		if(httpServletRequest.getRequestURI().contains("/download/"))
			chainFilter.doFilter(servletRequest, servletResponse);
		
		if(httpServletRequest.getSession().getAttribute("LoggedIn") == null
				&& httpServletRequest.getSession().getAttribute("UserInfo") == null) {
			logger.debug("No logged in information in session.");
			
			if(httpServletRequest.getRemoteUser() != null) {
				logger.debug("Request contains remote user information.");
				Principal principal = httpServletRequest.getUserPrincipal();
				if(populateUserDetails(servletContext, httpServletRequest.getSession(), principal.getName())) {
					logger.debug("User information populated successfully.");
					chainFilter.doFilter(servletRequest, servletResponse);
				} else {
					logger.warn("Failed to populate user information, redirect to login page.");
					logUserOut(httpServletRequest, (HttpServletResponse)servletResponse, servletContext);
				}
			} else {
				logger.warn("Remote user information not found, redirect to login page.");
				logUserOut(httpServletRequest, (HttpServletResponse)servletResponse, servletContext);
			}
		} else {
			chainFilter.doFilter(servletRequest, servletResponse);
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig)
			throws ServletException {
		this.filterConfig = filterConfig;
	}
	
	@SuppressWarnings("unchecked")
	private String getSystemConfig(ServletContext servletContext, String identifier) {
		return (String)((Map<String, String>)servletContext.getAttribute("SystemConfigs")).get(identifier);
	}
	
	private boolean populateUserDetails(ServletContext servletContext, HttpSession httpSession, String username) {
		logger.debug("Populating user details for " + username);
		boolean userDetailsPopulated = false;
		Session session = null;
		Transaction transaction = null;
		
		try {
			SessionFactory sessionFactory = (SessionFactory)servletContext.getAttribute("SessionFactory");
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			
			User user = new UserManager(sessionFactory, session).getUserByUsername(username);
			
			if(user != null) {
				if(user.getUserType().equals(UserType.INTERNAL.getCode())) {
					Map<String, String> userInfo = new HashMap<String, String>();
					
					userInfo.put("Id", user.getId().toString());
					userInfo.put("UserType", user.getUserType());
					userInfo.put("Username", user.getUsername());
					userInfo.put("FirstName", user.getFirstName());
					userInfo.put("LastName", user.getLastName());
					userInfo.put("IsAdmin", user.isAdmin().toString());
					
					httpSession.setAttribute("UserInfo", userInfo);
					httpSession.setAttribute("LoggedIn", Boolean.TRUE);
					
					userDetailsPopulated = true;
					transaction.commit();
				} else if(user.getUserType().equals(UserType.LDAP.getCode())) {
					// Retrieve from LDAP
					AuthenticationHandler authenticationHandler = user.getAuthenticationHandler();
					// Commit DB transaction before perform long haul service call
					transaction.commit();
					
					if(authenticationHandler != null) {
						LDAPService ldapService = new LDAPService(servletContext);
						UserDTO userDTO = ldapService.getUserInfoByUsername(authenticationHandler, user.getUsername());
						
						if(userDTO != null) {
							Map<String, String> userInfo = new HashMap<String, String>();
							userInfo.put("Id", user.getId().toString());
							userInfo.put("UserType", user.getUserType());
							userInfo.put("Username", user.getUsername());
							userInfo.put("FirstName", userDTO.getFirstName());
							userInfo.put("LastName",  userDTO.getLastName());
							userInfo.put("IsAdmin", user.isAdmin().toString());
							
							httpSession.setAttribute("UserInfo", userInfo);
							httpSession.setAttribute("LoggedIn", Boolean.TRUE);
							
							userDetailsPopulated = true;
						}
					} else {
						logger.error("Authentication handler is null for external user.");
					}
				}
			}
		} catch(ManagerException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
		} catch(Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return userDetailsPopulated;
	}
	
	private void logUserOut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ServletContext servletContext)
		throws IOException, ServletException {
		logger.debug("Logging user out in filter.");
		String loginURL = getSystemConfig(servletContext, SystemConfigKey.LOGIN_URL) 
				+ getSystemConfig(servletContext, SystemConfigKey.SMART_CLASSIFIER_URL);
		
		httpServletResponse.getWriter().print(UNAUTHENTICATED_RESPONSE);
		httpServletResponse.getWriter().flush();
		
		httpServletRequest.getSession().invalidate();
		httpServletResponse.sendRedirect(loginURL);
	}
}
