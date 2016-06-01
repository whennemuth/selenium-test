
var cycleFactory = function($http, $q) {
	
	var GET_EMPTY_CYCLE_URL = '/rest/cycle/empty';
	var GET_CYCLE_BY_ID_URL = '/rest/cycle/lookup/';	// tack id on to the end as a path variable
	
	var emptyCycle;
	var thisCycle;
	
	return {
		getCycle : function(cycleId) {
			var deferred = $q.defer();
			
			if(emptyCycle) {
				deferred.resolve(emptyCycle);
			}
			else {
				$http.get(GET_EMPTY_CYCLE_URL)
				.success(function(response) {
					if(cycleId == null) {
						emptyCycle = response;
						deferred.resolve(emptyCycle);
					}
					else {
						thisCycle = response;
						deferred.resolve(thisCycle);
					}
					
				}).error(function(response){
					deferred.reject(response);
				});
			}
			return deferred.promise;
			
		}
	};
};