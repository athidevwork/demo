set oracleSchema=odev20181/ODEV20181@NY2ORA12CR1D_SE12CR1
set scriptName=@runOsFormInterfaceConfig.sql

echo sqlplus.exe %oracleSchema% %scriptName% 'GHOSTDRAFT' 'delphidev' 'ODEV20181' 'ODEV20181'
sqlplus.exe %oracleSchema% %scriptName% 'delphidev' 'ODEV20181' 'ODEV20181'
