mainApp.controller('appController', [
    '$scope',
    'networkService',
    'configService',
    'loggerService',
    '$location',
    '$templateCache',
    'sharedService',
    '$filter',
    '$state',
    'authenticationService',
    function($scope, networkService, configService, loggerService, $location, $templateCache, sharedService, $filter,
            $state, authenticationService) {

      configService.setConfig(MainAppConfig);

      $scope.tabs = [{
        title: 'TAB.Overview',
        url: 'overview',
        finalUrl: 'Overview',
        icon: "Dashboard_P.svg",
        requireAdmin: false
      }, {
        title: 'TAB.Rules',
        url: 'rules',
        finalUrl: 'Rules',
        icon: "Rules_P.svg",
        requireAdmin: false
      }, {
        title: 'TAB.SCServices',
        url: 'scservices',
        icon: "Components_P.svg",
        requireAdmin: false,
        children: [{
          title: 'TAB.SCServices.Watchers',
          url: 'scservices({type:"watcher"})',
          finalUrl: 'Services/watcher',
          icon: "FileWatcher_P.svg"
        }, {
          title: 'TAB.SCServices.Extractors',
          url: 'scservices({type:"extractor"})',
          finalUrl: 'Services/extractor',
          icon: "ContentExtractor_P.svg"
        }, {
          title: 'TAB.SCServices.RuleEngine',
          url: 'scservices({type:"ruleEngine"})',
          finalUrl: 'Services/ruleEngine',
          icon: "RuleEngine_P.svg"
        }]
      }, {
        title: 'TAB.IndexDatabaseQuery',
        url: 'indexDatabaseQuery({id:"custom"})',
        finalUrl: "IndexDatabaseQuery",
        icon: "IndexQuery_P.svg",
        requireAdmin: false
      }, {
        title: 'TAB.Settings',
        url: 'settings',
        icon: "Settings_P.svg",
        requireAdmin: false,
        children: [{
          title: 'TAB.Settings.General',
          url: 'GeneralSettings',
          finalUrl: 'Settings/General',
          icon: "SettingsGeneral_P.svg"
        }, {
          title: 'TAB.Settings.ExecutionWindow',
          url: 'ExecutionWindow',
          finalUrl: 'Settings/ExecutionWindow',
          icon: "ExecutionWindow_P.svg"
        }, {
          title: 'TAB.Settings.Plugins',
          url: 'Plugins',
          finalUrl: 'Settings/Plugins',
          icon: "Plugins_P.svg"
        }, {
          title: 'TAB.Settings.JMS',
          url: 'JMS',
          finalUrl: 'Settings/MessagingService',
          icon: "MessagingServices_P.svg"
        }, {
          title: 'TAB.Settings.RepositoryCredentials',
          url: 'RepositoryCredentials',
          finalUrl: 'Settings/RepositoryCredentials',
          icon: "RepositoryCredentials_P.svg"
        }, {
          title: 'TAB.Settings.License',
          url: 'License',
          finalUrl: 'Settings/License',
          icon: "License_P.svg"
        }]
      }, {
        title: 'TAB.Report',
        url: 'report',
        icon: 'Report_P.svg',
        requireAdmin: false,
        children: [{
          title: 'TAB.Report.AdHocReport',
          url: 'adHocReportDetails({id:"custom"})',
          finalUrl: 'Report/AdHocReportDetails',
          icon: 'ReportAdHoc_P.svg'
        }]
      }, {
        title: 'TAB.Administration',
        url: 'administration',
        icon: 'Administration_P.svg',
        requireAdmin: true,
        children: [{
          title: 'TAB.Administration.LoginConfigurations',
          url: 'loginConfigurations',
          finalUrl: 'Administration/LoginConfigurations',
          icon: 'LoginConfigurations_P.svg'
        }, {
          title: 'TAB.Administration.Users',
          url: 'users',
          finalUrl: 'Administration/Users',
          icon: 'Users_P.svg'
        }]
      }];

      var url = $location.url();
      if (url.indexOf('/') == 0) url = url.substring(1);
      if (url[url.length - 1] == '/') url = url.substring(0, url.length - 1);

      var findAndSet = function(parent, tab) {
        if (!tab.children) {
          if (tab.finalUrl && url.indexOf(tab.finalUrl) == 0 || tab.url && url.indexOf(tab.url) == 0) {
            if (parent) parent.expanded = true;
            $scope.currentTab = tab.url;
          }
        } else {
          tab.expanded = false;
          angular.forEach(tab.children, function(childTab) {
            if (findAndSet(tab, childTab)) {
              tab.expanded = true
            }
          })
        }
      }

      for (var i = 0; i < $scope.tabs.length; i++) {
        findAndSet(undefined, $scope.tabs[i]);
      }
      /*
       * angular.forEach($scope.tabs, function(tab) { findAndSet(undefined, tab)
       * });
       */

      $scope.onClickTab = function(tab) {
        authenticationService.getUserInfo(function(response) {
          $scope.currentTab = tab.url;
        });
      }

      $scope.isActiveTab = function(tabUrl) {
        return $scope.currentTab == tabUrl
      }

      $scope.$on('$locationChangeStart', function(next, current) {
        url = current.substring(current.indexOf('#') + 2)
        angular.forEach($scope.tabs, function(tab) {
          findAndSet(undefined, tab)
        });
      })

      $scope.isDetailsPage = false;

      $scope.getMenuClass = function() {
        if ($scope.isDetailsPage) {
          return "hide";
        } else {
          return "col-xs-4 col-sm-4 col-md-4"
        }
      }

      $scope.getMainContentClass = function() {
        if ($scope.isDetailsPage) {
          return "col-xs-12 col-sm-12 col-md-12";
        } else {
          return "col-xs-8 col-sm-8 col-md-8"
        }
      }

      $scope.getTooltipMessage = function(field) {
        var message = "";
        if (!field) { return ""; }
        ;
        if (field.$valid) {
          return '';
        } else {
          if (field.$error.minlength) {
            message += $filter('translate')('component.minlength.validation') + "; ";
          }
          if (field.$error.maxlength) {
            message += $filter('translate')('component.maxlength.validation') + "; ";
          }
          if (field.$error.email) {
            message += $filter('translate')('component.email.validation') + "; ";
          }
          if (field.$error.required) {
            message += $filter('translate')('component.required.validation') + "; ";
          }
          message = message.slice(0, message.length - 2);
          return message;
        }
      }

      $scope.parseTimeFromString = function(timeString) {
        if (!timeString) { return ""; }
        var date = new Date();
        date.setHours(parseInt(timeString.slice(0, 2)));
        date.setMinutes(parseInt(timeString.slice(2, 4)));
        return date;
      }

      $scope.parseTimeToString = function(date, withSeparator) {
        if (!date) { return ""; }
        var hour = date.getHours();
        var hourString = hour.toString();
        var min = date.getMinutes();
        var minString = min.toString();
        if (hour < 10) {
          hourString = "0" + hourString;
        }

        if (min < 10) {
          minString = "0" + minString;
        }
        if (withSeparator) {
          return hourString + ":" + minString;
        } else {
          return hourString + minString;
        }
      }

      $scope.abs = function(number) {
        return Math.abs(number);
      }

      $scope.doughnutChartOption = {
        cutoutPercentage: 85,
        animationEasing: "easeOutQuad"

      }

      $scope.pieChartOption = {
        animationEasing: "easeOutQuad"
      }

      $scope.getDateFormat = function() {
        loggerService.getLogger().debug("The date format is  = " + configService.configObject['date.format']);
        return configService.configObject['date.format']
      }

      $scope.serviceChartColors = ['#5CB85C', '#F0AD4E', '#D9534F', '#222222', '#FDB45C', '#949FB1', '#4D5360'];

      $scope.processChartColors = ['#1F4392', '#444444'];

      $scope.getDateFromLong = function(long) {
        return new Date(long);
      }

      $scope.goHome = function() {
        loggerService.getLogger().debug("AppController.js : Inside goHome()");
        $state.go('overview');
      }

      loggerService.getLogger().debug("Log level is " + configService.configObject.logLevel);

    }]);
