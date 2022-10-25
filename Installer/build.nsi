; build.nsi
;
; This script attempts to test most of the functionality of the NSIS exehead.

;-----------------------------------------------------
;Includes

  !include "MUI2.nsh"
  !include 'nsdialogs.nsh'
  !include 'LogicLib.nsh'
  !include 'sections.nsh'
  !include "StrRep.nsh"
  !include "ReplaceInFile.nsh"
  !include "TextFunc.nsh"
  !include "winmessages.nsh"
  !include "StrCharStrip.nsh"
  !include "URLEncode.nsh"
  ;!include "textlog.nsh"
  
;--------------------------------
;Interface Settings

  !define MUI_ICON "nxce.ico"
  !define MUI_UNICON "nxce.ico"
  !define MUI_COMPONENTSPAGE_NODESC
  !define MUI_FINISHPAGE_NOAUTOCLOSE
  !define MUI_UNFINISHPAGE_NOAUTOCLOSE
  !define MUI_ABORTWARNING
  !define MUI_ABORTWARNING_CANCEL_DEFAULT
  !define MUI_UNABORTWARNING
  !define MUI_UNABORTWARNING_CANCEL_DEFAULT
 
;--------------------------------
; This isnt used for now.

 !ifdef HAVE_UPX
  !packhdr "$%TEMP%\tmp.dat" '"upx\upx -9 tmp.dat" "$%TEMP%\tmp.dat"'
 !endif

 !ifdef NOCOMPRESS
  SetCompress off
 !endif

 
;--------------------------------
; Versioning
  !define InstFile "$%PRODUCT_INSTALLER_FILENAME%"
  !define COMPANY_NAME "Nextlabs"
  !define PRODUCT_NAME "Smart Classifier"
  !define PRODUCT_VERSION "2022.03"
  !define FULL_PRODUCT_NAME "${PRODUCT_NAME} ${PRODUCT_VERSION}"

;--------------------------------
; Defines

  !define FILE_WATCHER "File Watcher"
  !define QUEUE "Queue"
  !define EXTRACTOR "Content Extractor"
  !define INDEXER "Indexer"
  !define RULE_ENGINE "Rule Engine"
  !define PLUGINS "plugins"
  !define WEBUI "WebUI"
  !define WEBAPPS "webapps"
  !define INSTALLER "Installer"
  !define COMMON "Common"
  !define CAS "cas"
  !define LICENSE_CHECKER "LicenseChecker"
  !define DB_CONNECTION_TEST "DBConnectionTest"
  !define JAVA "Java11"
  
  !define LVM_GETITEMTEXT 0x102D

  !define PackagerDir "$%WORKSPACE%"
  !define ExternalDir "$%NLEXTERNALDIR%"

  !define env_hklm 'HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"'
  !define env_hkcu 'HKCU "Environment"'

  !define REG_INSTALL "Software\Nextlabs\SmartClassifier"

  ; this is done to add entry to add remove programs.
  !define REG_UNINSTALL "Software\Microsoft\Windows\CurrentVersion\Uninstall\SmartClassifier"
 
;--------------------------------
; General

  Name "$%PRODUCTNAME%"

  OutFile "${InstFile}"
  BrandingText "${FULL_PRODUCT_NAME}"
  RequestExecutionLevel admin
  SetDateSave on
  SetDatablockOptimize on
  CRCCheck force
  SilentInstall normal
  XPStyle on

  InstallDir "C:\NextLabs\Smart Classifier"
  InstallDirRegKey HKLM ${REG_INSTALL} "Install_Dir"
  
  AutoCloseWindow false
  ShowInstDetails show

;---------------------------------------------------------------------
; Variables
  
    Var Dialog

    Var LicenceDialogLabel
    Var LicenceTextBox
    Var LicenceBrowseButton
    Var LicenceTextBoxState
    Var path

    Var DBURLLabel
    Var DBURLTextBox
    Var DBURLState
    Var DBUsernameLabel
    Var DBUsernameTextBox
    Var DBUsernameState
    Var DBPasswordLabel
    Var DBPasswordTextBox
    Var DBPasswordState

    Var trimmedURL
    Var trimmedUsername
    Var trimmedPassword
    Var trimmedString
    Var trimmedPath

    Var HostNameLabel
    Var HostNameTextBox
    Var HostNameTextBoxState

    Var QueuePortNumberLabel
    Var QueuePortNumberTB
    Var QueuePortNumberState

    Var IndexerLabel
    Var IndexerPortNumberTB
    Var IndexerPortNumberState

    Var JMSLabel
    Var JMSDropList
    Var JMSDropListState

    Var LocalJMSURL

    Var HOSTNAME

    Var DBURL
    Var DBDriver
    Var DBUsername
    Var DBPasswordEncrypted

    Var STR_HAYSTACK
    Var STR_NEEDLE
    Var STR_CONTAINS_VAR_1
    Var STR_CONTAINS_VAR_2
    Var STR_CONTAINS_VAR_3
    Var STR_CONTAINS_VAR_4
    Var STR_RETURN_VAR

    Var LicenseExpiryDate
    Var DataSize

    Var uninstallLogFileName

    Var isInstalled
    Var DBURL_QuotesOnSemiColon
    Var TempStr
		
	Var upgrade
	Var currentVersion

    Var DBDriverTemp
    Var DBURLTemp
    Var DBUsernameTemp
    Var DBEncPassTemp

    Var install_log_file

    Var serverName
    Var serverConfigPath
    Var UIFound
	
	Var InstallDir_Enc
;--------------------------------
; Page Descriptions
  
  LangString LICENCE_PAGE_TITLE ${LANG_ENGLISH} "Smart Classifier License File"
  LangString LICENCE_PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter the location of the Smart Classifier license file"
  
  LangString DB_PAGE_TITLE ${LANG_ENGLISH} "Database Information"
  LangString DB_PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter the database connection URL, the username and the password."
  
  LangString HOSTNAME_PAGE_TITLE ${LANG_ENGLISH} "Hostname"
  LangString HOSTNAME_PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter the hostname of this machine"
  
  LangString Q_PAGE_TITLE ${LANG_ENGLISH} "Queue Port Number"
  LangString Q_PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter the port number to be used by the Queue."
  
  LangString INDEXER_PAGE_TITLE ${LANG_ENGLISH} "Indexer Port Number"
  LangString INDEXER_PAGE_SUBTITLE ${LANG_ENGLISH} "Please enter the port number to be used by the Indexer."
  
  LangString JMS_PAGE_TITLE ${LANG_ENGLISH} "Queue URL"
  LangString JMS_PAGE_SUBTITLE ${LANG_ENGLISH} "Please select the Queue URL from the dropdown list."
  
;--------------------------------
;Pages
  
  ; Part1: Install Java and common needed for checking licence file

  !define MUI_WELCOMEPAGE_TITLE "${FULL_PRODUCT_NAME} Installer "
  !define MUI_WELCOMEPAGE_TEXT "Welcome to the ${FULL_PRODUCT_NAME} setup. $\n$\nThe NextLabs Smart Classifier enables you to search for files based on the filename, file content, or metadata, and to classify, protect, and manage those files based on rules that you define. $\n$\nSmart Classifier can insert explicit classifications into files and help improve the data condition prior to deploying any information risk policies. $\n$\nSmart Classifier retrieves a list of matching files based on the rules that you define. You can then apply actions such as copy, move, delete, classify, remove classification, protect, remove protection, or notify using emails."
  !define MUI_PAGE_CUSTOMFUNCTION_PRE skipIfUpgrade
  !insertmacro MUI_PAGE_WELCOME
  
  !define MUI_LICENSEPAGE_CHECKBOX
  !insertmacro MUI_PAGE_LICENSE "license.rtf"

  !define MUI_PAGE_CUSTOMFUNCTION_PRE checkInstallDir
  !define MUI_PAGE_CUSTOMFUNCTION_SHOW showDirectoryPage
  !insertmacro MUI_PAGE_DIRECTORY
  
  ; un-select main components before installing Java and common
  !define MUI_PAGE_CUSTOMFUNCTION_PRE unSelectSections
  !insertmacro MUI_PAGE_INSTFILES
  
  Page custom getLicenceFile checkLicenceFile
  
  ; Part2: If licence file is valid, ask for components to be installed.

  Page Custom getDBInfo checkDBInfo

  !define MUI_PAGE_CUSTOMFUNCTION_PRE selectSections
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE checkJMSStatus 
  !insertmacro MUI_PAGE_COMPONENTS
  
  Page Custom getHostname checkHostName
  Page Custom getQueuePort checkQueuePort
  Page Custom getIndexerPort checkIndexerPort
  Page Custom getJMS checkJMS
  
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE postInstall
  !insertmacro MUI_PAGE_INSTFILES
  
  !define MUI_FINISHPAGE_CANCEL_ENABLED
  !define MUI_FINISHPAGE_SHOWREADME $install_log_file
  !define MUI_FINISHPAGE_SHOWREADME_TEXT "Show Installation Log"
  !define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
  !define MUI_FINISHPAGE_NOREBOOTSUPPORT
  !insertmacro MUI_PAGE_FINISH
  
  # Uninstaller
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  
  !define MUI_PAGE_CUSTOMFUNCTION_PRE un.selectSections
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE un.checkJMSStatus
  !insertmacro MUI_UNPAGE_COMPONENTS
  
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE un.postInstall
  !insertmacro MUI_UNPAGE_INSTFILES
  
  !define MUI_FINISHPAGE_CANCEL_ENABLED
  !define MUI_FINISHPAGE_SHOWREADME $uninstallLogFileName
  !define MUI_FINISHPAGE_SHOWREADME_TEXT "Show Uninstall Log"
  !define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED 
  !insertmacro MUI_UNPAGE_FINISH


;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"
  
;--------------------------------
;Macros

  !define Trim "!insertmacro Trim"  
  !macro Trim ResultVar String
    Push "${String}"
    !ifdef __UNINSTALL__
        Call un.Trim
    !else
        Call Trim
    !endif

    Pop "${ResultVar}"
  !macroend
  
    ; Trim
  ;   Removes leading & trailing whitespace from a string
  ; Usage:
  ;   Push 
  ;   Call Trim
  ;   Pop
  !macro Func_Trim un
      Function ${un}Trim
            Exch $R1 ; Original string
            Push $R2

          Loop:
            StrCpy $R2 "$R1" 1
            StrCmp "$R2" " " TrimLeft
            StrCmp "$R2" "$\r" TrimLeft
            StrCmp "$R2" "$\n" TrimLeft
            StrCmp "$R2" "$\t" TrimLeft
            GoTo Loop2
          TrimLeft:
            StrCpy $R1 "$R1" "" 1
            Goto Loop

          Loop2:
            StrCpy $R2 "$R1" 1 -1
            StrCmp "$R2" " " TrimRight
            StrCmp "$R2" "$\r" TrimRight
            StrCmp "$R2" "$\n" TrimRight
            StrCmp "$R2" "$\t" TrimRight
            GoTo Done
          TrimRight:
            StrCpy $R1 "$R1" -1
            Goto Loop2

          Done:
            Pop $R2
            Exch $R1
      FunctionEnd
  !macroend
  !insertmacro Func_Trim ""
  !insertmacro Func_Trim "un."

 !define StrTrimNewLines "!insertmacro StrTrimNewLines"
 !macro StrTrimNewLines ResultVar String
   Push "${String}"
  
  !ifdef __UNINSTALL__
    Call un.StrTrimNewLines
  !else
    Call StrTrimNewLines
  !endif
  
  Pop ${ResultVar}
 !macroend

 !macro StrTrimNewLine un
   Function ${un}StrTrimNewLines
   /* After this point:
     ------------------------------------------
     $R0 = String (input)
     $R1 = TrimCounter (temp)
     $R2 = Temp (temp)*/

     ;Get input from user
     Exch $R0
     Push $R1
     Push $R2

     ;Initialize trim counter
     StrCpy $R1 0

     loop:
     ;Subtract to get "String"'s last characters
     IntOp $R1 $R1 - 1

     ;Verify if they are either $\r or $\n
     StrCpy $R2 $R0 1 $R1
     ${If} $R2 == `$\r`
     ${OrIf} $R2 == `$\n`
       Goto loop
     ${EndIf}

     ;Trim characters (if needed)
     IntOp $R1 $R1 + 1
     ${If} $R1 < 0
       StrCpy $R0 $R0 $R1
     ${EndIf}

   /*After this point:
     ------------------------------------------
     $R0 = ResultVar (output)*/

     ;Return output to user
     Pop $R2
     Pop $R1
     Exch $R0
   FunctionEnd
 !macroend
 
  !insertmacro StrTrimNewLine ""
  !insertmacro StrTrimNewLine "un."

 !define StrTok "!insertmacro StrTok"
 !macro StrTok ResultVar String Separators ResultPart SkipEmptyParts
   Push "${String}"
   Push "${Separators}"
   Push "${ResultPart}"
   Push "${SkipEmptyParts}"
   Call StrTok
   Pop "${ResultVar}"
 !macroend

  !define StrContains '!insertmacro "_StrContainsConstructor"'
  !macro _StrContainsConstructor OUT NEEDLE HAYSTACK
    Push `${HAYSTACK}`
    Push `${NEEDLE}`
    Call StrContains
    Pop `${OUT}`
  !macroend

  !define readHostName "!insertmacro readHostName"
  !macro readHostName
      !ifdef __UNINSTALL__
          Call un.getHostNameFromRegistry
      !else
          Call getHostNameFromRegistry
      !endif
  !macroend
  !macro getHostNameFromRegistry un
    Function ${un}getHostNameFromRegistry
      ReadRegStr $0 HKLM "SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" "Hostname"
      StrCmp $0 "" win9x
      StrCpy $HOSTNAME $0
	  
	  ReadRegStr $0 HKLM "SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" "Domain"
      StrCpy $HOSTNAME $HOSTNAME.$0
             
      Goto done
      
      win9x:
        ReadRegStr $0 HKLM "System\CurrentControlSet\Control\ComputerName\ComputerName" "ComputerName"
        ;StrCpy $1 $0 4 3
        StrCpy $HOSTNAME $0
      
      done:
    FunctionEnd
  !macroend
  !insertmacro getHostNameFromRegistry ""  
  !insertmacro getHostNameFromRegistry "un." 

 !macro ExecTimeout commandline timeout_ms terminate var_exitcode
   Timeout::ExecTimeout '${commandline}' '${timeout_ms}' '${terminate}'
   Pop ${var_exitcode}
 !macroend

 !define ExecTimeout "!insertmacro ExecTimeout"

  !macro GET_STRING_TOKEN INPUT PART
    Push $R0
    Push $R1
    Push $R2
   
  ; R0 = indice di scorrimento stringa
  ; R0 = index of current position in the string
    StrCpy 	$R0 -1
  ; R1 = indice del carattere " da trovare
  ; R1 = index of '"' character to be found
    IntOp  	$R1 ${PART} * 2
    IntOp  	$R1 $R1 - 1
   
  ; cerco il " che indica l'inizio della sottostringa di interesse
  ; searching '"' character beginning the sub-string
  findStart_loop_${PART}:						
   
    IntOp  	$R0 $R0 + 1 					; i++
    StrCpy	$R2 ${INPUT} 1 $R0				; getting next character
    StrCmp 	$R2 "" error_${PART}
    StrCmp 	$R2 '"' 0 findStart_loop_${PART}
   
    IntOp 	$R1 $R1 - 1
    IntCmp 	$R1 0 0 0 findStart_loop_${PART}		
   
  ; salvo in R1 l'indice di inizio della sottostringa di interesse
  ; storing in R1 the index beginning the sub-string
    IntOp 	$R1 $R0 + 1
   
  ; cerco il " successivo, che indica la fine della stringa di interesse
  ; searching '"' character ending the sub-string
  findEnd_loop_${PART}:						
    IntOp  	$R0 $R0 + 1 					; i++
    StrCpy	$R2 ${INPUT} 1 $R0				; getting next character
    StrCmp 	$R2 "" error_${PART}
    StrCmp 	$R2 '"' 0 findEnd_loop_${PART}
   
  ; R0 = indice di fine della sottostringa di interesse
  ; R0 = the index ending the sub-string
    IntOp 	$R0 $R0 - $R1					
  ; salvo in R0 la lunghezza della sottostringa di interesse
  ; storing in R0 the sub-string's length
    StrCpy 	$R0 ${INPUT} $R0 $R1
    Goto 		done_${PART}
   
   
  error_${PART}:
    StrCpy 	$R0 error
   
   
  done_${PART}:
    Pop 		$R2
    Pop 		$R1
    Exch 		$R0
  !macroend

  !macro DumpLog un
    Function ${un}DumpLog
      Exch $5
      Push $0
      Push $1
      Push $2
      Push $3
      Push $4
      Push $6
     
      FindWindow $0 "#32770" "" $HWNDPARENT
      GetDlgItem $0 $0 1016
      StrCmp $0 0 exit
      FileOpen $5 $5 "w"
      StrCmp $5 "" exit
        SendMessage $0 ${LVM_GETITEMCOUNT} 0 0 $6
        System::Alloc ${NSIS_MAX_STRLEN}
        Pop $3
        StrCpy $2 0
        System::Call "*(i, i, i, i, i, i, i, i, i) i \
          (0, 0, 0, 0, 0, r3, ${NSIS_MAX_STRLEN}) .r1"
        loop: StrCmp $2 $6 done
          System::Call "User32::SendMessageA(i, i, i, i) i \
            ($0, ${LVM_GETITEMTEXT}, $2, r1)"
          System::Call "*$3(&t${NSIS_MAX_STRLEN} .r4)"
          FileWrite $5 "$4$\r$\n"
          IntOp $2 $2 + 1
          Goto loop
        done:
          FileClose $5
          System::Free $1
          System::Free $3
      exit:
        Pop $6
        Pop $4
        Pop $3
        Pop $2
        Pop $1
        Pop $0
        Exch $5
    FunctionEnd
  !macroend
  
  !insertmacro DumpLog ""
  !insertmacro DumpLog "un."
  
  ### TimeStamp
  !ifndef TimeStamp
      !define TimeStamp "!insertmacro _TimeStamp"
      !macro _TimeStamp FormatedString
          !ifdef __UNINSTALL__
              Call un.__TimeStamp
          !else
              Call __TimeStamp
          !endif
          Pop ${FormatedString}
      !macroend
 
  !macro __TimeStamp UN
  Function ${UN}__TimeStamp
      ClearErrors
      ## Store the needed Registers on the stack
          Push $0 ; Stack $0
          Push $1 ; Stack $1 $0
          Push $2 ; Stack $2 $1 $0
          Push $3 ; Stack $3 $2 $1 $0
          Push $4 ; Stack $4 $3 $2 $1 $0
          Push $5 ; Stack $5 $4 $3 $2 $1 $0
          Push $6 ; Stack $6 $5 $4 $3 $2 $1 $0
          Push $7 ; Stack $7 $6 $5 $4 $3 $2 $1 $0
          ;Push $8 ; Stack $8 $7 $6 $5 $4 $3 $2 $1 $0
   
      ## Call System API to get the current system Time
          System::Alloc 16
          Pop $0
          System::Call 'kernel32::GetLocalTime(i) i(r0)'
          System::Call '*$0(&i2, &i2, &i2, &i2, &i2, &i2, &i2, &i2)i (.r1, .r2, n, .r3, .r4, .r5, .r6, .r7)'
          System::Free $0
   
          IntFmt $2 "%02i" $2
          IntFmt $3 "%02i" $3
          IntFmt $4 "%02i" $4
          IntFmt $5 "%02i" $5
          IntFmt $6 "%02i" $6
   
      ## Generate Timestamp
          ;StrCpy $0 "YEAR=$1$\nMONTH=$2$\nDAY=$3$\nHOUR=$4$\nMINUITES=$5$\nSECONDS=$6$\nMS$7"
          StrCpy $0 "$1$2$3$4$5$6.$7"
   
      ## Restore the Registers and add Timestamp to the Stack
          ;Pop $8  ; Stack $7 $6 $5 $4 $3 $2 $1 $0
          Pop $7  ; Stack $6 $5 $4 $3 $2 $1 $0
          Pop $6  ; Stack $5 $4 $3 $2 $1 $0
          Pop $5  ; Stack $4 $3 $2 $1 $0
          Pop $4  ; Stack $3 $2 $1 $0
          Pop $3  ; Stack $2 $1 $0
          Pop $2  ; Stack $1 $0
          Pop $1  ; Stack $0
          Exch $0 ; Stack ${TimeStamp}
   
  FunctionEnd
  !macroend
  !insertmacro __TimeStamp ""
  !insertmacro __TimeStamp "un."
  !endif
  ###########
  
  !define GetAfterChar "!insertmacro GetAfterChar"
  !macro GetAfterChar ResultVar Seperator Path
  
    Push "${Path}"
    Push "${Seperator}"

    !ifdef __UNINSTALL__
      Call un.GetAfterChar
    !else
      Call GetAfterChar
    !endif

    Pop ${ResultVar}
  !macroend
  
  !macro InsertFunc_GetAfterChar un 
    Function ${un}GetAfterChar
      Exch $0 ; chop char
      Exch
      Exch $1 ; input string
      Push $2
      Push $3
      StrCpy $2 0
      loop:
        IntOp $2 $2 - 1
        StrCpy $3 $1 1 $2
        StrCmp $3 "" 0 +3
          StrCpy $0 ""
          Goto exit2
        StrCmp $3 $0 exit1
        Goto loop
      exit1:
        IntOp $2 $2 + 1
        StrCpy $0 $1 "" $2
      exit2:
        Pop $3
        Pop $2
        Pop $1
        Exch $0 ; output
    FunctionEnd
  !macroend
  
  !insertmacro InsertFunc_GetAfterChar ""
  !insertmacro InsertFunc_GetAfterChar "un."


    !macro InsertFunc_ReplaceLineStr un
        Function ${un}ReplaceLineStr
        Exch $R0 ; string to replace that whole line with
        Exch
        Exch $R1 ; string that line should start with
        Exch
        Exch 2
        Exch $R2 ; file
        Push $R3 ; file handle
        Push $R4 ; temp file
        Push $R5 ; temp file handle
        Push $R6 ; global
        Push $R7 ; input string length
        Push $R8 ; line string length
        Push $R9 ; global

        StrLen $R7 $R1

        GetTempFileName $R4

        FileOpen $R5 $R4 w
        FileOpen $R3 $R2 r

        ReadLoop:
        ClearErrors
         FileRead $R3 $R6
          IfErrors Done

         StrLen $R8 $R6
         StrCpy $R9 $R6 $R7 -$R8
         StrCmp $R9 $R1 0 +3

          FileWrite $R5 "$R0$\r$\n"
          Goto ReadLoop

          FileWrite $R5 $R6
          Goto ReadLoop

        Done:

        FileClose $R3
        FileClose $R5

        SetDetailsPrint none
         Delete $R2
         Rename $R4 $R2
        SetDetailsPrint both

        Pop $R9
        Pop $R8
        Pop $R7
        Pop $R6
        Pop $R5
        Pop $R4
        Pop $R3
        Pop $R2
        Pop $R1
        Pop $R0
        FunctionEnd
    !macroend
    !insertmacro InsertFunc_ReplaceLineStr ""
    !insertmacro InsertFunc_ReplaceLineStr "un."

    !define replaceLineInFile "!insertmacro replaceLineInFile"
    !macro replaceLineInFile FileName Str RepStr
        Push "${FileName}" ; file to modify
        Push "${Str}" ; string that a line must begin with *WS Sensitive*
        Push "${RepStr}" ; string to replace whole line with
        !ifdef __UNINSTALL__
            Call un.ReplaceLineStr
        !else
            Call ReplaceLineStr
        !endif
    !macroend
	
	!macro CreateInternetShortcutWithIcon FILEPATH URL ICONPATH ICONINDEX
		WriteINIStr "${FILEPATH}" "InternetShortcut" "URL" "${URL}"
		WriteINIStr "${FILEPATH}" "InternetShortcut" "IconIndex" "${ICONINDEX}"
		WriteINIStr "${FILEPATH}" "InternetShortcut" "IconFile" "${ICONPATH}"
	!macroend
	
;----------------------------------------------------------------------
; Sections

    Section "-Java SDK 11 (64bit) and Common Library" SEC_JAVA
    ; SectionIn RO
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_JAVA"
        IfErrors NotInstalled Installed

        NotInstalled:
            ; set the registry value
            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_JAVA" ""

            WriteRegStr HKLM "${REG_INSTALL}" "Install_Dir" "$INSTDIR"
            WriteRegStr HKLM "${REG_INSTALL}" "Product_Version" "$%PRODUCTVERSION%"

            ${If} $upgrade == "true"
                SetRegView 32
                DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_JAVA"
            ${Else}
                DetailPrint "$\nStart extracting Java..."
                SetOutPath "$INSTDIR\${JAVA}\"
                File /r "${PackagerDir}\${COMMON}\build\java\*"

                DetailPrint "Finished extracting java."
                DetailPrint "Replacing the JCE files"
                SetOverwrite on
                SetOutPath "$INSTDIR\${JAVA}\lib\security"
                File "${ExternalDir}\j2sdk\UnlimitedJCEPolicyJDK7\*"

                ; write environment variable and make sure windows knows about the change
                WriteRegExpandStr ${env_hklm} "SC_JAVA_HOME" "$INSTDIR\${JAVA}"
                SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

            ${EndIf}

            ; this makes it available to the current installer process
            SetEnv::SetEnvVar "SC_JAVA_HOME" "$INSTDIR\${JAVA}"

            Goto End

    Installed:
        SetOutPath "$INSTDIR\${JAVA}\"
        DetailPrint "Skipping the installation of Java section, java (for smart classifier) is already installed"

    End:
    SectionEnd

    Section "-Common Library" SEC_COMMON
        ;SectionIn RO
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_COMMON"
        IfErrors NotInstalled Installed

        NotInstalled:

        ;if upgrade, first clean up
        ${If} $upgrade == "true"
           SetRegView 32
           DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_COMMON"
           RMDir /r "$INSTDIR\${COMMON}"
        ${EndIf}

        SetRegView 64
        WriteRegStr HKLM "${REG_INSTALL}" "SEC_COMMON" ""

        DetailPrint "$\nStart extracting common libraries..."

        SetOutPath "$INSTDIR\${COMMON}"
        File "nxce.ico"

        SetOutPath "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}"
        File /r "${PackagerDir}\${COMMON}\DBConnectionTest\*"

        SetOutPath "$INSTDIR\${COMMON}\${LICENSE_CHECKER}"
        File /r "${PackagerDir}\${COMMON}\LicenseChecker\*"

        SetOutPath "$INSTDIR\${COMMON}\lib"
        File /r "${PackagerDir}\${COMMON}\lib\*"

        DetailPrint "Trying to update $INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf\log4j2-test.xml"

        nsisXML::create
        nsisXML::load "$INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf\log4j2-test.xml"
        nsisXML::select '/Configuration/Properties/Property[@name="filename"]'
        nsisXML::setText "$INSTDIR\${INSTALLER}\logs\LicenseCheck.log"
        nsisXML::save "$INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf\log4j2-test.xml"

        DetailPrint "$INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf\log4j2-test.xml was updated."

        DetailPrint "Trying to update $INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf\log4j2-test.xml"

        nsisXML::create
        nsisXML::load "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf\log4j2-test.xml"
        nsisXML::select '/Configuration/Properties/Property[@name="filename"]'
        nsisXML::setText "$INSTDIR\${INSTALLER}\logs\DBConnectionTest.log"
        nsisXML::save "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf\log4j2-test.xml"

        DetailPrint "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf\log4j2-test.xml was updated!"

        ${If} $upgrade != "true"
            DetailPrint "Start writing the uninstaller..."

            ; Add uninstall information to Add/Remove Programs
            ; Required Values
            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayName" "Smart Classifier"
            WriteRegStr HKLM "${REG_UNINSTALL}" "UninstallString" '"$INSTDIR\Uninstall.exe"'

            WriteRegStr HKLM "${REG_UNINSTALL}" "InstallLocation" "$INSTDIR"
            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayIcon" "$INSTDIR\${COMMON}\nxce.ico"
            WriteRegStr HKLM "${REG_UNINSTALL}" "Publisher" "NextLabs, Inc."
            WriteRegStr HKLM "${REG_UNINSTALL}" "InstallSource" "$EXEDIR"
            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayVersion" "$%PRODUCTVERSION%"

            WriteRegStr HKLM "${REG_UNINSTALL}" "InstallLocation" "$INSTDIR"

            ;Under WinXP this creates two separate buttons: "Modify" and "Remove".
            ;"Modify" will run installer and "Remove" will run uninstaller.

            WriteRegDWORD HKLM "${REG_UNINSTALL}" "NoModify" 0
            WriteRegDWORD HKLM "${REG_UNINSTALL}" "NoRepair" 1
            WriteRegStr HKLM "${REG_UNINSTALL}" "ModifyPath" '"$EXEDIR\${InstFile}"'
			
			; write environment variable and make sure windows knows about the change
            WriteRegExpandStr ${env_hklm} "SC_HOME" "$INSTDIR"
            SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

            WriteUninstaller "$INSTDIR\Uninstall.exe"

            SetOutPath "$INSTDIR\${INSTALLER}\sql"
            File /r "${PackagerDir}\${INSTALLER}\sql\*"

            DetailPrint "Copying the installation logs to log_part1.txt"
            CreateDirectory "$INSTDIR\${INSTALLER}\logs"
            StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part1.txt"
            Push $0
            Call DumpLog
            Goto End

        ${Else}
            SetOverwrite off
            SetOutPath "$INSTDIR\${INSTALLER}\sql"
            File /r "${PackagerDir}\${INSTALLER}\sql\*"
            SetOverwrite on
            DetailPrint "Copying the installation logs to upgrade_31_35_p1.txt"
            CreateDirectory "$INSTDIR\${INSTALLER}\logs"
            StrCpy $0 "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p1.txt"
            Push $0
            Call DumpLog
            Goto End
        ${EndIf}

    Installed:
        SetOutPath "$INSTDIR\${INSTALLER}\"
        DetailPrint "Common Library was already found in the system; skipping installation..."

      End:
  SectionEnd

    Section /o "-Database Tables" SEC_DB
        SetRegView 64

        ${If} $upgrade == "true"

            DetailPrint "$\nTrying to read the database details from DBConnection.txt"
            AccessControl::GrantOnFile "$INSTDIR\${INSTALLER}\sql" "(BU)" "FullAccess"
			IfFileExists "$INSTDIR\${INSTALLER}\sql\DBConnection.txt" 0 file_not_found
            FileOpen $4 "$INSTDIR\${INSTALLER}\sql\DBConnection.txt" r
            IfErrors cantOpenTheFile read

            file_not_found:
                MessageBox MB_ICONSTOP "Cannot find the file $INSTDIR\Installer\sql\DBConnection.txt."
                DetailPrint "Cannot find the file $INSTDIR\Installer\sql\DBConnection.txt."
                DetailPrint "Copying the installation logs to upgrade_31_35_p2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
                
            cantOpenTheFile:
                MessageBox MB_ICONSTOP "Can't open $INSTDIR\${INSTALLER}\sql\DBConnection.txt for reading."
                DetailPrint "Can't open $INSTDIR\${INSTALLER}\sql\DBConnection.txt for reading."
                DetailPrint "Copying the installation logs to upgrade_31_35_p2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort

            read:
                FileRead $4 $1
                ${StrTrimNewLines} $DBURLState $1

                FileRead $4 $1
                ${StrTrimNewLines} $DBUsernameState $1

                FileRead $4 $1
                ${StrTrimNewLines} $DBPasswordEncrypted $1

                FileClose $4

        ${Else}
            DetailPrint "$\nEncrypting DB password..."

            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\lib\common-framework.jar";"$INSTDIR\${COMMON}\lib\crypt.jar" com.bluejungle.framework.crypt.Encryptor -w $DBPasswordState"' "" "$INSTDIR\${INSTALLER}\logs\test.txt"
            Pop $0 # return value

            FileOpen $4 "$INSTDIR\${INSTALLER}\logs\test.txt" r
            FileRead $4 $1
                ${StrTrimNewLines} $DBPasswordEncrypted $1
            FileClose $4

            Delete "$INSTDIR\${INSTALLER}\logs\test.txt"
        ${EndIf}

        DetailPrint "Storing DB connection information as environment variables."

        ${StrTok} $2 $DBURLState ";" "0" "1"
        ${StrTok} $3 $DBURLState ";" "1" "1"

        StrCpy $DBURL_QuotesOnSemiColon  "$2';'$3"

        WriteRegExpandStr ${env_hklm} "SC_DB_USERNAME" "$DBUsernameState"
        WriteRegExpandStr ${env_hklm} "SC_DB_PASSWORD" "$DBPasswordEncrypted"
        WriteRegExpandStr ${env_hklm} "SC_DB_URL" "$DBURLState"
        WriteRegExpandStr ${env_hklm} "SC_DB_URL_WITH_QUOTES_ON_SEMICOLON" "$DBURL_QuotesOnSemiColon"
        WriteRegExpandStr ${env_hklm} "SC_DB_DRIVER" "$DBDriver"

        ; make sure windows knows about the change
        SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

        SetEnv::SetEnvVar "SC_DB_USERNAME" "$DBUsernameState"
        SetEnv::SetEnvVar "SC_DB_PASSWORD" "$DBPasswordEncrypted"
        SetEnv::SetEnvVar "SC_DB_URL" "$DBURLState"
        SetEnv::SetEnvVar "SC_DB_URL_WITH_QUOTES_ON_SEMICOLON" "$DBURL_QuotesOnSemiColon"
        SetEnv::SetEnvVar "SC_DB_DRIVER" "$DBDriver"

        DetailPrint "Finished creating database environment variables."

        ${If} $upgrade == "true"
            DetailPrint "Checking if tables in the database are already updated and updating them if they are not..."
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection checkTableExistence $DBDriver $DBURLState $DBUsernameState $DBPasswordEncrypted USERS ENC"' "" ""
            Pop $0
            StrCmp $0 0 NothingToUpdate UpdateNeeded

            UpdateNeeded:
                DetailPrint "Updating the tables now..."
                ; Now try an execute sql3_1.script
                ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\script_3_1.sql" "@hostname" $HostNameTextBoxState
                ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection createTables $DBDriver $DBURLState $DBUsernameState $DBPasswordEncrypted ENC "$INSTDIR\${INSTALLER}\sql\script_3_1.sql"' "" ""
                Pop $0 # return value
                StrCmp $0 0 UpdateSuccess UpdateFailed

                UpdateFailed:
                    MessageBox MB_ICONSTOP "Could not update the tables in the database."
                    DetailPrint "Copying the installation logs to upgrade_31_35_p2.txt"
                    StrCpy $0 "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p2.txt"
                    Push $0
                    Call DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort

                UpdateSuccess:
                    DetailPrint "Tables successfully updated."
				
				DetailPrint "Updating the rules now..."
                ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection update31Rules $DBDriver $DBURLState $DBUsernameState $DBPasswordEncrypted"' "" ""
                Pop $0 # return value
                StrCmp $0 0 UpdateRuleSuccess UpdateRuleFailed

                UpdateRuleFailed:
                    MessageBox MB_ICONSTOP "Could not update the rules in v3.1."
                    DetailPrint "Could not update the rules in v3.1."
                    DetailPrint "Copying the installation logs to upgrade_31_35_p2.txt"
                    StrCpy $0 "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p2.txt"
                    Push $0
                    Call DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort

                UpdateRuleSuccess:
                    DetailPrint "Rules successfully updated."
				
            NothingToUpdate:
                DetailPrint "Tables in the database are already updated to v3.5.4.0. "

        ${Else}

            DetailPrint "Checking if the database tables exist and creating them if they don't..."
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection checkTableExistence $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES NA"' "" ""
            Pop $0 # return value

            StrCmp $0 0 Tables_Exist
            StrCmp $0 1 Tables_DoNotExist

            Tables_Exist:
                DetailPrint "Tables found in the database. Nothing to create..."
                Goto InsertLicense

            Tables_DoNotExist:
                DetailPrint "Creating database tables now..."

                ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\script.sql" "@hostname" $HostNameTextBoxState
                ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection createTables $DBDriver $DBURLState $DBUsernameState $DBPasswordState NA "$INSTDIR\${INSTALLER}\sql\script.sql"' "" ""
                Pop $0 # return value
                StrCmp $0 0 CreateSuccess CreateFailed

                CreateFailed:
                    MessageBox MB_ICONSTOP "Could not create tables in the database."
                    DetailPrint "Copying the installation logs to log_part2.txt"
                    StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                    Push $0
                    Call DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort

                CreateSuccess:
                    DetailPrint "Tables successfully created."

                InsertLicense:

                    DetailPrint "Reading the license properties from $INSTDIR\${INSTALLER}\logs\license.properties"

                    ${ConfigRead} "$INSTDIR\${INSTALLER}\logs\license.properties" "expiration" $R0
                    ${Trim} $LicenseExpiryDate $R0
                    ${ConfigRead} "$INSTDIR\${INSTALLER}\logs\license.properties" "data_size" $R1
                    ${Trim} $DataSize $R1

                    ${If} $DataSize == "null"
                        StrCpy $DataSize "UNLIMITED"
                    ${EndIf}

                    ${If} $DataSize == ""
                        StrCpy $DataSize "UNLIMITED"
                    ${EndIf}

                    ${StrRep} '$0' '$LicenseExpiryDate' '=' ''
                    StrCpy $LicenseExpiryDate $0

                    ${StrRep} '$1' '$DataSize' '=' ''
                    StrCpy $DataSize $1

                    ;MessageBox MB_OK $LicenseExpiryDate
                    ;MessageBox MB_OK $DataSize

                    DetailPrint "Checking if the license information is present in the database..."

                    AccessControl::GrantOnFile "$INSTDIR\${INSTALLER}\logs" "(BU)" "FullAccess"
                    Delete "$INSTDIR\${INSTALLER}\logs\licenseCheck.txt"

                    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection isInfoPresent $DBDriver $DBURLState $DBUsernameState $DBPasswordEncrypted ENC "$INSTDIR\${INSTALLER}\sql\licenseCheck.sql"' "" ""
                    Pop $0
                    StrCmp $0 0 LicenseInfoFound LicenseInfoNotFound

                    LicenseInfoFound:

                        DetailPrint "License Information was found in the database. Updating it now.."

                        ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\licenseUpdate.sql" "expiry_date" $LicenseExpiryDate
                        ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\licenseUpdate.sql" "data_size" $DataSize

                        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState SYSTEM_CONFIGS NULL "$INSTDIR\${INSTALLER}\sql\licenseUpdate.sql"' "" ""
                        Pop $0
                        StrCmp $0 0 Updated FailedToUpdate

                        FailedToUpdate:
                            DetailPrint "Could not update the license information in the database."
                            DetailPrint "Copying the installation logs to log_part2.txt"
                            StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                            Push $0
                            Call DumpLog
                            System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                            Abort

                        Updated:
                            DetailPrint "Successfully updated the license information in the database."
                            Goto End_If

                    LicenseInfoNotFound:

                        DetailPrint "License Information was not found in the database. Inserting it now.."

                        ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\license.sql" "expiry_date" $LicenseExpiryDate
                        ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\license.sql" "data_size" $DataSize

                        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState SYSTEM_CONFIGS NULL "$INSTDIR\${INSTALLER}\sql\license.sql"' "" ""
                        Pop $0
                        StrCmp $0 0 Inserted FailedToInsert

                        FailedToInsert:
                            DetailPrint "Could not insert the license information in the database."
                            DetailPrint "Copying the installation logs to log_part2.txt"
                            StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                            Push $0
                            Call DumpLog
                            System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                            Abort

                        Inserted:
                            DetailPrint "Successfully inserted the license information in the database."

        End_If:
        ${EndIf}
    End:
  SectionEnd

    Section /o "Queue" SEC_QUEUE
        ;SectionIn 1 2
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_QUEUE"
        IfErrors NotInstalled Installed

        NotInstalled:
            DetailPrint "$\nInstalling the queue..."

            WriteRegStr HKLM "${REG_INSTALL}" "SEC_QUEUE" ""

            CreateDirectory "$INSTDIR\${QUEUE}"
            SetOutPath "$INSTDIR\${QUEUE}"
            File /r "${PackagerDir}\${QUEUE}\build\NextLabs-Queue\*"
            AccessControl::GrantOnFile "$INSTDIR\${QUEUE}" "(BU)" "FullAccess"

            DetailPrint "Trying to update $INSTDIR\${QUEUE}\bin\sc\etc\bootstrap.xml"
			
			StrCpy $InstallDir_Enc $INSTDIR 
			Push $InstallDir_Enc
			Call URLEncode
			Pop $0
			StrCpy $InstallDir_Enc $0
			DetailPrint "Done URL Encode from $INSTDIR to $InstallDir_Enc"
			
			${ReplaceInFile} "$INSTDIR\${QUEUE}\bin\sc\etc\bootstrap.xml" "@sc_home" $InstallDir_Enc
			
            DetailPrint "Successfully updated $INSTDIR\${QUEUE}\bin\sc\etc\bootstrap.xml"
			
			DetailPrint "Generating keystore for SSL"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -genkeypair -storetype JKS -alias queue -dname "CN=$HostNameTextBoxState,OU=CompliantEnterprise, O=NextLabs,L=San Mateo,ST=CA,C=US" -keystore "$INSTDIR\${QUEUE}\bin\sc\etc\key.jks" -keypass changeit -storepass changeit -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3650' "" ""
				
			DetailPrint "Private key generated"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -export -alias queue -keystore "$INSTDIR\${QUEUE}\bin\sc\etc\key.jks" -keypass changeit -storepass changeit -rfc -file "$INSTDIR\${QUEUE}\bin\sc\etc\queue.cer"' "" ""
				
			DetailPrint "Cert exported"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool.exe" -import -alias queue -trustcacerts -noprompt -cacerts -storepass changeit -file "$INSTDIR\${QUEUE}\bin\sc\etc\queue.cer"' "" ""
			Pop $0
			StrCmp $0 0 SuccessAdd FailedAdd
			
			SuccessAdd:

				SetOutPath "$INSTDIR\${QUEUE}\bin"
				ExpandEnvStrings $0 %COMSPEC%
				nsExec::ExecToStack '"$0" /C "$INSTDIR\${QUEUE}\bin\installQueue.bat"'

				${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_Insert.sql" "@hostname" $HostNameTextBoxState
				${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_Insert.sql" "61616" $QueuePortNumberState

				DetailPrint "Inserting the queue information in the database..."
				ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES NULL "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_Insert.SQL"' "" ""
				Pop $0 # return value
				StrCmp $0 0 Success Failed

				Failed:
					MessageBox MB_ICONSTOP "Could not insert the queue details in the database."
					DetailPrint "Copying the installation logs to log_part2.txt"
					StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
					Push $0
					Call DumpLog
					System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
					Abort

				Success:
					DetailPrint "Successfully inserted the queue information into the table."
					!insertmacro CreateInternetShortcutWithIcon "$Desktop\NextLabs Queue.URL" "https://localhost:8161/console" "$INSTDIR\${QUEUE}\bin\sc\activemq-logo.ico" 0
					Goto End
			FailedAdd:
				MessageBox MB_ICONSTOP "Could not add queue service cert to cacerts"
				DetailPrint "Copying the installation logs to log_part2.txt"
				StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
				Push $0
				Call DumpLog
				System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
				Abort

        Installed:
            DetailPrint "Smart Classifier queue is already installed, skipping installation..."

        End:
    SectionEnd

    Section /o "Indexer" SEC_INDEXER
    ;SectionIn 1 2
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_INDEXER"
        IfErrors NotInstalled Installed

        NotInstalled:
            DetailPrint "$\nInstalling the indexer..."

            WriteRegStr HKLM "${REG_INSTALL}" "SEC_INDEXER" ""

			SetOverwrite off
            SetOutPath "$INSTDIR\${INDEXER}"
            File /r "${PackagerDir}\Indexer\build\NextLabs-Indexer\*"
			
			DetailPrint "Generating keystore for SSL"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -genkeypair -storetype JKS -alias indexer -dname "CN=$HostNameTextBoxState,OU=CompliantEnterprise, O=NextLabs,L=San Mateo,ST=CA,C=US" -keystore "$INSTDIR\${INDEXER}\server\etc\key.jks" -keypass changeit -storepass changeit -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3650' "" ""
				
			DetailPrint "Private key generated"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -export -alias indexer -keystore "$INSTDIR\${INDEXER}\server\etc\key.jks" -keypass changeit -storepass changeit -rfc -file "$INSTDIR\${INDEXER}\server\etc\indexer.cer"' "" ""
				
			DetailPrint "Cert exported"
			ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool.exe" -import -alias indexer -trustcacerts -noprompt -cacerts -storepass changeit -file "$INSTDIR\${INDEXER}\server\etc\indexer.cer"' "" ""
			Pop $0
			StrCmp $0 0 SuccessAdd FailedAdd
			
			SuccessAdd:
				${ReplaceInFile} "$INSTDIR\${INDEXER}\bin\installIndexer.bat" "8093" $IndexerPortNumberState
				
				AccessControl::GrantOnFile "$INSTDIR\Indexer" "(BU)" "FullAccess"
				SetOverwrite on
				SetOutPath "$INSTDIR\Indexer\bin"
				ExpandEnvStrings $0 %COMSPEC%
				nsExec::ExecToStack '"$0" /C "$INSTDIR\Indexer\bin\installIndexer.bat"'

				DetailPrint "Adding the indexer information to the database..."

				${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\SYSTEM_CONFIGS_Insert.sql" "@hostname" $HostNameTextBoxState
				${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\SYSTEM_CONFIGS_Insert.sql" "8093" $IndexerPortNumberState

				ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState SYSTEM_CONFIGS NULL "$INSTDIR\${INSTALLER}\sql\SYSTEM_CONFIGS_Insert.SQL"' "" ""
				Pop $0 # return value
				StrCmp $0 0 Success Failed

				Failed:
					MessageBox MB_ICONSTOP "Could not insert the indexer details in the database."
					DetailPrint "Copying the installation logs to log_part2.txt"
					StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
					Push $0
					Call DumpLog
					System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
					Abort

				Success:
					DetailPrint "Successfully inserted the indexer information into the table."
					!insertmacro CreateInternetShortcutWithIcon "$Desktop\NextLabs Indexer.URL" "https://$HostNameTextBoxState:$IndexerPortNumberState/solr/#/" "$INSTDIR\${INDEXER}\server\solr-webapp\webapp\favicon.ico" 0
					Goto End
					
			FailedAdd:
				MessageBox MB_ICONSTOP "Could not add indexer cert to cacerts"
				DetailPrint "Copying the installation logs to log_part2.txt"
				StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
				Push $0
				Call DumpLog
				System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
				Abort

        Installed:
            DetailPrint "Smart Classifier Indexer is already installed, skipping installation..."

        End:
    SectionEnd

    Section /o "Rule Engine" SEC_RULE
   ; SectionIn 1 2
    ClearErrors

    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_RULE"
    IfErrors NotInstalled Installed

    NotInstalled:
        DetailPrint "$\nInstalling the Rule Engine..."

        WriteRegStr HKLM "${REG_INSTALL}" "SEC_RULE" ""

        DetailPrint "Copying the files..."

        SetOutPath "$INSTDIR\${RULE_ENGINE}"
        File /r "${PackagerDir}\RuleEngine\build\NextLabs-Rule-Engine\*"
        file "nxce.ico"
      
        AccessControl::GrantOnFile "$INSTDIR\${RULE_ENGINE}" "(BU)" "FullAccess"
      
        DetailPrint "Trying to update $INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"

        nsisXML::create
        nsisXML::load "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"
        nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
        nsisXML::setText $DBURLState

        nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
        nsisXML::setText $DBUsernameState

        nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
        nsisXML::setText $DBPasswordEncrypted

        nsisXML::save "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"

        DetailPrint "Successfully updated $INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml."
      
        DetailPrint "Copying the license file to $INSTDIR\${RULE_ENGINE}\conf"
        CopyFiles /SILENT /FILESONLY '$INSTDIR\${COMMON}\${LICENSE_CHECKER}\license.dat' '$INSTDIR\${RULE_ENGINE}\conf'

        DetailPrint "Installing the Rule Engine service.."
        SetOutPath "$INSTDIR\${RULE_ENGINE}\bin"
        ExpandEnvStrings $0 %COMSPEC%
        nsExec::ExecToStack '"$0" /C "$INSTDIR\${RULE_ENGINE}\bin\install.bat"'

        DetailPrint "Copying the plugins..."

        SetOutPath "$INSTDIR\${RULE_ENGINE}\${PLUGINS}"
        File /r "${PackagerDir}\Plugin\build\NextLabs-Rule-Engine-Plug-in\plugins\*"

        Setoverwrite on
        SetOutPath "$INSTDIR\${RULE_ENGINE}\xlib"
        File /r "${PackagerDir}\Plugin\build\NextLabs-Rule-Engine-Plug-in\xlib\*"

        DetailPrint "Inserting the Rule Engine information in the database..."

        ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\RULE_ENGINES_Insert.sql" "@hostname" $HostNameTextBoxState
        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState RULE_ENGINES NULL "$INSTDIR\${INSTALLER}\sql\RULE_ENGINES_Insert.sql"' "" ""
        Pop $0 # return value
        StrCmp $0 0 SuccessRuleEngine FailedRuleEngine

        FailedRuleEngine:
            MessageBox MB_ICONSTOP "Could not insert the rule engine information in the database."
            DetailPrint "Could not insert the rule engine information in the database..."
            DetailPrint "Copying the installation logs to log_part2.txt"
            StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
            Push $0
            Call DumpLog
            System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
            Abort

        SuccessRuleEngine:
            DetailPrint "Successfully inserted the Rule Engine information in the database."
            Goto End

    Installed:
       DetailPrint "Smart Classifier Rule Engine is already installed, skipping installation..."

    End:
  SectionEnd

    Section /o "File Watcher" SEC_WATCHER
  ;SectionIn 1 3
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_WATCHER"
        IfErrors NotInstalled Installed

        NotInstalled:
            DetailPrint "$\nInstalling the File Watcher..."

            WriteRegStr HKLM "${REG_INSTALL}" "SEC_WATCHER" ""

            DetailPrint "Copying the files..."

            SetOutPath "$INSTDIR\${FILE_WATCHER}"
            File /r "${PackagerDir}\Watcher\build\NextLabs-Watcher\*"
            file "nxce.ico"
            AccessControl::GrantOnFile "$INSTDIR\${FILE_WATCHER}" "(BU)" "FullAccess"
      
            DetailPrint "Updating $INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"
            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::setText $DBURLState

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::setText $DBUsernameState

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::setText $DBPasswordEncrypted

            nsisXML::save "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"

            DetailPrint "Successfully updated $INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"
      
            DetailPrint "Getting the Queue URLS from the database..."
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectFromTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES PROVIDER_URL "$INSTDIR\${INSTALLER}\logs\jms_urls.txt"' "" ""
            Pop $0
            StrCmp $0 0 JMSFound JMSNotFound
    
            JMSFound:
                ClearErrors
                FileOpen $4 "$INSTDIR\${INSTALLER}\logs\jms_urls.txt" r
                IfErrors CantOpenTheFile ReadAgain

            CantOpenTheFile:
                MessageBox MB_ICONSTOP "Can't open the $INSTDIR\${INSTALLER}\logs\jms_urls.txt for reading."
                DetailPrint "Copying the installation logs to log_part2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort

            ReadAgain:
                FileRead $4 $1
                IfErrors FileEnd

                ${StrContains} $0 $JMSDropListState $1
                StrCmp $0 "" ReadAgain

                FileEnd:
                    FileClose $4
          
                ;${StrTok} "ResultVar" "String" "Separators" "ResultPart" "SkipEmptyParts"
                ${StrTok} $2 $1 " " "0" "1"
            
                DetailPrint "The JMS ID for this file watcher will be = $2"

                DetailPrint "Inserting the file watcher information in the database..."

                ;${ReplaceInFile} SOURCE_FILE SEARCH_TEXT REPLACEMENT
                ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\WATCHERS_Insert.sql" "jms_id" $2
                ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\WATCHERS_Insert.sql" "@hostname" $HostNameTextBoxState
      
                ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState WATCHERS NULL "$INSTDIR\${INSTALLER}\sql\WATCHERS_Insert.sql"' "" ""
                Pop $0
                StrCmp $0 0 Success Failure

                Failure:
                    MessageBox MB_ICONSTOP "Could not insert the File Watcher details in the database."
                    DetailPrint "Copying the installation logs to log_part2.txt"
                    StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                    Push $0
                    Call DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort

                Success:
                    DetailPrint "Successfully inserted the watcher information into the table."
                    DetailPrint "Installing the watcher service..."
                    SetOutPath "$INSTDIR\${FILE_WATCHER}\bin"
                    ExpandEnvStrings $0 %COMSPEC%
                    nsExec::ExecToStack '"$0" /C "$INSTDIR\${FILE_WATCHER}\bin\install.bat"'
                    DetailPrint "Successfully installed the watcher service..."
                    Goto End

            JMSNotFound:
                MessageBox MB_ICONSTOP "No JMS information found in the database. Aborting Installation..."
                DetailPrint "No JMS information found in the database. Aborting Installation..."
                DetailPrint "Copying the installation logs to log_part2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort

        Installed:
           DetailPrint "Smart Classifier File Watcher is already installed, skipping installation..."

    End:
  SectionEnd

    Section /o "Content Extractor" SEC_EXTRACTOR
   ; SectionIn 1 3 4
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
        IfErrors NotInstalled Installed

        NotInstalled:
            DetailPrint "$\nInstalling the Content Extractor..."

            WriteRegStr HKLM "${REG_INSTALL}" "SEC_EXTRACTOR" ""

            DetailPrint "Extracting the files..."
            SetOutPath "$INSTDIR\${EXTRACTOR}"
            File /r "${PackagerDir}\Extractor\build\NextLabs-Extractor\*"

            AccessControl::GrantOnFile "$INSTDIR\${EXTRACTOR}" "(BU)" "FullAccess"

            DetailPrint "Updating the $INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::setText $DBURLState

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::setText $DBUsernameState

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::setText $DBPasswordEncrypted

            nsisXML::save "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            DetailPrint "Successfully updated $INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml."

            DetailPrint "Getting the Queue URLs.."
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectFromTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES PROVIDER_URL "$INSTDIR\${INSTALLER}\logs\jms_urls.txt"' "" ""
            Pop $0
            StrCmp $0 0 JMSFound JMSNotFound
      
            JMSFound:
                ClearErrors
                FileOpen $4 "$INSTDIR\${INSTALLER}\logs\jms_urls.txt" r
                IfErrors CantOpenTheFile ReadAgain

                CantOpenTheFile:
                    MessageBox MB_ICONSTOP "Can't open the $INSTDIR\${INSTALLER}\logs\jms_urls.txt for reading."
                    DetailPrint "Can't open the $INSTDIR\${INSTALLER}\logs\jms_urls.txt for reading."
                    DetailPrint "Copying the installation logs to log_part2.txt"
                    StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                    Push $0
                    Call DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort

                ReadAgain:
                    FileRead $4 $1
                    IfErrors File_End

                    ${StrContains} $0 $JMSDropListState $1
                    StrCmp $0 "" ReadAgain

                    File_End:
                        FileClose $4
          
                    ${StrTok} $2 $1 " " "0" "1"
                    DetailPrint "The JMS for this Content Extractor will be = $2"

                    DetailPrint "Inserting the Content Extractor information in the database..."

                    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_Insert.sql" "JMS_ID" $2
                    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_Insert.sql" "@hostname" $HostNameTextBoxState
  
                    DetailPrint "Inserting the Content Extractor information in the database..."
                    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState EXTRACTORS DOCUMENT_SIZE_LIMITS "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_Insert.sql"' "" ""
                    Pop $0
                    StrCmp $0 0 InsertSuccessful InsertFailed
  
                    InsertSuccessful:
                        DetailPrint "Successfully inserted the content extractor information into the database."
            
                        DetailPrint "Installing the Content Extractor service..."
                        SetOutPath "$INSTDIR\${EXTRACTOR}\bin"
                        ExpandEnvStrings $0 %COMSPEC%
                        nsExec::ExecToStack '"$0" /C "$INSTDIR\${EXTRACTOR}\bin\install.bat"'
                        Goto End

                    InsertFailed:
                        MessageBox MB_ICONSTOP "Could not insert the Content Watcher Extractor in the database."
                        DetailPrint "Could not insert the Content Watcher Extractor in the database."
                        DetailPrint "Copying the installation logs to log_part2.txt"
                        StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort

            JMSNotFound:
                MessageBox MB_ICONSTOP "No JMS information found in the database. Aborting Installation..."
                DetailPrint "No JMS information found in the database. Aborting Installation..."
                DetailPrint "Copying the installation logs to log_part2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort

        Installed:
            DetailPrint "Smart Classifier Content Extractor is already installed, skipping installation..."

        End:
    SectionEnd

    Section /o "Web User interface" SEC_UI
    ;SectionIn 1 2
        ClearErrors

        SetRegView 64
        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_UI"
        IfErrors NotInstalled Installed

        NotInstalled:

            DetailPrint "$\nInstalling the Web User Interface..."
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_UI" ""

            DetailPrint "Extracting the files..."
            SetOutPath "$INSTDIR\${WEBUI}"
            File /r "${PackagerDir}\WebUI\build\tomcat\*"
            file "nxce.ico"
      
            DetailPrint "Trying to update $INSTDIR\${WEBUI}\conf\context.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${WEBUI}\conf\context.xml"
      
            nsisXML::select '/Context'
            nsisXML::createElement "ResourceLink"
            nsisXML::setAttribute "name" "jdbc/SmartClassifier"
            nsisXML::setAttribute "global" "jdbc/SmartClassifier"
            nsisXML::setAttribute "auth" "Container"
            nsisXML::setAttribute "type" "javax.sql.DataSource"
            nsisXML::appendChild
      
            nsisXML::release $2
            nsisXML::release $1
            nsisXML::save "$INSTDIR\${WEBUI}\conf\context.xml"
            nsisXML::release $0
          
            DetailPrint "Successfully updated $INSTDIR\${WEBUI}\conf\context.xml"

            DetailPrint "Trying to update $INSTDIR\${WEBUI}\conf\server.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${WEBUI}\conf\server.xml"

            nsisXML::select '/Server/GlobalNamingResources'
            nsisXML::createElement "Resource"
            nsisXML::setAttribute "name" "jdbc/SmartClassifier"
            nsisXML::setAttribute "auth" "Container"
            nsisXML::setAttribute "type" "javax.sql.DataSource"
            nsisXML::setAttribute "maxTotal" "10"
            nsisXML::setAttribute "maxIdle" "2"
            nsisXML::setAttribute "maxWaitMillis" "10000"
            nsisXML::setAttribute "username" $DBUsernameState
            nsisXML::setAttribute "password" $DBPasswordState
            nsisXML::setAttribute "driverClassName" "$DBDriver"
            nsisXML::setAttribute "url" $DBURLState
            nsisXML::setAttribute "validationQuery" "select 1"
            nsisXML::appendChild

            nsisXML::release $2
            nsisXML::release $1
            nsisXML::save "$INSTDIR\${WEBUI}\conf\server.xml"
            nsisXML::release $0
      
            DetailPrint "Successfully updated $INSTDIR\${WEBUI}\conf\server.xml"
          
            DetailPrint "Writing the environment variables..."

            WriteRegExpandStr ${env_hklm} "SC_CATALINA_HOME" "$INSTDIR\${WEBUI}"
            SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

            SetEnv::SetEnvVar "SC_CATALINA_HOME" "$INSTDIR\${WEBUI}"
              
            DetailPrint "Trying to insert web user interface information in the database..."

            ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\WebUI_Insert.sql" "@hostname" $HostNameTextBoxState
			
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection insertIntoTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState SYSTEM_CONFIGS NULL "$INSTDIR\${INSTALLER}\sql\WebUI_Insert.sql"' "" ""
            Pop $0
            StrCmp $0 0 Success Failed

            Failed:
                MessageBox MB_ICONSTOP "Could not insert Web UI information in the database.."
                DetailPrint "Could not insert Web UI information in the database.."
                DetailPrint "Copying the installation logs to log_part2.txt"
                StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
        
            Success:

                DetailPrint "Successfully inserted the Web UI information in the database."

                DetailPrint "Adding the CAS webapp to WEBUI.."
                SetOutPath "$INSTDIR\${WEBUI}\${WEBAPPS}\cas"
                File /r "${PackagerDir}\CASExtension\build\cas\*"

                SetOutPath "$INSTDIR\${WEBUI}\conf"
                File "${PackagerDir}\CASExtension\conf\cas.properties"

                ${StrRep} '$serverName' 'https://localhost' 'localhost' '$HostNameTextBoxState'
                ${StrRep} '$serverConfigPath' 'Path' 'Path' '$INSTDIR\${WEBUI}\conf'

                DetailPrint "Setting the environment variables SC_SERVER_NAME, server.config.path"

                WriteRegExpandStr ${env_hklm} "SC_SERVER_NAME" $serverName
                WriteRegExpandStr ${env_hklm} "server.config.path" $serverConfigPath

                ; make sure windows knows about the change
                SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

                ; make available to the current installer process.
                SetEnv::SetEnvVar "SC_SERVER_NAME" "$serverName"
                SetEnv::SetEnvVar "server.config.path" "$serverConfigPath"

                DetailPrint "Modifying $INSTDIR\${WEBUI}\conf\cas.properties..."

                ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "server.name=" "$serverName" $R0
                ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.driverClass=" "$DBDriver" $R0
                ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.url=" "$DBURLState" $R0
                ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.user=" "$DBUsernameState" $R0
                ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.password=" "$DBPasswordEncrypted" $R0
				
				DetailPrint "Generating keystore for SSL"
				ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -genkeypair -storetype JKS -alias webui -dname "CN=$HostNameTextBoxState,OU=CompliantEnterprise, O=NextLabs,L=San Mateo,ST=CA,C=US" -keystore "$INSTDIR\${WEBUI}\conf\key.jks" -keypass changeit -storepass changeit -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3650' "" ""
				
				DetailPrint "Private key generated"
				ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool" -export -alias webui -keystore "$INSTDIR\${WEBUI}\conf\key.jks" -keypass changeit -storepass changeit -rfc -file "$INSTDIR\${WEBUI}\conf\webui.cer"' "" ""
				
				DetailPrint "Cert exported"
				ExecDos::exec '"$INSTDIR\${JAVA}\bin\keytool.exe" -import -alias webui -trustcacerts -noprompt -cacerts -storepass changeit -file "$INSTDIR\${WEBUI}\conf\webui.cer"' "" ""
				Pop $0
				StrCmp $0 0 SuccessAdd FailedAdd
				
				FailedAdd:
					MessageBox MB_ICONSTOP "Could not add cert to cacerts.."
					DetailPrint "Could not add cert to cacerts..."
					DetailPrint "Copying the installation logs to log_part2.txt"
					StrCpy $0 "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
					Push $0
					Call DumpLog
					System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
					Abort
        
				SuccessAdd:
				
					DetailPrint "Cert added to cacerts"
				
					!insertmacro CreateInternetShortcutWithIcon "$Desktop\SmartClassifier.URL" "https://$HostNameTextBoxState/SmartClassifier" "$INSTDIR\${WEBUI}\nxce.ico" 0
				
					DetailPrint "Installing the WEBUI service..."
					AccessControl::GrantOnFile "$INSTDIR\${WEBUI}\bin" "(BU)" "FullAccess"
					SetOutPath "$INSTDIR\${WEBUI}\"
					ExpandEnvStrings $0 %COMSPEC%
					nsExec::ExecToLog '"$0" /C "$INSTDIR\${WEBUI}\bin\install.bat" install'

					DetailPrint "Starting the WEBUI service..."
					SimpleSC::SetServiceStartType "Smart Classifier Web UI" 2
					SimpleSC::StartService "Smart Classifier Web UI" "" 30
					Goto End

        Installed:
            DetailPrint "Smart Classifier Web UI is already installed, skipping installation..."

    End:
  SectionEnd

    Section /o "Queue upgrade to v3.5" SEC_UPG_QUEUE

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_QUEUE"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:
            DetailPrint "$\nTrying to update Queue service to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier Queue"
            Pop $0 ; returns an error-code (<>0) otherwise success (0)
            Pop $1 ; return the status of the service
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Smart Classifier Queue service..."
                    SimpleSC::StopService "Smart Classifier Queue" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier Queue"
                        Pop $R1
                        Pop $R2
                    ${Else}

                        MessageBox MB_ICONSTOP "Could not stop the Smart Classifier Queue service."
                        DetailPrint "Could not stop the Smart Classifier Queue service."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0

                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier Queue"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Smart Classifier Queue service status"
                        DetailPrint "Could not get the Smart Classifier Queue service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0

                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the status of Smart Classifier Queue service"
                DetailPrint "Could not get the status of Smart Classifier Queue service"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Smart Classifier Queue service has been stopped.."

            DetailPrint "Removing $INSTDIR\${QUEUE}\bin\sc\data"
            RMDir /r "$INSTDIR\${QUEUE}\bin\sc\data"

            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_QUEUE"

            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_QUEUE" ""
            Goto End

        Already_Upgraded:
            DetailPrint "The queue service is already upgraded to v3.5.4.0"

        End:
    SectionEnd

    Section /o "Indexer upgrade to v3.5" SEC_UPG_INDEXER

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_INDEXER"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:
            DetailPrint "$\nTrying to upgrade Indexer to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier Indexer"
            Pop $0 ; success = 0
            Pop $1 ; status, 1 = stopped
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Smart Classifier Indexer service..."
                    SimpleSC::StopService "Smart Classifier Indexer" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier Indexer"
                        Pop $R1
                        Pop $R2
                    ${Else}

                        MessageBox MB_ICONSTOP "Could not stop the Smart Classifier Indexer service."
                        DetailPrint "Could not stop the Smart Classifier Indexer service."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0

                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier Indexer"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Smart Classifier Indexer service status"
                        DetailPrint "Could not get the Smart Classifier Indexer service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0

                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the status of Smart Classifier Indexer service"
                DetailPrint "Could not get the status of Smart Classifier Indexer service"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Smart Classifier Indexer service has been stopped.."

            DetailPrint "Backing up the schema at $INSTDIR\${INDEXER}\server\solr\sc\schema_bak.xml"
            CopyFiles /SILENT /FILESONLY "$INSTDIR\${INDEXER}\server\solr\sc\schema.xml" "$INSTDIR\${INDEXER}\server\solr\sc\schema_bak.xml"

            DetailPrint "Adding the new schema.xml"
            SetOverwrite on
            SetOutPath $INSTDIR\${INDEXER}\xlib\solr\sc\"
            File "${PackagerDir}\Indexer\build\NextLabs-Indexer\server\solr\sc\schema.xml"

            ; delete old registry and write new one
            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_INDEXER"


            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_INDEXER" ""
            Goto End

        Already_Upgraded:
            DetailPrint "The Indexer service is already upgraded to v3.5.4.0"

        End:

    SectionEnd

    Section /o "File Watcher upgrade to v3.5" SEC_UPG_WATCHER

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_WATCHER"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:
            DetailPrint "$\nTrying to update File Watcher to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier File Watcher"
            Pop $0 ; success = 0
            Pop $1 ; status, 1 = stopped
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Smart Classifier File Watcher service.."
                    SimpleSC::StopService "Smart Classifier File Watcher" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier File Watcher"
                        Pop $R1
                        Pop $R2
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not stop the Smart Classifier File Watcher service.."
                        DetailPrint "Could not stop the Smart Classifier File Watcher service.."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0

                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier File Watcher"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Smart Classifier File Watcher service status"
                        DetailPrint "Could not get the Smart Classifier File Watcher service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0

                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the Smart Classifier File Watcher service status"
                DetailPrint "Could not get the Smart Classifier File Watcher service status"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Smart Classifier File Watcher service has been stopped.."

            AccessControl::GrantOnFile "$INSTDIR\${FILE_WATCHER}" "(BU)" "FullAccess"

            DetailPrint "Replacing the Watcher xlib jar files."

            RMDir /r "$INSTDIR\${FILE_WATCHER}\xlib"
            SetOutPath "$INSTDIR\${FILE_WATCHER}\xlib"
            File "${PackagerDir}\Watcher\build\NextLabs-Watcher\xlib\*"

            DetailPrint "Replacing the nextlabs-watcher.jar"
            Delete "$INSTDIR\${FILE_WATCHER}\nextlabs-watcher.jar"
            SetOutPath "$INSTDIR\${FILE_WATCHER}"
            File "${PackagerDir}\Watcher\build\NextLabs-Watcher\nextlabs-watcher.jar"

            ; remove DOCTYPE declaration from hibernate.cfg.xml
            ${replaceLineInFile} "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml" "<!DOCTYPE" ""


            DetailPrint "Backing up the hibernate.cfg.xml to $INSTDIR\${FILE_WATCHER}\conf\hibernate_bak.cfg.xml"
            CopyFiles /SILENT /FILESONLY "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml" "$INSTDIR\${FILE_WATCHER}\conf\hibernate_bak.cfg.xml"

            Delete "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"
            SetOutPath "$INSTDIR\${FILE_WATCHER}\conf"
            File "${PackagerDir}\Watcher\build\NextLabs-Watcher\conf\hibernate.cfg.xml"

            DetailPrint "Reading the database information from hibernate_bak.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${FILE_WATCHER}\conf\hibernate_bak.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::getText
            StrCpy $DBDriverTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::getText
            StrCpy $DBURLTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::getText
            StrCpy $DBUsernameTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::getText
            StrCpy $DBEncPassTemp $3

            DetailPrint "Trying to update the new hibernate.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::setText $DBDriverTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::setText $DBURLTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::setText $DBUsernameTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::setText $DBEncPassTemp

            nsisXML::save "$INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"

            DetailPrint "Successfully updated $INSTDIR\${FILE_WATCHER}\conf\hibernate.cfg.xml"

            ; delete old registry and write new one
            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_WATCHER"

            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_WATCHER" ""
            DetailPrint "The File Watcher is successfully upgraded to v3.5.4.0."
            Goto End

        Already_Upgraded:
            DetailPrint "The File Watcher service is already upgraded to v3.5.4.0"

        End:
    SectionEnd

    Section /o "Content Extractor upgrade to v3.5" SEC_UPG_EXTRACTOR

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:
            DetailPrint "$\nTrying to update Content Extractor to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier Content Extractor"
            Pop $0 ; returns an error-code (<>0) otherwise success (0)
            Pop $1 ; return the status of the service
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Content Extractor service..."
                    SimpleSC::StopService "Smart Classifier Content Extractor" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier Content Extractor"
                        Pop $R1
                        Pop $R2
                    ${Else}

                        MessageBox MB_ICONSTOP "Could not stop the Content Extractor service."
                        DetailPrint "Could not stop the Content Extractor service."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0
                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier Content Extractor"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Content Extractor service status"
                        DetailPrint "Could not get the Content Extractor service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0
                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the status of Content Extractor service"
                DetailPrint "Could not get the status of Content Extractor service"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Content Extractor service has been stopped.."

            AccessControl::GrantOnFile "$INSTDIR\${EXTRACTOR}" "(BU)" "FullAccess"

            ; replace the xlib jars
            DetailPrint "Replacing the Content Extractor xlib jar files."

            RMDir /r "$INSTDIR\${EXTRACTOR}\xlib"
            SetOutPath "$INSTDIR\${EXTRACTOR}\xlib"
            File "${PackagerDir}\Extractor\build\NextLabs-Extractor\xlib\*"

            DetailPrint "Replacing the nextlabs-extractor.jar"
            Delete "$INSTDIR\${EXTRACTOR}\nextlabs-extractor.jar"
            SetOutPath "$INSTDIR\${EXTRACTOR}"
            File "${PackagerDir}\Extractor\build\NextLabs-Extractor\nextlabs-extractor.jar"

            ; remove DOCTYPE declaration from hibernate.cfg.xml
            ${replaceLineInFile} "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml" "<!DOCTYPE" ""

            DetailPrint "Backing up the hibernate.cfg.xml to $INSTDIR\${EXTRACTOR}\conf\hibernate_bak.cfg.xml"
            CopyFiles /SILENT /FILESONLY "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml" "$INSTDIR\${EXTRACTOR}\conf\hibernate_bak.cfg.xml"
            Delete "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"
            SetOutPath "$INSTDIR\${EXTRACTOR}\conf"
            File "${PackagerDir}\Extractor\build\NextLabs-Extractor\conf\hibernate.cfg.xml"

            DetailPrint "Reading the database information from $INSTDIR\${EXTRACTOR}\conf\hibernate_bak.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${EXTRACTOR}\conf\hibernate_bak.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::getText
            StrCpy $DBDriverTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::getText
            StrCpy $DBURLTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::getText
            StrCpy $DBUsernameTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::getText
            StrCpy $DBEncPassTemp $3

            DetailPrint "Trying to update the $INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::setText $DBDriverTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::setText $DBURLTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::setText $DBUsernameTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::setText $DBEncPassTemp

            nsisXML::save "$INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            DetailPrint "Successfully updated $INSTDIR\${EXTRACTOR}\conf\hibernate.cfg.xml"

            ; delete old registry and write new one
            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_EXTRACTOR"

            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_EXTRACTOR" ""
            DetailPrint "The Content Extractor is successfully upgraded to v3.5.4.0."
            Goto End

        Already_Upgraded:
            DetailPrint "The Content Extractor service is already upgraded to v3.5.4.0"

        End:
    SectionEnd

    Section /o "Rule Engine upgrade to v3.5" SEC_UPG_RULE

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_RULE"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:
            DetailPrint "$\nTrying to update the Rule Engine to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier Rule Engine"
            Pop $0 ; returns an error-code (<>0) otherwise success (0)
            Pop $1 ; return the status of the service
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Rule Engine service..."
                    SimpleSC::StopService "Smart Classifier Rule Engine" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier Rule Engine"
                        Pop $R1
                        Pop $R2
                    ${Else}

                        MessageBox MB_ICONSTOP "Could not stop the Rule Engine service."
                        DetailPrint "Could not stop the Rule Engine service."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0

                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier Rule Engine"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Rule Engine service status"
                        DetailPrint "Could not get the Rule Engine service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0

                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the status of Rule Engine service"
                DetailPrint "Could not get the status of Rule Engine service"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Rule Engine service has been stopped.."

            AccessControl::GrantOnFile "$INSTDIR\${RULE_ENGINE}" "(BU)" "FullAccess"

            DetailPrint "Replacing the Rule Engine xlib jar files."

            RMDir /r "$INSTDIR\${RULE_ENGINE}\xlib"
            SetOutPath "$INSTDIR\${RULE_ENGINE}\xlib"
            File /r "${PackagerDir}\RuleEngine\build\NextLabs-Rule-Engine\xlib\*"

            DetailPrint "Replacing the nextlabs-rule-engine.jar"

            Delete "$INSTDIR\${RULE_ENGINE}\nextlabs-rule-engine.jar"
            SetOutPath "$INSTDIR\${RULE_ENGINE}"
            File "${PackagerDir}\RuleEngine\build\NextLabs-Rule-Engine\nextlabs-rule-engine.jar"

            ; remove DOCTYPE declaration from hibernate.cfg.xml
            ${replaceLineInFile} "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml" "<!DOCTYPE" ""

            DetailPrint "Backing up the hibernate.cfg.xml to $INSTDIR\${RULE_ENGINE}\conf\hibernate_bak.cfg.xml"

            CopyFiles /SILENT /FILESONLY "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml" "$INSTDIR\${RULE_ENGINE}\conf\hibernate_bak.cfg.xml"
            Delete "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"
            SetOutPath "$INSTDIR\${RULE_ENGINE}\conf"
            File "${PackagerDir}\RuleEngine\build\NextLabs-Rule-Engine\conf\hibernate.cfg.xml"

            DetailPrint "Reading the database information from $INSTDIR\${RULE_ENGINE}\conf\hibernate_bak.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${RULE_ENGINE}\conf\hibernate_bak.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::getText
            StrCpy $DBDriverTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::getText
            StrCpy $DBURLTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::getText
            StrCpy $DBUsernameTemp $3

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::getText
            StrCpy $DBEncPassTemp $3

            DetailPrint "Trying to update the $INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"

            nsisXML::create
            nsisXML::load "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.driver_class"]'
            nsisXML::setText $DBDriverTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.url"]'
            nsisXML::setText $DBURLTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.username"]'
            nsisXML::setText $DBUsernameTemp

            nsisXML::select '/hibernate-configuration/session-factory/property[@name="connection.password"]'
            nsisXML::setText $DBEncPassTemp

            nsisXML::save "$INSTDIR\${RULE_ENGINE}\conf\hibernate.cfg.xml"

            DetailPrint "Adding the new plugins..."

            Delete "$INSTDIR\${RULE_ENGINE}\plugins\add-tag-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\copy-file-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\csv-dataprovider.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\db-dataprovider.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\decrypt-file-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\delete-file-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\encrypt-file-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\move-file-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\remove-tag-action.jar"
            Delete "$INSTDIR\${RULE_ENGINE}\plugins\send-email-action.jar"

            SetOutPath "$INSTDIR\${RULE_ENGINE}\plugins"
            File /r "${PackagerDir}\Plugin\build\NextLabs-Rule-Engine-Plug-in\plugins\*"

            Setoverwrite on
            SetOutPath "$INSTDIR\${RULE_ENGINE}\xlib"
            File /r "${PackagerDir}\Plugin\build\NextLabs-Rule-Engine-Plug-in\xlib\*"

            ; delete old registry and write new one
            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_RULE"

            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_RULE" ""
            DetailPrint "The Rule Engine is successfully upgraded to v3.5.4.0."

            Goto End

        Already_Upgraded:
            DetailPrint "The Rule Engine is already upgraded to v3.5.4.0"

        End:
    SectionEnd

    Section /o "Web User Interface upgrade to v3.5" SEC_UPG_UI

        ClearErrors

        SetRegView 64
        ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_UI"
        IfErrors NotUpgraded Already_Upgraded

        NotUpgraded:

            DetailPrint "$\nTrying to update the Web User Interface to v3.5.4.0"

            SimpleSC::GetServiceStatus "Smart Classifier Web UI"
            Pop $0 ; returns an error-code (<>0) otherwise success (0)
            Pop $1 ; return the status of the service
            ${If} $0 = 0
                ${If} $1 <> 1 ; =1 means stopped

                    DetailPrint "Trying to stop the Web UI service..."
                    SimpleSC::StopService "Smart Classifier Web UI" 1 30
                    Pop $R0

                    ${If} $R0 = 0
                        SimpleSC::GetServiceStatus "Smart Classifier Web UI"
                        Pop $R1
                        Pop $R2
                    ${Else}

                        MessageBox MB_ICONSTOP "Could not stop the Web UI service."
                        DetailPrint "Could not stop the Web UI service."
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ;$R0 = 0

                    ${If} $R1 = 0
                        ${While} $R2 <> 1
                            Sleep 1000
                            SimpleSC::GetServiceStatus "Smart Classifier Web UI"
                            Pop $R1
                            Pop $R2
                        ${EndWhile}
                    ${Else}
                        MessageBox MB_ICONSTOP "Could not get the Web UI service status"
                        DetailPrint "Could not get the Web UI service status"
                        DetailPrint "Copying the installation logs to $install_log_file"
                        StrCpy $0 $install_log_file
                        Push $0
                        Call DumpLog
                        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                        Abort
                    ${EndIf} ; $R1 = 0

                ${EndIf} ; $1 <> 1
            ${Else}
                MessageBox MB_ICONSTOP "Could not get the status of Web UI service"
                DetailPrint "Could not get the status of Web UI service"
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            DetailPrint "The Web UI service has been stopped.."

            DetailPrint "Removing the Web UI service..."
            SimpleSC::RemoveService "Smart Classifier Web UI"
            Pop $R3 ; returns an error-code (<>0) otherwise success (0)
            ${If} $R3 = 0
                DetailPrint "The Web UI service has been removed"
            ${Else}
                MessageBox MB_ICONSTOP "Could not remove the WEBUI service.."
                DetailPrint "Could not remove the WEBUI service.."
                DetailPrint "Copying the installation logs to $install_log_file"
                StrCpy $0 $install_log_file
                Push $0
                Call DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort
            ${EndIf}

            AccessControl::GrantOnFile "$INSTDIR\${WEBUI}" "(BU)" "FullAccess"

            DetailPrint "Adding the CAS webapp to WEBUI.."
            SetOutPath "$INSTDIR\${WEBUI}\${WEBAPPS}\cas"
            File /r "${PackagerDir}\CASExtension\build\cas\*"

            SetOutPath "$INSTDIR\${WEBUI}\conf\"
            File "${PackagerDir}\CASExtension\conf\cas.properties"

            DetailPrint "Adding the environment variables SC_SERVER_NAME, server.config.path"

            ${StrRep} '$serverName' 'https://localhost' 'localhost' '$HostNameTextBoxState'
            ${StrRep} '$serverConfigPath' 'Path' 'Path' '$INSTDIR\${WEBUI}\conf'

            WriteRegExpandStr ${env_hklm} "SC_SERVER_NAME" $serverName
            WriteRegExpandStr ${env_hklm} "server.config.path" $serverConfigPath

            ; make sure windows knows about the change
            SendMessage ${HWND_BROADCAST} ${WM_SETTINGCHANGE} 0 "STR:Environment" /TIMEOUT=5000

            ; make available to the current installer process.
            SetEnv::SetEnvVar "SC_SERVER_NAME" "$serverName"
            SetEnv::SetEnvVar "server.config.path" "$serverConfigPath"

            ; modify CAS.properties
            ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "server.name=" "$serverName" $R0
            ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.driverClass=" "$DBDriver" $R0
            ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.url=" "$DBURLState" $R0
            ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.user=" "$DBUsernameState" $R0
            ${ConfigWrite} "$INSTDIR\${WEBUI}\conf\cas.properties" "database.password=" "$DBPasswordEncrypted" $R0

            DetailPrint "Replacing the SmartClassifier.war file"

            Delete "$INSTDIR\${WEBUI}\webapps\SmartClassifier.war"
            RMDir /r "$INSTDIR\${WEBUI}\webapps\SmartClassifier"
            SetOutPath "$INSTDIR\${WEBUI}\webapps"
            File "${PackagerDir}\WebUI\build\NextLabs-WebUI\webapps\SmartClassifier.war"

            ; replace  conf/tomcat-users.xml, conf/web.xml
            DetailPrint "Replacing the tomcat-users.xml and web.xml of tomcat"

            Delete "$INSTDIR\${WEBUI}\conf\tomcat-users.xml"
            Delete "$INSTDIR\${WEBUI}\conf\web.xml"
            SetOutPath "$INSTDIR\${WEBUI}\conf"
            File "${PackagerDir}\WebUI\build\NextLabs-WebUI\conf\web.xml"
            File "${PackagerDir}\WebUI\build\NextLabs-WebUI\conf\tomcat-users.xml"

            DetailPrint "Re-installing the Smart Classifier Web UI service"

            ; replace bin/service_sc.bat
            Delete "$INSTDIR\${WEBUI}\bin\install.bat"
            SetOutPath "$INSTDIR\${WEBUI}\bin"
            File "${PackagerDir}\WebUI\build\NextLabs-WebUI\bin\install.bat"

            AccessControl::GrantOnFile "$INSTDIR\${WEBUI}\bin" "(BU)" "FullAccess"
            SetOutPath "$INSTDIR\${WEBUI}\"
            ExpandEnvStrings $0 %COMSPEC%
            nsExec::ExecToLog '"$0" /C "$INSTDIR\${WEBUI}\bin\install.bat" install'
            SimpleSC::SetServiceStartType "Smart Classifier Web UI" 2
            SimpleSC::StartService "Smart Classifier Web UI" "" 30

            ; delete old registry and write new one
            SetRegView 32
            DeleteRegKey HKLM "${REG_UNINSTALL}\Components\SEC_NXL_UI"

            SetRegView 64
            WriteRegStr HKLM "${REG_INSTALL}" "SEC_UI" ""

            DetailPrint "The Web User Interface is successfully upgraded to v3.5.4.0."
            Goto End

        Already_Upgraded:
            DetailPrint "The Web User Interface is already upgraded to v3.5.4.0"

        End:
    SectionEnd

;----------------------------------------------------------------------
; Uninstall Sections

    Section /o "un.Web User Interface" SEC_UN_UI

        SetRegView 64
        DetailPrint "$\nRemoving the web user interface..."

        SimpleSC::GetServiceStatus "Smart Classifier Web UI"
        Pop $0 ; returns an error-code (<>0) otherwise success (0)
        Pop $1 ; return the status of the service

        ${If} $0 = 0
            ${If} $1 <> 1 ; =1 means stopped

                DetailPrint "Trying to stop the Web UI service..."
                SimpleSC::StopService "Smart Classifier Web UI" 1 30
                Pop $R0

                ${If} $R0 = 0
                    SimpleSC::GetServiceStatus "Smart Classifier Web UI"
                    Pop $R1
                    Pop $R2
                ${Else}
                    MessageBox MB_ICONSTOP "Could not stop the Web UI service."
                    DetailPrint "Could not stop the Web UI service."
                    ${TimeStamp} $1
                    StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
                    DetailPrint "Copying the uninstallation logs to $uninstallLogFileName"
                    Push $uninstallLogFileName
                    Call un.DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort
                ${EndIf} ;$R0 = 0

                ${If} $R1 = 0
                    ${While} $R2 <> 1
                        Sleep 1000
                        SimpleSC::GetServiceStatus "Smart Classifier Web UI"
                        Pop $R1
                        Pop $R2
                    ${EndWhile}
                ${Else}
                    MessageBox MB_ICONSTOP "Could not get the Web UI service status"
                    DetailPrint "Could not get the Web UI service status"
                    ${TimeStamp} $1
                    StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
                    DetailPrint "Copying the uninstallation logs to $uninstallLogFileName"
                    Push $uninstallLogFileName
                    Call un.DumpLog
                    System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                    Abort
                ${EndIf} ; $R1 = 0

            ${EndIf} ; $1 <> 1
        ${Else}
            MessageBox MB_ICONSTOP "Could not get the status of Web UI service"
            DetailPrint "Could not get the status of Web UI service"
            ${TimeStamp} $1
            StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
            DetailPrint "Copying the uninstallation logs to $uninstallLogFileName"
            Push $uninstallLogFileName
            Call un.DumpLog
            System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
            Abort
        ${EndIf}

        DetailPrint "The WEBUI service has been stopped. Removing it now..."

        SimpleSC::RemoveService "Smart Classifier Web UI"
        Pop $R3 ; returns an error-code (<>0) otherwise success (0)

        ${If} $R3 = 0
            DetailPrint "The WEBUI service has been removed"

            DetailPrint "Trying to remove web UI information from the database..."
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\WebUI_delete.sql"' "" ""
            Pop $0
            StrCmp $0 0 Success Failed

            Failed:
                MessageBox MB_ICONSTOP "Could not clear Web UI information from the database.."
                DetailPrint "Could not clear Web UI information from the database.."
                ${TimeStamp} $1
                StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
                DetailPrint "Copying the installation logs to $uninstallLogFileName"
                Push $uninstallLogFileName
                Call un.DumpLog
                System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
                Abort

            Success:
                DetailPrint "Successfully removed the web UI information from the database."

                DeleteRegValue ${env_hklm} "SC_CATALINA_HOME"
                DeleteRegValue ${env_hklm} "SC_SERVER_NAME"
                DeleteRegValue ${env_hklm} "server.config.path"

                DeleteRegValue HKLM "${REG_INSTALL}" "SEC_UI"

                ; make sure windows knows about the change
                SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
                RMDir /r "$INSTDIR\${WEBUI}"
        ${Else}
            MessageBox MB_ICONSTOP "Could not remove the WEBUI service.."
            DetailPrint "Could not remove the WEBUI service.."
            ${TimeStamp} $1
            StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
            DetailPrint "Copying the installation logs to $uninstallLogFileName"
            Push $uninstallLogFileName
            Call un.DumpLog
            System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
            Abort
        ${EndIf}
    SectionEnd

  Section /o "un.Content Extractor" SEC_UN_EXTRACTOR   
    SetRegView 64
    
    DetailPrint "$\nRemoving the content extractor from this machine..."
    
    ; uninstall the service
    ExpandEnvStrings $0 %COMSPEC%
    nsExec::ExecToLog /TIMEOUT=60000 '"$0" /C "$INSTDIR\${EXTRACTOR}\bin\uninstall.bat"'
    
    ; remove from database
    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_delete.sql" "@hostname" $HOSTNAME
    
    ; print the contents of the file
    DetailPrint "Printing the contents of $INSTDIR\${INSTALLER}\sql\EXTRACTORS_delete.sql..."
    FileOpen $4 "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_delete.sql" r
    IfErrors done

    ReadAgain:
      FileRead $4 $1
      IfErrors endOfFile
      
      DetailPrint $1
      Goto ReadAgain
    
    endOfFile:
      FileClose $4

    done:
      DetailPrint "-----------------------------------"

    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\EXTRACTORS_delete.sql"' "" ""
    Pop $0
    StrCmp $0 0 Success Failed

    Failed:
      MessageBox MB_ICONSTOP "Could not clear Content Extractor information from the database.."
      ${TimeStamp} $1
      StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
      DetailPrint "Copying the installation logs to $uninstallLogFileName"
      Push $uninstallLogFileName
      Call un.DumpLog
      System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
      Abort

    Success:
    
      ; remove from file system and registry
      
      RMDir /r "$INSTDIR\${EXTRACTOR}"
      DeleteRegValue HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
      DetailPrint "Content Extractor was uninstalled successfully."

  SectionEnd

  Section /o "un.File Watcher" SEC_UN_WATCHER

    SetRegView 64
    DetailPrint "$\nRemoving the File Watcher from this machine..."
    
    ; uninstall the service
    ExpandEnvStrings $0 %COMSPEC%
    nsExec::ExecToLog /TIMEOUT=60000 '"$0" /C "$INSTDIR\${FILE_WATCHER}\bin\uninstall.bat"'
    
    ; remove from database
    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\WATCHERS_delete.sql" "@hostname" $HOSTNAME
    
    ; print the contents of the file
    DetailPrint "Printing the contents of $INSTDIR\${INSTALLER}\sql\WATCHERS_delete.sql"
    FileOpen $4 "$INSTDIR\${INSTALLER}\sql\WATCHERS_delete.sql" r
    IfErrors done

    ReadAgain:
      FileRead $4 $1
      IfErrors endOfFile
      
      DetailPrint $1
      Goto ReadAgain
    
    endOfFile:
      FileClose $4

    done:
      DetailPrint "-----------------------------------"

    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\WATCHERS_delete.sql"' "" ""
    Pop $0
    StrCmp $0 0 Success Failed

    Failed:
      MessageBox MB_ICONSTOP "Could not clear File Watcher information from the database.."
      ${TimeStamp} $1
      StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
      DetailPrint "Copying the installation logs to $uninstallLogFileName"
      Push $uninstallLogFileName
      Call un.DumpLog
      System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
      Abort

    Success:
    
      # remove from file system and registry
      RMDir /r "$INSTDIR\${FILE_WATCHER}"
      DeleteRegValue HKLM "${REG_INSTALL}" "SEC_WATCHER"
      DetailPrint "File Watcher was uninstalled successfully."
  SectionEnd

  Section /o "un.Rule Engine" SEC_UN_RULE
    SetRegView 64  
    
    DetailPrint "$\nRemoving the Rule Engine from this machine..."
    
    # uninstall the service
    ExpandEnvStrings $0 %COMSPEC%
    nsExec::ExecToLog /TIMEOUT=60000 '"$0" /C "$INSTDIR\${RULE_ENGINE}\bin\uninstall.bat"'
    
    # remove from database
    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\RULE_ENGINES_delete.sql" "@hostname" $HOSTNAME
    
    ;print the contents of the file
    DetailPrint "Printing the contents of $INSTDIR\${INSTALLER}\sql\RULE_ENGINES_delete.sql"
    FileOpen $4 "$INSTDIR\${INSTALLER}\sql\RULE_ENGINES_delete.sql" r
    IfErrors done

    ReadAgain:
      FileRead $4 $1
      IfErrors endOfFile
      
      DetailPrint $1
      Goto ReadAgain
    
    endOfFile:
      FileClose $4

    done:
        DetailPrint "-------------------------------"

    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\RULE_ENGINES_delete.sql"' "" ""
    Pop $0
    StrCmp $0 0 Success Failed

    Failed:
      MessageBox MB_ICONSTOP "Could not clear Rule Engine information from the database.."
      DetailPrint "Copying the installation logs to uninstall.txt"
      ${TimeStamp} $1
      StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
      Push $uninstallLogFileName
      Call un.DumpLog
      System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
      Abort

    Success:

      # remove from file system and registry
      RMDir /r "$INSTDIR\${RULE_ENGINE}"
      DeleteRegValue HKLM "${REG_INSTALL}" "SEC_RULE"
      DetailPrint "Rule Engine was uninstalled successfully."
  SectionEnd

  Section /o "un.Queue" SEC_UN_QUEUE
    SetRegView 64
    
    DetailPrint "$\nRemoving the Queue on this machine..."
    
    # remove the service
    ExpandEnvStrings $0 %COMSPEC%    
    nsExec::ExecToLog /TIMEOUT=60000 '"$0" /C "$INSTDIR\${QUEUE}\bin\uninstallQueue.bat"'
            
    # remove from database
    ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_delete.sql" "@hostname" $HOSTNAME  
    
    ;print the contents of the file
    DetailPrint "Printing the contents of $INSTDIR\${INSTALLER}\sql\JMS_PROFILES_delete.sql"
    FileOpen $4 "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_delete.sql" r
    IfErrors done

    ReadAgain:
      FileRead $4 $1
      IfErrors endOfFile
      
      DetailPrint $1
      Goto ReadAgain
    
    endOfFile:
      FileClose $4

    done:
      DetailPrint "-------------------------------"

    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\JMS_PROFILES_delete.sql"' "" ""
    Pop $0
    StrCmp $0 0 Success Failed

    Failed:
        MessageBox MB_ICONSTOP "Could not clear Queue information from the database.."
        ${TimeStamp} $1
        StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
        DetailPrint "Copying the installation logs to $uninstallLogFileName"
        Push $uninstallLogFileName
        Call un.DumpLog
        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
        Abort

    Success:
    
        # remove from file system and registry
        DeleteRegValue HKLM "${REG_INSTALL}" "SEC_QUEUE"
        DetailPrint "Queue was uninstalled successfully."

        ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_QUEUE"
        IfErrors NotInstalled Installed

        NotInstalled:
            RMDir /r "$INSTDIR\${QUEUE}"

        Installed:
      
  SectionEnd

  Section /o "un.Indexer" SEC_UN_INDEXER
    SetRegView 64
    
    DetailPrint "$\nRemoving the Indexer on this machine..."
    
    # remove the service
    ExpandEnvStrings $0 %COMSPEC%    
    nsExec::ExecToLog /TIMEOUT=60000 '"$0" /C "$INSTDIR\Indexer\bin\uninstallIndexer.bat"'
            
    # remove from database
    
    ;print the contents of the file
    DetailPrint "Printing the contents of $INSTDIR\${INSTALLER}\sql\INDEXER_delete.sql"
    FileOpen $4 "$INSTDIR\${INSTALLER}\sql\INDEXER_delete.sql" r
    IfErrors done

    ReadAgain:
      FileRead $4 $1
      IfErrors endOfFile
      
      DetailPrint $1
      Goto ReadAgain
    
    endOfFile:
      FileClose $4

     done:
       DetailPrint "-------------------------------"
    
    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection deleteFromTable $DBDriver $DBURL $DBUsername $DBPasswordEncrypted "$INSTDIR\${INSTALLER}\sql\INDEXER_delete.sql"' "" ""
    Pop $0
    StrCmp $0 0 Success Failed

    Failed:

        MessageBox MB_ICONSTOP "Could not clear Indexer information from the database.."
        ${TimeStamp} $1
        StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
        DetailPrint "Copying the installation logs to $uninstallLogFileName"
        Push $uninstallLogFileName
        Call un.DumpLog
        System::Call 'USER32::PostMessage(i$HWNDPARENT,i0x408,i 1,i0)' ; Delayed skip 1 page
        Abort

    Success:

      ; remove from file system and registry
      DeleteRegValue HKLM "${REG_INSTALL}" "SEC_INDEXER"
      DetailPrint "Indexer was uninstalled successfully."
      
      ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_QUEUE"
      IfErrors NotInstalled Installed
      
      NotInstalled:
        RMDir /r "$INSTDIR\Indexer"

      Installed:

  SectionEnd


  
  Section "-un.Common_Java_DB" SEC_UN_COM_DB_JAVA
    SetRegView 64
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_QUEUE"
    IfErrors 0 SectionExists
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_INDEXER"
    IfErrors 0 SectionExists
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_WATCHER"
    IfErrors 0 SectionExists
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
    IfErrors 0 SectionExists
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_RULE"
    IfErrors 0 SectionExists
    
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_UI"
    IfErrors 0 SectionExists

    ; remove Java
    Delete "$INSTDIR\Uninstall.exe"

    DetailPrint "$\nRemoving java.."
	RMDir /r "$INSTDIR\${JAVA}"
    DeleteRegValue HKLM "${REG_INSTALL}" "SEC_JAVA"
    
    ; remove Common
    DetailPrint "$\nRemoving Common libraries..."
	RMDir /r "$INSTDIR\${COMMON}"
    
    DeleteRegValue ${env_hklm} "SC_DB_USERNAME"
    DeleteRegValue ${env_hklm} "SC_DB_PASSWORD"
    DeleteRegValue ${env_hklm} "SC_DB_URL"
    DeleteRegValue ${env_hklm} "SC_DB_URL_WITH_QUOTES_ON_SEMICOLON"
    DeleteRegValue ${env_hklm} "SC_DB_DRIVER"
    DeleteRegValue ${env_hklm} "SC_JAVA_HOME"
    	
    DeleteRegValue HKLM "${REG_INSTALL}" "SEC_COMMON"
    
    DeleteRegKey HKLM "${REG_UNINSTALL}"
    DeleteRegKey HKLM "${REG_INSTALL}"
    
    ; make sure windows knows about the change
    SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000

    CopyFiles /SILENT /FILESONLY "$INSTDIR\${INSTALLER}\logs\*.*" "$TEMP\SmartClassifier"

    RMDir /r "$INSTDIR\${INSTALLER}"
    RMDir $INSTDIR
    
    Goto End
    
    SectionExists:
        DetailPrint "Cannot remove java and common since one of the component still exists.."

    End:
  SectionEnd


;----------------------------------------------------------------------
; Functions

  Function .onInit

    ${readHostName}
    StrCpy $HostNameTextBoxState $HOSTNAME
	System::Call "kernel32::GetCurrentDirectory(i ${NSIS_MAX_STRLEN}, t .r0)"
    StrCpy $LicenceTextBoxState "$0\license.dat"
    StrCpy $DBDriver "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    ; read the currently installed version from registry
    ; decide whether it is an upgrade of full install
    SetRegView 32
    ReadRegStr $0 HKLM "${REG_UNINSTALL}" "DisplayVersion"
    IfErrors full_install upgrade

    full_install:
        StrCpy $DBURLState "jdbc:sqlserver://hostname:1433;database=databaseName"
        StrCpy $DBUsernameState ""
        StrCpy $DBPasswordState ""
        StrCpy $QueuePortNumberState "61616"
        StrCpy $IndexerPortNumberState "8093"
        StrCpy $JMSDropListState ""
        StrCpy $LocalJMSURL "tcp://hostname:61616"
        StrCpy $TempStr ""
        StrCpy $DBPasswordEncrypted ""
        StrCpy $install_log_file "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
        Goto End

    upgrade:
        ; $0 = current version
        ${If} $0 == "3.5.4.0"
            MessageBox MB_OK "You already have v3.5.4.0 installed. Press ok to quit"
        ${EndIf}

        ${StrContains} $R1 "3.1" $0
        ${If} $R1 == "3.1"
            MessageBox MB_OKCANCEL "You have already installed Smart Classifier v$0. This installer will upgrade to v3.5.4.0. Press ok to continue upgrade, press cancel to quit." IDCANCEL QuitInstaller
            StrCpy $upgrade "true"
            StrCpy $currentVersion $R1
            StrCpy $install_log_file "$INSTDIR\${INSTALLER}\logs\upgrade_31_35_p2.txt"
            Goto End
        ${Else}
            MessageBox MB_OK "Unknown version $0 detected. Press ok to quit"
            Quit
        ${EndIf}

    ; during the full install initialize variables
    ; during the upgrade - only do certain things - set flags to skip pages. decide which section to be installed.
    QuitInstaller:
        Quit

    End:
  FunctionEnd

    Function skipIfUpgrade
        ${If} $upgrade == "true"
            Abort
        ${EndIf}
    FunctionEnd

    Function checkInstallDir
        ${If} $upgrade == "true"
            ReadRegStr $0 HKLM "${REG_UNINSTALL}" "DisplayIcon"
            ${StrStrip}  "\Common\nxce.ico" $0 $R0
            StrCpy $INSTDIR $R0
        ${EndIf}
    FunctionEnd

    Function showDirectoryPage
        ${If} $upgrade == "true"
            !if "${MUI_SYSVERSION}" >= 2.0
                SendMessage $mui.DirectoryPage.Directory ${EM_SETREADONLY} 1 0
                EnableWindow $mui.DirectoryPage.BrowseButton 0
            !else
                FindWindow $0 '#32770' '' $HWNDPARENT
                GetDlgItem $1 $0 0x3FB
                SendMessage $1 ${EM_SETREADONLY} 1 0
                GetDlgItem $1 $0 0x3E9
                EnableWindow $1 0
            !endif
        ${EndIf}
    FunctionEnd

    /* Make sure only Java and common gets installed */
    Function unSelectSections

        ; for upgrade
        ; need to replace java and common
        ; need to replace other components

        ;insertmacro UnSelectSection ${SEC_DB}
        ;!insertmacro UnSelectSection ${SEC_EXTRACTOR}
        ;!insertmacro UnSelectSection ${SEC_WATCHER}
        ;!insertmacro UnSelectSection ${SEC_UI}
        ;!insertmacro UnSelectSection ${SEC_RULE}
        ;!insertmacro UnSelectSection ${SEC_INDEXER}
    FunctionEnd
  

  
    Function getLicenceFile

    ${If} $upgrade == "true"
        Abort
    ${EndIf}

    !insertmacro MUI_HEADER_TEXT $(LICENCE_PAGE_TITLE) $(LICENCE_PAGE_SUBTITLE)

    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}

    ${NSD_CreateLabel} 0 0 100% 12u "Please select the license file."
    Pop $LicenceDialogLabel

    ${NSD_CreateText} 0 50% 70% 12u $LicenceTextBoxState
    Pop $LicenceTextBox

    ${NSD_CreateBrowseButton} 71% 50% 29% 12u "Browse"
    pop $LicenceBrowseButton

    ${NSD_OnClick} $LicenceBrowseButton BrowseLicenseFile

    nsDialogs::Show

    FunctionEnd

  Function BrowseLicenseFile
      
    Pop $0 ; $0 == $LicenceBrowseButton
      
    nsDialogs::SelectFileDialog open "" "*.dat"
    pop $path
      
    ${NSD_SetText} $LicenceTextBox $path
  FunctionEnd

  Function checkLicenceFile
    
    StrCpy $trimmedPath ""
    
    ${NSD_GetText} $LicenceTextBox $LicenceTextBoxState
    ${Trim} $trimmedPath $LicenceTextBoxState
    
    ${If} $trimmedPath == ""
      MessageBox MB_ICONEXCLAMATION  "The licence path cannot be empty! Please enter the path to the license file."
      Abort
    ${EndIf}
    
    IfFileExists $LicenceTextBoxState ContinueNextCheck error
    
    error:
      MessageBox MB_ICONEXCLAMATION  "The file at this path does not exist! Please enter the path to a valid licence file!"
      Abort
    
    ContinueNextCheck:

      ${GetAfterChar} $R0 "\" $LicenceTextBoxState
      ${If} $R0 != "license.dat"
        MessageBox MB_ICONINFORMATION "Please select a valid Smart Classifier licence file (license.dat)"
        Abort
      ${EndIf}
      
      CopyFiles /SILENT /FILESONLY $LicenceTextBoxState $INSTDIR\${COMMON}\${LICENSE_CHECKER}
      
      ClearErrors
      ;ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${LICENSE_CHECKER}";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\lib\*";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\*" com.nextlabs.license.checker.LicenseChecker ..\Common\lib\license.jar' "" ""

      AccessControl::GrantOnFile "$INSTDIR\${INSTALLER}\logs" "(BU)" "FullAccess"
      Delete "$INSTDIR\${INSTALLER}\logs\license.properties"

      ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${LICENSE_CHECKER}";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\*";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\lib\*";"$INSTDIR\${COMMON}\${LICENSE_CHECKER}\conf" com.nextlabs.license.checker.LicenseChecker "$INSTDIR\${COMMON}\${LICENSE_CHECKER}\lib\license.jar" "$INSTDIR\${INSTALLER}\logs\license.properties"' "" ""
      Pop $0 # return value
      StrCmp $0 0 Success Failed

      Failed:
        MessageBox MB_ICONEXCLAMATION 'Please enter a valid license file !!! Error Code : $0'
        Abort

      Success:    
  FunctionEnd

  Function getDBInfo

    ${If} $upgrade == "true"
        Abort
    ${EndIf}

    !insertmacro MUI_HEADER_TEXT $(DB_PAGE_TITLE) $(DB_PAGE_SUBTITLE)
    
    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}

    ${If} $DBURLState == "jdbc:sqlserver://hostname:1433;database=databaseName"
    ${AndIf} $DBUsernameState == ""
    ${AndIf} $DBPasswordState == ""
        ; nothing entered by user yet
        StrCpy $DBURLTemp ""
        ReadEnvStr $DBURLTemp "SC_DB_URL"

        StrCpy $DBUsernameTemp ""
        ReadEnvStr $DBUsernameTemp "SC_DB_USERNAME"

        ${If} $DBURLTemp != ""
            StrCpy $DBURLState $DBURLTemp
            StrCpy $DBUsernameState $DBUsernameTemp
        ${EndIf}

    ${EndIf}

    ${NSD_CreateLabel} 0 17u 20% 12u "Connection URL:"
    Pop $DBURLLabel
    
    ${NSD_CreateText} 21% 16u 75% 12u "$DBURLState"
    Pop $DBURLTextBox
    
    ${NSD_CreateLabel} 0 41u 20% 12u "Username:"
    Pop $DBUsernameLabel

    ${NSD_CreateText} 21% 40u 75% 12u "$DBUsernameState"
    Pop $DBUsernameTextBox
    
    ${NSD_CreateLabel} 0 65u 20% 12u "Password:"
    Pop $DBPasswordLabel

    ${NSD_CreatePassword} 21% 64u 75% 12u "$DBPasswordState"
    Pop $DBPasswordTextBox 
    
    ${NSD_OnBack} "StoreDBInfo"
    
    nsDialogs::Show

  FunctionEnd

    Function storeDBInfo
        ${NSD_GetText} $DBURLTextBox $DBURLState
        ${NSD_GetText} $DBUsernameTextBox $DBUsernameState
        ${NSD_GetText} $DBPasswordTextBox $DBPasswordState
    FunctionEnd

  Function checkDBInfo

    ${NSD_GetText} $DBURLTextBox $DBURLState 
    ${NSD_GetText} $DBUsernameTextBox $DBUsernameState
    ${NSD_GetText} $DBPasswordTextBox $DBPasswordState
    
    StrCpy $trimmedURL ""
    StrCpy $trimmedUsername ""
    StrCpy $trimmedPassword ""
    
    ${Trim} $trimmedURL $DBURLState
    ${Trim} $trimmedUsername $DBUsernameState
    ${Trim} $trimmedPassword $DBPasswordState
      
    ${If} $trimmedURL == ""
    ${OrIf} $trimmedUsername == ""
    ${OrIf} $trimmedPassword == ""
      MessageBox MB_ICONEXCLAMATION  "The fields cannot be empty. Please enter the appropriate values."
      Abort
    ${EndIf}
     
    ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection checkUserPermissions $DBDriver $DBURLState $DBUsernameState $DBPasswordState "$INSTDIR\${INSTALLER}\sql\checkUserPermissions.sql"' "" ""
    Pop $0 # return value
    
    StrCmp $0 0 Success Failed

    Failed:
      MessageBox MB_ICONEXCLAMATION "The connection to the database failed; Please check your URL, username and password."
      Abort
     
    Success:
      
  FunctionEnd

    Function selectSections

        !insertmacro RemoveSection ${SEC_COMMON}
        !insertmacro RemoveSection ${SEC_JAVA}

        !insertmacro SelectSection ${SEC_DB}

        ${If} $upgrade == "true"

            ; Remove fresh install sections
            !insertmacro RemoveSection ${SEC_RULE}
            !insertmacro RemoveSection ${SEC_INDEXER}
            !insertmacro RemoveSection ${SEC_QUEUE}
            !insertmacro RemoveSection ${SEC_UI}
            !insertmacro RemoveSection ${SEC_EXTRACTOR}
            !insertmacro RemoveSection ${SEC_WATCHER}

            SetRegView 32
            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_WATCHER" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_WATCHER}
            ${Else}
                SectionSetFlags ${SEC_UPG_WATCHER} 17
            ${EndIf}

            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_EXTRACTOR" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_EXTRACTOR}
            ${Else}
                SectionSetFlags ${SEC_UPG_EXTRACTOR} 17
            ${EndIf}

            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_RULE" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_RULE}
            ${Else}
                SectionSetFlags ${SEC_UPG_RULE} 17
            ${EndIf}

            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_UI" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_UI}
            ${Else}
                SectionSetFlags ${SEC_UPG_UI} 17
            ${EndIf}

            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_INDEXER" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_INDEXER}
            ${Else}
                SectionSetFlags ${SEC_UPG_INDEXER} 17
            ${EndIf}

            ClearErrors
            ReadRegDWORD $0 HKLM "${REG_UNINSTALL}\Components\SEC_NXL_QUEUE" "Installed"
            ${If} ${Errors}
                !insertmacro RemoveSection ${SEC_UPG_QUEUE}
            ${Else}
                SectionSetFlags ${SEC_UPG_QUEUE} 17
            ${EndIf}

        ${Else}

            ; Remove upgrade sections
            !insertmacro RemoveSection ${SEC_UPG_QUEUE}
            !insertmacro RemoveSection ${SEC_UPG_INDEXER}
            !insertmacro RemoveSection ${SEC_UPG_RULE}
            !insertmacro RemoveSection ${SEC_UPG_EXTRACTOR}
            !insertmacro RemoveSection ${SEC_UPG_UI}
            !insertmacro RemoveSection ${SEC_UPG_WATCHER}

            ; remove sections which are already installed
            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_EXTRACTOR}
            ${Else}
                !insertmacro RemoveSection ${SEC_EXTRACTOR}
            ${EndIf}

            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_WATCHER"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_WATCHER}
            ${Else}
                !insertmacro RemoveSection ${SEC_WATCHER}
            ${EndIf}

            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_RULE"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_RULE}
            ${Else}
                !insertmacro RemoveSection ${SEC_RULE}
            ${EndIf}

            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_UI"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_UI}
            ${Else}
                !insertmacro RemoveSection ${SEC_UI}
            ${EndIf}

            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_QUEUE"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_QUEUE}
            ${Else}
                !insertmacro RemoveSection ${SEC_QUEUE}
            ${EndIf}

            ClearErrors
            ReadRegStr $0 HKLM "${REG_INSTALL}" "SEC_INDEXER"
            ${If} ${Errors}
                !insertmacro SelectSection ${SEC_INDEXER}
            ${Else}
                !insertmacro RemoveSection ${SEC_INDEXER}
            ${EndIf}

            DetailPrint "Checking if the webui information is present in the database..."

            AccessControl::GrantOnFile "$INSTDIR\${INSTALLER}\logs" "(BU)" "FullAccess"

            Delete "$INSTDIR\${INSTALLER}\logs\webuiCheck.txt"
            ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection isInfoPresent $DBDriver $DBURLState $DBUsernameState $DBPasswordState NA "$INSTDIR\${INSTALLER}\sql\WebUI_Check.sql"' "" ""
            Pop $0
            StrCmp $0 0 WebInfoFound WebInfoNotFound

            WebInfoFound:
                !insertmacro RemoveSection ${SEC_UI}
                StrCpy $UIFound "true"
            WebInfoNotFound:

        ${EndIf}

    End:
  FunctionEnd
  
  Function checkJMSStatus
         
    SectionGetFlags ${SEC_WATCHER} $R0 
    SectionGetFlags ${SEC_EXTRACTOR} $R1
    SectionGetFlags ${SEC_QUEUE} $R2

    IntOp $R0 $R0 & ${SF_SELECTED} 
    IntOp $R1 $R1 & ${SF_SELECTED}
    IntOp $R2 $R2 & ${SF_SELECTED}
    
    ; if the watcher or extractor was selected, but the queue wasn't
    ${If} $R2 = 0 
      ${If} $R0 = ${SF_SELECTED}
      ${OrIf} $R1 = ${SF_SELECTED}
        
        ; check if the tables exist
        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection checkTableExistence $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES NA"' "" ""
        Pop $0 # return value
      
        StrCmp $0 0 Tables_Exist 
        StrCmp $0 1 Tables_DoNotExist
        
        Tables_Exist:
          
          ; get the rows from JMS_PROFILES table
          ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectFromTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES PROVIDER_URL "$INSTDIR\${INSTALLER}\logs\jms_values.txt"' "" ""
          Pop $0
          StrCmp $0 0 JMSFound JMSNotFound
          
          JMSFound:
            Return
          JMSNotFound:
            MessageBox MB_ICONINFORMATION "Please install the queue first, if you want to install the File Watcher or the Context Extractor"
            Abort
            
        Tables_DoNotExist:
          MessageBox MB_ICONINFORMATION "Please install the queue also, if you want to install the File-Watcher or the Context Extractor"
          Abort
      ${EndIf}
    ${EndIf}
  FunctionEnd
  
  Function getHostName

    ${If} $upgrade == "true"
        Abort
    ${EndIf}

    !insertmacro MUI_HEADER_TEXT $(HOSTNAME_PAGE_TITLE) $(HOSTNAME_PAGE_SUBTITLE)
    
    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}

    ${NSD_CreateLabel} 0 50% 30% 12u "Hostname"
    Pop $HostNameLabel
    
    ${NSD_CreateText} 31% 49% 65% 12u $HostNameTextBoxState
    Pop $HostNameTextBox
    
    ${NSD_OnBack} storeHostName
    
    nsDialogs::Show

  FunctionEnd
  
  Function storeHostName 
    ${NSD_GetText} $HostNameTextBox $HostNameTextBoxState
  FunctionEnd
  
  Function checkHostName

    ; initialize temp variable
    StrCpy $trimmedString ""

    ${NSD_GetText} $HostNameTextBox $HostNameTextBoxState
    ${Trim} $trimmedString $HostNameTextBoxState
    ${If} $trimmedString == ""
      MessageBox MB_ICONEXCLAMATION "The field cannot be empty. Please enter the appropriate value."
      Abort
    ${EndIf}
    
  FunctionEnd

  Function getQueuePort

    SectionGetFlags ${SEC_QUEUE} $R0 
    IntOp $R0 $R0 & ${SF_SELECTED} 
    
    IntCmp $R0 ${SF_SELECTED} show 
      Abort
    
    show:
    
    !insertmacro MUI_HEADER_TEXT $(Q_PAGE_TITLE) $(Q_PAGE_SUBTITLE)
    
    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
      Abort
    ${EndIf}
    
    ${NSD_CreateLabel} 0 50% 30% 12u "Queue Port Number"
    Pop $QueuePortNumberLabel

    ${NSD_CreateText} 31% 49% 65% 12u $QueuePortNumberState
    Pop $QueuePortNumberTB
    
    ${NSD_OnBack} storeQueuePortNumber
    
    nsDialogs::Show
      
  FunctionEnd

  Function storeQueuePortNumber
    ${NSD_GetText} $QueuePortNumberTB $QueuePortNumberState
  FunctionEnd

  Function checkQueuePort
    
    StrCpy $trimmedString ""
    
    ${NSD_GetText} $QueuePortNumberTB $QueuePortNumberState
    ${Trim} $trimmedString $QueuePortNumberState
    ${If} $trimmedString == ""
      MessageBox MB_OK "The field cannot be empty. Please enter the appropriate value."
      Abort
    ${EndIf}
      
  FunctionEnd

  Function getIndexerPort

    SectionGetFlags ${SEC_INDEXER} $R0 
    
    IntOp $R0 $R0 & ${SF_SELECTED} 
    IntCmp $R0 ${SF_SELECTED} show 
      Abort
    
    show:
    
    !insertmacro MUI_HEADER_TEXT $(INDEXER_PAGE_TITLE) $(INDEXER_PAGE_SUBTITLE)    
    
    nsDialogs::Create 1018
    Pop $Dialog

    ${If} $Dialog == error
        Abort
    ${EndIf}
    
    ${NSD_CreateLabel} 0 50% 30% 12u "Indexer Port Number"
    Pop $IndexerLabel

    ${NSD_CreateText} 31% 49% 65% 12u $IndexerPortNumberState
    Pop $IndexerPortNumberTB
    
    ${NSD_OnBack} storeIndexerPort
    
    nsDialogs::Show
      
  FunctionEnd

  Function storeIndexerPort
    ${NSD_GetText} $IndexerPortNumberTB $IndexerPortNumberState
  FunctionEnd

  Function checkIndexerPort
  
    StrCpy $trimmedString ""
    ${NSD_GetText} $IndexerPortNumberTB $IndexerPortNumberState  
    ${Trim} $trimmedString $QueuePortNumberState
    ${If} $trimmedString == ""
      MessageBox MB_OK "The field cannot be empty. Please enter the appropriate value."
      Abort
    ${EndIf}
  FunctionEnd

  Function getJMS
    
    SectionGetFlags ${SEC_EXTRACTOR} $R1 
    SectionGetFlags ${SEC_WATCHER} $R2 
    
    IntOp $R1 $R1 & ${SF_SELECTED} 
    IntOp $R2 $R2 & ${SF_SELECTED} 
    
    ${If} $R1 == ${SF_SELECTED}
    ${OrIf} $R2 == ${SF_SELECTED}
      
      !insertmacro MUI_HEADER_TEXT $(JMS_PAGE_TITLE) $(JMS_PAGE_SUBTITLE)
      
      nsDialogs::Create 1018
      Pop $Dialog

      ${If} $Dialog == error
        MessageBox MB_OK "The JMS Page could not be created!"
        Abort
      ${EndIf}
      
      ${NSD_CreateLabel} 0 50% 30% 12u "Queue URL"
      Pop $JMSLabel
   
      ${NSD_CreateDropList} 31% 49% 65% 12u $JMSDropListState
        Pop $JMSDropList
    
      SectionGetFlags ${SEC_QUEUE} $R0 
      
      IntOp $R0 $R0 & ${SF_SELECTED}   
      
      ${If} $R0 = ${SF_SELECTED}
        ${StrRep} '$LocalJMSURL' '$LocalJMSURL' 'hostname' '$HostNameTextBoxState'
        ${StrRep} '$LocalJMSURL' '$LocalJMSURL' '61616' '$QueuePortNumberState'        
        ${NSD_CB_AddString} $JMSDropList $LocalJMSURL
      ${EndIf}
      
      IfFileExists $INSTDIR\${INSTALLER}\logs\jms_values.txt FileExists
        
        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectFromTable $DBDriver $DBURLState $DBUsernameState $DBPasswordState JMS_PROFILES PROVIDER_URL "$INSTDIR\${INSTALLER}\logs\jms_values.txt"' "" ""
        Pop $0
        StrCmp $0 0 FileExists done ; if no table was found, it wasn't installed previously

      FileExists:

        ClearErrors
        FileOpen $4 "$INSTDIR\${INSTALLER}\logs\jms_values.txt" r
        IfErrors done ReadNextLine
        
        ReadNextLine:
          FileRead $4 $1
          IfErrors endOfFile

          ;${StrTok} "ResultVar" "String" "Separators" "ResultPart [0..n-1]" "SkipEmptyParts"
          ${StrTok} $2 $1 " " "1" "1"
          ${NSD_CB_AddString} $JMSDropList $2
            
          Goto ReadNextLine
        
        endOfFile:
          FileClose $4
        done:
        
        ${NSD_OnBack} storeJMS
        nsDialogs::Show
    ${Else}
      Abort
    ${EndIf}
  FunctionEnd

  Function storeJMS
    ${NSD_GetText} $JMSDropList $JMSDropListState
  FunctionEnd
  
  Function checkJMS      
    ${NSD_GetText} $JMSDropList $JMSDropListState
    ${If} $JMSDropListState == ""
        MessageBox MB_OK "Please select a Queue URL from the drop-down list."
        Abort
    ${EndIf}
  FunctionEnd
  
    Function postInstall

        ${If} $upgrade == "true"

            Call delete31Uninstaller

            Call writeNewUninstaller

        ${EndIf}

            DetailPrint "Copying the installation logs to $install_log_file"
            StrCpy $0 $install_log_file
            Push $0
            Call DumpLog
    FunctionEnd

    Function delete31Uninstaller
        DetailPrint "Deleting the uninstaller for v3.1.0.0..."

        Delete "$INSTDIR\Uninstall.exe"
        SetRegView 32
        DeleteRegKey HKLM "${REG_UNINSTALL}"
    FunctionEnd

    Function writeNewUninstaller

            SetRegView 64

            DetailPrint "Writing the uninstaller for v3.5.4.0..."

            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayName" "Smart Classifier"
            WriteRegStr HKLM "${REG_UNINSTALL}" "UninstallString" '"$INSTDIR\Uninstall.exe"'

            WriteRegStr HKLM "${REG_UNINSTALL}" "InstallLocation" "$INSTDIR"
            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayIcon" "$INSTDIR\${COMMON}\nxce.ico"
            WriteRegStr HKLM "${REG_UNINSTALL}" "Publisher" "NextLabs, Inc."
            WriteRegStr HKLM "${REG_UNINSTALL}" "InstallSource" "$EXEDIR"
            WriteRegStr HKLM "${REG_UNINSTALL}" "DisplayVersion" "$%PRODUCTVERSION%"

            ;Under WinXP this creates two separate buttons: "Modify" and "Remove".
            ;"Modify" will run installer and "Remove" will run uninstaller.

            WriteRegDWORD HKLM "${REG_UNINSTALL}" "NoModify" 0
            WriteRegDWORD HKLM "${REG_UNINSTALL}" "NoRepair" 1
            WriteRegStr HKLM "${REG_UNINSTALL}" "ModifyPath" '"$EXEDIR\${InstFile}"'

            WriteUninstaller "$INSTDIR\Uninstall.exe"
    FunctionEnd

  Function openLogs
    ExecShell "open" "$INSTDIR\${INSTALLER}\logs\log_part1.txt"
    ExecShell "open" "$INSTDIR\${INSTALLER}\logs\log_part2.txt"
  FunctionEnd
    
  Function un.onInit
  
    ${readHostName}
    
    CreateDirectory "$TEMP\SmartClassifier"
    
    ReadEnvStr $DBDriver "SC_DB_DRIVER"
    ReadEnvStr $DBURL "SC_DB_URL"
    ReadEnvStr $DBUsername "SC_DB_USERNAME"
    ReadEnvStr $DBPasswordEncrypted "SC_DB_PASSWORD"
    
  FunctionEnd
  
  Function un.selectSections

    ; select components that are installed
    Call un.selectExtractor
    Call un.selectWatcher
    Call un.selectRuleEngine
    Call un.selectQueue
    Call un.selectIndexer
    Call un.selectUI
    
  FunctionEnd
  
  Function un.selectUI
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_UI"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_UI}
  FunctionEnd
  
  Function un.selectExtractor
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_EXTRACTOR"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_EXTRACTOR}
  FunctionEnd
  
  Function un.selectWatcher
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_WATCHER"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_WATCHER}
  FunctionEnd
  
  Function un.selectRuleEngine
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_RULE"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_RULE}
  FunctionEnd
  
  Function un.selectQueue
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_QUEUE"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_QUEUE}
  FunctionEnd
  
  Function un.selectIndexer
    SetRegView 64
    ReadRegStr $isInstalled HKLM "${REG_INSTALL}" "SEC_INDEXER"
    IfErrors NotInstalled Installed
    
    Installed:
      Return
    NotInstalled:
      !insertmacro RemoveSection ${SEC_UN_INDEXER}
  FunctionEnd
  
  Function un.checkJMSStatus
    
    ; check if queue is being un-installed
    SectionGetFlags ${SEC_UN_QUEUE} $R0
    IntOp $R0 $R0 & ${SF_SELECTED}     

    AccessControl::GrantOnFile "$INSTDIR\${INSTALLER}" "(BU)" "FullAccess"

    ${If} $R0 = ${SF_SELECTED}

      ; replace in file
      ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\DependentWatcherHosts.sql" "@hostname" $HOSTNAME
      ${ReplaceInFile} "$INSTDIR\${INSTALLER}\sql\DependentExtractorHosts.sql" "@hostname" $HOSTNAME

      ; get dependent watchers
      Delete "$INSTDIR\${INSTALLER}\logs\depWatcherHosts.txt"
      ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectQuery $DBDriver $DBURL $DBUsername $DBPasswordEncrypted ENC "HOSTNAME" "$INSTDIR\${INSTALLER}\sql\DependentWatcherHosts.sql" "$INSTDIR\${INSTALLER}\logs\depWatcherHosts.txt"' "" ""
      Pop $0
      StrCmp $0 0 WFetchSuccess WFetchFailure
      
      WFetchFailure:
        MessageBox MB_ICONSTOP "Could not get the list of watchers dependent on this queue!!!"
        Quit
      
      WFetchSuccess:
        IfFileExists "$INSTDIR\${INSTALLER}\logs\depWatcherHosts.txt" 0 nextCheck

        FileOpen $4 "$INSTDIR\${INSTALLER}\logs\depWatcherHosts.txt" r
        IfErrors CantOpenWFileError ReadWAgain
        
        CantOpenWFileError:
          MessageBox MB_ICONSTOP "Could not open the file $INSTDIR\${INSTALLER}\logs\depWatcherHosts.txt for reading."
          Quit
          
        ReadWAgain:

          FileRead $4 $1
          IfErrors endOfWFile

          StrCpy $TempStr ""
          ${Trim} $TempStr $1
          
            ${If} $TempStr == "$HOSTNAME"
                SectionGetFlags ${SEC_UN_WATCHER} $R1
                IntOp $R1 $R1 & ${SF_SELECTED}

                ${If} $R1 != ${SF_SELECTED}
                    MessageBox MB_ICONSTOP "You cannot uninstall the queue, since watcher on this machine is referring to it."
                    FileClose $4
                    Abort
                ${EndIf}
            ${Else} ; $TempStr != "$HOSTNAME"
                MessageBox MB_ICONSTOP "You cannot uninstall the queue, since other watchers ($TempStr) are dependent on it."
                FileClose $4
                Abort
            ${EndIf}
          
            Goto ReadWAgain
          
        endOfWFile:
          FileClose $4

        nextCheck:

        ; get dependent extractors
        Delete "$INSTDIR\${INSTALLER}\logs\depExtractorHosts.txt"

        ExecDos::exec '"$INSTDIR\${JAVA}\bin\java" -cp "$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\lib\*";"$INSTDIR\${COMMON}\${DB_CONNECTION_TEST}\conf" com.nextlabs.TestConnection selectQuery $DBDriver $DBURL $DBUsername $DBPasswordEncrypted ENC "HOSTNAME" "$INSTDIR\${INSTALLER}\sql\DependentExtractorHosts.sql" "$INSTDIR\${INSTALLER}\logs\depExtractorHosts.txt"' "" ""
        Pop $0
        StrCmp $0 0 EFetchSuccess EFetchFailure
      
        EFetchFailure:
            MessageBox MB_ICONSTOP "Could not get the list of extractors dependent on this queue!!!"
            Quit
      
        EFetchSuccess:
            IfFileExists "$INSTDIR\${INSTALLER}\logs\depExtractorHosts.txt" 0 endOfIf
            
            FileOpen $4 "$INSTDIR\${INSTALLER}\logs\depExtractorHosts.txt" r
            IfErrors CantOpenEFileError ReadEAgain
        
            CantOpenEFileError:
              MessageBox MB_ICONSTOP "Could not open the file $INSTDIR\${INSTALLER}\logs\depExtractorHosts.txt for reading."
              Quit
          
            ReadEAgain:
                FileRead $4 $1
                IfErrors endOfEFile

                StrCpy $TempStr ""
                ${Trim} $TempStr $1
          
                ${If} $TempStr == "$HOSTNAME"

                    SectionGetFlags ${SEC_UN_EXTRACTOR} $R1
                    IntOp $R1 $R1 & ${SF_SELECTED}
            
                    ${If} $R1 != ${SF_SELECTED}
                        MessageBox MB_ICONSTOP "You cannot uninstall the queue, since extractor on this machine is referring to it."
                        FileClose $4
                        Abort
                    ${EndIf}
                ${Else} ;$TempStr != "$HOSTNAME"
                    MessageBox MB_ICONSTOP "You cannot uninstall the queue, since other extractor ($TempStr) are dependent on it."
                    FileClose $4
                    Abort
                ${EndIf}
          
                Goto ReadEAgain
          
            endOfEFile:
              FileClose $4

       endOfIf:
    ${EndIf}
    
  FunctionEnd

  Function un.postInstall
    ${TimeStamp} $1
    StrCpy $uninstallLogFileName "$TEMP\SmartClassifier\uninstall_$1.log"
    DetailPrint "Copying the installation logs to $uninstallLogFileName"
    Push $uninstallLogFileName
    Call un.DumpLog
  FunctionEnd
  

  Function disableBackButton
    ;Disable the back button
    GetDlgItem $0 $hwndparent 3 ;1,2 or 3
    EnableWindow $0 0 ;1 or 
  FunctionEnd

  Function StrTok
  /*After this point:
    ------------------------------------------
    $0 = SkipEmptyParts (input)
    $1 = ResultPart (input)
    $2 = Separators (input)
    $3 = String (input)
    $4 = SeparatorsLen (temp)
    $5 = StrLen (temp)
    $6 = StartCharPos (temp)
    $7 = TempStr (temp)
    $8 = CurrentLoop
    $9 = CurrentSepChar
    $R0 = CurrentSepCharNum
    */

    ;Get input from user
    Exch $0
    Exch
    Exch $1
    Exch
    Exch 2
    Exch $2
    Exch 2
    Exch 3
    Exch $3
    Exch 3
    Push $4
    Push $5
    Push $6
    Push $7
    Push $8
    Push $9
    Push $R0

    ;Parameter defaults
    ${IfThen} $2 == `` ${|} StrCpy $2 `|` ${|}
    ${IfThen} $1 == `` ${|} StrCpy $1 `L` ${|}
    ${IfThen} $0 == `` ${|} StrCpy $0 `0` ${|}

    ;Get "String" and "Separators" length
    StrLen $4 $2
    StrLen $5 $3
    ;Start "StartCharPos" and "ResultPart" counters
    StrCpy $6 0
    StrCpy $8 -1

    ;Loop until "ResultPart" is met, "Separators" is found or
    ;"String" reaches its end
    ResultPartLoop: ;"CurrentLoop" Loop

    ;Increase "CurrentLoop" counter
    IntOp $8 $8 + 1

    StrSearchLoop:
    ${Do} ;"String" Loop
      ;Remove everything before and after the searched part ("TempStr")
      StrCpy $7 $3 1 $6

      ;Verify if it's the "String" end
      ${If} $6 >= $5
        ;If "CurrentLoop" is what the user wants, remove the part
        ;after "TempStr" and itself and get out of here
        ${If} $8 == $1
        ${OrIf} $1 == `L`
          StrCpy $3 $3 $6
        ${Else} ;If not, empty "String" and get out of here
          StrCpy $3 ``
        ${EndIf}
        StrCpy $R0 `End`
        ${ExitDo}
      ${EndIf}

      ;Start "CurrentSepCharNum" counter (for "Separators" Loop)
      StrCpy $R0 0

      ${Do} ;"Separators" Loop
        ;Use one "Separators" character at a time
        ${If} $R0 <> 0
          StrCpy $9 $2 1 $R0
        ${Else}
          StrCpy $9 $2 1
        ${EndIf}

        ;Go to the next "String" char if it's "Separators" end
        ${IfThen} $R0 >= $4 ${|} ${ExitDo} ${|}

        ;Or, if "TempStr" equals "CurrentSepChar", then...
        ${If} $7 == $9
          StrCpy $7 $3 $6

          ;If "String" is empty because this result part doesn't
          ;contain data, verify if "SkipEmptyParts" is activated,
          ;so we don't return the output to user yet

          ${If} $7 == ``
          ${AndIf} $0 = 1 ;${TRUE}
            IntOp $6 $6 + 1
            StrCpy $3 $3 `` $6
            StrCpy $6 0
            Goto StrSearchLoop
          ${ElseIf} $8 == $1
            StrCpy $3 $3 $6
            StrCpy $R0 "End"
            ${ExitDo}
          ${EndIf} ;If not, go to the next result part
          IntOp $6 $6 + 1
          StrCpy $3 $3 `` $6
          StrCpy $6 0
          Goto ResultPartLoop
        ${EndIf}

        ;Increase "CurrentSepCharNum" counter
        IntOp $R0 $R0 + 1
      ${Loop}
      ${IfThen} $R0 == "End" ${|} ${ExitDo} ${|}

      ;Increase "StartCharPos" counter
      IntOp $6 $6 + 1
    ${Loop}

  /*After this point:
    ------------------------------------------
    $3 = ResultVar (output)*/

    ;Return output to user

    Pop $R0
    Pop $9
    Pop $8
    Pop $7
    Pop $6
    Pop $5
    Pop $4
    Pop $0
    Pop $1
    Pop $2
    Exch $3
  FunctionEnd

  Function GetLocalTime
    # Prepare variables
    Push $0
    Push $1
    Push $2
    Push $3
    Push $4
    Push $5
    Push $6
   
    # Call GetLocalTime API from Kernel32.dll
    System::Call '*(&i2, &i2, &i2, &i2, &i2, &i2, &i2, &i2) i .r0'
    System::Call 'kernel32::GetLocalTime(i) i(r0)'
    System::Call '*$0(&i2, &i2, &i2, &i2, &i2, &i2, &i2, &i2)i \
    (.r4, .r5, .r3, .r6, .r2, .r1, .r0,)'
   
    # Day of week: convert to name
    StrCmp $3 0 0 +3
      StrCpy $3 Sunday
        Goto WeekNameEnd
    StrCmp $3 1 0 +3
      StrCpy $3 Monday
        Goto WeekNameEnd
    StrCmp $3 2 0 +3
      StrCpy $3 Tuesday
        Goto WeekNameEnd
    StrCmp $3 3 0 +3
      StrCpy $3 Wednesday
        Goto WeekNameEnd
    StrCmp $3 4 0 +3
      StrCpy $3 Thursday
        Goto WeekNameEnd
    StrCmp $3 5 0 +3
      StrCpy $3 Friday
        Goto WeekNameEnd
    StrCmp $3 6 0 +2
      StrCpy $3 Saturday
    WeekNameEnd:
   
    # Minute: convert to 2 digits format
      IntCmp $1 9 0 0 +2
        StrCpy $1 '0$1'
   
    # Second: convert to 2 digits format
      IntCmp $0 9 0 0 +2
        StrCpy $0 '0$0'
   
    # Return to user
    Exch $6
    Exch
    Exch $5
    Exch
    Exch 2
    Exch $4
    Exch 2
    Exch 3
    Exch $3
    Exch 3
    Exch 4
    Exch $2
    Exch 4
    Exch 5
    Exch $1
    Exch 5
    Exch 6
    Exch $0
    Exch 6
   
  FunctionEnd

  Function isEmptyDir
    # Stack ->                    # Stack: <directory>
    Exch $0                       # Stack: $0
    Push $1                       # Stack: $1, $0
    FindFirst $0 $1 "$0\*.*"
    strcmp $1 "." 0 _notempty
      FindNext $0 $1
      strcmp $1 ".." 0 _notempty
        ClearErrors
        FindNext $0 $1
        IfErrors 0 _notempty
          FindClose $0
          Pop $1                  # Stack: $0
          StrCpy $0 1
          Exch $0                 # Stack: 1 (true)
          goto _end
       _notempty:
         FindClose $0
         ClearErrors
         Pop $1                   # Stack: $0
         StrCpy $0 0
         Exch $0                  # Stack: 0 (false)
    _end:
  FunctionEnd


  Function StrContains
    Exch $STR_NEEDLE
    Exch 1
    Exch $STR_HAYSTACK
    ; Uncomment to debug
    ;MessageBox MB_OK 'STR_NEEDLE = $STR_NEEDLE STR_HAYSTACK = $STR_HAYSTACK '
      StrCpy $STR_RETURN_VAR ""
      StrCpy $STR_CONTAINS_VAR_1 -1
      StrLen $STR_CONTAINS_VAR_2 $STR_NEEDLE
      StrLen $STR_CONTAINS_VAR_4 $STR_HAYSTACK
      loop:
        IntOp $STR_CONTAINS_VAR_1 $STR_CONTAINS_VAR_1 + 1
        StrCpy $STR_CONTAINS_VAR_3 $STR_HAYSTACK $STR_CONTAINS_VAR_2 $STR_CONTAINS_VAR_1
        StrCmp $STR_CONTAINS_VAR_3 $STR_NEEDLE found
        StrCmp $STR_CONTAINS_VAR_1 $STR_CONTAINS_VAR_4 done
        Goto loop
      found:
        StrCpy $STR_RETURN_VAR $STR_NEEDLE
        Goto done
      done:
     Pop $STR_NEEDLE ;Prevent "invalid opcode" errors and keep the
     Exch $STR_RETURN_VAR  
  FunctionEnd