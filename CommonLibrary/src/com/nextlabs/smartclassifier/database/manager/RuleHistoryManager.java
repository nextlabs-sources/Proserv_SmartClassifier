package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.RuleHistoryDAO;
import com.nextlabs.smartclassifier.database.entity.RuleHistory;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class RuleHistoryManager 
		extends Manager {
	
	private RuleHistoryDAO ruleHistoryDAO;
	
	public RuleHistoryManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.ruleHistoryDAO = new RuleHistoryDAO(sessionFactory, session);
	}
	
	public RuleHistory getRuleHistory(Long ruleId, Integer ruleVersion) 
			throws ManagerException {
		if(ruleId == null || ruleId == 0L) {
			throw new ManagerException("Invalid rule id: " + ruleId);
		}
		
		if(ruleVersion == null || ruleVersion == 0) {
			throw new ManagerException("Invalid rule version: " + ruleVersion);
		}
		
		try {
			logger.debug("Get rule history with rule id " + ruleId + "; rule version id " + ruleVersion);
			List<Criterion> criteria = new ArrayList<Criterion>();
			criteria.add(Restrictions.eq(RuleHistory.RULE_ID, ruleId));
			criteria.add(Restrictions.eq(RuleHistory.RULE_VERSION, ruleVersion));
			
			List<RuleHistory> ruleHistories = ruleHistoryDAO.findByCriteria(criteria);
			
			if(ruleHistories != null && ruleHistories.size() > 0) {
				return ruleHistories.get(0);
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public List<RuleHistory> getRuleHistories(Long ruleId) 
			throws ManagerException {
		if(ruleId == null || ruleId == 0L) {
			throw new ManagerException("Invalid rule id: " + ruleId);
		}
		
		try {
			logger.debug("Get rule history with rule id " + ruleId);
			List<Criterion> criteria = new ArrayList<Criterion>();
			criteria.add(Restrictions.eq(RuleHistory.RULE_ID, ruleId));
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc(RuleHistory.RULE_VERSION));
			
			return ruleHistoryDAO.findByCriteria(criteria, orders);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
}
