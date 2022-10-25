package com.nextlabs.smartclassifier.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.service.v1.metadatafield.MetadataFieldService;
import com.nextlabs.smartclassifier.service.v1.systemconfig.SystemConfigService;

@WebListener
public class HibernateSessionFactoryListener implements ServletContextListener {

  public final Logger logger = LogManager.getLogger(HibernateSessionFactoryListener.class);

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {

    Configuration configuration = new Configuration();
    configuration.configure(NextLabsConstant.HIBERNATE_CONFIG_FILE);
    logger.info("Hibernate configuration created successfully");

    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    logger.info("ServiceRegistry created successfully");

    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    logger.info("SessionFactory created successfully");

    servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
    logger.info("Hibernate SessionFactory configured successfully");

    servletContextEvent
        .getServletContext()
        .setAttribute(
            "SystemConfigs",
            new SystemConfigService(servletContextEvent.getServletContext()).loadConfigs());
    logger.info("System configuration loaded successfully");

    servletContextEvent
        .getServletContext()
        .setAttribute(
            "MetadataFields",
            new MetadataFieldService(servletContextEvent.getServletContext()).loadFields());
    logger.info("Metadata field loaded successfully");
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    SessionFactory sessionFactory =
        (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
    if (sessionFactory != null && !sessionFactory.isClosed()) {
      logger.info("Closing sessionFactory");
      sessionFactory.close();
    }

    logger.info("Released Hibernate sessionFactory resource");
    logger.info(
        "=================================================================================================\n");
  }
}
