package com.nextlabs.smartclassifier.database.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(
  name = "REPO_FOLDERS",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"PATH"})}
)
public class RepoFolder {

  // Field name for search criteria construction
  public static final String ID = "id";
  public static final String WATCHER_ID = "watcher.id";
  public static final String DISPLAY_ORDER = "displayOrder";
  public static final String PATH = "path";
  public static final String EXCLUDED_REPO_FOLDERS = "excludeRepoFolders";
  public static final String REPOSITORY_TYPE = "repositoryType";
  public static final String SOURCE_AUTHENTICATION = "sourceAuthentication";
  public static final String SOURCE_AUTHENTICATION_ID = "sourceAuthentication.id";

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "REPO_FOLDERS_SEQ")}
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "WATCHER_ID")
  private Watcher watcher;

  @Column(name = "DISPLAY_ORDER", nullable = false)
  private Integer displayOrder;

  @Column(name = "PATH", nullable = false, length = 1024)
  private String path;

  @Column(name = "REPOSITORY_TYPE", nullable = false, length = 30)
  private String repositoryType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "SOURCE_AUTHENTICATION_ID")
  @NotFound(action = NotFoundAction.IGNORE)
  private SourceAuthentication sourceAuthentication;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "repoFolder", cascade = CascadeType.ALL)
  @OrderBy("displayOrder ASC")
  @NotFound(action = NotFoundAction.IGNORE)
  private Set<ExcludeRepoFolder> excludeRepoFolders = new LinkedHashSet<ExcludeRepoFolder>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Watcher getWatcher() {
    return watcher;
  }

  public void setWatcher(Watcher watcher) {
    this.watcher = watcher;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getRepositoryType() {
    return repositoryType;
  }

  public void setRepositoryType(String repositoryType) {
    this.repositoryType = repositoryType;
  }

  public SourceAuthentication getSourceAuthentication() {
    return sourceAuthentication;
  }

  public void setSourceAuthentication(SourceAuthentication sourceAuthentication) {
    this.sourceAuthentication = sourceAuthentication;
  }

  public Set<ExcludeRepoFolder> getExcludeRepoFolders() {
    return excludeRepoFolders;
  }

  public void setExcludeRepoFolders(Set<ExcludeRepoFolder> excludeRepoFolders) {
    this.excludeRepoFolders = excludeRepoFolders;
  }
}
