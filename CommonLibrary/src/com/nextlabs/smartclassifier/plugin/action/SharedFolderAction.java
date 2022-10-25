package com.nextlabs.smartclassifier.plugin.action;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.exception.RollbackException;

/**
 * Created by pkalra on 11/23/2016.
 * This class indicates actions that are executed once for every file.
 */
public abstract class SharedFolderAction extends Action {

    public SharedFolderAction(String name) {
        super(name);
        this.repositoryType = RepositoryType.SHARED_FOLDER;
    }

    public void rollback() 
    		throws RollbackException {
    }

    public boolean deleteBackupFile() {
        return true;
    }
}
