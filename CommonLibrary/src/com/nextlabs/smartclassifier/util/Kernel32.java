package com.nextlabs.smartclassifier.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

public interface Kernel32
		extends StdCallLibrary {
	
	static final int FILE_SHARE_READ = (0x00000001);
	//final int FILE_SHARE_WRITE = (0x00000002);
	//final int FILE_SHARE_DELETE = (0x00000004);
	static final int OPEN_EXISTING = (3);
	static final int GENERIC_READ = (0x80000000);
	//final int GENERIC_WRITE = (0x40000000);
	//final int FILE_FLAG_NO_BUFFERING = (0x20000000);
	//final int FILE_FLAG_WRITE_THROUGH = (0x80000000);
	//final int FILE_READ_ATTRIBUTES = (0x0080);
	//final int FILE_WRITE_ATTRIBUTES = (0x0100);
	//final int ERROR_INSUFFICIENT_BUFFER = (122);
	static final int FILE_ATTRIBUTE_ARCHIVE = (0x20);
	
	final static Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		
		{
			put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
			put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
		}
	};
	
	public Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class, WIN32API_OPTIONS);
	
	public int GetLastError();
	
	public class BY_HANDLE_FILE_INFORMATION 
			extends Structure {
		public DWORD    dwFileAttributes;
		public FILETIME ftCreationTime;
		public FILETIME ftLastAccessTime;
		public FILETIME ftLastWriteTime;
		public DWORD    dwVolumeSerialNumber;
		public DWORD    nFileSizeHigh;
		public DWORD    nFileSizeLow;
		public DWORD    nNumberOfLinks;
		public DWORD    nFileIndexHigh;
		public DWORD    nFileIndexLow;
		
		public static class ByReference 
				extends BY_HANDLE_FILE_INFORMATION 
				implements Structure.ByReference {
			
		};
		
		public static class ByValue 
				extends BY_HANDLE_FILE_INFORMATION 
				implements Structure.ByValue {
			
		}

		@Override
		protected List<String> getFieldOrder() {
			List<String> fields = new ArrayList<String>();
			
			fields.add("dwFileAttributes");
			fields.add("ftCreationTime");
			fields.add("ftLastAccessTime");
			fields.add("ftLastWriteTime");
			fields.add("dwVolumeSerialNumber");
			fields.add("nFileSizeHigh");
			fields.add("nFileSizeLow");
			fields.add("nNumberOfLinks");
			fields.add("nFileIndexHigh");
			fields.add("nFileIndexLow");
			
			return fields;
		};        
	};
	
	boolean GetFileInformationByHandle(HANDLE hFile, BY_HANDLE_FILE_INFORMATION lpFileInformation);
	
	HANDLE CreateFile(String lpFileName, int dwDesiredAccess, int dwShareMode,
            WinBase.SECURITY_ATTRIBUTES lpSecurityAttributes,
            int dwCreationDisposition, int dwFlagsAndAttributes,
            HANDLE hTemplateFile);
	
	boolean CloseHandle(HANDLE hObject);
}
