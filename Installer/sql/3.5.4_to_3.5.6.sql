INSERT INTO SYSTEM_CONFIGS (ID, SYSTEM_CONFIG_GROUP_ID, DISPLAY_ORDER, LABEL, IDENTIFIER, VALUE, DESCRIPTION, CREATED_ON) 
VALUES (1512010900000864, 1512010900000004, 7, 'Extract Body', 'e.extract.extractBody', 'false', 'Setting for extract file content or body, true/false.', CONVERT (datetime2,GETDATE(),120));

INSERT INTO SYSTEM_CONFIGS (ID, SYSTEM_CONFIG_GROUP_ID, DISPLAY_ORDER, LABEL, IDENTIFIER, VALUE, DESCRIPTION, CREATED_ON) 
VALUES (1512010900000865, 1512010900000003, 4, 'Check Last Modified', 'w.check.lastmodified', 'false', 'Setting for watcher to check last modified before sending to queue, true/false.', CONVERT (datetime2,GETDATE(),120))