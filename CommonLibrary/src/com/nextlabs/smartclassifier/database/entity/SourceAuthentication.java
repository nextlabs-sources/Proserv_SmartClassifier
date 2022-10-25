package com.nextlabs.smartclassifier.database.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Table(
        name = "SOURCE_AUTHENTICATION",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"NAME"}),
                @UniqueConstraint(columnNames = {"DOMAIN_NAME", "USERNAME"})
        }
)
public class SourceAuthentication {
    // Field name for search criteria construction
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DOMAIN_NAME = "domainName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
    @GenericGenerator(
            name = "SequenceGeneratorYYMMDDHH",
            strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
            parameters = {@Parameter(name = "sequence", value = "SOURCE_AUTHENTICATION_SEQ")}
    )
    private Long id;

    @Column(name = "NAME", nullable = false, length = 256)
    private String name;

    @Column(name = "DOMAIN_NAME", nullable = false, length = 100)
    private String domainName;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "PASSWORD", length = 256)
    private String password;

    @Column(name = "CREATED_ON", nullable = false)
    private Date createdOn;

    @Version
    @Column(name = "MODIFIED_ON", nullable = true)
    private Date modifiedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String toString() {
        return "[" + id + ", " + domainName + ", " + username + ", " + password + "]";
    }
}
