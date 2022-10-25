INSERT INTO SYSTEM_CONFIGS (ID, SYSTEM_CONFIG_GROUP_ID, DISPLAY_ORDER, LABEL, IDENTIFIER, VALUE, DESCRIPTION, CREATED_ON) 
VALUES (id, 1512010900000001, 8, 'Smart Classifier URL', 'smart.classifier.url', 'https://@hostname/SmartClassifier/', 'Smart Classifier URL for accessing UI', CONVERT (datetime2,GETDATE(),120))
-EOL-
INSERT INTO SYSTEM_CONFIGS (ID, SYSTEM_CONFIG_GROUP_ID, DISPLAY_ORDER, LABEL, IDENTIFIER, VALUE, DESCRIPTION, CREATED_ON) 
VALUES (id, 1512010900000001, 9, 'Login URL', 'login.url', 'https://@hostname/cas/login?service=', 'Smart Classifier CAS login URL', CONVERT (datetime2,GETDATE(),120))
-EOL-
INSERT INTO SYSTEM_CONFIGS (ID, SYSTEM_CONFIG_GROUP_ID, DISPLAY_ORDER, LABEL, IDENTIFIER, VALUE, DESCRIPTION, CREATED_ON) 
VALUES (id, 1512010900000001, 10, 'Logout URL', 'logout.url', 'https://@hostname/cas/logout?service=', 'Smart Classifier logout redirect URL', CONVERT (datetime2,GETDATE(),120))
-EOL-