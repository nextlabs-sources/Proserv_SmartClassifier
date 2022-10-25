mainApp.controller('jmsDetailsController', ['$scope', '$state', '$location', 'loggerService', '$window', '$stateParams', '$filter', 'dialogService', 'jmsService', '$anchorScroll',
		function($scope, $state, $location, loggerService, $window, $stateParams, $filter, dialogService, jmsService, $anchorScroll) {
			$scope.$parent.isDetailsPage = true;
			$scope.jmsID = $stateParams.id;
			$scope.jmsTypes = ["Q", "T"];

			// reload jms
			$scope.reloadJMS = function() {

				jmsService.getJMS($scope.jmsID, function(response) {
					$scope.jms = response.data;
					$scope.resetJMS = angular.copy($scope.jms);
					loggerService.getLogger().debug($scope.jms);
					if (!$scope.jms) {
						$scope.detailsFound = false;
					} else {
						$scope.detailsFound = true;
					}
				})
			}

			if ($scope.jmsID == "create") {
				// if creating jms, initialize default values
				$scope.jms = {
					"displayName" : null,
					"description" : "JMS for sending document extraction information to extractor.",
					"type" : "Q",
					"providerURL" : null,
					"initialContextFactory" : null,
					"serviceName" : null,
					"connectionRetryInterval" : 5,
				}
				$scope.detailsFound = true;
				$scope.resetJMS = angular.copy($scope.jms);
				$scope.isCreated = true;
			} else {

				// get from web service
				$scope.reloadJMS();
				$scope.isCreated = false;

			}

			$scope.backToJMSList = function(form) {
				if (!angular.equals($scope.jms, $scope.resetJMS)) {
					dialogService.confirm({
						msg : $filter('translate')('details.back.confirm'),
						ok : function() {
							$state.go("JMS");
						}
					})
				} else {
					$state.go("JMS");
				}
			}

			// scrollTo functionalities
			$scope.scrollTo = function(target) {
				$location.hash(target);
				$anchorScroll();
				// $location.hash(target, '');
				currentTarget = target;
			}

			$scope.discardJMSChanges = function(form) {
				loggerService.getLogger().info("Discarding changes");
				$scope.jms = angular.copy($scope.resetJMS);
				form.$setPristine();
			}

			$scope.changeJMSType = function(jmsType) {
				$scope.jms.type = jmsType;
			}

			$scope.saveJMSChanges = function(form) {
				// to be implemented

				if (form.$invalid) {
					form.$setDirty();
					for ( var field in form) {
						if (field[0] == '$')
							continue;
						// console.log(field);
						form[field].$touched = true;
					}
					return;
				}

				if ($scope.isCreated) {
					// if created, call add web service
					jmsService.createJMS($scope.jms, function(response) {
						$state.go("JMS");
						loggerService.getLogger().debug(response.message);
					})
				} else {
					// call modify web service
					jmsService.modifyJMS($scope.jms, function(response) {
						$scope.resetJMS = angular.copy($scope.jms);
						loggerService.getLogger().debug(response.message);
					})
				}
				form.$setPristine();
			}
		}])