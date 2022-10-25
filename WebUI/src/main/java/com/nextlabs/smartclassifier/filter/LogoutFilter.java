package com.nextlabs.smartclassifier.filter;

import java.io.IOException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;

public class LogoutFilter
		implements Filter {
	
	private static final Logger logger = LogManager.getLogger(LogoutFilter.class);
	
	protected FilterConfig filterConfig;
	
	@Override
	public void destroy() {
		// Do nothing
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		ServletContext servletContext = this.filterConfig.getServletContext();
		HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
		
		if(httpServletRequest.getRequestURI().contains("/logout") || httpServletRequest.getRequestURI().contains("/login.jsf")) {
			logger.info("Logout request came " + httpServletRequest.getRequestURI());
			
			if(httpServletRequest.getSession() != null) {
				httpServletRequest.getSession().invalidate();
			}
			
			String logoutURL = getSystemConfig(servletContext, SystemConfigKey.LOGOUT_URL) 
					+ getSystemConfig(servletContext, SystemConfigKey.SMART_CLASSIFIER_URL);
			
			httpServletResponse.sendRedirect(logoutURL);
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
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
}
