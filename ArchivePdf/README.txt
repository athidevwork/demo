#Python is required to run this. Installation of the following modules should also be done as given below,

1. python -m pip install --upgrade pip
2. pip install cx_Oracle
3. easy_install mail

This tool support three different actions that could be run together or as separate steps too.

1. Get Metadata file (comma separated file of policy data to be used for archival process)
2. Purge DB Data (Calls the database archival tool to purge/archive the database output data)
3. Archive and remove the output PDF files from the file system as determined in Step 1.

 

