@echo off
set SERVICE_NAME=Smart Classifier Indexer
set HOME_DIR=%~dp0

set PR_INSTALL=%CD%\%SERVICE_NAME%.exe

@REM Service Log Configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%HOME_DIR%..\server\logs
set PR_STDOUTPUT=
set PR_STDERROR=auto
set PR_LOGLEVEL=Info
 
@REM Path to Java Installation

@REM Startup Configuration
set PR_STARTUP=auto
set PR_STARTMODE=exe
set PR_STARTIMAGE=%HOME_DIR%solr.cmd
set PR_STARTPARAMS=start;-p;8093;-f

@REM Shutdown Configuration
set PR_STOPMODE=exe
set PR_STOPIMAGE=%HOME_DIR%solr.cmd
set PR_STOPPARAMS=stop;-p;8093
 
"%PR_INSTALL%" //IS/"%SERVICE_NAME%" ^
  --DisplayName="%SERVICE_NAME%" ^
  --Description="Runs the Indexer component of Smart Classifier" ^
  --ServiceUser LocalSystem  ^
  --Install="%PR_INSTALL%" ^
  --Startup="%PR_STARTUP%" ^
  --LogPath="%PR_LOGPATH%" ^
  --LogPrefix="%PR_LOGPREFIX%" ^
  --LogLevel="%PR_LOGLEVEL%" ^
  --StdOutput="%PR_STDOUTPUT%" ^
  --StdError="%PR_STDERROR%" ^
  --StartMode="%PR_STARTMODE%" ^
  --StartImage="%PR_STARTIMAGE%" ^
  --StartParams="%PR_STARTPARAMS%" ^
  --StopMode="%PR_STOPMODE%" ^
  --StopImage="%PR_STOPIMAGE%" ^
  --StopParams="%PR_STOPPARAMS%"
  
  
if not errorlevel 1 goto installed
echo Failed to install "%SERVICE_NAME%" service.  Refer to log in %PR_LOGPATH%
goto end
 
:installed
echo The Service "%SERVICE_NAME%" has been installed
 
:end