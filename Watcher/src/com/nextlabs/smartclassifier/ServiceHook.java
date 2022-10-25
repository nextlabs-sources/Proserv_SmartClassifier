package com.nextlabs.smartclassifier;

import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;

public class ServiceHook {

    private static final Logger logger = LogManager.getLogger(ServiceHook.class);
    private static Watcher watcher = null;

    /**
     * Program entry point.
     *
     * @param args Startup parameters.
     */
    public static void main(String[] args) {
        try {
            logger.info("Starting up file watcher service.");
            
            if((new File(NextLabsConstant.NXL_DATA_DUMP_PATH.concat(NextLabsConstant.DATA_DUMP_FILE))).exists()) {
            	logger.info("Found recovery data dump file!");
                watcher = new Watcher(false);
                watcher.recover();
            } else {
                watcher = new Watcher(true);
                watcher.crawl();
                watcher.dumpToFile();
            }

            for (; ; ) {
                // Update database for heart beat
                watcher.beat();
                // Reload latest configuration
                watcher.reloadConfigs();
                // Process problematic root monitor path entry
                watcher.retryImaginaryPaths();

                try {
                    Thread.sleep(watcher.getSleepTime());
                } catch (InterruptedException err) {
                    // Ignore
                }
            }
        } catch (Throwable throwable) {
            logger.fatal(throwable.getMessage(), throwable);

            if (watcher != null) {
                watcher.shutdown();
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
            if (watcher != null) {
                watcher.shutdown();
            }

            System.exit(0);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }
}
