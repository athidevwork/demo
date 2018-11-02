--add a new form
INSERT INTO os_form 
(os_form_pk, subsystem, form_frequency, form_level, form_type, form_id, form_desc, sort_order, generate_no_b, Edition_Date, valid_from_date, valid_to_date, attach_b, ufe_b, ufe_interactive_b, generate_b, addl_sql, cvr_print_b, Interactive_b, lob_code, template_id) 
VALUES 
(oasis_sequence.nextval, 'PM', 'PERTRANS', 'TRANS', 'AUTO', '&formId', 'form description', 500, '', '1/1/2018', '1/1/1900', '1/1/3000','','','','','','','','','','');

--os_form_map (maps form to a transaction)
insert into os_form_map
()
VALUES
()

-- os_form_device if it is a new device (Add new device if needed)

--os_form_device_map (maps forms to a device like Email or Printer)
insert into os_form_device_map
()
VALUES
()

--os_form_copy_type if it is a new copy type
insert into os_form_copy_type
()
VALUES
()

--os_form_copy_type_map (maps form to send to a copy type like agent)
insert into os_form_copy_type_map
()
VALUES
()

--os_form_schedule
insert into os_form_schedule
()
VALUES
()