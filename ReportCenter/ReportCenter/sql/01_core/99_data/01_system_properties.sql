EXECUTE CORE.SYSTEMPROPERTY('globalcatalog.binddn','CN=MU CF SDAV,CN=USERS,DC=COL,DC=MISSOURI,DC=EDU');
EXECUTE CORE.SYSTEMPROPERTY('globalcatalog.password','Dbsb'||'&'||'484');
EXECUTE CORE.SYSTEMPROPERTY('globalcatalog.server','col.missouri.edu');
EXECUTE CORE.SYSTEMPROPERTY('report.root','/home/reportcenter/reports');
EXECUTE CORE.SYSTEMPROPERTY('report.cache','/tmp/reportcenter/reportcache');
EXECUTE CORE.SYSTEMPROPERTY('report.imageformats','PNG;GIF;JPG;BMP;SWF;SVG');
EXECUTE CORE.SYSTEMPROPERTY('report.cache.months','2');
EXECUTE CORE.SYSTEMPROPERTY('projex.server', 'devapp1.cf.missouri.edu');
-- EXECUTE CORE.SYSTEMPROPERTY('email.server','massmail.missouri.edu');
-- EXECUTE CORE.SYSTEMPROPERTY('email.server.port','25');
-- EXECUTE CORE.SYSTEMPROPERTY('email.server','smtpinternal.missouri.edu');
-- EXECUTE CORE.SYSTEMPROPERTY('email.server.port','25');
EXECUTE CORE.SYSTEMPROPERTY('email.server','localhost');
EXECUTE CORE.SYSTEMPROPERTY('email.server.port','25');
EXECUTE CORE.SYSTEMPROPERTY('email.protocol','smtp');
EXECUTE CORE.SYSTEMPROPERTY('email.starttls.enable','false');
-- EXECUTE CORE.SYSTEMPROPERTY('email.authentication.username','mucfsdsfw@missouri.edu');
-- EXECUTE CORE.SYSTEMPROPERTY('email.authentication.password','crmc!94PGE');
EXECUTE CORE.SYSTEMPROPERTY('email.authentication.username','mucfsdservermessages@missouri.edu');
EXECUTE CORE.SYSTEMPROPERTY('email.authentication.password','sgaRwb.9');
EXECUTE CORE.SYSTEMPROPERTY('email.authentication.required','false');
EXECUTE CORE.SYSTEMPROPERTY('email.from','mucfsdservermessages@missouri.edu');
EXECUTE CORE.SYSTEMPROPERTY('crontasks.enabled','1');
EXECUTE CORE.SYSTEMPROPERTY('crontasks.server','devapp1.cf.missouri.edu');

COMMIT;

