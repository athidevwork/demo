#!/usr/bin/python

import argparse
import logging
import subprocess
import os.path
import cx_Oracle
import pprint
import sys
import textwrap
import time

from pathlib import Path

__author__ = 'athi'
__version__ = 'Docupro Forms Test Tool 2018.1.0.1'

def run_option(args, cmd):
    print("Running %s for document forms test for connection %s" % (cmd, args.conn))
    arg1=''
    arg2=''
    arg3=''
    username=args.conn.split("/",1)[0]
    host_sid=args.conn.split("@",1)[1]
    if cmd == 'run':
        script = 'runHealthCheck.sql'
        if args.subsystem is None:
            arg1=''
        else:
            arg1=args.subsystem
        if args.email is None:
            arg2=''
        else:
            arg2=args.email
        arg3='FALSE'
    elif cmd == 'install':
        script = 'install.sql'
    elif cmd == 'select_policies':
        script = 'select_policies.sql'
        arg1=args.formskey
        arg2=args.numberpolicies
    elif cmd == 'config_gd_form':
        script = 'configGDform.sql'
        arg1=args.expdate
        arg2=args.formskey
    elif cmd == 'reextract_forms':
        script = 'reextract_forms.sql'
    elif cmd == 'submit_eod_forms':
        script = 'submit_EOD_Forms.sql'
    elif cmd == 'audit_time':
        script = 'audit_time.sql'
        arg1=args.dbkey
    elif cmd == 'uninstall':
        script = 'uninstall.sql'
    else:
        script = 'runHealthCheck.sql'
        if args.subsystem is None:
            arg1=''
        else:
            arg1=args.subsystem
        if args.email is None:
            arg2=''
        else:
            arg2=args.email
        arg3='FALSE'

    msg = run_sql_script(args.conn, script, arg1, arg2, arg3) 
    if not msg:
        logger.error('SQL script execution status : ' + str(msg))
        #print(msg)
    else:
        print (script + ' - execution complete.')   

def run_sql_script(conn, script, arg1, arg2, arg3):
    tscript = os.path.join('scripts', script)
    config = Path(tscript)
    if not config.is_file():
        print (tscript + ' script not found to run.')
        return False
    else:
        #add @ at the beginning of the script name to run it in sqlplus.
        script = '@' + os.path.join('scripts', script)
        ret_status = False

        #print ('run_sql_script args : conn=' + conn + ', script=' + script + ', arg1=' + str(arg1) + ', arg2=' + arg2 + ', arg3=' + arg3)

        logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
        print('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
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
            print(' '.join(proc.args) + ', stdout=' + proc.stdout.decode('utf-8'))
            ret_status = True
            logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
            logger.info('Return Code: {}\n\n'.format(proc.returncode))
            if len(proc.stdout) > 0:
                logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
            if len(proc.stderr) > 0:
                logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

        print('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

        return ret_status

def install_dft(args):
    print("Installing Document Forms Test")
    run_option(args, 'install')

def uninstall_dft(args):
    print("Uninstalling Oasis Health Check")
    run_option(args, 'uninstall')

def select_policies(args):
    print("Selecting Policies")
    run_option(args, 'select_policies')

def config_gd_form(args):
    print("Configuring Ghost draft form")
    run_option(args, 'config_gd_form')

def reextract_forms(args):
    print("Re-extracting Forms")
    run_option(args, 'reextract_forms')

def submit_eod_forms(args):
    print("Submitting EOD forms")
    run_option(args, 'submit_eod_forms')

def audit_time(args):
    print("Getting time for forms generation")
    run_option(args, 'audit_time')

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

def get_stats_4_requestid(args):
    #print("Running Get Env from %s with db name as %s ... " % (args.envconn, args.key))
    env = []
    sql_stmt = 'SELECT oct.os_crystal_trigger_pk, oct.request_id, oct.doc_gen_prd_name, oct.form_id,  oct.crystal_status, oct.status_msg, oct.drive_destination, oct.filename, \
                       oct.sys_create_time, oct.sys_update_time, oct.ce_processing_time \
                FROM os_crystal_trigger oct \
                WHERE oct.request_id = %s \
                ORDER BY 1 DESC' % repr(str(args.dbkey));

    #print ('sql=%s' % sql_stmt)
    
    # 1- Create a Connection Object
    client_db_conn = cx_Oracle.connect(args.conn)

    # 2- Create a Cursor Object
    cur = client_db_conn.cursor()
    # 3- Execute the SQL statement
    cur.execute(sql_stmt)

    # 4- Fetch records
    stats = cur.fetchall()

    # 5- Clean up - Close Cursor and connection.
    cur.close()
    client_db_conn.close()
    #print(len(env))
    #pprint.pprint(env)
    #print(env)

    timestr = time.strftime("%Y%m%d_%H%M%S")
    text_file = open("stats_report_"+args.dbkey+"_run"+timestr+".txt", "w")
    text_file.write ('\n===================================REPORT START=================================================\n')
    text_file.write ('\nOCT_PK, REQUEST_ID, FORM_ENGINE, FORM_ID, FORM_STATUS, SYS_START_TIME, SYS_UPDATE_TIME, CE_PROCESSING_TIME\n')
    for stat in stats:
        text_file.write ('%s, %s, %s, %s, %s, %s, %s, %s\n' % (stat[0], stat[1], stat[2], stat[3], stat[4], stat[8], stat[9], stat[10]))
    text_file.write ('\n===================================REPORT END===================================================\n')
    
    # convert the list of tuples into a list of values.
    return [i[0] for i in stats]

def is_install_valid(args):
    sql_stmt = '';
    client_db_conn = cx_Oracle.connect(args.conn)
    cur = client_db_conn.cursor()
    res = cur.callfunc('oasis_health_check_main.isoasishealthcheckinstallvalid', cx_Oracle.STRING, [])
    print('Oasis Health Check Objects Install status : ' + res)
    return res

def main():
    #common parameters for all 3 actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    #parent_parser.add_argument('runDate',help='Health Check run date', nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser]
                                     , formatter_class=argparse.RawDescriptionHelpFormatter
                                     , description='''Docupro Forms Test Script.
    All actions to be specified with --<option>.
    All parameters to be specified with -<char> or --<option>.''',
                                     epilog=textwrap.dedent('''
Usage examples:

Display Oasis health check version : DocuproFormsTest.exe -v

Run install docupro forms test from file for all env : DocuproFormsTest.exe -f docuproformstest.txt --install
Run install docupro forms test from file for one env : DocuproFormsTest.exe -f docuproformstest.txt -k ODEV20181SE --install

Run uninstall docupro forms test from file for all env : DocuproFormsTest.exe -f docuproformstest.txt --uninstall
Run uninstall docupro forms test from file for one env : DocuproFormsTest.exe -f docuproformstest.txt -k ODEV20181SE --uninstall

Run docupro forms test from file for all env : DocuproFormsTest.exe -f oasishealthcheckenv.txt
Run docupro forms test from file for one env : DocuproFormsTest.exe -f oasishealthcheckenv.txt -k ODEV20181SE

Run docupro forms test specifying connection string for all env : DocuproFormsTest.exe -c ODEV20191/ODEV20191@NY2ORA12CBASE01_ODEV122

Display all health checks that are run : DocuproFormsTest.exe -c ODEV20191/ODEV20191@NY2ORA12CBASE01_ODEV122 --regenerate
Display health checks for a subsystem : DocuproFormsTest.exe -c ODEV20191/ODEV20191@NY2ORA12CBASE01_ODEV122 -s OUTPUT --list

Run a report for health check run on date : DocuproFormsTest.exe -c ODEV20191/ODEV20191@NY2ORA12CBASE01_ODEV122 --datereport '09/13/2018 11:48:51'

Run report for all health check run on db : DocuproFormsTest.exe -c ODEV20191/ODEV20191@NY2ORA12CBASE01_ODEV122 --allreport
'''))
    
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid')
    parser.add_argument('-f','--file', help='file with database environments to manage health check actions.')
    parser.add_argument('-k','--key', help='environment key.')
    
    parser.add_argument('-o','--config_gd_form', help='configure ghost draft form to active/inactive.', action='store_true')
    parser.add_argument('-k4','--expdate', help='expiry date as 01/01/2000 or 01/01/3000.')
    
    parser.add_argument('-p','--select_policies', help='select policies.', action='store_true')
    parser.add_argument('-k2','--formskey', help='forms primary key to select policies.')
    parser.add_argument('-k3','--numberpolicies', help='number of polcicies to select.')
    
    parser.add_argument('-r','--regenerate_forms', help='regenerate forms.', action='store_true')
    parser.add_argument('-x','--reextract_forms', help='extract forms.', action='store_true')
    
    parser.add_argument('-t','--get_forms_generation_time', help='get time to generate forms.', action='store_true')
    parser.add_argument('-k1','--dbkey', help='request id to get time for.')
    parser.add_argument('-t1','--get_forms_generation_stats', help='get stats to generate forms.', action='store_true')
    
    parser.add_argument('-s','--submit_eod_forms', help='submit end of day forms.', action='store_true')
    parser.add_argument('-v','--version', help='displays the oasis health check version', action='store_true')

    parser.add_argument('--install',help='Install OasisHealthCheck', action='store_true')
    parser.add_argument('--uninstall',help='Uninstall OasisHealthCheck', action='store_true')
    parser.add_argument('--run',help='Run OasisHealthCheck and get last run report to csv file. Defaults to this option if no other option is provided.', action='store_true')
    #parser.add_argument('previewMode',help='Purge data parameter - preview mode', nargs='?', default='Y')    
    args = parser.parse_args()

    if args.version:
        print (__version__)
        sys.exit
    else : 
        connlist = []
        if args.file:
            f=open("docuprotestenv.txt", "r")
            fl =f.readlines()
            #print ('Environments in file:')
            for x in fl:
                #print (x)
                l = x.split()
                env1=l[0]
                conn1=l[1]
                #print(l)
                #print (repr(l[0]), repr(l[1]))
                #print (env1,conn1)
                args.conn = conn1
                #print ('key : ' + args.key)
                #print ('check: ' + str(env1 == args.key))
                if args.key:
                    if env1 == args.key:
                        connlist.append(conn1)
                else:
                    connlist.append(conn1)
            
        if not (args.conn or args.file):
            #if (args.configGhostdraft and not(args.inputfile and args.instance and args.env and args.docservice and args.mapping)):
            parser.error('Either -c or -f argument is required for oasis health check to perform actions on oasis health check. Exiting ...')
            sys.exit

        if args.conn and (not args.key):
            connlist.append(args.conn)         
          
        if not (len(connlist) == 0): 
            print ('------------------------')
            print ('Environments docupro forms test would run on :')
            print (connlist)
            print ('------------------------')
        
        if len(connlist) == 0:
            print ('No environment found to perform an action.')
            sys.exit

        #print(len(connlist))
        for y in connlist:
            j = y.split()
            print ('*************current env = %s*************' % (j[0]))
            args.conn = j[0]
                    
            if not args.conn:
                parser.error('connection parameter not found. Exiting ...')
                sys.exit
                
            if args.install:
                install_dft(args)
            elif args.uninstall:
                uninstall_dft(args)
            elif args.select_policies:
                select_policies(args)
            elif args.config_gd_form:
                config_gd_form(args)
            elif args.regenerate_forms:
                reextract_forms(args)
                submit_eod_forms(args)
            elif args.submit_eod_forms:
                submit_eod_forms(args)
            elif args.get_forms_generation_time:
                audit_time(args)
            elif args.get_forms_generation_stats:
                get_stats_4_requestid(args)
            elif args.run:
                run_ohc(args)        
            else:
                print("Processing run and report")
                run_ohc(args)
                lastreport(args)

logger = logging.getLogger('DocuproFormsTest')

if __name__ == '__main__':
    main()
