select o.crystal_status, count(*)
from os_crystal_trigger o
group by o.crystal_status
;

select o.request_id, count(*) from os_crystal_trigger o
--where o.request_id = 4120077
--where o.request_id = 4114573
--where o.request_id = 4069905
group by o.request_id
order by 2 desc
;

select * 
from os_crystal_trigger o
where o.request_id = 4137477
--ORDER BY 1 DESC
--for update
;  
select * from os_crystal_trigger oct, os_form_version ofv
where ofv.os_crystal_trigger_fk = oct.os_crystal_trigger_pk
and oct.request_id = 4064218
--for update
;

select * from os_crystal_trigger oct, os_form_version ofv, os_form_distribution ofd
where ofd.source_record_fk = ofv.os_form_version_pk
and ofv.os_crystal_trigger_fk = oct.os_crystal_trigger_pk
and oct.request_id = 4064218
;

--select o.drive_destination, REPLACE(o.drive_destination, 'MO20181\ODSOutput', 'ODSOutput\MO20181')
update os_crystal_trigger o set o.drive_destination = REPLACE(o.drive_destination, 'MO20181\ODSOutput', 'ODSOutput\MO20181'),
             o.status_msg = '',
             o.crystal_status = 'SUBMITTED'
--from os_crystal_trigger o
;

update os_form_version o set o.status = 'SUBMITTED', o.drive_destination = REPLACE(o.drive_destination, 'MO20181\ODSOutput', 'ODSOutput\MO20181');

select * from os_form_distribution;

select * from os_form_request
order by 1 desc
;
