package com.nextlabs.smartclassifier.dto;

import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;

/**
 * Created by pkalra on 6/7/2017.
 */
public class SourceAuthenticationDTO {
    private String domain;
    private String userName;
    private String password;

    public SourceAuthenticationDTO(SourceAuthentication sa) {
        this.domain = sa.getDomainName();
        this.userName = sa.getUsername();
        this.password = sa.getPassword();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "[" + domain + "," + userName + "," + password + "]";
    }
}
