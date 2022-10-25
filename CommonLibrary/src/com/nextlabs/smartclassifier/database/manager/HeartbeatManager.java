package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.HeartbeatDAO;
import com.nextlabs.smartclassifier.database.entity.Heartbeat;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class HeartbeatManager 
		extends Manager {
	
	private HeartbeatDAO heartbeatDAO;
	
	public HeartbeatManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.heartbeatDAO = new HeartbeatDAO(sessionFactory, session);
	}
	
	public List<Heartbeat> getHeartbeats() 
			throws ManagerException {
		try {
			return heartbeatDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void beat(Long componentId, String componentType) 
			throws ManagerException {
		try {
			List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
			heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, componentId));
			heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, componentType));
			
			List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);
			
			if(heartbeats != null
					&& heartbeats.size() > 0) {
				Heartbeat heartbeat = heartbeats.get(0);
				heartbeat.setLastHeartbeat(System.currentTimeMillis());
				
				heartbeatDAO.saveOrUpdate(heartbeat);
			} else {
				logger.info("Register heart beat for new component [ID]:" + componentId + ", [Type]:" + componentType);
				Heartbeat componentHeartbeat = new Heartbeat();
				
				componentHeartbeat.setComponentId(componentId);
				componentHeartbeat.setComponentType(componentType);
				componentHeartbeat.setLastHeartbeat(System.currentTimeMillis());
				componentHeartbeat.setCreatedOn(new Date());
				
				heartbeatDAO.saveOrUpdate(componentHeartbeat);
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
}
