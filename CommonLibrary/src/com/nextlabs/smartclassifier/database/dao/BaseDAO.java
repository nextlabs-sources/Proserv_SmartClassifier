package com.nextlabs.smartclassifier.database.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;

@SuppressWarnings({"rawtypes"})
public interface BaseDAO<T> {
	
	String getTableName();
	
	T load(Serializable id);
	
	Object save(T entity);
	
	void saveOrUpdate(T entity);
	
	void saveOrUpdateAll(Collection<T> entities);
	
	void update(T entity);
	
	void delete(T entity);
	
	void delete(Serializable id);
	
	int deleteAll();
	
	T merge(T entity);
	
	void flush();
	
	void clear();
	
	void flushAndClear();
	
	T get(Serializable id);
	
	List<T> getAll();
	
	List<T> getPage(PageInfo pi);
	
	List<T> getAllByOrder(String orderBy, String orderDir);
	
	int getCount();
	
	long getCount(Criterion... criterion);
	
	long getCount(List<Criterion> criterion);
	
	long getCount(String sql);
	
	long getMax(String columnName);
	
	long getNextSequenceValue(String sequenceName);
	
	boolean exists(Serializable id);
	
	List<T> findByExample(T exampleInstance);
	
	List<T> findByExample(T exampleInstance, String[] excludeProperty);
	
	List<T> findByCriteria(Criterion... criterion);
	
	List<T> findByCriteria(Collection<Criterion> criterionList);
	
	List<T> findByCriteria(Collection<Criterion> criterionList, PageInfo pageInfo);
	
	List<T> findByCriteria(Collection<Criterion> criterionList, Collection<Order> orderList);
	
	List<T> findByCriteria(Collection<Criterion> criterionList, Collection<Order> orderList, int maxResult);
	
	List<T> findByCriteria(Collection<Criterion> criterionList, Collection<Order> orderList, PageInfo pageInfo);
	
	List findByCriteria(ProjectionList projectionList, Collection<Criterion> criterionList, Collection<Order> orderList, PageInfo pageInfo, Class... transformClass);
	
	List<T> findByCriteria(Map<String, String> alias, Collection<Criterion> criterionList, Collection<Order> orderList);
	
	List<T> findByCriteria(Map<String, String> alias, Collection<Criterion> criterionList, Collection<Order> orderList, PageInfo pageInfo);
	
	List findByCriteria(ProjectionList projectionList, Collection<Criterion> criterionList, Class... transformClass);
	
	List findByCriteria(ProjectionList projectionList, Collection<Criterion> criterionList, Collection<Order> orderList, Class... transformClass);
	
	List getProjection(Projection projection);
}
