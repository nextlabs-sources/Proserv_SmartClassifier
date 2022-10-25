var mainApp = angular.module('mainApp', ['ui.router', 'ui.bootstrap', 'ui.numeric', 'uiSwitch', 'ngSanitize',
    'pascalprecht.translate', 'ngAnimate', 'ngMessages', 'ngMaterial', 'ui.utils.masks', 'chart.js', 'toaster']);

mainApp.config(['$stateProvider', '$urlRouterProvider', '$translateProvider', '$mdThemingProvider', 'ChartJsProvider',
    function($stateProvider, $urlRouterProvider, $translateProvider, $mdThemingProvider, ChartJsProvider) {

      $mdThemingProvider.theme('default').primaryPalette('blue');

      ChartJsProvider.setOptions({
        colors: ['#5CB85C', '#F0AD4E', '#D9534F', '#222222', '#FDB45C', '#949FB1', '#4D5360']
      });

      $urlRouterProvider.otherwise('/Overview');

      // remove the trailing slash at the end of the url if exists
      $urlRouterProvider.rule(function($injector, $location) {

        var path = $location.path();
        var hasTrailingSlash = path[path.length - 1] === '/';

        if (hasTrailingSlash) {

          // if last charcter is a slash, return the same url
          // without the slash
          var newPath = path.substr(0, path.length - 1);
          return newPath;
        }

      });

      $stateProvider.state('changePassword', {
        url: '/User',
        templateUrl: 'ui/app/User/changePassword.html',
        controller: 'authenticationController'
      }).state('overview', {
        url: '/Overview',
        templateUrl: 'ui/app/Overview/overview.html',
        controller: 'overviewController'
      }).state('rules', {
        url: '/Rules',
        templateUrl: 'ui/app/Rules/ruleList.html',
        controller: 'ruleListController'
      }).state('ruleDetails', {
        url: '/Rules/{id}',
        templateUrl: 'ui/app/Rules/ruleDetails.html',
        controller: 'ruleDetailsController'
      }).state('scservices', {
        url: '/Services/{type}',
        templateUrl: 'ui/app/SCServices/serviceList.html',
        controller: 'servicesListController'
      }).state('serviceDetails', {
        url: '/Services/{type}/{id}',
        templateUrl: 'ui/app/SCServices/serviceDetails.html',
        controller: 'serviceDetailsController'
      }).state('indexDatabaseQuery', {
        url: '/IndexDatabaseQuery/{id}',
        templateUrl: 'ui/app/IndexDatabaseQuery/indexDatabaseQuery.html',
        controller: 'indexDatabaseQueryController'
      }).state('indexDatabaseQueryTemp', {
        url: '/IndexDatabaseQuery',
        templateUrl: 'ui/app/IndexDatabaseQuery/indexDatabaseQuery.html',
        controller: 'indexDatabaseQueryController'
      }).state('indexDatabaseQueryResult', {
        url: "/IndexDatabaseQueryResult/{id}",
        templateUrl: "ui/app/IndexDatabaseQuery/indexDatabaseQueryResult.html",
        controller: "indexDatabaseQueryResultController"
      }).state('Plugins', {
        url: '/Settings/Plugins',
        templateUrl: 'ui/app/Settings/Plugins/pluginList.html',
        controller: 'pluginListController'
      }).state('PluginDetails', {
        url: '/Settings/Plugins/{id}',
        templateUrl: 'ui/app/Settings/Plugins/pluginDetails.html',
        controller: 'pluginDetailsController'
      }).state('JMS', {
        url: '/Settings/MessagingService',
        templateUrl: 'ui/app/Settings/JMS/jmsList.html',
        controller: 'jmsListController'
      }).state('JMSDetails', {
        url: '/Settings/MessagingService/{id}',
        templateUrl: 'ui/app/Settings/JMS/jmsDetails.html',
        controller: 'jmsDetailsController'
      }).state('login', {
        url: '/Login',
        templateUrl: 'ui/app/Login/login.html',
        controller: 'loginController'
      }).state('ExecutionWindow', {
        url: '/Settings/ExecutionWindow',
        templateUrl: 'ui/app/Settings/ExecutionWindow/executionWindowList.html',
        controller: 'executionWindowListController'
      }).state('ExecutionWindowDetails', {
        url: '/Settings/ExecutionWindow/{id}',
        templateUrl: 'ui/app/Settings/ExecutionWindow/executionWindowDetails.html',
        controller: 'executionWindowDetailsController'
      }).state('RepositoryCredentials', {
        url: '/Settings/RepositoryCredentials',
        templateUrl: 'ui/app/Settings/RepositoryCredentials/repoCredentialsList.html',
        controller: 'repoCredentialsListController'
      }).state('RepositoryCredentialsDetails', {
        url: '/Settings/RepositoryCredentials/{id}',
        templateUrl: 'ui/app/Settings/RepositoryCredentials/repoCredentialsDetails.html',
        controller: 'repoCredentialsDetailsController'
      }).state('License', {
        url: '/Settings/License',
        templateUrl: 'ui/app/Settings/License/license.html',
        controller: 'licenseController'
      }).state('GeneralSettings', {
        url: '/Settings/GeneralSettings',
        templateUrl: 'ui/app/Settings/General/generalSettings.html',
        controller: 'generalSettingsController'
      }).state('adHocReports', {
        url: '/Report/AdHocReports',
        templateUrl: 'ui/app/Report/AdHoc/adHocReportList.html',
        controller: 'adHocReportListController'
      }).state('adHocReportDetails', {
        url: '/Report/AdHocReportDetails/{id}',
        templateUrl: 'ui/app/Report/AdHoc/adHocReportDetails.html',
        controller: 'adHocReportDetailsController'
      }).state('adHocReportResult', {
        url: '/Report/AdHocReportResult/{id}',
        templateUrl: 'ui/app/Report/AdHoc/adHocReportResult.html',
        controller: 'adHocReportResultController'
      }).state('loginConfigurations', {
        url: '/Administration/LoginConfigurations',
        templateUrl: 'ui/app/Administration/LoginConfiguration/loginConfigurationList.html',
        controller: 'loginConfigurationListController'
      }).state('loginConfigurationDetails', {
        url: '/Administration/LoginConfigurations/{id}',
        templateUrl: 'ui/app/Administration/LoginConfiguration/loginConfigurationDetails.html',
        controller: 'loginConfigurationDetailsController'
      }).state('users', {
        url: '/Administration/Users',
        templateUrl: 'ui/app/Administration/User/userList.html',
        controller: 'userListController'
      }).state('internalUserDetails', {
        url: '/Administration/InternalUsers/{id}',
        templateUrl: 'ui/app/Administration/User/internalUserDetails.html',
        controller: 'internalUserDetailsController'
      }).state('importExternalUser', {
        url: '/Administration/ExternalUsers',
        templateUrl: 'ui/app/Administration/User/externalUserDetails.html',
        controller: 'externalUserDetailsController'
      })

      $translateProvider.useStaticFilesLoader({
        prefix: 'ui/app/i18n/',
        suffix: '.json'
      });
      $translateProvider.preferredLanguage('en');
      // Enable escaping of HTML
      $translateProvider.useSanitizeValueStrategy('sanitizeParameters');

    }]);

mainApp.filter('num', function() {
  return function(input) {
    return parseInt(input, 10);
  }
});
