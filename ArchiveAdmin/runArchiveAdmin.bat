@echo off

if "%1" == "" goto insufficient_parameters
if "%2" == "" goto sufficient_parameters

:insufficient_parameters
echo
echo one command line argument required : action
echo "runArchivePdf.bat <metadata|purgedata|archivedata|sendreports>"
echo
exit /b 1

:sufficient_parameters

set option=%1
rem set program="dist/archiveAdmin.exe"
set program="python archiveAdmin.py"

rem echo option = %option%

if "%option%" == "metadata"  (
	echo %program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --get_metadata 52 Y
	%program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --get_metadata 52 Y
	)

IF "%option%" == "archivedata" (
	echo %program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --preview_pdf_archive 52 Y
	%program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --preview_pdf_archive 52 Y
)

IF "%option%" == "purgedata" (
	echo %program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --purge_db_data 52 Y
	%program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --purge_db_data 52 Y
)

IF "%option%" == "sendreports" (
	echo %program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --send_reports 52 Y
	%program% -c LIFESP/LIFESP20141@LIFESP20141.ASP.AD.DTI -i lifespanProd12232013pdfMetadata.csv -o output.txt -m athi.muthukumarasamy@delphi-tech.com --send_reports 52 Y
)