package com.nextlabs.smartclassifier.dto.v1;

import static com.nextlabs.smartclassifier.constant.Punctuation.BACK_SLASH;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.ExcludeRepoFolder;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ExcludeRepoFolderDTO extends BaseDTO {

  @Expose private Integer displayOrder;
  @Expose private String path;

  public ExcludeRepoFolderDTO() {
    super();
  }

  public ExcludeRepoFolderDTO(ExcludeRepoFolder excludeRepoFolder) {
    super();
    copy(excludeRepoFolder);
  }

  public void copy(ExcludeRepoFolder excludeRepoFolder) {
    if (excludeRepoFolder != null) {
      this.id = excludeRepoFolder.getId();
      this.displayOrder = excludeRepoFolder.getDisplayOrder();
      this.path = excludeRepoFolder.getPath();
    }
  }

  public ExcludeRepoFolder getEntity() {
    ExcludeRepoFolder excludeRepoFolder = new ExcludeRepoFolder();

    excludeRepoFolder.setId(this.id);
    excludeRepoFolder.setDisplayOrder(this.displayOrder);
    excludeRepoFolder.setPath(this.path.endsWith(BACK_SLASH) ? this.path : this.path + BACK_SLASH);

    return excludeRepoFolder;
  }

  public ExcludeRepoFolder getEntity(RepositoryType repoType) {
    ExcludeRepoFolder excludeRepoFolder = new ExcludeRepoFolder();

    excludeRepoFolder.setId(this.id);
    excludeRepoFolder.setDisplayOrder(this.displayOrder);
    if (repoType == RepositoryType.SHARED_FOLDER) {
      excludeRepoFolder.setPath(
          this.path.endsWith(BACK_SLASH) ? this.path : this.path + BACK_SLASH);
    } else {
      excludeRepoFolder.setPath(this.path);
    }

    return excludeRepoFolder;
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
}
