mainApp.controller('licenseController', ['$scope', 'loggerService', '$filter', 'summaryService', function ($scope, loggerService, $filter, summaryService) {

    $scope.$parent.isDetailsPage = false;

    $scope.getLicenseInformation = function () {

        summaryService.getSummary("document", function (response) {
            $scope.documentSummary = response.data;
            $scope.licenseFound = true;
            $scope.loadLicenseInfo();

        });
    };

    $scope.getLicenseInformation();

    $scope.loadLicenseInfo = function () {

        for (var i = 0; i < $scope.documentSummary.length; i++) {
            if ($scope.documentSummary[i].value) {
                if ($scope.documentSummary[i].code == "Data size") {
                    if ($scope.documentSummary[i].value == "UNLIMITED") {
                        $scope.dataSize = $scope.documentSummary[i].value;
                    } else {
                        $scope.dataSize = Number($scope.documentSummary[i].value);
                    }
                    continue;
                }

                if ($scope.documentSummary[i].code == "License Validity") {
                    console.log($scope.documentSummary[i].value);
                    $scope.licenseValidity = $scope.documentSummary[i].value;
                    continue;
                }

                if ($scope.documentSummary[i].code == "License expiry date") {
                    if ($scope.documentSummary[i].value == -1) {
                        $scope.expiryDate = "NEVER EXPIRES"
                    } else {
                        $scope.expiryDate = new Date($scope.documentSummary[i].value);
                        loggerService.getLogger().debug(JSON.stringify($scope.expiryDate));
                        continue;
                    }
                }

                if ($scope.documentSummary[i].code == "Used size") {
                    $scope.usedSize = Number($scope.documentSummary[i].value) / 1024 / 1024 / 1024;
                    $scope.usedSize = $scope.usedSize.toFixed(2);
                    loggerService.getLogger().debug($scope.usedSize);

                }
            }
        }

        if ($scope.expiryDate == undefined || $scope.expiryDate == null || $scope.dataSize == undefined || $scope.usedSize == undefined) {

            $scope.licenseFound = false;

        }

    };

    $scope.getLicenseRemainingDays = function () {
        var oneDay = 24 * 60 * 60 * 1000;
        var today = new Date();
        if ($scope.expiryDate) {
            return Math.round(($scope.expiryDate - today.getTime()) / (oneDay));
        } else {
            return 0;
        }
    };

    $scope.getRemaining = function () {
        if ($scope.dataSize == "UNLIMITED") {
            return "UNLIMITED";
        }
        return ($scope.dataSize - $scope.usedSize).toFixed(2);
    };

    $scope.getRemainingClass = function () {
        if ($scope.getUsedSizeStyle() === 'success') {
            return "sc-green";
        }

        if ($scope.getUsedSizeStyle() === 'danger') {
            return "sc-red";
        }

        if ($scope.getUsedSizeStyle() === 'warning') {
            return "sc-orange";
        }
    };

    $scope.getExpiryClass = function () {

        if ($scope.expiryDate == "NEVER EXPIRES") return "sc-green";

        if ($scope.getLicenseRemainingDays() >= 180) {
            return "sc-green";
        }

        if ($scope.getLicenseRemainingDays() <= 0) {
            return "sc-red";
        }

        if ($scope.getLicenseRemainingDays() < 180) {
            return "sc-orange";
        }
    };

    $scope.getUsedSizeStyle = function () {
        if ($scope.dataSize == "UNLIMITED") {
            return "success";
        } else {
            if ($scope.usedSize / $scope.dataSize < 0.5) {
                return "success";
            }

            if ($scope.usedSize / $scope.dataSize < 0.75) {
                return "warning";
            }

            if ($scope.usedSize / $scope.dataSize <= 1) {
                return "danger";
            }

            if ($scope.usedSize / $scope.dataSize > 1) {
                return "danger";
            }
        }
    };

    $scope.hasExpiryDate = function () {
        loggerService.getLogger().debug($scope.expiryDate);
        return $scope.expiryDate != "NEVER EXPIRES";
    };

}]);