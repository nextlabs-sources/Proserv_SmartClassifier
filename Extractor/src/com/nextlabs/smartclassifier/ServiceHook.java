package com.nextlabs.smartclassifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.util.FileUtil;

public class ServiceHook {
	
	private static final Logger logger = LogManager.getLogger(ServiceHook.class);
	private static Extractor extractor = null;
	
	/**
	 * Program entry point.
	 * 
	 * @param args Startup parameters.
	 */
	public static void main(String[] args) {
		try {
			logger.info("Starting up content extractor service.");
			extractor = new Extractor();
			boolean messageReceived;
			
			for(;;) {
				do {
					// Update database for heart beat
					extractor.beat();
					extractor.reloadConfigs();
					messageReceived = extractor.checkMessage();
				} while(!extractor.isShuttingDown() && messageReceived);
				
				extractor.processReQueue();
				
				try {
					Thread.sleep(extractor.getSleepTime());
				} catch(InterruptedException err) {
					// Ignore
				}
			}
		} catch(Throwable throwable) {
			logger.fatal(throwable.getMessage(), throwable);
			
			if(extractor != null) {
				extractor.shutdown();
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
		} catch(Throwable throwable) {
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
			if(extractor != null) {
				extractor.shutdown();
			}
			
			System.exit(0);
		} catch(Throwable throwable) {
			logger.error(throwable.getMessage(), throwable);
		}
	}
}
