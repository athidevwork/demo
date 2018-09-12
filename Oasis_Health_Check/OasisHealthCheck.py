#!/usr/bin/python

import argparse
import logging
import subprocess
import os.path
import cx_Oracle
import pprint
import sys

__author__ = 'athi'
__version__ = '1.0.0'

def run_option(args, cmd):
    print("Running %s for oasis health check for connection %s" % (cmd, args.conn))
    arg1=''
    arg2=''
    arg3=''
    if cmd == 'run':
        script = 'runHealthCheck.sql'
        arg1=args.subsystem
        arg2=args.email
        arg3='FALSE'
    elif cmd == 'list':
        script = 'runListHealthCheck.sql'
        arg1='ALL'        
    elif cmd == 'lastreport':
        script = 'getLastOasisHealthCheckReport.sql'
        username=args.conn.split("/",1)[0]
        host_sid=args.conn.split("@",1)[1]
        if args.report is None:
            arg1='lastreport_' + username + '_' + host_sid + '.csv'
        else:
            arg1=args.report
    elif cmd == 'datereport':
        script = 'getReportByDateOasisHealthCheckReport.sql' 
        if args.report is None:
            arg1='datereport_' + username + '_' + host_sid + '.csv'
        else:
            arg1=args.report
        arg2=args.runDate
    elif cmd == 'allreport':
        script = 'getAllOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='allreport_' + username + '_' + host_sid + '.csv'
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
        arg1=args.subsystem
        arg1=args.email
        arg3='FALSE'
        
    if not run_sql_script(args.conn, script, arg1, arg2, arg3):
        logger.error(msg)
        print(msg)
    else:
        print (script + ' - execution complete.')   

def run_sql_script(conn, script, arg1, arg2, arg3):
    # add @ at the beginning of the script name to run it in sqlplus.
    script = '@' + os.path.join('scripts', script)
    ret_status = False

    print ('args : conn=' + conn + ', script=' + script + ', arg1=' + str(arg1) + ', arg2=' + arg2 + ', arg3=' + arg3)

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

def install_ohc(args):
    print("Installing Oasis Health Check")
    run_option(args, 'install')

def install_packages_ohc(args):
    print("Installing Oasis Health Check Packages")
    run_option(args, 'install_packages')
    
def uninstall_ohc(args):
    print("Uninstalling Oasis Health Check")
    run_option(args, 'uninstall')

def lastreport(args):
    print("Running Last Oasis Health Check Report")
    run_option(args, 'lastreport')

def datereport(args):
    print("Running Report for Oasis Health Check as of Date")
    run_option(args, 'datereport')

def allreport(args):
    print("Running All Oasis Health Check Reports")
    run_option(args, 'allreport')

def list_ohc(args):
    print("Listing Oasis Health Check ...")
    run_option(args, 'list')
    
def run_ohc(args):
    print("Running Oasis Health Check ...")
    run_option(args, 'run')

def get_env(args):
    print("Running Get Env from %s with db name as %s ... " % (args.envconn, args.key))
    #run_option(args, 'getenv')
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

    # convert the list of tuples into a list of values.
    return [i[1] for i in env]

def main():
    #common parameters for all 3 actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    parent_parser.add_argument('runDate',help='Health Check run date', nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser], description='Oasis Health Check Script.')
    #parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid',required=True)
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid')
    parser.add_argument('-e','--email', help='email to send report of health check run. defaults to none.')
    parser.add_argument('-f','--file', help='file with database environments to manage health check actions.')
    parser.add_argument('-k','--key', help='database environment key to manage health check actions.')
    parser.add_argument('-l','--envconn', help='database connection string where health check environments are managed.')
    parser.add_argument('-r','--report', help='report file. defaults to lastreport.csv.')
    parser.add_argument('-s','--subsystem', help='sub system to run health check on. defaults to all subsystems.')

    parser.add_argument('--install',help='Install OasisHealthCheck', action='store_true')
    parser.add_argument('--uninstall',help='Uninstall OasisHealthCheck', action='store_true')
    parser.add_argument('--run',help='Run OasisHealthCheck and get last run report to csv file.', action='store_true')
    parser.add_argument('--list',help='List OasisHealthCheck', action='store_true')  
    parser.add_argument('--lastreport',help='Run Report on last performed health check.', action='store_true')
    parser.add_argument('--datereport',help='Run report for a particular dated run', action='store_true')    
    parser.add_argument('--allreport',help='Run report of all the runs in database to comma separated file', action='store_true')
    #parser.add_argument('--upload_reports',help='Upload Reports to ftp, given the ftp path to the client')  
    #parser.add_argument('previewMode',help='Purge data parameter - preview mode', nargs='?', default='Y')    
    args = parser.parse_args()

    connlist = []
    if args.file:
        f=open("oasishealthcheckenv.txt", "r")
        fl =f.readlines()
        print ('Environments in file:')
        for x in fl:
            #print (x)
            l = x.split()
            env1=l[0]
            conn1=l[1]
            #print(l)
            #print (repr(l[0]), repr(l[1]))
            print (env1,conn1)
            args.conn = conn1
            if args.key:
                if env1 == args.key:
                    connlist.append(conn1)
            else:
                connlist.append(conn1)
    else:
        connlist.append(args.conn)
        
    print ('------------------------')
    print ('Environments health check would run on :')
    print (connlist)
    print ('------------------------')
        
    if not (args.key or args.conn):
        #if (args.configGhostdraft and not(args.inputfile and args.instance and args.env and args.docservice and args.mapping)):
        parser.error('Either -c or -e argument is required for oasis health check to run. Exiting ...')
        sys.exit

    if len(connlist) == 0:
        print ('No environment found to perform an action.')
        sys.exit
               
    for y in connlist:
        j = y.split()
        print ('*************current env = %s*************' % (j[0]))
        args.conn = j[0]

        if args.key:
            if not args.conn:
                if not args.envconn:
                    parser.error('connection sring parameter for env not found. Exiting ...')
                    sys.exit
                else:
                    args.conn = get_env(args)[0]
                    print ('connection retrieved from db : %s' % args.conn)
                
        if not args.conn:
            parser.error('connection parameter not found. Exiting ...')
            sys.exit
            
        if args.install:
            install_ohc(args)
            install_packages_ohc(args)
            install_packages_ohc(args)
        elif args.uninstall:
            uninstall_ohc(args)    
        elif args.lastreport:
            lastreport(args)
        elif args.datereport:
            print('in date report with parameters : %s, %s' % (args.conn, args.report))
            #sys.exit
            datereport(args)    
        elif args.allreport:
            allreport(args)
        elif args.list:
            list_ohc(args)  
        elif args.run:
            run_ohc(args)        
        else:
            print("Processing run and report")
            run_ohc(args)
            lastreport(args)

logger = logging.getLogger('OasisHealthCheck')

if __name__ == '__main__':
    main()
