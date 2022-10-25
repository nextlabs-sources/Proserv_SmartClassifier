/*
 * This is a service for general functions
 */

mainApp.factory('generalService', ['configService', function (configService) {

    var booleanValues = [true, false];

    var contains = function (needle) {
        // Per spec, the way to identify NaN is that it
        // is not equal to itself
        var findNaN = needle !== needle;
        var indexOf;

        if (!findNaN && typeof Array.prototype.indexOf === 'function') {
            indexOf = Array.prototype.indexOf;
        } else {
            indexOf = function (needle) {
                var i = -1, index = -1;

                for (i = 0; i < this.length; i++) {
                    var item = this[i];

                    if ((findNaN && item !== item) || item === needle) {
                        index = i;
                        break;
                    }
                }

                return index;
            };
        }

        return indexOf.call(this, needle) > -1;
    };

    var containsObject = function (list, obj) {
        var i;
        for (i = 0; i < list.length; i++) {
            loggerService.getLogger().debug("List Object Id is = " + list[i].id);
            if (list[i].id == obj.id) {
                return true;
            }
        }

        return false;
    };

    var isSubFolder = function (sub, parent) {
        return sub.lastIndexOf(parent, 0) === 0;
    };

    var stringStartWith = function (string, text) {
        return string.lastIndexOf(text, 0) === 0;
    };

    var mapDayOftheWeek = {
        "0": "sun",
        "1": "mon",
        "2": "tue",
        "3": "wed",
        "4": "thu",
        "5": "fri",
        "6": "sat",
        "sun": "0",
        "mon": "1",
        "tue": "2",
        "wed": "3",
        "thu": "4",
        "fri": "5",
        "sat": "6"
    };

    var getDayOfTheWeek = function () {
        var arrayDayOftheWeek = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];
        return arrayDayOftheWeek;
    };

    var getDayOfTheMonth = function () {
        var arrayDayOfTheMonth = [];
        for (var i = 1; i < 31; i++) {
            arrayDayOfTheMonth.push(i);
        }
        arrayDayOfTheMonth.push("L");
        return arrayDayOfTheMonth;
    };

    var getDayOfTheWeekMap = function () {
        return mapDayOftheWeek;
    };

    var getBooleanValuesArray = function () {
        return booleanValues;
    };

    return {
        contains: contains,
        containsObject: containsObject,
        isSubFolder: isSubFolder,
        getBooleanValuesArray: getBooleanValuesArray,
        getDayOfTheWeek: getDayOfTheWeek,
        getDayOfTheMonth: getDayOfTheMonth,
        getDayOfTheWeekMap: getDayOfTheWeekMap,
        stringStartWith: stringStartWith
    }
}]);