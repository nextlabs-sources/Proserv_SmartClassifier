package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.dao.PageInfo;

public class RetrievalResponse 
		extends ServiceResponse {
	
	@Expose
	private Object data;
	@Expose
	private int pageNo;
	@Expose
	private int pageSize;
	@Expose
	private long totalNoOfRecords;
	@Expose
	private Long totalDocumentRecords;
	
	public RetrievalResponse() {
		super();
		this.data = null;
		this.pageNo = 1;
		this.pageSize = 10;
		this.totalNoOfRecords = 0;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public int getPageNo() {
		return pageNo;
	}
	
	public void setPageNo(int pageNumber) {
		this.pageNo = pageNumber;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public long getTotalNoOfRecords() {
		return totalNoOfRecords;
	}
	
	public void setTotalNoOfRecords(long totalNumberOfRecords) {
		this.totalNoOfRecords = totalNumberOfRecords;
	}
	
	public void setPageInfo(PageInfo pageInfo) {
		if(pageInfo != null) {
			this.pageNo = pageInfo.getPageNumber();
			this.pageSize = pageInfo.getSize();
		}
	}
	
	public Long getTotalDocumentRecords() {
		return totalDocumentRecords;
	}
	
	public void setTotalDocumentRecords(Long totalDocumentRecords) {
		this.totalDocumentRecords = totalDocumentRecords;
	}
}
