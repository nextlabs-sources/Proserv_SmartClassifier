package com.nextlabs.smartclassifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceHook {

    private static final Logger logger = LogManager.getLogger(ServiceHook.class);
    private static RuleEngine ruleEngine = null;

    /**
     * Program entry point.
     *
     * @param args Startup parameters.
     */
    public static void main(String[] args) {
        try {
            logger.info("Starting up rule engine service.");
            ruleEngine = new RuleEngine();

            for (; ; ) {
                // Update database for heart beat
                ruleEngine.beat();
                // Update task map
                ruleEngine.removeCompletedTask();
                ruleEngine.reloadConfigs();

                try {
                    Thread.sleep(ruleEngine.getSleepTime());
                } catch (InterruptedException err) {
                    // Ignore
                }
            }
        } catch (Throwable throwable) {
            logger.fatal(throwable.getMessage(), throwable);

            if (ruleEngine != null) {
                ruleEngine.shutdown();
            }
        }

        System.exit(0);
    }

    /**
     * Call by service to start program execution.
     *
     * @param args Startup parameters
     */
    public static void start(String[] args) {
        try {
            // process service start function
            main(args);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * Call by service to stop program execution.
     *
     * @param args Stop parameters
     */
    public static void stop(String[] args) {
        try {
            if (ruleEngine != null) {
                ruleEngine.shutdown();
            }

            System.exit(0);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }
}
