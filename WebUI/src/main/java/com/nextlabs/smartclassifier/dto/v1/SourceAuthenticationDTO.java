package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

public class SourceAuthenticationDTO extends BaseDTO {

  @Expose private String name;

  @Expose private String username;

  @Expose private String password;

  @Expose private String domain;

  public SourceAuthenticationDTO() {
    super();
  }

  public SourceAuthenticationDTO(SourceAuthentication sa) {
    super();
    copy(sa);
  }

  public void copy(SourceAuthentication sa) {
    if (sa != null) {
      this.id = sa.getId();
      this.name = sa.getName();
      this.username = sa.getUsername();
      this.password = NxlCryptoUtil.decrypt(sa.getPassword());
      this.domain = sa.getDomainName();

      this.createdTimestamp = sa.getCreatedOn();
      this.createdOn = sa.getCreatedOn().getTime();
      this.modifiedTimestamp = sa.getModifiedOn();
      this.modifiedOn = sa.getModifiedOn().getTime();
    }
  }

  public SourceAuthentication getEntity() {
    SourceAuthentication sa = new SourceAuthentication();

    sa.setId(this.id);
    sa.setName(this.name);
    sa.setDomainName(this.domain);
    sa.setUsername(this.username);
    sa.setPassword(NxlCryptoUtil.encrypt(this.password));

    if (this.createdOn != null && this.createdOn > 0) {
      sa.setCreatedOn(new Date(this.createdOn));
    }

    if (this.modifiedOn != null && this.modifiedOn > 0) {
      sa.setModifiedOn(new Date(this.modifiedOn));
    }
    return sa;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
}
