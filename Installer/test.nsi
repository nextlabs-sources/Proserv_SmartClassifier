; build.nsi
;
; This script attempts to test most of the functionality of the NSIS exehead.

;--------------------------------

!ifdef HAVE_UPX
!packhdr tmp.dat "upx\upx -9 tmp.dat"
!endif

!ifdef NOCOMPRESS
SetCompress off
!endif

!include 'nsdialogs.nsh'
!include 'nsdialogs_createIPaddress.nsh'

;--------------------------------

Name "Nextlabs Smart Classifier"
Caption "Nextlabs Smart Classifier"
Icon "nxce.ico"
OutFile "Smart Classifier.exe"

SetDateSave on
SetDatablockOptimize on
CRCCheck on
SilentInstall normal
BGGradient 000000 008000 FFFFFF
InstallColors FF8080 000030
XPStyle on

InstallDir "C:\Smart Classifier"
InstallDirRegKey HKLM "Software\Nextlabs\Smart Classifier" "Install_Dir"

CheckBitmap "${NSISDIR}\Contrib\Graphics\Checks\classic-cross.bmp"

LicenseText "Please read the following license agreement carefully"
LicenseData "license.txt"

RequestExecutionLevel admin

;--------------------------------
Page license
Page components
Page Custom MyDialogePre MyDialogeAfter
Page Custom MyNextPage
Page directory
Page instfiles


;UninstPage uninstConfirm
;UninstPage instfiles

;-----------------------------------------
!ifndef NOINSTTYPES ; only if not defined
  InstType "All"
  InstType "Server"
  InstType "NextLabs-Queue"
!endif

AutoCloseWindow false
ShowInstDetails show

;--------------------------------

!define PackagerDir "D:\P4\sgdev_LEO\ProfessionalServices\SmartClassifier2"
!define ExternalDir "D:\P4\sgdev_LEO\external\"
!define COMPANYNAME "NextLabs"


Section "Java 1.7 (64bit)"
	SectionIn RO
	
	;SetOutPath "$INSTDIR\java1.7\"
	;File /r "${ExternalDir}\j2sdk\\jdk1.7.0_75\*"
	;CreateDirectory "$INSTDIR\java1.7\"
	
	; include for some of the windows messages defines
   !include "winmessages.nsh"
   ; HKLM (all users) vs HKCU (current user) defines
   !define env_hklm 'HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"'
   !define env_hkcu 'HKCU "Environment"'
   ; set variable
   WriteRegExpandStr ${env_hklm} "SC_JAVA_HOME" "$INSTDIR\java1.7\"
   ; make sure windows knows about the change
   SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
   SetEnv::SetEnvVar "SC_JAVA_HOME" "$INSTDIR\java1.7\"
	
SectionEnd

Section "NextLabs-Queue"

SectionIn 1 2 3
  
  SetOutPath "$INSTDIR\NextLabs-Indexer"
	;File /r "${PackagerDir}\Indexer\build\NextLabs-Indexer\*"
	CreateDirectory "$INSTDIR\NextLabs-Indexer"
	SetOutPath "$INSTDIR\NextLabs-Indexer\setup"
	ExpandEnvStrings $0 %COMSPEC%
	nsExec::ExecToStack '"$0" /C "$INSTDIR\NextLabs-Indexer\setup\installQueue.bat"'
	
	nsisXML::create
	nsisXML::load "$INSTDIR\NextLabs-Indexer\conf\systemconfig.xml"
	nsisXML::select '/system-configuration/jnp-host/value'
	nsisXML::getText
	MessageBox MB_OK $3
	

SectionEnd


Var IPAddressControl
Var IPADR
 
Function MyDialogePre
  nsDialogs::Create 1018
  Pop $R0
 
  ${If} $R0 == error
    Abort
  ${EndIf}
 
  ; This would more appropriately be called in .onGUIInit
  ${NSD_InitIPaddress}
  Pop $0
 
  IntCmp $0 0 0 +3 +3
  MessageBox MB_OK "Something went wrong while initializing the IPaddress control"
  Abort
 
  ${NSD_CreateLabel} 0u 0 50% 10% "Input IP address:"
  Pop $0
  ${NSD_CreateIPaddress} 5% 90% 30% 12u ""
  Pop $IPAddressControl
  ${NSD_SetText} $IPAddressControl "192.168.1.1"
  Pop $IPAddressControl
  
  nsDialogs::Show
FunctionEnd
 
Function MyDialogeAfter
  ${NSD_GetText} $IPAddressControl $IPADR
  MessageBox MB_OK "IP=$IPADR: Here you could save IP-address to file..."
FunctionEnd
 
Function MyNextPage
  MessageBox MB_OK "(IP=$IPADR): Do some more stuff here..."
FunctionEnd
 
Section
SectionEnd

Section
SectionEnd



















