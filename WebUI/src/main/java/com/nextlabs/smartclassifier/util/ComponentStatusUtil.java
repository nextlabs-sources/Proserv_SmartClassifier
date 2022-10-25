package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.ComponentStatus;

public class ComponentStatusUtil {
	
	public static String getComponentStatus(long lastHeartbeat, int heartbeatInterval, int criticalCount) {
		if(lastHeartbeat <= 0 || heartbeatInterval <= 0) {
			return ComponentStatus.CRITICAL.getName().toLowerCase();
		}
		
		long missedCount = (System.currentTimeMillis() - lastHeartbeat)/(heartbeatInterval * 1000);
		
		if(missedCount >= criticalCount) {
			return ComponentStatus.CRITICAL.getName().toLowerCase();
//		} else if(missedCount >= warningCount) {
//			return ComponentStatus.WARNING.getName().toLowerCase();
		}
		
		return ComponentStatus.HEALTHY.getName().toLowerCase();
	}
}
