@echo off

REM ==============================CONFIG START========================================
REM NEED TO POINT THIS VARIABLE TO THE CORRECT SCHEMA and updated EMAIL.
set oracleSchema=UAT/UAT@NY2ORA12CPOC01_RE201XSE
rem set emailAddr=
set fileName=report.csv
REM ===============================CONFIG END=========================================

REM ======================No updates needed below this line==========================
set installScriptName=@scripts/runInstallHealthCheck.sql
set uninstallScriptName=@scripts/runUninstallHealthCheck.sql
set runHealthCheckscriptName=@scripts/runHealthCheck.sql
set packageInstallScriptName=@scripts/runPackageInstallHealthCheck.sql
set lastReportScriptName=@scripts/getLastOasisHealthCheckReport.sql
set reportByDateScriptName=@scripts/getReportByDateOasisHealthCheckReport.sql
set allReportScriptName=@scripts/getAllOasisHealthCheckReport.sql

@if [%1] == [] GOTO MISSING_RUN_OPTION

set runOption=%1
set reportdate=%2
2>NUL CALL :CASE_%1
IF ERRORLEVEL 1 CALL :DEFAULT_CASE
GOTO:END_CASE

:CASE_install
  echo sqlplus.exe %oracleSchema% %installScriptName%
  sqlplus.exe %oracleSchema% %installScriptName%
  sqlplus.exe %oracleSchema% %packageInstallScriptName%
  echo Running install again to clean up errors on previous install
  sqlplus.exe %oracleSchema% %packageInstallScriptName%
  GOTO:END_CASE
:CASE_uninstall
  echo sqlplus.exe %oracleSchema% %uninstallScriptName%
  sqlplus.exe %oracleSchema% %uninstallScriptName%
  GOTO:END_CASE
:CASE_run
  rem echo sqlplus.exe %oracleSchema% %runHealthCheckscriptName% %emailAddr%
  rem sqlplus.exe %oracleSchema% %runHealthCheckscriptName% %emailAddr%
  echo sqlplus.exe %oracleSchema% %runHealthCheckscriptName%
  sqlplus.exe %oracleSchema% %runHealthCheckscriptName% 
  GOTO:END_CASE
:CASE_lastreport
  echo sqlplus.exe %oracleSchema% %lastReportScriptName% %fileName%
  sqlplus.exe %oracleSchema% %lastReportScriptName% %fileName%
  GOTO:END_CASE
:CASE_reportfordate
  echo sqlplus.exe %oracleSchema% %reportByDateScriptName% %fileName% %reportdate%
  sqlplus.exe %oracleSchema% %reportByDateScriptName% %fileName% %reportdate%
  GOTO:END_CASE
:CASE_allreport
  echo sqlplus.exe %oracleSchema% %allReportScriptName% %fileName%
  sqlplus.exe %oracleSchema% %allReportScriptName% %fileName%
  GOTO:END_CASE  
:DEFAULT_CASE
  ECHO Unknown option %runOption%
  GOTO:END_CASE
:END_CASE
  VER > NUL # reset ERRORLEVEL
  GOTO :EOF # return from CALL  

:MISSING_RUN_OPTION
echo One command line parameter required : runMode (valid values : install, uninstall, run, lastreport, reportfordate, allreport)
exit /b 1



