/*
 * This is a shared service is a gateway for network calls (ajax)
 */
mainApp.factory('networkService', ['$window', '$http', '$filter', 'dialogService',
    function($window, $http, $filter, dialogService) {
      var errorHandler = function(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        // handle generic network errors

        console.error("Status error is " + response.status);
        switch (response.status) {
        case 404:
          console.info("Not found");
          dialogService.notify({
            type: "sc-dialog-error",
            msg: $filter('translate')('ERROR.400'),
            ok: function() {

            }
          });
          break;
        case 500:
          console.info("Internal");
          dialogService.notify({
            type: "sc-dialog-error",
            msg: $filter('translate')('ERROR.500'),
            ok: function() {

            }
          });
          break;
        default:
          console.info("Unknown");
          dialogService.notify({
            type: "sc-dialog-error",
            msg: $filter('translate')('ERROR.unknown') + ":" + response.statusText,
            ok: function() {

            }
          });
        }
      };

      var get = function(url, callback) {
        $http({
          method: 'GET',
          url: url
        }).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      var post = function(url, data, callback) {
        $http({
          method: 'POST',
          data: data,
          url: url
        }).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      var delWithData = function(url, data, callback, contentType) {
        var req = {
          method: 'DELETE',
          headers: {},
          data: data,
          url: url
        };
        contentType && (req.headers['Content-Type'] = contentType);
        $http(req).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      var del = function(url, callback) {
        $http({
          method: 'DELETE',
          url: url
        }).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      var put = function(url, data, callback) {
        $http({
          method: 'PUT',
          data: data,
          url: url
        }).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      var upload = function(url, file, callback) {
        var formData = new FormData();
        formData.append('file', file);

        $http.post(url, formData, {
          headers: {
            'Content-Type': undefined
          },
          transformRequest: angular.identity
        }).then(function successCallback(response) {
          // this callback will be called asynchronously
          // when the response is available
          if (response.data && JSON.stringify(response.data).indexOf('<!DOCTYPE html>') !== -1) {
            $window.location.reload();
          } else {
            if (callback) {
              callback(response.data);
            }
          }
        }, errorHandler);
      };

      return {
        get: get,
        post: post,
        put: put,
        del: del,
        delWithData: delWithData,
        upload: upload
      }
    }]);