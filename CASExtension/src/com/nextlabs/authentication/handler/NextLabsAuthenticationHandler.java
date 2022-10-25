package com.nextlabs.authentication.handler;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.security.auth.login.FailedLoginException;

import org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.LdapAuthenticationHandler;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.BindAuthenticationHandler;
import org.ldaptive.auth.FormatDnResolver;
import org.ldaptive.auth.SearchEntryResolver;
import org.ldaptive.auth.ext.ActiveDirectoryAuthenticationResponseHandler;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nextlabs.authentication.model.AuthenticationHandlerDetails;

public class NextLabsAuthenticationHandler 
		extends AbstractUsernamePasswordAuthenticationHandler {
	
	private ComboPooledDataSource dataSource;
	private QueryDatabaseAuthenticationHandler primaryHandler;
	private Map<Long, AuthenticationHandlerDetails> ldapAuthenticationHandlers;
	private boolean refresh;
	private long refreshInterval;
	private long lastRefreshTime;
	
	private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
	@PostConstruct
	public void loadAuthenticationHandlers() {
		ldapAuthenticationHandlers = new ConcurrentHashMap<Long, AuthenticationHandlerDetails>();
		loadAllAuthenticationHandlers();
	}
	
	@Override
	protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential)
			throws GeneralSecurityException, PreventedException {
		reloadAuthenticationHandlers();
		
		HandlerResult handlerResult = null;
		
		try {
			handlerResult = primaryHandler.authenticate(credential);
		} catch(Exception err) {
			if(ldapAuthenticationHandlers.size() > 0) {
				handlerResult = performSecondaryAuthentication(credential);
			}
		}
		
		if(handlerResult == null) {
			throw new FailedLoginException("Authentication failed.");
		}
		
		return handlerResult;
	}
	
	private void reloadAuthenticationHandlers() {
		if(refresh) {
			if(((System.currentTimeMillis() - lastRefreshTime)/1000) > refreshInterval) {
				loadAllAuthenticationHandlers();
			}
		}
	}
	
	private void loadAllAuthenticationHandlers() {
		String query = "SELECT ID, TYPE, CONFIGURATION_DATA FROM AUTH_HANDLERS";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Map<Long, AuthenticationHandlerDetails> authenticationHandlers = new ConcurrentHashMap<Long, AuthenticationHandlerDetails>();
		
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				Long handlerId = resultSet.getLong("ID");
				AuthenticationHandlerDetails authenticationHandlerDetails = gson.fromJson(resultSet.getString("CONFIGURATION_DATA"), AuthenticationHandlerDetails.class);
				authenticationHandlerDetails.setType(resultSet.getString("TYPE"));
				
				if("LDAP".equalsIgnoreCase(authenticationHandlerDetails.getType())) {
					authenticationHandlers.put(handlerId, authenticationHandlerDetails);
				}
			}
			
			// Set new handlers to 
			synchronized (ldapAuthenticationHandlers) {
				ldapAuthenticationHandlers = authenticationHandlers;
			}
			
			lastRefreshTime = System.currentTimeMillis();
		} catch(SQLException err) {
			logger.error(err.getMessage(), err);
		} finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
			
			if(preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
			
			if(connection != null) {
				try{
					connection.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
		}
	}
	
	private HandlerResult performSecondaryAuthentication(UsernamePasswordCredential credential)
			throws GeneralSecurityException, PreventedException {
		logger.info("LDAP authentication for username:" + credential.getUsername());
		String query = "SELECT AUTH_HANDLER_ID FROM USERS WHERE USER_TYPE = ? AND USERNAME = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "L");
			preparedStatement.setString(2, credential.getUsername());
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				Long authenticationHandlerId = resultSet.getLong("AUTH_HANDLER_ID");
				
				if(ldapAuthenticationHandlers.containsKey(authenticationHandlerId)) {
					LdapAuthenticationHandler authenticationHandler = getAuthenticationHandler(ldapAuthenticationHandlers.get(authenticationHandlerId));
					
					if(authenticationHandler != null) {
						return authenticationHandler.authenticate(credential);
					}
				}
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		} finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
			
			if(preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
			
			if(connection != null) {
				try{
					connection.close();
				} catch(SQLException err) {
					logger.error(err.getMessage(), err);
				}
			}
		}
		
		return null;
	}
	
	private LdapAuthenticationHandler getAuthenticationHandler(AuthenticationHandlerDetails authenticationHandlerDetails) 
			throws Exception {
		try {
			if(authenticationHandlerDetails != null) {
				SslConfig sslConfig = new SslConfig();
				X509CredentialConfig credentialConfig = new X509CredentialConfig();
				credentialConfig.setTrustCertificates("");
				sslConfig.setCredentialConfig(credentialConfig);
				
				ConnectionConfig connectionConfig = new ConnectionConfig(authenticationHandlerDetails.getUrl());
				connectionConfig.setConnectTimeout(30000);
				connectionConfig.setUseStartTLS(false);
				connectionConfig.setSslConfig(sslConfig);
				
				ConnectionFactory connectionFactory = new DefaultConnectionFactory(connectionConfig);
				
				SearchEntryResolver entryResolver = new SearchEntryResolver(connectionFactory);
				entryResolver.setBaseDn(authenticationHandlerDetails.getRootDN());
				entryResolver.setUserFilter(authenticationHandlerDetails.getUserFilter());
				entryResolver.setSubtreeSearch(true);
				
				Authenticator authenticator = new Authenticator(new FormatDnResolver("%s@" 
						+ authenticationHandlerDetails.getDomain()), new BindAuthenticationHandler(connectionFactory));
				authenticator.setEntryResolver(entryResolver);
				authenticator.setAuthenticationResponseHandlers(new ActiveDirectoryAuthenticationResponseHandler());
				
				return new LdapAuthenticationHandler(authenticator);
			}
		} catch(Exception err) {
			throw err;
		}
		
		return null;
	}
	
	private ComboPooledDataSource decryptDatabasePassword(ComboPooledDataSource dataSource) {
		IDecryptor decryptor = new ReversibleEncryptor();
		dataSource.setPassword(decryptor.decrypt(dataSource.getPassword()));
		
		return dataSource;
	} 
	
	public ComboPooledDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(ComboPooledDataSource dataSource) {
		if(dataSource != null) {
			this.dataSource = decryptDatabasePassword(dataSource);
		}
	}
	
	public QueryDatabaseAuthenticationHandler getPrimaryHandler() {
		return primaryHandler;
	}
	
	public void setPrimaryHandler(QueryDatabaseAuthenticationHandler primaryHandler) {
		this.primaryHandler = primaryHandler;
	}
	
	public boolean isRefresh() {
		return refresh;
	}
	
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
	
	public long getRefreshInterval() {
		return refreshInterval;
	}
	
	public void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
}	
