var configCtrlFactory = function() {
	
	return {
		setScope: function(scope, configSvc) {
			console.log("Configuring application controller scope");
			scope.config = '';
			scope.action = '';
			scope.cycle = '';
			scope.cycles = [];
			
			// Load in the existing configuration
			if(!scope.config) {
				configSvc.getConfig(false).then(
						function(data) {
							scope.config = data;
						},
						function(error) {
							alert("Configuration retrieval error!\n" + error);
						}
					);
			}
			
			// Define an event handler for the save button associated with updates to the configuration
			scope.setConfig = function(action) {
				scope.action = action;
				configSvc.saveConfig(scope).then(
					function(data) {
						scope.config = data.config.data;
					},
					function(error) {
						alert(error);
					}
				);
			};
			
		}
	};
};