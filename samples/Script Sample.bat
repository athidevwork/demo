@echo off
SET SCRIPT_VER=1.0.0

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
