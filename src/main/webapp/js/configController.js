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
						function(config) {
							scope.config = config;
						},
						function(error) {
							if(error.message) {
								alert(
									"Configuration retrieval error!\n\n" + 
									error.message + '\n\n' + 
									error.data);
							}
							else if(error.data) {
								alert(error.data);
							}
							else {
								alert(error);
							}
						}
					);
			}
			
			// Define an event handler for the save button associated with updates to the configuration
			scope.setConfig = function(action) {
				scope.action = action;
				configSvc.saveConfig(scope).then(
					function(config) {
						scope.config = config;
					},
					function(error) {
						if(error.message) {
							alert(
								"Configuration save error!\n\n" + 
								error.message + '\n\n' + 
								error.data);
						}
						else if(error.data) {
							alert(error.data);
						}
						else {
							alert(error);
						}
					}
				);
			};
			
		}
	};
};