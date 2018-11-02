--Expire a form
UPDATE os_form o SET o.valid_from_date = SYSDATE AND o.valid_to_date = SYSDATE WHERE o.form_id = '&formId';
UPDATE os_form_map o SET o.valid_from_date = SYSDATE AND o.valid_to_date = SYSDATE WHERE o.form_id = '&formId'; 
UPDATE os_form_device_map o SET o.valid_from_date = SYSDATE AND o.valid_to_date = SYSDATE WHERE o.form_id = '&formId';
UPDATE os_form_copy_type_map o SET o.valid_from_date = SYSDATE AND o.valid_to_date = SYSDATE WHERE o.form_id = '&formId'; 
UPDATE os_form_schedule o SET o.valid_from_date = SYSDATE AND o.valid_to_date = SYSDATE WHERE o.form_id = '&formId';

