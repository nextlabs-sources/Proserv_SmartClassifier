package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(
  name = "JMS_PROFILES",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"DISPLAY_NAME", "TYPE"}),
    @UniqueConstraint(
      columnNames = {
        "TYPE",
        "PROVIDER_URL",
        "INITIAL_CONTEXT_FACTORY",
        "SERVICE_NAME"
      }
    )
  }
)
public class JMSProfile {

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "JMS_PROFILES_SEQ")}
  )
  private Long id;

  @Column(name = "DISPLAY_NAME", nullable = false, length = 320)
  private String displayName;

  @Column(name = "DESCRIPTION", nullable = true, length = 1024)
  private String description;

  @Column(name = "TYPE", nullable = false, length = 1)
  private String type;

  @Column(name = "PROVIDER_URL", nullable = false, length = 255)
  private String providerURL;

  @Column(name = "INITIAL_CONTEXT_FACTORY", nullable = false, length = 255)
  private String initialContextFactory;

  @Column(name = "SERVICE_NAME", nullable = false, length = 255)
  private String serviceName;

  @Column(name = "CONNECTION_RETRY_INTERVAL", nullable = false)
  private Long connectionRetryInterval;

  @Column(name = "USERNAME", length = 255)
  private String username;
  
  @Column(name = "PASSWORD", length = 1024)
  private String password;
  
  @Column(name = "CREATED_ON", nullable = false)
  private Date createdOn;

  @Version
  @Column(name = "MODIFIED_ON", nullable = true)
  private Date modifiedOn;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "JMSProfile", cascade = CascadeType.ALL)
  @NotFound(action = NotFoundAction.IGNORE)
  private Set<Watcher> fileWatchers = new HashSet<Watcher>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getProviderURL() {
    return providerURL;
  }

  public void setProviderURL(String providerURL) {
    this.providerURL = providerURL;
  }

  public String getInitialContextFactory() {
    return initialContextFactory;
  }

  public void setInitialContextFactory(String initialContextFactory) {
    this.initialContextFactory = initialContextFactory;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Long getConnectionRetryInterval() {
    return connectionRetryInterval;
  }

  public void setConnectionRetryInterval(Long connectionRetryInterval) {
    this.connectionRetryInterval = connectionRetryInterval;
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

  public Set<Watcher> getFileWatchers() {
    return fileWatchers;
  }

  public void setFileWatchers(Set<Watcher> fileWatchers) {
    this.fileWatchers = fileWatchers;
  }
}
