#!/usr/bin/python

import argparse
import logging
import subprocess
import os.path

__author__ = 'athi'
__version__ = '1.0.0'

def run_option(args, cmd):
    print("Running install oasis health check - %s" % args.conn)
    arg1=''
    arg2=''
    if cmd == 'run':
        script = 'runHealthCheck.sql'
        arg1=''
        arg2='FALSE'
    elif cmd == 'list':
        script = 'runListHealthCheck.sql'
        arg1='ALL'        
    elif cmd == 'lastreport':
        script = 'getLastOasisHealthCheckReport.sql'
        if args.report is None:
            arg1='lastreport.csv'
        else:
            arg1=args.report
    elif cmd == 'datereport':
        script = 'getReportByDateOasisHealthCheckReport.sql'
        arg1=args.report
        arg2=args.runDate        
    elif cmd == 'allreport':
        script = 'getAllOasisHealthCheckReport.sql'
        arg1=args.report
    elif cmd == 'install':
        script = 'runInstallHealthCheck.sql'
    elif cmd == 'install_packages':
        script = 'runPackageInstallHealthCheck.sql'        
    elif cmd == 'uninstall':
        script = 'runUninstallHealthCheck.sql'
    else:
        script = 'runHealthCheck.sql'
        arg1=''
        arg2='FALSE'
        
    if not run_sql_script(args.conn, script, arg1, arg2):
        logger.error(msg)
        print(msg)
    else:
        print (script + ' - execution complete.')   

def run_sql_script(conn, script, arg1, arg2):
    # add @ at the beginning of the script name to run it in sqlplus.
    script = '@' + os.path.join('scripts', script)
    ret_status = False

    print ('args : conn=' + conn + ', script=' + script + ', arg1=' + str(arg1) + ', arg2=' + arg2)

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
    
def main():
    #common parameters for all 3 actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    parent_parser.add_argument('runDate',help='Health Check run date', nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser], description='Oasis Health Check Script.')
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid',required=True)
    parser.add_argument('-r','--report', help='report file.')

    parser.add_argument('--install',help='Install OasisHealthCheck', action='store_true')
    parser.add_argument('--uninstall',help='Uninstall OasisHealthCheck', action='store_true')
    parser.add_argument('--run',help='Run OasisHealthCheck', action='store_true')
    parser.add_argument('--list',help='List OasisHealthCheck', action='store_true')  
    parser.add_argument('--lastreport',help='Last Run report.', action='store_true')
    parser.add_argument('--datereport',help='Run report for a particular date run', action='store_true')    
    parser.add_argument('--allreport',help='Run report of all runs to comma separated file', action='store_true')
    #parser.add_argument('--upload_reports',help='Upload Reports to ftp, given the ftp path to the client')  
    #parser.add_argument('previewMode',help='Purge data parameter - preview mode', nargs='?', default='Y')    
    args = parser.parse_args()

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
