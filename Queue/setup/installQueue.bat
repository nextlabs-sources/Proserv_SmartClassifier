@echo off
set SERVICE_NAME=Smart Classifier Queue
set HOME_DIR=%~dp0..
set LIB_HOME=%HOME_DIR%\lib
set INSTANCE_DIR=%HOME_DIR%\bin\sc
set INSTANCE_ETC=%INSTANCE_DIR%\etc
set DATA_DIR=%INSTANCE_DIR%\data
set INSTANCE_URI=file:/%INSTANCE_DIR%
set INSTANCE_ETC_URI=file:/%INSTANCE_ETC%
 
set PR_INSTALL=%~dp0%SERVICE_NAME%.exe
 
@REM Service Log Configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%INSTANCE_DIR%\log
set PR_STDOUTPUT=
set PR_STDERROR=auto
set PR_LOGLEVEL=Error
 
@REM Path to Java Installation
set PR_JVM=%SC_JAVA_HOME%\bin\server\jvm.dll
set PR_CLASSPATH=%LIB_HOME%\*
 
@REM JVM Configuration
set PR_JVMMS=256
set PR_JVMMX=3096
set PR_JVMSS=3096
set PR_JVMOPTIONS='-Xbootclasspath/a:%HOME_DIR%\lib\jboss-logmanager-2.1.10.Final.jar;%HOME_DIR%\lib\wildfly-common-1.5.2.Final.jar';-XX:+UseParallelGC;-Dartemis.home=%HOME_DIR%;-Dartemis.instance=%INSTANCE_DIR%;-Ddata.dir=%DATA_DIR%;-Dartemis.instance.etc=%INSTANCE_ETC%;-Djava.util.logging.manager=org.jboss.logmanager.LogManager;-Dlogging.configuration=%INSTANCE_ETC_URI%/logging.properties;-Djava.security.auth.login.config=%INSTANCE_ETC%/login.config;-Dhawtio.disableProxy=true;-Dhawtio.realm=activemq;-Dhawtio.offline=true;-Dhawtio.role=amq;-Dhawtio.rolePrincipalClasses=org.apache.activemq.artemis.spi.core.security.jaas.RolePrincipal;-Djolokia.policyLocation=%INSTANCE_ETC_URI%/jolokia-access.xml
@REM Startup Configuration
set START_CLASS=org.apache.activemq.artemis.boot.Artemis
set START_PARAMS=run

set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=%START_CLASS%
 
@REM Shutdown Configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=%START_CLASS%
set PR_STOP_PARAMS=stop

"%PR_INSTALL%" //IS/"%SERVICE_NAME%" ^
  --DisplayName="%SERVICE_NAME%" ^
  --Description="Runs the Queue component of Smart Classifier" ^
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
  --StartParams="%START_PARAMS%" ^
  --StopMode="%PR_STOPMODE%" ^
  --StopClass="%PR_STOPCLASS%" ^
  --StopParams="%PR_STOP_PARAMS%"
  
if not errorlevel 1 goto installed
echo Failed to install "%SERVICE_NAME%" service.  Refer to log in %PR_LOGPATH%
goto end
 
:installed
echo The Service "%SERVICE_NAME%" has been installed
 
:end