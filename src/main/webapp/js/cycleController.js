var cycleCtrlFactory = function() {
	
	return {
		setScope: function(scope, cycleSvc) {
			
			// Load in Element Types and shortcuts from web service
			cycleSvc.getElementTypes();
			
			// Add an event handler to query if the service has intialized yet.
			scope.isInitialized = function() {
				return cycleSvc.isInitialized();
			}
			
			scope.getElementTypes = function() {
				var types = cycleSvc.getElementTypes();
				var filtered = {};
				for(var et in types) {
					if(et != 'BUTTONIMAGE') {
						filtered[et] = types[et];
					}
				}
				return filtered;
			};
			
			scope.getShortcuts = function() {
				return scope.config.configShortcuts;
			};
			
			// Control which fields for label and value row get shown/hidden depending on element type selection.
			scope.lvShow = function(lv, inputType) {
				var checkable = false;
				var clickonly = false;
				var textvalue = false;
				var shortcut = false;
				switch(lv.elementType) {
					case 'BUTTON': 	case 'HOTSPOT': case 'HYPERLINK': case 'SHORTCUT':
						clickonly = true;
						shortcut = lv.elementType == 'SHORTCUT';
						break;
					case 'CHECKBOX': case 'RADIO': 
						checkable = true;
						break;
					case 'SELECT': case 'TEXTAREA': case 'TEXTBOX': case 'PASSWORD': case 'OTHER':
						textvalue = true;
						break;
					default:
						return false;
				}
				
				switch(inputType) {
					case 'state':
						return checkable;
					case 'value':
						return textvalue;
					case 'navigates':
						return clickonly && !shortcut;
					case 'label': case 'identifier':
						return !shortcut;
					case 'shortcut':
						return shortcut;
					default:
						return lv.elementType;
				}
			};
			
			// Clear out or pre-populate field values of a label and value row depending on element type and checked state selections
			scope.lvChange = function(lv) {
				switch(lv.elementType) {
					case 'BUTTON': 	case 'HOTSPOT': case 'HYPERLINK':
						lv.value = null;
						lv.booleanValue = false;
						lv.shortcut.id = 0;
						break;
					case 'CHECKBOX': case 'RADIO': 
						lv.value = lv.booleanValue;
						lv.navigates = false;
						lv.shortcut.id = 0;
						break;
					case 'SELECT': case 'TEXTAREA': case 'TEXTBOX': case 'PASSWORD': case 'OTHER':
						lv.booleanValue = false;
						lv.value = /^(true)|(false)$/i.test(lv.value) ? null : lv.value;
						lv.navigates = false;
						lv.shortcut.id = 0;
						break;
					case 'SHORTCUT':
						lv.identifier = null;
						lv.label = null;
						lv.value = null;
						lv.booleanValue = false;
						if(lv.shortcut) {
							lv.navigates = (lv.navigates || lv.shortcut.navigates);
						}
						break;
					default:
						lv.identifier = null;
						lv.label = null;
						lv.value = null;
						lv.booleanValue = false;
						lv.navigates = false;
						lv.shortcut.id = 0;
						break;
				}
			};
			
			scope.resequence = function(items) {
				for(var i=0; i<items.length; i++) {
					items[i].sequence = (i+1);
				}
			};
			
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
			};
			
			scope.backupCycle = function() {
				scope.cycleBackup = angular.copy(scope.cycle);
			};
				
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
				cycleSvc.launch(scope.config.id, itemId, itemType, scope.cycle.kerberosLoginParms).then(
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
			
			scope.getBlankObject = function(objectType) {
				var cycleTemplate = cycleSvc.getEmptyCycle(scope.config.user.id);
				switch(objectType) {
				case 'suite': return cycleTemplate.suites[0];
				case 'lv': return cycleTemplate.suites[0].labelAndValues[0];
				}
			};
			
			scope.addSuite = function(cycle, suiteIdx) {
				cycle.suites.splice(suiteIdx+1, 0, scope.getBlankObject('suite')); 
				scope.resequence(cycle.suites);
			};
			
			scope.removeSuite = function(cycle, suiteIdx) {
				cycle.suites.splice(suiteIdx, 1); 
				scope.resequence(cycle.suites);
			};
			
			scope.addLabelAndValue = function(suite, lvIdx) {
				suite.labelAndValues.splice(lvIdx+1, 0, scope.getBlankObject('lv')); 
				scope.resequence(suite.labelAndValues);
			};
				
			scope.removeLabelAndValue = function(suite, lvIdx) {
				suite.labelAndValues.splice(lvIdx, 1); 
				scope.resequence(suite.labelAndValues);
			};
			
			/**
			 * Condense all field values of a LabelAndValue to one line of text.
			 */
			scope.getLvLabel = function(lv) {
				var s = lv.sequence + ') ';
				s += (lv.id && lv.id > 0) ? 'id:' + lv.id : 'new';
				s += ', type:';
				var shortcut = '';
				if(lv.elementType) {
					s += lv.elementType;
					if(lv.elementType == 'SHORTCUT') {
						if(lv.shortcut) {
							var id = lv.shortcut.id;
							shortcut = '[id=' + (id > 0 ? id : '?') + ']';
						}
						else {
							shortcut = '[id=?]';
						}
					}
				}
				else {
					s += '?';
				}
				if(shortcut) {
					s += (', shortcut:' + shortcut);
				}
				else {
					s += ', label:';
					s += lv.label ? lv.label : '?';
					s += ', value:';
					s += lv.value ? lv.value : '?';
					s += ', other identifier:';
					s += lv.identifier ? lv.identifier : '?';
				}
				s += ', navigates:';
				s += lv.navigates ? 'true' : 'false';
				
				return s;
			};
			
			/**
			 * Flatten out an array like ['a', 'b', 'c'] to 'a   >   b   >   c'
			 */
			scope.getHierarchyLabel = function(shortcut) {
				var s = shortcut.sequence + ') ' + 
				shortcut.elementType + ': ';
				var parts = shortcut.labelHierarchyParts;
				for(var i=0; i<parts.length; i++) {
					s += parts[i];
					if((i+1) < parts.length) {
						s += '   >   ';
					}
				}
				return s;
			};
			
			scope.getCycleJson = function() {
				return 'CYCLE:\n' + angular.toJson(scope.cycle, true) + '\n\nCYCLES:\n' + angular.toJson(scope.cycles, true) + '\n\nBACKUP:\n' + angular.toJson(scope.cycleBackup, true);
			};
		}
	};
};