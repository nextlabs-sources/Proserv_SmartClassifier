@echo off
set SERVICE_NAME=Smart Classifier Queue
set HOME_DIR=%~dp0
 
set PR_INSTALL=%HOME_DIR%/%SERVICE_NAME%.exe

"%PR_INSTALL%" //SS/"%SERVICE_NAME%"
"%PR_INSTALL%" //DS/"%SERVICE_NAME%"
 
if not errorlevel 1 goto installed
echo Failed to uninstall "%SERVICE_NAME%" service.  Refer to log in %PR_LOGPATH%
goto end
 
:installed
echo The Service "%SERVICE_NAME%" has been uninstalled
 
:end