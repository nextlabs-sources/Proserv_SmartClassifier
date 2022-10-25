mainApp.controller('ruleDetailsController', [
    '$scope',
    '$location',
    'loggerService',
    '$filter',
    '$stateParams',
    'sharedService',
    'generalService',
    'dialogService',
    'ruleService',
    'collectionService',
    '$state',
    '$anchorScroll',
    function ($scope, $location, loggerService, $filter, $stateParams, sharedService, generalService, dialogService, ruleService, collectionService, $state, $anchorScroll) {
        $scope.$parent.isDetailsPage = true;
        $scope.ruleID = $stateParams.id;
        $scope.rule = {
            "name": null,
            "scheduleType": "D",
            "displayOrder": 1,
            "repositoryType": "SHARED FOLDER",
            "ruleEngine": null
        };

        $scope.pluginList = [];

        loggerService.getLogger().info("Rule id is " + $scope.ruleID);

        $scope.validity = {};

        // validity variables
        $scope.validFromPopUp = {
            opened: false
        };
        $scope.expiringOnPopUp = {
            opened: false
        };

        $scope.validFromOpen = function () {
            $scope.validFromPopUp.opened = true;
        };

        $scope.expiringOnOpen = function () {
            $scope.expiringOnPopUp.opened = true;
        };

        // schedule variables
        $scope.dayOfTheWeek = generalService.getDayOfTheWeek();
        $scope.dayOfTheMonth = generalService.getDayOfTheMonth();

        $scope.loadSharePointPlugins = function () {
            $scope.sharePointPlugins = [];
            collectionService.getCollection("SharePointPlugin", function (response) {
                $scope.sharePointPlugins = response.data;
                if ($scope.rule && $scope.rule.repositoryType === "SHAREPOINT") {
                    $scope.pluginList = $scope.sharePointPlugins;
                }
            });
        };

        $scope.loadSharedFolderPlugins = function () {
            $scope.sharedFolderPlugins = [];
            collectionService.getCollection("SharedFolderPlugin", function (response) {
                $scope.sharedFolderPlugins = response.data;
                if ($scope.rule && $scope.rule.repositoryType === "SHARED FOLDER") {
                    $scope.pluginList = $scope.sharedFolderPlugins;
                }
            });
        };

        $scope.loadSharedFolderPlugins();
        $scope.loadSharePointPlugins();

        // criteria variables
        ruleService.getMatchingConditions(function (data) {
            $scope.matchingConditions = data;
        });

        ruleService.getMetaDataMatchingConditions(function (data) {
            $scope.metaDataMatchingConditions = data;
        });

        ruleService.getDocumentMatchingConditions(function (data) {
            $scope.documentMatchingConditions = data;
        });

        ruleService.getDataProviders(function (data) {
            $scope.dataProviders = data;
        });

        ruleService.getMetaDataList(function (data) {
            $scope.metaDataList = data;
        });

        $scope.dataSections = ruleService.getDataSections();

        // action variables
        $scope.actionCollapse = {};
        $scope.selectActionDropDownBtn = $filter('translate')('rule.editor.select.action.dropdown');

        // initialize service
        $scope.reloadRule = function () {
            ruleService.getRule($scope.ruleID, function (response) {
                $scope.rule = response.data;
//					loggerService.getLogger().debug($scope.rule);

                if ($scope.rule.repositoryType === "SHAREPOINT") {
                    $scope.pluginList = $scope.sharePointPlugins;
                } else if ($scope.rule.repositoryType === "SHARED FOLDER") {
                    $scope.pluginList = $scope.sharedFolderPlugins;
                }
                // process validity
                $scope.validity.validFrom = new Date($scope.rule.effectiveFrom);
                if (!$scope.rule.effectiveUntil || $scope.rule.effectiveUntil === 0) {
                    $scope.validity.expiringOnOption = "never";
                    $scope.validity.expiringOn = new Date();
                } else {
                    $scope.validity.expiringOnOption = "specific";
                    $scope.validity.expiringOn = new Date($scope.rule.effectiveUntil);
                }

                // process schedule
                $scope.loadSchedule();

                // process criteras
                if ($scope.rule.criteriaGroups) {
                    $scope.loadCriterias();
                }

                // copy initial data to
                // reset object
                $scope.copyResetObjects();

                $scope.detailsFound = $scope.rule;

                // loggerService.getLogger().debug(JSON.stringify($scope.rule));
                loggerService.getLogger().debug($scope.rule.repositoryType);
            })
        };

        $scope.copyResetObjects = function () {
            $scope.resetRule = angular.copy($scope.rule);
            $scope.resetWeeklySchedules = angular.copy($scope.weeklySchedules);
            $scope.resetDailySchedules = angular.copy($scope.dailySchedules);
            $scope.resetMonthlySchedules = angular.copy($scope.monthlySchedules);
            $scope.resetValidity = angular.copy($scope.validity);
        };

        // load schedule variables from object loaded from
        // web service
        $scope.loadSchedule = function () {
            if ($scope.rule.executionFrequency) {
                var date = new Date();
                date.setHours($scope.rule.executionFrequency.time.slice(0, 2));
                date.setMinutes($scope.rule.executionFrequency.time.slice(2, 4));
                if ($scope.rule.scheduleType === "D") {
                    $scope.dailySchedules = [{
                        time: date
                    }];

                    $scope.initializeWeeklyTimeSlot();
                    $scope.initializeMonthlyTimeSlot();
                } else if ($scope.rule.scheduleType === "W") {
                    var days = {
                        "sun": false,
                        "mon": false,
                        "tue": false,
                        "wed": false,
                        "thu": false,
                        "fri": false,
                        "sat": false
                    };

                    angular.forEach($scope.rule.executionFrequency.dayOfWeek, function (day, index) {
                        loggerService.getLogger().debug(generalService.getDayOfTheWeekMap()[day]);
                        days[generalService.getDayOfTheWeekMap()[day]] = true;
                    });

                    $scope.weeklySchedules = [{
                        time: date,
                        days: days
                    }];

                    $scope.initializeDailyTimeSlot();
                    $scope.initializeMonthlyTimeSlot();
                } else {
                    var days = {};
                    for (var i = 1; i < 31; i++) {
                        days[i] = false;
                    }
                    days['L'] = false;
                    angular.forEach($scope.rule.executionFrequency.dayOfMonth, function (day, index) {
                        days[day] = true;
                    });
                    $scope.monthlySchedules = [{
                        time: date,
                        days: days
                    }];

                    $scope.initializeDailyTimeSlot();
                    $scope.initializeWeeklyTimeSlot();
                }
            } else {
                $scope.rule.executionFrequency = {};
                $scope.initializeDailyTimeSlot();
                $scope.initializeWeeklyTimeSlot();
                $scope.initializeMonthlyTimeSlot();
            }
        };

        // initialize an array with one default element for
        // weekly timeslot
        $scope.initializeWeeklyTimeSlot = function () {
            var time = new Date();
            time.setHours(0);
            time.setMinutes(0);
            $scope.weeklySchedules = [{
                days: {
                    "sun": false,
                    "mon": false,
                    "tue": false,
                    "wed": false,
                    "thu": false,
                    "fri": false,
                    "sat": false,
                },
                time: time
            }]
        };

        // initialize an array with one default element for
        // daily timeslot
        $scope.initializeDailyTimeSlot = function () {
            var time = new Date();
            time.setHours(0);
            time.setMinutes(0);
            $scope.dailySchedules = [{
                time: time,
            }]
        };

        // initialize an array with one default element for
        // monthly timeslot
        $scope.initializeMonthlyTimeSlot = function () {
            var time = new Date();
            time.setHours(0);
            time.setMinutes(0);
            var days = {};
            for (var i = 1; i < 31; i++) {
                days[i + ""] = false;
            }
            days['L'] = false;
            $scope.monthlySchedules = [{
                days: days,
                time: time
            }]
        };

        $scope.selectAll = function (monthlySchedule) {
            angular.forEach(monthlySchedule.days, function (value, key) {
                if (key != 'L') {
                    monthlySchedule.days[key] = true;
                }
            });
        };

        $scope.clearAll = function (monthlySchedule) {
            angular.forEach(monthlySchedule.days, function (value, key) {
                monthlySchedule.days[key] = false;
            });
        };

        $scope.getDayOfMonthString = function (days) {
            var returnedString = "";
            var object = [];
            angular.forEach(days, function (value, day) {

                if (value) {
                    if (day === 'L') {
                        object.push($filter('translate')('L'));
                    } else {
                        object.push(day);
                    }
                } else {
                    if (object.length > 2) {
                        returnedString += object[0] + "-" + object[object.length - 1] + ", ";
                    } else {
                        for (var i = 0; i < object.length; i++) {
                            returnedString += object[i] + ", ";
                        }
                    }
                    object.length = 0;
                }
            });
            if (object.length > 0) {
                if (object.length > 2) {
                    returnedString += object[0] + "-" + object[object.length - 1] + ", ";
                } else {
                    for (var i = 0; i < object.length; i++) {
                        returnedString += object[i] + ", ";
                    }
                }
            }
            if (returnedString.length === 0) {
                returnedString = $filter('translate')('month.days.alt');
            } else {
                returnedString = returnedString.slice(0, returnedString.length - 2);
            }
            return returnedString;
        };

        // get daily or weekly or monthly schedule as text
        $scope.getScheduleAsText = function () {
            var returnedString = "";
            if ($scope.rule.scheduleType === 'D') {
                returnedString += $filter('translate')('daily.label') + " " + $filter('translate')('at.label') + " ";
                angular.forEach($scope.dailySchedules, function (slot, index) {
                    if (index != 0) {
                        returnedString += " " + $filter('translate')('and.label') + " ";
                    }

                    returnedString += $scope.parseTimeToString(slot.time, true);
                });
            } else if ($scope.rule.scheduleType === 'W') {
                returnedString += $filter('translate')('weekly.label') + " " + $filter('translate')('on.label') + " ";
                angular.forEach($scope.weeklySchedules, function (slot, index) {
                    if (index != 0) {
                        returnedString += " " + $filter('translate')('and.label') + " ";
                    }

                    var hasDay = false;
                    var dayString = "";
                    angular.forEach(slot.days, function (value, key) {
                        if (value) {
                            hasDay = true;
                            dayString += $filter('translate')(key + ".label") + " ";
                        }
                    });
                    if (!hasDay) {
                        returnedString += "<" + $filter('translate')('week.days.alt') + ">";
                    } else {
                        returnedString += "(" + dayString.slice(0, dayString.length - 1) + ")";
                    }
                    returnedString += " " + $filter('translate')('at.label') + " ";
                    returnedString += $scope.parseTimeToString(slot.time, true);
                })
            } else {
                returnedString += $filter('translate')('monthly.label') + " " + $filter('translate')('on.label') + " ";
                angular.forEach($scope.monthlySchedules, function (slot, index) {
                    if (index != 0) {
                        returnedString += " " + $filter('translate')('and.label') + " ";
                    }

                    var dayString = $scope.getDayOfMonthString(slot.days);
                    if (dayString.length === 0) {
                        returnedString += "<" + $filter('translate')('month.days.alt') + ">";
                    } else {
                        returnedString += "(" + dayString + ")";
                    }
                    returnedString += " " + $filter('translate')('at.label') + " ";
                    returnedString += $scope.parseTimeToString(slot.time, true);
                })
            }
            return returnedString;
        };

        $scope.monthlyScheduleChange = function (monthlySchedule, day, value) {
            loggerService.getLogger().debug(day + " " + value);
            if (day == 'L' && value) {
                for (var i = 1; i < 31; i++) {
                    monthlySchedule.days[i] = false;
                }
            }

            if (day != 'L' && value) {
                monthlySchedule.days['L'] = false;
            }
        };

        $scope.loadRuleEngine = function () {
            $scope.ruleEngineList = [];
            $scope.totalRuleEngines = 0;
            collectionService.getCollection("RuleEngine", function (response) {
                $scope.ruleEngineList = response.data;
                $scope.totalRuleEngines = response.totalNoOfRecords;

                if ($scope.ruleEngineList.length > 0
                    && $scope.rule.ruleEngine == null) {
                    $scope.rule.ruleEngine = $scope.ruleEngineList[0];
                    $scope.resetRule = angular.copy($scope.rule);
                }
            });
        };

        $scope.loadRuleEngine();

        $scope.changeRuleEngine = function (ruleEngine) {
            $scope.rule.ruleEngine = ruleEngine;
        };

        $scope.loadRepositoryTypes = function () {
            $scope.repositoryTypes = [];
            collectionService.getCollection("RepositoryType", function (response) {
                $scope.repositoryTypes = response.data;
            });
        };

        $scope.loadRepositoryTypes();

        if ($scope.ruleID === "create") {
            $scope.validity.validFrom = new Date();
            $scope.validity.expiringOn = new Date();
            $scope.validity.expiringOnOption = "never";
            $scope.initializeWeeklyTimeSlot();
            $scope.initializeDailyTimeSlot();
            $scope.initializeMonthlyTimeSlot();

            $scope.copyResetObjects();
            $scope.detailsFound = true;
            $scope.isCreated = true;
        } else {
            // get from web service
            $scope.reloadRule();
            $scope.isCreated = false;
        }

        $scope.loadCriterias = function () {
        };

        // add criteria groups
        $scope.addCriteriaGroup = function () {
            if (!$scope.rule.criteriaGroups) {
                $scope.rule.criteriaGroups = [];
            }

            $scope.rule.criteriaGroups.push({
                "criterias": [{
                    "displayOrder": 1,
                    "dataSection": "A",
                    "fieldName": "",
                    "matchingCondition": "CONTAINS",
                    "value": null,
                    "newCri": true
                }],
                "displayOrder": $scope.rule.criteriaGroups.length + 1,
                "operator": "AND"
            })
        };

        // add criteria
        $scope.addCriteria = function (cg) {
            if (!cg.criterias) {
                cg.criterias = [];
            }

            cg.criterias.push({
                "displayOrder": cg.criterias.length + 1,
                "dataSection": "A",
                "fieldName": "",
                "matchingCondition": "CONTAINS",
                "value": null,
                "newCri": true
            });
        };

        // remove criteria group
        $scope.removeCriteriaGroup = function (index) {
            $scope.rule.criteriaGroups.splice(index, 1);
        };

        // remove criteria
        $scope.removeCriteria = function (cg, index, cindex) {
            cg.criterias.splice(cindex, 1);
            if (cg.criterias.length === 0) {
                $scope.rule.criteriaGroups.splice(index, 1);
            }
        };

        $scope.removeCriteriaGroup = function (index) {
            $scope.rule.criteriaGroups.splice(index, 1);
        };

        $scope.changeDataSection = function (cri, section) {
            cri.dataSection = section;
            cri.matchingCondition = "CONTAINS";
            cri.value = null;
            if (section !== 'M') {
                cri.fieldName = "";
                cri.field = "";
            }
        };

        $scope.changeMatchingCondition = function (cri, condition) {
            cri.matchingCondition = condition.code;
            cri.value = null;
        };

        $scope.changeMetaMatchingCondition = function (cri, condition) {
            cri.matchingCondition = condition.code;
            cri.value = null;
            if (cri.fieldName) {
                if ($scope.isStringCriteria(cri)) {
                    cri.fieldName = cri.field;
                } else if ($scope.isDateCriteria(cri)) {
                    cri.date = new Date();
                    cri.dateOpened = false;
                    cri.openDate = function () {
                        cri.dateOpened = true;
                    };
                    cri.value = cri.date.getTime();
                    cri.fieldName = cri.field;
                } else if ($scope.isNumberCriteria(cri)) {
                    cri.fieldName = cri.field;
                } else {
                    cri.fieldName = cri.field;
                }
            } else {
                if ($scope.isDateCriteria(cri)) {
                    cri.date = new Date();
                    cri.value = cri.date.getTime();
                    cri.dateOpened = false;
                    cri.openDate = function () {
                        cri.dateOpened = true;
                    }
                }
            }
        };

        $scope.changeDocumentMatchingCondition = function (cri, condition) {
            cri.matchingCondition = condition.code;
        };

        $scope.updateDateCriteria = function (cri) {
            if (cri.date) {
                cri.value = cri.date.getTime();
            }
        };

        $scope.updateMetaField = function (cri) {
            if (cri.field) {
                cri.fieldName = cri.field;
            } else {
                cri.fieldName = null;
            }
        };

        $scope.changeMetadataField = function (cri, meta) {
            cri.field = meta.value;
            cri.fieldName = meta.value;
            cri.fieldCode = meta.code;

            if (meta.uiDataType === "DATE") {
                cri.matchingCondition = "DATE_EQUALS";
                cri.date = new Date();
                cri.value = cri.date.getTime();
                cri.dateOpened = false;
                cri.openDate = function () {
                    cri.dateOpened = true;
                };
                cri.value = cri.date.getTime();
            } else if (meta.uiDataType === "INTEGER") {
                cri.matchingCondition = "NUM_EQUALS";
                cri.value = null;
            } else {
                cri.matchingCondition = "CONTAINS";
                cri.value = null;
            }
        };

        $scope.resetMetaMatchingCondition = function (cri) {
            cri.fieldCode = undefined;
        };

        $scope.hideMetaMatchingCondition = function (cri, condition) {
            if (!cri.fieldCode) {
                return false;
            }

            if (generalService.stringStartWith(condition, 'DATE')) {
                if (generalService.stringStartWith(cri.matchingCondition, 'DATE')) {
                    return false;
                }
            } else if (generalService.stringStartWith(condition, "NUM")) {
                if (generalService.stringStartWith(cri.matchingCondition, 'NUM')) {
                    return false;
                }
            } else if (condition === 'NOT' || condition === 'CONTAINS' || condition === 'NOT_CONTAINS' || condition === 'MUST_CONTAINS') {
                if (cri.matchingCondition === 'NOT' || cri.matchingCondition === 'CONTAINS' || cri.matchingCondition === 'NOT_CONTAINS' || cri.matchingCondition === 'MUST_CONTAINS') {
                    return false;
                }
            }

            return true;
        };

        $scope.isMetaData = function (cri, cg) {
            var result;
            if (cri.dataSection === 'M') {
                cri.isMetaData = true;
                if (cri.fieldName && !cri.field) {
                    if (cri.fieldDisplayName) {
                        cri.field = cri.fieldDisplayName;
                        cri.fieldCode = cri.fieldName;
                        cri.fieldName = cri.fieldDisplayName;

                    } else {
                        cri.field = cri.fieldName;
                    }
                }
                result = true;
            } else {
                cri.isMetaData = false;
                result = false;
            }

            if (!cri.metaLoaded && !cri.newCri) {
                cri.metaLoaded = true;
                $scope.resetRule = angular.copy($scope.rule);
            }

            return result;
        };

        $scope.hasMetaData = function (cg) {
            for (var i = 0; i < cg.criterias.length; i++) {
                if (cg.criterias[i].isMetaData) {
                    return true;
                }
            }
            return false;
        };

        $scope.isDateCriteria = function (cri) {
            var isDate = (generalService.stringStartWith(cri.matchingCondition, 'DATE'));
            if (isDate) {
                if (!cri.date) {
                    cri.dateOpened = false;
                    cri.openDate = function () {
                        cri.dateOpened = true;
                    };

                    if (cri.value) {
                        cri.date = new Date(Number(cri.value));
                    } else {
                        cri.date = new Date();
                        cri.value = cri.date.getTime();
                    }
                }
            }

            if (!cri.dateLoaded && !cri.newCri) {
                cri.dateLoaded = true;
                $scope.resetRule = angular.copy($scope.rule);
            }

            return isDate;
        };

        $scope.isNumberCriteria = function (cri) {
            return (generalService.stringStartWith(cri.matchingCondition, 'NUM'));
        };

        $scope.isStringCriteria = function (cri) {
            return (cri.matchingCondition === 'NOT' || cri.matchingCondition === 'NOT_CONTAINS' || cri.matchingCondition === 'MUST_CONTAINS' || cri.matchingCondition === 'CONTAINS');
        };

        $scope.checkDataProvider = function (value, viewValue) {
            loggerService.getLogger().debug(value + " " + viewValue);
            if (value && viewValue) {
                return generalService.stringStartWith(value, viewValue);
            } else {
                return false;
            }
        };

        $scope.changeDataProvider = function (cri, dp) {
            cri.value = dp.suggestion;
        };

        // go back to list
        $scope.backToRuleList = function (form) {
            if (!$scope.dataPristine()) {
                dialogService.confirm({
                    msg: $filter('translate')('details.back.confirm'),
                    ok: function () {
                        $state.go("rules");
                    }
                })
            } else {
                $state.go("rules");
            }

        };

        $scope.dataPristine = function () {
            if (!angular.equals($scope.rule, $scope.resetRule)) {
                return false;
            }

            if (!angular.equals($scope.weeklySchedules, $scope.resetWeeklySchedules)) {
                return false;
            }

            if (!angular.equals($scope.dailySchedules, $scope.resetDailySchedules)) {
                return false;
            }

            if (!angular.equals($scope.monthlySchedules, $scope.resetMonthlySchedules)) {
                return false;
            }

            if (!angular.equals($scope.validity, $scope.resetValidity)) {
                return false;
            }

            return true;
        };

        $scope.scrollTo = function (target) {
            $location.hash(target);
            $anchorScroll();
            /* $location.hash('resourceInfo', ''); */
            currentTarget = target;
        };

        $scope.discardRuleChanges = function (form) {
            loggerService.getLogger().info("Discarding changes");
            $scope.rule = angular.copy($scope.resetRule);
            $scope.weeklySchedules = angular.copy($scope.resetWeeklySchedules);
            $scope.dailySchedules = angular.copy($scope.resetDailySchedules);
            $scope.monthlySchedules = angular.copy($scope.resetMonthlySchedules);
            $scope.validity = angular.copy($scope.resetValidity);
            form.$setPristine();
        };

        $scope.execute = function () {
            if($scope.resetRule.ruleEngine) {
            	ruleService.executeRule($scope.rule.id, function (data) {
                    // ignore
                });
            } else {
            	dialogService.notify({
                    type: "sc-dialog-error",
                    msg: $filter('translate')('rule.editor.invalid.ruleEngine.validation'),
                    ok: function () {
                    	// ignore
                    }
                });
            }
        };

        $scope.preview = function () {
            if (!$scope.rule.criteriaGroups) {
                $scope.rule.criteriaGroups = [];
            }
            if (!$scope.rule.directories) {
                $scope.rule.directories = [{}];
            }
            sharedService.data.query = {
                "repositoryType": angular.copy($scope.rule.repositoryType),
                "criteria": angular.copy($scope.rule.criteriaGroups),
                "directories": angular.copy($scope.rule.directories)
            };

            $state.go("indexDatabaseQuery", {
                id: $scope.rule.id
            });
        };

        $scope.saveRuleChanges = function (form) {
            // to be implemented

            if (form.$invalid) {
                form.$setDirty();
                for (var field in form) {
                    if (field[0] == '$')
                        continue;
                    // console.log(field);
                    form[field].$touched = true;
                    form[field].$dirty = true;
                }
                return;
            }

            $scope.rule.effectiveFrom = $scope.validity.validFrom.getTime();

            if ($scope.validity.expiringOnOption === 'specific') {
                $scope.rule.effectiveUntil = $scope.validity.expiringOn.getTime();
            } else {
                $scope.rule.effectiveUntil = 0;
                $scope.validity.expiringOn = new Date();
            }

            // process validity
            if ($scope.validity.expiringOn != 0 && $scope.validity.validFrom.getTime() > $scope.validity.expiringOn.getTime()) {
                dialogService.notify({
                    type: "sc-dialog-error",
                    msg: $filter('translate')('rule.editor.invalid.expiration.validation'),
                    ok: function () {

                    }
                });
                return;
            }

            // process timeslots
            // currently backend supports only one
            // element - getting the first element and put
            // directly to the executionFrequency
            loggerService.getLogger().debug($scope.rule.scheduleType);
            if ($scope.rule.scheduleType === 'D') {
                var element = {
                    "time": $scope.parseTimeToString($scope.dailySchedules[0].time, false)
                };
                $scope.rule.executionFrequency = element;

            } else if ($scope.rule.scheduleType === "W") {
                var dayArray = [];
                angular.forEach($scope.weeklySchedules[0].days, function (value, key) {
                    if (value === true) {
                        dayArray.push(generalService.getDayOfTheWeekMap()[key]);
                        loggerService.getLogger().debug(key);
                    }
                });
                var element = {
                    "dayOfWeek": dayArray,
                    "time": $scope.parseTimeToString($scope.weeklySchedules[0].time, false)

                };

                $scope.rule.executionFrequency = element;
            } else {
                var dayArray = [];
                angular.forEach($scope.monthlySchedules[0].days, function (value, key) {
                    if (value === true) {
                        dayArray.push(key);
                    }
                });
                var element = {

                    "dayOfMonth": dayArray,
                    "time": $scope.parseTimeToString($scope.monthlySchedules[0].time, false)
                };

                $scope.rule.executionFrequency = element;
            }

            // process directories
            angular.forEach($scope.rule.directories, function (group, index) {
                angular.forEach(group.criterias, function (dir, index) {
                    $scope.checkDirectory(dir);
                });
            });

            if ($scope.isCreated) {
                // if created, call add web service
                ruleService.createRule($scope.rule, function (response) {
                    $state.go("rules");
                })
            } else {
                // call modify web service
                ruleService.modifyRule($scope.rule, function (response) {
                    $scope.copyResetObjects();
                })
            }
            form.$setPristine();
        };

        $scope.changeRepositoryType = function (repositoryType) {
            if (($scope.rule.actions && $scope.rule.actions.length) ||
                ($scope.rule.criteriaGroups && $scope.rule.criteriaGroups.length) ||
                ($scope.rule.directories && $scope.rule.directories.length)) {
                dialogService.confirm({
                    msg: $filter('translate')('rule.change.repo.confirm'),
                    ok: function () {
                        // reset criteria, action and directories
                        $scope.rule.actions = [];
                        $scope.rule.criteriaGroups = [];
                        $scope.rule.directories = [];
                        $scope.rule.repositoryType = repositoryType;
                        if (repositoryType === "SHAREPOINT") {
                            $scope.pluginList = $scope.sharePointPlugins;
                        } else if (repositoryType === "SHARED FOLDER") {
                            $scope.pluginList = $scope.sharedFolderPlugins;
                        }
                    }
                })
            } else {
                $scope.rule.repositoryType = repositoryType;
                if (repositoryType === "SHAREPOINT") {
                    $scope.pluginList = $scope.sharePointPlugins;
                } else if (repositoryType === "SHARED FOLDER") {
                    $scope.pluginList = $scope.sharedFolderPlugins;
                }
            }

        };

        $scope.checkBoolean = function (param) {
            var result = false;

            if (param.dataType === 'Boolean') {
//					loggerService.getLogger().debug(param);
                if (typeof param.values[0].value === "string" || param.values[0].value instanceof String) {
                    if (param.values[0].value === "true") {
                        param.values[0].value = true;
                    } else {
                        param.values[0].value = false;
                    }
                }
                result = true;
            }

            if (!param.paramLoaded && !param.newParam) {
                param.paramLoaded = true;
                $scope.resetRule = angular.copy($scope.rule);
            }

            return result;
        };

        $scope.getActionPluginName = function (actionPlugin) {
            return actionPlugin.name.replace(/_/g, " ");
        };

        /*			$scope.loadPlugins = function() {
         $scope.pluginList = [];
         $scope.totalPlugins = 0;
         collectionService.getCollection("ActionPlugin", function(response) {
         $scope.pluginList = response.data;
         $scope.totalPlugins = response.totalNoOfRecords;
         });
         };

         $scope.loadPlugins();*/

        $scope.addAction = function (selectPlugin) {
            var action = {
                "actionPlugin": {
                    "id": selectPlugin.id,
                    "name": selectPlugin.name,
                    "params": selectPlugin.params
                },
                "toleranceLevel": "S",
                "actionParams": [],
            };

            angular.forEach(selectPlugin.params, function (param, index) {
                var actionParams = {
                    "identifier": param.identifier,
                    "values": [],
                    "dataType": param.dataType,
                    "collections": param.collections,
                    "keyValue": param.keyValue,
                    "label": param.label,
                    "newParam": true

                };

                if (!param.collections) {
                    if (!param.keyValue) {
                        if (param.dataType == "String" || param.dataType === "Character" || param.dataType === "Email") {
                            actionParams.values.push({
                                "value": ""
                            });
                        }
                        if (param.dataType === "Number" || param.dataType === "Integer") {
                            actionParams.values.push({
                                "value": 0
                            });
                        }
                        if (param.dataType === "Boolean") {
                            actionParams.values.push({
                                "value": true
                            });
                        }
                    } else {
                        actionParams.values.push({
                            "key": "",
                            "value": ""
                        });
                    }
                } else {
                    if (param.keyValue) {
                        actionParams.values.push({
                            "key": "",
                            "value": ""
                        });
                    }
                }
                action.actionParams.push(actionParams);
            });

            if (!$scope.rule.actions) {
                $scope.rule.actions = [];
            }
            $scope.rule.actions.push(action);
            $scope.actionCollapse[$scope.rule.actions.length - 1] = false;
            $scope.selectActionDropDownBtn = $filter('translate')('rule.editor.select.action.dropdown');
        };

        $scope.removeActionParamElement = function (param, index) {
            param.values.splice(index, 1);
        };

        $scope.addActionParamElement = function (param) {
            param.values.push({
                "key": "",
                "value": ""
            });
        };

        $scope.saveAction = function (index, form) {
            var isValid = true;
            for (var field in form) {
                if (field.toString().lastIndexOf('actionParam-' + index, 0) === 0) {
                    if (form[field].$invalid) {
                        loggerService.getLogger().info("invalid field " + field);
                        isValid = false;
                        form[field].$touched = true;
                        form[field].$dirty = true;
                    }
                }
            }

            if (!isValid) {
                return;
            }

            $scope.actionCollapse[index] = true;
        };

        $scope.editAction = function (index) {
            $scope.actionCollapse[index] = false;
        };

        $scope.removeAction = function (index) {
            $scope.rule.actions.splice(index, 1);
            $scope.actionCollapse = {};
            for (var i = 0; i < $scope.rule.actions.length; i++) {
                $scope.actionCollapse[i] = true;
            }
        };

// directories function
        $scope.addDirectoryGroup = function () {
            if (!$scope.rule.directories || $scope.rule.directories.length === 0) {
                $scope.rule.directories = [{
                    "operator": "AND",
                    "criterias": [{
                        "dataSection": "D",
                        "matchingCondition": "CONTAINS",
                        "value": ""
                    }]
                }];
            } else {
                $scope.rule.directories.push({
                    "operator": "AND",
                    "criterias": [{
                        "dataSection": "D",
                        "matchingCondition": "CONTAINS",
                        "value": ""
                    }]
                });
            }
        };

        $scope.removeDirectoryGroup = function (index) {
            $scope.rule.directories.splice(index, 1);
        };

        $scope.addDirectory = function (directoryGroup) {
            if (!directoryGroup.criterias) {
                directoryGroup.criterias = [];
            }

            directoryGroup.criterias.push({
                "dataSection": "D",
                "matchingCondition": "CONTAINS",
                "value": ""
            });
        };

        $scope.removeDirectory = function (directoryGroup, index, dindex) {
            directoryGroup.criterias.splice(dindex, 1);

            if (directoryGroup.criterias.length === 0) {
                $scope.rule.directories.splice(index, 1);
            }
        };

        $scope.changeDirectoryMatchingCondition = function (directory, matchingCondition) {
            directory.matchingCondition = matchingCondition.code;
        };

        $scope.checkDirectory = function (dir) {
            if (!dir.value) {
                return;
            }

            if (dir.value.slice(dir.value.length - 2, dir.value.length) != ("\\*")
                && (dir.value.slice(dir.value.length - 1, dir.value.length) != ("\\") && dir.value.slice(dir.value.length - 1, dir.value.length) != ("/"))
                && dir.value.slice(dir.value.length - 2, dir.value.length) != ("/*")) {
                if($scope.rule.repositoryType === "SHAREPOINT") {
                    dir.value += "/";
                } else {
                    dir.value += "\\";
                }
            }
        };
    }])
;