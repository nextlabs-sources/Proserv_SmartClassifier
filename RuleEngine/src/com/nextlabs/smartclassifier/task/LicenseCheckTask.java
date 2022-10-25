package com.nextlabs.smartclassifier.task;

import com.nextlabs.smartclassifier.RuleEngine;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.database.manager.DocumentRecordManager;
import com.nextlabs.smartclassifier.database.manager.ExtractorManager;
import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.util.DateUtil;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.wald.license.checker.JarChecker;
import com.wald.license.common.LicenseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class LicenseCheckTask implements Runnable {

    private RuleEngine ruleEngine;
    protected final Logger logger = LogManager.getLogger(getClass());

    public LicenseCheckTask(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @Override
    public void run() {

        try {

            String currentDate = DateUtil.format(new Date(), DateUtil.DF_DEFAULT);

            logger.info("Checking the license on " + currentDate);

            String pwd = Paths.get(".").toAbsolutePath().getParent().getParent().normalize().toString();
            JarChecker checker = new JarChecker(
                    pwd + "/" + FileUtil.getLibraryFolder() + NextLabsConstant.LICENSE_JAR_FILE);
            checker.check();

            Properties properties = JarChecker.getProperties();

            logger.info("Got the license properties successfully. Valid License File detected");

            if(!properties.getProperty("expiration").equals("-1")) {
                Date licenseExpiryDate = JarChecker.license.getExpirationDate();
                logger.info("The license expiry date is = " + DateUtil.format(licenseExpiryDate, DateUtil.DF_DDMMMMYYYY));
            } else {
                logger.info("License Check - OK. License is a PERPETUAL license.");
            }

            String licensedDataVolume = properties.getProperty("data_size");
            String licensedExpiryDate = properties.getProperty("expiration");

            updateLicenseProperties(licensedExpiryDate, licensedDataVolume);

            printDataVolumeUsageInMBs(licensedDataVolume);

            // if the license was expired, an exception would have been thrown
            updateLicenseValidityAndCheckedDate(true);

        } catch (LicenseException e) {
            logger.error("Invalid or expired license detected.\nPlease contact Nextlabs Support Team for assistance.", e);
            updateLicenseValidityAndCheckedDate(false);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            logger.info("----------------------------------------------------------------");
        }

    }

    private void printDataVolumeUsageInMBs(String data_size) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = ruleEngine.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            long sum = (new DocumentRecordManager(ruleEngine.getSessionFactory(), session)).getVolumeUsed();
            float sumInMB = sum / 1048576;
            logger.info("The total volume used so far = " + sumInMB + " MBs.");
            logger.info("The total licensed volume is = " + (data_size!=null ? data_size + " GBs." : "UNLIMITED"));

            transaction.commit();
        } catch (ManagerException | Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Used to print Properties object
     *
     * @param properties The license properties
     */
    public void logProperties(Properties properties) {

        StringBuilder sb = new StringBuilder("The license properties are as follows : \n");
        Enumeration e = properties.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            sb.append(key).append(" = ").append(properties.getProperty(key)).append("\n");
        }
        logger.info(sb.toString());
    }


    /**
     * Used to check if the license is expired.
     *
     * @param licenseExpiryDate the license expiry date
     * @return true if the license is expired, false otherwise
     */
    public boolean isLicenseExpired(Date licenseExpiryDate) {

        Date today = Calendar.getInstance().getTime();

        if (licenseExpiryDate.before(today)) {
            logger.error("License Check = Failed. The license is expired! Expiry date = " + licenseExpiryDate);
            return true;
        } else {
            logger.debug("License Check = OK. The license is not yet expired.");
            logger.info("----------------------------------------------------------------");
            return false;
        }
    }

    /**
     * Checks if this Smart Classifier is installed on a machine with more
     * number of cores than mentioned in the license.
     *
     * @return true if the machine cores exceeds the numbers of cores in the
     * license and false otherwise
     */
    public boolean hasInvalidNumberOfCores(int licensedCoreCount) {

        int cpu_core_count = Runtime.getRuntime().availableProcessors();

        if (cpu_core_count > licensedCoreCount) {
            logger.error("License Check 2 = Failed. The number of CPU cores in the machine exceeds the number of cores mentioned in the license!");
            return true;
        } else {
            logger.debug("License Check 2 = OK. The number of cores is less than or equal to the number of cores in the license.");
            return false;
        }
    }

    /**
     * Checks if this Smart Classifier has more number of Content Extractors
     * installed than mentioned in the license
     *
     * @return true, if this installation has invalid number of extractors and
     * false otherwise
     */
    public boolean hasInvalidNumberOfExtractors(int licenseNumberOfExtractors) {

        long installed_extractor_count = getExtractorCount();

        if (installed_extractor_count > licenseNumberOfExtractors) {
            logger.error("License Check 3 = Failed. The number of extractors in this installation exceeds the number of extractors mentioned in the license.");
            return true;
        } else {
            logger.debug("License Check 3 = OK. The number of extractors in this installation is less than or equal to the number of extractors mentioned in the license");
            return false;
        }
    }

    /**
     * Updates the license validity in the database
     *
     * @param validity the license validity now
     */
    private void updateLicenseValidityAndCheckedDate(boolean validity) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = ruleEngine.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            (new SystemConfigManager(ruleEngine.getSessionFactory(), session)).updateLicenseValidityAndCheckedDate(validity);

            transaction.commit();
        } catch (ManagerException | Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Update the license properties after analyzing the license file
     * @param licensedExpiryDate
     * @param licensedDataVolume
     */
    private void updateLicenseProperties(String licensedExpiryDate, String licensedDataVolume) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = ruleEngine.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            logger.info("Trying to update the license properties in the database");

            (new SystemConfigManager(ruleEngine.getSessionFactory(), session)).updateLicenseProperties(licensedExpiryDate,
                    licensedDataVolume);
            transaction.commit();
        } catch (ManagerException | Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                   logger.error(err.getMessage(), err);
                }
            }
        }
    }

    /**
     * Method to get the extractor count - number of extractors installed for
     * this smart classifier installation.
     *
     * @return the number of extractors installed
     * 
     */
    private long getExtractorCount() {
        long result = 0;

        Session session = null;
        Transaction transaction = null;

        try {
            session = ruleEngine.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            result = (new ExtractorManager(ruleEngine.getSessionFactory(), session)).getExtractorCount();

            transaction.commit();
        } catch (Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore for now
                }
            }
        }
        logger.debug(" The number of extractors found is = " + result);
        return result;
    }
}
