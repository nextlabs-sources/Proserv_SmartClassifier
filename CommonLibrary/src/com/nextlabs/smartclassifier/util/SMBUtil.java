package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.SCConstant;

import static com.nextlabs.smartclassifier.constant.Punctuation.*;

public class SMBUtil {
	
	/**
	 * Convert from \\xyz\ to smb://xyz/
	 * 
	 * @param filePath File path in windows explorer format
	 * @return Path in SMB format
	 */
	public static final String convert2SMBFormat(String filePath) {
		if(filePath != null && !filePath.startsWith(SCConstant.SMB_PREFIX)) {
			if(!filePath.endsWith(BACK_SLASH)) {
				filePath += BACK_SLASH;
			}
			
			return SCConstant.SMB_PREFIX + filePath.replace(BACK_SLASH, FORWARD_SLASH);
		}
		
		return filePath;
	}
	
	/**
	 * Convert from smb://xyz/ to \\xyz\
	 * 
	 * @param smbPath Path in SMB format
	 * @return File path in windows explorer format
	 */
	public static final String convert2FilePath(String smbPath) {
		if(smbPath != null && smbPath.startsWith(SCConstant.SMB_PREFIX)) {
			smbPath = smbPath.replaceFirst(SCConstant.SMB_PREFIX, EMPTY_STRING);
			return smbPath.replace(FORWARD_SLASH, BACK_SLASH);
		}
		
		return smbPath;
	}
}
