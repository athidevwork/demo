@echo off

set installScriptName=@runPackageInstall.sql

@if [%1] == [] GOTO MISSING_RUN_OPTION

set oracleSchema=%1
set runOption=%2
2>NUL CALL :CASE_%2
IF ERRORLEVEL 1 CALL :DEFAULT_CASE
GOTO:END_CASE

:CASE_install
  echo sqlplus.exe %oracleSchema% %installScriptName%
  sqlplus.exe %oracleSchema% %installScriptName%
  rem sqlplus.exe %oracleSchema% %packageInstallScriptName%
  rem echo Running install again to clean up errors on previous install
  rem sqlplus.exe %oracleSchema% %packageInstallScriptName%
  GOTO:END_CASE  
:DEFAULT_CASE
  ECHO Unknown option %runOption%
  GOTO:END_CASE
:END_CASE
  VER > NUL # reset ERRORLEVEL
  GOTO :EOF # return from CALL  

:MISSING_RUN_OPTION
echo One command line parameter required : oracle_connection runMode (valid values : install)
exit /b 1



