/*
 * Created by @pkalra
 */
mainApp.factory('repoCredentialsService', ['loggerService', 'networkService', 'configService', 'statusCodeService',
    function(loggerService, networkService, configService, statusCodeService) {

      var PAGE_SIZE = configService.configObject['defaultCredentialsPageSize'];

      var getRepoCredentialList = function(sortFields, pageNo, callback) {
        loggerService.getLogger().info("Trying to get the list of repository credentials, page number = " + pageNo)

        var requestData = {
          'sortFields': sortFields,
          'pageNo': pageNo,
          'pageSize': PAGE_SIZE
        }

        networkService.post(configService.getUrl("repocreds.list"), requestData, function(data) {
          statusCodeService.list("Repository Credentials", callback, data);
        });
      };

      var getCredential = function(credentialID, callback) {
        loggerService.getLogger().info("Getting credential with ID = " + credentialID);
        
        loggerService.getLogger().debug(configService.getUrl("repocreds.get") + credentialID);
        
        networkService.get(configService.getUrl("repocreds.get") + credentialID, function(data) {
          statusCodeService.get("Repository Credentials", callback, data);
        });

      };
      
      var modifyCredential = function(credential, callback) {
        loggerService.getLogger().info("Modifying credential with ID = " + credential.id);
        var requestData = credential;
        networkService.put(configService.getUrl("repocreds.modify"), requestData, function(data) {
          statusCodeService.modify("Repository Credentials", callback, data);
        });
      };

      var deleteCredentials = function(id, lastModifiedDate, callback) {
        loggerService.getLogger().info("Deleting credentials ID = " + id);

        networkService.del(configService.getUrl("repocreds.delete") + id + "/" + lastModifiedDate, function(data) {
          statusCodeService.del("Repository Credentials", callback, data);
        });

      };

      var createCredentials = function(credentials, callback) {
        loggerService.getLogger().debug("Creating a new set of credentials");

        var requestData = credentials;

        networkService.post(configService.getUrl("repocreds.add"), requestData, function(data) {
          statusCodeService.create("Repository Credentials", callback, data);
        });
      };

      return {
        getRepoCredentialList: getRepoCredentialList,
        getCredential: getCredential,
        modifyCredential: modifyCredential,
        deleteCredentials: deleteCredentials,
        createCredentials: createCredentials
      }
    }]);
