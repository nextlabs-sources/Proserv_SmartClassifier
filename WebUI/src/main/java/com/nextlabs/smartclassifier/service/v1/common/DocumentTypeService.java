package com.nextlabs.smartclassifier.service.v1.common;

import com.nextlabs.smartclassifier.database.entity.DocumentExtractor;
import com.nextlabs.smartclassifier.database.manager.DocumentExtractorManager;
import com.nextlabs.smartclassifier.dto.v1.DocumentExtractorDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

public class DocumentTypeService 
		extends Service {
	
	public DocumentTypeService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public DocumentTypeService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getDocumentTypes() {
		CollectionResponse response = new CollectionResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			DocumentExtractorManager documentExtractorManager = new DocumentExtractorManager(getSessionFactory(), session);
			List<DocumentExtractor> documentExtractors = documentExtractorManager.getDocumentExtractors();
			response.setTotalNoOfRecords(documentExtractorManager.getRecordCount(null));
			
			transaction.commit();
			
			if(documentExtractors != null && documentExtractors.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<DocumentExtractorDTO> documentExtractorDTOs = new ArrayList<DocumentExtractorDTO>();
				for(DocumentExtractor documentExtractor : documentExtractors) {
					documentExtractorDTOs.add(new DocumentExtractorDTO(documentExtractor));
				}
				response.setData(documentExtractorDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
}
