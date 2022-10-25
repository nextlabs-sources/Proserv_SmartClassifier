// Karma configuration
// Generated on Thu Jan 14 2016 20:48:38 GMT+0800 (Malay Peninsula Standard Time)

module.exports = function(config) {
	config
			.set({

				// base path that will be used to resolve all patterns (eg.
				// files, exclude)
				basePath : '',

				// frameworks to use
				// available frameworks:
				// https://npmjs.org/browse/keyword/karma-adapter
				frameworks : [ 'jasmine' ],

				// list of files / patterns to load in the browser
				files : [
						'config/*.js',
						'lib/jquery/1.8.2/jquery.js',
						'lib/angular/1.8.2/angular.min.js',
						'lib/chart-js/2.3.0/Chart.min.js',
						'lib/angular-chart/1.1.1/angular-chart.min.js',
						'lib/angular-input-mask/angular-input-masks-standalone.min.js',
						'lib/angular-material/1.0.0/angular-material.min.js',
						'lib/angular/1.8.2/angular-sanitize.min.js',
						'lib/angular/1.8.2/angular-animate.min.js',
						'lib/angular/1.8.2/angular-messages.min.js',
						'lib/angular/1.8.2/angular-aria.min.js',
						'lib/angular-ui-router/0.4.3/angular-ui-router.min.js',
						'lib/angular-ui/bootstrap/ui-bootstrap-tpls-0.14.3.min.js',
						'lib/angular-ui/angular-ui-numeric.js',
						'lib/angular-ui-switch/angular-ui-switch.min.js',
						'lib/angular-translate/2.19.0/angular-translate.min.js',
						'lib/angular-translate/angular-translate-loader-static-files.min.js',
						'lib/jquery-ui/1.11.0/jquery-ui.min.js',
						'lib/ng-tags-input/3.0/ng-tags-input.min.js',
						'testlib/angular-mocks/angular-mocks.js', 'app/*.js',
						'app/**/*.js', 'test/**/*.js'],

				// list of files to exclude
				exclude : [],

				// preprocess matching files before serving them to the browser
				// available preprocessors:
				// https://npmjs.org/browse/keyword/karma-preprocessor
				preprocessors : {},

				// test results reporter to use
				// possible values: 'dots', 'progress'
				// available reporters:
				// https://npmjs.org/browse/keyword/karma-reporter
				reporters : [ 'progress' ],

				// web server port
				port : 9876,

				// enable / disable colors in the output (reporters and logs)
				colors : true,

				// level of logging
				// possible values: config.LOG_DISABLE || config.LOG_ERROR ||
				// config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
				logLevel : config.LOG_DEBUG,

				// enable / disable watching file and executing tests whenever
				// any file changes
				autoWatch : true,

				// start these browsers
				// available browser launchers:
				// https://npmjs.org/browse/keyword/karma-launcher
				browsers : ['Firefox', 'PhantomJS', 'Chrome'],

				// Continuous Integration mode
				// if true, Karma captures browsers, runs the tests and exits
				singleRun : false,

				// Concurrency level
				// how many browser should be started simultanous
				concurrency : Infinity
			})
}