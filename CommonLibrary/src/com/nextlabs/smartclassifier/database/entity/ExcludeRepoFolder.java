package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "EXCLUDE_REPO_FOLDERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"PATH"})})
public class ExcludeRepoFolder {
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "EXCLUDE_REPO_FOLDERS_SEQ")})
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPO_FOLDER_ID")
	private RepoFolder repoFolder;
	
	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "PATH", nullable = false, length = 1024)
	private String path;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public RepoFolder getRepoFolder() {
		return repoFolder;
	}
	
	public void setRepoFolder(RepoFolder repoFolder) {
		this.repoFolder = repoFolder;
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
