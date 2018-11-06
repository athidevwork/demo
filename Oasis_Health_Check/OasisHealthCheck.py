#!/usr/bin/python

import argparse
import glob
import logging
import subprocess
import os.path
import cx_Oracle
import pprint
import sys
import textwrap
import time
import smtplib
import Util

from pathlib import Path

from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders

__author__ = 'athi'
__version__ = 'Oasis Health Check 2018.1.0.1'

def run_option(args, cmd):
    #print("Running %s for oasis health check for connection %s" % (cmd, args.conn))
    arg1=''
    arg2=''
    arg3=''
    username=args.conn.split("/",1)[0]
    host_sid=args.conn.split("@",1)[1]
    timestr = time.strftime("%Y%m%d_%H%M%S")
    if cmd == 'run':
        script = 'runHealthCheck.sql'
        if args.subsystem is None:
            arg1=''
        else:
            arg1=args.subsystem
        '''if args.email is None:
            arg2=''
        else:
            arg2=args.email'''
        arg2=''
        arg3='FALSE'
    elif cmd == 'insert_env':
        if args.script is None:
            script = 'insertHealthCheckEnv.sql'
        else:
            script = args.script
    elif cmd == 'list':
        script = 'runListHealthCheck.sql'
        if args.subsystem is None:
            arg1='ALL'
        else:
            arg1=args.subsystem
    elif cmd == 'lastreport':
        script = 'getLastOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='lastreport_' + username + '_' + host_sid + '_' + timestr + '.csv'
        else:
            arg1=args.report
    elif cmd == 'datereport':
        script = 'getReportByDateOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='datereport_' + username + '_' + host_sid + '_' + timestr + '.csv'
        else:
            arg1=args.report
        arg2=args.runDate
    elif cmd == 'daterunreport':
        script = 'getReportByDateRunOasisHealthCheckReport.sql' 
        if args.report is None:
            arg1='date_run_report_' + username + '_' + host_sid + '_' + timestr + '.csv'
        else:
            arg1=args.report
        arg2=args.runDate
        arg3=args.runNo
    elif cmd == 'listreport':
        script = 'getAllRunListOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='ohclistreport_' + username + '_' + host_sid + '_' + timestr + '.csv'
        else:
            arg1=args.report
    elif cmd == 'allreport':
        script = 'getAllOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='allreport_' + username + '_' + host_sid + '_' + timestr + '.csv'
        else:
            arg1=args.report
    elif cmd == 'install':
        script = 'runInstallHealthCheck.sql'
    elif cmd == 'install_packages':
        script = 'runPackageInstallHealthCheck.sql'        
    elif cmd == 'uninstall':
        script = 'runUninstallHealthCheck.sql'
    else:
        script = 'runHealthCheck.sql'
        if args.subsystem is None:
            arg1=''
        else:
            arg1=args.subsystem
        '''if args.email is None:
            arg2=''
        else:
            arg2=args.email'''
        arg2=''
        arg3='FALSE'

    msg = run_sql_script(args.conn, script, arg1, arg2, arg3, args.debug) 
    if not msg:
        logger.error('SQL script execution status : ' + str(msg))
        #print(msg)
    else:
        print (script + ' - execution complete.')   

def run_sql_script(conn, script, arg1, arg2, arg3, arg4):
    tscript = os.path.join('scripts', script)
    config = Path(tscript)
    if not config.is_file():
        print (tscript + ' script not found to run.')
        return False
    else:
        #add @ at the beginning of the script name to run it in sqlplus.
        script = '@' + os.path.join('scripts', script)
        ret_status = False

        if arg4:
            print ('run_sql_script args : conn=' + conn + ', script=' + script + ', arg1=' + str(arg1) + ', arg2=' + arg2 + ', arg3=' + arg3)

        logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
        #print('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
        try:
            # proc_exit = subprocess.run(['echo', 'exit'], shell=True, stdout=subprocess.PIPE)

            proc = subprocess.run(['sqlplus', '-l', '-s', conn, script, '%s' % str(arg1), str(arg2), arg3],
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
            if arg4:
                print(' '.join(proc.args) + ', stdout=' + proc.stdout.decode('utf-8'))
            ret_status = True
            logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
            logger.info('Return Code: {}\n\n'.format(proc.returncode))
            if len(proc.stdout) > 0:
                logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
            if len(proc.stderr) > 0:
                logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

        #print('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

        return ret_status

def install_ohc(args):
    print("Installing Oasis Health Check tables and sequences ...")
    run_option(args, 'install')

def install_packages_ohc(args):
    print("Installing Oasis Health Check Packages ...")
    run_option(args, 'install_packages')
    
def uninstall_ohc(args):
    print("Uninstalling Oasis Health Check ...")
    run_option(args, 'uninstall')

def process_report_options(args):
    if args.email != None:
        send_email(args)
    if args.verbose == True:
        show_report(args)

def show_report(args):
    list_of_files = glob.glob('*.csv')
    latest_file = max(list_of_files, key=os.path.getmtime)
    f = open(latest_file, 'r')
    print(f.read())
    f.close()
    
def lastreport(args):
    print("Running Last Oasis Health Check Report ...")
    run_option(args, 'lastreport')
    process_report_options(args)

def listreport(args):
    print("Running Oasis Health Check Runs List Report ...")
    run_option(args, 'listreport')
    process_report_options(args)

def daterunreport(args):
    print("Running Report for Oasis Health Check as of Date and Run ...")
    run_option(args, 'daterunreport')
    process_report_options(args)
    
def datereport(args):
    print("Running Report for Oasis Health Check as of Date")
    run_option(args, 'datereport')
    process_report_options(args)

def allreport(args):
    print("Running All Oasis Health Check Reports ...")
    run_option(args, 'allreport')
    process_report_options(args)

def list_ohc(args):
    print("Listing All Oasis Health Check runs report ...")
    run_option(args, 'list')

def insert_env(args):
    print("Insert Oasis Health Check environments to database ...")
    run_option(args, 'insert_env')
    
def run_ohc(args):
    print("Running Oasis Health Check ...")
    run_option(args, 'run')

def get_env(args):
    #print("Running Get Env from %s with db name as %s ... " % (args.envconn, args.key))
    env = []
    sql_stmt = 'SELECT * FROM OASIS_HEALTH_CHECK_ENV e WHERE e.ENV_NAME = %s' % repr(str(args.key));

    #print ('sql=%s' % sql_stmt)
    
    # 1- Create a Connection Object
    client_db_conn = cx_Oracle.connect(args.envconn)

    # 2- Create a Cursor Object
    cur = client_db_conn.cursor()
    # 3- Execute the SQL statement
    cur.execute(sql_stmt)

    # 4- Fetch records
    env = cur.fetchall()

    # 5- Clean up - Close Cursor and connection.
    cur.close()
    client_db_conn.close()
    #print(len(env))
    #pprint.pprint(env)

    #print ([i[1] for i in env])
    # convert the list of tuples into a list of values.
    return [i[1] for i in env]

def get_all_env(args):
    #print("Running Get Env from %s with db name as %s ... " % (args.envconn, args.key))
    env = []
    sql_stmt = 'SELECT * FROM OASIS_HEALTH_CHECK_ENV';

    #print ('sql=%s' % sql_stmt)
    
    # 1- Create a Connection Object
    client_db_conn = cx_Oracle.connect(args.envconn)

    # 2- Create a Cursor Object
    cur = client_db_conn.cursor()
    # 3- Execute the SQL statement
    cur.execute(sql_stmt)

    # 4- Fetch records
    env = cur.fetchall()

    # 5- Clean up - Close Cursor and connection.
    cur.close()
    client_db_conn.close()
    #print(len(env))
    #pprint.pprint(env)

    #print ([i[1] for i in env])
    # convert the list of tuples into a list of values.
    return env

def is_install_valid(args):
    sql_stmt = '';
    client_db_conn = cx_Oracle.connect(args.conn)
    cur = client_db_conn.cursor()
    res = cur.callfunc('oasis_health_check_main.isoasishealthcheckinstallvalid', cx_Oracle.STRING, [])
    print('Oasis Health Check Objects Install status : ' + res)
    return res

def send_email(args):
    print ('Sending email to %s' % args.email)
    fromaddr = "noreplyoasis@delphi-tech.com"
    toaddr = args.email
     
    msg = MIMEMultipart()
     
    msg['From'] = fromaddr
    msg['To'] = toaddr
    msg['Subject'] = "Do Not Reply - Oasis Health Check completed @ " + str(datetime.now())
     
    body = "OASIS Health Check has been run successfully.\n\n" + \
           "The health check comma separated file report is attached herewith.\n\n" + \
           "Please forward this email with attachement to Delphi customer support team and contact them for any further clarifications.\n\n" + \
           "Regards\n\n" + \
           "OasisHealthCheckTool"
     
    msg.attach(MIMEText(body, 'plain'))

    list_of_files = glob.glob('*.csv') # * means all if need specific format then *.csv
    latest_file = max(list_of_files, key=os.path.getmtime)
    #print ('report file : ', latest_file)
    #sys.exit()

    filename = latest_file
    attachment = open(filename, "rb")
     
    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % filename)
     
    msg.attach(part)

    mailserver = smtplib.SMTP('relay01.dti-hosting.net', 25)
    mailserver.ehlo()
    mailserver.starttls()
    text = msg.as_string()
    mailserver.sendmail(fromaddr, toaddr, text)
    mailserver.close()
    #mailserver.quit()

def main():
    #common parameters for all 3 actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    parent_parser.add_argument('runDate',help='Health Check run date', nargs='?')
    parent_parser.add_argument('runNo',help='Health Check run number', nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser]
                                     , formatter_class=argparse.RawDescriptionHelpFormatter
                                     , description='''Oasis Health Check Script.
    All actions to be specified with --<option>.
    All parameters to be specified with -<char> or --<option>.''',
                                     epilog=textwrap.dedent('''
Usage examples:

Display Oasis health check version : OasisHealthCheck.exe -v

Run install health check from file for all env : OasisHealthCheck.exe -f oasishealthcheckenv.txt --install
Run install health check from file for one env : OasisHealthCheck.exe -f oasishealthcheckenv.txt -k env_key --install

Run uninstall health check from file for all env : OasisHealthCheck.exe -f oasishealthcheckenv.txt --uninstall
Run uninstall health check from file for one env : OasisHealthCheck.exe -f oasishealthcheckenv.txt -k env_key --uninstall

Run health check from file for all env : OasisHealthCheck.exe -f oasishealthcheckenv.txt
Run health check from file for one env : OasisHealthCheck.exe -f oasishealthcheckenv.txt -k env_key

Run health check from database for all env : OasisHealthCheck.exe -d username/password@host_schema
Run health check from database for one env : OasisHealthCheck.exe -d username/password@host_schema -k env_key

Run health check specifying connection string for all env : OasisHealthCheck.exe -c username/password@host_schema
Run health check specifying connection string for all env (show in console) : OasisHealthCheck.exe -c username/password@host_schema --verbose

Display all health checks that are run : OasisHealthCheck.exe -c username/password@host_schema --list
Display health checks for a subsystem : OasisHealthCheck.exe -c username/password@host_schema -s OUTPUT --list

Run a report for health check run by date/run : OasisHealthCheck.exe -c username/password@host_schema --daterunreport '10/24/2018' 28

Run report for list of all health check runs on db : OasisHealthCheck.exe -c username/password@host_schema --listreport

Run report for all health check run on db : OasisHealthCheck.exe -c username/password@host_schema --allreport
'''))

    '''Run a report for health check run on date : OasisHealthCheck.exe -c username/password@host_schema --datereport '09/13/2018 11:48:51' '''
    
    #parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid',required=True)
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid')
    parser.add_argument('-d','--envconn', help='database connection string where health check environments are managed.')
    parser.add_argument('-e','--email', help='email address to send health check report. defaults to none.')
    parser.add_argument('-f','--file', help='file with database environments to manage health check actions.')
    parser.add_argument('-k','--key', help='database environment key to manage health check actions.')
    parser.add_argument('-r','--report', help='report file. defaults to lastreport_<dbusername>_<host_sid>.csv. if not provided.')
    parser.add_argument('-s','--subsystem', help='sub system to run health check on. defaults to all subsystems. Valid values are POLICY, OUTPUT, CLAIMS, CIS, FM')
    parser.add_argument('-v','--version', help='displays the oasis health check version', action='store_true')

    parser.add_argument('--install',help='Install OasisHealthCheck', action='store_true')
    parser.add_argument('--uninstall',help='Uninstall OasisHealthCheck', action='store_true')
    parser.add_argument('--run',help='Run OasisHealthCheck and get last run report to csv file. Defaults to this option if no other option is provided.', action='store_true')
    parser.add_argument('--list',help=argparse.SUPPRESS, action='store_true')  
    parser.add_argument('--lastreport',help='Run Report on last performed health check.', action='store_true')
    #parser.add_argument('--datereport',help='Run report for a particular dated run', action='store_true')
    parser.add_argument('--datereport',help=argparse.SUPPRESS, action='store_true')
    parser.add_argument('--daterunreport',help='Run report for a particular date and run number', action='store_true')
    parser.add_argument('--listreport',help='Run a report list of all the health check runs', action='store_true') 
    parser.add_argument('--allreport',help='Run report of all the runs in database to comma separated file', action='store_true')
    parser.add_argument('--insert_env', help='insert health check environments to database.', action='store_true')
    parser.add_argument('--verbose', help='print health check report to the console.', default='False', action='store_true')
    parser.add_argument('--script',help='script file to insert env data.')
    parser.add_argument('--generate_key',help=argparse.SUPPRESS, action='store_true') #not needed since Delphi would be using the generated key
    parser.add_argument('--encrypt_pass',help='encrypt password')
    parser.add_argument('--decrypt_pass',help=argparse.SUPPRESS) # this also need not be shown since Delphi only would use this option.
    parser.add_argument('--print',help=argparse.SUPPRESS, action='store_true') # developer option
    parser.add_argument('--debug',help=argparse.SUPPRESS, action='store_true') # developer option
    #parser.add_argument('--upload_reports',help='Upload Reports to ftp, given the ftp path to the client')  
    #parser.add_argument('previewMode',help='Purge data parameter - preview mode', nargs='?', default='Y')    
    args = parser.parse_args()

    if args.version:
        print (__version__)
        sys.exit()
    else : 
        connlist = []
        if args.file:
            f=open(args.file, "r")
            fl =f.readlines()
            #print ('Environments in file:')
            for x in fl:
                #print (x)
                if not x.startswith("#"):
                    l = x.split()
                    env1=l[0]
                    conn1=l[1]
                    #print(l)
                    #print (repr(l[0]), repr(l[1]))
                    #print (env1,conn1)

                    if str(conn1).startswith("b'"):
                        #print('ecnrypted=%s' %conn1)
                        ciphered_text = Util.decrypt_pass(conn1, False)
                        if str(ciphered_text).startswith("b'"):
                            conn1 = str(ciphered_text).strip("b")
                            #print ('if b : %s' % conn1)
                            conn1 = str(conn1).strip("'")
                            #print ('if : %s' % conn1)
                        #print('arg = %s' % conn1)

                    #print ('key : ' + args.key)
                    #print ('check: ' + str(env1 == args.key))
                    if args.key:
                        if env1 == args.key:
                            connlist.append(conn1)
                    else:
                        connlist.append(conn1)
            #print (connlist);
            #sys.exit()
        else:
            if args.key:
                if not args.conn:
                    if not args.envconn:
                        parser.error('connection sring parameter for env not found. Exiting ...')
                        sys.exit()
                    else:
                        if (len(get_env(args)) == 0):
                            print ('connection cannot be retrieved from db for key : %s' % args.key)
                            sys.exit()
                        else:
                            args.conn = get_env(args)[0]
                            print ('connection retrieved from db : %s' % args.key)
                            #print ('conn str : %s' % args.conn)
                            if str(args.conn).startswith("b'"):
                                ciphered_text = Util.decrypt_pass(args.conn, False)
                                if str(ciphered_text).startswith("b'"):
                                    args.conn = str(ciphered_text).strip("b")
                                    #print ('if b : %s' % args.conn)
                                    args.conn = str(args.conn).strip("'")
                                    #print ('if quote : %s' % args.conn)
                                    connlist.append(args.conn)
                            else:
                                connlist.append(args.conn)

        if args.generate_key:
            Util.generate_key_fernet()
            sys.exit()

        if args.encrypt_pass:
            Util.encrypt_pass(args)
            sys.exit()

        if args.decrypt_pass:
            #print (args.decrypt_pass.decode("utf-8"))
            #print(str(args.decrypt_pass, encoding="ascii"))
            enc_pass = args.decrypt_pass
            if args.print:
                Util.decrypt_pass(enc_pass, True)
            else:
                Util.decrypt_pass(enc_pass, False)
            sys.exit()

        if args.envconn and not args.key:
            #print('all env %s' % len(get_all_env(args)));
            env = get_all_env(args)
            for x in env:
                #print ('env[%s] = %s' % (x[0], x[1]))
                conn = x[1]
                if str(conn).startswith("b'"):
                    ciphered_text = Util.decrypt_pass(conn, False)
                    if str(ciphered_text).startswith("b'"):
                        conn = str(ciphered_text).strip("b")
                        #print ('if b : %s' % conn)
                        conn = str(conn).strip("'")
                        #print ('if quote : %s' % conn)
                        connlist.append(conn)
                else:
                    connlist.append(conn)
                #print (connlist)
                #print('envconn--- : %s' % conn)
            #print(len(connlist))
        
        if not (args.file or args.conn or args.envconn):
            #if (args.configGhostdraft and not(args.inputfile and args.instance and args.env and args.docservice and args.mapping)):
            parser.error('Either -c or -d or -f argument is required for oasis health check to perform actions on oasis health check. Exiting ...')
            sys.exit()

        if args.conn and (not args.key or args.envconn):
            connlist.append(args.conn)

        if (args.datereport and (args.runDate is None)):
            print ('DateTime argument as parameter is required.')
            sys.exit()            

        connlist = list(set(connlist))
        #print(len(connlist))

        if args.debug:
            if not (len(connlist) == 0): 
                print ('--------------------------------------------------------------------------------------------')
                print ('Environments health check would run on :')
                print (connlist)
                print ('--------------------------------------------------------------------------------------------')

        if len(connlist) == 0:
            print ('No environment found to perform an action.')
            sys.exit()

        for y in connlist:
            j = y.split()
            #print ('j = %s' % j)
            username=j[0].split("/",1)[0]
            host_sid=j[0].split("@",1)[1]
            print ('*************current env = %s@%s*************' % (username, host_sid))
            args.conn = j[0]
                    
            if not args.conn:
                parser.error('connection parameter not found. Exiting ...')
                sys.exit()
                
            if args.install:
                install_ohc(args)
                install_packages_ohc(args)
                install_packages_ohc(args)
                is_install_valid(args)
            elif args.uninstall:
                uninstall_ohc(args)
            elif args.insert_env:
                insert_env(args)
            elif args.lastreport:
                lastreport(args)
            elif args.listreport:
                listreport(args)
            elif args.daterunreport:
                #print('in date report with parameters : %s, %s' % (args.conn, args.report))
                #sys.exit
                daterunreport(args)  
            elif args.datereport:
                #print('in date report with parameters : %s, %s' % (args.conn, args.report))
                datereport(args)    
            elif args.allreport:
                allreport(args)
            elif args.list:
                list_ohc(args)  
            elif args.run:
                run_ohc(args)
                lastreport(args)
            else:
                #print("Processing run and report")
                run_ohc(args)
                lastreport(args)

logger = logging.getLogger('OasisHealthCheck')

if __name__ == '__main__':
    main()
