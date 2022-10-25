package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.util.Kernel32.BY_HANDLE_FILE_INFORMATION;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import static com.nextlabs.smartclassifier.constant.Punctuation.*;

public class FileUtil {

    public static final String BACKUP_FILE_PREFIX = "~$";
    public static final String BACKUP_FILE_EXTENSION = ".bak";
    private static final String CONFIG_FOLDER = "config.folder";
    private static final String PLUGIN_FOLDER = "plugins.folder";
    private static final String LIBRARY_FOLDER = "lib.folder";

    public static final String getConfigFolder() {
        if (System.getProperty(CONFIG_FOLDER) != null) {
            return System.getProperty(CONFIG_FOLDER);
        }

        return "conf/";
    }

    public static final String getPluginFolder() {
        if (System.getProperty(PLUGIN_FOLDER) != null) {
            return System.getProperty(PLUGIN_FOLDER);
        }

        return "plugins/";
    }

    public static final String getLibraryFolder() {
        if (System.getProperty(LIBRARY_FOLDER) != null) {
            return System.getProperty(LIBRARY_FOLDER);
        }

        return "xlib/";
    }

    public static final String getFilePath(String absolutePath) {
        if (StringUtils.isNotBlank(absolutePath) && absolutePath.lastIndexOf(FORWARD_SLASH) > 1) {
            return absolutePath.substring(0, absolutePath.lastIndexOf(FORWARD_SLASH));
        }

        return absolutePath;
    }

    public static final String getFileName(String absolutePath) {
        if (StringUtils.isNotBlank(absolutePath)) {
            return absolutePath.substring(absolutePath.lastIndexOf(FORWARD_SLASH) + 1);
        }

        return absolutePath;
    }

    public static final String getFileExtension(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }

        return fileName;
    }

    public static final String getNonDuplicateFilename(String parent, String child)
            throws SecurityException {
        if (StringUtils.isNotBlank(parent) && StringUtils.isNotBlank(child)) {
            int dotIndex = child.lastIndexOf(".");
            String extension = child.substring(dotIndex);
            String filename = child.substring(0, dotIndex);
            File file = new File(parent, child);

            if (!file.exists()) {
                return child;
            } else {
                int sequence = 0;
                String newFilename;

                do {
                    sequence++;
                    newFilename = filename + "(" + sequence + ")" + extension;
                    file = new File(parent, newFilename);
                } while (file.exists());

                return newFilename;
            }
        }

        return child;
    }

    public static final String getFileId(String absolutePath)
            throws Exception {
        return getFileId(Paths.get(absolutePath));
    }

    public static final String getFileId(Path path)
            throws Exception {
        try {
            if (path != null) {
                BasicFileAttributes basicAttributes = Files.readAttributes(path, BasicFileAttributes.class);

                // Linux file system, get file id from inode
                if (basicAttributes.fileKey() != null) {
                    return basicAttributes.fileKey().toString();
                } else {
                    // Windows file system, get file id via Kernel32.dll
                    WinBase.SECURITY_ATTRIBUTES attr = null;
                    BY_HANDLE_FILE_INFORMATION fileInformation = new BY_HANDLE_FILE_INFORMATION();
                    HANDLE fileHandler = Kernel32.INSTANCE.CreateFile(path.toString(), Kernel32.GENERIC_READ, Kernel32.FILE_SHARE_READ, attr, Kernel32.OPEN_EXISTING, Kernel32.FILE_ATTRIBUTE_ARCHIVE, null);
                    Kernel32.INSTANCE.GetFileInformationByHandle(fileHandler, fileInformation);
                    String fileId = fileInformation.nFileIndexHigh + "-" + fileInformation.nFileIndexLow;
                    Kernel32.INSTANCE.CloseHandle(fileHandler);

                    return fileId;
                }
            }
        } catch (NoSuchFileException err) {
            return EMPTY_STRING;
        } catch (Exception err) {
            throw err;
        }

        return EMPTY_STRING;
    }

    public static final BasicFileAttributes getBasicAttributes(String absolutePath)
    		throws Exception {
    	return getBasicAttributes(Paths.get(absolutePath));
    }

    public static final BasicFileAttributes getBasicAttributes(Path path) 
    		throws Exception {
    	if(path != null) {
    		return Files.readAttributes(path, BasicFileAttributes.class);
    	}

    	return null;
    }

    public static final String getBackupAbsolutePath(String absolutePath)
            throws Exception {
        if (StringUtils.isNotBlank(absolutePath)) {
            int lastIndex = -1;

            if (absolutePath.indexOf(FORWARD_SLASH) > -1) {
                lastIndex = absolutePath.lastIndexOf(FORWARD_SLASH);
            } else if (absolutePath.indexOf(BACK_SLASH) > -1) {
                lastIndex = absolutePath.lastIndexOf(BACK_SLASH);
            }

            if (lastIndex > 0 && (absolutePath.length() > (lastIndex + 1))) {
                return absolutePath.substring(0, lastIndex + 1)
                        + BACKUP_FILE_PREFIX + absolutePath.substring(lastIndex + 1) + BACKUP_FILE_EXTENSION;
            }
        }

        return EMPTY_STRING;
    }
}
