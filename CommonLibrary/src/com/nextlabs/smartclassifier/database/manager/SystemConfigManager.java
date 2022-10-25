package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.dao.SystemConfigDAO;
import com.nextlabs.smartclassifier.database.entity.SystemConfig;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemConfigManager
        extends Manager {

    private SystemConfigDAO systemConfigDAO;

    public SystemConfigManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.systemConfigDAO = new SystemConfigDAO(sessionFactory, session);
    }

    public Map<String, String> loadConfigs()
            throws ManagerException {
        Map<String, String> configuration = new HashMap<String, String>();

        try {
            List<SystemConfig> configs = systemConfigDAO.getAll();

            if (configs != null) {
                for (SystemConfig config : configs) {
                    if (config.getValue() != null && config.getValue().startsWith(SCConstant.ENCRYPTED_PREFIX)
                            && config.getValue().endsWith(SCConstant.ENCRYPTED_SUFFIX)) {
                        config.isEncrypted(true);
                        configuration.put(config.getIdentifier(), NxlCryptoUtil.decrypt(config.getValue()));
                    } else {
                        config.isEncrypted(false);
                        configuration.put(config.getIdentifier(), config.getValue());
                    }
                }
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return configuration;
    }

    public String getConfig(String configName)
            throws ManagerException {
        try {
            List<SystemConfig> configs = systemConfigDAO.findByCriteria(Restrictions.eq(SystemConfig.IDENTIFIER, configName));

            if (configs != null && configs.size() > 0) {
                SystemConfig config = configs.get(0);

                if (config.getValue() != null && config.getValue().startsWith(SCConstant.ENCRYPTED_PREFIX)
                        && config.getValue().endsWith(SCConstant.ENCRYPTED_SUFFIX)) {
                    return NxlCryptoUtil.decrypt(config.getValue());
                } else {
                    return config.getValue();
                }
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        throw new ManagerException("Invalid system configuration name. Record not found.");
    }

    public SystemConfig getSystemConfigById(Long id)
            throws ManagerException {
        try {
            logger.debug("Get system config with id " + id);
            SystemConfig systemConfig = systemConfigDAO.get(id);

            if (systemConfig != null) {
                logger.debug("System config found with id " + id);
                return systemConfig;
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return null;
    }

    public void updateSystemConfig(SystemConfig systemConfig)
            throws ManagerException, RecordNotFoundException, IllegalArgumentException {
        try {
            logger.debug("Update system configuration for system config id " + systemConfig.getId());
            SystemConfig entity = systemConfigDAO.get(systemConfig.getId());

            if (entity != null) {
                entity.setValue(systemConfig.getValue());
                entity.setDescription(systemConfig.getDescription());
                entity.setModifiedOn(new Date());

                systemConfigDAO.saveOrUpdate(entity);
            } else {
                throw new RecordNotFoundException("System config record not found for the given SystemConfigID.");
            }
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    public boolean updateLicenseValidityAndCheckedDate(boolean validity)
            throws ManagerException {
        try {
            List<SystemConfig> licenseLastCheck = systemConfigDAO.findByCriteria(Restrictions.eq(SystemConfig.IDENTIFIER, SystemConfigKey.LICENSE_LAST_CHECK));
            List<SystemConfig> licenseValidity = systemConfigDAO.findByCriteria(Restrictions.eq(SystemConfig.IDENTIFIER, SystemConfigKey.LICENSE_VALIDITY));
            SystemConfig sysConfigLicenseCheckDate = null, sysConfigLicenseValidity = null;
            Date now = new Date();

            if ((licenseLastCheck != null) && (licenseLastCheck.size() > 0)) {
                sysConfigLicenseCheckDate = licenseLastCheck.get(0);
            }

            if ((licenseValidity != null) && (licenseValidity.size() > 0)) {
                sysConfigLicenseValidity = licenseValidity.get(0);
            }

            logger.debug("Setting the license last check date to = " + now.toString() + " and the validity to = " + validity);

            if (sysConfigLicenseCheckDate != null && sysConfigLicenseValidity != null) {
                sysConfigLicenseCheckDate.setValue(now.toString());
                sysConfigLicenseCheckDate.setModifiedOn(now);
                sysConfigLicenseValidity.setValue(String.valueOf(validity));
                sysConfigLicenseValidity.setModifiedOn(now);
                systemConfigDAO.saveOrUpdate(sysConfigLicenseCheckDate);
                systemConfigDAO.saveOrUpdate(sysConfigLicenseCheckDate);
            } else {
                logger.error("The license.validity AND/OR license.checked.date is null");
            }

        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }

        return false;
    }

    public boolean updateLicenseProperties(String expiryDate, String data_size)
            throws ManagerException {
        try {
            List<SystemConfig> licenseExpiryDate = systemConfigDAO.findByCriteria(Restrictions.eq(SystemConfig.IDENTIFIER, SystemConfigKey.LICENSE_EXPIRY_DATE));
            List<SystemConfig> licensedDataSize = systemConfigDAO.findByCriteria(Restrictions.eq(SystemConfig.IDENTIFIER, SystemConfigKey.LICENSE_DATA_SIZE));
            SystemConfig sysConfigLicenseExpiryDate = null, sysConfigLicenseDataSize = null;
            Date now = new Date();

            if ((licenseExpiryDate != null) && (licenseExpiryDate.size() > 0)) {
                sysConfigLicenseExpiryDate = licenseExpiryDate.get(0);
            }

            if ((licensedDataSize != null) && (licensedDataSize.size() > 0)) {
                sysConfigLicenseDataSize = licensedDataSize.get(0);
            }

            logger.debug("Updating the license properties. Expiry date = " + expiryDate + " and the data volume = " + (data_size != null ? data_size + " GBs." : "UNLIMITED"));

            if (sysConfigLicenseExpiryDate != null && expiryDate != null) {
                sysConfigLicenseExpiryDate.setValue(expiryDate);
                sysConfigLicenseExpiryDate.setModifiedOn(now);
            }

            if (sysConfigLicenseDataSize != null) {
                if (data_size != null) {
                    sysConfigLicenseDataSize.setValue(data_size);
                } else {
                    sysConfigLicenseDataSize.setValue("UNLIMITED");
                }
                sysConfigLicenseDataSize.setModifiedOn(now);
            }

            systemConfigDAO.saveOrUpdate(sysConfigLicenseExpiryDate);
            systemConfigDAO.saveOrUpdate(sysConfigLicenseDataSize);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return false;
    }
}
