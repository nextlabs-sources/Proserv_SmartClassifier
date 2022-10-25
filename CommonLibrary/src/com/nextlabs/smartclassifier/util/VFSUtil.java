package com.nextlabs.smartclassifier.util;


import com.nextlabs.smartclassifier.constant.SCConstant;
import org.apache.commons.vfs2.FileObject;

import java.net.URLDecoder;
import java.util.regex.Pattern;

import static com.nextlabs.smartclassifier.constant.Punctuation.BACK_SLASH;
import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;

public class VFSUtil {
	
	public static final String convert2VFSFormat(String filePath) {
		if(filePath != null && !filePath.startsWith(SCConstant.FILE_PREFIX)) {
			if(!filePath.endsWith(BACK_SLASH)) {
				filePath += BACK_SLASH;
			}
			
			return SCConstant.FILE_PREFIX + filePath.replace(BACK_SLASH, FORWARD_SLASH);
		}
		
		return filePath;
	}
	
	public static final String getAbsoluteFilePath(FileObject file) 
			throws Exception {
		if(file != null) {
			return getAbsoluteFilePath(file.toString());
		}
		
		return null;
	}
	
	public static final String getAbsoluteFilePath(String VFSFilePath) 
			throws Exception {
		if(VFSFilePath != null) {
			String filename = URLDecoder.decode(VFSFilePath.replaceAll(Pattern.quote("+"), "%2B"), "UTF-8");
			
			if(filename.startsWith(SCConstant.FILE_PREFIX)) {
				return filename.substring(SCConstant.FILE_PREFIX.length());
			}
			
			return filename;
		}
		
		return null;
	}
}
