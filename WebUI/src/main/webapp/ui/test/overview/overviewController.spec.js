describe("Testing overviewController",
		function() {

			var appController, overviewController, configService, $scope;

			beforeEach(module('mainApp'));

			beforeEach(inject(function($injector) {
				configService = $injector.get('configService');
				configService.configObject['logLevel'] = 0;
			}));

			beforeEach(inject(function($rootScope) {
				$scope = $rootScope.$new();
			}));

			beforeEach(inject(function($rootScope, $controller, $httpBackend,
					$injector) {

				appController = $controller('appController', {
					$scope : $rootScope.$new()
				});

				summaryService = $injector.get('summaryService');

				overviewController = $controller('overviewController', {
					$scope : $scope
				})

				console.log(configService.getUrl("summary.get")
						+ 'documentSummary');
				
				$httpBackend.whenGET(/**//\.html$/).respond(200,'');
				$httpBackend.whenGET(/**//\.json$/).respond(200,'');
				$httpBackend
						.whenPOST(
								configService.getUrl("summary.get")
										+ 'documentSummary').respond({
							"data" : [ {
								"code" : "Total document",
								"value" : "20"
							}, {
								"code" : "Success extraction",
								"value" : "17"
							}, {
								"code" : "Fail extraction",
								"value" : "3"
							}, {
								"code" : "License expiry date",
								"value" : "2030-12-31"
							}, {
								"code" : "Data size",
								"value" : "1024"
							}, {
								"code" : "Used size",
								"value" : "536870912000"
							} ],
							"statusCode" : "1004",
							"message" : "Data loaded successfully."
						});

				$httpBackend.whenPOST(
						configService.getUrl("summary.get") + 'watcherSummary')
						.respond({
							"data" : [ {
								"code" : "Total",
								"value" : "2"
							}, {
								"code" : "Healthy",
								"value" : "1"
							}, {
								"code" : "Warning",
								"value" : "0"
							}, {
								"code" : "Critical",
								"value" : "1"
							} ],
							"criticalHeartBeat" : 30,
							"statusCode" : "1004",
							"message" : "Data loaded successfully."
						});

				$httpBackend.whenPOST(
						configService.getUrl("summary.get")
								+ 'extractorSummary').respond({
					"data" : [ {
						"code" : "Total",
						"value" : "2"
					}, {
						"code" : "Healthy",
						"value" : "1"
					}, {
						"code" : "Warning",
						"value" : "0"
					}, {
						"code" : "Critical",
						"value" : "1"
					} ],
					"criticalHeartBeat" : 30,
					"statusCode" : "1004",
					"message" : "Data loaded successfully."
				});

				$httpBackend.whenPOST(
						configService.getUrl("summary.get")
								+ "ruleEngineSummary").respond({
					"data" : [ {
						"code" : "Total",
						"value" : "2"
					}, {
						"code" : "Healthy",
						"value" : "1"
					}, {
						"code" : "Warning",
						"value" : "0"
					}, {
						"code" : "Critical",
						"value" : "1"
					} ],
					"criticalHeartBeat" : 30,
					"statusCode" : "1004",
					"message" : "Data loaded successfully."
				})

				$httpBackend.flush();
			}));

			it("should extract document information correctly", function() {
				expect($scope.usedSize).toBe(Number(500).toFixed(2));
				expect($scope.processDetails).toEqual([85, 15]);
			});
			
			it("should extract watcher information correctly", function() {
				expect($scope.watcherData).toEqual([1, 0, 1]);
			});
			
			it("should extract extractor information correctly", function() {
				expect($scope.extractorData).toEqual([1, 0, 1]);
			});
			
			it("should extract rule engine information correctly", function() {
				expect($scope.ruleEngineData).toEqual([1, 0, 1]);
			});
			
			it("should call and update the page content when refresh method is called", function() {
				spyOn(summaryService, 'getSummary');
				expect(summaryService.getSummary.calls.count()).toBe(0);
				$scope.refresh();
				expect(summaryService.getSummary.calls.count()).toBe(4);
			})

		});