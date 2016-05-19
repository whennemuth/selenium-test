var configFactory = function($http, $q) {

	var _configUrl = '/rest/config';
	var _browseUrl = '/rest/browse/for/directory';
	var _config;
	
	return {
		// Override the default url with a specific url
		setBrowseUrl : function(url) {
			_browseUrl = url;
		},
		// Override the default url with a specific url
		setConfigUrl : function(url) {
			_configUrl = url;
		},
		getConfig : function() {
			var deferred = $q.defer();
			if(_config) {
				// _data is cached, so use the cached value to prevent another ajax call.
				deferred.resolve(_config);
			}
			else {
				// Make an ajax call to get the config model
				$http.get(_configUrl)
					.success(function(response){
						_config = response;
						deferred.resolve(_config);
					}).error(function(response){
						_config = response;
						deferred.reject(_config);
					});
			}
			return deferred.promise;
		}
	};
};