@echo off
set SERVICE_NAME=Smart Classifier Content Extractor
set HOME_DIR=%~dp0..
set LIB_HOME=%HOME_DIR%\xlib
 
set PR_INSTALL=%CD%\%SERVICE_NAME%.exe
 
@REM Service Log Configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%HOME_DIR%\logs
set PR_STDOUTPUT=
set PR_STDERROR=
set PR_LOGLEVEL=Error
 
@REM Path to Java Installation
set PR_JVM=%SC_JAVA_HOME%\bin\server\jvm.dll
@ REM set PR_CLASSPATH=%HOME_DIR%/nextlabs-extractor.jar
set PR_CLASSPATH=%LIB_HOME%\*;%HOME_DIR%\nextlabs-extractor.jar
 
@REM JVM Configuration
set PR_JVMMS=1536
set PR_JVMMX=4096
set PR_JVMSS=3096
set PR_JVMOPTIONS=-Dconfig.folder=%HOME_DIR%\conf\;-Dcom.sun.management.jmxremote.port=8803;-Dcom.sun.management.jmxremote.ssl=false;-Dcom.sun.management.jmxremote.authenticate=false;-XX:+OptimizeStringConcat;-Dlog4j2.configurationFile=%HOME_DIR%\conf\log4j2.xml

@REM Startup Configuration
set START_CLASS=com.nextlabs.smartclassifier.ServiceHook
 
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=%START_CLASS%
set PR_STARTMETHOD=start
 
@REM Shutdown Configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=%START_CLASS%
set PR_STOPMETHOD=stop
set PR_STOPTIMEOUT=60
 
"%PR_INSTALL%" //IS/"%SERVICE_NAME%" ^
  --DisplayName="%SERVICE_NAME%" ^
  --Description="Runs the Extractor component of Smart Classifier" ^
  --ServiceUser LocalSystem  ^
  --Install="%PR_INSTALL%" ^
  --Startup="%PR_STARTUP%" ^
  --LogPath="%PR_LOGPATH%" ^
  --LogPrefix="%PR_LOGPREFIX%" ^
  --LogLevel="%PR_LOGLEVEL%" ^
  --StdOutput="%PR_STDOUTPUT%" ^
  --StdError="%PR_STDERROR%" ^
  --Jvm="%PR_JVM%" ^
  --JvmMs="%PR_JVMMS%" ^
  --JvmMx="%PR_JVMMX%" ^
  --JvmSs="%PR_JVMSS%" ^
  --JvmOptions="%PR_JVMOPTIONS%" ^
  --Classpath="%PR_CLASSPATH%" ^
  --StartMode="%PR_STARTMODE%" ^
  --StartClass="%START_CLASS%" ^
  --StartMethod="%PR_STARTMETHOD%" ^
  --StopMode="%PR_STOPMODE%" ^
  --StopClass="%PR_STOPCLASS%" ^
  --StopMethod="%PR_STOPMETHOD%" ^
  --StopTimeout="%PR_STOPTIMEOUT%"
  
if not errorlevel 1 goto installed
echo Failed to install "%SERVICE_NAME%" service.  Refer to log in %PR_LOGPATH%
goto end
 
:installed
echo The Service "%SERVICE_NAME%" has been installed
 
:end
