package com.nextlabs.smartclassifier.service.v1.sharedfolder;

import com.nextlabs.smartclassifier.dto.v1.FolderDTO;
import com.nextlabs.smartclassifier.dto.v1.RepoAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.util.SMBUtil;
import jcifs.smb.SmbFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class SharedFolderRetrievalService 
		extends Service {
	
	public SharedFolderRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public SharedFolderRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getSharedFolders(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();

		try {
			// Get domain and hostname from request
			String domainName = "qapf1.qalab01.nextlabs.com";
			String hostname = "\\\\SCDEV01W12R2\\";
			
			// Retrieve repository host authentication information from session
			Object repoAuth = ((HttpServletRequest)servletRequest).getSession().getAttribute("repoAuth");
			
			if(repoAuth != null) {
				// Domain Name -> Hostname -> RepoAuthenticationDTO
				@SuppressWarnings("unchecked")
				Map<String, Map<String, RepoAuthenticationDTO>> repoAuthenticationMap = (Map<String, Map<String, RepoAuthenticationDTO>>)repoAuth;
				Map<String, RepoAuthenticationDTO> hostsWithinDomain = repoAuthenticationMap.get(domainName);
				
				if(hostsWithinDomain != null) {
					RepoAuthenticationDTO repoAuthenticationDTO = hostsWithinDomain.get(hostname);
					
					if(repoAuthenticationDTO != null) {
						
						
						
						
						
					}
				}
			}
			
			response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
			response.setMessage(MessageUtil.getMessage("no.data.found"));
			
//			if(repoUsers != null && repoUsers.size() > 0) {
//				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
//				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
//				
//				List<RepoHostDTO> repoHostDTOs = new ArrayList<RepoHostDTO>();
//				
//				for(RepoUser repoUser : repoUsers) {
//					if(repoUser.getRepoHosts() != null
//							&& repoUser.getRepoHosts().size() > 0) {
//						for(RepoHost repoHost : repoUser.getRepoHosts()) {
//							NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(repoHost.getDomain(), 
//																									   repoUser.getUsername(), 
//																									   NxlCryptoUtil.decrypt(repoUser.getPassword()));
//							RepoHostDTO repoHostDTO = new RepoHostDTO(repoHost);
//							String repoHostname = SMBUtil.convert2SMBFormat(repoHostDTO.getHostname());
//							SmbFile hostEntry = new SmbFile(repoHostname, authentication);
//							
//							repoHostDTO.setSharedFolders(getSharedFolders(hostEntry));
//							repoHostDTOs.add(repoHostDTO);
//						}
//					}
//				}
//				
//				response.setData(repoHostDTOs);
//				response.setTotalNoOfRecords(repoHostDTOs.size());
//			} else {
//				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
//				response.setMessage(MessageUtil.getMessage("no.data.found"));
//			}
//			
//			response.setPageInfo(getPageInfo(retrievalRequest));
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		}
		
		return response;
	}
	
	private Set<FolderDTO> getSharedFolders(SmbFile smbFile) {
		Set<FolderDTO> folders = new LinkedHashSet<FolderDTO>();
		
		try {
			if(smbFile != null) {
				for(SmbFile file : smbFile.listFiles()) {
					if(!file.getShare().endsWith("$")
							&& file.isDirectory()) {
						FolderDTO folderDTO = new FolderDTO(SMBUtil.convert2FilePath(file.getPath()), 
																					 file.getName().replaceAll(FORWARD_SLASH, EMPTY));
						folderDTO.setSubFolders(getSharedFolders(file));
						
						folders.add(folderDTO);
					}
				}
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
		
		return folders;
	}
}
