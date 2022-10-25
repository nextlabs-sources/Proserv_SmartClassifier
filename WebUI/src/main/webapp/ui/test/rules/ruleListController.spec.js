describe(
		"Testing ruleListController",
		function() {

			var appController, configService, ruleService, dialogService, sharedService, $scope, createController, $httpBackend, ruleList;

			beforeEach(module('mainApp'));

			beforeEach(inject(function($rootScope) {
				$scope = $rootScope.$new();
			}));

			beforeEach(inject(function($rootScope, $controller, $injector) {
				window.MainAppConfig['defaultRulePageSize'] = 1;
				configService = $injector.get('configService');

				appController = $controller('appController', {
					$scope : $rootScope.$new()
				});

				createController = function() {
					return $controller('ruleListController', {
						$scope : $scope
					});
				}

				ruleService = $injector.get('ruleService');
				dialogService = $injector.get('dialogService');
				sharedService = $injector.get('sharedService');

				$httpBackend = $injector.get('$httpBackend');

				$httpBackend.whenGET(/**//\.html$/).respond(200, '');
				$httpBackend.whenGET('ui/app/i18n/en.json').respond(200, '');

				$httpBackend.whenGET(configService.getUrl("rule.sortFields"))
						.respond({
							"data" : [ {
								"code" : "modifiedOn",
								"value" : "Last Updated"
							}, {
								"code" : "createdOn",
								"value" : "Created Date"
							}, {
								"code" : "name",
								"value" : "Name"
							}, {
								"code" : "enabled",
								"value" : "Status"
							} ],
							"totalNoOfRecords" : 4,
							"statusCode" : "1004",
							"message" : "Data loaded successfully."
						});

				ruleList = [
						{
							"ruleEngine" : {},
							"name" : "Multiple Rules",
							"description" : "This will contains 4 rules for execute.",
							"enabled" : true,
							"scheduleType" : "D",
							"executionFrequency" : {
								"time" : "0000"
							},
							"effectiveFrom" : 1461055370761,
							"effectiveUntil" : 0,
							"criteriaGroups" : [

							],
							"directories" : [ {
								"displayOrder" : 1,
								"operator" : "AND",
								"criterias" : [ {
									"displayOrder" : 1,
									"dataSection" : "D",
									"operator" : "OR",
									"matchingCondition" : "CONTAINS",
									"value" : "\\\\SCDEV01W12R2\\MySharedFolder\\Demo\\",
									"id" : 1604191600000108,
									"createdOn" : 1461055598293,
									"modifiedOn" : 1461055598293
								} ],
								"id" : 1604191600000108,
								"createdOn" : 1461055598293,
								"modifiedOn" : 1461055598293
							} ],
							"actions" : [
									{
										"actionPlugin" : {
											"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.copy.CopyFile",
											"name" : "Copy",
											"description" : "Copy given file(s) to defined target folder.",
											"params" : [ {
												"displayOrder" : 1,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Destination folder",
												"identifier" : "target",
												"fixedParameter" : false,
												"id" : 1512301601000001,
												"createdOn" : 1460971666463,
												"modifiedOn" : 1460971666463
											} ],
											"id" : 1512300900000001,
											"createdOn" : 1460971666453,
											"modifiedOn" : 1460971666453
										},
										"toleranceLevel" : "S",
										"actionParams" : [ {
											"collections" : false,
											"keyValue" : false,
											"dataType" : "String",
											"label" : "Destination folder",
											"identifier" : "target",
											"values" : [ {
												"value" : "\\\\SCDEV01W12R2\\MySharedFolder\\Copied"
											} ]
										} ],
										"id" : 1604191600000117,
										"createdOn" : 1461055598293,
										"modifiedOn" : 1461055598293
									},
									{
										"actionPlugin" : {
											"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.move.MoveFile",
											"name" : "Move",
											"description" : "Move given file(s) to defined target folder.",
											"params" : [ {
												"displayOrder" : 1,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Destination folder",
												"identifier" : "target",
												"fixedParameter" : false,
												"id" : 1512301606000001,
												"createdOn" : 1460971666470,
												"modifiedOn" : 1460971666470
											} ],
											"id" : 1512300900000006,
											"createdOn" : 1460971666457,
											"modifiedOn" : 1460971666457
										},
										"toleranceLevel" : "F",
										"actionParams" : [ {
											"collections" : false,
											"keyValue" : false,
											"dataType" : "String",
											"label" : "Destination folder",
											"identifier" : "target",
											"values" : [ {
												"value" : "\\\\SCVED01W12R2\\MySharedFolder\\Moved"
											} ]
										} ],
										"id" : 1604191600000115,
										"createdOn" : 1461055598293,
										"modifiedOn" : 1461055598293
									},
									{
										"actionPlugin" : {
											"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.delete.DeleteFile",
											"name" : "Delete",
											"description" : "Delete given file(s) from its location.",
											"id" : 1512300900000003,
											"createdOn" : 1460971666453,
											"modifiedOn" : 1460971666453
										},
										"toleranceLevel" : "S",
										"actionParams" : [

										],
										"id" : 1604191600000114,
										"createdOn" : 1461055598293,
										"modifiedOn" : 1461055598293
									},
									{
										"actionPlugin" : {
											"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.addtag.AddTag",
											"name" : "Classify",
											"description" : "Add specified tag(s) to given file(s).",
											"params" : [ {
												"displayOrder" : 1,
												"paramType" : "T",
												"collections" : true,
												"keyValue" : true,
												"dataType" : "String",
												"label" : "Metadata",
												"identifier" : "tag",
												"fixedParameter" : false,
												"id" : 1512301609000001,
												"createdOn" : 1460971666460,
												"modifiedOn" : 1460971666460
											} ],
											"id" : 1512300900000009,
											"createdOn" : 1460971666453,
											"modifiedOn" : 1460971666453
										},
										"toleranceLevel" : "S",
										"actionParams" : [ {
											"collections" : true,
											"keyValue" : true,
											"dataType" : "String",
											"label" : "Metadata",
											"identifier" : "tag",
											"values" : [ {
												"key" : "Tag2",
												"value" : "Value 2"
											}, {
												"key" : "Tag1",
												"value" : "Value1"
											} ]
										} ],
										"id" : 1604191600000116,
										"createdOn" : 1461055598293,
										"modifiedOn" : 1461055598293
									} ],
							"lastExecutionDate" : 1461081649776,
							"lastExecutionOutcome" : "Partial failed",
							"id" : 1604191600000103,
							"createdOn" : 1461055598293,
							"modifiedOn" : 1461055598296
						},
						{
							"ruleEngine" : {
								"name" : "RuleEngine@SCDEV01W12R2",
								"hostname" : "SCDEV01W12R2",
								"onDemandInterval" : 5,
								"scheduledInterval" : 60,
								"onDemandPoolSize" : 2,
								"scheduledPoolSize" : 4,
								"configLoadedOn" : 1461123610498,
								"configReloadInterval" : 300,
								"id" : 1604191600000104,
								"createdOn" : 1461053602540,
								"modifiedOn" : 1461123610529
							},
							"name" : "Encrypt File",
							"description" : "Test encrypt file",
							"enabled" : true,
							"scheduleType" : "D",
							"executionFrequency" : {
								"time" : "0000"
							},
							"effectiveFrom" : 1461054530849,
							"effectiveUntil" : 0,
							"criteriaGroups" : [

							],
							"directories" : [ {
								"displayOrder" : 1,
								"operator" : "AND",
								"criterias" : [ {
									"displayOrder" : 1,
									"dataSection" : "D",
									"operator" : "OR",
									"matchingCondition" : "CONTAINS",
									"value" : "\\\\SCDEV01W12R2\\MySharedFolder\\Demo\\",
									"id" : 1604191600000105,
									"createdOn" : 1461054575969,
									"modifiedOn" : 1461054575969
								} ],
								"id" : 1604191600000105,
								"createdOn" : 1461054575969,
								"modifiedOn" : 1461054575969
							} ],
							"actions" : [ {
								"actionPlugin" : {
									"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.encrypt.EncryptFile",
									"name" : "Protect",
									"description" : "Encrypt given file(s) with NextLabslibrary.",
									"params" : [
											{
												"displayOrder" : 1,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "Boolean",
												"label" : "Delete original file",
												"identifier" : "delete-original-file",
												"fixedParameter" : false,
												"id" : 1512301605000001,
												"createdOn" : 1460971666463,
												"modifiedOn" : 1460971666463
											},
											{
												"displayOrder" : 2,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Keystore file",
												"identifier" : "keystore-file",
												"fixedParameter" : true,
												"value" : "C:\\RMS Store\\rmskmc-keystore.jks",
												"id" : 1512301605000002,
												"createdOn" : 1460971666463,
												"modifiedOn" : 1461054670282
											},
											{
												"displayOrder" : 3,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Keystore password",
												"identifier" : "keystore-password",
												"fixedParameter" : true,
												"value" : "sa1f78f49e437288039751654ece96ede",
												"id" : 1512301605000003,
												"createdOn" : 1460971666463,
												"modifiedOn" : 1461054670282
											},
											{
												"displayOrder" : 4,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Trust store file",
												"identifier" : "trust-store-file",
												"fixedParameter" : true,
												"value" : "C:\\RMS Store\\rmskmc-truststore.jks",
												"id" : 1512301605000004,
												"createdOn" : 1460971666467,
												"modifiedOn" : 1461054670282
											},
											{
												"displayOrder" : 5,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Truststore password",
												"identifier" : "trust-store-password",
												"fixedParameter" : true,
												"value" : "sa1f78f49e437288039751654ece96ede",
												"id" : 1512301605000005,
												"createdOn" : 1460971666467,
												"modifiedOn" : 1461054670282
											},
											{
												"displayOrder" : 6,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "String",
												"label" : "Hostname",
												"identifier" : "hostname",
												"fixedParameter" : true,
												"value" : "PLM-SCS-W12R2",
												"id" : 1512301605000006,
												"createdOn" : 1460971666467,
												"modifiedOn" : 1461054670282
											},
											{
												"displayOrder" : 7,
												"paramType" : "P",
												"collections" : false,
												"keyValue" : false,
												"dataType" : "Integer",
												"label" : "RMI port number",
												"identifier" : "rmi-port-number",
												"fixedParameter" : true,
												"value" : "1099",
												"id" : 1512301605000007,
												"createdOn" : 1460971666467,
												"modifiedOn" : 1461054670282
											}, {
												"displayOrder" : 9,
												"paramType" : "T",
												"collections" : true,
												"keyValue" : true,
												"dataType" : "String",
												"label" : "Metadata",
												"identifier" : "tag",
												"fixedParameter" : false,
												"id" : 1512301605000009,
												"createdOn" : 1460971666470,
												"modifiedOn" : 1460971666470
											} ],
									"id" : 1512300900000005,
									"createdOn" : 1460971666457,
									"modifiedOn" : 1461054670282
								},
								"toleranceLevel" : "S",
								"actionParams" : [ {
									"collections" : false,
									"keyValue" : false,
									"dataType" : "Boolean",
									"label" : "Delete original file",
									"identifier" : "delete-original-file",
									"values" : [ {
										"value" : "false"
									} ]
								} ],
								"id" : 1604191600000105,
								"createdOn" : 1461054575969,
								"modifiedOn" : 1461054575969
							} ],
							"lastExecutionDate" : 1461081649776,
							"lastExecutionOutcome" : "Partial failed",
							"id" : 1604191600000102,
							"createdOn" : 1461054575969,
							"modifiedOn" : 1461054575969
						},
						{
							"ruleEngine" : {
								"name" : "RuleEngine@SCDEV01W12R2",
								"hostname" : "SCDEV01W12R2",
								"onDemandInterval" : 5,
								"scheduledInterval" : 60,
								"onDemandPoolSize" : 2,
								"scheduledPoolSize" : 4,
								"configLoadedOn" : 1461123610498,
								"configReloadInterval" : 300,
								"id" : 1604191600000104,
								"createdOn" : 1461053602540,
								"modifiedOn" : 1461123610529
							},
							"name" : "Copy File",
							"description" : "Test copy file",
							"enabled" : true,
							"scheduleType" : "D",
							"executionFrequency" : {
								"time" : "0000"
							},
							"effectiveFrom" : 1461048727310,
							"effectiveUntil" : 0,
							"criteriaGroups" : [

							],
							"directories" : [ {
								"displayOrder" : 1,
								"operator" : "AND",
								"criterias" : [ {
									"displayOrder" : 1,
									"dataSection" : "D",
									"operator" : "OR",
									"matchingCondition" : "CONTAINS",
									"value" : "\\\\SCDEV01W12R2\\MySharedFolder\\Demo\\",
									"id" : 1604191600000104,
									"createdOn" : 1461054488403,
									"modifiedOn" : 1461054488403
								} ],
								"id" : 1604191600000104,
								"createdOn" : 1461054488403,
								"modifiedOn" : 1461054488403
							} ],
							"actions" : [ {
								"actionPlugin" : {
									"className" : "com.nextlabs.smartclassifier.plugin.action.sharedfolders.copy.CopyFile",
									"name" : "Copy",
									"description" : "Copy given file(s) to defined target folder.",
									"params" : [ {
										"displayOrder" : 1,
										"paramType" : "P",
										"collections" : false,
										"keyValue" : false,
										"dataType" : "String",
										"label" : "Destination folder",
										"identifier" : "target",
										"fixedParameter" : false,
										"id" : 1512301601000001,
										"createdOn" : 1460971666463,
										"modifiedOn" : 1460971666463
									} ],
									"id" : 1512300900000001,
									"createdOn" : 1460971666453,
									"modifiedOn" : 1460971666453
								},
								"toleranceLevel" : "S",
								"actionParams" : [ {
									"collections" : false,
									"keyValue" : false,
									"dataType" : "String",
									"label" : "Destination folder",
									"identifier" : "target",
									"values" : [ {
										"value" : "\\\\SCDEV01W12R2\\MySharedFolder\\Copied"
									} ]
								} ],
								"id" : 1604191600000104,
								"createdOn" : 1461054488403,
								"modifiedOn" : 1461054488403
							} ],
							"lastExecutionDate" : 1461081649776,
							"lastExecutionOutcome" : "Success",
							"id" : 1604191400000101,
							"createdOn" : 1461054488403,
							"modifiedOn" : 1461054488417
						} ];

				var postData = {
					"sortFields" : [ {
						"field" : "modifiedOn",
						"order" : "desc"
					} ],
					"pageNo" : 1,
					"pageSize" : 1
				};

				$httpBackend.whenPOST(configService.getUrl("rule.list"),
						postData).respond({
					"data" : [ ruleList[0] ],
					"pageNo" : 1,
					"pageSize" : 1,
					"totalNoOfRecords" : 3,
					"statusCode" : "1004",
					"message" : "Data loaded successfully."
				});

				var postDataMore = {
					"sortFields" : [ {
						"field" : "modifiedOn",
						"order" : "desc"
					} ],
					"pageNo" : 2,
					"pageSize" : 1
				};
				
				$httpBackend.whenPOST(configService.getUrl("rule.list"),
						postDataMore).respond({
					"data" : [ ruleList[1] ],
					"pageNo" : 2,
					"pageSize" : 1,
					"totalNoOfRecords" : 3,
					"statusCode" : "1004",
					"message" : "Data loaded successfully."
				});
				
				var postDataFull = {
						"sortFields" : [ {
							"field" : "modifiedOn",
							"order" : "desc"
						} ],
						"pageNo" : 2,
						"pageSize" : 10
					};
				$httpBackend.whenPOST(configService.getUrl("rule.list"),
						postDataFull).respond({
					"data" : ruleList,
					"pageNo" : 2,
					"pageSize" : 10,
					"totalNoOfRecords" : 3,
					"statusCode" : "1004",
					"message" : "Data loaded successfully."
				});

				$httpBackend.whenPOST(configService.getUrl("rule.add"))
						.respond({
							"statusCode" : "1000",
							"message" : "Data saved successfully."
						})
			}));

			it("should get sort fields from service",
					function() {
						$httpBackend.expectGET(configService
								.getUrl("rule.sortFields"));
						var controller = createController();
						$httpBackend.flush();

						expect($scope.sortBy).toEqual({
							"code" : "modifiedOn",
							"value" : "Last Updated"
						})
						expect($scope.sortOrder.value).toBe("desc");
					});

			it("should get rule list with correct request data", function() {
				spyOn(ruleService, 'getRuleList');
				var controller = createController();
				$httpBackend.flush();

				expect(ruleService.getRuleList.calls.count()).toBe(1);

				console.log(ruleService.getRuleList.mostRecentCall);

				expect(ruleService.getRuleList.calls.mostRecent().args[0])
						.toBe(1);
				expect(ruleService.getRuleList.calls.mostRecent().args[1])
						.toEqual([ {
							"field" : "modifiedOn",
							"order" : "desc"
						} ]);
			});

			it("should refresh rule list on sort by changed", function() {
				spyOn(ruleService, 'getRuleList');
				var controller = createController();
				$httpBackend.flush();

				$scope.sortByField({
					"code" : "name",
					"value" : "Name"
				});

				expect(ruleService.getRuleList.calls.mostRecent().args[0])
						.toBe(1);
				expect(ruleService.getRuleList.calls.mostRecent().args[1])
						.toEqual([ {
							"field" : "name",
							"order" : "desc"
						} ]);
			});

			it("should refresh rule list on sort order changed", function() {
				spyOn(ruleService, 'getRuleList');
				var controller = createController();
				$httpBackend.flush();

				$scope.sortByOrder({
					label : "Ascending",
					value : "asc"
				});

				expect(ruleService.getRuleList.calls.mostRecent().args[0])
						.toBe(1);
				expect(ruleService.getRuleList.calls.mostRecent().args[1])
						.toEqual([ {
							"field" : "modifiedOn",
							"order" : "asc"
						} ]);
			});

			it(
					"should get more rule when loadMore method is called",
					function() {
						spyOn(ruleService, 'getRuleList').and.callThrough();
						var controller = createController();
						$httpBackend.flush();

						$httpBackend.expectPOST(configService
								.getUrl("rule.list"));
						$scope.loadMore();
						$httpBackend.flush();

						expect(
								ruleService.getRuleList.calls.mostRecent().args[0])
								.toBe(2);
						expect($scope.rulePageNumber).toBe(2);
						expect($scope.ruleList.length).toBe(2);

					});

			it("should pop up confirm dialog on deleting rule", function() {
				spyOn(dialogService, 'confirm');
				var controller = createController();
				$httpBackend.flush();

				$scope.deleteRule(ruleList[0], {
					stopPropagation : function() {
					}
				});
				expect(dialogService.confirm).toHaveBeenCalled();
			});

			it(
					"should populate rule to sharedService data when preview method is called",
					function() {
						var controller = createController();
						$httpBackend.flush();

						$scope.preview(ruleList[0]);

						expect(sharedService.data.query).toEqual({
							"criteria" : ruleList[0].criteriaGroups,
							"directories" : ruleList[0].directories
						});
					});

			it("should create a new rule when duplicate method is called",
					function() {
						spyOn(ruleService, "createRule");
						var controller = createController();
						$httpBackend.flush();

						$scope.duplicateRule(ruleList[0]);

						expect(ruleService.createRule).toHaveBeenCalled();
					});

			it("should refresh rule list on duplicating rule", function() {
				var controller = createController();
				$httpBackend.flush();

				spyOn(ruleService, "getRuleList").and.callThrough();
				expect(ruleService.getRuleList.calls.count()).toBe(0);
				$scope.duplicateRule(ruleList[0]);
				$httpBackend.flush();

				expect(ruleService.getRuleList.calls.count()).toBe(1);

			});

			describe(
					"should options behave correctly",
					function() {

						beforeEach(inject(function($injector) {
							var controller = createController();
							$httpBackend.flush();
							
							$scope.ruleList.push(ruleList[1]);
						}));

						it("should option closed for each rule", function() {
							angular.forEach($scope.ruleList, function(rule) {
								expect(rule.optionOpen).toBeFalsy();
							});
						});

						it(
								"should open option for one rule if the vertical ellipsis button is clicked",
								function() {
									$scope
											.openOption(
													$scope.ruleList[0],
													!$scope
															.openOption($scope.ruleList[0]));
									expect($scope.ruleList[0].optionOpen)
											.toBeTruthy();
								});

						it(
								"should close option for one rule if it's open already and clicked again",
								function() {
									$scope
											.openOption($scope.ruleList[0],
													false);
									expect(
											$scope
													.openOption($scope.ruleList[0]))
											.toBeFalsy();
									$scope
											.openOption(
													$scope.ruleList[0],
													!$scope
															.openOption($scope.ruleList[0]));
									expect($scope.ruleList[0].optionOpen)
											.toBeTruthy();
									$scope
											.openOption(
													$scope.ruleList[0],
													!$scope
															.openOption($scope.ruleList[0]));
									expect($scope.ruleList[0].optionOpen)
											.toBeFalsy();
								});

						it(
								"should close other rule with open option if one rule is clicked",
								function() {
									$scope
											.openOption($scope.ruleList[0],
													false);
									$scope
											.openOption($scope.ruleList[1],
													false);
									expect($scope.ruleList[0].optionOpen)
											.toBeFalsy();
									expect($scope.ruleList[1].optionOpen)
											.toBeFalsy();
									$scope.openOption($scope.ruleList[0], true);
									$scope.openOption($scope.ruleList[1], true);
									expect($scope.ruleList[0].optionOpen)
											.toBeFalsy();
									expect($scope.ruleList[1].optionOpen)
											.toBeTruthy();
								});
					});

		});