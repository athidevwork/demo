#!/bin/bash

# to create a spec file - first time
# pyinstaller --onefile DocuproFormsTest.py
# pyinstaller --onefile DocuproFormsTest.spec

#remove old package dir
rm -fr dist/Docupro_Forms_Test
rm -f dist/Docupro_Forms_Test.zip

if [ "$1" = "rebuild" ]; then
    pyinstaller --clean -F DocuproFormsTest.py
else
    pyinstaller -F DocuproFormsTest.py
fi

# package the files
cd dist
mkdir Docupro_Forms_Test
mv DocuproFormsTest.exe Docupro_Forms_Test
cp ../DocuproFormsTestenv.txt Docupro_Forms_Test
cp ../"DocuproFormsTest User Guide.docx" Docupro_Forms_Test
cp -r ../scripts Docupro_Forms_Test
cd Docupro_Forms_Test/scripts
cd ../..

zip -r Docupro_Forms_Test.zip ./*
rm -fr Docupro_Forms_Test
