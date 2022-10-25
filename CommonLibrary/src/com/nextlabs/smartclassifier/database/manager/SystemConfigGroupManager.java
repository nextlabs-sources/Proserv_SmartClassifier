package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.SystemConfigGroupDAO;
import com.nextlabs.smartclassifier.database.entity.SystemConfigGroup;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class SystemConfigGroupManager 
		extends Manager {
	
	private SystemConfigGroupDAO systemConfigGroupDAO;
	
	public SystemConfigGroupManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.systemConfigGroupDAO = new SystemConfigGroupDAO(sessionFactory, session);
	}
	
	public List<SystemConfigGroup> getSystemConfigGroups()
			throws ManagerException {
		try {
			logger.debug("Get all system config groups.");
			
			List<Criterion> criterionList = new ArrayList<Criterion>();
			criterionList.add(Restrictions.eq(SystemConfigGroup.EDITABLE, true));
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(Order.asc(SystemConfigGroup.DISPLAY_ORDER));
			
			List<SystemConfigGroup> systemConfigGroups = systemConfigGroupDAO.findByCriteria(criterionList, orderList);
			
			for(SystemConfigGroup systemConfigGroup : systemConfigGroups) {
				systemConfigGroup.getSystemConfigs();
			}
			
			return systemConfigGroups;
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public SystemConfigGroup getSystemConfigGroup(String configGroupName)
			throws ManagerException {
		try {
			logger.debug("Get system config group named: " + configGroupName);
			
			List<SystemConfigGroup> systemConfigGroups = systemConfigGroupDAO.findByCriteria(Restrictions.eq(SystemConfigGroup.NAME, configGroupName));
			
			if(systemConfigGroups != null && systemConfigGroups.size() > 0) {
				systemConfigGroups.get(0).getSystemConfigs();
				return systemConfigGroups.get(0);
			}
			
			return null;
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			return systemConfigGroupDAO.getCount(criterion);
		} catch(HibernateException err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			throw new ManagerException(err);
		}
	}
}
