package com.nextlabs.smartclassifier.service.v1.sourceauthentication;

import com.nextlabs.smartclassifier.database.manager.SourceAuthenticationManager;
import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CreationResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SourceAuthenticationCreationService extends Service {

    public SourceAuthenticationCreationService(ServletContext servletContext) {
        super(servletContext);
    }

    public SourceAuthenticationCreationService(
            ServletContext servletContext,
            ServletRequest servletRequest,
            ServletResponse servletResponse) {
        super(servletContext, servletRequest, servletResponse);
    }

    /**
     * Adds a new Source Authentication
     *
     * @param request the creation request
     * @return
     */
    public ServiceResponse addSourceAuthentication(CreationRequest request) {
        CreationResponse response = new CreationResponse();
        Session session = null;
        Transaction trans = null;

        try {
            session = getSessionFactory().openSession();
            trans = session.beginTransaction();

            SourceAuthenticationManager saManager = new SourceAuthenticationManager(getSessionFactory(), session);

            SourceAuthenticationDTO saDTO = (SourceAuthenticationDTO) request.getData();
            // unqiue(domainname, username)
            if (!saManager.hasSameUsername(saDTO.getDomain(), saDTO.getUsername())) {
                long entityId = saManager.addSourceAuthentication(saDTO.getEntity());
                logger.debug("Source authentication ( ID = " + entityId + " ) added.");

                trans.commit();

                response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
                response.setMessage(MessageUtil.getMessage("success.data.saved"));
                response.setEntityId(entityId);
            } else {
                response.setStatusCode(MessageUtil.getMessage("invalid.credential.duplicate.username.code"));
                response.setMessage(MessageUtil.getMessage("invalid.credential.duplicate.username", saDTO.getDomain(), saDTO.getUsername()));
            }

        } catch (ManagerException | Exception e) {
            if (trans != null) {
                try {
                    trans.rollback();
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
            logger.error(e.getMessage(), e);
            response.setStatusCode(MessageUtil.getMessage("server.error.code"));
            response.setMessage(MessageUtil.getMessage("server.error", e.getMessage()));
            response.setEntityId(-1L);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return response;
    }
}
