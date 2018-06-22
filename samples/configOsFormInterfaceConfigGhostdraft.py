#!/usr/bin/python

import argparse
import logging
import subprocess
import os.path
import cx_Oracle
from cx_Oracle import DatabaseError

__author__ = 'athi'

def display_rows(con):
    print("Fetching rows....")
    sel_cur = con.cursor()
    sel_cur.execute('select * from os_form_interface_config ofic \
                  order by ofic.doc_gen_prd_name, ofic.category, ofic.sub_category, ofic.code')
    for res in sel_cur.fetchall():
        print(res)
    sel_cur.close()

def set_integration_mode(con, mode):
    cfg_cur = con.cursor()
    print("Setting integration mode : " + mode)
    cfg_cur.callproc("oasis_config.Set_Integration",[mode])
    cfg_cur.close()

def insert_config(con, args):
    print("Processing insert")
    seq_cur = con.cursor()
    rows = [
            (seq_cur.execute("select oasis_sequence.nextval from dual").fetchone()[0], "GHOSTDRAFT", "GENERAL", "DOCUMENT_SERVICE", "REST_URI", "A value that indicates the REST URI", "https://secure.ghostdraft.com/instances/"+args.instance+"/GhostDraftServer/RestApi/"),
            (seq_cur.execute("select oasis_sequence.nextval from dual").fetchone()[0], 'GHOSTDRAFT', 'PMS', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ghostdraft.com/instances/' + args.instance + '/' + args.env + '/OASIS Policy Model Library/1.0/')
           ]
    cur = con.cursor()
    cur.bindarraysize = 2
    cur.setinputsizes(int, 50, 50, 50, 50, 200, 1000)
    try:
        cur.executemany("insert into os_form_interface_config (OS_FORM_INTERFACE_CONFIG_PK, DOC_GEN_PRD_NAME, CATEGORY, SUB_CATEGORY, CODE, DESCRIPTION, VALUE)\
                     values (:1, :2, :3, :4, :5, :6, :7)", rows, True, True)
    except DatabaseError:
        print(cur.getbatcherrors())
        print(cur.getarraydmlrowcounts())
        #cur.statement()
        #print(cur.getbatcherrors())
        '''error, = e.args
        print("  >> Database error on insert : %s" % format(e))
        conn.rollback()'''          
        print('Continue on problem with insert')

    #con.commit()
    
    # Now query the results back
    #display_rows(con)
    cur.close()
    seq_cur.close()

def update_config(con, args):
    print("Processing update")
    rows = [
            ("GHOSTDRAFT", "GENERAL", "DOCUMENT_SERVICE", "REST_URI", "TEST A value that indicates the REST URI", "https://secure.ghostdraft.com/instances/"+args.instance+"/GhostDraftServer/RestApi/"),
            ('GHOSTDRAFT', 'PMS', 'DOCUMENT_SERVICE', 'PACKAGE_URI', 'TEST A URL value that indicates the  document service used to launch interactive form.', 'https://secure.ghostdraft.com/instances/' + args.instance + '/' + args.env + '/OASIS Policy Model Library/1.0/')
           ]
    cur = con.cursor()
    cur.bindarraysize = 2
    cur.setinputsizes(1000, 200, 50, 50, 50, 50)
    try:
        #cur.statement()
        cur.executemany("update os_form_interface_config set VALUE=:6, DESCRIPTION=:5 \
                        where DOC_GEN_PRD_NAME = :1 and CATEGORY = :2 and SUB_CATEGORY = :3 and CODE = :4", rows, True, True)
        i=1
        for res in cur.getarraydmlrowcounts():
            #print("row " + str(i) + " : " + res)
            print (res)
            i = i + 1
        con.commit();        
    except DatabaseError:
        #print(cur.statement())
        print(cur.getbatcherrors())
        '''error, = e.args
        print("  >> Database error: %s" % format(e))
        conn.rollback()'''        
        print('Continue processing on problem with update')
    
    # Now query the results back
    #display_rows(con)
    cur.close()
    
def config_eloq_os_form_interface_config(con, args):
    print("Processing Eloquence config");
    
def config_gd_os_form_interface_config(con, args):
    print("Processing Ghostdraft config")
    set_integration_mode(con,'Y')
    insert_config(con, args)
    update_config(con, args)
    set_integration_mode(con,'N')

def main():
    example_text = '''

example:

 python configOsFormInterfaceConfigGhostdraft.py -c odev20181/ODEV20181@NY2ORA12CR1D_SE12CR1 --configGhostdraft -i delphidev -e ODEV20181 -d ODEV20181 -m ODEV20181
 python configOsFormInterfaceConfigGhostdraft.py -c odev20181/ODEV20181@NY2ORA12CR1D_SE12CR1 --configEloquence
'''

    #common parameters for all actions
    parent_parser = argparse.ArgumentParser(add_help=False)
    #parent_parser.add_argument('runDate',help='Health Check run date', nargs='?')

    parser = argparse.ArgumentParser(parents=[parent_parser], description='os_form_interface_config script.', epilog=example_text,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('-c','--conn', help='Connection string to database of format user/password@host_sid',required=True)   

    parser.add_argument('--configGhostdraft',help='Configure os_form_interface_config', action='store_true')
    parser.add_argument('-i','--instance', help='Ghostdraft instance')
    parser.add_argument('-e','--env', help='Ghostdraft environment')
    parser.add_argument('-d','--docservice', help='Ghostdraft environment document service')
    parser.add_argument('-m','--mapping', help='Ghostdraft environment mapping')
    
    parser.add_argument('--configEloquence',help='Configure os_form_interface_config', action='store_true')

    parser.add_argument('--checkConfig',help='Check os_form_interface_config Configuration', action='store_true')
    
    args = parser.parse_args()

    logger = logging.getLogger('OsFormConfigInterfaceSetup')        
    if args.configGhostdraft:
        '''print (args.configGhostdraft)
        print (args.instance)
        print (args.env)
        print (args.docservice)
        print (args.mapping)'''
        if (args.configGhostdraft and not(args.instance and args.env and args.docservice and args.mapping)):
            parser.error('The --configGhostdraft argument requires dependent arguements --instance, --env, --docservice and --mapping to process')
            sys.exit            
        else:
            con = cx_Oracle.connect(args.conn)
            config_gd_os_form_interface_config(con, args)
    elif args.configEloquence:
        con = cx_Oracle.connect(args.conn)
        config_eloq_os_form_interface_config(con, args)
    elif args.checkConfig:
        con = cx_Oracle.connect(args.conn)
        display_rows(con)
    else:
        print("Unknown Option - Exiting without any action.")

    con.close()

if __name__ == '__main__':
    main()
