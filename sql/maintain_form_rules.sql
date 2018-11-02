SELECT * FROM os_form o
ORDER BY o.subsystem, o.form_id;

SELECT * FROM os_form_map o
ORDER BY o.form_id;

select * from os_form o
order by o.subsystem, o.form_id;

select * from os_form_map o
where o.form_id in ('BILL', 'ES_BILL');

select * from os_form_rule o
where o.form_id in ('BILL', 'ES_BILL');

select * from os_form_device_map ofdm
where ofdm.form_id in ('BILL', 'ES_BILL')
order by ofdm.sys_create_time desc;

select * from os_form_schedule ofs
where ofs.form_id in ('BILL', 'ES_BILL');
