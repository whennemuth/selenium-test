if (typeof dwr == 'undefined' || dwr.engine == undefined) throw new Error('You must include DWR engine before including this file');

(function() {
  if (dwr.engine._getObject("CustomAttributeService") == undefined) {
    var p;
    
    p = {};

    /**
     * @param {class java.lang.String} p0 a param
     * @param {function|Object} callback callback function or options object
     */
    p.getLookupReturnsForAjaxCall = function(p0, callback) {
      return dwr.engine._execute(p._path, 'CustomAttributeService', 'getLookupReturnsForAjaxCall', arguments);
    };
    
    dwr.engine._setObject("CustomAttributeService", p);
  }
})();

