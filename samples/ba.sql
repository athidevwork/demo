select *
  from build_applied
 where timestamp > sysdate - 30
 order by timestamp desc;

exit;