/**
 * Given a particular web element (node), return an object that represents the html table that
 * the web element is expected to be a descendent of. A list of table cells is built for this
 * object, one cell for the containing td/th element that contains the web element and all
 * additional 'outer' cells where there is a nested table hierarchy. NOTE: a "descendent" is not
 * necessarily a descendent of an ancestor, but will be a descendent of the row of that ancestor.
 * 
 * @param node The web element contained in a table cell.
 * @param cells The list of table cells that will be built.
 * @param depth How many jumps does it take up the document hierarchy to go from node to document root.
 * @returns
 */
function getAncestorTableCells(node, cells, depth) {
	if(node) {
		if(isCell(node)) {
			var table = getTable(node, depth);
			var tableTag = getFullTag(table);
			var id = getAttribute(node, "id");
			var tag = getFullTag(node);
			var first100Chars = getFirst100Chars(node);
			if(table) {
				cells[cells.length] = {
					id: id, 
					cell: node,
					depth: depth,
					x: node.cellIndex + 1,
					y: node.parentNode.rowIndex + 1,
					table: table,
					tableRows: table.rows.length,
					tableCols: table.rows[0].cells.length,
					tableTag: tableTag,
					tag: tag,
					first100Chars: first100Chars
				};
			}
		}
		return getAncestorTableCells(node.parentNode, cells, --depth);
	}			
	return cells;
}

/**
 * For each web element in the provided list, assume they each exist in the same table row and 
 * return that table row. This search accomodates iteration through a nested table hierarchy to
 * get the innermost table row that all the elements have in common.
 * 
 * @param nodes any set of web elements
 * @returns The table row or null if any of the nodes does not share the same row with any of the others
 */
function getCommonAncestorRow(nodes) {
	if(arguments.length == 1) {
		// First call to this function. Iterate over the whole array of nodes.
		if(nodes && nodes.length > 0) {
			if(nodes.length == 1) {
				return getRow(nodes[0]);
			}
			else if(nodes.length > 1) {
				var rows = [];
				var node1 = nodes[0];
				var remain = nodes.splice(1, nodes.length);
				for(var i=0; i<remain.length; i++) {
					var row = getCommonAncestorRow(node1, remain[i]);
					if(row == null) {
						return null;
					}
					rows[rows.length] = row;
				}	
				rows.sort(function(row1, row2) {
					// One row is a parent of another, so the parent will have larger innerHtml
					return row1.innerHTML.length < row2.innerHTML.length;
				});
				return rows[0];
			}
		}
		return null;		
	}
	else {
		// Recursive call to this function. It has narrowed work between only 2 nodes out of the initial array.
		var node1 = arguments[0];
		var node2 = arguments[1];
		
		if(node1 == null || node2 == null) {
			return null;
		}
		if(node1 === node2) {
			return node1;
		}
		
		var originalNode2 = arguments[2] ? arguments[2] : node2;
		
		if(isRow(node1)) {
			var row1 = node1;
			var row2 = getRow(node2.parentNode);
			if(row2 == null) {
				row1 = getRow(row1.parentNode);
				if(row1 != null) {
					row2 = getRow(originalNode2);
				}			
			}

			return getCommonAncestorRow(row1, row2, originalNode2); 
		}
		else {
			var row1 = getRow(node1.parentNode);
			var row2 = getRow(node2.parentNode);
			return getCommonAncestorRow(row1, row2, originalNode2);
		}
	}
}

/**
 * Object that represents a table column that is a parent, grandparent, etc. to a web element (originalField)
 */
function cellObject(originalField) {
	this.originalField = originalField;
	this.cell = getCell(originalField);
	this.sameColumn = function(othercell) {
		if(!this.cell || !othercell)
			return false;
		return this.cell.cellIndex == othercell.cell.cellIndex;
	};
	this.getDepth = function() {
		return getDepth(this.cell);
	};
	this.deeperThan = function(othercell) {
		if(!this.cell || !othercell)
			return false;
		return this.getDepth() > othercell.getDepth();
	};
	this.sameTable = function(othercell) {
		if(!this.cell || !othercell)
			return false;
		var table1 = getTable(this.cell);
		var table2 = getTable(othercell.cell);
		return table1 === table2;
	}
	this.nextcell = function() {
		if(this.cell) {
			this.cell = getParentCell(this.cell);
		}		
		return this.cell != null;
	}	
}

/**
 * Given a particular web element that is in a table, select out of a list of other web elements those that are
 * in the same column of the table. If no matches are found for the immediate parent column, look for an outer table
 * and repeat the same check for the "grandparent" column. Repeat this until a match is found or the document root is reached.
 * 
 * @param node
 * @param nodes
 */
function getNodesInSameColumn(node, nodes) {
	
	// If this is the first call, node will be a web element. Wrap it in an object that will carry it through recursive calls.
	var cell = node;
	var cells = nodes;
	var exit = false;
	
	if(!node.originalField) {
		cell = new cellObject(node);
		for(var i=0; i<nodes.length; i++) {
			cells[i] = new cellObject(nodes[i]);
		}
	}
	else {
		var ascend = true;
		var fieldsAbove = false;
		for(var i=0; i<cells.length; i++) {
			var othercell = cells[i];
			if(othercell.cell && othercell.deeperThan(cell)) {
				if(othercell.nextcell()) {
					ascend = false;
				}
			}
			else if(!cell.sameTable(othercell)) {
				fieldsAbove = true;
			}
		}
		
		if(ascend) {
			if(fieldsAbove) {
				cell.nextcell();
			}
			else {
				exit = true;
			}
		}		
	}

	var filtered = [];
	if(cell.cell) {
		for(var i=0; i<cells.length; i++) {
			var othercell = cells[i];
			if(cell.sameColumn(othercell)) {
				if(cell.sameTable(othercell)) {
					filtered[filtered.length] = othercell.originalField;
				}
			}
		}
		if(filtered.length == 0 && !exit) {
			return getNodesInSameColumn(cell, cells);
		}
	}
	
	return filtered;
}

function getAttribute(node, attributeName) {
	if(!node)
		return null;
	//if(node.hasAttributes()) { // causes error
		var attrs = node.attributes;
		for(var i=0; i<attrs.length; i++) {
			if(attrs[i].name == attributeName) {
				return attrs[i].value;
			}
		}
	//}
	return null;
}

/**
 * @returns How many jumps does it take up the document hierarchy to go from node to document root.
 */
function getDepth(node, depth) {
	if(node) {
		if(depth == undefined) 
			return getDepth(node.parentNode, 0);
		else
			return getDepth(node.parentNode, ++depth);
	}
	return depth;
}

/**
 * @returns The first html table ancestor of a node
 */
function getTable(node, depth) {
	if(node) {
		if(node.nodeName.toLowerCase() == 'table') {
			node.setAttribute('depth', depth);
			return node;
		} 		
		return getTable(node.parentNode, --depth);
	}
	return null;
}

/**
 * @returns The first html row ancestor of node
 */
function getRow(node) {
	if(node) {
		if(node.nodeName.toLowerCase() == 'tr') {
			return node;
		} 		
		return getRow(node.parentNode);
	}
	return null;
}

function getCell(node) {
	if(node) {
		if(node.nodeName.toLowerCase() == 'td' || node.nodeName.toLowerCase() == 'th') {
			return node;
		} 		
		return getCell(node.parentNode);
	}
	return null;	
}

function getParentCell(node) {
	if(node) {
		return getCell(node.parentNode);
	}
	return null;
}

/**
 * @returns The first html cell ancestor of node that is at the specified depth beneath the document root.
 */
function getAncestorCellAtDepth(node, depth) {
	if(node) {
		if(node.nodeName.toLowerCase() == 'td' || node.nodeName.toLowerCase() == 'th') {
			var nodeDepth = getDepth(node) + 0;
			if(nodeDepth < depth) {
				// node is alread at a "shallower" depth than depth, and this is an invalid condition.
				return null;
			}
			if(nodeDepth == depth) {
				node.setAttribute("columnIndex", node.cellIndex + 1)
				return node;
			}
		} 		
		return getAncestorCellAtDepth(node.parentNode, depth);
	}
	return null;
}

/**
 * @returns True if a web element is either a table cell or table header cell.
 */
function isCell(node) {
	if(!node)
		return false;
	if(!node.nodeName)
		return false;
	if(node.nodeName.toLowerCase() == 'td')
		return true;
	if(node.nodeName.toLowerCase() == 'th')
		return true;
	return false;
}

/**
 * @returns True if a web element is a table row (tr).
 */
function isRow(node) {
	return node && node.nodeName && node.nodeName.toLowerCase() == 'tr';
}

/**
 * Get the full content within the opening tag of a node.
 * @param node
 * @returns
 */
function getFullTag(node) {
	if(!node.outerHTML)
		return null;
	if(!node.innerHTML)
		return node.outerHTML;
		
	return node.outerHTML.slice(0, node.outerHTML.indexOf(node.innerHTML));
}

function getFirst100Chars(node) {
	if(!node.outerHTML)
		return null;
	var html = removeEmptyLines(node.outerHTML);
	if(html.length <= 100)
		return html;
	if(html.length > 100) {
		return html.substr(0, 100) + ' [more...]';
	}
}

function removeEmptyLines(content) {
	if(content) {
		content = content.replace(/(((\r\n)|\n)+[\x20\t]*){2,}/, '\n');
	}
	return content;
}

function doTest() {
	var node = document.getElementById('div2');
	var nodes = [
	    document.getElementById('txt2'),
	    document.getElementById('txt8')
	];
	var results = getNodesInSameColumn(node, nodes);
	var s = '';
	for(var i=0; i<results.length; i++) {
		s += (results[i].id + ', ');
	}
	alert(s);
}

var task = arguments[0];

if(task == 'cell') {
	var node = arguments[1];
	var cellInfo = getAncestorTableCells(node, [], getDepth(node));
	cellInfo.node = node;
	return cellInfo;
}
else if(task == 'row') {
	var nodes = arguments[1];
	var compareInfo = getCommonAncestorRow(nodes);
	return compareInfo;
}
else if(task == 'ancestorcell') {
	var cell = arguments[1];
	var depth = arguments[2];
	var ancestor = getAncestorCellAtDepth(cell, depth);
	return ancestor;
}
else if(task == 'column') {
	var node = arguments[1];
	var nodes = arguments[2];
	var filtered = getNodesInSameColumn(node, nodes);
	return filtered;
}
