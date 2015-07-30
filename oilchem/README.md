oilchem
===============
oilchem Android客户端

oilchem主项目，asynchttp,pulltorefresh,viewpagerlibrary为其依赖的第三方开源项目...

 select  sms_id,sms_sendMsg_ID,sms_time,sms_message,sms_GroupId
 from ET_sms with (NOLOCK)
 where sms_GroupId > 0
 and sms_phone='15821422479'  and sms_time > '2014-05-07' order by sms_time desc

 select * from ET_sms_reply where username='15821422479'

生成签名文件
C:\Java\jdk1.6.0_37\bin\keytool -genkey -alias oilchem.keystore -keyalg RSA -validity 20000 -keystore oilchem123.keystore

给APK中添加签名文件
C:\Java\jdk1.6.0_37\bin\jarsigner -digestalg SHA1 -sigalg MD5withRSA -verbose -keystore oilchem123.keystore -signedjar oilchem_signed.apk oilchem.apk oilchem.keystore


