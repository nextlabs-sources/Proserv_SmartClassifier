SELECT HOSTNAME FROM EXTRACTORS WHERE JMS_PROFILE_ID = (SELECT ID from JMS_PROFILES WHERE PROVIDER_URL LIKE 'tcp://@hostname%')
-EOL-