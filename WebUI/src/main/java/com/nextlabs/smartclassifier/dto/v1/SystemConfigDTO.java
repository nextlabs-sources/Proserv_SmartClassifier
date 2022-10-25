package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.database.entity.SystemConfig;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

public class SystemConfigDTO 
		extends BaseDTO {
	
	@Expose
	private String label;
	@Expose
	private String identifier;
	@Expose
	private String value;
	@Expose
	private String description;
	@Expose
	private Boolean encrypt;
	
	public SystemConfigDTO() {
		super();
	}
	
	public SystemConfigDTO(SystemConfig systemConfig) {
		super();
		copy(systemConfig);
	}
	
	public void copy(SystemConfig systemConfig) {
		if(systemConfig != null) {
			this.id = systemConfig.getId();
			this.label = systemConfig.getLabel();
			this.identifier = systemConfig.getIdentifier();
			
			if(systemConfig.getValue().startsWith(SCConstant.ENCRYPTED_PREFIX)
					&& systemConfig.getValue().endsWith(SCConstant.ENCRYPTED_SUFFIX)) {
				this.encrypt = true;
				this.value = NxlCryptoUtil.decrypt(systemConfig.getValue());
			} else {
				this.encrypt = false;
				this.value = systemConfig.getValue();
			}
			
			this.description = systemConfig.getDescription();
			this.createdTimestamp = systemConfig.getCreatedOn();
			this.createdOn = systemConfig.getCreatedOn().getTime();
			this.modifiedTimestamp = systemConfig.getModifiedOn();
			this.modifiedOn = systemConfig.getModifiedOn().getTime();
		}
	}
	
	public SystemConfig getEntity() {
		SystemConfig systemConfig = new SystemConfig();
		
		systemConfig.setId(this.id);
		systemConfig.setLabel(this.label);
		systemConfig.setIdentifier(this.identifier);
		if(this.encrypt) {
			systemConfig.setValue(NxlCryptoUtil.encrypt(this.value, true));
		} else {
			systemConfig.setValue(this.value);
		}
		systemConfig.setDescription(this.description);
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			systemConfig.setCreatedOn(new Date(createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			systemConfig.setModifiedOn(new Date(modifiedOn));
		}
		
		return systemConfig;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Boolean isEncrypt() {
		return encrypt;
	}
	
	public void isEncrypt(Boolean encrypt) {
		this.encrypt = encrypt;
	}
}
