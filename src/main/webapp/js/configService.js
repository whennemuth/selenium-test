/**
 * Provide CRUD operations for the application configuration.
 * 
 * NOTE: Whatever state is assigned to $scope is lost when the route is changed (ie: clicking a 
 * link, href='#newRoute'). The controller is re-run and is passed a re-initialized $scope
 * variable (state is blanked out). Therefore, state (like user input against the model or data 
 * acquired from ajax calls) is not directly assigned to $scope in the success function of the 
 * promise object when a route change is yet to occur. Instead, we take advantage of the fact that the 
 * injected services are singletons and their properties survive between route changes. State is 
 * stored as service properties and is restored on the $scope variable each time the controller is run/re-run.
 * The stored state in this case is the configCache variable.
 */
var configFactory = function($http, $q) {

	var GET_URL = '/rest/config';
	var SAVE_URL = '/rest/config/save';
	var SET_DIR_URL = '/rest/config/update/output/directory';
	
	var configCache;
	
	return {
		
		/**
		 * Call a webservice that brings up a FileChooser so the user can specify a directory
		 * on their file system. A new configuration (as json) is returned reflecting the new directory.
		 */
		setOutputDirectory : function() {
			var deferred = $q.defer();
			$http.post(SET_DIR_URL)
			.success(function(response){
				deferred.resolve(response);
			}).error(function(response){
				deferred.reject(response);
			});
			return deferred.promise;
		},
		
		/**
		 * Call a webservice that returns the configuration for the application as json.
		 * The configuration is obtained from a .cfg file located near the jar file.
		 */
		getConfig : function(refresh) {
			var deferred = $q.defer();
			
			if(configCache && refresh==false) {
				deferred.resolve(configCache);
			}
			else {
				$http.get(GET_URL)
				.success(function(response) {
					configCache = response
					deferred.resolve(configCache);
				}).error(function(response){
					deferred.reject(response);
				});
			}
			return deferred.promise;
		},
		
		/**
		 * 
		 */
		saveConfig : function(config) {
			var deferred = $q.defer();
			$http({
				method: 'POST',
				url: SAVE_URL,
				data: config
			}).then(
				function successCallback(response){
					configCache = response;
					deferred.resolve(configCache);
				}, 
				function errorCallback(response){
					deferred.reject(response);
				}
			);
			return deferred.promise;
		}
	};
};