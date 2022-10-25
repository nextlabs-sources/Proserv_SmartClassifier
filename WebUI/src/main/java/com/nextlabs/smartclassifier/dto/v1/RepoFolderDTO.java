package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.ExcludeRepoFolder;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.dto.BaseDTO;

import java.util.ArrayList;
import java.util.List;

import static com.nextlabs.smartclassifier.constant.Punctuation.BACK_SLASH;

public class RepoFolderDTO extends BaseDTO {

    @Expose
    private SourceAuthenticationDTO sourceAuthentication;
    @Expose
    private String repositoryType;
    @Expose
    private Integer displayOrder;
    @Expose
    private String path;
    @Expose
    private List<ExcludeRepoFolderDTO> excludeRepositoryFolders;

    public RepoFolderDTO() {
        super();
    }

    public RepoFolderDTO(RepoFolder repoFolder) {
        super();
        copy(repoFolder);
    }

    public void copy(RepoFolder repoFolder) {
        if (repoFolder != null) {
            this.id = repoFolder.getId();
            this.repositoryType = repoFolder.getRepositoryType();
            this.sourceAuthentication = new SourceAuthenticationDTO(repoFolder.getSourceAuthentication());
            this.displayOrder = repoFolder.getDisplayOrder();
            this.path = repoFolder.getPath();

            if (repoFolder.getExcludeRepoFolders() != null
                    && repoFolder.getExcludeRepoFolders().size() > 0) {
                this.excludeRepositoryFolders = new ArrayList<ExcludeRepoFolderDTO>();
                for (ExcludeRepoFolder excludeRepoFolder : repoFolder.getExcludeRepoFolders()) {
                    this.excludeRepositoryFolders.add(new ExcludeRepoFolderDTO(excludeRepoFolder));
                }
            }
        }
    }

    public RepoFolder getEntity() {
        RepoFolder repoFolder = new RepoFolder();

        repoFolder.setId(this.id);
        repoFolder.setDisplayOrder(this.displayOrder);

        RepositoryType repositoryType = RepositoryType.getRepositoryType(this.repositoryType);
        if (repositoryType == RepositoryType.SHARED_FOLDER) {
            repoFolder.setPath(this.path.endsWith(BACK_SLASH) ? this.path : this.path + BACK_SLASH);
        } else {
            repoFolder.setPath(this.path);
        }
        repoFolder.setRepositoryType(this.repositoryType);

        repoFolder.setSourceAuthentication(this.sourceAuthentication.getEntity());

        RepositoryType repoType = RepositoryType.getRepositoryType(this.repositoryType);
        if (this.excludeRepositoryFolders != null && this.excludeRepositoryFolders.size() > 0) {
            for (ExcludeRepoFolderDTO excludeRepoFolderDTO : this.excludeRepositoryFolders) {
                repoFolder.getExcludeRepoFolders().add(excludeRepoFolderDTO.getEntity(repoType));
            }
        }

        return repoFolder;
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

    public List<ExcludeRepoFolderDTO> getExcludeRepositoryFolders() {
        return excludeRepositoryFolders;
    }

    public void setExcludeRepositoryFolders(List<ExcludeRepoFolderDTO> excludeRepositoryFolders) {
        this.excludeRepositoryFolders = excludeRepositoryFolders;
    }

    public SourceAuthenticationDTO getSourceAuthentication() {
        return sourceAuthentication;
    }

    public void setSourceAuthentication(SourceAuthenticationDTO sourceAuthentication) {
        this.sourceAuthentication = sourceAuthentication;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }
}
