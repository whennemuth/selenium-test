
var GET_EMPTY_CYCLE_URL = '/rest/cycle/empty';
var GET_CYCLE_BY_ID_URL = '/rest/cycle/lookup';	// tack id on to the end as a path variable
var GET_CYCLES_BY_USER_ID = '/rest/cycles';
var SAVE_CYCLE_URL = '/rest/cycle/save';
var GET_ELEMENT_TYPES_URL = '/rest/cycle/element/types';
var GET_MODULE_ACTIONS_URL = '/rest/cycle/module/actions';
var LAUNCH_CYCLE_URL = '/rest/cycle/launch/cycle';
var LAUNCH_SUITE_URL = '/rest/cycle/launch/suite';
var LAUNCH_MODULE_URL = '/rest/cycle/launch/module';

var cycleSvcFactory = function($http, $q) {
	
	var emptyCycleJson;
	var elementTypes;
	var moduleActions;
	var cyclesCache;	
	
	return {
		isInitialized : function() {
			// Once these objects have been obtained and cached from the corresponding web services, we are initialized.
			return emptyCycleJson && elementTypes && moduleActions;
		},
		getEmptyCycle : function(userId) {
			if(emptyCycleJson) {
				// Called AFTER page load (not a promise)
				var cycle = null;
				eval("cycle = " + emptyCycleJson);
				return cycle;
			}
			else {
				// Called DURING page load (is a promise)
				var deferred = $q.defer();
				var url = GET_EMPTY_CYCLE_URL + "/" + userId;
				$http.get(url)
					.success(function(response) {
						emptyCycleJson = angular.toJson(response.data);
						deferred.resolve(response.data);
						
					}).error(function(response){
						deferred.reject(response);
					});
				return deferred.promise;
			}
		},
		getCycle : function(cycleId, userId) {
			var deferred = $q.defer();
			if(cycleId == null) {
				var emptyCycle = this.getEmptyCycle(userId);
				deferred.resolve(emptyCycle);
			}
			else {
				$http.get(GET_CYCLE_BY_ID_URL)
					.success(function(response) {
						deferred.resolve(response.data);
						
					}).error(function(response){
						deferred.reject(response);
					});
			}
			
			return deferred.promise;
		},
		getCycles : function(userId, refresh) {
			var deferred = $q.defer();
			if(cyclesCache && refresh==false) {
				deferred.resolve(cyclesCache);
			}
			else {
				var url = GET_CYCLES_BY_USER_ID + "/" + userId;
				$http.get(url)
				.success(function(response) {
					cyclesCache = response.data;
					deferred.resolve(response.data);
					
				}).error(function(response){
					deferred.reject(response);
				});
			}
			return deferred.promise;
		},
		saveCycle : function(cycle) {
			var deferred = $q.defer();
			$http({
				method: 'POST',
				url: SAVE_CYCLE_URL,
				data: cycle
			})
				.success(function(response) {
					cyclesCache = response.data;
					deferred.resolve(response);
					
				}).error(function(response){
					deferred.reject(response);
				});
			return deferred.promise;
		},
		launch : function(configId, itemId, itemType, kerberosLoginParms) {
			var deferred = $q.defer();
			var url = '';
			switch(itemType) {
			case 'cycle':
				var url = LAUNCH_CYCLE_URL + "?cfgId=" + configId + "&cycleId=" + itemId;
				break;
			case 'suite':
				var url = LAUNCH_SUITE_URL + "?cfgId=" + configId + "&suiteId=" + itemId;
				break;
			case 'module':
				var url = LAUNCH_MODULE_URL + "?cfgId=" + configId + "&moduleId=" + itemId;
				break;
			}

			if(url) {
				$http({
					method: 'POST',
					url: url,
					data: kerberosLoginParms
				})
				.success(function(response) {
					deferred.resolve(response);				
				}).error(function(response){
					deferred.reject(response);
				});
				return deferred.promise;
			}
		},
		getElementTypes : function() {
			if(elementTypes) {
				// Called AFTER first load and is taken from cached variable (not a promise)
				//var ets = null;
				//eval("ets = " + elementTypes);
				//return ets;
				return elementTypes;
			}
			else {
				// First load must come from the web service call (is a promise)
				var deferred = $q.defer();
				$http.get(GET_ELEMENT_TYPES_URL)
					.success(function(response) {
						//elementTypes = angular.toJson(response.data);
						elementTypes = response.data;
						deferred.resolve(response.data);
						
					}).error(function(response){
						deferred.reject(response);
					});
				return deferred.promise;
			}
		},		
		getModuleActions : function() {
			if(moduleActions) {
				// Called AFTER first load and is taken from cached variable (not a promise)
				return moduleActions;
			}
			else {
				// First load must come from the web service call (is a promise)
				var deferred = $q.defer();
				$http.get(GET_MODULE_ACTIONS_URL)
					.success(function(response) {
						moduleActions = response.data;
						deferred.resolve(response.data);
						
					}).error(function(response){
						deferred.reject(response);
					});
				return deferred.promise;
			}
		}		
	};
};
