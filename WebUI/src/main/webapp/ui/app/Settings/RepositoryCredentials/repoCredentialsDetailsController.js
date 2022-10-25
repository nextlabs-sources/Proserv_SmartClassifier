/**
 * 
 */
mainApp.controller('repoCredentialsDetailsController', [
    '$scope',
    'repoCredentialsService',
    '$state',
    '$location',
    '$stateParams',
    '$filter',
    'dialogService',
    '$anchorScroll',
    'loggerService',
    function($scope, repoCredentialsService, $state, $location, $stateParams, $filter, dialogService, $anchorScroll,
            loggerService) {

      $scope.reloadCredentials = function() {

        repoCredentialsService.getCredential($scope.credentialID, function(response) {
          $scope.credential = response.data;
          $scope.copyResetObjects();

          loggerService.getLogger().debug($scope.credential);

          if (!$scope.credential) {
            $scope.detailsFound = false;
          } else {
            $scope.detailsFound = true;
          }
        });
      };

      $scope.backToCredentialsList = function(form) {
        if (!$scope.dataPristine()) {
          dialogService.confirm({
            msg: $filter('translate')('details.back.confirm'),
            ok: function() {
              $state.go("RepositoryCredentials");
            }
          })
        } else {
          $state.go("RepositoryCredentials");
        }
      };

      $scope.dataPristine = function() {
        if (!angular.equals($scope.credential, $scope.resetCredential)) { return false; }

        return true;
      };

      $scope.copyResetObjects = function() {
        $scope.resetCredential = angular.copy($scope.credential);
      };

      // scrollTo functionalities
      $scope.scrollTo = function(target) {
        loggerService.getLogger().info("Scrolling to " + target);
        $location.hash(target);
        $anchorScroll();
        currentTarget = target;
      };

      $scope.discardChanges = function(form) {
        loggerService.getLogger().info("Discarding changes");
        $scope.credential = angular.copy($scope.resetCredential);
        form.$setPristine();
      };

      $scope.saveForm = function(form) {
        if (form.$invalid) {
          form.$setDirty();
          for ( var field in form) {
            if (field[0] == '$') continue;
            // console.log(field);
            form[field].$touched = true;
          }
          return;
        }

        if ($scope.isCreated) {
          repoCredentialsService.createCredentials($scope.credential, function(response) {
            $state.go("RepositoryCredentials");
            loggerService.getLogger().debug(response.message);
          });
        } else {
          repoCredentialsService.modifyCredential($scope.credential, function(response) {
            $scope.copyResetObjects();
            loggerService.getLogger().debug(response.message);
          });
        }
        form.$setPristine();
      };

      $scope.hideShowPassword = function() {
        $scope.inputTypePassword = !$scope.inputTypePassword;
      };

      loggerService.getLogger().debug($scope.$parent);
      $scope.$parent.isDetailsPage = true;

      $scope.credentialID = $stateParams.id;
      $scope.inputTypePassword = "true";

      if ($scope.credentialID == "create") {
        $scope.credential = {
          "name": "",
          "username": "",
          "domain": "",
          "password": ""
        };
        $scope.detailsFound = true;
        $scope.resetCredential = angular.copy($scope.credential);
        $scope.isCreated = true;
      } else {
        $scope.reloadCredentials();
        $scope.isCreated = false;
      }
      loggerService.getLogger().debug(JSON.stringify($scope.credential));
    }]);