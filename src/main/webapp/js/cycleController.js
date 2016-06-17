var cycleCtrlFactory = function() {
	
	return {
		setScope: function(scope, cycleSvc) {
			
			// Define an event handler to load in an empty default cycle object bound to a ng-repeat block for new cycles
			scope.newCycle = function() {
				cycleSvc.getCycle(null, scope.config.user.id).then(
					function(data) {
						scope.cycle = data;
					},
					function(error) {
						alert("Cycle retrieval error!\n" + error);
					}
				);
			}
			
			scope.saveCycle = function() {
				cycleSvc.saveCycle(scope.cycle).then(
					function(serviceResponse) {
						//if(serviceResponse.message) {							
						//	alert(serviceResponse.message);
						//}
						scope.cycles = serviceResponse.data;					
						scope.cycle = '';
						scope.$apply;
					},
					function(serviceResponse) {
						if(serviceResponse != undefined && serviceResponse.message) {
							alert(serviceResponse.message);
						}
						else {
							alert('Cycle saving error:\n' + serviceResponse);
						}
					}
				);
			};
			
			// Define an event handler to cancel the addition of a new cycle.
			scope.cancelNewCycle = function() {
				scope.cycle = '';
			};
			
			scope.cloneCycle = function() {
				
			};
			
			scope.getBlankObject = function(objectType) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				switch(objectType) {
				case 'suite': return cycleTemplate.suites[0];
				case 'module': return cycleTemplate.suites[0].modules[0];
				case 'tab': return cycleTemplate.suites[0].modules[0].tabs[0];
				case 'lv': return cycleTemplate.suites[0].modules[0].tabs[0].labelAndValues[0];
				}
			}
			
			scope.newSuite = function(suiteIdx) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				scope.cycle.suites[suiteIdx+1] = cycleTemplate.suites[0];
			};
			
			scope.newModule = function(modules, moduleIdx) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				scope.cycle.suites[suiteIdx].modules[moduleIdx+1] = cycleTemplate.suites[0].modules[0];
			};
			
			scope.newTab = function(suiteIdx, moduleIdx, tabIdx) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				scope.cycle.suites[suiteIdx].modules[moduleIdx].tabs[tabIdx+1] = cycleTemplate.suites[0].modules[0].tabs[0];
			};
			
			scope.newLabelAndValue = function(suiteIdx, moduleIdx, tabIdx, lvIdx) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				scope.cycle.suites[suiteIdx].modules[moduleIdx].tabs[tabIdx].labelAndValues[lvIdx+1] = cycleTemplate.suites[0].modules[0].tabs[0].labelAndValues[0];
			}
			
			/**
			 * This function repopulates tab picklist options to reflect a new parent module when that modules 
			 * picklist selected value has changed.
			 */
			scope.getTabs = function(suiteIdx, moduleIdx) {
				var moduleName = scope.cycle.suites[suiteIdx].modules[moduleIdx].name;
				for(var i=0; i<scope.config.configModules.length; i++) {
					var cfgMdl = scope.config.configModules[i];
					if(moduleName == cfgMdl.label) {
						return cfgMdl.configTabs;
					}
				}
				return [];
			};
		}
	};
};