EXECUTE COREPROPERTY.SYSTEMPROPERTY('globalcatalog.binddn','CN=MU CF SDAV,CN=USERS,DC=COL,DC=MISSOURI,DC=EDU');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('globalcatalog.password','Dbsb'||'&'||'484');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('globalcatalog.server','col.missouri.edu');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('report.root','/home/reportcenter/reports');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('report.cache','/tmp/reportcenter/reportcache');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('report.imageformats','PNG;GIF;JPG;BMP;SWF;SVG');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('report.cache.months','2');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('projex.server', 'devapp1.cf.missouri.edu');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server','massmail.missouri.edu');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server.port','25');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server','smtpinternal.missouri.edu');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server.port','25');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server','localhost');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.server.port','25');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.protocol','smtp');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.starttls.enable','false');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.authentication.username','mucfsdsfw@missouri.edu');
-- EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.authentication.password','crmc!94PGE');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.authentication.username','mucfsdservermessages@missouri.edu');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.authentication.password','sgaRwb.9');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.authentication.required','false');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('email.from','mucfsdservermessages@missouri.edu');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('crontasks.enabled','1');
EXECUTE COREPROPERTY.SYSTEMPROPERTY('crontasks.server','devapp1.cf.missouri.edu');

COMMIT;

