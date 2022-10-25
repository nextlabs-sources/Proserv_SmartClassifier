package com.nextlabs.smartclassifier.database.dao;

public class PageInfo {
	
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";
	
	public static int SIZE = 10;
	public static int SKIP = 0;
	public static int PAGE = 1;
	public static String ORDER_BY = "id";
	public static String ORDER_DIRECTION = "ASC";
	
	private Integer size = SIZE;
	private Integer skip = SKIP;
	private Integer page = 1;
	private String orderBy = ORDER_BY;
	private String orderDirection = ORDER_DIRECTION;
	
	public Integer getSize() {
		if (this.size == null || this.size.intValue() <= 0) {
			return SIZE;
		}
		return size;
	}
	
	public void setSize(Integer size) {
		if (size == null || size.intValue() <= 0) {
			this.size = SIZE;
		}
		this.size = size;
	}
	
	public Integer getSkip() {
		if (this.skip == null || this.skip.intValue() < 0) {
			return SKIP;
		}
		return skip;
	}
	
	public void setSkip(Integer skip) {
		if (skip == null || skip.intValue() < 0) {
			this.skip = SKIP;
		}
		this.skip = skip;
	}
	
	public Integer getPageNumber() {
		if(this.page == null || this.page.intValue() <= 0) {
			return PAGE;
		}
		return page;
	}
	
	public void setPageNumber(Integer page) {
		if(page == null || page.intValue() <= 0) {
			this.page = PAGE;
		}
		this.page = page;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public String getOrderDirection() {
		return orderDirection;
	}
	
	public void setOrderBy(String orderBy) {
		if (!orderBy.equalsIgnoreCase(ASC) && !(orderBy.equalsIgnoreCase(DESC))) {
			this.orderBy = ORDER_BY;
		}
		this.orderBy = orderBy;
	}
	
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
		result = prime * result + ((orderDirection == null) ? 0 : orderDirection.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((skip == null) ? 0 : skip.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		PageInfo other = (PageInfo) obj;
		if (orderBy == null) {
			if (other.orderBy != null) {
				return false;
			}
		} else if (!orderBy.equals(other.orderBy)) {
			return false;
		}
		
		if (orderDirection == null) {
			if (other.orderDirection != null) {
				return false;
			}
		} else if (!orderDirection.equals(other.orderDirection)) {
			return false;
		}
		
		if (size == null) {
			if (other.size != null) {
				return false;
			}
		} else if (!size.equals(other.size)) {
			return false;
		}
		
		if (skip == null) {
			if (other.skip != null) {
				return false;
			}
		} else if (!skip.equals(other.skip)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageInfo [orderBy=");
		builder.append(orderBy);
		builder.append(", orderDirection=");
		builder.append(orderDirection);
		builder.append(", size=");
		builder.append(size);
		builder.append(", skip=");
		builder.append(skip);
		builder.append("]");
		return builder.toString();
	}
}
