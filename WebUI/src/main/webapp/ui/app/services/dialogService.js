mainApp.factory('dialogService', [
    '$uibModal',
    'toaster',
    function($uibModal, toaster) {
      var confirm = function(parameter) {
        var title = parameter.title;
        var msg = parameter.msg;
        var ok = parameter.ok;
        var cancel = parameter.cancel;
        $uibModal.open({
          animation: true,
          // template: msg,
          templateUrl: 'ui/app/templates/dialog-confirm.html',
          controller: ['$uibModalInstance', '$scope', function($uibModalInstance, $scope) {
            $scope.title = title;
            $scope.msg = msg;
            $scope.ok = function() {
              $uibModalInstance.close(false);
              ok && ok();
            }, $scope.cancel = function() {
              $uibModalInstance.close(false);
              cancel && cancel();
            };
          }]
        }).result.catch(function (resp) {
          if (['cancel', 'backdrop click', 'escape key press'].indexOf(resp) === -1) throw resp;
        });
      }

      var notify = function(parameter) {
        var type = parameter.type;
        var title = parameter.title;
        var msg = parameter.msg;
        var ok = parameter.ok;
        $uibModal.open({
          animation: true,
          // template: msg,
          templateUrl: 'ui/app/templates/dialog-notify.html',
          controller: ['$uibModalInstance', '$scope', function($uibModalInstance, $scope) {
            $scope.type = type;
            $scope.title = title;
            $scope.msg = msg;
            $scope.ok = function() {
              $uibModalInstance.close(false);
              ok && ok();
            }
          }]
        }).result.catch(function (resp) {
          if (['cancel', 'backdrop click', 'escape key press'].indexOf(resp) === -1) throw resp;
        });
      }

      var input = function(parameter) {
        var title = parameter.title;
        var templateUrl = parameter.templateUrl;
        var msg = parameter.msg;
        var ok = parameter.ok;
        var cancel = parameter.cancel;
        $uibModal.open({
          animation: true,
          templateUrl: templateUrl,
          controller: ['$uibModalInstance', '$scope', function($uibModalInstance, $scope) {
            if (parameter.initialParam) {
              $scope.returnParam = parameter.initialParam;
            } else {
              $scope.returnParam = [];
            }
            $scope.title = title;
            $scope.msg = msg;
            $scope.ok = function(form) {
              if (form.$invalid) {
                form.$setDirty();
                for ( var field in form) {
                  if (field[0] == '$') continue;
                  form[field].$touched = true;
                  form[field].$dirty = true;
                }
                return;
              }

              $uibModalInstance.close(false);
              ok && ok($scope.returnParam);
            }, $scope.cancel = function() {
              $uibModalInstance.close(false);
              cancel && cancel();
            };
          }]
        }).result.catch(function (resp) {
          if (['cancel', 'backdrop click', 'escape key press'].indexOf(resp) === -1) throw resp;
        });
      }

      var notifyWithoutBlocking = function(parameter) {
        var timeout = parameter.timeout || null;
        var strObj = JSON.stringify(parameter);
        toaster.pop('success', null, "{template: 'ui/app/templates/toaster-notify.html', data:" + strObj + "}",
                timeout, 'templateWithData');
      }

      return {
        confirm: confirm,
        notify: notify,
        input: input,
        notifyWithoutBlocking: notifyWithoutBlocking
      }
    }]);
