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
var configSvcFactory = function($http, $q) {

	var GET_URL = '/rest/config';
	var SAVE_URL = '/rest/config/save';
	var SET_DIR_URL = '/rest/config/relocate';
	var GET_EMPTY_MODULE_URL = '/rest/config/module/empty';
	
	var emptyModuleJson;
	var configCache;
	
	return {
		
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
				.success(function(ServiceResponse) {
					configCache = ServiceResponse.data;
					deferred.resolve(configCache);
				}).error(function(ServiceResponse){
					deferred.reject(ServiceResponse);
				});
			}
			return deferred.promise;
		},
		
		getEmptyConfigModule : function(configId) {
			var deferred = $q.defer();
			if(emptyModuleJson) {
				var module = null;
				eval("module = " + angular.toJson(emptyModuleJson));
				deferred.resolve(module);				
			}
			else {
				$http.get(GET_EMPTY_MODULE_URL + "/" + configId)
				.success(function(ServiceResponse) {
					emptyModuleJson = ServiceResponse.data;
					var module = null;
					eval("module = " + angular.toJson(emptyModuleJson));
					deferred.resolve(module);
				}).error(function(ServiceResponse){
					deferred.reject(ServiceResponse);
				});				
			}
			return deferred.promise;
		},
		
		/**
		 * 
		 */
		saveConfig : function(scope) {
			var deferred = $q.defer();
			
			if(scope.action) {
				if(scope.config.currentEnvironment) {
					var envName = scope.config.currentEnvironment.name;
					var envUrl = scope.config.currentEnvironment.url;
				}
				
				if(scope.action == 'add environment') {
					// Validate by ensuring the name and or url are not already present in config.environments
					for(var e in scope.config.configEnvironments) {
						var env = scope.config.configEnvironments[e];
						if(areEqualIgnoreCase(envName, env.name)) {
							deferred.reject(envName + ' already used!');
							return deferred.promise;
						}
						if(areEqualIgnoreCase(envUrl, env.url)) {
							deferred.reject(envUrl + ' already used!');
							return deferred.promise;
						}
					}

					// Validation success, so add a new environment to the collection in config object
					scope.config.configEnvironments[scope.config.configEnvironments.length] = {
						name: envName,
						url:  envUrl,
						parentConfig: {id: scope.config.id, transitory: true}
					};
					// Avoids repeating persistence for the same new environment when it is present in the environments list and as the currentEnvironment
					// Alternatively you could set currentEnvironment equal to one of the pre-existing environments if any exist.
					scope.config.currentEnvironment = null;
					return deferred.promise;
				}
				else if(scope.action == 'edit environment') {
					for(var i=0; i< scope.config.configEnvironments.length; i++) {
						var env = scope.config.configEnvironments[i];
						if(scope.config.currentEnvironment.id == env.id) {
							env.name = scope.config.currentEnvironment.name;
							env.url = scope.config.currentEnvironment.url;
						}
					}
					return deferred.promise;
				}
				else if(scope.action == 'remove environment') {
					for(var i=0; i< scope.config.configEnvironments.length; i++) {
						var env = scope.config.configEnvironments[i];
						if(areEqualIgnoreCase(envName, env.name)) {
							scope.config.configEnvironments.splice(i, 1);
						}
					}
					return deferred.promise;
				}
				else if(scope.action == 'save config') {					
					$http({
						method: 'POST',
						url: SAVE_URL,
						data: scope.config
					}).then(
						function successCallback(ServiceResponse){
							configCache = ServiceResponse.data.data;
							deferred.resolve(configCache);
						}, 
						function errorCallback(ServiceResponse){
							deferred.reject(ServiceResponse);
						}
					);
					return deferred.promise;
				}
			}
			else {
				return deferred.promise;
			}
		}
	};
};

function areEqualIgnoreCase(val1, val2) {
	if(val1 == undefined || val1 == null)
		return false;
	if(val2 == undefined || val1 == null)
		return false;
	if(val1.length != val2.length)
		return false;
	val1 = val1.trim().toLowerCase();
	val2 = val2.trim().toLowerCase();
	if(val1 == false || val2 == false)
		return false;
	return val1 == val2;
}
