package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.database.dao.MetadataFieldDAO;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.exception.ManagerException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.*;

public class MetadataFieldManager 
		extends Manager {
	
	private MetadataFieldDAO metadataFieldDAO;
	
	public MetadataFieldManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.metadataFieldDAO = new MetadataFieldDAO(sessionFactory, session);
	}
	
	public List<MetadataField> getFields()
			throws ManagerException {
		try {
			return metadataFieldDAO.getAll();
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public Map<String, MetadataField> loadFields()
			throws ManagerException {
		Map<String, MetadataField> metadataFieldByName = new LinkedHashMap<>();
		
		try {
			List<Order> recordOrder = new ArrayList<>();
			recordOrder.add(Order.asc(MetadataField.DISPLAY_ORDER));
			
			List<MetadataField> fields = metadataFieldDAO.findByCriteria(Collections.<Criterion> emptyList(), recordOrder);
			
			if(fields != null) {
				for(MetadataField field : fields) {
					metadataFieldByName.put(field.getFieldName(), field);
				}
			}
		} catch(Exception err) {
			throw new ManagerException(err.getMessage(), err);
		}
		
		return metadataFieldByName;
	}
}