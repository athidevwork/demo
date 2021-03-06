DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --uninstall
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --install

rem DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --select_policies
rem DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -p -k2 2952329 -k3 5
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -p -k2 2952329 -k3 3

rem DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --regenerate_forms
rem removes the records from os_crystal_trigger for the request id and move those to submitted for ODS to reprocess
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -r

rem DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --reextract_forms
rem DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 --submit_eod_forms

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 2601448675
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2601448675

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2601506862
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2132411726
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2540651195
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2132434805

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -o -k4 01/01/2000 -k2 2602523949 (COI)
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -o -k4 01/01/2000 -k2 2602523947 (DECLARATIONS)

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -r

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 2601448675
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2601448675

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -o -k4 01/01/3000 -k2 2602523949 (COI)
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -o -k4 01/01/3000 -k2 2602523947 (DECLARATIONS)

DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 2601448675
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 2601448675
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 3000297786
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 3000297786
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 3517737776
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 3517737776


--check if there are any policy forms that are in scheduling
--update policy_info for the policies to regenerate
time DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -r
--check to make sure all forms processing is complete
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t1 -k1 3517737776
DocuproFormsTest.py -c TMLT20161SE/TMLT20161SE@NY2ORA12CR1C_SE12CR4 -t -k1 3517737776