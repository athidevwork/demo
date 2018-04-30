@echo off
rem ###########################################################################
rem # @@@ IMPORTANT @@@
rem # @@@@@@@@@@@@@@@@@
rem # Make sure to increment SCRIPT_VER variable when making changes to this script.
REM ###########################################################################
REM usage:
REM    patch_install.bat conn_string
REM ###########################################################################
REM Date       Dev  Ver    Note
REM ---------- ---- ------ ----------------------------------------------------
REM 07/12/2017 mwy  1.0.0  - Initial version.
REM 09/15/2017 mwy  1.0.1  - Confirm that required zip files for builds listed
REM                          in db_blds.txt files exist.
REM                        - Unzip files before installing.
REM                        - Add the GetDateTime function and add the start/end
REM                          time for each build apply.
REM                        - Remove any "accept x" command from the build 
REM                          install script so it does not wait for user input.
REM                        - Add "exit" to the end of the build install script
REM                          if it is not there.
REM 10/22/2017 myw  1.0.2  - Check for ORA-03135 error in the build log files
REM                          and abort if found.
REM 01/22/2018 myw  1.0.3  - (191024)Stop recording the password to the log file.
REM 02/02/2018 myw  1.0.4  - Call Pre/Post Install scripts before/after applying 
REM                          delivery.
REM                        - Parse the build logs and compare invalids.
REM ###########################################################################

SET SCRIPT_VER=1.0.4

REM Read the connection string from the command line.
@if [%1] == [] GOTO MISSING_DATABASE_CONN_STR
	set DATABASE_CONN_STR=%1

for /f "tokens=1,2 delims=/ " %%a in ("%DATABASE_CONN_STR%") do set DATABASE_NAME=%%a&set PWD_HOST_NAME=%%b
for /f "tokens=1,2 delims=@ " %%a in ("%PWD_HOST_NAME%") do set PASSWORD=%%a&set HOST_NAME=%%b

Set CONNSTR_NO_PWD=%DATABASE_NAME%@%HOST_NAME%

Set BAT_SCRIPT_FLDR=%cd%
set BAT_SCRIPT_NAME=%~n0
set BUILDS_FILE=db_blds.txt
SET LOG_PARSER=%BAT_SCRIPT_FLDR%\parselogs.exe
SET PRE_INSTALL=%BAT_SCRIPT_FLDR%\pre_install.sql
SET POST_INSTALL=%BAT_SCRIPT_FLDR%\post_install.sql

@if not exist %BUILDS_FILE% goto MISSING_BUILDS_LIST

REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
set DATESTR=%YEAR%%MONTH%%DAY%
set TIMESTR=%HOUR%%MIN%%SEC%

set ALL_LOGS_FOLDER=%BAT_SCRIPT_FLDR%\logs\%DATESTR%_%TIMESTR%
@if not exist %ALL_LOGS_FOLDER% mkdir %ALL_LOGS_FOLDER%

REM Log file to capture the messages from this script.
set BAT_SCRIPT_LOG=%ALL_LOGS_FOLDER%\%BAT_SCRIPT_NAME%_%DATABASE_NAME%_%DATESTR%_%TIMESTR%.log
echo %BAT_SCRIPT_NAME% version %SCRIPT_VER%>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
echo Command: %~n0%~x0 %CONNSTR_NO_PWD%>>%BAT_SCRIPT_LOG%
echo DI Log Folder: %ALL_LOGS_FOLDER%>>%BAT_SCRIPT_LOG%

echo.>>%BAT_SCRIPT_LOG%
echo %MONTH%/%DAY%/%YEAR% %HOUR%:%MIN%:%SEC%.%MSEC% - Patch apply started.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Read the list of build folders from a file (db_blds.txt), this file will be prepared by RE.
REM The builds will be applied in the same oder as in this list.
Set "BUILD_LIST_ORIG="
Set "BUILD_LIST="
set /A COUNTER=0
REM for /F "tokens=*" %%a in (builds.txt) do call set "BUILD_LIST=%%BUILD_LIST%%,%%a"
for /F "tokens=*" %%a in (%BUILDS_FILE%) do (
    call set "BUILD_LIST_ORIG=%%BUILD_LIST_ORIG%%,%%a"
    call set /A "COUNTER=%%COUNTER%%+1"
)
REM subtract one from the DI_BUILD_COUNTS
set /A DI_BUILDS_COUNT=%COUNTER%-1
echo DI Builds Count: %DI_BUILDS_COUNT%.>>%BAT_SCRIPT_LOG%

REM Create the Delivery_Apply_Summary.log file.
set DI_APPLY_LOG=%ALL_LOGS_FOLDER%\Delivery_Apply_Summary.log
SET PADDING=..........................................
echo This delivery includes [%DI_BUILDS_COUNT%] build(s).>>%DI_APPLY_LOG%
echo BUILD ID                                  RESULTS >>%DI_APPLY_LOG%
echo ----------------------------------------  -------->>%DI_APPLY_LOG%

REM drop the first comma and add one to the end of the string.
REM This is needed for the loop over the build list to work.
set BUILD_LIST_ORIG=%BUILD_LIST_ORIG:~1%,

REM Get the DI# and drop it from the BUILD_LIST_ORIG.
for /f "tokens=1,* delims=, " %%a in ("%BUILD_LIST_ORIG%") do set TMP_DI=%%a&&set TMP_BUILD_LIST=%%b

if ["%TMP_DI:~0,2%"]==["DI"] (
	REM echo inside IF...
	set DI=%TMP_DI%
	set BUILD_LIST_ORIG=%TMP_BUILD_LIST%
) else (
	goto MISSING_DI_NUMBER
)

echo *******************************************************************************
echo *** NOTICE TO USERS:
echo *** =================
echo *** Applying Delivery Issue# %DI%.
echo ***
echo *** Connection: %CONNSTR_NO_PWD%
echo ***
echo *** If the above connection information is correct, press return for
echo *** installation to take place.
echo ***
echo *** If you are not connected to the correct schema, press Control C to abort.
echo *******************************************************************************

set /P userinput=


set ALL_LOGS_ZIP=%DI%_%DATABASE_NAME%_%DATESTR%_%TIMESTR%.zip

echo DI#: %DI%>>%BAT_SCRIPT_LOG%
echo Builds List: %BUILD_LIST_ORIG%>>%BAT_SCRIPT_LOG%
echo All Logs Zip File: %ALL_LOGS_ZIP%>>%BAT_SCRIPT_LOG%

echo.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Checking the ZIP files.
REM =======================
REM BUILD_LIST will be used as a loop itrator.
set BUILD_LIST=%BUILD_LIST_ORIG%

echo vvvv Check ZIP files - Started vvvv>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

Set "MISSING_ZIP="
:CHKFILESLOOP
@if ["%BUILD_LIST%"] == [""] goto ENDCHKFILES

for /f "tokens=1,* delims=, " %%a in ("%BUILD_LIST%") do set CUR_BUILD=%%a&set BUILD_LIST=%%b

set CUR_ZIP_FILE=%CUR_BUILD%.zip
REM echo Zip File: %CUR_ZIP_FILE%

@if exist %CUR_BUILD% rmdir /s /q %CUR_BUILD%

@if not exist %CUR_ZIP_FILE% set MISSING_ZIP=%CUR_ZIP_FILE%,%MISSING_ZIP%

goto CHKFILESLOOP

:ENDCHKFILES
if not "%MISSING_ZIP%" == "" goto MISSING_ZIP_FILES

echo All ZIP files exist.>>%BAT_SCRIPT_LOG%

echo.>>%BAT_SCRIPT_LOG%
echo ^^^^^^^^ Check ZIP files - Completed ^^^^^^^^>>%BAT_SCRIPT_LOG%

REM Unzipping all builds.
REM =======================
REM re-initialize BUILD_LIST variable.
set BUILD_LIST=%BUILD_LIST_ORIG%

echo vvvv Unzipping all builds - Started vvvv>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

:UNZIPLOOP

@if ["%BUILD_LIST%"] == [""] goto ENDUNZIP

for /f "tokens=1,* delims=, " %%a in ("%BUILD_LIST%") do set CUR_BUILD=%%a&set BUILD_LIST=%%b
set CUR_ZIP_FILE=%CUR_BUILD%.zip
echo ...extracting %CUR_ZIP_FILE%>>%BAT_SCRIPT_LOG%
unzip -o -q %CUR_ZIP_FILE% 1>nul

goto UNZIPLOOP

:ENDUNZIP
echo.>>%BAT_SCRIPT_LOG%
echo ^^^^^^^^ Unzipping all builds - Completed ^^^^^^^^>>%BAT_SCRIPT_LOG%

REM Check if the last line of the pre/post install scripts is the exit command.
REM If not, add the "exit" command as the last line.

REM Modifying PRE_INSTALL Script
set "lastline="

for /F "tokens=1*" %%a in ('findstr /v /r /c:"^$" %PRE_INSTALL%') do set lastline=%%a

if not ["%lastline%"]==["exit"] (
	echo Modify [%PRE_INSTALL%] script, insert exit command at the end.>>%BAT_SCRIPT_LOG%
	echo.>>%BAT_SCRIPT_LOG%
	echo.>>%PRE_INSTALL%
	echo exit>>%PRE_INSTALL%
)

REM Modifying POST_INSTALL Script
set "lastline="
REM <findstr /v /r /c:"^$" %INSTALL_SCRIPT%> returns only non-empty lines.
for /F "tokens=1*" %%a in ('findstr /v /r /c:"^$" %POST_INSTALL%') do set lastline=%%a

if not ["%lastline%"]==["exit"] (
	echo Modify [%POST_INSTALL%] script, insert exit command at the end.>>%BAT_SCRIPT_LOG%
	echo.>>%BAT_SCRIPT_LOG%
	echo.>>%POST_INSTALL%
	echo exit>>%POST_INSTALL%
)

REM Run Pre_Install.sql script
REM ==========================
echo.
echo **************************************
echo *** Pre-install check list - Started
echo **************************************
echo.
echo Running pre_install script [%PRE_INSTALL%].>>%BAT_SCRIPT_LOG%
echo Command: sqlplus -l %CONNSTR_NO_PWD% @%PRE_INSTALL%>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
sqlplus -l %DATABASE_CONN_STR% @%PRE_INSTALL%
echo.
echo **************************************
echo *** Pre-install check list - Completed
echo **************************************
echo.
echo *******************************************************************************
echo *** NOTICE TO USERS:
echo *** =================
echo *** Please review the results of the pre-install check list above and make any 
echo *** recommended changes before continuing.
echo ***
echo *** When done, please press return for installation to take place or press 
echo *** Control C to abort.
echo *******************************************************************************

set /P userinput=


REM Applying builds.
REM ================
REM re-initialize BUILD_LIST variable.
set BUILD_LIST=%BUILD_LIST_ORIG%


echo vvvv Patch install - Started vvvv>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

SET /A cnt=0
SET /A total_errcnt=0
REM Loop through the build list and jump to the RUNBUILD section.
:BUILDLOOP
@if ["%BUILD_LIST%"] == [""] goto ENDLOOP

REM set the build counter in the format 001, 002, etc
set /A cnt+=1
set FORMATED_COUNTER=000%cnt%
set FORMATED_COUNTER=%FORMATED_COUNTER:~-3%

REM get the first build from the list.
for /f "tokens=1,* delims=, " %%a in ("%BUILD_LIST%") do set CUR_BUILD_FLDR=%%a&set BUILD_LIST=%%b
echo.>>%BAT_SCRIPT_LOG%
echo ##############################################################################>>%BAT_SCRIPT_LOG%
echo #### %FORMATED_COUNTER% - Applying %CUR_BUILD_FLDR% ####>>%BAT_SCRIPT_LOG%
echo ##############################################################################>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
echo.>>%BAT_SCRIPT_LOG%
echo %MONTH%/%DAY%/%YEAR% %HOUR%:%MIN%:%SEC%.%MSEC% - Build apply started.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Get the build id from the folder name
for /f "tokens=1,2 delims=_ " %%a in ("%CUR_BUILD_FLDR%") do set CUR_BUILD_ID=%%a
REM CALL :GetBuildID CUR_BUILD_ID
REM echo After calling :GetBuildID - Build ID is %CUR_BUILD_ID%
set INSTALL_SCRIPT=%CUR_BUILD_ID%_install.sql

cd %CUR_BUILD_FLDR%

echo.>>%BAT_SCRIPT_LOG%
echo Apply Order: %FORMATED_COUNTER%>>%BAT_SCRIPT_LOG%
echo Build: %CUR_BUILD_ID%>>%BAT_SCRIPT_LOG%
echo Build_ID Folder: %CUR_BUILD_FLDR%>>%BAT_SCRIPT_LOG%
echo Build Install Script: %INSTALL_SCRIPT%>>%BAT_SCRIPT_LOG%
echo Build_ID List: %BUILD_LIST%>>%BAT_SCRIPT_LOG%

REM Create the log folder if it does not exist.
set LOG_FOLDER_NAME=.\logs\applies
set LOG_FOLDER_NAME=%LOG_FOLDER_NAME%\%DATABASE_NAME%\%DATESTR%_%TIMESTR%

echo Build Log Folder: %LOG_FOLDER_NAME%>>%BAT_SCRIPT_LOG%

@if not exist %LOG_FOLDER_NAME% mkdir %LOG_FOLDER_NAME%

set ZIPFILENAME=%FORMATED_COUNTER%_%CUR_BUILD_FLDR%_%DATABASE_NAME%_%HOST_NAME%.zip
echo Build Log File: %ZIPFILENAME%>>%BAT_SCRIPT_LOG%

REM Start applying build
echo.>>%BAT_SCRIPT_LOG%
echo Applying %CUR_BUILD_ID% - Started>>%BAT_SCRIPT_LOG%
echo.

set INSTALL_BAK=%INSTALL_SCRIPT%.bak
if not exist %INSTALL_BAK% (
	echo.>>%BAT_SCRIPT_LOG%
	echo Modify build install script [%INSTALL_SCRIPT%], remove accept command.>>%BAT_SCRIPT_LOG%
	move /Y %INSTALL_SCRIPT% %INSTALL_BAK% 1>nul
	echo -- patch_install: This file was modified by the patch_install.bat script.>%INSTALL_SCRIPT%
	findstr /B /I /V /C:"accept" /C:"-- patch_install:" %INSTALL_BAK%>>%INSTALL_SCRIPT%
)

REM Check if the last line of the install script is the exit command.
REM If not, add the "exit" command at the end of the install script.
set "lastline="
REM <findstr /v /r /c:"^$" %INSTALL_SCRIPT%> returns only non-empty lines.
for /F "tokens=1*" %%a in ('findstr /v /r /c:"^$" %INSTALL_SCRIPT%') do set lastline=%%a

if not ["%lastline%"]==["exit"] (
	echo Modify build install script [%INSTALL_SCRIPT%], insert exit command at the end.>>%BAT_SCRIPT_LOG%
	echo.>>%BAT_SCRIPT_LOG%
	echo exit>>%INSTALL_SCRIPT%
)

REM Run the install script
echo Running the install script [%INSTALL_SCRIPT%].>>%BAT_SCRIPT_LOG%
echo Command: sqlplus -l %CONNSTR_NO_PWD% @%INSTALL_SCRIPT%>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
sqlplus -l %DATABASE_CONN_STR% @%INSTALL_SCRIPT%

REM Parse log files for any errors.
set LOGPARSER_OUTPUT=log_parser_%CUR_BUILD_ID%.log

echo Parsing log files, results are written to %LOGPARSER_OUTPUT% file>>%BAT_SCRIPT_LOG%
echo Command: %LOG_PARSER% -c -p %LOG_FOLDER_NAME% -o %LOGPARSER_OUTPUT%>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
FOR /F %%v IN ('%LOG_PARSER% -c -p %LOG_FOLDER_NAME% -o %LOGPARSER_OUTPUT%') DO SET /a build_errcnt=%%v
set /a total_errcnt=%total_errcnt%+%build_errcnt%

echo Copying and zipping the log files.>>%BAT_SCRIPT_LOG%
REM copy and ZIP log files.
REM copy *.log and *.lis files to the log folder
copy /Y *.log %LOG_FOLDER_NAME% 1>nul
copy /Y *.lis %LOG_FOLDER_NAME% 1>nul
REM copy /Y %LOGPARSER_OUTPUT% %LOG_FOLDER_NAME% 1>nul

cd %LOG_FOLDER_NAME%
for /F "tokens=*" %%a in ('dir /b *.log') do (zip -r -p "%ZIPFILENAME%" "%%a") 1>nul
for /F "tokens=*" %%a in ('dir /b *.lis') do (zip -r -p "%ZIPFILENAME%" "%%a") 1>nul
copy %ZIPFILENAME% %ALL_LOGS_FOLDER% 1>nul

REM Check for connecion lost error in the log files.
set /A conn_lost_errcnt=0
REM Cannot replace the find command below with findstr. "find /c" returns the count which has no equevelent in findstr.
for /f "tokens=1,2 delims=_ " %%a in ('findstr /I /N "ORA-03135" *.log ^| find /C ":"') do set conn_lost_errcnt=%%a

cd %BAT_SCRIPT_FLDR%

REM Results of the error checking.
set PADDED_BUILD_ID_TMP=%CUR_BUILD_ID%%PADDING%
SET PADDED_BUILD_ID=%PADDED_BUILD_ID_TMP:~0,42%
IF %build_errcnt%==0 (
  echo.>>%BAT_SCRIPT_LOG%
  echo Applying %CUR_BUILD_ID% - Completed with no errors.>>%BAT_SCRIPT_LOG%
  echo.>>%BAT_SCRIPT_LOG%
  echo %PADDED_BUILD_ID%OK>>%DI_APPLY_LOG%
) else (
  echo.>>%BAT_SCRIPT_LOG%
  echo Applying %CUR_BUILD_ID% - Completed with errors.>>%BAT_SCRIPT_LOG%
  echo Check %LOGPARSER_OUTPUT% for more details.>>%BAT_SCRIPT_LOG%
  echo.>>%BAT_SCRIPT_LOG%
  echo %PADDED_BUILD_ID%ERROR>>%DI_APPLY_LOG%
)

REM Results of the Oracle connection lost error checking.
IF %conn_lost_errcnt%==0 (
  echo.>>%BAT_SCRIPT_LOG%
  echo Checked for ORA-03135 [connection lost], none were found. >>%BAT_SCRIPT_LOG%
  echo.>>%BAT_SCRIPT_LOG%
) else (
  echo %conn_lost_errcnt%
  goto CONNECTION_LOST
)

REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
echo.>>%BAT_SCRIPT_LOG%
echo %MONTH%/%DAY%/%YEAR% %HOUR%:%MIN%:%SEC%.%MSEC% - Build apply completed.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Jump back to the BUILDLOOP section to see if any more builds to install.
goto BUILDLOOP

:ENDLOOP
REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
echo.>>%BAT_SCRIPT_LOG%
echo %MONTH%/%DAY%/%YEAR% %HOUR%:%MIN%:%SEC%.%MSEC% - Patch apply completed.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

echo.>>%BAT_SCRIPT_LOG%
echo ^^^^^^^^ Patch install - Completed ^^^^^^^^>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%

REM Run Post_Install.sql script
REM =============================
REM Run Pre_Install.sql script
REM ==========================
echo.
echo.
echo.
echo **************************************
echo *** Post_install script - Started
echo **************************************
echo.

echo Running pre_install script [%POST_INSTALL%].>>%BAT_SCRIPT_LOG%
echo Command: sqlplus -l %CONNSTR_NO_PWD% @%POST_INSTALL%>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
sqlplus -l %DATABASE_CONN_STR% @%POST_INSTALL%
echo.
echo **************************************
echo *** Post_install script - Completed
echo **************************************
echo.

REM Move the pre_install.log and post_install.log into the log folder.
REM ==================================================================
echo.
@move /Y *_install.log %ALL_LOGS_FOLDER% 1>nul

echo.
echo ******************************
echo *** Delivery Apply Summary ***
echo ******************************
more %DI_APPLY_LOG%
echo.
echo *********************************************************************

echo *********************************************************************>>%BAT_SCRIPT_LOG%
IF %total_errcnt% GTR 0 (
REM write to log file
	echo * WARNINIG >>%BAT_SCRIPT_LOG%
	echo * ========>>%BAT_SCRIPT_LOG%
	echo * Patch install was completed with errors. Please contact Release Engineering to review the logs.>>%BAT_SCRIPT_LOG%
REM Display on the screen
	echo * WARNINIG
	echo * ========
	echo * Patch install was completed with errors. Please contact Release Engineering to review the logs.
) else (
REM write to log file
	echo * Patch install was completed successfully. You do NOT need to contact>>%BAT_SCRIPT_LOG%
	echo * Release Engineering to review the logs.>>%BAT_SCRIPT_LOG%
REM Display on the screen
	echo * Patch install was completed successfully. You do NOT need to contact
	echo * Release Engineering to review the logs.
)
REM write to log file
echo *>>%BAT_SCRIPT_LOG%
echo * For your convenience, all log files are included in this zip file.>>%BAT_SCRIPT_LOG%
echo * %ALL_LOGS_FOLDER%\%ALL_LOGS_ZIP%>>%BAT_SCRIPT_LOG%
echo *********************************************************************>>%BAT_SCRIPT_LOG%
REM Display on the screen
echo *
echo * For your convenience, all log files are included in this zip file.
echo * %ALL_LOGS_FOLDER%\%ALL_LOGS_ZIP%
echo *********************************************************************

cd %ALL_LOGS_FOLDER%

for /F "tokens=*" %%a in ('dir /b *.zip') do (zip -r -p -m "%ALL_LOGS_ZIP%" "%%a") 1>nul
for /F "tokens=*" %%a in ('dir /b *.log') do (zip -r -p -m "%ALL_LOGS_ZIP%" "%%a") 1>nul
cd %BAT_SCRIPT_FLDR%

goto END

REM ********************
REM ** ERROR HANDLING **
REM ********************
:MISSING_DATABASE_CONN_STR
@echo The 1st parameter is required to specify the database connection string in the format [user_id/password@host_name]
REM the "."is needed to print a blank line.
echo.
@echo USAGE: %~n0 conn_string
goto FAILURE

:MISSING_BUILDS_LIST
@echo The %BUILDS_FILE% file is missing, please contact Release Engineering.
REM the "."is needed to print a blank line.
echo.
goto FAILURE

:MISSING_DI_NUMBER
REM add the failure message into the log file.
@echo Missing DI number from %BUILDS_FILE% file, please contact Release Engineering.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
REM display the failure message on the screen.
@echo Missing DI number from %BUILDS_FILE% file, please contact Release Engineering.
echo.
goto FAILURE

:MISSING_ZIP_FILES
REM add the failure message into the log file.
@echo The following zip files are missing, >>%BAT_SCRIPT_LOG%
@echo Missing ZIP: %MISSING_ZIP%>>%BAT_SCRIPT_LOG%
@echo Please contact Release Engineering.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
REM display the failure message on the screen.
@echo The following zip files are missing,
@echo Missing ZIP: %MISSING_ZIP%
@echo Please contact Release Engineering.
echo.
goto FAILURE

:CONNECTION_LOST
REM add the failure message into the log file.
@echo Connection lost while applying "%CUR_BUILD_ID%". >>%BAT_SCRIPT_LOG%
@echo ERRORS: >>%BAT_SCRIPT_LOG%
@echo ORA-03135: connection lost contact. >>%BAT_SCRIPT_LOG%
@echo ORA-03114: not connected to ORACLE. >>%BAT_SCRIPT_LOG%
@echo Please contact Release Engineering.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
REM display the failure message on the screen.
@echo Connection lost whil applying "%CUR_BUILD_ID%".
@echo ERRORS:
@echo ORA-03135: connection lost contact.
@echo ORA-03114: not connected to ORACLE.
@echo Please contact Release Engineering.
echo.
goto FAILURE

:FAILURE
REM add the failure message into the log file.
@echo %BAT_SCRIPT_NAME% failed.>>%BAT_SCRIPT_LOG%
echo.>>%BAT_SCRIPT_LOG%
REM display the failure message on the screen.
@echo %BAT_SCRIPT_NAME% failed.
echo.
@set RETURN=-1

:END
echo Finish
@echo on

@echo off
REM This exit command is need to prevent running the function code without being called.
EXIT /B %ERRORLEVEL%
@echo on
REM *****************
REM **  FUNCTIONS  **
REM *****************
:GetDateTime
REM Get Date String
for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (
	 REM DOW
     set "%~1=%%i"
	 REM MONTH
     set "%~2=%%j"
	 REM DAY
     set "%~3=%%k"
	 REM YEAR
     set "%~4=%%l"
)

REM Get Time String
for /f "tokens=1-4 delims=: " %%i in ("%time%") do (
     REM HOUR
	 set "%~5=%%i"
	 REM MIN
     set "%~6=%%j"
	 REM Second and Millsecond
     set sec_milSec=%%k
)

REM drop the milliseconds
for /f "tokens=1,2 delims=. " %%i in ("%sec_milSec%") do (
	REM SEC
	set "%~7=%%i"
	REM MSEC
	set "%~8=%%j"
)

EXIT /B 0

:GetBuildID
set "stripped_bid=%CUR_BUILD_FLDR%"
echo 0 - %stripped_bid%
:repeat
echo.
for /f "tokens=1,2 delims=_" %%a in ("%stripped_bid%") do set bld_id=%%a&set stripped_bid=%%b
set prestripped_bid=%stripped_bid%
set stripped_bid=%stripped_bid:*_=%
call set "bld_id=%%bld_id%%_%%stripped_bid%%"
set %~1=%bld_id%

if not "%prestripped_bid:_=%"=="%prestripped_bid%" goto :repeat

REM EXIT /B 0