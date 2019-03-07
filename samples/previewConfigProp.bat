@echo off
SET SCRIPT_VER=1.0.0

REM deployConfigProp.bat CPMAGM1113.zip

REM Read the config prop path.
@if [%1] == [] GOTO MISSING_CONFIG_LABEL
	set CONFIG_PROP_LABEL=%1

@if not exist %CONFIG_PROP_LABEL%.zip goto MISSING_ZIP_FILE

REM Get Date String
call :GetDateTime DOW,MONTH,DAY,YEAR,HOUR,MIN,SEC,MSEC
set DATESTR=%YEAR%%MONTH%%DAY%
set TIMESTR=%HOUR%%MIN%%SEC%

Set BAT_SCRIPT_FLDR=%cd%
set BAT_SCRIPT_NAME=%~n0
set ALL_LOGS_FOLDER=%BAT_SCRIPT_FLDR%\logs\%DATESTR%_%TIMESTR%
set BAT_SCRIPT_LOG=%ALL_LOGS_FOLDER%\%BAT_SCRIPT_NAME%_%DATESTR%_%TIMESTR%.log

set mystartingpath=%cd%
echo Starting directory %cd%

echo CONFIG_PROP_LABEL = %CONFIG_PROP_LABEL%
unzip.exe  %CONFIG_PROP_LABEL%.zip -d  %CONFIG_PROP_LABEL%

cd %mystartingpath%
goto END

REM ********************
REM ** ERROR HANDLING **
REM ********************
:MISSING_CONFIG_LABEL
@echo The 1st parameter is required to specify the config prop perforce label in the format [CPMAGM1113]
REM the "."is needed to print a blank line.
echo.
@echo USAGE: %~n0 config_prop_label
goto FAILURE

:MISSING_ZIP_FILE
@echo The %CONFIG_PROP_LABEL%.zip file is missing, please check your config label or for existence of the file.
REM the "."is needed to print a blank line.
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
echo Deploy Completed
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