@echo off
SET SCRIPT_VER=1.0.0

REM buildConfigProp.bat //cust/MAG/OASIS/db/2018.1.7/ConfigProp CPMAGM1113

REM Read the config prop path.
@if [%1] == [] GOTO MISSING_CONFIG_PROP_PATH
	set CONFIG_PROP_PATH=%1

REM Read the config prop label.
@if [%2] == [] GOTO MISSING_CONFIG_PROP_LABEL
	set CONFIG_PROP_LABEL=%2

echo CONFIG_PROP_PATH = %CONFIG_PROP_PATH%
echo CONFIG_PROP_LABEL = %CONFIG_PROP_LABEL%

REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
set DATESTR=%YEAR%%MONTH%%DAY%
set TIMESTR=%HOUR%%MIN%%SEC%

Set BAT_SCRIPT_FLDR=%cd%
set BAT_SCRIPT_NAME=%~n0
set ALL_LOGS_FOLDER=%BAT_SCRIPT_FLDR%\logs\%DATESTR%_%TIMESTR%
set BAT_SCRIPT_LOG=%ALL_LOGS_FOLDER%\%BAT_SCRIPT_NAME%_%DATESTR%_%TIMESTR%.log

p4 info | find "Client root:"

for /F "tokens=1,* delims=root: " %%a in ('p4 info ^| find "Client root:"') do set CLIENT_ROOT=%%b

echo CLIENT ROOT = %CLIENT_ROOT%

set mystartingpath=%cd%
echo Starting directory %cd%

set REPLACED_PATH=%CONFIG_PROP_PATH:/=\%
REM echo %REPLACED_PATH%
set TRANSLATED_PATH=%REPLACED_PATH:\\=%
REM echo %TRANSLATED_PATH%
cd %CLIENT_ROOT%\%TRANSLATED_PATH%
echo Present directory %cd%

p4 files @%CONFIG_PROP_LABEL%

for /f %%a in ('p4 files @%CONFIG_PROP_LABEL%') do set FILES_LIST=%FILES_LIST% %%a
echo %FILES_LIST%

echo Processing p4 sync -f %CONFIG_PROP_PATH%/...@%CONFIG_PROP_LABEL%
p4 sync -f %CONFIG_PROP_PATH%/...@%CONFIG_PROP_LABEL%

echo starting zip
zip z * 
move z.zip %mystartingpath%/%CONFIG_PROP_LABEL%.zip

cd %mystartingpath%
goto END

REM ********************
REM ** ERROR HANDLING **
REM ********************
:MISSING_CONFIG_PROP_PATH
@echo The 1st parameter is required to specify the config prop perforce path with the format [//cust/MAG/OASIS/db/2018.1.7/ConfigProp/]
REM the "."is needed to print a blank line.
echo.
@echo USAGE: %~n0 config_prop_path config_prop_label
goto FAILURE

:MISSING_CONFIG_PROP_LABEL
@echo The 2nd parameter is required to specify the config prop perforce label with the format [CPMAG1113]
REM the "."is needed to print a blank line.
echo.
@echo USAGE: %~n0 config_prop_path config_prop_label
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
echo Build Completed
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
