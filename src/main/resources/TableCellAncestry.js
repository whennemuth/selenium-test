/**
 * Given a particular web element (node), return an object that represents the html table that
 * the web element is expected to be a descendent of. A list of table cells is built for this
 * object, one cell for for the containing td/th element that contains the web element and all
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
			var id = getAttribute(node, "id");
			if(table) {
				cells[cells.length] = {
					id: id, 
					cell: node,
					depth: depth,
					x: node.cellIndex + 1,
					y: node.parentNode.rowIndex + 1,
					table: table,
					tableRows: table.rows.length,
					tableCols: table.rows[0].cells.length
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