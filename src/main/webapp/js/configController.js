var configCtrlFactory = function(cycleCtrl) {
	return {
		setScope: function(scope, configSvc, cycleSvc) {
			console.log("Configuring application controller scope");
			scope.config = '';
			scope.action = '';
			scope.cycle = '';
			scope.cycles = [];
			
			cycleSvc.getElementTypes();
			
			// Load in the existing configuration
			if(!scope.config) {
				configSvc.getConfig(false).then(
						function(config) {
							scope.config = config;
							if(config.user && config.user.id) {
								cycleSvc.getCycles(config.user.id, false).then(
									function(cycles) {
										scope.cycles = cycles;
										cycleSvc.getEmptyCycle(config.user.id);
									},
									function(error) {
										if(error.message) {
											alert(
												"Cycles fetch error!\n\n" + 
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
				scope.config.currentEnvironment.current = true;
// TODO: The new environment is saved, but the listbox should be refreshed so that it becomes the selected item. currentEnvironment might not be referring to the new environment. Fix this.
// TODO: The "Add" button results in an empty row showing up at the top of the listbox (angular problem). Fix this (currentEnvironment above will be null).				
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
			
			scope.insertNewModule = function(index) {
				configSvc.getEmptyConfigModule(scope.config.id).then(
					function(blankModule) {
						scope.config.configModules.splice(index+1, 0, blankModule);
					},
					function(error) {
						if(error.message) {
							alert(
								"Blank module lookup error!\n\n" + 
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
				)
			};
			
			scope.insertNewTab = function(parentIndex, index) {
				configSvc.getEmptyConfigModule(scope.config.id).then(
					function(blankModule) {
						scope.config.configModules[parentIndex].configTabs.splice(index+1, 0, blankModule.configTabs[0]);
					},
					function(error) {
						if(error.message) {
							alert(
								"Blank tab lookup error!\n\n" + 
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
				)
			};
			
			scope.getElementTypes = function() {
				return cycleSvc.getElementTypes();
			}
			
			scope.getShortcutElementTypes = function() {
				var types = cycleSvc.getElementTypes();
				var filtered = {};
				for(var et in types) {
					if(et == 'HYPERLINK' || et == 'HOTSPOT') {
						filtered[et] = types[et];
					}
				}
				return filtered;
			}
			
			scope.insertNewShortcut = function(index) {
				configSvc.getEmptyConfigShortcut(scope.config.id).then(
					function(blankShortcut) {
						scope.config.configShortcuts.splice(index+1, 0, blankShortcut);
						scope.config.configShortcuts[index+1].checked = true;
					},
					function(error) {
						if(error.message) {
							alert(
								"Blank shortcut lookup error!\n\n" + 
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
				)
			}
			
			scope.getConfigJson = function() {
				return 'CONFIG:\n' + angular.toJson(scope.config, true);
			}
		}
	};
};