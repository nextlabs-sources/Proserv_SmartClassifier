package com.nextlabs.smartclassifier.database.dao;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.transform.Transformers;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class HibernateDAO<T> implements BaseDAO<T> {

  protected final Logger logger;
  protected Class<T> persistentClass;
  protected SessionFactory sessionFactory;
  protected Session session;

  public HibernateDAO(SessionFactory sessionFactory, Session session) {
    this.sessionFactory = sessionFactory;
    this.session = session;
    this.persistentClass =
        (Class<T>)
            ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    logger = LogManager.getLogger(this.persistentClass);
  }

  @Override
  public String getTableName() {
    return persistentClass.getAnnotation(Table.class).name();
  }

  @Override
  public T load(Serializable id) {
    //		logger.debug("Loading " + persistentClass.getSimpleName()	+ " with id: " + id);
    return (T) getSession().load(persistentClass, id);
  }

  @Override
  public Object save(T entity) {
    //		logger.debug("Save: " + entity.toString());
    return getSession().save(entity);
  }

  @Override
  public void saveOrUpdate(T entity) {
    //		logger.debug("Save or Update: " + entity.toString());
    getSession().saveOrUpdate(entity);
  }

  @Override
  public void saveOrUpdateAll(Collection<T> entities) {
    if (entities != null) {
      Iterator<T> iterator = entities.iterator();
      while (iterator.hasNext()) {
        getSession().saveOrUpdate(iterator.next());
      }
    }
  }

  @Override
  public void update(T entity) {
    //		logger.debug("Update: " + entity.toString());
    getSession().update(entity);
  }

  @Override
  public void delete(T entity) {
    //		logger.debug("Delete: " + entity.toString());
    getSession().delete(entity);
  }

  @Override
  public void delete(Serializable id) {
    //		logger.debug("Delete with id: " + id);
    getSession().delete(getSession().load(persistentClass, id));
  }

  @Override
  public int deleteAll() {
    //		logger.debug("Delete all in table: " + getTableName());
    SQLQuery query = getSession().createSQLQuery("TRUNCATE TABLE " + getTableName());
    return query.executeUpdate();
  }

  @Override
  public T merge(T entity) {
    //		logger.debug("Merge: " + entity);
    return (T) getSession().merge(entity);
  }

  @Override
  public void flush() {
    //		logger.debug("Flushing cache");
    getSession().flush();
  }

  @Override
  public void clear() {
    //		logger.debug("Clearing cache");
    getSession().clear();
  }

  @Override
  public void flushAndClear() {
    flush();
    clear();
  }

  @Override
  public T get(Serializable id) {
    //		logger.debug("Retrieve " + getTableName() + " - " + id);
    return (T) getSession().get(persistentClass, id);
  }

  @Override
  public List<T> getAll() {
    //		logger.debug("Retrieve all " + getTableName());
    return getSession().createQuery("from " + persistentClass.getName()).list();
  }

  @Override
  public List<T> getPage(PageInfo page) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (page != null) {
      criteria.setMaxResults(page.getSize());
      criteria.setFirstResult(page.getSkip());

      if (page.getOrderDirection().equalsIgnoreCase(PageInfo.ASC)) {
        criteria.addOrder(Property.forName(page.getOrderBy()).asc());
      } else {
        criteria.addOrder(Property.forName(page.getOrderBy()).desc());
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> getAllByOrder(String orderBy, String orderDirection) {
    //		logger.debug("Retrieve all " + getTableName() + " by order: " + orderBy + ", " + orderDirection);

    Criteria criteria = getSession().createCriteria(persistentClass);

    if (orderDirection.equalsIgnoreCase(PageInfo.ASC)) {
      criteria.addOrder(Order.asc(orderBy));
    } else if (orderDirection.equalsIgnoreCase(PageInfo.DESC)) {
      criteria.addOrder(Order.desc(orderBy));
    } else {
      throw new IllegalArgumentException("Invalid sort order: " + orderDirection);
    }

    return criteria.list();
  }

  @Override
  public int getCount() {
    SQLQuery query = getSession().createSQLQuery("SELECT COUNT(*) FROM " + getTableName());
    return (Integer) query.uniqueResult();
  }

  @Override
  public long getCount(Criterion... criterion) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }
    criteria.setProjection(Projections.rowCount());

    return (Long) criteria.uniqueResult();
  }

  @Override
  public long getCount(List<Criterion> criterion) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }
    criteria.setProjection(Projections.rowCount());

    return (Long) criteria.uniqueResult();
  }

  @Override
  public long getCount(String sql) {
    SQLQuery query = getSession().createSQLQuery(sql);
    return (Long) query.uniqueResult();
  }

  @Override
  public long getMax(String columnName) {
    SQLQuery query =
        getSession().createSQLQuery("SELECT MAX(" + columnName + ") FROM " + getTableName());
    return ((BigDecimal) query.uniqueResult()).intValue();
  }

  @Override
  public long getNextSequenceValue(String sequenceName) {
    Session session = getSessionFactory().openSession();

    try {
      logger.debug("SQL: " + "SELECT NEXT VALUE FOR " + sequenceName);
      Query query = session.createSQLQuery("SELECT NEXT VALUE FOR " + sequenceName);
      return ((BigInteger) query.uniqueResult()).longValue();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new HibernateException(err.getMessage(), err.getCause());
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (Exception err) {
          // Ignore
        }
      }
    }
  }

  @Override
  public boolean exists(Serializable id) {
    Query q =
        getSession()
            .createQuery(
                "select count(x) from " + persistentClass.getSimpleName() + " x where x.id = :id");
    q.setLong("id", (Long) id);

    return ((Long) q.uniqueResult()).equals(1L);
  }

  @Override
  public List<T> findByExample(T exampleEntity) {
    Criteria criteria = getSession().createCriteria(persistentClass);
    criteria.add(Example.create(exampleEntity));

    return criteria.list();
  }

  @Override
  public List<T> findByExample(T exampleEntity, String[] excludeProperty) {
    Criteria criteria = getSession().createCriteria(persistentClass);
    Example example = Example.create(exampleEntity);

    for (String exclude : excludeProperty) {
      example.excludeProperty(exclude);
    }

    criteria.add(example);

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(Criterion... criterion) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(Collection<Criterion> criterion) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(Collection<Criterion> criterion, PageInfo pageInfo) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (pageInfo != null) {
      criteria.setMaxResults(pageInfo.getSize());
      criteria.setFirstResult(pageInfo.getSkip());
    }

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(Collection<Criterion> criterionList, Collection<Order> orderList) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(
      Collection<Criterion> criterionList, Collection<Order> orderList, int maxResult) {
    Criteria criteria = getSession().createCriteria(persistentClass);
    criteria.setMaxResults(maxResult);

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(
      Collection<Criterion> criterionList, Collection<Order> orderList, PageInfo pageInfo) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (pageInfo != null) {
      criteria.setMaxResults(pageInfo.getSize());
      criteria.setFirstResult(pageInfo.getSkip());
    }

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    return criteria.list();
  }

  @Override
  public List findByCriteria(
      ProjectionList projectionList,
      Collection<Criterion> criterionList,
      Collection<Order> orderList,
      PageInfo pageInfo,
      Class... transformClass) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (pageInfo != null) {
      criteria.setMaxResults(pageInfo.getSize());
      criteria.setFirstResult(pageInfo.getSkip());
    }

    if (projectionList != null) {
      criteria.setProjection(projectionList);
    }

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    if (transformClass != null && transformClass.length > 0) {
      criteria.setResultTransformer(Transformers.aliasToBean(transformClass[0]));
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(
      Map<String, String> alias, Collection<Criterion> criterionList, Collection<Order> orderList) {
    Criteria criteria =
        getSession()
            .createCriteria(
                persistentClass, Introspector.decapitalize(persistentClass.getSimpleName()));

    if (alias != null) {
      for (String associationPath : alias.keySet()) {
        criteria.createAlias(associationPath, alias.get(associationPath));
      }
    }

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    return criteria.list();
  }

  @Override
  public List<T> findByCriteria(
      Map<String, String> alias,
      Collection<Criterion> criterionList,
      Collection<Order> orderList,
      PageInfo pageInfo) {
    Criteria criteria =
        getSession()
            .createCriteria(
                persistentClass, Introspector.decapitalize(persistentClass.getSimpleName()));

    if (pageInfo != null) {
      criteria.setMaxResults(pageInfo.getSize());
      criteria.setFirstResult(pageInfo.getSkip());
    }

    if (alias != null) {
      Iterator<String> itr = alias.keySet().iterator();
      while (itr.hasNext()) {
        String associationPath = itr.next();
        criteria.createAlias(associationPath, alias.get(associationPath));
      }
    }

    if (criterionList != null) {
      for (Criterion criterion : criterionList) {
        criteria.add(criterion);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    return criteria.list();
  }

  @Override
  public List findByCriteria(
      ProjectionList projectionList, Collection<Criterion> criterionList, Class... transformClass) {
    Criteria criteria =
        getSession()
            .createCriteria(
                persistentClass, Introspector.decapitalize(persistentClass.getSimpleName()));

    if (projectionList != null) {
      criteria.setProjection(projectionList);
    }

    if (criterionList != null) {
      for (Criterion c : criterionList) {
        criteria.add(c);
      }
    }

    if (transformClass != null && transformClass.length > 0) {
      criteria.setResultTransformer(Transformers.aliasToBean(transformClass[0]));
    }

    return criteria.list();
  }

  @Override
  public List findByCriteria(
      ProjectionList projectionList,
      Collection<Criterion> criterionList,
      Collection<Order> orderList,
      Class... transformClass) {
    Criteria criteria =
        getSession()
            .createCriteria(
                persistentClass, Introspector.decapitalize(persistentClass.getSimpleName()));

    if (projectionList != null) {
      criteria.setProjection(projectionList);
    }

    if (criterionList != null) {
      for (Criterion c : criterionList) {
        criteria.add(c);
      }
    }

    if (orderList != null) {
      for (Order order : orderList) {
        criteria.addOrder(order);
      }
    }

    if (transformClass != null && transformClass.length > 0) {
      criteria.setResultTransformer(Transformers.aliasToBean(transformClass[0]));
    }

    return criteria.list();
  }

  @Override
  public List getProjection(Projection projection) {
    Criteria criteria = getSession().createCriteria(this.persistentClass);
    criteria.setProjection(projection);

    return criteria.list();
  }

  protected SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /*
   * Swing between application and web container
   */
  protected Session getSession() {
    if (session != null) {
      return session;
    } else {
      logger.debug("Specific session not found! Return current session.");
      return sessionFactory.getCurrentSession();
    }
  }

  public long getSum(String columnName) {
    SQLQuery query =
        getSession().createSQLQuery("SELECT SUM(" + columnName + ") FROM " + getTableName());
    return ((Long) query.uniqueResult());
  }

  public long getSum(String columnName, Criterion... criterion) {
    Criteria criteria = getSession().createCriteria(persistentClass);

    if (criterion != null) {
      for (Criterion c : criterion) {
        criteria.add(c);
      }
    }

    criteria.setProjection(Projections.sum(columnName));

    if (criteria.uniqueResult() == null) {
      return 0L;
    }

    return (Long) criteria.uniqueResult();
    //SQLQuery query = getSession().createSQLQuery("SELECT SUM(" + columnName + ") FROM " + getTableName());
    //return ((Long) query.uniqueResult());
  }
}
