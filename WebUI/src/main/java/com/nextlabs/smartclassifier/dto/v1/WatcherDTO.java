package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DocumentTypeAssociation;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class WatcherDTO extends BaseDTO {

  @Expose private String name;
  @Expose private String hostname;
  @Expose private JMSProfileDTO JMSProfile;
  @Expose private Integer fileMonitorCount;
  @Expose private Long configLoadedOn;
  @Expose private Integer configReloadInterval;
  @Expose private List<RepoFolderDTO> repositoryFolders;
  @Expose private List<DocumentTypeAssociationDTO> documentTypeAssociations;
  @Expose private String status;

  public WatcherDTO() {
    super();
  }

  public WatcherDTO(Watcher watcher) {
    super();
    copy(watcher);
  }

  public void copy(Watcher watcher) {
    if (watcher != null) {
      this.id = watcher.getId();
      this.name = watcher.getName();
      this.hostname = watcher.getHostname();
      this.JMSProfile = new JMSProfileDTO(watcher.getJMSProfile());
      this.fileMonitorCount = watcher.getFileMonitorCount();
      this.configReloadInterval = watcher.getConfigReloadInterval();

      if (watcher.getConfigLoadedOn() != null) {
        this.configLoadedOn = watcher.getConfigLoadedOn().getTime();
      }

      if (watcher.getRepoFolders() != null && watcher.getRepoFolders().size() > 0) {
        this.repositoryFolders = new ArrayList<>();
        for (RepoFolder repoFolder : watcher.getRepoFolders()) {
          this.repositoryFolders.add(new RepoFolderDTO(repoFolder));
        }
      }

      if (watcher.getDocumentTypeAssociations() != null
          && watcher.getDocumentTypeAssociations().size() > 0) {
        this.documentTypeAssociations = new ArrayList<>();
        for (DocumentTypeAssociation documentTypeAssociation :
            watcher.getDocumentTypeAssociations()) {
          this.documentTypeAssociations.add(
              new DocumentTypeAssociationDTO(documentTypeAssociation));
        }
      }

      this.createdTimestamp = watcher.getCreatedOn();
      this.createdOn = watcher.getCreatedOn().getTime();
      this.modifiedTimestamp = watcher.getModifiedOn();
      this.modifiedOn = watcher.getModifiedOn().getTime();
    }
  }

  public Watcher getEntity() {
    Watcher watcher = new Watcher();

    watcher.setId(this.id);
    watcher.setName(this.name);
    watcher.setHostname(this.hostname);

    if (this.JMSProfile != null) {
      watcher.setJMSProfile(this.JMSProfile.getEntity());
    }

    watcher.setFileMonitorCount(this.fileMonitorCount);
    if (this.configLoadedOn != null) {
      watcher.setConfigLoadedOn(new Date(this.configLoadedOn));
    }
    watcher.setConfigReloadInterval(this.configReloadInterval);

    if (this.repositoryFolders != null && this.repositoryFolders.size() > 0) {
      for (RepoFolderDTO repositoryFolder : this.repositoryFolders) {
        watcher.getRepoFolders().add(repositoryFolder.getEntity());
      }
    }

    if (this.documentTypeAssociations != null && this.documentTypeAssociations.size() > 0) {
      for (DocumentTypeAssociationDTO documentTypeAssociationDTO : this.documentTypeAssociations) {
        watcher.getDocumentTypeAssociations().add(documentTypeAssociationDTO.getEntity());
      }
    }

    if (this.createdOn != null && this.createdOn > 0) {
      watcher.setCreatedOn(new Date(this.createdOn));
    }

    if (this.modifiedOn != null && this.modifiedOn > 0) {
      watcher.setModifiedOn(new Date(this.modifiedOn));
    }

    return watcher;
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

  public void setHostName(String hostname) {
    this.hostname = hostname;
  }

  public JMSProfileDTO getJMSProfile() {
    return JMSProfile;
  }

  public void setJMSProfile(JMSProfileDTO jMSProfile) {
    JMSProfile = jMSProfile;
  }

  public Integer getFileMonitorCount() {
    return fileMonitorCount;
  }

  public void setFileMonitorCount(Integer fileMonitorCount) {
    this.fileMonitorCount = fileMonitorCount;
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

  public List<RepoFolderDTO> getRepositoryFolders() {
    return repositoryFolders;
  }

  public void setRepositoryFolders(List<RepoFolderDTO> repositoryFolders) {
    this.repositoryFolders = repositoryFolders;
  }

  public List<DocumentTypeAssociationDTO> getDocumentTypeAssociations() {
    return documentTypeAssociations;
  }

  public void setDocumentTypeAssociations(
      List<DocumentTypeAssociationDTO> documentTypeAssociations) {
    this.documentTypeAssociations = documentTypeAssociations;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
