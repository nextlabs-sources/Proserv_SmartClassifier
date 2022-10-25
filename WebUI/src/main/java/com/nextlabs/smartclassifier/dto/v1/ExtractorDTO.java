package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DocumentSizeLimit;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.database.entity.Extractor;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ExtractorDTO 
		extends BaseDTO {
	
	@Expose
	private String name;
	@Expose
	private String hostname;
	@Expose
	private JMSProfileDTO JMSProfile;
	@Expose
	private Integer documentExtractorCount;
	@Expose
	private Integer minimumHeapMemory;
	@Expose
	private Long configLoadedOn;
	@Expose
	private Integer configReloadInterval;
	@Expose
	private List<DocumentSizeLimitDTO> documentSizeLimits;
	@Expose
	private List<ExecutionWindowSetDTO> executionWindowSets;
	@Expose
	private String status;
	
	public ExtractorDTO() {
		super();
	}
	
	public ExtractorDTO(Extractor extractor) {
		super();
		copy(extractor);
	}
	
	public void copy(Extractor extractor) {
		if(extractor != null) {
			this.id = extractor.getId();
			this.name = extractor.getName();
			this.hostname = extractor.getHostname();
			this.JMSProfile = new JMSProfileDTO(extractor.getJMSProfile());
			this.documentExtractorCount = extractor.getDocumentExtractorCount();
			this.minimumHeapMemory = extractor.getMinHeapMemory();
			this.configReloadInterval = extractor.getConfigReloadInterval();
			
			if(extractor.getConfigLoadedOn() != null) {
				this.configLoadedOn = extractor.getConfigLoadedOn().getTime();
			}
			
			if(extractor.getDocumentSizeLimits() != null && extractor.getDocumentSizeLimits().size() > 0) {
				this.documentSizeLimits = new ArrayList<DocumentSizeLimitDTO>();
				for(DocumentSizeLimit documentSizeLimit : extractor.getDocumentSizeLimits()) {
					this.documentSizeLimits.add(new DocumentSizeLimitDTO(documentSizeLimit));
				}
			}
			
			if(extractor.getExecutionWindowSets() != null && extractor.getExecutionWindowSets().size() > 0) {
				this.executionWindowSets = new ArrayList<ExecutionWindowSetDTO>();
				for(ExecutionWindowSet executionWindowSet : extractor.getExecutionWindowSets()) {
					this.executionWindowSets.add(new ExecutionWindowSetDTO(executionWindowSet));
				}
			}
			
			this.createdTimestamp = extractor.getCreatedOn();
			this.createdOn = extractor.getCreatedOn().getTime();
			this.modifiedTimestamp = extractor.getModifiedOn();
			this.modifiedOn = extractor.getModifiedOn().getTime();
		}
	}
	
	public Extractor getEntity() {
		Extractor extractor = new Extractor();
		
		extractor.setId(this.id);
		extractor.setName(this.name);
		extractor.setHostname(this.hostname);
		
		if(this.JMSProfile != null) {
			extractor.setJMSProfile(this.JMSProfile.getEntity());
		}
		
		extractor.setDocumentExtractorCount(this.documentExtractorCount);
		extractor.setMinHeapMemory(this.minimumHeapMemory);
		if(this.configLoadedOn != null) {
			extractor.setConfigLoadedOn(new Date(this.configLoadedOn));
		}
		extractor.setConfigReloadInterval(this.configReloadInterval);
		
		if(this.documentSizeLimits != null && this.documentSizeLimits.size() > 0) {
			for(DocumentSizeLimitDTO documentSizeLimitDTO : this.documentSizeLimits) {
				extractor.getDocumentSizeLimits().add(documentSizeLimitDTO.getEntity());
			}
		}
		
		if(this.executionWindowSets != null && this.executionWindowSets.size() > 0) {
			for(ExecutionWindowSetDTO executionWindowSetDTO : this.executionWindowSets) {
				extractor.getExecutionWindowSets().add(executionWindowSetDTO.getEntity());
			}
		}
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			extractor.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			extractor.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return extractor;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public JMSProfileDTO getJMSProfile() {
		return JMSProfile;
	}
	
	public void setJMSProfile(JMSProfileDTO jMSProfile) {
		JMSProfile = jMSProfile;
	}
	
	public Integer getDocumentExtractorCount() {
		return documentExtractorCount;
	}
	
	public void setDocumentExtractorCount(Integer documentExtractorCount) {
		this.documentExtractorCount = documentExtractorCount;
	}
	
	public Integer getMinimumHeapMemory() {
		return minimumHeapMemory;
	}
	
	public void setMinimumHeapMemory(Integer minimumHeapMemory) {
		this.minimumHeapMemory = minimumHeapMemory;
	}
	
	public Long getConfigLoadedOn() {
		return configLoadedOn;
	}
	
	public void setConfigLoadedOn(Long configLoadedOn) {
		this.configLoadedOn = configLoadedOn;
	}
	
	public Integer getConfigReloadInterval() {
		return configReloadInterval;
	}
	
	public void setConfigReloadInterval(Integer configReloadInterval) {
		this.configReloadInterval = configReloadInterval;
	}
	
	public List<DocumentSizeLimitDTO> getDocumentSizeLimits() {
		return documentSizeLimits;
	}
	
	public void setDocumentSizeLimits(List<DocumentSizeLimitDTO> documentSizeLimits) {
		this.documentSizeLimits = documentSizeLimits;
	}
	
	public List<ExecutionWindowSetDTO> getExecutionWindowSets() {
		return executionWindowSets;
	}
	
	public void setExecutionWindowSets(
			List<ExecutionWindowSetDTO> executionWindowSets) {
		this.executionWindowSets = executionWindowSets;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
