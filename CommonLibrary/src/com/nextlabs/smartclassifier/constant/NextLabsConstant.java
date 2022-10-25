package com.nextlabs.smartclassifier.constant;
/*
 * Created on April 30, 2012
 *
 * All sources, binaries and HTML pages (C) copyright 2012 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 */

/**
 * An interface for defining constant variable and configuration location
 * @author klee
 * @version: $Id: 
 */
public interface NextLabsConstant {
	
	// Configuration file path	
	public static final String NXL_WIN_CONFIG_SUB_PATH = ".\\conf\\";
	
	public static final String NXL_SOL_CONFIG_SUB_PATH = "../conf/";
	
	// Data dump file path
	public static final String NXL_DATA_DUMP_PATH = "..\\data\\";
	
	// Runtime configuration file
	public static final String NXL_RUNTIME_FILE = "runtimeconfig.xml";
		
	// System configuration file
	public static final String NXL_SYSTEM_FILE = "systemconfig.xml";
	
	// Log4j configuration file
	public static final String LOG4J_CONFIG_FILE = "log4j.xml";
	
	// Hibernate configuration file
	public static final String HIBERNATE_CONFIG_FILE = "hibernate.cfg.xml";
	
	// Data source configuration file
	public static final String DATA_SOURCE_CONFIG_FILE = "datasource.xml";
	
	// License jar file
	public static final String LICENSE_JAR_FILE = "license.jar";
	
	// Data dump file name
	public static final String DATA_DUMP_FILE = "datadump.ndmp";
	
	public static final String ACTION_ADD = "add";
	
	public static final String ACTION_UPDATE = "upd";
	
	public static final String ACTION_DELETE = "del";
		
}
