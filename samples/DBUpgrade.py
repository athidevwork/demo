#!/usr/bin/env python
# ==============================================================================
# mm/dd/yyyy 1.0.0: initial version
# 06/30/2017 1.0.1: With Dat Load.
# ------------------------------------------------------------------------------

import os
import sys
import argparse
import datetime
import json
from collections import OrderedDict
import re
import subprocess
import fileinput
import logging
import logging.config
import shutil

"""
Description:
    Utility to run the Database upgrade steps; individual step or all at once.

    This tool depends on 3 config file:
        dbupgrade.json - holds the configuration about the required folders and sql scripts.
        dbupgrade_logging.json - used to configure and format the log file/log messages.
        dbupgrade_errors.json - a dictionary of error codes and message to look for while parsing the log files.

Example:
    DBUpgrade conn step [--verbose] [--debug]

Args:
    conn: Database connection string in the format username/password@database.
    step: Indicates the step to run. Accepted values are, STEP1, STEP2, STEP3 or ALL.

"""

__author__ = 'mywahba'
__version__ = '1.0.1'

# global variables
dir_dict = {}
sql_dict = {}
CONFIG_FILE = 'dbupgrade.json'
ERRORS_FILE = 'dbupgrade_errors.json'
logger = logging.getLogger('dbupgrade')


class MyParser(argparse.ArgumentParser):
    """
    Description:
        Extends ArgumentParser class to override the error method to print the usage
        when the program is called with incorrect number of arguments.

    TODO: move MyParser out to a separate module and use import to access it.
    """
    def error(self, message):
        sys.stderr.write('Error: %s\n' % message)
        print('\n')
        self.print_help()
        sys.exit(2)


def load_config(cfg_file):
    """
    Description:
        Load config file into a dictionary object.
    Parameters:
        cfg_file(str): name of the dbupgrade tool config file.
    Return:
        OrderdDict: dictionary object contains loaded configuration.
    """
    cfg = {}
    if os.path.exists(cfg_file):
        with open(cfg_file, 'r') as f:
            try:
                s = f.read()

                # load the string as OrderedDict
                cfg = json.loads(s, object_pairs_hook=OrderedDict)
            except IOError:
                print("Could not read config file, {}".format(cfg_file))
    else:
        print("Could not find config file, {}".format(cfg_file))

    return cfg


def setup_logging(
    log_file='dbupgrade.log',
    logger_name='dbupgrade',
    default_path='dbupgrade_logging.json',
    default_level=logging.INFO
):
    """Setup logging configuration

    """
    path = default_path
    log_handler = '{}_handler'.format(logger_name)
    if os.path.exists(path):
        with open(path, 'rt') as f:
            config = json.load(f)

        config['handlers'][log_handler]['filename'] = log_file
        logging.config.dictConfig(config)
    else:
        logging.basicConfig(level=default_level)

    return logging.getLogger(logger_name)


def chk_required_fldrs(dirs):
    # build folder strings
    ret_status = True
    scripts_fldr = os.path.join(dirs['root'], dirs['scripts'])
    scripts_fldr_status = True
    logs_fldr = os.path.join(dirs['root'], dirs['logs'])
    logs_fldr_status = True
    history_fldr = os.path.join(dirs['root'], dirs['logshistory'])
    history_fldr_status = True

    if not (os.path.exists(scripts_fldr)):
        logger.error('Scripts Folder is missing - {}'.format(scripts_fldr))
        scripts_fldr_status = False

    if os.path.exists(logs_fldr):
        logger.info('Logs Applies folder already exists - {}.'.format(logs_fldr))
    else:
        logs_fldr_status = create_folder(logs_fldr)
        if ret_status:
            logger.info('Logs Applies folder was created successfully - {}.'.format(logs_fldr))
        else:
            logger.error('Could not create the Applies folder - {}'.format(logs_fldr))

    if os.path.exists(history_fldr):
        logger.info('Logs History folder already exists - {}.'.format(history_fldr))
    else:
        history_fldr_status = create_folder(history_fldr)

        if history_fldr_status:
            logger.info('Logs History folder was created successfully - {}.'.format(history_fldr))
        else:
            logger.error('Could not create the History folder - {}'.format(history_fldr))

    ret_status = (scripts_fldr_status and logs_fldr_status and history_fldr_status)

    return ret_status


# TODO: move this to the FileUtil class
def create_folder(path):
    # print("\nattempting to create {}".format(path))

    if os.path.exists(path):
        # print("\t\tthis folder already exists.")
        pass

    if os.path.isdir(path):
        # print("\t\tthis path is a folder.")
        pass

    if not (os.path.exists(path)) or not (os.path.isdir(path)):
        try:
            os.makedirs(path)
            print('\nFolder created - {}'.format(path))
            return True
        except OSError as err:
            print('\nCould not create {}.'.format(path))
            print('Error: ', err)
            return False

    return True


# TODO: Move this to the LogFile class
def create_logs_fldr(path, schema, datetime, step):
    # assumptions: timestamp is in the format %Y%m%d_%H%M%S (yyyymmdd_hhmiss)
    # datepart = re.split(r'[_]', datetime)[0]
    logs_fldr = os.path.join(path, schema, "{}.{}".format(datetime, step))

    if create_folder(logs_fldr):
        return logs_fldr
    else:
        return None


# Load Web Config.
# TODO: verify if sqlldr is installed.
def load_web_dat(path, script, conn):
    ret_status = False
    cwd = os.getcwd()
    os.chdir(path)
    logger.info('Web DAT Load - Started')
    logger.info('Current working directory: {}'.format(cwd))
    logger.info('WebWB path: {}'.format(path))
    logger.info('WebWB Script: {}'.format(script))
    logger.info('Command: {} -b {}'.format(script, conn))

    try:
        proc = subprocess.run([script, conn],
                              shell=True,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE
                              )
    except subprocess.CalledProcessError as err:
        logger.exception('Exception: {}'.format(err))
        ret_status = False
    else:
        ret_status = True
        logger.info('Return Code: {}\n\n'.format(proc.returncode))
        if len(proc.stdout) > 0:
            logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
        if len(proc.stderr) > 0:
            logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

    os.chdir(cwd)
    logger.info('Web DAT Load - Ended')
    return ret_status


# TODO: Create an RESqlUtil class
def run_sql_script(conn, script):
    # add @ at the beginning of the script name to run it in sqlplus.
    script = '@' + script
    ret_status = False

    logger.info('\n\nvvvvvvvvvv {} - START vvvvvvvvvv\n'.format(script))
    try:
        # proc_exit = subprocess.run(['echo', 'exit'], shell=True, stdout=subprocess.PIPE)

        proc = subprocess.run(['sqlplus', '-l', '-s', conn, script],
                              shell=True,
                              # stdin=proc_exit.stdout,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE
                              )
    except subprocess.CalledProcessError as err:
        logger.exception('{}'.format(err))
        ret_status = False
    else:
        ret_status = True
        logger.info('Command: {}\n\n'.format(' '.join(proc.args)))
        logger.info('Return Code: {}\n\n'.format(proc.returncode))
        if len(proc.stdout) > 0:
            logger.info('Captured output:\n{}'.format(proc.stdout.decode('utf-8')))
        if len(proc.stderr) > 0:
            logger.error('Captured errors:\n{}'.format(proc.stderr.decode('utf-8')))

    logger.info('^^^^^^^^^^ {} - COMPLETED ^^^^^^^^^^'.format(script))

    return ret_status


def get_schema(conn):
    conn_parts = re.split(r'[\/\@]', conn)
    return conn_parts[0]+"."+conn_parts[2]


def create_run_script(scripts_path, orig_filename, run_filename, logs_path, schema):
    ret_status = True
    run_script = os.path.join(scripts_path, run_filename)
    orig_script = os.path.join(scripts_path, orig_filename)
    logger.info("Creating run script - {}".format(run_script))

    starts_with = '@@build_config.sql'
    RE_ENV_INIT = os.path.join(scripts_path, "RE_Bld_Init_Env_{}.sql".format(schema))
    scripts_tmp = "{}\\".format(os.path.join(logs_path, 'TMP'))
    create_folder(scripts_tmp)

    if logs_path[-1] != '\\':
        logs_path += '\\'

    # if original script does not exist, exit
    if not (os.path.exists(orig_script)):
        # TODO: raise exception and exit.
        logger.error('Could not find {}'.format(orig_script))
        ret_status = False
    else:
        # if backup file does not exist, make one
        # TODO: add try...except block
        logger.info('Copying {} to {}.'.format(orig_script, run_script))
        shutil.copyfile(orig_script, run_script)
        logger.info('{} was created successfully.'.format(run_script))

        logger.info('Looking for lines starts with {}'.format(starts_with))
        with fileinput.FileInput(run_script, inplace=True) as file:
            for line in file:
                print(line, end='')

                if line.lower().startswith(starts_with):
                    logger.info('Match found...\n')
                    logger.info('Setting OASIS_BUILD_LOGS to {}\n'.format(logs_path))
                    statement = '''

REM ==========================================================================
REM This section was added by the DB Upgrade process @ {}
REM Re-define the DEFINE OASIS_BUILD_LOGS to point to the schema log folder.
REM ==========================================================================
UNDEFINE OASIS_BUILD_LOGS
DEFINE OASIS_BUILD_SCRIPTS_TMP="&OASIS_BUILD_ROOT.Scripts&OS_SLASH.Tmp&OS_SLASH"
DEFINE OASIS_BUILD_LOGS="{}"
DEFINE INTERACTIVE_MODE='OFF'

DEFINE RE_ENV_INIT="{}"
DEFINE OASIS_BUILD_SCRIPTS_TMP="{}"\n\n'''.format(datetime.datetime.now().strftime('%m/%d/%Y %H:%M:%S'),
                                                  logs_path, RE_ENV_INIT, scripts_tmp)
                    print(statement)
                    logger.info('Injected statements: \n{}'.format(statement))

        # could not append to the end of the file using FileInput.
        # reopen the file to add exit command at the end to make sure
        # the SqlPlus subprocess exits at the end.
        logger.info('Adding exit to the run script - {}'.format(run_script))
        f = open(run_script, "a+")
        f.write("\nexit\n")
        f.close()
        ret_status = True

    return ret_status


def check_log(step, path):
    # TODO: get the name of the script causing the error.
    # TODO: Launch Notepad and show the content of th log file.
    status = True
    errors_list = []
    error_counter = {}
    severity1_dict = {}
    severity2_dict = {}
    severity3_dict = {}

    steplog_filename = os.path.join(path, '{}_checked.log'.format(step))
    step_logger = setup_logging(steplog_filename, 'stepslog')

    step_logger.info('Loading the list of potential errors from {}.'.format(ERRORS_FILE))
    # load the error list from config file.
    err_dict = load_config(ERRORS_FILE)

    if not err_dict:
        step_logger.error('{} is empty or does not exist.'.format(CONFIG_FILE))
        sys.exit(2)

    step_logger.info('{} error codes were loaded from {}'.format(len(err_dict), ERRORS_FILE))

    # load the log file and find only the lines starting with SP- or ORA-
    logfile_name = '{}.log'.format(step)
    logfile_path = os.path.join(path, logfile_name)

    step_logger.info('{} log file: {}'.format(step.capitalize(), logfile_path))

    step_logger.info('Extracting the error lines from {}'.format(logfile_name))
    with open(logfile_path, 'r') as f:
        step_logger.debug('Errors found in the log file:')
        for l in f.readlines():
            line = l.strip()
            if line.startswith('SP2-') or line.startswith('ORA-'):
                errors_list.append(line)
                step_logger.debug('\t{}'.format(line))
                # look for ORA-nnnnn or SP2-nnnn errors
                error_code = re.findall('(ORA-[0-9]+|SP2-[0-9]+)', line)[0]

                # get count per error code.
                if error_code in error_counter:
                    error_counter[error_code] += 1
                else:
                    error_counter[error_code] = 1

    # write the results into the log file
    # print(error_counter)
    missing_error_codes = []

    if len(error_counter) > 0:
        print("{} - errors found. Please check the log file.\n\n{}".format(step.capitalize(), steplog_filename))
        for code, count in error_counter.items():
            try:
                error_rec = err_dict[code]
                error_rec[1] = error_rec[1].replace('$ERRCOUNT$', str(count))

                if error_rec[0] == 1:
                    severity1_dict[code] = error_rec[1:]  # remove the first item (severity level), it is not needed.
                elif error_rec[0] == 2:
                    severity2_dict[code] = error_rec[1:]
                else:
                    severity3_dict[code] = error_rec[1:]
            except KeyError as err:
                logger.error("Could not find {} error code in {}".format(code, ERRORS_FILE))
                missing_error_codes.append(code)

        # TODO: clean this up and replace it with a function that return a formatted string.
        if len(severity1_dict) > 0:
            status = False
            msg = 'Severity 1 type of errors found. These errors need to be resolved before continuing.\n\n'
            # TODO: clean this up and replace it with a function that return a formatted string.
            for code, msg_lines in severity1_dict.items():
                msg += '\n{}:\n\t{}\n\n'.format(code, '\n\t'.join(msg_lines))

            step_logger.error(msg)

        if len(severity2_dict) > 0:
            msg = 'Severity 2 type of errors found. These errors need to be reviewed before continuing. It may be harmless.\n\n'
            status = False
            for code, msg_lines in severity2_dict.items():
                msg += '\n{}:\n\t{}\n'.format(code, '\n\t'.join(msg_lines))

            step_logger.error(msg)

        if len(severity3_dict) > 0:
            status = False
            msg = 'Severity 3 type of errors found. These errors can be ignored.\n\n'
            for code, msg_lines in severity3_dict.items():
                msg += '\n{}:\n\t{}'.format(code, '\n\t'.join(msg_lines))

            step_logger.error(msg)

        if len(missing_error_codes) > 0:
            status = False
            msg = 'Error codes found in the log file but missing from {}\n\n'.format(ERRORS_FILE)
            msg += '\n'.join(missing_error_codes)

            step_logger.error(msg)
    else:
        msg = '{} - No errors found.'.format(step.capitalize())
        step_logger.info(msg)
        print(msg)

    return status


def process_step(conn, step, path, sql_dict, logs_path):
    # TODO: check if the step exists in the config file first.
    # TODO: Check if the required script exists before launching sqlplus
    # TODO: implement "ALL" steps.
    ret_status = True
    msg = '{} Started'.format(step.capitalize())
    logger.info(msg)

    schema = get_schema(conn).split('@')[0]
    script = sql_dict[step.lower()]
    script_filepath = os.path.join(path, script)

    # if config file does not exist, exit
    if not (os.path.exists(script_filepath)):
        # TODO: raise exception and exit.
        msg = '{} - could not find script - {}'.format(step.capitalize(), script_filepath)
        logger.error(msg)
        print(msg)
        ret_status = False
    else:
        # create_run_script(scripts_path, script_name, logs_path, schem_name, step)

        run_filename = 'run_{}_{}_{}.sql'.format(step.lower(), schema, datetime.datetime.now().strftime('%Y%m%d_%H%M%S'))
        run_filepath = os.path.join(path, run_filename)
        if create_run_script(path, script, run_filename, logs_path, schema):
            logger.info('Run script was created successfully.')
        else:
            msg = "{} - Failed to create temp run script - {}".format(step.capitalize(), run_filepath)
            logger.error(msg)
            print(msg)

        msg = "{} script - Start".format(step.capitalize())
        logger.info(msg)
        print(msg)
        print("Processing...")
        if not run_sql_script(conn, run_filepath):
            ret_status = False
            msg = '\n{} - Failed to run script for step {}.\n\t{}\n'.format(step.capitalize(), step, run_filepath)
            logger.error(msg)
            print(msg)
        else:
            ret_status = True

        msg = '{} script - Completed'.format(step.capitalize())
        logger.info(msg)
        print('{}\n'.format(msg))

        # TODO: add try...except block
        shutil.move(run_filepath, logs_path)

        if step.lower() == 'step3':
            webdat_path = os.path.join(dir_dict['root'], dir_dict['webwb'])
            webdat_script = sql_dict['webwb']
            webdat_script_path = os.path.join(webdat_path, webdat_script)
            load_web_cfg = True
            missing_webwb_fldr = False
            missing_webwb_script = False

            if not (os.path.exists(webdat_path)):
                load_web_cfg = False
                missing_webwb_fldr = True
            elif not (os.path.exists(webdat_script_path)):
                load_web_cfg = False
                missing_webwb_script = True

            if load_web_cfg:
                msg = 'Loading Base Web Config - Started'
                logger.info(msg)
                print(msg)
                load_web_dat(webdat_path, webdat_script, conn)
                msg = 'Loading Base Web Config - Ended'
                print(msg)
            else:
                if missing_webwb_fldr:
                    msg = 'Web configuration is not included in this release delivery.'
                elif missing_webwb_script:
                    msg = '{} script is missing, please contact Delphi Release Engineering'.format(webdat_script_path)
                logger.error(msg)
                print(msg)

        msg = '\n{} - Checking log file for errors - calling check_log.'.format(step.capitalize())
        logger.info(msg)
        print(msg)

        ret_status = check_log(step, logs_path)

    return ret_status


def get_steps(sql_dict):
    return OrderedDict(sorted(dict((k, v) for k, v in sql_dict.items() if k.startswith('step')).items()))


def get_steps_id(sql_dict):
    return sorted([k for k, v in sql_dict.items() if k.startswith('step')])


def get_steps_value(sql_dict):
    ordered_dict = get_steps(sql_dict)

    return list(ordered_dict.values())


def process_all_steps(conn, scripts_path, sql_dict, logs_path):
    ret_status = True
    # get sorted steps list
    # NOTE: assumption - number of steps is less than 10. If the step number is > 9,
    #       the list will be sorted incorrectly (10 comes before 2 in string sorting).
    #       if this is the case we need to revisit this logic.
    steps_list = get_steps_id(sql_dict)

    for step in steps_list:
        ret_status = process_step(conn, step, scripts_path, sql_dict, logs_path)

        if ret_status:
            logger.info('{} was completed successfully.'.format(step.capitalize()))
        else:
            logger.error('{} failed. Aborting!!'.format(step.capitalize()))
            break  # stop processing the next step

    return ret_status


def adjust_bld_cfg(path, config_file, build_root):
    # TODO: pass in the replacements as a dictionary to allow multiple adjustments.
    ret_status = False
    starts_with = 'DEFINE OASIS_BUILD_ROOT'

    if build_root[-1] != '\\':
        build_root += '\\'
    replacement = '{}="{}"\n'.format(starts_with, build_root)

    backup_file = '{}.bak'.format(config_file.split('.')[0])
    backup_file_path = os.path.join(path, backup_file)
    config_file_path = os.path.join(path, config_file)

    # if config file does not exist, exit
    if not (os.path.exists(config_file_path)):
        # TODO: raise exception and exit.
        logger.error('Could not find config file - {}'.format(config_file_path))
        ret_status = False
    else:
        # if backup file does not exist, make one
        if not (os.path.exists(backup_file_path)):
            # TODO: add try...except block
            logger.info('Making a backup copy of {}'.format(config_file))
            shutil.copyfile(config_file_path, backup_file_path)
            logger.info('{} was created successfully.'.format(backup_file))

        with fileinput.FileInput(config_file_path, inplace=True) as file:
            for line in file:
                if line.startswith(starts_with):
                    logger.info('Adjusting the OASIS_BUILD_ROOT variable in {}'.format(config_file))
                    logger.info('Replacing:\n\t{}\nwith:\n\t{}\n'.format(line, replacement))
                    line = replacement

                print(line, end='')
        ret_status = True

    return ret_status


def show_user_notice(conn, build_id):
    notice = '''
******************************************************************************
******************************************************************************
*
*   NOTICE TO USERS
*   ===============
*   Release Notes, Upgrade Installation Instructions, must be followed
*   to accurately install Oasis {} release.
*   Please obtain printed copy from Doc\System directory.
*
*   Connection: {}.
*
*   Please verify the above connection info and press "Y" to proceed or
*   "N" to terminate.
*
*******************************************************************************
*******************************************************************************

Continue (Y/N)? '''.format(build_id, conn)

    answer = input(notice)

    return answer


def get_build_id(scripts_path, sql_dict):
    bld_id = ''
    starts_with = "DEFINE OASIS_BUILD_ID"
    bldcfg_script = sql_dict['bldcfg']
    bldcfg_path = os.path.join(scripts_path, bldcfg_script)

    with fileinput.FileInput(bldcfg_path, inplace=False) as file:
        for line in file:
            if line.startswith(starts_with):
                # return the part after "=" and remove the "'" if exists.
                bld_id = line.split('=')[1].replace("'", "")
                break

    return bld_id


def chk_required_tools(tools):
    ret_status = True

    for tool in tools:
        if shutil.which(tool) is None:
            ret_status = False
            msg = '"{}" is missing.'.format(tool)
            logger.error(msg)
            print(msg)

    if ret_status:
        msg = 'All required tools were found.'
        logger.info(msg)
        print(msg)

    return ret_status


def chk_required_files(path, step, sql_dict):
    ret_status = True

    # TODO: add logic to check for the existence of the build_config.sql file.
    bldcfg_script = os.path.join(path, sql_dict['bldcfg'])

    if step.lower() == 'all':
        scripts_list = get_steps_value(sql_dict)

        for script in scripts_list:
            script_path = os.path.join(path, script)
            if not os.path.exists(script_path):
                ret_status = False
                msg = "required file missing - {}".format(script_path)
                logger.error(msg)
                print(msg)
    else:
        script_path = os.path.join(path, sql_dict[step])
        if not os.path.exists(script_path):
            ret_status = False
            msg = "required file missing - {}".format(script_path)
            logger.error(msg)
            print(msg)

    return ret_status


def dbupgrade(conn, step):
    # TODO: Clean up the code and implement --verbose/--debug to control the printing.
    # TODO:     -> create a method to print messages to screen.
    # TODO:     -> create a help method to print out the usage of the script.
    # TODO: <DONE> Create/Write to log file.
    # TODO: <DONE> Update Build_Config.SQL and change the OASIS_BUILD_ROOT variable to match the build path
    # TODO: Implement Steps 1-3 & ALL
    # TODO: <DONE> Due to the need to re-define some variables, we need to create our own step1-3 scripts.
    # TODO: <DONE>   -> the script names should be run_<step>_<schema>_<timestamp>.sql
    # TODO: <DONE>   -> first step in the script is to run the build_config.sql
    # TODO: <DONE>   -> next, re-define OASIS_BUILD_LOGS need to be
    # TODO: <DONE>       &OASIS_BUILD_ROOT.Logs&OS_SLASH.Applies&OS_SLASH.<schema>&OS_SLASH.<date>&OS_SLASH
    # TODO: <DONE>   -> last, issue @'&RE_BUILD_MASTER_STEP1', 2 or 3 (may be I can pick up thid from the json file)
    # TODO: <DONE> Validate the logs and display the proper message (convert the perl scripts into Python class/methods).
    # TODO: Zip log files.
    # TODO: Add a progress file into the schema log folder.
    # TODO: Add a custom step at the end and add a switch to control if you want to run it or not
    # TODO: Apply the tip pull build.
    # TODO: Run the Web Dat Load.
    # TODO: Run Post Install script

    # Initialization
    #################
    global dir_dict
    global sql_dict
    step = step.lower()

    schema = get_schema(conn)

    # Get datetime stamp and convert it to string
    ver_msg = 'DBUpgrade Tool - Version {}\n'.format(__version__)
    print(ver_msg)
    sysdate = datetime.datetime.now()
    date_str = sysdate.strftime('%Y%m%d')
    datetime_str = sysdate.strftime('%Y%m%d_%H%M%S')
    command = ' '.join(sys.argv)

    log_filename = 'dbupgrade_{}_{}.log'.format(schema, datetime_str)

    print("Loading config file...")
    # Load configuration from file.
    cfg_dict = load_config(CONFIG_FILE)

    if not cfg_dict:
        print("Setting up log file...")
        logger = setup_logging(log_filename, 'dbupgrade')
        logger.info(ver_msg)
        logger.info('Command Line: {}'.format(command))
        logger.info('\n\tConn: {}'.format(conn))
        logger.info('\tStep: {}'.format(step))
        logger.info('\tTime: {}'.format(sysdate.strftime('%m/%d/%Y %H:%M:%S')))
        logger.error('{} is empty or does not exist.'.format(CONFIG_FILE))
        sys.exit(2)

    # Get the dir and sql sections of the json file.
    dir_dict = cfg_dict['dir']
    sql_dict = cfg_dict['sql']
    tools_list = cfg_dict['tools']

    build_root = os.path.abspath(dir_dict['root'])
    scripts_path = os.path.join(build_root, dir_dict['scripts'])

    print("Creating log folders...")
    logs_root = os.path.join(build_root, dir_dict['logs'])
    logs_path = create_logs_fldr(logs_root, schema, datetime_str, step)

    if not logs_path:
        # TODO: raise exception and exit
        print('ERROR: create_logs_fldr failed')

    logfile_path = os.path.join(logs_path, log_filename)

    print("Setting up log file...")
    logger = setup_logging(logfile_path, 'dbupgrade')
    logger.info(ver_msg)
    logger.info('Command Line: {}'.format(command))
    logger.info('Conn: {}'.format(conn))
    logger.info('Step: {}'.format(step))
    logger.info('Time: {}'.format(sysdate.strftime('%m/%d/%Y %H:%M:%S')))
    logger.info('Build Root: {}'.format(build_root))
    logger.info('Scripts Root: {}'.format(scripts_path))

    logger.info('Calling chk_required_tools')
    print("Checking required tools...")
    if not chk_required_tools(tools_list):
        logger.error('One or more required tools missing.')
        sys.exit(2)

    logger.info('Calling chk_required_fldrs')
    print("Checking required folders...")
    if not chk_required_fldrs(dir_dict):
        logger.error('Required folders missing.')
        sys.exit(2)

    logger.info('Calling chk_required_files')
    print("Checking required files...")
    if not chk_required_files(scripts_path, step, sql_dict):
        logger.error('Required files missing.')
        sys.exit(2)

    print("Retrieving Build ID...")
    build_id = get_build_id(scripts_path, sql_dict)

    user_input = show_user_notice(conn, build_id)
    if not (user_input.lower() == 'y'):
        msg = "Exiting - process interrupted by user."
        logger.error(msg)
        print(msg)
        sys.exit(3)

    # make any required adjustment to the build config script (BUILD_CONFIG.SQL)
    logger.info('Calling adjust_bld_cfg')
    if adjust_bld_cfg(scripts_path, sql_dict['bldcfg'], build_root):
        logger.info('{} was adjusted successfully'.format(sql_dict['bldcfg']))
    else:
        logger.info('Failed to adjust {}.'.format(sql_dict['bldcfg']))

    # Process step(s).

    if step.lower() == 'all':
        print(">>>START<<< Processing ALL steps.")
        logger.info('Calling process_all_steps')
        process_all_steps(conn, scripts_path, sql_dict, logs_path)
    else:
        print(">>>START<<< Processing {} \n".format(step.upper()))
        logger.info('Calling process_step')
        process_step(conn, step, scripts_path, sql_dict, logs_path)

    print("\n>>>END<<< Processing step(s).\n")
    print(ver_msg)


def main():
    parser = MyParser()
    parser.add_argument("conn", help='DB connection string in the format username/password@database')
    parser.add_argument("step", help='[STEP1 | STEP2 | STEP3 | ALL] Specify which step to run.')
    parser.add_argument("--verbose", help='Optional - Turn verbose on.')
    parser.add_argument("--debug", help='Optional - Turn debug on.')
    args = parser.parse_args()

    cmd = ' '.join(sys.argv)
    print('Command: {}'.format(cmd))
    dbupgrade(args.conn, args.step)


if __name__ == '__main__':
    main()
