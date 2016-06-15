package edu.bu.ist.apps.kualiautomation.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import edu.bu.ist.apps.kualiautomation.entity.util.EntityInspector;

public class Utils {

	public static String stackTraceToString(Throwable e) {
		if(e == null)
			return null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		String trace = sw.getBuffer().toString();
		return trace;
	}
	
	public static boolean isEmpty(String val) {
		return (val == null || val.trim().length() == 0);
	}
	
	public static boolean isEmpty(Object val) {
		try {
			return (val == null || val.toString().length() == 0);
		} catch (Exception e) {
			return String.valueOf(val).length() == 0;
		}
	}
	
	public static boolean anyEmpty(String... vals) {
		for(int i=0; i<vals.length; i++) {
			if(isEmpty(vals[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNumeric(String val) {
		if(isEmpty(val))
			return false;
		return val.matches("\\d+");
	}

	/**
	 * Turn a bean into a Map
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> beanToMap(Object bean) throws Exception {
		Map<String, Object> map  = new HashMap<String, Object>();		
		Method[] methods = bean.getClass().getMethods();
		for(Method method : methods) {
			if(method.getName().startsWith("get")) {
				if(method.getParameterTypes().length == 0) {
					String fldName = ReflectionUtils.getFieldName(method.getName());
					Object fldVal = method.invoke(bean, (Object[]) null);
					map.put(fldName, fldVal);
				}
			}
		}
		return map;
	}
	
	/**
	 * Get the directory containing the jar file whose code is currently running.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static File getRootDirectory() throws Exception {
		String path = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		File f = new File(decodedPath);
		if(f.isFile() && f.getName().endsWith(".jar")) {
			return f.getParentFile();
		}
		return f;
    }

}
