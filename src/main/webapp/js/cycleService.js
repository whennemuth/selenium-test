
var GET_EMPTY_CYCLE_URL = '/rest/cycle/empty';
var GET_CYCLE_BY_ID_URL = '/rest/cycle/lookup/';	// tack id on to the end as a path variable
var SAVE_CYCLE_URL = '/rest/cycle/save';

var cycleFactory = function($http, $q) {
	
	var emptyCycleJson;
	var thisCycle;
	
	return {
		getCycle : function(cycleId) {
			var deferred = $q.defer();
			
			if(cycleId == null) {
				if(emptyCycleJson) {
					var cycle = null;
					eval("cycle = " + emptyCycleJson);
					deferred.resolve(cycle);
				}
				else {
					$http.get(GET_EMPTY_CYCLE_URL)
						.success(function(response) {
							emptyCycleJson = angular.toJson(response.data);
							deferred.resolve(response.data);
							
						}).error(function(response){
							deferred.reject(response);
						});
				}
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
		getEmptyCycle : function() {
			var cycle = null;
			eval("cycle = " + emptyCycleJson);
			return cycle;
		},
		saveCycle : function(cycle) {
			var deferred = $q.defer();
			$http({
				method: 'POST',
				url: SAVE_CYCLE_URL,
				data: cycle
			})
				.success(function(response) {
					deferred.resolve(response);
					
				}).error(function(response){
					deferred.reject(response);
				});
			return deferred.promise;
		}
	};
};
