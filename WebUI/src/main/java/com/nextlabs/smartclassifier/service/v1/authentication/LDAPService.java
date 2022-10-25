package com.nextlabs.smartclassifier.service.v1.authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.SortControl;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.database.manager.UserManager;
import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.LDAPConfigurationDataDTO;
import com.nextlabs.smartclassifier.dto.v1.MappingDTO;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.ExecutionRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ExecutionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class LDAPService 
		extends Service {
	
	private static final int MAX_RECORD = 50;
	
	public LDAPService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public LDAPService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse connect(ExecutionRequest executionRequest) {
		ExecutionResponse response = new ExecutionResponse();
		LdapContext ldapContext = null;
		
		try {
			AuthenticationHandlerDTO authenticationHandlerDTO = (AuthenticationHandlerDTO)executionRequest.getData();
			// Create LDAP context
			ldapContext = getLdapContext(authenticationHandlerDTO);
			
			if(ldapContext != null) {
				// Retrieve user attributes
				response.setData(getLdapUserAttributes(ldapContext, authenticationHandlerDTO.getConfigurationData()));
			}
			
			response.setStatusCode(MessageUtil.getMessage("success.ldap.connection.code"));
			response.setMessage(MessageUtil.getMessage("success.ldap.connection"));
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("failed.ldap.connection.code"));
			response.setMessage(MessageUtil.getMessage("failed.ldap.connection"));
		} finally {
			if(ldapContext != null) {
				try {
					ldapContext.close();
				} catch(NamingException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
	
	public UserDTO getUserInfoByUsername(AuthenticationHandler authenticationHandler, String username) {
		UserDTO userDTO = null;
		LdapContext ldapContext = null;
		NamingEnumeration<SearchResult> resultEnum = null;
		
		try {
			if(authenticationHandler != null) {
				AuthenticationHandlerDTO authenticationHandlerDTO = new AuthenticationHandlerDTO(authenticationHandler);
				ldapContext = getLdapContext(authenticationHandlerDTO);
				SearchControls searchControls = getSimpleSearchControls(authenticationHandlerDTO.getUserAttributeMapping());
				Map<String, String> attributeMap = getAttributeMap(authenticationHandlerDTO.getUserAttributeMapping());
				resultEnum = ldapContext.search(authenticationHandlerDTO.getConfigurationData().getRootDN(), 
						attributeMap.get("username") + "=" + username, 
						searchControls);
				
				if(resultEnum.hasMore()) {
					Attributes attributes = ((SearchResult)resultEnum.next()).getAttributes();
					
					userDTO = new UserDTO();
					userDTO.setFirstName(attributes.get(attributeMap.get("firstName")) == null ? "" : String.valueOf(attributes.get(attributeMap.get("firstName")).get()));
					userDTO.setLastName(attributes.get(attributeMap.get("lastName")) == null ? "" : String.valueOf(attributes.get(attributeMap.get("lastName")).get()));
				}
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		} finally {
			if(resultEnum != null) {
				try {
					resultEnum.close();
				} catch(NamingException err) {
					// Ignore
				}
			}
			
			if(ldapContext != null) {
				try {
					ldapContext.close();
				} catch(NamingException err) {
					// Ignore
				}
			}
		}
		
		return userDTO;
	}
	
	public ServiceResponse getUsers(ExecutionRequest executionRequest) {
		RetrievalResponse response = new RetrievalResponse();
		LdapContext ldapContext = null;
		boolean hasMore = false;
		
		try {
			AuthenticationHandlerDTO authenticationHandlerDTO = (AuthenticationHandlerDTO)executionRequest.getData();
			List<UserDTO> userDTOs = new ArrayList<UserDTO>();
			ldapContext = getLdapContext(authenticationHandlerDTO);
			
			if(ldapContext != null) {
				Set<String> registeredUsers = getRegisteredUsers();
				Map<String, String> attributeMap = getAttributeMap(authenticationHandlerDTO.getUserAttributeMapping());
				Control[] requestControls = new Control[1];
				requestControls[0] = new SortControl(attributeMap.get("username") , Control.CRITICAL);
				ldapContext.setRequestControls(requestControls);
				
				SearchControls searchControls = new SearchControls();
				
				if(authenticationHandlerDTO.getUserAttributeMapping() != null) {
					List<String> attributes = new ArrayList<String>();
					for(MappingDTO mappingDTO : authenticationHandlerDTO.getUserAttributeMapping()) {
						attributes.add(mappingDTO.getFrom());
					}
					
					searchControls.setReturningAttributes(attributes.toArray(new String[attributes.size()]));
				}
				
				searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				searchControls.setTimeLimit(30000);
				
				String filterString = getFilterString(authenticationHandlerDTO);
				logger.debug("Filter: " + filterString);
				
				NamingEnumeration<SearchResult> searchResults = ldapContext.search(authenticationHandlerDTO.getConfigurationData().getRootDN(), 
						filterString, searchControls);
				
				while(searchResults.hasMore()) {
					if(userDTOs.size() >= MAX_RECORD) {
						hasMore = true;
						break;
					}
					
					UserDTO userDTO = new UserDTO();
					SearchResult searchResult = (SearchResult)searchResults.next();
					Attributes attributes = searchResult.getAttributes();
					
					if(attributeMap.containsKey("username"))
						userDTO.setUsername(attributes.get(attributeMap.get("username")) == null ? "" : (String)attributes.get(attributeMap.get("username")).get());
					if(attributeMap.containsKey("firstName"))
						userDTO.setFirstName(attributes.get(attributeMap.get("firstName")) == null ? "" : (String)attributes.get(attributeMap.get("firstName")).get());
					if(attributeMap.containsKey("lastName"))
						userDTO.setLastName(attributes.get(attributeMap.get("lastName")) == null ? "" : (String)attributes.get(attributeMap.get("lastName")).get());
					
					logger.debug("Username: " + userDTO.getUsername() + "; FirstName: " + userDTO.getFirstName() + "; LastName: " + userDTO.getLastName());
					
					if(registeredUsers.contains(userDTO.getUsername().toLowerCase())) {
						continue;
					}
					
					userDTOs.add(userDTO);
				}
				
				if(userDTOs.size() > 0) {
					if(hasMore) {
						response.setStatusCode(MessageUtil.getMessage("max.record.found.code"));
						response.setMessage(MessageUtil.getMessage("max.record.found", Integer.toString(MAX_RECORD)));
					} else {
						response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
						response.setMessage(MessageUtil.getMessage("success.data.found"));
					}
				} else {
					response.setStatusCode(MessageUtil.getMessage("no.ldap.user.found.code"));
					response.setMessage(MessageUtil.getMessage("no.ldap.user.found"));
				}
				
				response.setData(userDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("failed.ldap.connection.code"));
				response.setMessage(MessageUtil.getMessage("failed.ldap.connection"));
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("failed.ldap.connection.code"));
			response.setMessage(MessageUtil.getMessage("failed.ldap.connection"));
		} finally {
			if(ldapContext != null) {
				try {
					ldapContext.close();
				} catch(NamingException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
	
	private LdapContext getLdapContext(AuthenticationHandlerDTO authenticationHandlerDTO) 
			throws Exception {
		LdapContext ldapContext = null;
		
		try {
			if(authenticationHandlerDTO != null) {
				Hashtable<String, String> env = new Hashtable<String, String>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				
				if(authenticationHandlerDTO.getConfigurationData() != null) {
					String securityPrincipal = "CN=" + authenticationHandlerDTO.getConfigurationData().getUsername() 
													 + ",ou=Users," 
													 + authenticationHandlerDTO.getConfigurationData().getRootDN();
					
					env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
					env.put(Context.SECURITY_CREDENTIALS, authenticationHandlerDTO.getConfigurationData().getPassword());
					env.put(Context.PROVIDER_URL, authenticationHandlerDTO.getConfigurationData().getUrl());
				}
				
				ldapContext = new InitialLdapContext(env, null);
			}
		} catch(Exception err) {
			throw err;
		}
		
		return ldapContext;
	}
	
	private Set<String> getLdapUserAttributes(LdapContext ldapContext, LDAPConfigurationDataDTO configurationDataDTO) 
			throws Exception {
		Set<String> attributes = new LinkedHashSet<String>();
		NamingEnumeration<?> usersEnum = null;
		int count = 0;
		
		if(ldapContext != null && configurationDataDTO != null) {
			try {
				SearchControls constraints = new SearchControls();
				constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
				constraints.setTimeLimit(30000);
				
				usersEnum = ldapContext.search(configurationDataDTO.getRootDN(), "(objectclass=user)", constraints);
				
				while(usersEnum.hasMore()) {
					SearchResult result = (SearchResult) usersEnum.next();
					count++;
					Attributes attrs = result.getAttributes();
					NamingEnumeration<String> attributesEnum = attrs.getIDs();
					
					while(attributesEnum.hasMore()) {
						String attrName = attributesEnum.next();
						attributes.add(attrName);
					}
					
					attributesEnum.close();
					
					if(count > 0) {
						break;
					}
				}
			} catch(Exception err) {
				throw err;
			} finally {
				if(usersEnum != null) {
					try {
						usersEnum.close();
					} catch(NamingException err) {
						// Ignore
					}
				} 
			}
		}
		
		return attributes;
	}
	
	private SearchControls getSimpleSearchControls(Set<MappingDTO> attributeMappings) {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		
		if(attributeMappings != null) {
			Set<String> attributeIdList = new HashSet<String>();
			String[] attributeIds = new String[attributeMappings.size()];
			for(MappingDTO attribute : attributeMappings) {
				if(StringUtils.isNotBlank(attribute.getFrom())) {
					attributeIdList.add(attribute.getFrom());
				}
			}
			
			attributeIds = attributeIdList.toArray(attributeIds);
			searchControls.setReturningAttributes(attributeIds);
		}
		
		return searchControls;
	}
	
	/**
	 * MappingDTO is storing LDAP attribute mapped to user attribute
	 * @param userAttributeMapping User attribute mapping which configured in Authentication Handler screen
	 * @return Map of local user attribute name to LDAP attribute name
	 */
	private Map<String, String> getAttributeMap(Set<MappingDTO> userAttributeMapping) {
		Map<String, String> attributeMap = new HashMap<String, String>();
		
		if(userAttributeMapping != null) {
			for(MappingDTO mappingDTO : userAttributeMapping) {
				if(StringUtils.isNotBlank(mappingDTO.getFrom())) {
					attributeMap.put(mappingDTO.getTo(), mappingDTO.getFrom());
				}
			}
		}
		
		return attributeMap;
	}
	
	private String getFilterString(AuthenticationHandlerDTO authenticationHandlerDTO) {
		if(authenticationHandlerDTO.getConfigurationData() != null) {
			String filter = authenticationHandlerDTO.getFilter();
			if(StringUtils.isNotBlank(filter)) {
				if(filter.indexOf('*') == -1) {
					filter = "*" + filter + "*";
				}
				
				StringBuilder userFilter = new StringBuilder();
				
				if(authenticationHandlerDTO.getUserAttributeMapping() != null
						&& authenticationHandlerDTO.getUserAttributeMapping().size() > 0) {
					userFilter.append("(|");
					
					for(MappingDTO mappingDTO : authenticationHandlerDTO.getUserAttributeMapping()) {
						if(StringUtils.isNotBlank(mappingDTO.getFrom())) {
							userFilter.append("(")
									  .append(mappingDTO.getFrom())
									  .append("=")
									  .append(filter)
									  .append(")");
						}
					}
					userFilter.append(")");
				}
				
				if(StringUtils.isNotBlank(authenticationHandlerDTO.getConfigurationData().getUserSearchBase())) {
					String userSearchBase = authenticationHandlerDTO.getConfigurationData().getUserSearchBase();
					
					return userSearchBase.substring(0, userSearchBase.length()-1) + userFilter.toString() + ")";
				} else {
					return userFilter.toString();
				}
			} else {
				return authenticationHandlerDTO.getConfigurationData().getUserSearchBase();
			}
		}
		
		return StringUtils.EMPTY;
	}
	
	private Set<String> getRegisteredUsers() {
		Session session = null;
		Transaction transaction = null;
		Set<String> registeredUsers = new HashSet<String>();
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();

			List<User> users = (new UserManager(getSessionFactory(), session)).getUsers();
			transaction.commit();
			
			if(users != null) {
				for(User user : users) {
					registeredUsers.add(user.getUsername().toLowerCase());
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
		
		return registeredUsers;
	}
}
