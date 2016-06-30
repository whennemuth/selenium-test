var cycleCtrlFactory = function() {
	
	return {
		setScope: function(scope, cycleSvc) {
			
			// Load in Element Types and module actions from web service
			cycleSvc.getElementTypes();
			cycleSvc.getModuleActions();
			
			// Add an event handler to query if the service has intialized yet.
			scope.isInitialized = function() {
				return cycleSvc.isInitialized();
			}
			
			scope.getElementTypes = function() {
				return cycleSvc.getElementTypes();
			}
			
			scope.getModuleActions = function() {
				return cycleSvc.getModuleActions();
			}
			
			// Control which fields for label and value row get shown/hidden depending on element type selection.
			scope.lvShow = function(lv, inputType) {
				var checkable = false;
				var clickonly = false;
				var textvalue = false;
				switch(lv.elementType) {
					case 'BUTTON': 	case 'BUTTONIMAGE': case 'HYPERLINK':
						clickonly = true;
						break;
					case 'CHECKBOX': case 'RADIO': 
						checkable = true;
						break;
					case 'SELECT': case 'TEXTAREA': case 'TEXTBOX': case 'OTHER':
						textvalue = true;
						break;
				}
				
				switch(inputType) {
					case 'state':
						return checkable;
					case 'value':
						return textvalue;
					default:
						return false;
				}
			}
			
			// Clear out or pre-populate field values of a label and value row depending on element type and checked state selections
			scope.lvChange = function(lv) {
				switch(lv.elementType) {
					case 'BUTTON': 	case 'BUTTONIMAGE': case 'HYPERLINK':
						lv.value = '';
						lv.checked = '';
						break;
					case 'CHECKBOX': case 'RADIO': 
						lv.value = lv.checked;
						break;
					case 'SELECT': case 'TEXTAREA': case 'TEXTBOX': case 'OTHER':
						lv.checked = '';
						lv.value = /^(true)|(false)$/i.test(lv.value) ? '' : lv.value;
						break;
					default:
						lv.identifier = '';
						lv.label = '';
						lv.value = '';
						lv.checked = '';
						break;
				}
			}
			
			scope.resequence = function(items) {
				for(var i=0; i<items.length; i++) {
					items[i].sequence = (i+1);
				}
			}
			
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
			
			scope.backupCycle = function() {
				scope.cycleBackup = angular.copy(scope.cycle);
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
			
			/**
			 * Define an event handler to cancel the addition of a new cycle. It must restore the cycle that was being 
			 * edited from its backup copy made before the edits started (backup made on change event for the cycles listbox).
			 */
			scope.cancelNewCycle = function() {
				scope.cycle = '';
				if(scope.cycles && scope.cycles.length && scope.cycles.length > 0) {
					for(var i=0; i<scope.cycles.length; i++) {
						if(scope.cycles[i].id == scope.cycleBackup.id) {
							scope.cycles[i] = scope.cycleBackup;
							scope.cycleBackup = '';
							break;
						}
					}
				}				
			};
			
			scope.cloneCycle = function() {
				
			};
			
			scope.launch = function(itemId, itemType) {
				cycleSvc.launch(scope.config.id, itemId, itemType).then(
						function(serviceResponse) {
							alert(serviceResponse.message);					
						},
						function(serviceResponse) {
							if(serviceResponse != undefined && serviceResponse.message) {
								alert(serviceResponse.message + '\n\n' + serviceResponse.data);
							}
							else {
								alert('Launch error:\n' + serviceResponse);
							}
						}
					);
			};
			
			/**
			 * If the module type is "custom", then there is to be no module name selection and no tabs, so blank and hide these.
			 */
			scope.toggleModuleType = function(module) {
				module.tabs = [];
				module.tabs[0] = scope.getBlankObject('tab');
				module.name = null;
				module.customName = null;
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
			
			scope.getCycleJson = function() {
				return 'CYCLE:\n' + angular.toJson(scope.cycle, true) + '\n\nCYCLES:\n' + angular.toJson(scope.cycles, true) + '\n\nBACKUP:\n' + angular.toJson(scope.cycleBackup, true);
			}
		}
	};
};