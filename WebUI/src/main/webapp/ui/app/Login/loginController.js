mainApp.controller('loginController', ['$scope', '$state', 'loggerService', '$location', function($scope, $state, loggerService, $location) {
	$scope.doLogin = function(username, pwd) {
		// login logic : server call etc

		$state.go('overview');
	}

	loggerService.getLogger().info("Logging");
	loggerService.getLogger().info($location.path())
}])