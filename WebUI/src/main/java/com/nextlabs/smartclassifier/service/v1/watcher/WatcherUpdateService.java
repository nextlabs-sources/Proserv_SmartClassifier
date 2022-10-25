package com.nextlabs.smartclassifier.service.v1.watcher;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.database.manager.WatcherManager;
import com.nextlabs.smartclassifier.dto.v1.WatcherDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.UpdateResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class WatcherUpdateService extends Service {

  public WatcherUpdateService(ServletContext servletContext) {
    super(servletContext);
  }

  public WatcherUpdateService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse updateWatcher(UpdateRequest updateRequest) {
    UpdateResponse response = new UpdateResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      WatcherManager watcherManager = new WatcherManager(getSessionFactory(), session);
      Watcher watcher = ((WatcherDTO) updateRequest.getData()).getEntity();

      Map<String, String> monitoredFolder = watcherManager.checkMonitorFolder(watcher);

      if (monitoredFolder == null || monitoredFolder.isEmpty()) {
        watcherManager.updateWatcher(watcher);
        response.setStatusCode(MessageUtil.getMessage("success.data.modified.code"));
        response.setMessage(MessageUtil.getMessage("success.data.modified"));
      } else {
        Set<Entry<String, String>> monitoredFolderEntries = monitoredFolder.entrySet();
        Iterator<Entry<String, String>> entryIterator = monitoredFolderEntries.iterator();
        Entry<String, String> firstEntry = entryIterator.next();

        response.setStatusCode(
            MessageUtil.getMessage("invalid.input.field.includedirectorymonitored.code"));
        response.setMessage(
            MessageUtil.getMessage(
                "invalid.input.field.includedirectorymonitored",
                firstEntry.getKey(),
                firstEntry.getValue()));
      }

      transaction.commit();

    } catch (RecordNotFoundException err) {
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(err.getMessage(), err);
      response.setStatusCode(MessageUtil.getMessage("no.entity.found.modify.code"));
      response.setMessage(MessageUtil.getMessage("no.entity.found.modify", "watcher"));
    } catch (ManagerException | Exception err) {
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(err.getMessage(), err);
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));
      response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (HibernateException err) {
          // Ignore
        }
      }
    }

    return response;
  }
}
