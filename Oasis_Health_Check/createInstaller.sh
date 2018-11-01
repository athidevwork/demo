#!/bin/bash

# to create a spec file - first time
# pyinstaller --onefile OasisHealthCheck.py
# pyinstaller --onefile OasisHealthCheck.spec

#remove old package dir
rm -fr dist/Oasis_Health_Check
rm -f dist/Oasis_Health_Check.zip
rm -f pkg/*.~pkg

if [ "$1" = "rebuild" ]; then
    pyinstaller --clean -F OasisHealthCheck.py
else
    pyinstaller -F OasisHealthCheck.py
fi

# package the files
cd dist
mkdir Oasis_Health_Check
mv OasisHealthCheck.exe Oasis_Health_Check
cp ../oasishealthcheckenv.txt Oasis_Health_Check
cp ../"OASIS HealthCheck User Guide.docx" Oasis_Health_Check
cp -r ../scripts Oasis_Health_Check
cp -r ../pkg Oasis_Health_Check
cd Oasis_Health_Check/scripts
#rm -f "insertHealthCheckEnv-Delphi.sql"
#rm -f "insertHealthCheckEnv - Delphi.sql"
#rm -f *Delphi*.sql
rm -f *Delphi*.* 
cd ../..

zip -r Oasis_Health_Check.zip ./*
rm -fr Oasis_Health_Check
