angular.module('templates-main', ['ui/app/IndexDatabaseQuery/indexDatabaseQuery.html', 'ui/app/IndexDatabaseQuery/indexDatabaseQueryResult.html', 'ui/app/Login/login.html', 'ui/app/Overview/overview.html', 'ui/app/Rules/partials/ruleoption-template.html', 'ui/app/Rules/ruleDetails.html', 'ui/app/Rules/ruleList.html', 'ui/app/SCServices/partials/extractorTemplate.html', 'ui/app/SCServices/partials/ruleEngineTemplate.html', 'ui/app/SCServices/partials/serviceoption-template.html', 'ui/app/SCServices/partials/watcherTemplate.html', 'ui/app/SCServices/serviceDetails.html', 'ui/app/SCServices/serviceList.html', 'ui/app/Settings/ExecutionWindow/executionWindowDetails.html', 'ui/app/Settings/ExecutionWindow/executionWindowList.html', 'ui/app/Settings/ExecutionWindow/partials/executionwindowoption-template.html', 'ui/app/Settings/General/generalSettings.html', 'ui/app/Settings/JMS/jmsDetails.html', 'ui/app/Settings/JMS/jmsList.html', 'ui/app/Settings/JMS/partials/jmsoption-template.html', 'ui/app/Settings/License/license.html', 'ui/app/Settings/Plugins/partials/pluginoption-template.html', 'ui/app/Settings/Plugins/pluginDetails.html', 'ui/app/Settings/Plugins/pluginList.html', 'ui/app/templates/dialog-confirm.html', 'ui/app/templates/dialog-notify.html', 'ui/app/templates/menu.html', 'ui/app/templates/multiParam.html']);

angular.module("ui/app/IndexDatabaseQuery/indexDatabaseQuery.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/IndexDatabaseQuery/indexDatabaseQuery.html",
    "<div class=\"sc-resource\" style=\"height: 100%\">\n" +
    "	<form id=\"idqForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"idqForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-page-title-left\">\n" +
    "				<div\n" +
    "					class=\"sc-vertical-middle-div-inverse sc-details-page-title-right\">{{'idq.title'|\n" +
    "					translate}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-btn-execute\"\n" +
    "					data-ng-if=\"ruleID !=='custom'\" data-ng-click=\"backToRule()\"><i\n" +
    "					class=\"fa fa-arrow-left\" data-ng-class=\"\"></i>\n" +
    "					{{\"back.to.rule.button\" | translate}} </a> <a\n" +
    "					class=\"btn btn-default sc-btn-execute\"\n" +
    "					data-ng-click=\"querySolr(idqForm)\">{{\"execute.button\" |\n" +
    "					translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-idq-panel sc-details-page-editor-div\">\n" +
    "\n" +
    "				<div id=\"criterias\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"idq.editor.criterias.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label data-ng-if=\"query.criteria && query.criteria.length > 0\">{{\"idq.editor.look.for.label\"\n" +
    "							| translate}}</label> <label\n" +
    "							data-ng-if=\"!query.criteria || query.criteria.length === 0\">{{\"idq.editor.look.for.alt\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-criteria-group-div\"\n" +
    "							data-ng-repeat=\"(index,cg) in query.criteria\">\n" +
    "							<i class=\"fa fa-times-circle-o sc-component-del-icon\"\n" +
    "								style=\"transform: translate(50%, -50%)\"\n" +
    "								data-ng-click=\"removeCriteriaGroup(index)\"> </i>\n" +
    "							<div class=\"sc-criteria-group-sub-div\">\n" +
    "								<table class=\"sc-criteria-table\">\n" +
    "									<tr>\n" +
    "										<td width=\"5%\" rowspan=\"{{cg.criterias.length + 1}}\"\n" +
    "											style=\"border-right: 1px solid #CCCCCC\"\n" +
    "											data-ng-if=\"cg.criterias.length  > 1\"><span>{{'or.label'\n" +
    "												| translate}}</span></td>\n" +
    "									</tr>\n" +
    "									<tr data-ng-repeat=\"(cindex,cri) in cg.criterias\">\n" +
    "\n" +
    "										<td width=\"20%\"><div class=\"btn-group btn-block\"\n" +
    "												data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\">{{\"data.section.\"\n" +
    "														+ cri.dataSection + \".label\" | translate}}</span> <span\n" +
    "														class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\" data-ng-repeat=\"ds in dataSections\"\n" +
    "														data-ng-if=\"ds != 'D'\"\n" +
    "														data-ng-click=\"changeDataSection(cri, ds)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{\"data.section.\" + ds\n" +
    "														+ \".label\" | translate}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"20%\" data-ng-if=\"!isMetaData(cri, cg)\"><div\n" +
    "												class=\"btn-group btn-block\" data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\">{{\"matching.condition.\"\n" +
    "														+ cri.matchingCondition | translate}}</span> <span\n" +
    "														class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\"\n" +
    "														data-ng-repeat=\"mc in matchingConditions\"\n" +
    "														data-ng-click=\"changeMatchingCondition(cri, mc)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{\"matching.condition.\"\n" +
    "														+ mc.code | translate}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"23%\" data-ng-if=\"isMetaData(cri, cg)\"><input\n" +
    "											class=\"sc-input-dropdown-input sc-details-page-editor-input-placeholder sc-details-page-common-input\"\n" +
    "											data-ng-model=\"cri.field\"\n" +
    "											data-ng-keypress=\"resetMetaMatchingCondition(cri)\"\n" +
    "											name=\"{{'criteriaField-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': idqForm['criteriaField-' + index + '' + cindex].$dirty && idqForm['criteriaField-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(idqForm['criteriaField-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"50\" ng-maxlength=\"50\"\n" +
    "											data-ng-change=\"updateMetaField(cri)\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[idqForm['criteriaField-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.field.place.holder' | translate}}\">\n" +
    "											<div class=\"sc-input-dropdown-dropdown btn-group\"\n" +
    "												data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-input-dropdown-menu\"\n" +
    "													role=\"menu\" aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\" data-ng-repeat=\"meta in metaDataList\"\n" +
    "														data-ng-click=\"changeMetadataField(cri, meta)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{meta.value}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"19%\" data-ng-if=\"isMetaData(cri, cg)\"><div\n" +
    "												class=\"btn-group btn-block\" data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\"\n" +
    "														ng-bind-html=\"'\n" +
    "														meta.matching.condition.'\n" +
    "														+cri.matchingCondition | translate\"></span>\n" +
    "													<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\"\n" +
    "														ng-bind-html=\"'\n" +
    "														meta.matching.condition.'\n" +
    "														+ mc.code | translate\"\n" +
    "														data-ng-if=\"!hideMetaMatchingCondition(cri, mc.code)\"\n" +
    "														data-ng-repeat=\"mc in metaDataMatchingConditions\"\n" +
    "														data-ng-click=\"changeMetaMatchingCondition(cri, mc)\"\n" +
    "														class=\"sc-clickable-dropdown\"></li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "\n" +
    "										<td width=\"50%\" data-ng-if=\"!isMetaData(cri, cg)\"\n" +
    "											colspan=\"{{hasMetaData(cg)?2:1}}\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											data-ng-change=\"checkDataProvider(cri)\"\n" +
    "											uib-typeahead=\"dp.suggestion for dp in dataProviders | filter:{suggestion:$viewValue}:checkDataProvider | limitTo:10\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': idqForm['criteriaValue-' + index + '' + cindex].$dirty && idqForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(idqForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[idqForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder'| translate}}\"></td>\n" +
    "\n" +
    "										<td width=\"28%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isStringCriteria(cri)\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											data-ng-change=\"checkDataProvider(cri)\"\n" +
    "											uib-typeahead=\"dp.suggestion for dp in dataProviders | filter:{suggestion:$viewValue}:checkDataProvider | limitTo:10\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': idqForm['criteriaValue-' + index + '' + cindex].$dirty && idqForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(idqForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[idqForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder'| translate}}\"></td>\n" +
    "\n" +
    "										<td width=\"28%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && cri.matchingCondition ==='BETWEEN'\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': idqForm['criteriaValue-' + index + '' + cindex].$dirty && idqForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(idqForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[idqForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder' | translate}}\"></td>\n" +
    "\n" +
    "\n" +
    "										<td width=\"28%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isNumberCriteria(cri)\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\" ui-hide-group-sep\n" +
    "											ui-negative-number\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': idqForm['criteriaValue-' + index + '' + cindex].$dirty && idqForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(idqForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[idqForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder' | translate}}\"\n" +
    "											ui-number-mask=\"0\"></td>\n" +
    "\n" +
    "										<td width=\"28%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isDateCriteria(cri)\">\n" +
    "											<div class=\"sc-date-picker\">\n" +
    "												<span class=\"input-group\"> <input type=\"text\"\n" +
    "													class=\"form-control\"\n" +
    "													name=\"{{'criteriaValue-' + index + '' + cindex}}\"\n" +
    "													uib-datepicker-popup=\"dd-MMMM-yyyy\" ng-model=\"cri.date\"\n" +
    "													data-ng-change=\"updateDateCriteria(cri)\"\n" +
    "													on-open-focus=\"true\" is-open=\"cri.dateOpened\" ng-required\n" +
    "													show-button-bar=\"true\"\n" +
    "													close-text=\"{{'close.label'|translate}}\"\n" +
    "													current-text=\"{{'today.label'|translate}}\"\n" +
    "													clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "													class=\"input-group-btn\">\n" +
    "														<button type=\"button\" class=\"btn btn-default\"\n" +
    "															data-ng-click=\"cri.openDate()\">\n" +
    "															<i class=\"fa fa-calendar\"></i>\n" +
    "														</button>\n" +
    "												</span>\n" +
    "												</span>\n" +
    "											</div>\n" +
    "										</td>\n" +
    "										<td width=\"5%\"><div\n" +
    "												class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "												data-ng-click=\"removeCriteria(cg, index, cindex)\"></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "\n" +
    "								<a data-ng-click=\"addCriteria(cg)\" class=\"btn btn-default\"\n" +
    "									style=\"margin-right: 1%; margin-top: 1%; margin-bottom: 1%\">\n" +
    "									<i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'idq.editor.add.criteria.button'\n" +
    "									| translate}}\n" +
    "								</a>\n" +
    "							</div>\n" +
    "							<div class=\"sc-rule-criteria-and\"\n" +
    "								data-ng-show=\"index != query.criteria.length - 1\">{{'and.label'|\n" +
    "								translate}}</div>\n" +
    "						</div>\n" +
    "						<a data-ng-click=\"addCriteriaGroup()\" class=\"btn btn-default\"\n" +
    "							data-ng-if=\"!query.criteria || query.criteria.length  === 0\"\n" +
    "							style=\"margin-right: 2%; margin-top: 2%\"> <i\n" +
    "							class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'idq.editor.add.criteria.group.button.first'\n" +
    "							| translate}}\n" +
    "						</a> <a data-ng-click=\"addCriteriaGroup()\" class=\"btn btn-default\"\n" +
    "							data-ng-if=\"query.criteria.length > 0\" style=\"margin-top: 2%\">\n" +
    "							<i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'idq.editor.add.criteria.group.button'\n" +
    "							| translate}}\n" +
    "						</a>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "				<div id=\"directories\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"idq.editor.directories.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label\n" +
    "							data-ng-if=\"query.directories && query.directories.length > 0 && query.directories[0].criterias && query.directories[0].criterias.length > 0\">{{\"idq.editor.directories.context\"\n" +
    "							| translate}}</label> <label\n" +
    "							data-ng-if=\"!query.directories || query.directories.length === 0 ||   !query.directories[0].criterias || query.directories[0].criterias.length === 0\">{{\"idq.editor.directories.alt\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-rule-directories\">\n" +
    "							<table>\n" +
    "								<tr\n" +
    "									data-ng-repeat=\"(index, dir) in query.directories[0].criterias\">\n" +
    "									<!-- <td width=\"10%\"><span>{{'browse.label' |\n" +
    "											translate}}</span> -->\n" +
    "									</td>\n" +
    "									<td width=\"95%\"><input\n" +
    "										data-ng-class=\"{'has-error': idqForm['directory0' + index].$dirty && idqForm['directory0' + index].$invalid}\"\n" +
    "										class=\"sc-details-page-editor-input-placeholder\n" +
    "									sc-details-page-editor-data-full sc-details-page-common-input\"\n" +
    "										placeholder=\"{{'rule.editor.directory.place.holder' |\n" +
    "									translate}}\"\n" +
    "										data-ng-model=\"dir.value\" name=\"{{'directory0' + index}}\"\n" +
    "										required max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "										uib-tooltip=\"{{getTooltipMessage(idqForm['directory0' + index])}}\"\n" +
    "										max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "										tooltip-trigger=\"mouseenter\" tooltip-placement=\"bottom\">\n" +
    "									<td width=\"5%\"><div\n" +
    "											class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"removeDirectory(index)\"></div></td>\n" +
    "								</tr>\n" +
    "							</table>\n" +
    "						</div>\n" +
    "						<a data-ng-click=\"addDirectory()\" class=\"btn btn-default\"\n" +
    "							style=\"margin-top: 2%\"> <i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{\"idq.directory.new.button\"\n" +
    "							| translate}}\n" +
    "						</a>\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/IndexDatabaseQuery/indexDatabaseQueryResult.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/IndexDatabaseQuery/indexDatabaseQueryResult.html",
    "<div class=\"sc-resource\" style=\"height: 100%\">\n" +
    "	<form id=\"idqForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"idqForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\" data-ng-click=\"editQuery()\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-page-title-left\">\n" +
    "				<div class=\"sc-vertical-middle-div-inverse\">{{'idq.result.title'|\n" +
    "					translate}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-btn-export\" data-ng-hide=\"true\"\n" +
    "					data-ng-click=\"querySolr(idqForm)\">{{\"export.button\" |\n" +
    "					translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-details-page-summary-div\">\n" +
    "				<a href=\"\">\n" +
    "					<div class=\"sc-details-page-summary-row\">\n" +
    "						<label> {{\"idq.summary.look.for.criteria.label\" |\n" +
    "							translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span ng-show=\"!query.criteria || query.criteria.length === 0\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'rule.summary.criterias.alt'\n" +
    "								| translate}}> </span> <span\n" +
    "								data-ng-repeat=\"(index, cg) in query.criteria\"\n" +
    "								ng-show=\"query.criteria && query.criteria.length > 0\"\n" +
    "								class=\"sc-details-page-summary-content\"><span\n" +
    "								ng-bind-html=\"'(' | translate\"></span><span\n" +
    "								data-ng-repeat=\"(cindex, cri) in cg.criterias\"><span\n" +
    "									data-ng-if=\"cri.dataSection !== 'M'\">{{\"data.section.\" +\n" +
    "										cri.dataSection + \".label\" | translate}}</span><span\n" +
    "									class=\"sc-rule-summary-mc\" data-ng-if=\"cri.dataSection !== 'M'\">&nbsp;{{'matching.condition.'\n" +
    "										+ cri.matchingCondition | translate}}&nbsp;</span><span\n" +
    "									data-ng-if=\"cri.dataSection !== 'M'\">{{cri.value}}</span> <span\n" +
    "									data-ng-if=\"cri.dataSection === 'M'\">{{cri.field}}&nbsp;</span><span\n" +
    "									data-ng-if=\"cri.dataSection === 'M'\"\n" +
    "									ng-bind-html=\"\n" +
    "										'meta.matching.condition.' + cri.matchingCondition |\n" +
    "										translate\"\n" +
    "									class=\"sc-rule-summary-mc\"></span> <span\n" +
    "									data-ng-if=\"cri.dataSection === 'M'\">{{isDateCriteria(cri)?(cri.value\n" +
    "										| date:'dd/MM/yyyy'):cri.value}}</span><label\n" +
    "									data-ng-if=\"cindex !== cg.criterias.length - 1\">\n" +
    "										&nbsp;{{'or.label' | translate}}&nbsp;</label></span><span\n" +
    "								ng-bind-html=\"' )'\"></span></br> <label\n" +
    "								data-ng-if=\"index !== query.criteria.length - 1\">\n" +
    "									{{'and.label'| translate}}&nbsp;</label> </span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('directories')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('schedule')}\">\n" +
    "						<label> {{\"rule.summary.directories.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span\n" +
    "								data-ng-show=\"!query.directories || query.directories.length === 0  || !query.directories[0].criterias || query.directories[0].criterias.length === 0\"\n" +
    "								class=\"sc-details-page-summary-content\">{{'idq.summary.directories.alt'\n" +
    "								| translate}} </span> <span\n" +
    "								data-ng-show=\"query.directories && query.directories.length > 0 && query.directories[0].criterias && query.directories[0].criterias.length > 0\"\n" +
    "								data-ng-repeat=\"(index, dir) in query.directories[0].criterias\"><span\n" +
    "								data-ng-show=\"dir.value && dir.value.length > 0\">{{dir.value}}</br></span></span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a>\n" +
    "\n" +
    "				<div class=\"sc-rule-summary-btn-div\">\n" +
    "					<a class=\"btn btn-default sc-btn-preview-rul\"\n" +
    "						data-ng-click=\"editQuery()\">{{\"editQuery.button\" | translate}}\n" +
    "					</a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-div sc-idq-result-page\">\n" +
    "\n" +
    "				<div data-ng-if=\"resultList.length\" class=\"sc-operation\">\n" +
    "					<div class=\"sc-inline\" style=\"width: 30%\">\n" +
    "						<table width=\"100%\">\n" +
    "							<tr>\n" +
    "								<td width=\"50%\"><span class=\"sc-grey-text-on-table\">{{\"idq.total.result\"\n" +
    "										| translate}}</span></td>\n" +
    "								<td width=\"50%\"><span class=\"sc-idq-total-result\">{{total}}</span></td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "					<div id=\"sc-sort-by\" class=\"sc-inline\">\n" +
    "						<div class=\"sc-inline sc-sort-by-title\">{{\"sort.by.label\"|translate}}</div>\n" +
    "						<div\n" +
    "							class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "							uib-dropdown>\n" +
    "							<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "								data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "								<span class=\"sc-dropdown-btn-label\">{{ sortBy.value |\n" +
    "									translate}}</span> <span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "							</button>\n" +
    "							<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "								aria-labelledby=\"\">\n" +
    "								<li role=\"menuitem\" data-ng-repeat=\"s in sortByFields\"\n" +
    "									data-ng-click=\"sortByField(s)\" class=\"sc-clickable-dropdown\">{{s.value\n" +
    "									| translate}}</li>\n" +
    "							</ul>\n" +
    "						</div>\n" +
    "						<div class=\"sc-inline sc-sort-by-title\">{{\"sort.order.label\"|translate}}</div>\n" +
    "						<div\n" +
    "							class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "							uib-dropdown>\n" +
    "							<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "								data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "								<span class=\"sc-dropdown-btn-label\">{{sortOrder.label}}</span> <span\n" +
    "									class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "							</button>\n" +
    "							<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "								aria-labelledby=\"\">\n" +
    "								<li role=\"menuitem\" data-ng-repeat=\"s in sortOrders\"\n" +
    "									data-ng-click=\"sortByOrder(s)\" class=\"sc-clickable-dropdown\">{{s.label}}</li>\n" +
    "							</ul>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<div data-ng-if=\"resultList.length\">\n" +
    "					<table class=\"sc-list-table table table-hover\">\n" +
    "						<thead>\n" +
    "						</thead>\n" +
    "						<tbody>\n" +
    "							<tr data-ng-repeat=\"result in resultList\">\n" +
    "								<td class=\"sc-result-details-on-table\" style=\"width: 100%\">\n" +
    "									<div class=\"sc-inline-block-on-table\" style=\"width: 100%\">\n" +
    "										<p class=\"sc-sm-margin sc-bold-text-on-table\">{{result.documentName}}</p>\n" +
    "										<p class=\"sc-sm-margin\">\n" +
    "											<span class=\"sc-inline\">{{result.directory}}</span>\n" +
    "										</p>\n" +
    "										<table style=\"width:70%\">\n" +
    "											<tr>\n" +
    "												<td width=\"33%\">\n" +
    "													<p class=\"sc-sm-margin\">\n" +
    "														<span class=\"sc-grey-text-on-table sc-inline\">{{'idq.result.author.label'\n" +
    "															| translate}}:&nbsp;</span><span class=\"sc-inline\">{{result.author}}</span>\n" +
    "													</p>\n" +
    "												</td>\n" +
    "												<td width=\"33%\">\n" +
    "													<p class=\"sc-grey-text-on-table sc-inline\">{{'idq.result.created.date.label'\n" +
    "														| translate}}:</p>\n" +
    "													<p class=\"sc-inline\" data-ng-bind=\"result.createdOn | date\"\n" +
    "														data-ng-if=\"result.createdOn != 0\"></p>\n" +
    "												</td>\n" +
    "												<td width=\"33%\">\n" +
    "													<p class=\"sc-grey-text-on-table sc-inline\">{{'idq.result.modified.date.label'\n" +
    "														| translate}}:</p>\n" +
    "													<p class=\"sc-inline\"\n" +
    "														data-ng-bind=\"result.modifiedOn | date\"\n" +
    "														data-ng-if=\"result.createdOn != 0\"></p>\n" +
    "												</td>\n" +
    "											</tr>\n" +
    "										</table>\n" +
    "									</div>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "						</tbody>\n" +
    "					</table>\n" +
    "\n" +
    "					<div data-ng-show=\"resultList.length< total\">\n" +
    "						<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "							data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "							translate}}</button>\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "				<div data-ng-if=\"!resultList.length\"\n" +
    "					class=\"sc-empty-resource-list-box\">\n" +
    "					<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "				</div>\n" +
    "\n" +
    "\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/Login/login.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Login/login.html",
    "<div class=\"sc-login-container\">\n" +
    "	<div class=\"sc-login-left-panel\">\n" +
    "		<div style=\"padding: 50px 0 0 50px\"> \n" +
    "			<div class=\"logo\"></div>\n" +
    "		</div>\n" +
    "		<div class=\"welcome-msg\">{{'login.welcome' | translate}}</div>\n" +
    "	</div>	\n" +
    "	<div class=\"sc-login-right-panel\">\n" +
    "		<div class=\"wrapper\"> \n" +
    "			<div class=\"title\"> Login to your account</div>\n" +
    "			<div  class=\"login-box\">\n" +
    "			  	\n" +
    "			  	<input type=\"text\" name=\"username\" placeholder=\"Username\">\n" +
    "			<br>\n" +
    "			<br>\n" +
    "			<input type=\"text\" name=\"password\" placeholder=\"Password\">\n" +
    "			<br>\n" +
    "			<div style=\"padding: 40px 0 40px 0\">\n" +
    "				<span class=\"sc-search-include-subpolicy\">\n" +
    "					<switch class=\"sc-search-switch-include-sub\"  data-ng-model=\"policy.effects\"></switch>\n" +
    "				</span>\n" +
    "				<input name=\"\" type=\"hidden\" data-ng-model=\"policy.effects\" value=\"{{policy.effects}}\" data-ng-focus=\"highlightGrammar('policyEffect')\">\n" +
    "				<span>{{'keep me logged in' |translate}}</span>\n" +
    "			</div>\n" +
    "			\n" +
    "			<a style=\"margin-left: 130px\" class=\"btn btn-default sc-btn-save-page\" data-ng-click=\"doLogin()\">{{\"LOGIN\" | translate}}</a>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "</div>");
}]);

angular.module("ui/app/Overview/overview.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Overview/overview.html",
    "<div class=\"no margin sc-page-title\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'dashboard.title'|\n" +
    "			translate}}</div>\n" +
    "	</div>\n" +
    "	<div class=\"sc-page-title-right sc-title-button-box\">\n" +
    "		<a data-ng-click=\"refresh()\"\n" +
    "			class=\"sc-vertical-middle-div btn btn-default sc-btn-big\"> <i\n" +
    "			class=\"fa fa-refresh\"></i>&nbsp;&nbsp;{{\"button.refresh\" |\n" +
    "			translate}}\n" +
    "		</a>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"sc-overview-container\">\n" +
    "	<div class=\"sc-db-services-wrapper\">\n" +
    "		<div class=\"sc-db-wrapper-title\">\n" +
    "			<span>{{'components.label' | translate}} </span>\n" +
    "		</div>\n" +
    "		<table width=\"15%\" class=\"sc-db-legend-table\">\n" +
    "			<tr>\n" +
    "				<td width=\"3%\"><div\n" +
    "						class=\"sc-db-legend-square sc-db-legend-green\"></div></td>\n" +
    "				<td width=\"47%\" class=\"sc-db-legend-text\">{{'service.healthy.label'\n" +
    "					| translate}}</td>\n" +
    "				<td width=\"3%\"><div\n" +
    "						class=\"sc-db-legend-square sc-db-legend-red\"></td>\n" +
    "				<td width=\"50%\" class=\"sc-db-legend-text\">{{'service.unhealthy.label'\n" +
    "					| translate}}</td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "		<table width=\"90%\" class=\"sc-db-services-table\">\n" +
    "			<tr>\n" +
    "				<td width=\"33%\" data-ng-click=\"goToService('watcher')\"><div\n" +
    "						class=\"sc-db-service-total\" data-ng-if=\"!watcherInfo.missing\">\n" +
    "						<div class=\"sc-vertical-middle-div\">\n" +
    "							<p class='sc-db-context'>{{'total.label'|translate}}</p>\n" +
    "							<p class=\"sc-db-data\">{{watcherInfo.total}}</p>\n" +
    "							<p class='sc-db-context'>{{'watchers.label'|translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "					<canvas class=\"chart chart-doughnut\" id=\"watcherChart\"\n" +
    "						data-ng-if=\"!watcherInfo.missing\" chart-data=\"watcherData\"\n" +
    "						chart-labels=\"serviceLabels\" chart-options=\"doughnutChartOption\"\n" +
    "						chart-colours=\"serviceChartColors\">\n" +
    "					</canvas>\n" +
    "					<div class=\"sc-db-service-count\">\n" +
    "						<table data-ng-if=\"!watcherInfo.missing\">\n" +
    "							<tr>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-green\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{watcherInfo.healthy}}</div>\n" +
    "								</td>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-red\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{watcherInfo.critical}}</div>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "					<p data-ng-if=\"watcherInfo.missing\" class=\"sc-db-alternate\">{{'watcher.not.found'|\n" +
    "						translate}}</p></td>\n" +
    "				<td width=\"33%\" data-ng-click=\"goToService('extractor')\"><div\n" +
    "						class=\"sc-db-service-total\" data-ng-if=\"!extractorInfo.missing\">\n" +
    "						<div class=\"sc-vertical-middle-div\">\n" +
    "							<p class='sc-db-context'>{{'total.label'|translate}}</p>\n" +
    "							<p class=\"sc-db-data\">{{extractorInfo.total}}</p>\n" +
    "							<p class='sc-db-context'>{{'extractors.label'|translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "					<canvas class=\"chart chart-doughnut\" id=\"extractorChart\"\n" +
    "						data-ng-if=\"!extractorInfo.missing\" chart-data=\"extractorData\"\n" +
    "						chart-labels=\"serviceLabels\" chart-options=\"doughnutChartOption\"\n" +
    "						chart-colours=\"serviceChartColors\">\n" +
    "					</canvas>\n" +
    "					<div class=\"sc-db-service-count\">\n" +
    "						<table data-ng-if=\"!extractorInfo.missing\">\n" +
    "							<tr>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-green\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{extractorInfo.healthy}}</div>\n" +
    "								</td>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-red\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{extractorInfo.critical}}</div>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "					<p data-ng-if=\"extractorInfo.missing\" class=\"sc-db-alternate\">{{'extractor.not.found'|\n" +
    "						translate}}</p></td>\n" +
    "				<td width=\"33%\" data-ng-click=\"goToService('ruleEngine')\"><div\n" +
    "						class=\"sc-db-service-total\" data-ng-if=\"!ruleEngineInfo.missing\">\n" +
    "						<div class=\"sc-vertical-middle-div\">\n" +
    "							<p class='sc-db-context'>{{'total.label'|translate}}</p>\n" +
    "							<p class=\"sc-db-data\">{{ruleEngineInfo.total}}</p>\n" +
    "							<p class='sc-db-context'>{{'ruleEngines.label'|translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "					<canvas class=\"chart chart-doughnut\" id=\"ruleEngineChart\"\n" +
    "						data-ng-if=\"!ruleEngineInfo.missing\" chart-data=\"ruleEngineData\"\n" +
    "						chart-labels=\"serviceLabels\" chart-options=\"doughnutChartOption\"\n" +
    "						chart-colours=\"serviceChartColors\">\n" +
    "					</canvas>\n" +
    "					<div class=\"sc-db-service-count\">\n" +
    "						<table data-ng-if=\"!ruleEngineInfo.missing\">\n" +
    "							<tr>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-green\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{ruleEngineInfo.healthy}}</div>\n" +
    "								</td>\n" +
    "								<td>\n" +
    "									<div class=\"sc-db-legend-square sc-db-legend-red\"></div>\n" +
    "								</td>\n" +
    "								<td class=\"sc-db-service-count-data\">\n" +
    "									<div>{{ruleEngineInfo.critical}}</div>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "					<p data-ng-if=\"ruleEngineInfo.missing\" class=\"sc-db-alternate\">{{'ruleEngine.not.found'|\n" +
    "						translate}}</p></td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"sc-db-document-wrapper\">\n" +
    "		<div class=\"sc-db-extraction-wrapper sc-inline\">\n" +
    "			<div class=\"sc-db-wrapper-title\">\n" +
    "				<span>{{'file.processing.status.label' | translate}}</span>\n" +
    "			</div>\n" +
    "			<table width=\"15%\" class=\"sc-db-legend-table\">\n" +
    "				<tr>\n" +
    "					<td width=\"3%\"><div\n" +
    "							class=\"sc-db-legend-square sc-db-legend-blue\"></div></td>\n" +
    "					<td width=\"47%\" class=\"sc-db-legend-text\">{{'success.label' |\n" +
    "						translate}}</td>\n" +
    "					<td width=\"3%\"><div\n" +
    "							class=\"sc-db-legend-square sc-db-legend-grey\"></td>\n" +
    "					<td width=\"50%\" class=\"sc-db-legend-text\">{{'failure.label' |\n" +
    "						translate}}</td>\n" +
    "				</tr>\n" +
    "			</table>\n" +
    "\n" +
    "			<div class=\"sc-db-extraction-content\">\n" +
    "				<div class=\"sc-db-file-total\" data-ng-if=\"processDetailsFound\">\n" +
    "					<div class=\"sc-vertical-middle-div\">\n" +
    "						<p class='sc-db-context'>{{'total.label'|translate}}</p>\n" +
    "						<p class=\"sc-db-data\">{{totalDocs}}</p>\n" +
    "						<p class='sc-db-context'>{{'files.processed.label'|translate}}</p>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<canvas class=\"chart chart-doughnut\" id=\"extractionChart\"\n" +
    "					data-ng-if=\"processDetailsFound\"\n" +
    "					data-ng-if=\"!extractorInfo.missing\" chart-data=\"processDetails\"\n" +
    "					chart-labels=\"processLabels\" chart-options=\"doughnutChartOption\"\n" +
    "					chart-colours=\"processChartColors\"></canvas>\n" +
    "				<div class=\"sc-db-file-count\">\n" +
    "					<table data-ng-if=\"processDetailsFound\">\n" +
    "						<tr>\n" +
    "							<td>\n" +
    "								<div class=\"sc-db-legend-square sc-db-legend-blue\"></div>\n" +
    "							</td>\n" +
    "							<td class=\"sc-db-service-count-data\">\n" +
    "								<div>{{successExtraction}}</div>\n" +
    "							</td>\n" +
    "							<td>\n" +
    "								<div class=\"sc-db-legend-square sc-db-legend-grey\"></div>\n" +
    "							</td>\n" +
    "							<td class=\"sc-db-service-count-data\">\n" +
    "								<div>{{failExtraction}}</div>\n" +
    "							</td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "				<p data-ng-if=\"!processDetailsFound\" class=\"sc-db-alternate\"\n" +
    "					style=\"padding: 20% 10% 20% 10%\">{{'processDetails.not.found'|\n" +
    "					translate}}</p>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-db-license-wrapper sc-inline\">\n" +
    "			<div class=\"sc-db-wrapper-title\">\n" +
    "				<span>{{'data.volume.label' | translate}}</span>\n" +
    "			</div>\n" +
    "			<table width=\"15%\" class=\"sc-db-legend-table\"\n" +
    "				data-ng-if=\"getRemaining() >= 0\">\n" +
    "				<tr>\n" +
    "					<td width=\"3%\"><div\n" +
    "							class=\"sc-db-legend-square sc-db-legend-blue-1\"></div></td>\n" +
    "					<td width=\"47%\" class=\"sc-db-legend-text\">{{'used.label' |\n" +
    "						translate}}</td>\n" +
    "					<td width=\"3%\"><div\n" +
    "							class=\"sc-db-legend-square sc-db-legend-grey-1\"></td>\n" +
    "					<td width=\"50%\" class=\"sc-db-legend-text\">{{'remaining.label' |\n" +
    "						translate}}</td>\n" +
    "				</tr>\n" +
    "			</table>\n" +
    "			<div class=\"sc-red sc-db-limit-exceed\"\n" +
    "				data-ng-if=\"getRemaining() < 0\">{{'limit.exceeded.label'|translate}}!</div>\n" +
    "			<div class=\"sc-db-license-content\">\n" +
    "				<table width=\"100%\" class=\"sc-license-usage-table\"\n" +
    "					data-ng-if=\"licenseFound\">\n" +
    "					<tr>\n" +
    "						<td><uib-progressbar value=\"usedSize\" max=\"dataSize\"\n" +
    "								data-ng-class=\"{'sc-progress-bar-exceed': usedSize > dataSize}\"\n" +
    "								type=\"{{getUsedSizeStyle()}}\">\n" +
    "							<div class=\"sc-progress-bar-exceed-break\" data-ng-if=\"getRemaining() < 0\"></div>\n" +
    "							</uib-progressbar></td>\n" +
    "						<p data-ng-if=\"!licenseFound\" class=\"sc-db-alternate\"\n" +
    "							style=\"padding: 20% 10% 20% 10%\">{{'usage.not.found'|\n" +
    "							translate}}</p>\n" +
    "					</tr>\n" +
    "				</table>\n" +
    "\n" +
    "				<table width=\"100%\" data-ng-if=\"licenseFound\">\n" +
    "					<tr>\n" +
    "						<td width=\"33%\">\n" +
    "							<div class=\"sc-license-context\">{{'used.label' |\n" +
    "								translate}}</div>\n" +
    "							<div class=\"sc-db-data-sm\">{{usedSize}} GB</div>\n" +
    "						</td>\n" +
    "						<td width=\"33%\">\n" +
    "							<div class=\"sc-license-context\" data-ng-if=\"getRemaining() >= 0\">{{'license.remaining.label'\n" +
    "								| translate}}</div>\n" +
    "							<div class=\"sc-license-context\" data-ng-if=\"getRemaining() < 0\">{{'license.exceeded.label'\n" +
    "								| translate}}</div>\n" +
    "							<div class=\"sc-db-data-sm\">{{getRemaining()}} GB</div>\n" +
    "						</td>\n" +
    "						<td width=\"33%\">\n" +
    "							<div class=\"sc-license-context\">{{'license.limit.label' |\n" +
    "								translate}}</div>\n" +
    "							<div class=\"sc-db-data-sm\">{{dataSize}} GB</div>\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</table>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("ui/app/Rules/partials/ruleoption-template.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Rules/partials/ruleoption-template.html",
    "<div class=\"sc-btn-grp sc-inline\">\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Delete\" data-ng-click=\"deleteRule(rule, $event)\"><i class=\"sc-icon-trash\"></i></button>\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Edit\" data-ng-click=\"openRule(rule)\"><i class=\"fa fa-pencil\"></i></button>\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Duplicate\" data-ng-click=\"duplicateRule(rule)\"><i class=\"fa fa-copy\"></i></button>\n" +
    "  <button class=\"btn btn-default\" title=\"Preview\" data-ng-click=\"preview(rule)\" style = \"height: 40px\">{{\"preview.button\" | translate}}</button>\n" +
    "</div>");
}]);

angular.module("ui/app/Rules/ruleDetails.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Rules/ruleDetails.html",
    "<div data-ng-if=\"!detailsFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'component.details.not.found' |\n" +
    "		translate}}</p>\n" +
    "</div>\n" +
    "<div class=\"sc-resource\" data-ng-if=\"detailsFound\" style=\"height: 100%\">\n" +
    "	<form id=\"ruleDetailsForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"ruleDetailsForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\" data-ng-click=\"backToRuleList(ruleDetailsForm)\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-title\">\n" +
    "				<div class=\"sc-vertical-middle-div\">\n" +
    "					<p class=\"sc-details-page-edit-text\" data-ng-if=\"ruleID !='create'\">{{\"edit.label\"\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-text\" data-ng-if=\"ruleID =='create'\">{{\"create.label\"\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-subject\">{{rule.name}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-details-page-discard-btn\"\n" +
    "					data-ng-click=\"discardRuleChanges(ruleDetailsForm)\">{{\"button.discard\"\n" +
    "					| translate}} </a> <a class=\"btn btn-default sc-btn-save-page\"\n" +
    "					data-ng-click=\"saveRuleChanges(ruleDetailsForm)\">{{\"button.save\"\n" +
    "					| translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-details-page-summary-div\">\n" +
    "				<a href=\"\" data-ng-click=\"scrollTo('ruleInfo')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('ruleInfo')}\">\n" +
    "						<label> {{\"component.summary.this.label\" | translate}}\n" +
    "							{{'rule.label'| translate}}</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span ng-hide=\"rule.name.length\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'rule.summary.name.alt'\n" +
    "								| translate}}> </span> <span class=\"sc-details-page-summary-content\"\n" +
    "								ng-show=\"rule.name.length\"> {{rule.name}}</span><span\n" +
    "								data-ng-show=\"rule.enabled\"> ({{'active.label' |\n" +
    "								translate}})</span><span data-ng-show=\"!rule.enabled\">({{'inactive.label'\n" +
    "								| translate}})</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('criterias')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('criterias')}\">\n" +
    "						<label> {{\"rule.summary.look.for.criteria.label\" |\n" +
    "							translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span\n" +
    "								ng-show=\"!rule.criteriaGroups || rule.criteriaGroups.length === 0\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'rule.summary.criterias.alt'\n" +
    "								| translate}}> </span> <span\n" +
    "								data-ng-repeat=\"(index, cg) in rule.criteriaGroups\"\n" +
    "								ng-show=\"rule.criteriaGroups && rule.criteriaGroups.length > 0\"\n" +
    "								class=\"sc-details-page-summary-content\"><span\n" +
    "								ng-bind-html=\"'(' | translate\"></span><span\n" +
    "								data-ng-repeat=\"(cindex, cri) in cg.criterias\"><span\n" +
    "									data-ng-if=\"cri.dataSection !== 'M'\">{{\"data.section.\" +\n" +
    "										cri.dataSection + \".label\" | translate}}</span><span\n" +
    "									class=\"sc-rule-summary-mc\" data-ng-if=\"cri.dataSection !== 'M'\">&nbsp;{{'matching.condition.'\n" +
    "										+ cri.matchingCondition | translate}}&nbsp;</span><span\n" +
    "									data-ng-if=\"cri.dataSection !== 'M'\">{{cri.value}}</span> <span\n" +
    "									data-ng-if=\"!cri.field && cri.dataSection === 'M'\"><{{'key.label'\n" +
    "										|translate}}></span> <span data-ng-if=\"cri.dataSection === 'M'\">{{cri.field}}&nbsp;</span><span\n" +
    "									data-ng-if=\"cri.dataSection === 'M'\"\n" +
    "									ng-bind-html=\"\n" +
    "										'meta.matching.condition.' + cri.matchingCondition |\n" +
    "										translate\"\n" +
    "									class=\"sc-rule-summary-mc\"></span> <span\n" +
    "									data-ng-if=\"cri.dataSection === 'M'\">{{isDateCriteria(cri)?(cri.value\n" +
    "										| date:'dd/MM/yyyy'):cri.value}}</span><span data-ng-if=\"!cri.value\"><{{'value.label'\n" +
    "										|translate}}></span><label\n" +
    "									data-ng-if=\"cindex !== cg.criterias.length - 1\">\n" +
    "										&nbsp;{{'or.label' | translate}}&nbsp;</label></span><span\n" +
    "								ng-bind-html=\"' )'\"></span></br> <label\n" +
    "								data-ng-if=\"index !== rule.criteriaGroups.length - 1\">\n" +
    "									{{'and.label'| translate}}&nbsp;</label> </span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('directories')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('directories')}\">\n" +
    "						<label> {{\"rule.summary.directories.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span\n" +
    "								data-ng-show=\"!rule.directories || rule.directories.length === 0 || !rule.directories[0].criterias || rule.directories[0].criterias.length === 0\"\n" +
    "								class=\"sc-details-page-summary-content\">{{'rule.summary.directories.alt'\n" +
    "								| translate}} </span> <span\n" +
    "								data-ng-show=\"rule.directories && rule.directories.length > 0 && rule.directories[0].criterias && rule.directories[0].criterias.length > 0\"\n" +
    "								data-ng-repeat=\"(index, dir) in rule.directories[0].criterias\"><span\n" +
    "								data-ng-show=\"dir.value && dir.value.length > 0\">{{dir.value}}</br></span></span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a><a href=\"\" data-ng-click=\"scrollTo('actions')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('action')}\">\n" +
    "						<label> {{\"rule.summary.action.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span data-ng-show=\"!rule.actions || rule.actions.length === 0\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'rule.summary.action.alt'\n" +
    "								| translate}}> </span> <span\n" +
    "								data-ng-show=\"rule.actions && rule.actions.length > 0\"\n" +
    "								data-ng-repeat=\"(index, action) in rule.actions\"><span>{{getActionPluginName(action.actionPlugin)}}</br></span></span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a><a href=\"\" data-ng-click=\"scrollTo('expiry')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('expiry')}\">\n" +
    "						<label>{{'and.label' | translate}}\n" +
    "							{{\"component.summary.valid.from.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{validity.validFrom | date:'dd/MM/yyyy'}}</span>\n" +
    "						</div>\n" +
    "						<label data-ng-show=\"validity.expiringOnOption  === 'never'\">\n" +
    "							{{\"component.summary.never.expires.label\" | translate}} </label> <label\n" +
    "							data-ng-show=\"validity.expiringOnOpption ==='specific'\">\n" +
    "							{{\"component.summary.expiring.on.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\"\n" +
    "							data-ng-show=\"validity.expiringOnOption === 'specific'\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{validity.expiringOn | date:'dd/MM/yyyy'}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('schedule')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('schedule')}\">\n" +
    "						<label> {{\"rule.summary.schedule.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{getScheduleAsText()}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a>\n" +
    "				<div class=\"sc-rule-summary-btn-div\">\n" +
    "					<a class=\"btn btn-default sc-btn-preview-rul\"\n" +
    "						data-ng-click=\"preview()\">{{\"preview.button\" | translate}} </a> <a\n" +
    "						class=\"btn btn-default sc-btn-execute-rule\"\n" +
    "						data-ng-click=\"execute()\">{{\"execute.button\" | translate}} </a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "				<div id=\"ruleInfo\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"rule.editor.info.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"ruleName\">{{\"component.editor.name.label\" |\n" +
    "							translate}}</label> <input id=\"ruleName\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'rule.editor.name.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"rule.name\" name=\"ruleName\" required\n" +
    "							ng-maxlength=\"50\" max-length=\"50\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"ruleDetailsForm.ruleName.$error\"\n" +
    "							ng-if=\"ruleDetailsForm.ruleName.$touched\">\n" +
    "							<p ng-message=\"maxlength\">{{\"rule.editor.name.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"required\">{{\"rule.editor.name.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"description\">{{\"rule.editor.description.label\"\n" +
    "							| translate}}</label>\n" +
    "						<textarea id=\"description\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-component-text-area sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'rule.editor.description.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"rule.description\" name=\"description\"></textarea>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"status\">{{\"rule.editor.status.label\" |\n" +
    "							translate}}</label>\n" +
    "						<div style=\"margin-top: 10px\">\n" +
    "							<span> <switch class=\"sc-switch sc-inline\" id=\"status\"\n" +
    "									name=\"status\" data-ng-model=\"rule.enabled\"></switch>\n" +
    "							</span><span class=\"sc-inline\">{{'active.label'|translate}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"ruleEngine\">{{\"component.editor.rule.engine.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div id=\"ruleEngine\"\n" +
    "							class=\"sc-details-page-editor-data-right btn-group btn-block\"\n" +
    "							data-uib-dropdown>\n" +
    "							<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "								data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "								<span class=\"sc-dropdown-btn-label\">{{rule.ruleEngine.name}}</span>\n" +
    "								<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "							</button>\n" +
    "							<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "								aria-labelledby=\"\">\n" +
    "								<li role=\"menuitem\"\n" +
    "									data-ng-repeat=\"ruleEngine in ruleEngineList\"\n" +
    "									data-ng-click=\"changeRuleEngine(ruleEngine)\"\n" +
    "									class=\"sc-clickable-dropdown\">{{ruleEngine.name}}</li>\n" +
    "							</ul>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "				<div id=\"criterias\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"rule.editor.criterias.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label\n" +
    "							data-ng-if=\"rule.criteriaGroups && rule.criteriaGroups.length > 0\">{{\"rule.editor.look.for.label\"\n" +
    "							| translate}}</label> <label\n" +
    "							data-ng-if=\"!rule.criteriaGroups || rule.criteriaGroups.length === 0\">{{\"rule.editor.look.for.alt\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-criteria-group-div\"\n" +
    "							data-ng-repeat=\"(index,cg) in rule.criteriaGroups\">\n" +
    "							<i class=\"fa fa-times-circle-o sc-component-del-icon\"\n" +
    "								style=\"transform: translate(50%, -50%)\"\n" +
    "								data-ng-click=\"removeCriteriaGroup(index)\"> </i>\n" +
    "							<div class=\"sc-criteria-group-sub-div\">\n" +
    "								<table class=\"sc-criteria-table\">\n" +
    "									<tr>\n" +
    "										<td width=\"5%\" rowspan=\"{{cg.criterias.length + 1}}\"\n" +
    "											style=\"border-right: 1px solid #CCCCCC\"\n" +
    "											data-ng-if=\"cg.criterias.length  > 1\"><span>{{'or.label'\n" +
    "												| translate}}</span></td>\n" +
    "									</tr>\n" +
    "									<tr data-ng-repeat=\"(cindex,cri) in cg.criterias\">\n" +
    "\n" +
    "										<td width=\"20%\"><div class=\"btn-group btn-block\"\n" +
    "												data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\">{{\"data.section.\"\n" +
    "														+ cri.dataSection + \".label\" | translate}}</span> <span\n" +
    "														class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\" data-ng-repeat=\"ds in dataSections\"\n" +
    "														data-ng-if=\"ds != 'D'\"\n" +
    "														data-ng-click=\"changeDataSection(cri, ds)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{\"data.section.\" + ds\n" +
    "														+ \".label\" | translate}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"20%\" data-ng-if=\"!isMetaData(cri, cg)\"><div\n" +
    "												class=\"btn-group btn-block\" data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\">{{\"matching.condition.\"\n" +
    "														+ cri.matchingCondition | translate}}</span> <span\n" +
    "														class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\"\n" +
    "														data-ng-repeat=\"mc in matchingConditions\"\n" +
    "														data-ng-click=\"changeMatchingCondition(cri, mc)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{\"matching.condition.\"\n" +
    "														+ mc.code | translate}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"23%\" data-ng-if=\"isMetaData(cri, cg)\"><input\n" +
    "											class=\"sc-input-dropdown-input sc-details-page-editor-input-placeholder sc-details-page-common-input\"\n" +
    "											data-ng-model=\"cri.field\"\n" +
    "											data-ng-keypress=\"resetMetaMatchingCondition(cri)\"\n" +
    "											name=\"{{'criteriaField-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': ruleDetailsForm['criteriaField-' + index + '' + cindex].$dirty && ruleDetailsForm['criteriaField-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['criteriaField-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"50\" ng-maxlength=\"50\"\n" +
    "											data-ng-change=\"updateMetaField(cri)\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['criteriaField-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.field.place.holder' | translate}}\">\n" +
    "											<div class=\"sc-input-dropdown-dropdown btn-group\"\n" +
    "												data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-input-dropdown-menu\"\n" +
    "													role=\"menu\" aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\" data-ng-repeat=\"meta in metaDataList\"\n" +
    "														data-ng-click=\"changeMetadataField(cri, meta)\"\n" +
    "														class=\"sc-clickable-dropdown\">{{meta.value}}</li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "										<td width=\"19%\" data-ng-if=\"isMetaData(cri, cg)\"><div\n" +
    "												class=\"btn-group btn-block\" data-uib-dropdown>\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\"\n" +
    "														ng-bind-html=\"'\n" +
    "														meta.matching.condition.'\n" +
    "														+cri.matchingCondition | translate\"></span>\n" +
    "													<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "													<li role=\"menuitem\"\n" +
    "														ng-bind-html=\"'\n" +
    "														meta.matching.condition.'\n" +
    "														+ mc.code | translate\"\n" +
    "														data-ng-repeat=\"mc in metaDataMatchingConditions\"\n" +
    "														data-ng-click=\"changeMetaMatchingCondition(cri, mc)\"\n" +
    "														data-ng-if=\"!hideMetaMatchingCondition(cri, mc.code)\"\n" +
    "														class=\"sc-clickable-dropdown\"></li>\n" +
    "												</ul>\n" +
    "											</div></td>\n" +
    "\n" +
    "\n" +
    "										<td width=\"45%\" data-ng-if=\"!isMetaData(cri, cg)\"\n" +
    "											colspan=\"{{hasMetaData(cg)?2:1}}\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											data-ng-change=\"checkDataProvider(cri)\"\n" +
    "											uib-typeahead=\"dp.suggestion for dp in dataProviders | filter:{suggestion:$viewValue}:checkDataProvider | limitTo:10\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': ruleDetailsForm['criteriaValue-' + index + '' + cindex].$dirty && ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder'| translate}}\">\n" +
    "										</td>\n" +
    "\n" +
    "										<td width=\"23%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isStringCriteria(cri)\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											data-ng-change=\"checkDataProvider(cri)\"\n" +
    "											uib-typeahead=\"dp.suggestion for dp in dataProviders | filter:{suggestion:$viewValue}:checkDataProvider | limitTo:10\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': ruleDetailsForm['criteriaValue-' + index + '' + cindex].$dirty && ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder'| translate}}\"></td>\n" +
    "\n" +
    "										<td width=\"23%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && cri.matchingCondition ==='BETWEEN'\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\"\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': ruleDetailsForm['criteriaValue-' + index + '' + cindex].$dirty && ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder' | translate}}\"></td>\n" +
    "\n" +
    "\n" +
    "										<td width=\"23%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isNumberCriteria(cri)\"><input\n" +
    "											class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "											data-ng-model=\"cri.value\" ui-hide-group-sep\n" +
    "											ui-negative-number\n" +
    "											name=\"{{'criteriaValue-' + index + '' + cindex}}\" required\n" +
    "											data-ng-class=\"{'has-error': ruleDetailsForm['criteriaValue-' + index + '' + cindex].$dirty && ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid}\"\n" +
    "											uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['criteriaValue-' + index + '' + cindex])}}\"\n" +
    "											max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "											tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['criteriaValue-' + index + '' + cindex].$invalid]}}\"\n" +
    "											tooltip-placement=\"bottom\"\n" +
    "											placeholder=\"{{'meta.value.place.holder' | translate}}\"\n" +
    "											ui-number-mask=\"0\"></td>\n" +
    "\n" +
    "										<td width=\"23%\"\n" +
    "											data-ng-if=\"isMetaData(cri, cg) && isDateCriteria(cri)\">\n" +
    "											<div class=\"sc-date-picker\">\n" +
    "												<span class=\"input-group\"> <input type=\"text\"\n" +
    "													class=\"form-control\"\n" +
    "													name=\"{{'criteriaValue-' + index + '' + cindex}}\"\n" +
    "													uib-datepicker-popup=\"dd/MM/yyyy\" ng-model=\"cri.date\"\n" +
    "													data-ng-change=\"updateDateCriteria(cri)\"\n" +
    "													is-open=\"cri.dateOpened\" ng-required show-button-bar=\"true\"\n" +
    "													close-text=\"{{'close.label'|translate}}\"\n" +
    "													current-text=\"{{'today.label'|translate}}\"\n" +
    "													clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "													class=\"input-group-btn\">\n" +
    "														<button type=\"button\" class=\"btn btn-default\"\n" +
    "															data-ng-click=\"cri.openDate()\">\n" +
    "															<i class=\"fa fa-calendar\"></i>\n" +
    "														</button>\n" +
    "												</span>\n" +
    "												</span>\n" +
    "											</div>\n" +
    "										</td>\n" +
    "										<td width=\"5%\"><div\n" +
    "												class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "												data-ng-click=\"removeCriteria(cg, index, cindex)\"></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "\n" +
    "								<a data-ng-click=\"addCriteria(cg)\" class=\"btn btn-default\"\n" +
    "									style=\"margin-right: 1%; margin-top: 1%; margin-bottom: 1%\">\n" +
    "									<i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'rule.editor.add.criteria.button'\n" +
    "									| translate}}\n" +
    "								</a>\n" +
    "							</div>\n" +
    "							<div class=\"sc-rule-criteria-and\"\n" +
    "								data-ng-show=\"index != rule.criteriaGroups.length - 1\">{{'and.label'|\n" +
    "								translate}}</div>\n" +
    "						</div>\n" +
    "						<a data-ng-click=\"addCriteriaGroup()\" class=\"btn btn-default\"\n" +
    "							data-ng-if=\"!rule.criteriaGroups || rule.criteriaGroups.length  === 0\"\n" +
    "							style=\"margin-right: 2%; margin-top: 2%\"> <i\n" +
    "							class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'rule.editor.add.criteria.group.button.first'\n" +
    "							| translate}}\n" +
    "						</a> <a data-ng-click=\"addCriteriaGroup()\" class=\"btn btn-default\"\n" +
    "							data-ng-if=\"rule.criteriaGroups.length > 0\"\n" +
    "							style=\"margin-top: 2%\"> <i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{'rule.editor.add.criteria.group.button'\n" +
    "							| translate}}\n" +
    "						</a>\n" +
    "\n" +
    "\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "				<div id=\"directories\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"rule.editor.directories.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label\n" +
    "							data-ng-if=\"rule.directories && rule.directories.length > 0 && rule.directories[0].criterias && rule.directories[0].criterias.length > 0\">{{\"rule.editor.directories.context\"\n" +
    "							| translate}}</label> <label\n" +
    "							data-ng-if=\"!rule.directories || rule.directories.length === 0 ||  !rule.directories[0].criterias || rule.directories[0].criterias.length === 0\">{{\"rule.editor.directories.alt\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-rule-directories\">\n" +
    "							<table>\n" +
    "								<tr\n" +
    "									data-ng-repeat=\"(index, dir) in rule.directories[0].criterias\">\n" +
    "									<!-- <td width=\"10%\"><span>{{'browse.label' |\n" +
    "											translate}}</span></td> -->\n" +
    "									<td width=\"95%\"><input\n" +
    "										data-ng-class=\"{'has-error': ruleDetailsForm['directory0' + index].$dirty && ruleDetailsForm['directory0' + index].$invalid}\"\n" +
    "										class=\"sc-details-page-editor-input-placeholder\n" +
    "									sc-details-page-editor-data-full sc-details-page-common-input\"\n" +
    "										placeholder=\"{{'rule.editor.directory.place.holder' |\n" +
    "									translate}}\"\n" +
    "										data-ng-model=\"dir.value\" name=\"{{'directory0' + index}}\"\n" +
    "										required max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "										uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['directory0' + index])}}\"\n" +
    "										max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "										tooltip-trigger=\"mouseenter\" tooltip-placement=\"bottom\">\n" +
    "									<td width=\"5%\"><div\n" +
    "											class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"removeDirectory(index)\"></div></td>\n" +
    "								</tr>\n" +
    "							</table>\n" +
    "						</div>\n" +
    "						<a data-ng-click=\"addDirectory()\" class=\"btn btn-default\"\n" +
    "							style=\"margin-top: 2%\"> <i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{\"rule.directory.new.button\"\n" +
    "							| translate}}\n" +
    "						</a>\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "\n" +
    "\n" +
    "				<div id=\"actions\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"rule.editor.actions.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"schedule\">{{\"rule.editor.actions.context.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div data-ng-if=\"rule.actions.length\"\n" +
    "							data-ng-repeat=\"(index,action) in rule.actions\">\n" +
    "							<table class=\"sc-rule-action-table\">\n" +
    "								<tr>\n" +
    "									<td width=\"80%\"><div class=\"sc-action-name-heading\">{{getActionPluginName(action.actionPlugin)}}</div>\n" +
    "									</td>\n" +
    "									<td width=\"20%\" style=\"text-align: right\"><div\n" +
    "											class=\"sc-rule-action-inline-btn fa fa-check sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"saveAction(index, ruleDetailsForm)\"\n" +
    "											data-ng-show=\"!actionCollapse[index]\"></div>\n" +
    "										<div\n" +
    "											class=\"sc-rule-action-inline-btn fa fa-edit sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"editAction(index)\"\n" +
    "											data-ng-show=\"actionCollapse[index]\"></div>\n" +
    "										<div\n" +
    "											class=\"sc-rule-action-inline-btn fa fa-trash-o sc-bigger-icon\n" +
    "											sc-inline\"\n" +
    "											data-ng-click=\"removeAction(index)\"></div></td>\n" +
    "								</tr>\n" +
    "\n" +
    "							</table>\n" +
    "							<div uib-collapse=\"actionCollapse[index]\"\n" +
    "								id=\"{{'actionCollapse' + index}}\" class=\"sc-rule-action-details\">\n" +
    "								<table width=\"100%\">\n" +
    "\n" +
    "									<tr data-ng-repeat=\"(pindex,param) in action.actionParams\">\n" +
    "										<td width=\"18%\" class=\"sc-action-param-label\"><span>{{param.label}}</span></td>\n" +
    "										<td width=\"82%\">\n" +
    "											<div style=\"position: relative\"\n" +
    "												data-ng-if=\"!param.keyValue &&\n" +
    "											!param.collections\">\n" +
    "												<input data-ng-if=\"param.dataType ==='String'\"\n" +
    "													class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "													placeholder=\"{{'rule.editor.action.param.' + param.dataType | translate}}\"\n" +
    "													data-ng-model=\"param.values[0].value\"\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\" required\n" +
    "													data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid}\"\n" +
    "													uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex])}}\"\n" +
    "													max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "													tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid]}}\"\n" +
    "													tooltip-placement=\"bottom\" /> <input\n" +
    "													data-ng-if=\"param.dataType ==='Email'\"\n" +
    "													class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "													placeholder=\"{{'rule.editor.action.param.' + param.dataType | translate}}\"\n" +
    "													data-ng-model=\"param.values[0].value\" type=\"email\"\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\" required\n" +
    "													type=\"email\"\n" +
    "													data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid}\"\n" +
    "													uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex])}}\"\n" +
    "													max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "													tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid]}}\"\n" +
    "													tooltip-placement=\"bottom\" /> <input\n" +
    "													style=\"margin-left: 0px\"\n" +
    "													data-ng-if=\"param.dataType === 'Integer'\" ui-hide-group-sep\n" +
    "													ui-negative-number\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\"\n" +
    "													class=\"sc-number-input-sm sc-details-page-common-input sc-action-param\"\n" +
    "													ui-number-mask=\"0\" data-ng-model=\"param.values[0].value\"\n" +
    "													required\n" +
    "													data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid}\"\n" +
    "													uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex])}}\"\n" +
    "													max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "													tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid]}}\"\n" +
    "													tooltip-placement=\"bottom\" /> <input\n" +
    "													style=\"margin-left: 0px\"\n" +
    "													data-ng-if=\"param.dataType === 'Number'\" ui-hide-group-sep\n" +
    "													ui-negative-number\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\"\n" +
    "													class=\"sc-number-input-sm sc-details-page-common-input sc-action-param\"\n" +
    "													ui-number-mask=\"2\" data-ng-model=\"param.values[0].value\"\n" +
    "													required\n" +
    "													data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid}\"\n" +
    "													uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex])}}\"\n" +
    "													max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "													tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid]}}\"\n" +
    "													tooltip-placement=\"bottom\" />\n" +
    "												<switch class=\"sc-switch sc-inline\"\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\"\n" +
    "													data-ng-if=\"checkBoolean(param)\"\n" +
    "													data-ng-model=\"param.values[0].value\"></switch>\n" +
    "\n" +
    "												<div text-angular\n" +
    "													tooltip-trigger=\"{{{true: 'focus', false: \n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid]}}\"\n" +
    "													required name=\"{{'actionParam-' + index + '' + pindex}}\"\n" +
    "													tooltip-placement=\"bottom\"\n" +
    "													data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex].$invalid}\"\n" +
    "													uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex])}}\"\n" +
    "													data-ng-if=\"param.dataType === 'Text'\"\n" +
    "													data-ng-model=\"param.values[0].value\"></div>\n" +
    "											</div>\n" +
    "											<div data-ng-if=\"!param.keyValue && param.collections\">\n" +
    "												<multi-param paramList=\"param.values\" class=\"multi-param\"\n" +
    "													name=\"{{'actionParam-' + index + '' + pindex}}\"\n" +
    "													input-type=\"{{param.dataType}}\" input-required=\"true\"\n" +
    "													placeholder=\"{{'rule.editor.action.param.' + param.dataType + '.multi' | translate}}\">\n" +
    "												</multi-param>\n" +
    "											</div>\n" +
    "											<div data-ng-if=\"param.keyValue && !param.collections\">\n" +
    "												<table style=\"width: 100%\">\n" +
    "													<tr>\n" +
    "														<td width=\"45%\"><input\n" +
    "															class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "															placeholder=\"{{'tag.name.action.param' | translate}}\"\n" +
    "															data-ng-model=\"param.values[0].key\"\n" +
    "															name=\"{{'actionParam-' + index + '' + pindex + 'key'}}\"\n" +
    "															required\n" +
    "															data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex+ 'key'].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex+ 'key'].$invalid}\"\n" +
    "															uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex+ 'key'])}}\"\n" +
    "															max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "															tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex+ 'key'].$invalid]}}\"\n" +
    "															tooltip-placement=\"bottom\"></td>\n" +
    "														<td width=45%><input\n" +
    "															class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "															placeholder=\"{{'tag.value.action.param' | translate}}\"\n" +
    "															data-ng-model=\"param.values[0].value\"\n" +
    "															name=\"{{'actionParam-' + index + '' + pindex+ 'value'}}\"\n" +
    "															required\n" +
    "															data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex+ 'value'].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex+ 'value'].$invalid}\"\n" +
    "															uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex+ 'value'])}}\"\n" +
    "															max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "															tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex+ 'value'].$invalid]}}\"\n" +
    "															tooltip-placement=\"bottom\"></td>\n" +
    "													</tr>\n" +
    "												</table>\n" +
    "											</div>\n" +
    "											<div data-ng-if=\"param.keyValue && param.collections\">\n" +
    "												<table style=\"width: 100%\">\n" +
    "													<tr data-ng-repeat=\"(eindex,element) in param.values\">\n" +
    "														<td width=\"45%\"><input\n" +
    "															class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "															placeholder=\"{{'tag.name.action.param' | translate}}\"\n" +
    "															data-ng-model=\"element.key\"\n" +
    "															name=\"{{'actionParam-' + index + '' + pindex +'' + eindex+ 'key'}}\"\n" +
    "															required\n" +
    "															data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex +'' + eindex+ 'key'].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'key'].$invalid}\"\n" +
    "															uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'key'])}}\"\n" +
    "															max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "															tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'key'].$invalid]}}\"\n" +
    "															tooltip-placement=\"bottom\"></td>\n" +
    "														<td width=45%><input\n" +
    "															class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-action-param\"\n" +
    "															placeholder=\"{{'tag.value.action.param' | translate}}\"\n" +
    "															data-ng-model=\"element.value\"\n" +
    "															name=\"{{'actionParam-' + index + '' + pindex+'' + eindex+ 'value'}}\"\n" +
    "															required\n" +
    "															data-ng-class=\"{'has-error': ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'value'].$dirty && ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'value'].$invalid}\"\n" +
    "															uib-tooltip=\"{{getTooltipMessage(ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'value'])}}\"\n" +
    "															max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "															tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[ruleDetailsForm['actionParam-' + index + '' + pindex+'' + eindex+ 'value'].$invalid]}}\"\n" +
    "															tooltip-placement=\"bottom\"></td>\n" +
    "														<td><div\n" +
    "																class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "																data-ng-click=\"removeActionParamElement(param, eindex)\"></td>\n" +
    "													</tr>\n" +
    "												</table>\n" +
    "												<a data-ng-click=\"addActionParamElement(param)\"\n" +
    "													class=\"btn btn-default\"\n" +
    "													style=\"margin-left: 2%; margin-top: 1%\"> <i\n" +
    "													class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{param.label}}\n" +
    "												</a>\n" +
    "											</div>\n" +
    "										</td>\n" +
    "									</tr>\n" +
    "									<tr>\n" +
    "										<td colspan=\"2\"><md-checkbox class=\"md-primary\"\n" +
    "												data-ng-model=\"action.toleranceLevel\" ng-true-value=\"'S'\"\n" +
    "												ng-false-value=\"'F'\" aria-label=\"\">{{'rule.editor.action.torrelance.context'\n" +
    "											| translate}}</md-checkbox></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<table class=\"sc-rule-action-dropdown-table\">\n" +
    "							<tr>\n" +
    "								<td width=\"70%\"><div data-uib-dropdown>\n" +
    "\n" +
    "										<button class=\"form-control sc-button-common\"\n" +
    "											id=\"actionDropDown\" type=\"button\" data-uib-dropdown-toggle\n" +
    "											ng-disabled=\"disabled\">\n" +
    "											<span class=\"sc-dropdown-btn-label\">{{selectActionDropDownBtn}}</span>\n" +
    "											<span class=\"sc-dropdown-btn-expand-icon\" />\n" +
    "										</button>\n" +
    "										<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "											aria-labelledby=\"\">\n" +
    "											<li role=\"menuitem\" data-ng-repeat=\"plugin in pluginList\"\n" +
    "												data-ng-click=\"addAction(plugin)\"\n" +
    "												class=\"sc-clickable-dropdown\">{{getActionPluginName(plugin)}}</li>\n" +
    "										</ul>\n" +
    "									</div></td>\n" +
    "								<td width=\"30%\"></td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "				<div id=\"expiry\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"rule.editor.expiry.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"validFrom\" class=\"sc-label-inline\">{{\"rule.editor.valid.from.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div name=\"validFrom\" id=\"validFrom\" class=\"sc-inline\">\n" +
    "\n" +
    "							<div class=\"sc-date-picker\">\n" +
    "								<p class=\"input-group\">\n" +
    "									<input type=\"text\" class=\"form-control\" name=\"validFrom\"\n" +
    "										id=\"validFrom\" uib-datepicker-popup=\"dd/MM/yyyy\"\n" +
    "										data-ng-model=\"validity.validFrom\"\n" +
    "										is-open=\"validFromPopUp.opened\" show-button-bar=\"true\"\n" +
    "										close-text=\"{{'close.label'|translate}}\"\n" +
    "										current-text=\"{{'today.label'|translate}}\"\n" +
    "										clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "										class=\"input-group-btn\">\n" +
    "										<button type=\"button\" class=\"btn btn-default\"\n" +
    "											data-ng-click=\"validFromOpen()\">\n" +
    "											<i class=\"fa fa-calendar\"></i>\n" +
    "										</button>\n" +
    "									</span>\n" +
    "								</p>\n" +
    "							</div>\n" +
    "							<div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"ruleDetailsForm.validFrom.$error\"\n" +
    "								ng-if=\"ruleDetailsForm.validFrom.$touched\">\n" +
    "								<p ng-message=\"required\">{{\"rule.editor.valid.from.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "					</div>\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "\n" +
    "						<label class=\"sc-label-inline\">{{\"rule.editor.expiring.on.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-inline\">\n" +
    "							<md-radio-group data-ng-model=\"validity.expiringOnOption\">\n" +
    "							<md-radio-button value=\"specific\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">{{'rule.editor.specific.date.label'\n" +
    "							| translate}}</md-radio-button> <!-- <md-radio-button value=\"xdays\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">\n" +
    "							{{'rule.editor.after.x.days.label'| translate}} </md-radio-button> -->\n" +
    "							<md-radio-button value=\"never\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">\n" +
    "							{{'rule.editor.never.label'| translate}} </md-radio-button></md-radio-group>\n" +
    "							<div data-ng-show=\"validity.expiringOnOption == 'specific'\"\n" +
    "								class=\"sc-date-picker\">\n" +
    "								<p class=\"input-group\">\n" +
    "									<input type=\"text\" class=\"form-control\" name=\"expiringOn\"\n" +
    "										id=\"expiringOn\" uib-datepicker-popup=\"dd/MM/yyyy\"\n" +
    "										data-ng-model=\"validity.expiringOn\"\n" +
    "										is-open=\"expiringOnPopUp.opened\"\n" +
    "										ng-required=\"expiringOnOption =='specific'\"\n" +
    "										show-button-bar=\"true\"\n" +
    "										close-text=\"{{'close.label'|translate}}\"\n" +
    "										current-text=\"{{'today.label'|translate}}\"\n" +
    "										clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "										class=\"input-group-btn\">\n" +
    "										<button type=\"button\" class=\"btn btn-default\"\n" +
    "											data-ng-click=\"expiringOnOpen()\">\n" +
    "											<i class=\"fa fa-calendar\"></i>\n" +
    "										</button>\n" +
    "									</span>\n" +
    "								</p>\n" +
    "							</div>\n" +
    "							<!-- <div data-ng-show=\"expiringOnOption == 'xdays'\">\n" +
    "								<div numeric=\"{min:1}\" required=\"expiringOnOption == 'xdays'\"\n" +
    "									name=\"expiringOn1\" id=\"expiringOn1\"\n" +
    "									class=\"sc-number-input sc-details-page-common-input sc-inline\"\n" +
    "									data-ng-model=\"afterXDays\"></div>\n" +
    "								<span class=\"sc-inline sc-details-page-editor-label\">{{'day.label'\n" +
    "									| translate}}</span>\n" +
    "							</div> -->\n" +
    "							<div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"ruleDetailsForm.expiringOn.$error\"\n" +
    "								ng-if=\"ruleDetailsForm.expiringOn.$touched && expiringOnOption == 'specific' && ruleDetailsForm.expiringOn.$error\">\n" +
    "								<p ng-message=\"required\">{{\"rule.editor.expiring.on.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div>\n" +
    "							<!-- <div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"ruleDetailsForm.expiringOn1.$error\"\n" +
    "								ng-if=\"ruleDetailsForm.expiringOn1.$touched && expiringOnOption == 'xdays' && ruleDetailsForm.expiringOn1.$error\">\n" +
    "								<p ng-message=\"required\">{{\"rule.editor.expiring.on.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div> -->\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div id=\"schedule\" class=\"sc-details-page-editor-title\">\n" +
    "						<span class=\"sc-details-page-title-underline\">\n" +
    "							{{\"rule.editor.schedule.label\" | translate}}</span>\n" +
    "					</div>\n" +
    "					<div class=\"sc-details-page-editor-info\">\n" +
    "						<div class=\"sc-details-page-editor-data\">\n" +
    "							<label for=\"schedule\">{{\"rule.editor.execute.rule.label\"\n" +
    "								| translate}}</label>\n" +
    "							<div class=\"sc-grey-div\">\n" +
    "								<md-radio-group data-ng-model=\"rule.scheduleType\">\n" +
    "								<md-radio-button value=\"D\"\n" +
    "									class=\"md-primary sc-radio-inline-with-background sc-inline\">{{'daily.label'\n" +
    "								| translate}}</md-radio-button> <md-radio-button value=\"W\"\n" +
    "									class=\"md-primary sc-radio-inline-with-background sc-inline\">\n" +
    "								{{'weekly.label'| translate}} </md-radio-button> <md-radio-button value=\"M\"\n" +
    "									class=\"md-primary sc-radio-inline-with-background sc-inline\">\n" +
    "								{{'monthly.label'| translate}} </md-radio-button></md-radio-group>\n" +
    "							</div>\n" +
    "							<div class=\"sc-border-div\"\n" +
    "								data-ng-show=\"rule.scheduleType == 'D'\">\n" +
    "								<table class=\"sc-schedule-table-daily\">\n" +
    "									<tr data-ng-repeat=\"dailySchedule in dailySchedules\"\n" +
    "										class=\"sc-table-row\">\n" +
    "										<td class=\"sc-padding-cell-left\" width=\"5%\"><span\n" +
    "											class=\"sc-inline sc-details-page-editor-label\">{{'at.label'|\n" +
    "												translate}} </td>\n" +
    "										<td></span> <uib-timepicker data-ng-model=\"dailySchedule.time\"\n" +
    "												class=\"sc-inline sc-timepicker\" minute-step=\"1\"\n" +
    "												hour-step=\"1\" show-meridian=\"true\"></uib-timepicker></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "							</div>\n" +
    "							<div class=\"sc-border-div\"\n" +
    "								data-ng-show=\"rule.scheduleType == 'W'\">\n" +
    "								<table class=\"sc-schedule-table-weekly\">\n" +
    "									<tr data-ng-repeat=\"weeklySchedule in weeklySchedules\"\n" +
    "										class=\"sc-table-row\">\n" +
    "										<td class=\"sc-padding-cell-left sc-padding-cell-right\"\n" +
    "											width=\"50%\">\n" +
    "											<div class=\"btn-group\">\n" +
    "												<label class=\"btn btn-primary\"\n" +
    "													data-ng-repeat=\"day in dayOfTheWeek\"\n" +
    "													data-ng-model=\"weeklySchedule.days[day]\" uib-btn-checkbox\n" +
    "													btn-checkbox-true=\"true\" btn-checkbox-false=\"false\">{{day\n" +
    "													+ \".label\"| translate}}</label>\n" +
    "											</div>\n" +
    "\n" +
    "										</td>\n" +
    "										<td class=\"sc-padding-cell-left sc-horizontal-center-div\"><span\n" +
    "											class=\"sc-inline sc-details-page-editor-label\">{{'at.label'|\n" +
    "												translate}} </td>\n" +
    "										<td></span> <uib-timepicker data-ng-model=\"weeklySchedule.time\"\n" +
    "												class=\"sc-inline sc-timepicker\" minute-step=\"1\"\n" +
    "												show-meridian=\"true\"></uib-timepicker></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "							</div>\n" +
    "\n" +
    "							<div class=\"sc-border-div\"\n" +
    "								data-ng-show=\"rule.scheduleType == 'M'\">\n" +
    "								<table class=\"sc-schedule-table-monthly\">\n" +
    "									<tr data-ng-repeat=\"monthlySchedule in monthlySchedules\"\n" +
    "										class=\"sc-table-row\">\n" +
    "										<td class=\"sc-padding-cell-left\" width=\"50%\">\n" +
    "											<div class=\"btn-group btn-block\" data-uib-dropdown\n" +
    "												auto-close=\"outsideClick\">\n" +
    "												<button class=\"form-control sc-button-common\" id=\"\"\n" +
    "													type=\"button\" data-uib-dropdown-toggle\n" +
    "													ng-disabled=\"disabled\">\n" +
    "													<span class=\"sc-dropdown-btn-label\">{{getDayOfMonthString(monthlySchedule.days)}}</span>\n" +
    "													<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "												</button>\n" +
    "												<ul\n" +
    "													class=\"sc-dropdown-day-of-month-picker uib-dropdown-menu\"\n" +
    "													role=\"menu\" aria-labelledby=\"\">\n" +
    "													<table width=\"100%\">\n" +
    "														<tr>\n" +
    "															<td width=\"40%\">\n" +
    "																<div class=\"btn-group\">\n" +
    "																	<button class=\"sc-select-all-btn btn\"\n" +
    "																		data-ng-click=\"selectAll(monthlySchedule)\">{{'select.all.button'\n" +
    "																		| translate}}</button>\n" +
    "																	<button class=\"sc-clear-all-btn btn\"\n" +
    "																		data-ng-click=\"clearAll(monthlySchedule)\">{{'clear.all.button'\n" +
    "																		| translate}}</button>\n" +
    "																</div>\n" +
    "															</td>\n" +
    "															<td width=\"60%\">{{'rule.editor.monthly.schedule.context'\n" +
    "																| translate}}</td>\n" +
    "														</tr>\n" +
    "													</table>\n" +
    "													<div class=\"sc-dropdown-day-of-month-wrapper\">\n" +
    "														<div data-ng-repeat=\"day in dayOfTheMonth\"\n" +
    "															data-ng-click=\"monthlyScheduleChange(monthlySchedule, day, monthlySchedule.days[day])\"\n" +
    "															data-ng-model=\"monthlySchedule.days[day]\"\n" +
    "															class=\"sc-dropdown-day-of-month-item sc-inline\"\n" +
    "															uib-btn-checkbox btn-checkbox-true=\"true\"\n" +
    "															btn-checkbox-false=\"false\">{{day | translate}}</div>\n" +
    "													</div>\n" +
    "												</ul>\n" +
    "											</div>\n" +
    "\n" +
    "										</td>\n" +
    "										<td class=\"sc-padding-cell-left sc-horizontal-center-div\"><span\n" +
    "											class=\"sc-inline sc-details-page-editor-label\">{{'at.label'|\n" +
    "												translate}} </td>\n" +
    "										<td></span> <uib-timepicker data-ng-model=\"monthlySchedule.time\"\n" +
    "												class=\"sc-inline sc-timepicker\" minute-step=\"1\"\n" +
    "												show-meridian=\"true\"></uib-timepicker></td>\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/Rules/ruleList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Rules/ruleList.html",
    "<div class=\"no margin sc-page-title\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'rules.label'| translate}}</div>\n" +
    "	</div>\n" +
    "	<div class=\"sc-page-title-right sc-title-button-box\">\n" +
    "		<a data-ng-click=\"createNewRule()\"\n" +
    "			class=\"sc-vertical-middle-div btn btn-default sc-btn-big\"\n" +
    "			id=\"sc-btn-create-rule\"> <i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{\"rule.button.create\"\n" +
    "			| translate}}\n" +
    "		</a>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "<div data-ng-if=\"ruleList.length\" class=\"sc-list-container\">\n" +
    "	<div data-ng-if=\"ruleList.length\" class=\"sc-operation\">\n" +
    "		<div id=\"sc-sort-by\" class=\"sc-inline\">\n" +
    "			<div class=\"sc-inline sc-sort-by-title\">{{\"sort.by.label\"|translate}}</div>\n" +
    "			<div\n" +
    "				class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "				uib-dropdown>\n" +
    "				<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "					data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "					<span class=\"sc-dropdown-btn-label\">{{'rule.sortBy.' +\n" +
    "						sortBy.code | translate}}</span> <span\n" +
    "						class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "				</button>\n" +
    "				<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "					aria-labelledby=\"\">\n" +
    "					<li role=\"menuitem\" data-ng-repeat=\"s in sortByFields\"\n" +
    "						data-ng-click=\"sortByField(s)\" class=\"sc-clickable-dropdown\">{{'rule.sortBy.'\n" +
    "						+ s.code | translate}}</li>\n" +
    "				</ul>\n" +
    "			</div>\n" +
    "			<div class=\"sc-inline sc-sort-by-title\">{{\"sort.order.label\"|translate}}</div>\n" +
    "			<div\n" +
    "				class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "				uib-dropdown>\n" +
    "				<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "					data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "					<span class=\"sc-dropdown-btn-label\">{{sortOrder.label}}</span> <span\n" +
    "						class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "				</button>\n" +
    "				<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "					aria-labelledby=\"\">\n" +
    "					<li role=\"menuitem\" data-ng-repeat=\"s in sortOrders\"\n" +
    "						data-ng-click=\"sortByOrder(s)\" class=\"sc-clickable-dropdown\">{{s.label}}</li>\n" +
    "				</ul>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<table class=\"sc-list-table table table-hover\">\n" +
    "		<thead>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr data-ng-repeat=\"rule in ruleList\">\n" +
    "				<td class=\"sc-rule-details-on-table\">\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 50%\" data-ng-click=\"openRule(rule)\">\n" +
    "						<p class=\"sc-bold-text-on-table\">{{rule.name}}</p>\n" +
    "						<p class=\"sc-grey-text-on-table sc-inline\"\n" +
    "							data-ng-if=\"rule.lastExecutionDate > 0 \">{{'last.executed'\n" +
    "							|translate}}</p>\n" +
    "						<p class=\"sc-inline\" data-ng-if=\"rule.lastExecutionDate > 0 \"><friendly-date data-date=\"{{rule.lastExecutionDate}}\"\n" +
    "							data-date-type=\"miliseconds\"\n" +
    "							data-full-format=\"d MMM yyyy, hh:mm a\"\n" +
    "							data-short-format=\"hh:mm a\"></friendly-date></p>\n" +
    "						<p class=\"sc-grey-text-on-table\"\n" +
    "							data-ng-if=\"rule.lastExecutionDate === 0 \">{{'never.executed'\n" +
    "							| translate}}</p>\n" +
    "					</div>\n" +
    "					<div data-ng-click=\"openRule(rule)\"\n" +
    "						class=\"sc-inline-block-on-table sc-clickable\">\n" +
    "						<button data-ng-show=\"rule.enabled\"\n" +
    "							class=\"sc-green sc-rule-status-btn btn btn-default sc-btn-no-border\">\n" +
    "							<i class=\"fa fa-check\"> {{'active.status.label' | translate}}</i>\n" +
    "						</button>\n" +
    "						<button data-ng-show=\"!rule.enabled\"\n" +
    "							class=\"sc-grey sc-rule-status-btn btn btn-default sc-btn-no-border\">\n" +
    "							<i class=\"fa fa-exclamation\"> {{'inactive.status.label' |\n" +
    "								translate}}</i>\n" +
    "						</button>\n" +
    "					</div>\n" +
    "					<div class=\"sc-rule-option-block\">\n" +
    "						<div class=\"sc-align-right\">\n" +
    "							<button\n" +
    "								class=\"btn btn-default sc-component-option sc-btn-icon-big sc-btn-bkgrd-hover sc-btn-no-bkgrd sc-btn-no-border\"\n" +
    "								data-ng-click=\"openOption(rule, !openOption(rule), $event)\"></button>\n" +
    "						</div>\n" +
    "						<div\n" +
    "							data-ng-include=\"'ui/app/Rules/partials/ruleoption-template.html'\"\n" +
    "							class=\"fade-in fade-out sc-option-large\"\n" +
    "							data-ng-show=\"isOptionOpen(rule)\"></div>\n" +
    "					</div>\n" +
    "				</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "\n" +
    "	<div data-ng-show=\"ruleList.length< total\">\n" +
    "		<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "			data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "			translate}}</button>\n" +
    "	</div>\n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "<div data-ng-if=\"!ruleList.length\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/SCServices/partials/extractorTemplate.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/partials/extractorTemplate.html",
    "<div data-ng-controller=\"extractorController\"\n" +
    "	class=\"sc-layout-full-height\">\n" +
    "\n" +
    "	<div class=\"sc-details-page-summary-div\">\n" +
    "		<a href=\"\" data-ng-click=\"scrollTo('extractorInfo')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('extractorInfo')}\">\n" +
    "				<label for=\"extractorInfo\"> {{\"component.summary.this.label\"\n" +
    "					| translate}} {{'extractor.label'|translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.name.length\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'extractor.summary.extractor.name.alt'\n" +
    "						| translate}}> </span> <span ng-hide=\"service.hostname.length\"\n" +
    "						class=\"sc-details-page-summary-content\">(<{{'extractor.summary.extractor.hostname.alt'\n" +
    "						| translate}}>) </span> <span class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.name.length\"> {{service.name}}</span> <span\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.hostname.length\"> ({{service.hostname}})</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('fileTypes')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('fileTypes')}\">\n" +
    "				<label for=\"fileTypes\"> {{\"component.summary.look.for.label\"\n" +
    "					| translate}} </label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.documentSizeLimits.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.component.file.types.alt'\n" +
    "						| translate}}> </span> <span\n" +
    "						ng-show=\"service.documentSizeLimits.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						data-ng-repeat=\"documentAssociation in service.documentSizeLimits | orderBy:'documentExtractor.extension'\">\n" +
    "						{{ ( ($index < service.documentSizeLimits.length-1) ?\n" +
    "						((documentAssociation.maxFileSize > 0) ?\n" +
    "						documentAssociation.documentExtractor.extension + \"(\" +\n" +
    "						documentAssociation.maxFileSize +\" MB), \" : \"\") :\n" +
    "						((documentAssociation.maxFileSize > 0) ?\n" +
    "						documentAssociation.documentExtractor.extension + \"(\" +\n" +
    "						documentAssociation.maxFileSize +\" MB)\" : \"\")) | uppercase }} </span> <label>{{\"component.summary.look.for.extensions.label\"\n" +
    "						| translate}} </label>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('executionWindow')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('executionWindow')}\">\n" +
    "				<label for=\"executionWindow\">\n" +
    "					{{\"component.summary.execution.windows.using.label\" | translate}} </label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.executionWindowSets.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.component.execution.windows.alt'\n" +
    "						| translate}}> </span> <span\n" +
    "						ng-show=\"service.executionWindowSets.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						data-ng-repeat=\"executionWindow in service.executionWindowSets\">\n" +
    "						{{executionWindow.name + (($index <\n" +
    "						service.executionWindowSets.length-1) ? \", \" : \"\")}} </span> <label>{{\"component.summary.execution.windows.label\"\n" +
    "						| translate}} </label>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('sync')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('sync')}\">\n" +
    "				<label for=\"sync\">\n" +
    "					{{\"component.summary.sync.frequency.label\" | translate}}</label>\n" +
    "				<div class=\"wrapper\"\n" +
    "					data-ng-show=\"serviceTempObject.configReloadIntervalTemp\">\n" +
    "					<span> {{serviceTempObject.configReloadIntervalTemp}}</span> <span>{{\n" +
    "						serviceTempObject.configReloadIntervalUnit + '.label' |\n" +
    "						translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div>\n" +
    "					<span data-ng-hide=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.interval.alt'\n" +
    "						| translate}}> </span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "		<div id=\"extractorInfo\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"extractor.editor.extractor.info.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"name\">{{\"component.editor.name.label\" |\n" +
    "					translate}}</label> <input id=\"name\"\n" +
    "					class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "					placeholder=\"{{'extractor.editor.name.place.holder' | translate}}\"\n" +
    "					data-ng-model=\"service.name\" name=\"extractorName\" required\n" +
    "					ng-maxlength=\"50\" max-length=\"50\">\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.extractorName.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.extractorName.$touched\">\n" +
    "					<p ng-message=\"maxlength\">{{\"extractor.editor.name.validation.max.length\"\n" +
    "						| translate}}</p>\n" +
    "					<p ng-message=\"required\">{{\"extractor.editor.name.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"hostname\">{{\"component.editor.hostname.label\" |\n" +
    "					translate}}</label>\n" +
    "				<p>{{service.hostname}}</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"jmsProfile\">{{\"component.editor.jmsProfile.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"jmsProfile\"\n" +
    "					class=\"sc-details-page-editor-data-right btn-group btn-block\"\n" +
    "					data-uib-dropdown>\n" +
    "					<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "						data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "						<span class=\"sc-dropdown-btn-label\">{{service.JMSProfile.displayName}}</span>\n" +
    "						<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "					</button>\n" +
    "					<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "						aria-labelledby=\"\">\n" +
    "						<li role=\"menuitem\" data-ng-repeat=\"jmsProfile in jmsList\"\n" +
    "							data-ng-click=\"changeJMSProfile(jmsProfile)\"\n" +
    "							class=\"sc-clickable-dropdown\">{{jmsProfile.displayName}}</li>\n" +
    "					</ul>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"extractor-thread-count\">{{\"extractor.editor.threadcount.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"extractor-thread-count\" numeric=\"{min:1,max:16}\"\n" +
    "					class=\"sc-number-input-sm sc-details-page-common-input\"\n" +
    "					name=\"extractorThreadCount\"\n" +
    "					data-ng-model=\"service.documentExtractorCount\" required></div>\n" +
    "\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.extractorThreadCount.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.extractorThreadCount.$touched\">\n" +
    "					<p ng-message=\"required\">{{\"extractor.editor.threadcount.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"heapSize\">{{\"extractor.minimum.heap.memory.size\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"heapSize\"\n" +
    "					class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "					data-uib-dropdown>\n" +
    "					<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "						data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "						<span class=\"sc-dropdown-btn-label\">{{service.minimumHeapMemory}}</span>\n" +
    "						<span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "					</button>\n" +
    "					<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "						aria-labelledby=\"\">\n" +
    "						<li role=\"menuitem\" data-ng-repeat=\"heapMemory in heapMemoryList\"\n" +
    "							data-ng-click=\"changeHeapMemory(heapMemory)\"\n" +
    "							class=\"sc-clickable-dropdown\">{{heapMemory}}</li>\n" +
    "					</ul>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"executionWindow\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.executionwindow.heading\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"executionWindows\"\n" +
    "					data-ng-if=\"!service.executionWindowSets.length\"\n" +
    "					class=\"sc-details-page-editor-subheading\">{{\"component.editor.executionwindow.subheading.empty\"\n" +
    "					| translate}}</label> <label for=\"executionWindows\"\n" +
    "					data-ng-if=\"service.executionWindowSets.length\"\n" +
    "					class=\"sc-details-page-editor-subheading\">{{\"component.editor.executionwindow.subheading\"\n" +
    "					| translate}}</label>\n" +
    "				<div>\n" +
    "					<div\n" +
    "						data-ng-repeat=\"(index, executionWindow) in service.executionWindowSets\">\n" +
    "						<div data-ng-class=\"getExecutionWindowRowClass(index)\"\n" +
    "							class=\"sc-details-page-editor-data-right sc-data-list-row\">\n" +
    "							<table style=\"width: 100%\">\n" +
    "								<tr>\n" +
    "									<td width=\"92%\">\n" +
    "										<div class=\"sc-inline\">\n" +
    "											<div>{{executionWindow.name}}</div>\n" +
    "											<div class=\"sc-grey-text-on-table\">{{returnScheduleString(executionWindow)}}</div>\n" +
    "										</div>\n" +
    "									</td>\n" +
    "									<td width=\" 8%\"><div\n" +
    "											class=\"sc-component-option-block fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"removeExecutionWindow(index)\"></div></td>\n" +
    "								</tr>\n" +
    "							</table>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<div\n" +
    "					class=\"sc-details-page-editor-data-right sc-execution-window-add\">\n" +
    "					<div id=\"executionWindows\" class=\"sc-full-width\" data-uib-dropdown>\n" +
    "\n" +
    "						<button class=\"form-control sc-button-common\"\n" +
    "							id=\"executionWindowDropDown\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{selectedExecutionWindowName}}</span>\n" +
    "							<span class=\"sc-dropdown-btn-expand-icon\" />\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-if=\"!serviceTempObject.associatedExecutionWindowSetsMap[executionWindow.id]\"\n" +
    "								data-ng-repeat=\"executionWindow in executionWindowCollection\"\n" +
    "								data-ng-click=\"addToExecutionWindowList(executionWindow)\"\n" +
    "								class=\"sc-clickable-dropdown\"><div>{{executionWindow.name}}</div>\n" +
    "								<div class=\"sc-grey-text-on-table\">{{returnScheduleString(executionWindow)}}</div></li>\n" +
    "							<div class=\"sc-horizontal-center-div\"\n" +
    "								data-ng-if=\"checkEmptyExecutionWindowList()\">{{'empty.list.msg'|translate}}</div>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"fileTypes\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.file.types.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label class=\"sc-details-page-editor-subheading\">{{\"extractor.editor.filetypes.subheading\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"sc-details-page-editor-extractorswitch\"\n" +
    "					data-ng-repeat=\"documentType in service.documentSizeLimits | orderBy:'documentExtractor.extension'\">\n" +
    "					<div data-ng-if=\"documentType.maxFileSize > 0\">\n" +
    "						<div class=\"sc-inline sc-details-page-documentType-block\">\n" +
    "							<!-- <span class=\"sc-policy-status-switch\"> <switch\n" +
    "								class=\"sc-switch\"\n" +
    "								data-ng-model=\"mapDocumentTypeAssociation[documentType.extension]\"\n" +
    "								data-ng-change=\"updateDocumentTypeAssociation(documentType.extension,mapDocumentTypeAssociation[documentType.extension])\">\n" +
    "							</switch>\n" +
    "						</span> -->\n" +
    "							<span class=\"sc-inline\">{{documentType.documentExtractor.extension}}\n" +
    "							</span>\n" +
    "						</div>\n" +
    "						<div class=\"sc-inline\">\n" +
    "							<span>{{\"extractor.editor.filetypes.maxfilesize.label\" |\n" +
    "								translate}}</span>\n" +
    "						</div>\n" +
    "						<div id=\"maximum-file-size\" name=\"maximum-file-size\"\n" +
    "							numeric=\"{min:1,max:25}\" class=\"sc-number-input-sm sc-inline\"\n" +
    "							name=\"maximumFileSize\" data-ng-model=\"documentType.maxFileSize\"\n" +
    "							required></div>\n" +
    "						<span class=\"sc-inline sc-label-margin-left-sm\">{{\"component.editor.MB.label\"\n" +
    "							| translate}}</span>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"sync\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.sync.frequency.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"configuration-reload-interval\">{{\"component.editor.configure.reload.interval.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"wrapper\" id=\"configuration-reload-interval\">\n" +
    "					<div numeric=\"{min:5}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						data-ng-show=\"serviceTempObject.configReloadIntervalUnit == 'min'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"\n" +
    "						ng-keyup=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"></div>\n" +
    "\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						data-ng-show=\"serviceTempObject.configReloadIntervalUnit == 'hour'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"\n" +
    "						ng-keyup=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"></div>\n" +
    "\n" +
    "\n" +
    "					<div\n" +
    "						class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "						data-uib-dropdown>\n" +
    "						<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{serviceTempObject.configReloadIntervalUnit\n" +
    "								+ '.label' | translate}}</span> <span\n" +
    "								class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('min', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'min.label' | translate}}</li>\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('hour', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'hour.label' | translate}}</li>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-validation-block\"\n" +
    "						ng-messages=\"serviceDetailsForm.configurationReloadInterval.$error\"\n" +
    "						ng-if=\"serviceDetailsForm.configurationReloadInterval.$touched\">\n" +
    "						<p ng-message=\"required\">{{\"service.editor.reload.interval.validation.required\"\n" +
    "							| translate}}</p>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("ui/app/SCServices/partials/ruleEngineTemplate.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/partials/ruleEngineTemplate.html",
    "<div data-ng-controller=\"ruleEngineController\"\n" +
    "	class=\"sc-layout-full-height\">\n" +
    "\n" +
    "	<div class=\"sc-details-page-summary-div\">\n" +
    "		<a href=\"\" data-ng-click=\"scrollTo('ruleEngineInfo')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('ruleEngineInfo')}\">\n" +
    "				<label for=\"ruleEngineInfo\">\n" +
    "					{{\"component.summary.this.label\" | translate}}\n" +
    "					{{'ruleEngine.label'|translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.name.length\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'ruleEngine.summary.ruleEngine.name.alt'\n" +
    "						| translate}}> </span> <span ng-hide=\"service.hostname.length\"\n" +
    "						class=\"sc-details-page-summary-content\">(<{{'ruleEngine.summary.ruleEngine.hostname.alt'\n" +
    "						| translate}}>) </span> <span class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.name.length\"> {{service.name}}</span> <span\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.hostname.length\"> ({{service.hostname}})</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('onDemandRuleFrequency')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('onDemandRuleFrequency')}\">\n" +
    "				<label for=\"onDemandRuleFrequency\">\n" +
    "					{{\"ruleEngine.summary.onDemandRule.frequency.label\" | translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span class=\"sc-details-page-summary-content\">{{serviceTempObject.onDemandIntervalTemp}}</span>\n" +
    "					<span>{{ serviceTempObject.onDemandIntervalUnit + '.label' |\n" +
    "						translate}}</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('scheduledRuleFrequency')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('scheduledRuleFrequency')}\">\n" +
    "				<label for=\"scheduledRuleFrequency\">\n" +
    "					{{\"ruleEngine.summary.scheduledRule.frequency.label\" | translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span class=\"sc-details-page-summary-content\">{{serviceTempObject.scheduledIntervalTemp}}</span>\n" +
    "					<span>{{ serviceTempObject.scheduledIntervalUnit + '.label'\n" +
    "						| translate}}</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('executionWindow')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('executionWindow')}\">\n" +
    "				<label for=\"executionWindow\">\n" +
    "					{{\"component.summary.execution.windows.using.label\" | translate}} </label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.executionWindowSets.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.component.execution.windows.alt'\n" +
    "						| translate}}> </span> <span\n" +
    "						ng-show=\"service.executionWindowSets.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						data-ng-repeat=\"executionWindow in service.executionWindowSets\">\n" +
    "						{{executionWindow.name + (($index <\n" +
    "						service.executionWindowSets.length-1) ? \", \" : \"\")}} </span> <label>{{\"component.summary.execution.windows.label\"\n" +
    "						| translate}} </label>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a><a href=\"\" data-ng-click=\"scrollTo('sync')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('sync')}\">\n" +
    "				<label for=\"sync\">\n" +
    "					{{\"component.summary.sync.frequency.label\" | translate}}</label>\n" +
    "				<div class=\"wrapper\"\n" +
    "					data-ng-show=\"serviceTempObject.configReloadIntervalTemp\">\n" +
    "					<span> {{serviceTempObject.configReloadIntervalTemp}}</span> <span>{{\n" +
    "						serviceTempObject.configReloadIntervalUnit + '.label' |\n" +
    "						translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div>\n" +
    "					<span data-ng-hide=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.interval.alt'\n" +
    "						| translate}}> </span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "		<div id=\"ruleEngineInfo\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"ruleEngine.editor.ruleEngine.info.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"name\">{{\"component.editor.name.label\" |\n" +
    "					translate}}</label> <input id=\"name\"\n" +
    "					class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "					placeholder=\"{{'ruleEngine.editor.name.place.holder' | translate}}\"\n" +
    "					data-ng-model=\"service.name\" name=\"ruleEngineName\" required\n" +
    "					ng-maxlength=\"50\" max-length=\"50\">\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.ruleEngineName.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.ruleEngineName.$touched\">\n" +
    "					<p ng-message=\"maxlength\">{{\"ruleEngine.editor.name.validation.max.length\"\n" +
    "						| translate}}</p>\n" +
    "					<p ng-message=\"required\">{{\"ruleEngine.editor.name.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"hostname\">{{\"component.editor.hostname.label\" |\n" +
    "					translate}}</label>\n" +
    "				<p>{{service.hostname}}</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"onDemandRuleExecutionFrequency\">{{\"ruleEngine.editor.onDemandRule.execution.frequency.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"wrapper\" id=\"onDemandRuleExecutionFrequency\">\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"onDemandRuleExecutionFrequency\"\n" +
    "						data-ng-show=\"serviceTempObject.onDemandIntervalUnit == 'second'\"\n" +
    "						data-ng-model=\"serviceTempObject.onDemandIntervalTemp\"\n" +
    "						data-ng-change=\"onDemandRuleIntervalEdit(serviceTempObject.onDemandIntervalTemp)\"\n" +
    "						ng-keyup=\"onDemandRuleIntervalEdit(serviceTempObject.onDemandIntervalTemp)\"></div>\n" +
    "\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"onDemandRuleExecutionFrequency\"\n" +
    "						data-ng-show=\"serviceTempObject.onDemandIntervalUnit == 'min'\"\n" +
    "						data-ng-model=\"serviceTempObject.onDemandIntervalTemp\"\n" +
    "						data-ng-change=\"onDemandRuleIntervalEdit(serviceTempObject.onDemandIntervalTemp)\"\n" +
    "						ng-keyup=\"onDemandRuleIntervalEdit(serviceTempObject.onDemandIntervalTemp)\"></div>\n" +
    "\n" +
    "\n" +
    "					<div\n" +
    "						class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "						data-uib-dropdown>\n" +
    "						<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{serviceTempObject.onDemandIntervalUnit\n" +
    "								+ '.label' | translate}}</span> <span\n" +
    "								class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeOnDemandRuleIntervalUnit('second', serviceTempObject.onDemandIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'second.label' |\n" +
    "								translate}}</li>\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeOnDemandRuleIntervalUnit('min', serviceTempObject.onDemandIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'min.label' | translate}}</li>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"ondemandrule-thread-count\">{{\"ruleEngine.editor.onDemandRules.threadcount.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"ondemandrule-thread-count\" numeric=\"{min:1,max:10}\"\n" +
    "					class=\"sc-number-input-sm sc-details-page-common-input\"\n" +
    "					name=\"onDemandRuleThreadCount\"\n" +
    "					data-ng-model=\"service.onDemandPoolSize\" required></div>\n" +
    "\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.onDemandRuleThreadCount.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.onDemandRuleThreadCount.$touched\">\n" +
    "					<p ng-message=\"required\">{{\"ruleEngine.editor.onDemandRules.threadcount.label.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"scheduledRuleExecutionFrequency\">{{\"ruleEngine.editor.scheduledRule.execution.frequency.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"wrapper\" id=\"scheduledRuleExecutionFrequency\">\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"scheduledRuleExecutionFrequency\"\n" +
    "						data-ng-show=\"serviceTempObject.scheduledIntervalUnit == 'second'\"\n" +
    "						data-ng-model=\"serviceTempObject.scheduledIntervalTemp\"\n" +
    "						data-ng-change=\"onScheduledRuleIntervalEdit(serviceTempObject.scheduledIntervalTemp)\"\n" +
    "						ng-keyup=\"onScheduledRuleIntervalEdit(serviceTempObject.scheduledIntervalTemp)\"></div>\n" +
    "\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"scheduledRuleExecutionFrequency\"\n" +
    "						data-ng-show=\"serviceTempObject.scheduledIntervalUnit == 'min'\"\n" +
    "						data-ng-model=\"serviceTempObject.scheduledIntervalTemp\"\n" +
    "						data-ng-change=\"onScheduledRuleIntervalEdit(serviceTempObject.scheduledIntervalTemp)\"\n" +
    "						ng-keyup=\"onScheduledRuleIntervalEdit(serviceTempObject.scheduledIntervalTemp)\"></div>\n" +
    "\n" +
    "\n" +
    "					<div\n" +
    "						class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "						data-uib-dropdown>\n" +
    "						<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{serviceTempObject.scheduledIntervalUnit\n" +
    "								+ '.label' | translate}}</span> <span\n" +
    "								class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeScheduledRuleIntervalUnit('second', serviceTempObject.scheduledIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'second.label' |\n" +
    "								translate}}</li>\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeScheduledRuleIntervalUnit('min', serviceTempObject.scheduledIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'min.label' | translate}}</li>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"scheduledRule-thread-count\">{{\"ruleEngine.editor.scheduledRule.threadcount.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"scheduledRule-thread-count\" numeric=\"{min:1,max:20}\"\n" +
    "					class=\"sc-number-input-sm sc-details-page-common-input\"\n" +
    "					name=\"scheduledRuleThreadCount\"\n" +
    "					data-ng-model=\"service.scheduledPoolSize\" required></div>\n" +
    "\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.scheduledRuleThreadCount.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.scheduledRuleThreadCount.$touched\">\n" +
    "					<p ng-message=\"required\">{{\"ruleEngine.editor.scheduledRules.threadcount.label.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"executionWindow\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.executionwindow.heading\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"executionWindows\"\n" +
    "					data-ng-if=\"!service.executionWindowSets.length\"\n" +
    "					class=\"sc-details-page-editor-subheading\">{{\"component.editor.executionwindow.subheading.empty\"\n" +
    "					| translate}}</label> <label for=\"executionWindows\"\n" +
    "					data-ng-if=\"service.executionWindowSets.length\"\n" +
    "					class=\"sc-details-page-editor-subheading\">{{\"component.editor.executionwindow.subheading\"\n" +
    "					| translate}}</label>\n" +
    "				<div>\n" +
    "					<div\n" +
    "						data-ng-repeat=\"(index, executionWindow) in service.executionWindowSets\">\n" +
    "						<div data-ng-class=\"getExecutionWindowRowClass(index)\"\n" +
    "							class=\"sc-details-page-editor-data-right sc-data-list-row\">\n" +
    "							<table style=\"width: 100%\">\n" +
    "								<tr>\n" +
    "									<td width=\"92%\">\n" +
    "										<div class=\"sc-inline\">\n" +
    "											<div>{{executionWindow.name}}</div>\n" +
    "											<div class=\"sc-grey-text-on-table\">{{returnScheduleString(executionWindow)}}</div>\n" +
    "										</div>\n" +
    "									</td>\n" +
    "									<td width=\" 8%\"><div\n" +
    "											class=\"sc-component-option-block fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "											data-ng-click=\"removeExecutionWindow(index)\"></div></td>\n" +
    "								</tr>\n" +
    "							</table>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<div\n" +
    "					class=\"sc-details-page-editor-data-right sc-execution-window-add\">\n" +
    "					<div id=\"executionWindows\" class=\"sc-full-width\" data-uib-dropdown>\n" +
    "\n" +
    "						<button class=\"form-control sc-button-common\"\n" +
    "							id=\"executionWindowDropDown\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{selectedExecutionWindowName}}</span>\n" +
    "							<span class=\"sc-dropdown-btn-expand-icon\" />\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-if=\"!serviceTempObject.associatedExecutionWindowSetsMap[executionWindow.id]\"\n" +
    "								data-ng-repeat=\"executionWindow in executionWindowCollection\"\n" +
    "								data-ng-click=\"addToExecutionWindowList(executionWindow)\"\n" +
    "								class=\"sc-clickable-dropdown\"><div>{{executionWindow.name}}</div>\n" +
    "								<div class=\"sc-grey-text-on-table\">{{returnScheduleString(executionWindow)}}</div></li>\n" +
    "							<div class=\"sc-horizontal-center-div\"\n" +
    "								data-ng-if=\"checkEmptyExecutionWindowList()\">{{'empty.list.msg'|translate}}</div>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"sync\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.sync.frequency.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"configuration-reload-interval\">{{\"component.editor.configure.reload.interval.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"wrapper\" id=\"configuration-reload-interval\">\n" +
    "					<div numeric=\"{min:5}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						data-ng-show=\"serviceTempObject.configReloadIntervalUnit == 'min'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"\n" +
    "						ng-keyup=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"></div>\n" +
    "\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						data-ng-show=\"serviceTempObject.configReloadIntervalUnit == 'hour'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"\n" +
    "						ng-keyup=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"></div>\n" +
    "\n" +
    "\n" +
    "					<div\n" +
    "						class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "						data-uib-dropdown>\n" +
    "						<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{serviceTempObject.configReloadIntervalUnit\n" +
    "								+ '.label' | translate}}</span> <span\n" +
    "								class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('min', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'min.label' | translate}}</li>\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('hour', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'hour.label' | translate}}</li>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-validation-block\"\n" +
    "						ng-messages=\"serviceDetailsForm.configurationReloadInterval.$error\"\n" +
    "						ng-if=\"serviceDetailsForm.configurationReloadInterval.$touched\">\n" +
    "						<p ng-message=\"required\">{{\"service.editor.reload.interval.validation.required\"\n" +
    "							| translate}}</p>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("ui/app/SCServices/partials/serviceoption-template.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/partials/serviceoption-template.html",
    "<div class=\"sc-btn-grp sc-inline\">\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Delete\" data-ng-click=\"delService($index, $event)\"><i class=\"sc-icon-trash\"></i></button>\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Edit\" data-ng-click=\"editService(service)\"><i class=\"fa fa-pencil\"></i></button>\n" +
    "</div>");
}]);

angular.module("ui/app/SCServices/partials/watcherTemplate.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/partials/watcherTemplate.html",
    "<div data-ng-controller=\"watcherController\"\n" +
    "	class=\"sc-layout-full-height\">\n" +
    "	<div class=\"sc-details-page-summary-div\">\n" +
    "		<a href=\"\" data-ng-click=\"scrollTo('watcherInfo')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('watcherInfo')}\">\n" +
    "				<label for=\"watcherInfo\"> {{\"component.summary.this.label\" |\n" +
    "					translate}} {{'watcher.label' | translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.name.length\"\n" +
    "						class=\"sc-details-page-summary-content\">\n" +
    "						<{{'watcher.summary.watcher.name.alt' | translate}}> </span> <span\n" +
    "						ng-hide=\"service.hostname.length\"\n" +
    "						class=\"sc-details-page-summary-content\">(<{{'watcher.summary.watcher.hostname.alt'\n" +
    "						| translate}}>) </span> <span class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.name.length\"> {{service.name}}</span> <span\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						ng-show=\"service.hostname.length\"> ({{service.hostname}})</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('fileTypes')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('fileTypes')}\">\n" +
    "				<label for=\"fileTypes\"> {{\"component.summary.look.for.label\"\n" +
    "					| translate}} </label>\n" +
    "				<!-- <input id=\"attributesInfo\" class=\"sc-details-page-left-summary-placeholder\"  placeholder=\"<{{'createResource.summary.placeholder.Conditions' | translate}}/>\" value=\"{{resource.attributes}}\" data-ng-readonly=\"true\"/> -->\n" +
    "				<div class=\"wrapper\">\n" +
    "					<span ng-hide=\"service.documentTypeAssociations.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.component.file.types.alt'\n" +
    "						| translate}}> </span> <span\n" +
    "						ng-show=\"service.documentTypeAssociations.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"\n" +
    "						data-ng-repeat=\"documentType in service.documentTypeAssociations | orderBy:'documentExtractor.extension'\">\n" +
    "						{{(documentType.include)?\n" +
    "						(documentType.documentExtractor.extension + (($index <\n" +
    "						watcher.documentTypeAssociations.length-1)? ' ': '')) : '' |\n" +
    "						uppercase }} </span> <label>{{\"component.summary.look.for.extensions.label\"\n" +
    "						| translate}} </label>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('directories')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('directories')}\">\n" +
    "				<label> {{\"watcher.summary.include.folders.label\" |\n" +
    "					translate}} </label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<div class=\"sc-details-page-summary-content\"\n" +
    "						data-ng-repeat=\"folder in service.repositoryFolders\">\n" +
    "						<label data-ng-show=\"$index > 0\"> {{\"and.label\" |\n" +
    "							translate}} </label> <span>{{folder.path}}</span>\n" +
    "						<div class=\"sc-details-page-summary-sub-content\">\n" +
    "							<label data-ng-show=\"folder.excludeRepositoryFolders.length > 0\">\n" +
    "								{{\"watcher.summary.exclude.folders.label\" | translate}} </label>\n" +
    "							<div class=\"sc-details-page-summary-content\"\n" +
    "								data-ng-repeat=\"sfolder in folder.excludeRepositoryFolders\">\n" +
    "								<span>{{ sfolder.path}}</span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "					<span data-ng-hide=\"service.repositoryFolders.length > 0\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'watcher.summary.watcher.included.folders.alt'\n" +
    "						| translate}}> </span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a> <a href=\"\" data-ng-click=\"scrollTo('sync')\">\n" +
    "			<div class=\"sc-details-page-summary-row\"\n" +
    "				data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('sync')}\">\n" +
    "				<label for=\"sync\">\n" +
    "					{{\"component.summary.sync.frequency.label\" | translate}}</label>\n" +
    "				<div class=\"wrapper\"\n" +
    "					data-ng-show=\"serviceTempObject.configReloadIntervalTemp\">\n" +
    "					<span> {{serviceTempObject.configReloadIntervalTemp}}</span> <span>{{\n" +
    "						serviceTempObject.configReloadIntervalUnit + '.label' |\n" +
    "						translate}}</span>\n" +
    "				</div>\n" +
    "				<div>\n" +
    "					<span data-ng-hide=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						class=\"sc-details-page-summary-content\"><{{'component.summary.interval.alt'\n" +
    "						| translate}}> </span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</a>\n" +
    "	</div>\n" +
    "	<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "		<div id=\"watcherInfo\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"watcher.editor.watcher.info.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"name\">{{\"component.editor.name.label\" |\n" +
    "					translate}}</label> <input id=\"name\"\n" +
    "					class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "					placeholder=\"{{'watcher.editor.name.place.holder' | translate}}\"\n" +
    "					data-ng-model=\"service.name\" name=\"watcherName\" required\n" +
    "					ng-maxlength=\"50\" max-length=\"50\">\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.watcherName.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.watcherName.$touched\">\n" +
    "					<p ng-message=\"maxlength\">{{\"watcher.editor.name.validation.max.length\"\n" +
    "						| translate}}</p>\n" +
    "					<p ng-message=\"required\">{{\"watcher.editor.name.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"hostname\">{{\"component.editor.hostname.label\" |\n" +
    "					translate}}</label> <span class=\"sc-details-page-editor-label\">{{service.hostname}}</span>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"jmsProfile\">{{\"component.editor.jmsProfile.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"jmsProfile\"\n" +
    "					class=\"sc-details-page-editor-data-right btn-group btn-block\"\n" +
    "					data-uib-dropdown>\n" +
    "					<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "						data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "						<span class=\"sc-dropdown-btn-label\">{{service.JMSProfile.displayName}}</span>\n" +
    "						<span class=\"sc-dropdown-btn-expand-icon\" />\n" +
    "					</button>\n" +
    "					<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "						aria-labelledby=\"\">\n" +
    "						<li role=\"menuitem\" data-ng-repeat=\"jmsProfile in jmsList\"\n" +
    "							data-ng-click=\"changeJMSProfile(jmsProfile)\"\n" +
    "							class=\"sc-clickable-dropdown\">{{jmsProfile.displayName}}</li>\n" +
    "					</ul>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label for=\"file-monitor-thread-count\">{{\"watcher.editor.file.monitor.thread.count.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div id=\"file-monitor-thread-count\" numeric=\"{min:1,max:10}\"\n" +
    "					class=\"sc-number-input-sm sc-details-page-common-input\"\n" +
    "					name=\"fileMonitorThreadCount\"\n" +
    "					data-ng-model=\"service.fileMonitorCount\" required />\n" +
    "\n" +
    "				<div class=\"sc-validation-block\"\n" +
    "					ng-messages=\"serviceDetailsForm.fileMonitorThreadCount.$error\"\n" +
    "					ng-if=\"serviceDetailsForm.fileMonitorThreadCount.$touched\">\n" +
    "					<p ng-message=\"required\">{{\"watcher.editor.file.monitor.thread.count.validation.required\"\n" +
    "						| translate}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"fileTypes\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.file.types.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<div class=\"sc-details-page-watcher-switch sc-inline\"\n" +
    "					data-ng-repeat=\"documentType in service.documentTypeAssociations | orderBy:'documentExtractor.extension'\">\n" +
    "					<span> <switch class=\"sc-switch sc-inline\"\n" +
    "							data-ng-model=\"documentType.include\" />\n" +
    "					</span> <span class=\"sc-inline\">{{documentType.documentExtractor.extension}}</span>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"directories\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"watcher.editor.directories.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label>{{\"watcher.editor.directories.context\" | translate}}</label>\n" +
    "				<div class=\"sc-watcher-include-directory-block\"\n" +
    "					data-ng-repeat=\"(index,inFolder) in service.repositoryFolders\">\n" +
    "					<div class=\"sc-watcher-include-directory\">\n" +
    "						<table style=\"width: 100%\">\n" +
    "							<tr>\n" +
    "								<td width=\"99%\"><input\n" +
    "									class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-full sc-details-page-common-input\"\n" +
    "									placeholder=\"{{'watcher.editor.include.place.holder' | translate}}\"\n" +
    "									data-ng-class=\"{ 'has-error': serviceDetailsForm['inFolder' + index].$dirty && serviceDetailsForm['inFolder' + index].$invalid}\"\n" +
    "									data-ng-model=\"inFolder.path\" name=\"inFolder{{index}}\" required\n" +
    "									uib-tooltip=\"{{getTooltipMessage(serviceDetailsForm['inFolder' + index])}}\"\n" +
    "									max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "									tooltip-trigger=\"mouseenter\" tooltip-placement=\"bottom\"></td>\n" +
    "								<td width=\"1%\" style=\"vertical-align: top\"><i\n" +
    "									class=\"fa fa-times-circle-o sc-component-del-icon\"\n" +
    "									data-ng-click=\"removeInFolder(index)\"> </i></td>\n" +
    "							</tr>\n" +
    "						</table>\n" +
    "					</div>\n" +
    "					<div class=\"sc-watcher-exclude-directories\">\n" +
    "						<table>\n" +
    "							<tr\n" +
    "								data-ng-repeat=\"(xindex, exFolder) in inFolder.excludeRepositoryFolders\">\n" +
    "								<td width=\"10%\"><span>{{'exclude.label' |\n" +
    "										translate}}</span></td>\n" +
    "								<td width=\"85%\"><input\n" +
    "									data-ng-change=\"checkExFolder(inFolder, exFolder)\"\n" +
    "									data-ng-class=\"{'has-error': serviceDetailsForm['exFolder' + index + '' +\n" +
    "									xindex].$dirty && serviceDetailsForm['exFolder' + index + '' +\n" +
    "									xindex].$invalid}\"\n" +
    "									class=\"sc-details-page-editor-input-placeholder\n" +
    "									sc-details-page-editor-data-full sc-details-page-common-input\"\n" +
    "									placeholder=\"{{'watcher.editor.exclude.place.holder' |\n" +
    "									translate}}\"\n" +
    "									data-ng-model=\"exFolder.path\"\n" +
    "									name=\"exFolder{{index}}{{xindex}}\" required\n" +
    "									uib-tooltip=\"{{getTooltipMessage(serviceDetailsForm['exFolder' + index + '' +\n" +
    "									xindex])}}\"\n" +
    "									max-length=\"1000\" ng-maxlength=\"1000\"\n" +
    "									tooltip-trigger=\"mouseenter\" tooltip-placement=\"bottom\">\n" +
    "								<td width=\"5%\"><div\n" +
    "										class=\"fa fa-trash-o sc-bigger-icon sc-inline\"\n" +
    "										data-ng-click=\"removeExcludeFolder(xindex, inFolder)\"></div></td>\n" +
    "							</tr>\n" +
    "\n" +
    "						</table>\n" +
    "						<a data-ng-click=\"addExcludeFolder(inFolder)\"\n" +
    "							class=\"btn btn-default\"> <i class=\"fa fa-plus uppercase\" />&nbsp;&nbsp;{{\"watcher.exclude.directory.new.button\"\n" +
    "							| translate}}\n" +
    "						</a>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<a data-ng-click=\"addIncludeFolder()\" class=\"btn btn-default\"> <i\n" +
    "					class=\"fa fa-plus uppercase\"> </i>&nbsp;&nbsp;{{\"watcher.include.directory.new.button\"\n" +
    "					| translate}}\n" +
    "				</a>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div id=\"sync\" class=\"sc-details-page-editor-title\">\n" +
    "			<span class=\"sc-details-page-title-underline\">\n" +
    "				{{\"component.editor.sync.frequency.label\" | translate}}</span>\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-editor-info\">\n" +
    "			<div class=\"sc-details-page-editor-data\">\n" +
    "				<label>{{\"component.editor.configure.reload.interval.label\"\n" +
    "					| translate}}</label>\n" +
    "				<div class=\"wrapper\">\n" +
    "					<div numeric=\"{min:5}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						id=\"configurationReloadInterval\"\n" +
    "						data-ng-if=\"serviceTempObject.configReloadIntervalUnit == 'min'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\"\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\" \n" +
    "						ng-keyup = \"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\"/>\n" +
    "\n" +
    "					<div numeric=\"{min:1}\"\n" +
    "						class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "						name=\"configurationReloadInterval\" required\n" +
    "						id=\"configurationReloadInterval\"\n" +
    "						data-ng-if=\"serviceTempObject.configReloadIntervalUnit == 'hour'\"\n" +
    "						data-ng-model=\"serviceTempObject.configReloadIntervalTemp\" ng-k\n" +
    "						data-ng-change=\"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\" \n" +
    "						ng-keyup = \"onConfigReloadIntervalEdit(serviceTempObject.configReloadIntervalTemp)\" />\n" +
    "\n" +
    "\n" +
    "					<div\n" +
    "						class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "						data-uib-dropdown>\n" +
    "						<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "							data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "							<span class=\"sc-dropdown-btn-label\">{{\n" +
    "								serviceTempObject.configReloadIntervalUnit + '.label' |\n" +
    "								translate}}</span> <span class=\"sc-dropdown-btn-expand-icon\" />\n" +
    "						</button>\n" +
    "						<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "							aria-labelledby=\"\">\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('min', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'min.label' | translate}}</li>\n" +
    "							<li role=\"menuitem\"\n" +
    "								data-ng-click=\"changeConfigReloadIntervalUnit('hour', serviceTempObject.configReloadIntervalTemp)\"\n" +
    "								class=\"sc-clickable-dropdown\">{{'hour.label' | translate}}</li>\n" +
    "						</ul>\n" +
    "					</div>\n" +
    "					<div class=\"sc-validation-block\"\n" +
    "						ng-messages=\"serviceDetailsForm.configurationReloadInterval.$error\"\n" +
    "						ng-if=\"serviceDetailsForm.configurationReloadInterval.$touched\">\n" +
    "						<p ng-message=\"required\">{{\"service.editor.reload.interval.validation.required\"\n" +
    "							| translate}}</p>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>");
}]);

angular.module("ui/app/SCServices/serviceDetails.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/serviceDetails.html",
    "<div data-ng-if=\"!detailsFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'service.details.not.found' |\n" +
    "		translate}}</p>\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class= \"sc-vertical-middle-div\">{{'data.loading' | translate}}</p>\n" +
    "</div>\n" +
    "<div class=\"sc-resource\" data-ng-if=\"detailsFound\" style=\"height: 100%\">\n" +
    "	<form id=\"serviceDetailsForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"serviceDetailsForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\" data-ng-click=\"backToServiceList(serviceDetailsForm)\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-title\">\n" +
    "				<div class=\"sc-vertical-middle-div\">\n" +
    "					<p class=\"sc-details-page-edit-text\">{{\"edit.label\" |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-subject\">{{service.name}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-details-page-discard-btn\"\n" +
    "					data-ng-click=\"discardServiceChanges(serviceDetailsForm)\">{{\"button.discard\"\n" +
    "					| translate}} </a> <a class=\"btn btn-default sc-btn-save-page\"\n" +
    "					data-ng-click=\"saveServiceChanges(serviceDetailsForm)\">{{\"button.save\"\n" +
    "					| translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\"\n" +
    "			data-ng-include=\"'ui/app/SCServices/partials/' + serviceType + 'Template.html'\"></div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/SCServices/serviceList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/SCServices/serviceList.html",
    "<div class=\"no margin sc-page-title\" data-ng-if=\"serviceList.length\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{title}}</div>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "<div data-ng-if=\"serviceList.length\" class=\"sc-list-container\">\n" +
    "	<div data-ng-if=\"serviceList.length\" class=\"sc-operation\">\n" +
    "		<!-- <div id=\"sc-sort-by\" class=\"sc-inline\">\n" +
    "		<div class=\"sc-inline sc-sort-by-title\">{{\"sort.by.label\"|translate}}</div>\n" +
    "		<div\n" +
    "			class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "			uib-dropdown>\n" +
    "			<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "				data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "				<span class=\"sc-dropdown-btn-label\">{{sortBy.label}}</span> <span\n" +
    "					class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "			</button>\n" +
    "			<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "				aria-labelledby=\"\">\n" +
    "				<li role=\"menuitem\" data-ng-repeat=\"s in sortByFields\"\n" +
    "					data-ng-click=\"sortByField(s)\" class=\"sc-clickable-dropdown\">{{s.label}}</li>\n" +
    "			</ul>\n" +
    "		</div>\n" +
    "		<div class=\"sc-inline sc-sort-by-title\">{{\"sort.order.label\"|translate}}</div>\n" +
    "		<div\n" +
    "			class=\"sc-sort-by-select sc-inline btn-group btn-block sc-search-status\"\n" +
    "			uib-dropdown>\n" +
    "			<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "				data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "				<span class=\"sc-dropdown-btn-label\">{{sortOrder.label}}</span> <span\n" +
    "					class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "			</button>\n" +
    "			<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "				aria-labelledby=\"\">\n" +
    "				<li role=\"menuitem\" data-ng-repeat=\"s in sortOrders\"\n" +
    "					data-ng-click=\"sortByOrder(s)\" class=\"sc-clickable-dropdown\">{{s.label}}</li>\n" +
    "			</ul>\n" +
    "		</div>\n" +
    "	</div> -->\n" +
    "	</div>\n" +
    "\n" +
    "	<table class=\"sc-list-table table table-hover\">\n" +
    "		<thead>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr data-ng-repeat=\"service in serviceList\">\n" +
    "				<td class=\"no-padding-important sc-component-status-on-table\" data-ng-class=\"getServiceClass(service)\">\n" +
    "				</td>\n" +
    "				<td class=\"sc-component-details-on-table\">\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 30%\" data-ng-click=\"openService(service)\">\n" +
    "						<p class=\"sc-bold-text-on-table\">{{service.name}}</p>\n" +
    "						<p class=\"sc-grey-text-on-table\">{{service.hostname}}</p>\n" +
    "					</div>\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 30%\" data-ng-click=\"openService(service)\">\n" +
    "						<p class=\"sc-grey-text-on-table\">{{'services.last.sync.at' |\n" +
    "							translate}}</p>\n" +
    "						<friendly-date data-date=\"{{service.configLoadedOn}}\"\n" +
    "							data-date-type=\"miliseconds\"\n" +
    "							data-full-format=\"d MMM yyyy, hh:mm a\"\n" +
    "							data-short-format=\"hh:mm a\"></friendly-date>\n" +
    "<!-- 						<p data-ng-bind=\"service.configLoadedOn | date: getDateFormat()\"></p> -->\n" +
    "					</div>\n" +
    "					<div data-ng-click=\"openService(service)\"\n" +
    "						class=\"sc-inline-block-on-table sc-clickable\">\n" +
    "						<p class=\"sc-grey-text-on-table\">{{'services.sync.frequency' |\n" +
    "							translate}}</p>\n" +
    "						<p\n" +
    "							data-ng-show=\"service.configReloadInterval >=3600 && service.configReloadInterval %3600 == 0\">{{service.configReloadInterval/3600}}\n" +
    "							{{'hour.label' | translate}}</p>\n" +
    "						<p\n" +
    "							data-ng-show=\"service.configReloadInterval < 3600 || service.configReloadInterval % 3600 != 0\">{{service.configReloadInterval/60}}\n" +
    "							{{'min.label' | translate}}</p>\n" +
    "					</div>\n" +
    "					<div class=\"sc-service-option-block\" data-ng-hide=\"true\">\n" +
    "						<div class=\"sc-align-right\">\n" +
    "							<button\n" +
    "								class=\"btn btn-default sc-component-option sc-btn-icon-big sc-btn-bkgrd-hover sc-btn-no-bkgrd sc-btn-no-border\"\n" +
    "								data-ng-click=\"openOption(service, !openOption(service), $event)\"></button>\n" +
    "						</div>\n" +
    "						<div\n" +
    "							data-ng-include=\"'ui/app/SCServices/partials/serviceoption-template.html'\"\n" +
    "							class=\"fade-in fade-out sc-option\"\n" +
    "							data-ng-show=\"isOptionOpen(service)\"></div>\n" +
    "					</div>\n" +
    "				</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "\n" +
    "	<div data-ng-show=\"serviceList.length< total\">\n" +
    "		<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "			data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "			translate}}</button>\n" +
    "	</div>\n" +
    "\n" +
    "	<!-- <div class=\"sc-service-pagination\" data-ng-show=\"false\">\n" +
    "		<ul class=\"pagination\">\n" +
    "\n" +
    "			<li data-ng-class=\"{'disabled':servicePageNumber == 1}\"><a\n" +
    "				data-ng-click=\"previousPage()\">&laquo;</a></li>\n" +
    "			<li><a data-ng-repeat=\"index in paginator\"\n" +
    "				data-ng-click=\"goToPage(index)\">{{index}}</a></li>\n" +
    "			<li data-ng-class=\"{'disabled':servicePageNumber == numberOfPage}\"><a\n" +
    "				data-ng-click=\"nextPage()\">&raquo;</a></li>\n" +
    "		</ul>\n" +
    "	</div> -->\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div \">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div data-ng-if=\"!serviceList.length\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/ExecutionWindow/executionWindowDetails.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/ExecutionWindow/executionWindowDetails.html",
    "<div data-ng-if=\"!detailsFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'executionWindow.details.not.found'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div\">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div class=\"sc-resource\" data-ng-if=\"detailsFound\" style=\"height: 100%\">\n" +
    "	<form id=\"executionWindowDetailsForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"executionWindowDetailsForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\"\n" +
    "					data-ng-click=\"backToExecutionWindowList(executionWindowDetailsForm)\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-title\">\n" +
    "				<div class=\"sc-vertical-middle-div\">\n" +
    "					<p class=\"sc-details-page-edit-text\" data-ng-if = \"executionWindowID !='create'\">{{\"edit.label\" |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-text\" data-ng-if = \"executionWindowID =='create'\">{{\"create.label\" |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-subject\">{{executionWindow.name}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-details-page-discard-btn\"\n" +
    "					data-ng-click=\"discardExecutionWindowChanges(executionWindowDetailsForm)\">{{\"button.discard\"\n" +
    "					| translate}} </a> <a class=\"btn btn-default sc-btn-save-page\"\n" +
    "					data-ng-click=\"saveExecutionWindowChanges(executionWindowDetailsForm)\">{{\"button.save\"\n" +
    "					| translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-details-page-summary-div\">\n" +
    "				<a href=\"\" data-ng-click=\"scrollTo('executionWindowInfo')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('executionWindowInfo')}\">\n" +
    "						<label> {{\"component.summary.this.label\" | translate}} {{'executionWindow.label' | translate}}</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span ng-hide=\"executionWindow.name.length\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'executionWindow.summary.name.alt'\n" +
    "								| translate}}> </span> <span class=\"sc-details-page-summary-content\"\n" +
    "								ng-show=\"executionWindow.name.length\">\n" +
    "								{{executionWindow.name}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('validity')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('validity')}\">\n" +
    "						<label> {{\"component.summary.valid.from.label\" |\n" +
    "							translate}} </label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{validity.validFrom | date:'dd/MM/yyyy'}}</span>\n" +
    "						</div>\n" +
    "						<label data-ng-show=\"validity.expiringOnOption === 'never'\">\n" +
    "							{{\"component.summary.never.expires.label\" | translate}} </label> <label\n" +
    "							data-ng-show=\"validity.expiringOnOption === 'specific'\">\n" +
    "							{{\"component.summary.expiring.on.label\" | translate}} </label>\n" +
    "						<div class=\"wrapper\"\n" +
    "							data-ng-show=\"validity.expiringOnOption === 'specific'\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{validity.expiringOn | date:'dd/MM/yyyy'}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('schedule')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('schedule')}\">\n" +
    "						<label> {{\"component.summary.schedule.label\" | translate}}\n" +
    "						</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span class=\"sc-details-page-summary-content\">\n" +
    "								{{getScheduleAsText()}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "				<div id=\"executionWindowInfo\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"executionWindow.editor.info.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"executionWindowName\">{{\"component.editor.name.label\"\n" +
    "							| translate}}</label> <input id=\"executionWindowName\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'executionWindow.editor.name.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"executionWindow.name\" name=\"executionWindowName\"\n" +
    "							required ng-maxlength=\"50\" max-length=\"50\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"executionWindowDetailsForm.executionWindowName.$error\"\n" +
    "							ng-if=\"executionWindowDetailsForm.executionWindowName.$touched\">\n" +
    "							<p ng-message=\"maxlength\">{{\"executionWindow.editor.name.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"required\">{{\"executionWindow.editor.name.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"description\">{{\"executionWindow.editor.description.label\"\n" +
    "							| translate}}</label>\n" +
    "						<textarea id=\"description\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-component-text-area sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'executionWindow.editor.description.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"executionWindow.description\" name=\"description\"></textarea>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "\n" +
    "				<div id=\"validity\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"executionWindow.editor.validity.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"validFrom\" class=\"sc-label-inline\">{{\"executionWindow.editor.valid.from.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div name=\"validFrom\" id=\"validFrom\" class=\"sc-inline\">\n" +
    "\n" +
    "							<div class=\"sc-date-picker\">\n" +
    "								<p class=\"input-group\">\n" +
    "									<input type=\"text\" class=\"form-control\" name=\"validFrom\"\n" +
    "										id=\"validFrom\" uib-datepicker-popup=\"dd/MM/yyyy\"\n" +
    "										data-ng-model=\"validity.validFrom\"\n" +
    "										is-open=\"validFromPopUp.opened\" show-button-bar=\"true\"\n" +
    "										close-text=\"{{'close.label'|translate}}\"\n" +
    "										current-text=\"{{'today.label'|translate}}\"\n" +
    "										clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "										class=\"input-group-btn\">\n" +
    "										<button type=\"button\" class=\"btn btn-default\"\n" +
    "											data-ng-click=\"validFromOpen()\">\n" +
    "											<i class=\"fa fa-calendar\"></i>\n" +
    "										</button>\n" +
    "									</span>\n" +
    "								</p>\n" +
    "							</div>\n" +
    "							<div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"executionWindowDetailsForm.validFrom.$error\"\n" +
    "								ng-if=\"executionWindowDetailsForm.validFrom.$touched\">\n" +
    "								<p ng-message=\"required\">{{\"executionWindow.editor.valid.from.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "					</div>\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "\n" +
    "						<label class=\"sc-label-inline\">{{\"executionWindow.editor.expiring.on.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-inline\">\n" +
    "							<md-radio-group data-ng-model=\"validity.expiringOnOption\">\n" +
    "							<md-radio-button value=\"specific\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">{{'executionWindow.editor.specific.date.label'\n" +
    "							| translate}}</md-radio-button> <!-- <md-radio-button value=\"xdays\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">\n" +
    "							{{'executionWindow.editor.after.x.days.label'| translate}} </md-radio-button> -->\n" +
    "							<md-radio-button value=\"never\"\n" +
    "								class=\"md-primary sc-radio-inline sc-inline\">\n" +
    "							{{'executionWindow.editor.never.label'| translate}} </md-radio-button></md-radio-group>\n" +
    "							<div data-ng-show=\"validity.expiringOnOption == 'specific'\"\n" +
    "								class=\"sc-date-picker\">\n" +
    "								<p class=\"input-group\">\n" +
    "									<input type=\"text\" class=\"form-control\" name=\"expiringOn\"\n" +
    "										id=\"expiringOn\" uib-datepicker-popup=\"dd/MM/yyyy\"\n" +
    "										data-ng-model=\"validity.expiringOn\"\n" +
    "										is-open=\"expiringOnPopUp.opened\"\n" +
    "										ng-required=\"expiringOnOption =='specific'\"\n" +
    "										show-button-bar=\"true\"\n" +
    "										close-text=\"{{'close.label'|translate}}\"\n" +
    "										current-text=\"{{'today.label'|translate}}\"\n" +
    "										clear-text=\"{{'clear.label'|translate}}\" /> <span\n" +
    "										class=\"input-group-btn\">\n" +
    "										<button type=\"button\" class=\"btn btn-default\"\n" +
    "											data-ng-click=\"expiringOnOpen()\">\n" +
    "											<i class=\"fa fa-calendar\"></i>\n" +
    "										</button>\n" +
    "									</span>\n" +
    "								</p>\n" +
    "							</div>\n" +
    "							<!-- <div data-ng-show=\"expiringOnOption == 'xdays'\">\n" +
    "								<div numeric=\"{min:1}\" required=\"expiringOnOption == 'xdays'\"\n" +
    "									name=\"expiringOn1\" id=\"expiringOn1\"\n" +
    "									class=\"sc-number-input sc-details-page-common-input sc-inline\"\n" +
    "									data-ng-model=\"afterXDays\"></div>\n" +
    "								<span class=\"sc-inline sc-details-page-editor-label\">{{'day.label'\n" +
    "									| translate}}</span>\n" +
    "							</div> -->\n" +
    "							<div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"executionWindowDetailsForm.expiringOn.$error\"\n" +
    "								ng-if=\"executionWindowDetailsForm.expiringOn.$touched && expiringOnOption == 'specific' && executionWindowDetailsForm.expiringOn.$error\">\n" +
    "								<p ng-message=\"required\">{{\"executionWindow.editor.expiring.on.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div>\n" +
    "							<!-- <div class=\"sc-validation-block\"\n" +
    "								ng-messages=\"executionWindowDetailsForm.expiringOn1.$error\"\n" +
    "								ng-if=\"executionWindowDetailsForm.expiringOn1.$touched && expiringOnOption == 'xdays' && executionWindowDetailsForm.expiringOn1.$error\">\n" +
    "								<p ng-message=\"required\">{{\"executionWindow.editor.expiring.on.validation.required\"\n" +
    "									| translate}}</p>\n" +
    "							</div> -->\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "				<div id=\"schedule\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"executionWindow.editor.schedule.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"schedule\">{{\"executionWindow.editor.execute.rule.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"sc-grey-div\">\n" +
    "							<md-radio-group data-ng-model=\"executionWindow.scheduleType\">\n" +
    "							<md-radio-button value=\"D\"\n" +
    "								class=\"md-primary sc-radio-inline-with-background sc-inline\">{{'daily.label'\n" +
    "							| translate}}</md-radio-button> <md-radio-button value=\"W\"\n" +
    "								class=\"md-primary sc-radio-inline-with-background sc-inline\">\n" +
    "							{{'weekly.label'| translate}} </md-radio-button> </md-radio-group>\n" +
    "						</div>\n" +
    "						<div class=\"sc-border-div\"\n" +
    "							data-ng-show=\"executionWindow.scheduleType == 'D'\">\n" +
    "							<table class=\"sc-schedule-table-daily\">\n" +
    "								<tr data-ng-repeat=\"dailySchedule in dailySchedules\"\n" +
    "									class=\"sc-table-row\">\n" +
    "									<td class=\"sc-padding-cell-left\"><span\n" +
    "										class=\"sc-inline sc-details-page-editor-label\">{{'starts.at.label'|\n" +
    "											translate}} </td>\n" +
    "									<td class=\"\"></span> <uib-timepicker\n" +
    "											data-ng-model=\"dailySchedule.start\"\n" +
    "											class=\"sc-inline sc-timepicker\" hour-step=\"1\" minute-step=\"1\"\n" +
    "											show-meridian=\"true\" data-ng-change=\"checkEndTime(dailySchedule)\"></uib-timepicker></td>\n" +
    "									<td class=\"sc-padding-cell-left\"><span\n" +
    "										class=\"sc-inline sc-details-page-editor-label\">{{'ends.at.label'|\n" +
    "											translate}} </td>\n" +
    "									<td></span> <uib-timepicker data-ng-model=\"dailySchedule.end\"\n" +
    "											class=\"sc-inline sc-timepicker\"\n" +
    "											data-ng-change=\"checkEndTime(dailySchedule)\" hour-step=\"1\"\n" +
    "											minute-step=\"1\" show-meridian=\"true\"></uib-timepicker>\n" +
    "										<div class=\"sc-validation-block-on-table\"\n" +
    "											data-ng-if=\"!dailySchedule.validEndTime\">{{'executionWindow.schedule.validation.end.time'\n" +
    "											| translate}}</div></td>\n" +
    "								</tr>\n" +
    "							</table>\n" +
    "						</div>\n" +
    "						<div class=\"sc-border-div\"\n" +
    "							data-ng-show=\"executionWindow.scheduleType == 'W'\">\n" +
    "							<div data-ng-repeat=\"weeklySchedule in weeklySchedules\">\n" +
    "\n" +
    "								<div class=\"sc-schedule-execution-weekdays\">\n" +
    "									<div class=\"btn-group\">\n" +
    "										<div class=\"btn btn-primary\"\n" +
    "											data-ng-repeat=\"day in dayOfTheWeek\"\n" +
    "											data-ng-model=\"weeklySchedule.days[day]\" uib-btn-checkbox\n" +
    "											btn-checkbox-true=\"true\" btn-checkbox-false=\"false\">{{day\n" +
    "											+ \".label\"| translate}}</div>\n" +
    "									</div>\n" +
    "\n" +
    "								</div>\n" +
    "								<table class=\"sc-schedule-execution-table-weekly\">\n" +
    "									<tr>\n" +
    "										<td class=\"sc-padding-cell-left sc-horizontal-center-div\"><span\n" +
    "											class=\"sc-inline sc-details-page-editor-label\">{{'starts.at.label'|\n" +
    "												translate}} </td>\n" +
    "										<td></span> <uib-timepicker data-ng-model=\"weeklySchedule.start\"\n" +
    "												class=\"sc-inline sc-timepicker\" hour-step=\"1\"\n" +
    "												minute-step=\"1\" show-meridian=\"true\" data-ng-change=\"checkEndTime(weeklySchedule)\"></uib-timepicker></td>\n" +
    "										<td class=\"sc-padding-cell-left sc-horizontal-center-div\"><span\n" +
    "											class=\"sc-inline sc-details-page-editor-label\">{{'ends.at.label'|\n" +
    "												translate}} </td>\n" +
    "										<td></span> <uib-timepicker data-ng-model=\"weeklySchedule.end\"\n" +
    "												class=\"sc-inline sc-timepicker\"\n" +
    "												data-ng-change=\"checkEndTime(weeklySchedule)\" hour-step=\"1\"\n" +
    "												minute-step=\"1\" show-meridian=\"true\"></uib-timepicker>\n" +
    "											<div class=\"sc-validation-block-on-table\"\n" +
    "												data-ng-if=\"!weeklySchedule.validEndTime\">{{'executionWindow.schedule.validation.end.time'\n" +
    "												| translate}}</div></td>\n" +
    "\n" +
    "									</tr>\n" +
    "								</table>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "	</form>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/ExecutionWindow/executionWindowList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/ExecutionWindow/executionWindowList.html",
    "<div class=\"no margin sc-page-title\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'executionWindows.label'|\n" +
    "			translate}}</div>\n" +
    "	</div>\n" +
    "	<div class=\"sc-page-title-right sc-title-button-box\">\n" +
    "		<a data-ng-click=\"createNewExecutionWindow()\"\n" +
    "			class=\"sc-vertical-middle-div btn btn-default sc-btn-big\"\n" +
    "			id=\"sc-btn-create-ExecutionWindow\"> <i\n" +
    "			class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{\"executionWindow.button.create\"\n" +
    "			| translate}}\n" +
    "		</a>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "<div data-ng-if=\"executionWindowList.length\" class=\"sc-list-container\">\n" +
    "\n" +
    "	<div data-ng-if=\"executionWindowList.length\" class=\"sc-operation\"></div>\n" +
    "	<table class=\"sc-list-table table table-hover\">\n" +
    "		<thead>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr data-ng-repeat=\"executionWindow in executionWindowList\">\n" +
    "				<td class=\"sc-component-details-on-table\">\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 80%\"\n" +
    "						data-ng-click=\"openExecutionWindow(executionWindow)\">\n" +
    "						<p class=\"sc-bold-text-on-table\">{{executionWindow.name}}</p>\n" +
    "						<p class=\"sc-grey-text-on-table\">{{returnScheduleString(executionWindow)}}</p>\n" +
    "					</div>\n" +
    "					<div class=\"sc-executionWindow-option-block\">\n" +
    "						<div class=\"sc-align-right\">\n" +
    "							<button\n" +
    "								class=\"btn btn-default sc-component-option sc-btn-icon-big sc-btn-bkgrd-hover sc-btn-no-bkgrd sc-btn-no-border\"\n" +
    "								data-ng-click=\"openOption(executionWindow, !openOption(executionWindow), $event)\"></button>\n" +
    "						</div>\n" +
    "						<div\n" +
    "							data-ng-include=\"'ui/app/Settings/ExecutionWindow/partials/executionwindowoption-template.html'\"\n" +
    "							class=\"fade-in fade-out sc-option\"\n" +
    "							data-ng-show=\"isOptionOpen(executionWindow)\"></div>\n" +
    "					</div>\n" +
    "				</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "\n" +
    "	<div data-ng-show=\"executionWindowList.length< total\">\n" +
    "		<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "			data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "			translate}}</button>\n" +
    "	</div>\n" +
    "\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div \">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div data-ng-if=\"!executionWindowList.length\"\n" +
    "	class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/ExecutionWindow/partials/executionwindowoption-template.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/ExecutionWindow/partials/executionwindowoption-template.html",
    "<div class=\"sc-btn-grp sc-inline\">\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Delete\" data-ng-click=\"deleteExecutionWindow(executionWindow, $event)\"><i class=\"sc-icon-trash\"></i></button>\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Edit\" data-ng-click=\"openExecutionWindow(executionWindow)\"><i class=\"fa fa-pencil\"></i></button>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/General/generalSettings.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/General/generalSettings.html",
    "<form id=\"settingsForm\"\n" +
    "	class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "	novalidate=\"\" name=\"settingsForm\">\n" +
    "	<div class=\"no margin sc-page-title\">\n" +
    "		<div class=\"sc-page-title-left\">\n" +
    "			<div class=\"sc-vertical-middle-div\">{{'settings.title'|\n" +
    "				translate}}</div>\n" +
    "		</div>\n" +
    "		<div class=\"sc-page-title-right sc-title-button-box\">\n" +
    "			<a data-ng-click=\"resetSettings()\"\n" +
    "				class=\"sc-vertical-middle-div btn btn-default sc-setting-reset\">\n" +
    "				<!-- <i\n" +
    "			class=\"fa fa-undo\"></i>&nbsp;&nbsp; -->{{\"button.discard\" |\n" +
    "				translate}}\n" +
    "			</a> <a data-ng-click=\"saveAll(settingsForm)\"\n" +
    "				class=\"sc-vertical-middle-div btn btn-default sc-setting-save\">\n" +
    "				<!-- <i\n" +
    "			class=\"fa fa-save\"></i>&nbsp;&nbsp; --> {{\"button.save.all\" |\n" +
    "				translate}}\n" +
    "			</a>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "	<div data-ng-if=\"settingList.length\" class=\"sc-list-container\">\n" +
    "		<div style=\"margin-top: 3%\">\n" +
    "			<div class=\"sc-setting-group\"\n" +
    "				data-ng-repeat=\"(index,settingGroup) in settingList\">\n" +
    "				<table width=\"100%\" class=\"sc-setting-header-table\">\n" +
    "					<tr>\n" +
    "						<td width=\"30%\" class=\"sc-setting-group-name\">{{settingGroup.name}}</td>\n" +
    "						<td width=\"70%\" class=\"sc-setting-group-des\">{{settingGroup.description}}</td>\n" +
    "					</tr>\n" +
    "				</table>\n" +
    "				<div class=\"sc-details-page-editor-data\">\n" +
    "					<table class=\"sc-setting-table\">\n" +
    "						<tr\n" +
    "							data-ng-repeat=\"(sindex,setting) in settingGroup.systemConfigs\">\n" +
    "							<td width=\"35%\" class=\"sc-component-details-on-table\"><p\n" +
    "									class=\"sc-bold-text-on-table\">{{setting.label}}</p>\n" +
    "								<p class=\"sc-grey-text-on-table\">{{setting.identifier}}</p></td>\n" +
    "							<td width=\"35%\"><input\n" +
    "								class=\"sc-details-page-editor-input-placeholder sc-details-page-common-input sc-full-width\"\n" +
    "								data-ng-model=\"setting.value\"\n" +
    "								data-ng-change=\"onSettingChange(setting.id)\"\n" +
    "								name=\"{{'setting-' + index + '' + sindex}}\" required\n" +
    "								data-ng-class=\"{'has-error': settingsForm['setting-' + index + '' + sindex].$dirty && settingsForm['setting-' + index + '' + sindex].$invalid}\"\n" +
    "								uib-tooltip=\"{{getTooltipMessage(settingsForm['setting-' + index + '' + sindex])}}\"\n" +
    "								max-length=\"300\" ng-maxlength=\"300\"\n" +
    "								tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[settingsForm['setting-' + index + '' + sindex].$invalid]}}\"\n" +
    "								tooltip-placement=\"bottom\"\n" +
    "								placeholder=\"{{'setting.value.place.holder'| translate}}\"></td>\n" +
    "							<td width=\"20%\" style=\"text-align: center\"><span> <switch\n" +
    "										class=\"sc-switch sc-inline\"\n" +
    "										data-ng-change=\"onSettingChange(setting.id)\"\n" +
    "										data-ng-model=\"setting.encrypt\"></switch>\n" +
    "							</span><span class=\"sc-inline\">{{'encrypt.label'|translate}}</span></td>\n" +
    "							<td width=\"10%\" style=\"text-align: center\"><a\n" +
    "								class=\"btn btn-default sc-setting-save\"\n" +
    "								data-ng-show=\"isDirty[setting.id]\"\n" +
    "								data-ng-click=\"modifyGeneralSetting(setting, settingsForm['setting-' + index + '' + sindex])\">\n" +
    "									{{'button.save' | translate}} </a></td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "	</div>\n" +
    "\n" +
    "	<div data-ng-if=\"!settingList.length\"\n" +
    "		class=\"sc-empty-resource-list-box\">\n" +
    "		<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "	</div>\n" +
    "</form>");
}]);

angular.module("ui/app/Settings/JMS/jmsDetails.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/JMS/jmsDetails.html",
    "<div data-ng-if=\"!detailsFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'jms.details.not.found' |\n" +
    "		translate}}</p>\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div\">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div class=\"sc-resource\" data-ng-if=\"detailsFound\" style=\"height: 100%\">\n" +
    "	<form id=\"jmsDetailsForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"jmsDetailsForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\" data-ng-click=\"backToJMSList(jmsDetailsForm)\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-title\">\n" +
    "				<div class=\"sc-vertical-middle-div\">\n" +
    "					<p class=\"sc-details-page-edit-text\">{{\"edit.label\" |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-subject\">{{jms.displayName}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-details-page-discard-btn\"\n" +
    "					data-ng-click=\"discardJMSChanges(jmsDetailsForm)\">{{\"button.discard\"\n" +
    "					| translate}} </a> <a class=\"btn btn-default sc-btn-save-page\"\n" +
    "					data-ng-click=\"saveJMSChanges(jmsDetailsForm)\">{{\"button.save\"\n" +
    "					| translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\"></div>\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-details-page-summary-div\">\n" +
    "				<a href=\"\" data-ng-click=\"scrollTo('jmsInfo')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('jmsInfo')}\">\n" +
    "						<label for=\"jmsInfo\"> {{\"component.summary.this.label\" |\n" +
    "							translate}} {{'jms.label'|translate}}</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span ng-hide=\"jms.displayName.length\"\n" +
    "								class=\"sc-details-page-summary-content\"><{{'jms.summary.jms.name.alt'\n" +
    "								| translate}}> </span> <span class=\"sc-details-page-summary-content\"\n" +
    "								ng-show=\"jms.displayName.length\"> {{jms.displayName}}</span>\n" +
    "						</div>\n" +
    "						<label for=\"jmsInfo\" style=\"margin-top: 10px\">{{'jms.summary.type.label'|\n" +
    "							translate}}</label>\n" +
    "						<div>\n" +
    "							<span>{{'jms.' + jms.type | translate}}</span>\n" +
    "						</div>\n" +
    "						<label for=\"jmsInfo\" style=\"margin-top: 10px\">{{'jms.summary.provider.url.label'|\n" +
    "							translate}}</label>\n" +
    "						<div>\n" +
    "							<span>{{jms.providerURL}}</span>\n" +
    "							<span data-ng-if = \"!jms.providerURL\"><{{'jms.summary.provider.url.alt' | translate}}></span>\n" +
    "						</div>\n" +
    "						<label for=\"jmsInfo\" style=\"margin-top: 10px\">{{'jms.summary.retry.interval.label'|\n" +
    "							translate}}</label>\n" +
    "						<div>\n" +
    "							<span>{{jms.connectionRetryInterval}}</span>\n" +
    "							<span data-ng-if = \"!jms.connectionRetryInterval\"><{{'jms.summary.retry.interval.alt' | translate}}></span>\n" +
    "							<span data-ng-if = \"jms.connectionRetryInterval\">{{'second.label'|translate}}</span>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "				<div id=\"jmsInfo\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"jms.editor.jms.info.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"jmsName\">{{\"component.editor.name.label\" |\n" +
    "							translate}}</label> <input id=\"jmsName\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.name.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.displayName\" name=\"jmsName\" required\n" +
    "							ng-maxlength=\"50\" max-length=\"50\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.jmsName.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.jmsName.$touched\">\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.name.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.name.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"description\">{{\"component.editor.description.label\"\n" +
    "							| translate}}</label>\n" +
    "						<textarea id=\"description\" ng-maxlength=\"1000\" max-length=\"1000\"\n" +
    "							class=\"\n" +
    "							sc-details-page-editor-input-placeholder sc-component-text-area\n" +
    "							sc-details-page-editor-data-right\n" +
    "							sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.description.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.description\" name=\"description\"></textarea>\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.description.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.description.$touched\">\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.description.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"type\">{{\"component.editor.type.label\" |\n" +
    "							translate}}</label>\n" +
    "						<div id=\"type\"\n" +
    "							class=\"sc-details-page-editor-data-right-small btn-group btn-block\"\n" +
    "							data-uib-dropdown>\n" +
    "							<button class=\"form-control sc-button-common\" id=\"\" type=\"button\"\n" +
    "								data-uib-dropdown-toggle ng-disabled=\"disabled\">\n" +
    "								<span class=\"sc-dropdown-btn-label\">{{'jms.' + jms.type |\n" +
    "									translate}}</span> <span class=\"sc-dropdown-btn-expand-icon\"></span>\n" +
    "							</button>\n" +
    "							<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "								aria-labelledby=\"\">\n" +
    "								<li role=\"menuitem\" data-ng-repeat=\"jmsType in jmsTypes\"\n" +
    "									data-ng-click=\"changeJMSType(jmsType)\"\n" +
    "									class=\"sc-clickable-dropdown\">{{'jms.' + jmsType |\n" +
    "									translate}}</li>\n" +
    "							</ul>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"providerURL\">{{\"jms.editor.provider.url.label\"\n" +
    "							| translate}}</label> <input id=\"providerURL\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.provider.url.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.providerURL\" name=\"providerURL\" required\n" +
    "							max-length=\"250\" ng-maxlength=\"250\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.providerURL.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.providerURL.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.provider.url.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.provider.url.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"initialContextFactory\">{{\"jms.editor.initial.context.label\"\n" +
    "							| translate}}</label> <input id=\"initialContextFactory\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.initial.context.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.initialContextFactory\"\n" +
    "							name=\"initialContextFactory\" required max-length=\"250\"\n" +
    "							ng-maxlength=\"250\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.initialContextFactory.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.initialContextFactory.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.initial.context.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.initial.context.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"urlPackagePrefix\">{{\"jms.editor.url.package.prefix.label\"\n" +
    "							| translate}}</label> <input id=\"urlPackagePrefix\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.url.package.prefix.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.URLPackagePrefix\" name=\"urlPackagePrefix\"\n" +
    "							max-length=\"250\" ng-maxlength=\"250\" required>\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.urlPackagePrefix.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.urlPackagePrefix.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.url.package.prefix.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.url.package.prefix.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"connectionFactory\">{{\"jms.editor.connection.factory.label\"\n" +
    "							| translate}}</label> <input id=\"connectionFactory\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.connection.factory.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.connectionFactory\" name=\"connectionFactory\"\n" +
    "							max-length=\"250\" ng-maxlength=\"250\" required>\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.connectionFactory.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.connectionFactory.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.connection.factory.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.connection.factory.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"serviceName\">{{\"jms.editor.service.name.label\"\n" +
    "							| translate}}</label> <input id=\"serviceName\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'jms.editor.service.name.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"jms.serviceName\" name=\"serviceName\" required\n" +
    "							max-length=\"250\" ng-maxlength=\"250\">\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.serviceName.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.serviceName.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.service.name.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "							<p ng-message=\"maxlength\">{{\"jms.editor.service.name.validation.max.length\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"connectionRetryInterval\">{{\"component.editor.connection.retry.interval.label\"\n" +
    "							| translate}}</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<div numeric=\"{min:5}\" required\n" +
    "								class=\"sc-number-input-sm sc-details-page-common-input sc-inline\"\n" +
    "								name=\"connectionRetryInterval\" id=\"connectionRetryInterval\"\n" +
    "								data-ng-model=\"jms.connectionRetryInterval\"></div>\n" +
    "							<span class=\"sc-inline sc-details-page-editor-label\">{{'second.label'\n" +
    "								| translate}}</span>\n" +
    "						</div>\n" +
    "						<div class=\"sc-validation-block\"\n" +
    "							ng-messages=\"jmsDetailsForm.connectionRetryInterval.$error\"\n" +
    "							ng-if=\"jmsDetailsForm.connectionRetryInterval.$touched\">\n" +
    "							<p ng-message=\"required\">{{\"jms.editor.connection.retry.interval.validation.required\"\n" +
    "								| translate}}</p>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/Settings/JMS/jmsList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/JMS/jmsList.html",
    "<div class=\"no margin sc-page-title\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'JMS'| translate}}</div>\n" +
    "	</div>\n" +
    "	<div class=\"sc-page-title-right sc-title-button-box\">\n" +
    "		<a data-ng-click=\"createNewJMS()\" data-ng-hide = \"true\"\n" +
    "			class=\"sc-vertical-middle-div btn btn-default sc-btn-big\"\n" +
    "			id=\"sc-btn-create-JMS\"> <i class=\"fa fa-plus uppercase\"></i>&nbsp;&nbsp;{{\"jms.button.create\"\n" +
    "			| translate}}\n" +
    "		</a>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div data-ng-if=\"jmsList.length\" class=\"sc-list-container\">\n" +
    "	<div data-ng-if=\"jmsList.length\" class=\"sc-operation\"></div>\n" +
    "	<table class=\"sc-list-table table table-hover\">\n" +
    "		<thead>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr data-ng-repeat=\"jms in jmsList\">\n" +
    "				<td class=\"sc-component-details-on-table\">\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 30%\" data-ng-click=\"openJMS(jms)\">\n" +
    "						<p class=\"sc-bold-text-on-table\">{{jms.displayName}}</p>\n" +
    "						<p class=\"sc-grey-text-on-table\">{{'jms.' + jms.type |\n" +
    "							translate}}</p>\n" +
    "					</div>\n" +
    "					<div class=\"sc-jms-option-block\" data-ng-hide = \"true\">\n" +
    "						<div class=\"sc-align-right\">\n" +
    "							<button\n" +
    "								class=\"btn btn-default sc-component-option sc-btn-icon-big sc-btn-bkgrd-hover sc-btn-no-bkgrd sc-btn-no-border\"\n" +
    "								data-ng-click=\"openOption(jms, !openOption(jms), $event)\"></button>\n" +
    "						</div>\n" +
    "						<div\n" +
    "							data-ng-include=\"'ui/app/Settings/JMS/partials/jmsoption-template.html'\"\n" +
    "							class=\"fade-in fade-out sc-option\"\n" +
    "							data-ng-show=\"isOptionOpen(jms)\"></div>\n" +
    "					</div>\n" +
    "				</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "\n" +
    "	<div data-ng-show=\"jmsList.length< total\">\n" +
    "		<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "			data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "			translate}}</button>\n" +
    "	</div>\n" +
    "\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div \">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div data-ng-if=\"!jmsList.length\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/JMS/partials/jmsoption-template.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/JMS/partials/jmsoption-template.html",
    "<div class=\"sc-btn-grp sc-inline\">\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Delete\" data-ng-click=\"deleteJMS(jms, $event)\"><i class=\"sc-icon-trash\"></i></button>\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Edit\" data-ng-click=\"openJMS(jms)\"><i class=\"fa fa-pencil\"></i></button>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/License/license.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/License/license.html",
    "<div class=\"no margin sc-page-title\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'license.title'|\n" +
    "			translate}}</div>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div data-ng-if=\"licenseFound\">\n" +
    "	<div class=\"sc-license-wrapper\">\n" +
    "		<table width=\"100%\" class=\"sc-license-expiry-table\">\n" +
    "			<tr>\n" +
    "				<td width=\"50%\"><p class=\"sc-license-heading\">{{'expiry.date.label'\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-license-data\">{{expiryDate | date}}</p></td>\n" +
    "				<td width=\"50%\"><p class=\"sc-license-heading\"\n" +
    "						data-ng-if=\"getLicenseRemainingDays() >= 0\">{{'license.expiring.in.label'\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-license-heading\"\n" +
    "						data-ng-if=\"getLicenseRemainingDays() < 0\">{{'license.expired.label'\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-license-data\" data-ng-class=\"getExpiryClass()\">{{abs(getLicenseRemainingDays())}}</p>\n" +
    "					<p class=\"sc-license-context\">{{'days.label' | translate}}</td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "	</div>\n" +
    "	<div class=\"sc-license-wrapper\">\n" +
    "		<table width=\"100%\" class=\"sc-license-usage-table\">\n" +
    "			<tr>\n" +
    "				<td width=\"50%\">\n" +
    "					<p class=\"sc-license-heading\">{{'usage.label' | translate}}</p>\n" +
    "					<p class=\"sc-license-data\">{{usedSize}} GB/{{dataSize}} GB</p>\n" +
    "				</td>\n" +
    "				<td width=\"50%\"><p class=\"sc-license-heading\"\n" +
    "						data-ng-if=\"getRemaining() < 0\">{{'license.exceeded.label' |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-license-heading\" data-ng-if=\"getRemaining() >= 0\">{{'license.remaining.label'\n" +
    "						| translate}}</p>\n" +
    "					<p class=\"sc-license-data\" data-ng-class=\"getRemainingClass()\">{{getRemaining()}}</p>\n" +
    "					<span class=\"sc-license-context\">GB</span></td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "\n" +
    "	</div>\n" +
    "</div>\n" +
    "<div data-ng-if=\"!licenseFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'license.not.found.label' |\n" +
    "		translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/Plugins/partials/pluginoption-template.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/Plugins/partials/pluginoption-template.html",
    "<div class=\"sc-btn-grp sc-inline\">\n" +
    "  <button class=\"btn btn-default sc-btn sc-btn-icon-big\" title=\"Edit\" data-ng-click=\"openPlugin(plugins)\"><i class=\"fa fa-pencil\"></i></button>\n" +
    "</div>");
}]);

angular.module("ui/app/Settings/Plugins/pluginDetails.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/Plugins/pluginDetails.html",
    "<div data-ng-if=\"!detailsFound\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'plugins.details.not.found' |\n" +
    "		translate}}</p>\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div\">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div class=\"sc-resource\" data-ng-if=\"detailsFound\" style=\"height: 100%\">\n" +
    "	<form id=\"pluginsDetailsForm\"\n" +
    "		class=\"sc-layout-full-height ng-pristine ng-valid ng-valid-required ng-vaid-maxlength\"\n" +
    "		novalidate=\"\" name=\"pluginsDetailsForm\">\n" +
    "		<div class=\"no-margin sc-details-page-title-panel\">\n" +
    "			<div class=\"sc-details-page-back-btn-div \">\n" +
    "				<button class=\"sc-details-page-back-btn btn btn-default\"\n" +
    "					type=\"button\" data-ng-click=\"backToPluginsList(pluginsDetailsForm)\">\n" +
    "					<i class=\"fa fa-arrow-left\" data-ng-class=\"\"> </i>\n" +
    "				</button>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-title\">\n" +
    "				<div class=\"sc-vertical-middle-div\">\n" +
    "					<p class=\"sc-details-page-edit-text\">{{\"edit.label\" |\n" +
    "						translate}}</p>\n" +
    "					<p class=\"sc-details-page-edit-subject\">{{plugins.name}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"sc-details-page-btn-div\">\n" +
    "				<a class=\"btn btn-default sc-details-page-discard-btn\"\n" +
    "					data-ng-click=\"discardPluginChanges(pluginsDetailsForm)\">{{\"button.discard\"\n" +
    "					| translate}} </a> <a class=\"btn btn-default sc-btn-save-page\"\n" +
    "					data-ng-click=\"savePluginChanges(pluginsDetailsForm)\">{{\"button.save\"\n" +
    "					| translate}} </a>\n" +
    "			</div>\n" +
    "			<div class=\"clear\" />\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		<div class=\"sc-details-page-bottom-panel\">\n" +
    "			<div class=\"sc-details-page-summary-div\">\n" +
    "				<a href=\"\" data-ng-click=\"scrollTo('pluginsInfo')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('pluginsInfo')}\">\n" +
    "						<label for=\"pluginsInfo\"> {{\"component.summary.this.label\"\n" +
    "							| translate}} {{'plugin.label'|translate}}</label>\n" +
    "						<div class=\"wrapper\">\n" +
    "							<span ng-hide=\"plugins.name.length\"\n" +
    "								class=\"sc-details-page-summary-content\">\n" +
    "								<{{'plugins.summary.plugins.name.alt' | translate}}> </span> <span\n" +
    "								class=\"sc-details-page-summary-content\"\n" +
    "								ng-show=\"plugins.name.length\"> {{plugins.name}}</span>\n" +
    "						</div>\n" +
    "\n" +
    "					</div>\n" +
    "				</a> <a href=\"\" data-ng-click=\"scrollTo('properties')\">\n" +
    "					<div class=\"sc-details-page-summary-row\"\n" +
    "						data-ng-class=\"{'sc-details-page-summary-row-selected':isActive('properties')}\">\n" +
    "						<label for=\"properties\"> {{\"plugin.summary.has.properties\"\n" +
    "							| translate}} </label>\n" +
    "						<div class=\"wrapper sc-details-page-summary-sub-content\">\n" +
    "							<div class=\"sc-details-page-summary-content\"\n" +
    "								data-ng-repeat=\"(index, param) in plugins.params\">\n" +
    "								<span>{{param.label}}</span><span class=\"sc-summary-context\">\n" +
    "									{{'matching.condition.equals'|translate}}</span><span\n" +
    "									data-ng-if=\"param.value\"> {{param.value}}</span><span\n" +
    "									data-ng-if=\"!param.value\"> <{{'value.label'\n" +
    "									|translate}}></span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</a>\n" +
    "			</div>\n" +
    "			<div class=\"sc-details-page-editor-div\">\n" +
    "\n" +
    "				<div id=\"pluginsInfo\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"plugin.editor.plugin.info.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"hostname\">{{\"component.editor.name.label\" |\n" +
    "							translate}}</label>\n" +
    "						<p>{{plugins.name}}</p>\n" +
    "					</div>\n" +
    "\n" +
    "					<div class=\"sc-details-page-editor-data\">\n" +
    "						<label for=\"description\">{{\"component.editor.description.label\"\n" +
    "							| translate}}</label>\n" +
    "						<textarea id=\"description\"\n" +
    "							class=\"sc-details-page-editor-input-placeholder sc-component-text-area sc-details-page-editor-data-right sc-details-page-common-input\"\n" +
    "							placeholder=\"{{'plugins.editor.description.place.holder' | translate}}\"\n" +
    "							data-ng-model=\"plugins.description\" name=\"description\" />\n" +
    "					</div>\n" +
    "				</div>\n" +
    "\n" +
    "				<div id=\"properties\" class=\"sc-details-page-editor-title\">\n" +
    "					<span class=\"sc-details-page-title-underline\">\n" +
    "						{{\"plugins.editor.plugin.properties.label\" | translate}}</span>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"sc-details-page-editor-info\">\n" +
    "\n" +
    "					<table class=\"sc-properties-table\">\n" +
    "						<thead>\n" +
    "						</thead>\n" +
    "						<tbody>\n" +
    "							<tr data-ng-repeat=\"(index, param) in plugins.params\"\n" +
    "								class=\"sc-properties-row\">\n" +
    "								<td class=\"sc-plugin-details-on-table\">\n" +
    "									<div id=\"parameterName\" class=\"sc-inline-block-on-table\"\n" +
    "										style=\"width: 24%\">\n" +
    "										<p class=\"sc-plugin-property-heading\">{{'parameter.name' |\n" +
    "											translate}}</p>\n" +
    "										<input disabled=\"true\"\n" +
    "											class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "											type=\"text\" data-ng-model=\"param.label\" readonly />\n" +
    "									</div>\n" +
    "									<div id=\"parameterIdentifier\" class=\"sc-inline-block-on-table\"\n" +
    "										style=\"width: 24%\">\n" +
    "										<p class=\"sc-plugin-property-heading\">{{'parameter.identifier'\n" +
    "											| translate}}</p>\n" +
    "										<input disabled=\"true\"\n" +
    "											class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "											type=\"text\" data-ng-model=\"param.identifier\" readonly />\n" +
    "									</div>\n" +
    "									<div id=\"parameterDatatype\" class=\"sc-inline-block-on-table\"\n" +
    "										style=\"width: 24%\">\n" +
    "										<p class=\"sc-plugin-property-heading\">{{'parameter.datatype'\n" +
    "											| translate}}</p>\n" +
    "										<input disabled=\"true\"\n" +
    "											class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "											type=\"text\" data-ng-model=\"param.dataType\" readonly />\n" +
    "									</div>\n" +
    "\n" +
    "									<div id=\"parameterValue\" class=\"sc-inline-block-on-table\"\n" +
    "										style=\"width: 24%\">\n" +
    "										<p class=\"sc-plugin-property-heading\">{{'parameter.value'\n" +
    "											| translate}}</p>\n" +
    "										<div>\n" +
    "\n" +
    "											<input name=\"{{'booleanValue-' + index}}\"\n" +
    "												placeholder=\"{{'fixed.action.plugin.param.place.holder.boolean'| translate}}\"\n" +
    "												data-ng-if=\"param.dataType === 'Boolean'\" type=\"text\"\n" +
    "												data-ng-model=\"param.value\"\n" +
    "												data-ng-class=\"{'has-error': pluginsDetailsForm['booleanValue-' + index].$dirty && pluginsDetailsForm['booleanValue-' + index].$invalid}\"\n" +
    "												uib-tooltip=\"{{getTooltipMessage(pluginsDetailsForm['booleanValue-' + index])}}\"\n" +
    "												tooltip-trigger=\"{{{true: 'mouseenter', false:\n" +
    "									'never'}[pluginsDetailsForm['booleanValue-' + index].$invalid]}}\"\n" +
    "												tooltip-placement=\"bottom\"\n" +
    "												uib-typeahead=\"bool for bool in [true, false] | filter:$viewValue\"\n" +
    "												class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "												typeahead-show-hint=\"true\" typeahead-min-length=\"0\"\n" +
    "												ng-maxlength=\"50\" max-length=\"50\" typeahead-editable=\"false\"\n" +
    "												typeahead-focus-first required />\n" +
    "											<!-- 												<div \n" +
    "												class=\"sc-plugin-properties-value-button btn-group btn-block\"\n" +
    "												data-uib-dropdown data-ng-if=\"param.dataType == 'Boolean'\">\n" +
    "																										<button\n" +
    "													class=\"form-control sc-button-common sc-button-properties\"\n" +
    "													id=\"\" type=\"button\" style=\"width: 100%; padding: 3px 12px;\"\n" +
    "													data-uib-dropdown-toggle ng-disabled=\"disabled\"\n" +
    "													uib-tooltip=\"{{ param.value ? '' : 'This is a required field' }}\"\n" +
    "													tooltip-trigger=\"mouseenter\" tooltip-placement=\"bottom\">\n" +
    "														<span\n" +
    "														class=\"sc-dropdown-btn-label sc-button-properties-value\">{{param.value}}</span>\n" +
    "														<span\n" +
    "														class=\"sc-dropdown-btn-expand-icon sc-button-properties-dropdown-expand-icon\" />\n" +
    "													</button>\n" +
    "													<ul class=\"uib-dropdown-menu sc-dropdown-menu\" role=\"menu\"\n" +
    "													aria-labelledby=\"\">\n" +
    "														<li role=\"menuitem\" data-ng-repeat=\"bool in [true, false]\"\n" +
    "														data-ng-click=\"changeBooleanValue(bool, index)\"\n" +
    "														class=\"sc-clickable-dropdown-properties\">{{bool}}</li>\n" +
    "													</ul>\n" +
    "												</div> -->\n" +
    "\n" +
    "											<input name=\"{{'stringValue-' + index}}\"\n" +
    "												id=\"{{'stringValue-' + index}}\"\n" +
    "												class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "												type=\"text\" data-ng-model=\"param.value\"\n" +
    "												data-ng-if=\"param.dataType === 'String'\"\n" +
    "												data-ng-class=\"{'has-error': pluginsDetailsForm['stringValue-' + index].$dirty && pluginsDetailsForm['stringValue-' + index].$invalid}\"\n" +
    "												uib-tooltip=\"{{getTooltipMessage(pluginsDetailsForm['stringValue-' + index])}}\"\n" +
    "												tooltip-trigger=\"{{{true: 'mouseenter', false:\n" +
    "									'never'}[pluginsDetailsForm['stringValue-' + index].$invalid]}}\"\n" +
    "												tooltip-placement=\"bottom\"\n" +
    "												placeholder=\"{{'fixed.action.plugin.param.place.holder.string'| translate}}\"\n" +
    "												ng-maxlength=\"1024\" max-length=\"1024\" required /> <input\n" +
    "												name=\"{{'integerValue-' + index}}\"\n" +
    "												id=\"{{'integerValue-' + index}}\"\n" +
    "												data-ng-if=\"param.dataType === 'Integer'\"\n" +
    "												ng-class=\"{'has-error': pluginsDetailsForm['integerValue-' + index].$dirty && pluginsDetailsForm['integerValue-' + index].$invalid}\"\n" +
    "												class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "												data-ng-model=\"param.value\"\n" +
    "												uib-tooltip=\"{{getTooltipMessage(pluginsDetailsForm['integerValue-' + index])}}\"\n" +
    "												tooltip-trigger=\"{{{true: 'mouseenter', false:\n" +
    "									'never'}[pluginsDetailsForm['integerValue-' + index].$invalid]}}\"\n" +
    "												tooltip-placement=\"bottom\" ui-number-mask=\"0\"\n" +
    "												ui-hide-group-sep ui-negative-number\n" +
    "												placeholder=\"{{'fixed.action.plugin.param.place.holder.int'| translate}}\"\n" +
    "												ng-maxlength=\"50\" max-length=\"50\" required /> <input\n" +
    "												name=\"{{'numberValue-' + index}}\"\n" +
    "												id=\"{{'numberValue-' + index}}\" ui-number-mask=\"0\"\n" +
    "												ui-negative-number data-ng-if=\"param.dataType === 'Number'\"\n" +
    "												ui-hide-group-sep\n" +
    "												data-ng-class=\"{'has-error': pluginsDetailsForm['numberValue-' + index].$dirty && pluginsDetailsForm['numberValue-' + index].$invalid}\"\n" +
    "												class=\"sc-plugins-editor-properties-input-placeholder form-control\"\n" +
    "												data-ng-model=\"param.value\"\n" +
    "												uib-tooltip=\"{{getTooltipMessage(pluginsDetailsForm['numberValue-' + index])}}\"\n" +
    "												tooltip-trigger=\"{{{true: 'mouseenter', false:\n" +
    "									'never'}[pluginsDetailsForm['numberValue-' + index].$invalid]}}\"\n" +
    "												tooltip-placement=\"bottom\"\n" +
    "												placeholder=\"{{'fixed.action.plugin.param.place.holder.number'| translate}}\"\n" +
    "												ng-maxlength=\"50\" max-length=\"50\" required />\n" +
    "\n" +
    "										</div>\n" +
    "									</div>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "						</tbody>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("ui/app/Settings/Plugins/pluginList.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/Settings/Plugins/pluginList.html",
    "<div class=\"no margin sc-page-title\" data-ng-if=\"pluginsList.length\">\n" +
    "	<div class=\"sc-page-title-left\">\n" +
    "		<div class=\"sc-vertical-middle-div\">{{'plugins.label'| translate}}</div>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "<div data-ng-if=\"pluginsList.length\" class=\"sc-list-container\">\n" +
    "	<div data-ng-if=\"pluginsList.length\" class=\"sc-operation\"></div>\n" +
    "	<table class=\"sc-list-table table table-hover\">\n" +
    "		<thead>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr data-ng-repeat=\"plugins in pluginsList\">\n" +
    "				<td class=\"sc-component-details-on-table\">\n" +
    "					<div class=\"sc-inline-block-on-table sc-clickable\"\n" +
    "						style=\"width: 50%; vertical-align:middle\" data-ng-click=\"openPlugin(plugins)\">\n" +
    "						<p class=\"sc-bold-text-on-table\">{{plugins.name}}</p>\n" +
    "					</div>\n" +
    "					<div data-ng-click=\"openPlugin(plugins)\"\n" +
    "						class=\"sc-inline-block-on-table sc-clickable\">\n" +
    "						<p class=\"sc-grey-text-on-table\">{{'plugins.last.modified' |\n" +
    "							translate}}</p>\n" +
    "						<friendly-date data-date=\"{{plugins.modifiedOn}}\"\n" +
    "							data-date-type=\"miliseconds\"\n" +
    "							data-full-format=\"d MMM yyyy, hh:mm a\"\n" +
    "							data-short-format=\"hh:mm a\"></friendly-date>\n" +
    "<!-- 						<p>{{plugins.modifiedOn | date: getDateFormat()}}</p> -->\n" +
    "					</div>\n" +
    "				</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "\n" +
    "	<div data-ng-show=\"pluginsList.length < total\">\n" +
    "		<button type=\"button\" class=\"btn btn-default sc-btn-loadmore\"\n" +
    "			data-ng-click=\"loadMore()\">{{\"button.load.more\" |\n" +
    "			translate}}</button>\n" +
    "	</div>\n" +
    "\n" +
    "</div>\n" +
    "<div data-http-loader class=\"sc-data-loading\">\n" +
    "	<p style=\"text-align: center\" class=\"sc-vertical-middle-div \">{{'data.loading'\n" +
    "		| translate}}</p>\n" +
    "</div>\n" +
    "<div data-ng-if=\"!pluginsList.length\" class=\"sc-empty-resource-list-box\">\n" +
    "	<p style=\"text-align: center\">{{'empty.list.msg' | translate}}</p>\n" +
    "</div>");
}]);

angular.module("ui/app/templates/dialog-confirm.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/templates/dialog-confirm.html",
    "<div class=\"modal-header\" data-ng-if=\"title\">\n" +
    "	<h3 class=\"modal-title\">{{ title }}</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\" data-ng-if=\"msg\">\n" +
    "	<table class=\"sc-dialog-table\">\n" +
    "		<tr>\n" +
    "			<td>\n" +
    "				<div>\n" +
    "					<i class=\"fa fa-warning sc-dialog-icon sc-dialog-warning\"></i>\n" +
    "				</div>\n" +
    "			</td>\n" +
    "			<td class=\"sc-dialog-text\"><span >{{ msg }}</span></td>\n" +
    "		</tr>\n" +
    "	</table>\n" +
    "\n" +
    "</div>\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-primary\" type=\"button\" ng-click=\"ok()\">{{\"dialog.confirm\"\n" +
    "		| translate}}</button>\n" +
    "	<button class=\"btn btn-warning\" type=\"button\" ng-click=\"cancel()\">{{\"dialog.cancel\"\n" +
    "		| translate}}</button>\n" +
    "</div>");
}]);

angular.module("ui/app/templates/dialog-notify.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/templates/dialog-notify.html",
    "<div class=\"modal-header\" data-ng-if=\"title\">\n" +
    "	<h3 class=\"modal-title\">{{ title }}</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\" data-ng-if=\"msg\">\n" +
    "	<table class=\"sc-dialog-table\">\n" +
    "		<tr>\n" +
    "			<td>\n" +
    "				<div>\n" +
    "					<i class=\"fa fa-warning sc-dialog-icon\" data-ng-class=\"type\" data-ng-if =\"type == 'sc-dialog-warning'\"></i>\n" +
    "					<i class=\"fa fa-check-circle sc-dialog-icon\" data-ng-class=\"type\" data-ng-if =\"type == 'sc-dialog-success'\"></i>\n" +
    "					<i class=\"fa fa-times-circle sc-dialog-icon\" data-ng-class=\"type\" data-ng-if =\"type == 'sc-dialog-error'\"></i>\n" +
    "				</div>\n" +
    "			</td>\n" +
    "			<td class=\"sc-dialog-text\"><span >{{ msg }}</span></td>\n" +
    "		</tr>\n" +
    "	</table>\n" +
    "</div>\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-primary\" type=\"button\" ng-click=\"ok()\">{{\"dialog.ok\"\n" +
    "		| translate}}</button>\n" +
    "</div>");
}]);

angular.module("ui/app/templates/menu.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/templates/menu.html",
    "<div class=\"sc-menu sc-layout-full-height-auto\">\n" +
    "	<div data-ng-repeat=\"tab in tabs\">\n" +
    "		<div data-ng-if=\"!tab.children\" class=\"sc-menu-item-box\">\n" +
    "			<a class=\"sc-menu-item\"\n" +
    "				data-ng-class=\"{'sc-menu-selected':isActiveTab(tab.url)}\"\n" +
    "				data-ng-click=\"onClickTab(tab)\" ui-sref=\"{{tab.url}}\"> <span\n" +
    "				class=\"sc-menu-icon\"><i class=\"fa\" data-ng-class=\"tab.icon\"></i></span>\n" +
    "				{{tab.title | translate}}\n" +
    "			</a>\n" +
    "		</div>\n" +
    "\n" +
    "		<div data-ng-if=\"tab.children\" class=\"sc-sub-menu-item-box\">\n" +
    "			<div data-ng-class=\"{'sc-menu-item-expanded':tab.expanded}\">\n" +
    "				<a class=\"sc-menu-item\"\n" +
    "					data-ng-class=\"{'sc-menu-selected':isActiveTab(tab.url)}\"\n" +
    "					data-ng-click=\"tab.expanded = !tab.expanded\"> <span\n" +
    "					class=\"sc-menu-icon\"><i class=\"fa\" data-ng-class=\"tab.icon\"></i>{{tab.title\n" +
    "						| translate}} <span class=\"sc-menu-expand-icon\"> <i\n" +
    "							class=\"fa\"\n" +
    "							data-ng-class=\"{'fa-angle-up':tab.expanded, 'fa-angle-down':!tab.expanded}\"></i>\n" +
    "					</span></a>\n" +
    "			</div>\n" +
    "			<div class=\"sc-sub-menu\" uib-collapse=\"!tab.expanded\">\n" +
    "				<div data-ng-repeat=\"subtab in tab.children\"\n" +
    "					class=\"sc-menu-item-box\">\n" +
    "					<a class=\"sc-sub-menu-item sc-menu-item\"\n" +
    "						data-ng-class=\"{'sc-menu-selected':isActiveTab(subtab.url)}\"\n" +
    "						data-ng-click=\"onClickTab(subtab)\" ui-sref=\"{{subtab.url}}\"> <span\n" +
    "						class=\"sc-menu-icon\"><i class=\"fa\" data-ng-class=\"subtab.icon\"></i>{{subtab.title\n" +
    "							| translate}} </a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("ui/app/templates/multiParam.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ui/app/templates/multiParam.html",
    "<div class='tag-input-ctn' id=\"tag-input-ctn\">\n" +
    "	<div class='input-tag' data-ng-repeat=\"tag in tagArray()\">\n" +
    "		{{tag.value}}\n" +
    "		<div class='delete-tag' data-ng-click='deleteTag($index)'>&times;</div>\n" +
    "	</div>\n" +
    "	<input data-ng-style='{width: inputWidth}' data-ng-model='tagText'\n" +
    "		data-ng-class=\"{'has-error': form[name].$touched && (!tagText ||tagText.length === 0) && tagArray().length === 0 && inputRequired == 'true'}\"\n" +
    "		data-ng-required=\"tagArray().length === 0 && inputRequired == 'true'\"\n" +
    "		uib-tooltip=\"{{form[name].$valid ? '' : 'component.required.validation' |\n" +
    "									translate}}\"\n" +
    "		tooltip-trigger=\"{{{true: 'focus', false:\n" +
    "									'never'}[form[name].$invalid]}}\"\n" +
    "		tooltip-placement=\"bottom\" name=\"{{name}}\"\n" +
    "		placeholder='{{placeholder}}' data-ng-blur = \"addTag()\"/>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"sc-validation-block-sm\" data-ng-show=\"invalidInput\">\n" +
    "	<p>{{'multi.param.invalid.' + inputType | translate}}</p>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"sc-validation-block-sm\" data-ng-show=\"duplicateInput\">\n" +
    "	<p>{{'multi.param.duplicate' | translate}}</p>\n" +
    "</div>");
}]);
