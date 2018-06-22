#!/usr/bin/python
import argparse
import csv
import re
import os
import sys
import shutil
import logging
import subprocess
import os.path
import cx_Oracle

from pathlib import Path
from shutil import copyfile
from datetime import datetime
from os.path import exists
from collections import defaultdict

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders

__author__ = 'athi'

import zipfile

def zipdir(path, ziph):
    # ziph is zipfile handle
    for root, dirs, files in os.walk(path):
        for file in files:
            ziph.write(os.path.join(root, file))

def compressPdf():
    if exists('ODSOutput') == False:
        print ('PDF archival directory ODSOutput not found to compress');    
    if exists('ODSOutput') == True:
        print ('compressing pdf :' + str(datetime.now()));
        zipf = zipfile.ZipFile('policy_pdf.zip', 'w', zipfile.ZIP_DEFLATED)        
        zipdir('ODSOutput/', zipf)
        zipf.close()
        print ('completed compressing pdf :' + str(datetime.now()));
        shutil.rmtree('ODSOutput')

def archivePdf(args, previewMode):
    ## show values ##
    print ("Input file: %s" % args.input )
    print ("Output file: %s" % args.output )
    
    #ifile  = open('test.csv', "rb")
    ifile  = open(args.input, "rt")
    reader = csv.reader(ifile)
    #ofile  = open('test.csv', "wb")
    ofile  = open(args.output, "wt")
    '''writer = csv.writer(ofile, delimiter=',', quotechar='"', quoting=csv.QUOTE_ALL)'''

    print ('reading input file :' + str(datetime.now()));
    rows = defaultdict(list) # each value in each row is appended to a list
    columns = defaultdict(list) # each value in each column is appended to a list
    tuplesList = []#defaultdict(list)
    for row in reader:
        #print (row)
        #print (row[2], row[12])
        #tuplesList = [list(zip(row[2], row[12]))]
        tuple = (row[2], row[12])
        tuplesList.append(tuple)
        #writer.writerow(row)
        for (i,v) in enumerate(row):
            columns[i].append(v)

    #print(columns[0])
    #print(columns[2])
    print ('completed reading input file :' + str(datetime.now()));

    filesList = columns[2]
    filesList.pop(0)
    #print(filesList)
    #print (rows[0])
    #print (keys)

    #del tuplesList[0]
    tuplesList.pop(0)
    #print (tuplesList)
    #print ('========================')
                    
    #dictList = dict(zip(columns[2], columns[13], columns[14]))
    #print (dictList)

    #tfile  = open("testout.txt", "wt")

    #ofile = open(args.output, 'w')
    print ('reading file list :' + str(datetime.now()))
    cwd = os.getcwd()
    #prefix = "c:\dev\shell_scripts\ODSOutput"
    prefix = cwd + '\ODSOutput'
    if os.path.exists(prefix):
        shutil.rmtree(prefix)
    if os.path.exists('policy_pdf.zip'):        
        os.remove('policy_pdf.zip')
    print ('Reading from list ...')
    tokenList = ['ODSOutput', 'ODS', 'odsoutput']
    #print ('TESTING----' + "[0]" + str(tuplesList[0]) + ", [1]=" + str(tuplesList[1]))
    #print ('----------------')
    '''for filepath,formstatus in tuplesList:
        print ('xx filepath = ' + filepath + ", formstatus = " + formstatus)'''
    #for src in str(tuplesList[0]):
    cnt=1
    for src,formstatus in tuplesList:
        #print ('yy filepath = ' + src + ", formstatus = " + formstatus)
        #if cnt % 1000 == 0:
        #    print(cnt,',',end='', flush=True)
        cnt=cnt+1
        if len(src) != 0:
            '''searchStr='ODSOutput'
            odsindex = src.find("ODSOutput")
            if odsindex == -1:
                odsindex = src.find("ODS")
                searchStr='ODS'
            if odsindex == -1:
                print ('ODSOutput or ODS not found in the file path')
                break;
            '''
            searchStr=''
            if searchStr == '':
                for token in tokenList:
                    tokenIndex = src.find(token)
                    #print ('%s - token Index = %s ' % (src, tokenIndex))
                    if tokenIndex == -1:
                        #print ('%s token not found in file path' % token)
                        continue
                    else:
                        searchStr = token;
                        #print ('%s token found in file path' % token)
                        break
            newlist = src.split(searchStr)
            #print(newlist)
            dstfilename = prefix + newlist[-1]
            '''print ('source' + src)
            print ('dest : ' + dst)
            print ('...... ')'''
            #newStr = src.replace('\\', '\\\\')
            #tfile.write ('new Str : %s\n' %newStr)
            #srcfile = Path(newStr.strip())
            #tfile.write ('%s - %s - %s - %s\n' % (src, srcfile.exists(), os.path.exists(src.strip()), os.path.exists(newStr.strip())))
            srcfilename = src.strip()
            '''print ('source : ' + srcfilename)
            print ('dest : ' + dstfilename)'''
            if os.path.exists(srcfilename):
                if previewMode == True:
                    try:
                        shutil.copy(srcfilename, dstfilename)
                    except IOError as e:
                        os.makedirs(os.path.dirname(dstfilename))
                        shutil.copy(srcfilename, dstfilename)
                    ofile.write("%s - Copied file to %s.\n" % (srcfilename, dstfilename))
                else:
                    try:
                        shutil.move(srcfilename, dstfilename)
                    except IOError as e:
                        os.makedirs(os.path.dirname(dstfilename))
                        shutil.move(srcfilename, dstfilename)                     
                    ofile.write("%s - Moved file to %s.\n" % (srcfilename, dstfilename))
                    #copyfile(srcfilename, dstfilename)
                    #shutil.move(srcfilename, dstfilename)
                #ofile.write("%s - Moved/Copied file to %s.\n" % (srcfilename, dstfilename))
            else:
                #print(src + ' file does not exist');
                #writer.writerow(src + '- file not found')
                #print ('status = ' + formstatus + ', condition = ' + str(formstatus == "ERROR"))
                if formstatus.find("ERROR")>0:
                    #print ('error')
                    ofile.write("%s - file not created, error in transaction.\n" % srcfilename)
                else:
                    ofile.write("%s - file not found.\n" % srcfilename)
    #print ('', end='\n')
    print ('completed reading file list :' + str(datetime.now()));

    #tfile.close()
    ifile.close()
    ofile.close()

def get_meta_data(args):
    print("Getting metadata from database")
    if not run_get_metadata_sql_script(args.conn, 'runGetMetaData.sql', args.monthsToGoBack, args.input):
        logger.error(msg)
        print(msg)
    else:
        print ('runGetMetaData.sql' + ' - execution complete.')   

def run_get_metadata_sql_script(conn, script, arg1, arg2):
    # add @ at the beginning of the script name to run it in sqlplus.
    print ('args : conn=' + conn + ', script=' + script + ', months2GoBack=' + str(arg1) + ', metadatafile=' + arg2)
    script = '@' + script
    ret_status = False

    logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    print('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    try:
        # proc_exit = subprocess.run(['echo', 'exit'], shell=True, stdout=subprocess.PIPE)

        proc = subprocess.run(['sqlplus', '-l', '-s', conn, script, '%s' % str(arg1), arg2],
                              shell=True,
                              # stdin=proc_exit.stdout,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE
                              )
    except subprocess.CalledProcessError as err:
        logger.exception('{}'.format(err))
        ret_status = False
        print('Error in sub process')
    else:
        print('Else args=' + ' '.join(proc.args) + ', stdout=' + proc.stdout.decode('utf-8'))
        ret_status = True
        logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
        logger.info('Return Code: {}\n\n'.format(proc.returncode))
        if len(proc.stdout) > 0:
            logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
        if len(proc.stderr) > 0:
            logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

    logger.info('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

    return ret_status

'''def run_sql_script(conn, script, paramList):
    # add @ at the beginning of the script name to run it in sqlplus.
    print (paramList)
    print ('args : conn=' + conn + ', script=' + script + ', parameters =' + ','.join(repr(str(param)) for param in paramList))
    script = '@' + script
    ret_status = False

    logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    print('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    try:
        # proc_exit = subprocess.run(['echo', 'exit'], shell=True, stdout=subprocess.PIPE)

        proc = subprocess.run(['sqlplus', '-l', '-s', conn, script, ','.join(repr(str(param)) for param in paramList)],
                              shell=True,
                              # stdin=proc_exit.stdout,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE
                              )
        #subprocess.Popen(args=[script, arg1, '%s' % arg2, '%s' % arg3], shell=True)
    except subprocess.CalledProcessError as err:
        logger.exception('{}'.format(err))
        ret_status = False
        print('Error in sub process')
    else:
        print('Else args=' + ' '.join(proc.args) + ', stdout=' + proc.stdout.decode('utf-8'))
        ret_status = True
        logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
        logger.info('Return Code: {}\n\n'.format(proc.returncode))
        if len(proc.stdout) > 0:
            logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
        if len(proc.stderr) > 0:
            logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

    logger.info('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

    return ret_status
'''
def run_db_purge_sql_script(conn, script, arg1, arg2, arg3):
    # add @ at the beginning of the script name to run it in sqlplus.
    print ('args : conn=' + conn + ', script=' + script + ', email=' + arg1 + ', months2GoBack=' + str(arg2) + ', metadatafile=' + arg3)
    script = '@' + script
    ret_status = False

    logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    print('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    try:
        # proc_exit = subprocess.run(['echo', 'exit'], shell=True, stdout=subprocess.PIPE)

        #arg1='athi.muthukumarasamy@delphi-tech.com'
        #arg1=''
        #arg2=52
        #arg3='Y'
        proc = subprocess.run(['sqlplus', '-l', '-s', conn, script, arg1, '%s' % str(arg2), '%s' % arg3],
                              shell=True,
                              # stdin=proc_exit.stdout,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE
                              )
    except subprocess.CalledProcessError as err:
        logger.exception('{}'.format(err))
        ret_status = False
        print('Error in sub process')
    else:
        print('Else args=' + ' '.join(proc.args) + ', stdout=' + proc.stdout.decode('utf-8'))
        ret_status = True
        logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
        logger.info('Return Code: {}\n\n'.format(proc.returncode))
        if len(proc.stdout) > 0:
            logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
        if len(proc.stderr) > 0:
            logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

    logger.info('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

    return ret_status

def purge_db_data(args):
    print("Purging data from database")
    #if not run_db_purge_sql_script(args.conn, 'RunDataPurge.sql', args.emailAddr, args.monthsToGoBack, args.previewMode):
    if not run_db_purge_sql_script(args.conn, 'RunDataPurge.sql', '', args.monthsToGoBack, args.previewMode):
        logger.error(msg)
        print(msg)
    else:
        print ('RunDataPurge.sql' + ' - execution complete.')        
    
def pdf_archive(args, previewMode):
    print("Processing Pdf Archive")
    archivePdf(args, previewMode)
    compressPdf()

def send_reports(args):
    print ('Getting Reports')
    con = cx_Oracle.connect(args.conn)
    # Query all rows
    cur = con.cursor()
    statement = "select cal.arch_date, cal.arch_report \
                    from cs_arch_log cal \
                    where cal.cs_arch_log_pk in ( \
                          select max(cal1.cs_arch_log_pk) \
                          from cs_arch_log cal1 \
                          where cs_parse_parm('p_arch_id',arch_params) in ('OSARCH', 'NONPMOSARCH', 'XTARCH', 'XTFARCH', 'FMXTARCH') \
                          group by cs_parse_parm('p_arch_id',arch_params) \
                    )\
                    order by 1 desc"
    cur.execute(statement)
    res = cur.fetchall()
    #print (res)

    text_file = open("Db_Purge_Report.txt", "w")
    newline=''
    
    for report in res:
        lobContents = report[1].read()
        #print (lobContents)
        text_file.write ('\n===================================REPORT START=================================================\n')
        text_file.write(lobContents)
        text_file.write ('\n===================================REPORT END===================================================\n')
    cur.close()
    text_file.close()

    if args.emailAddr!=None:
        send_email(args)

def send_email(args):
    print ('Sending email to %s' % args.emailAddr)
    fromaddr = "noreplyoasis@delphi-tech.com"
    toaddr = args.emailAddr
     
    msg = MIMEMultipart()
     
    msg['From'] = fromaddr
    msg['To'] = toaddr
    msg['Subject'] = "OASIS Archival / Purge - COMPLETED - " + str(datetime.now())
     
    body = "OASIS Output Data Purge has been completed successfully.\n\n" + \
           "The Metadata file, DB Purge Report and the pdf archival output report are all attached herewith.\n" + \
           "Please contact the customer support team for any further questions.\n\n" + \
           "Regards\n\n" + \
           "OasisAdminOutputDataTool"
     
    msg.attach(MIMEText(body, 'plain'))
     
    filename = "Db_Purge_Report.txt"
    attachment = open(filename, "rb")
     
    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % filename)
     
    msg.attach(part)

    filename = args.input
    attachment = open(filename, "rb")
     
    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % filename)
     
    msg.attach(part)
    
    filename = args.output
    attachment = open(filename, "rb")
     
    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % filename)
     
    msg.attach(part)

    mailserver = smtplib.SMTP('relay01.dti-hosting.net', 25)
    #mailserver = smtplib.SMTP('smtp.gmail.com', 587)
    mailserver.ehlo()
    mailserver.starttls()
    #mailserver.login("athiyamanmms@gmail.com", "Dheek12320161")
    text = msg.as_string()
    mailserver.sendmail(fromaddr, toaddr, text)
    #mailserver.sendmail("athiyamanmms@gmail.com", toaddr, text)
    mailserver.close()
    #mailserver.quit()

def upload_reports(args):
    shutil.copy('policy_pdf.zip', args.upload_ftp_dir)
    
def main():
    #common parameters for all 3 actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    parent_parser.add_argument('monthsToGoBack',help='Get MetaData/Purge DB parameter - months to go back', type=int, nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser], description='DB/PDF Archival Script.')
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid',required=True)
    parser.add_argument('-i','--input', help='Name of comma separated metadata file.',required=True)
    parser.add_argument('-o','--output',help='Output file name to get all errors listed, if any', required=True)
    parser.add_argument('-m','--emailAddr',help='Email Address to send reports')
    parser.add_argument('-u','--upload_ftp_dir',help='Ftp directory for reports to be uploaded.')      
    parser.add_argument('--pdf_archive',help='Archive pdf', action='store_true')
    parser.add_argument('--preview_pdf_archive',help='Preview Archive pdf', action='store_true')
    parser.add_argument('--get_metadata',help='Get Metadata from database to a comma separated file', action='store_true')
    parser.add_argument('--purge_db_data',help='Purge DB data from database', action='store_true')    
    parser.add_argument('--send_reports',help='Send Reports by Email', action='store_true')
    parser.add_argument('--upload_reports',help='Upload Reports to ftp, given the ftp path to the client')  
    #parser.add_argument('emailAddr',help='Purge data parameter - email', nargs='?')
    #parser.add_argument('monthsToGoBack',help='Purge data parameter - months to go back for purge', nargs='?')
    parser.add_argument('previewMode',help='Purge data parameter - preview mode', nargs='?', default='Y')    
    args = parser.parse_args()

    if args.upload_reports:
        upload_reports(args) 
    elif args.send_reports:
        send_reports(args)    
    elif args.get_metadata:
        get_meta_data(args)
    elif args.purge_db_data:
        print('in purge data with parameters : %s, %s, %s' % (args.emailAddr, args.monthsToGoBack, args.previewMode))
        sys.exit
        purge_db_data(args)    
    elif args.pdf_archive:
        pdf_archive(args, False)
    elif args.preview_pdf_archive:
        #get_meta_data(args)
        pdf_archive(args, True)        
    else:
        print("Processing all - getting metadata, purge data from db and archive pdf files")
        get_meta_data(args)
        purge_db_data(args) 
        pdf_archive(args, False)
        send_reports(args)

logger = logging.getLogger('archiveAdmin')

#python archiveAdmin.py -c LIFESP20141SE/LIFESP20141SE@NJVMORA11GR2C_STSE11G6 -i lifespanProd12132013pdfMetadata.csv -o test.txt 12 athi.muthukumarasamy@delphi-tech.com Y
if __name__ == '__main__':
    main()

