if (typeof dwr == 'undefined' || dwr.engine == undefined) throw new Error('You must include DWR engine before including this file');

(function() {
  if (dwr.engine._getObject("DocumentTypeService") == undefined) {
    var p;
    
    p = {};

    /**
     * @param {class java.lang.String} p0 a param
     * @param {function|Object} callback callback function or options object
     */
    p.findByName = function(p0, callback) {
      return dwr.engine._execute(p._path, 'DocumentTypeService', 'findByName', arguments);
    };

    /**
     * @param {class java.lang.String} p0 a param
     * @param {function|Object} callback callback function or options object
     */
    p.findByNameCaseInsensitive = function(p0, callback) {
      return dwr.engine._execute(p._path, 'DocumentTypeService', 'findByNameCaseInsensitive', arguments);
    };

    /**
     * @param {class java.lang.String} p0 a param
     * @param {function|Object} callback callback function or options object
     */
    p.findById = function(p0, callback) {
      return dwr.engine._execute(p._path, 'DocumentTypeService', 'findById', arguments);
    };
    
    dwr.engine._setObject("DocumentTypeService", p);
  }
})();

