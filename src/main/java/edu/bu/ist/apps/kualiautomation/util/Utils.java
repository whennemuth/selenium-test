package edu.bu.ist.apps.kualiautomation.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;

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
	
	/**
	 * Convert a field name to the standard mutator method name, ie: "name" becomes "setName"
	 * @param fldName
	 * @return
	 */
	public static String getMutatorName(String fldName) {
		if(fldName == null)
			return null;
		String setter = new String(fldName);
		if(!fldName.startsWith("set")) {
			setter = "set" + setter.substring(0, 1).toUpperCase() + setter.substring(1);
		}
		return setter;
	}
	
	/**
	 * From the name of an accessor or a mutator, get the name of the target field (remove "set" or "get" and lowercase the first character of the result)
	 * @param methodName
	 * @return
	 */
	public static String getFieldName(String methodName) {
		return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
	}
		
	public static String getAccessorName(String fldName) {
		if(fldName == null)
			return null;
		return getMutatorName(fldName).replaceFirst("set", "get");
	}
	
	public static Object getAccessorValue(Object o, String fldName) throws Exception {
		return getAccessorValue(o, fldName, false);
	}
	
	/**
	 * Get the value of one of the accessors of an object identified by its private field name.
	 * IE: fldName = "firstName" returns o.getFirstName().
	 * 
	 * @param o
	 * @param fldName
	 * @param ignoreFldCase
	 * @return
	 * @throws Exception
	 */
	public static Object getAccessorValue(Object o, String fldName, boolean ignoreFldCase) throws Exception {
		if(fldName == null || o == null)
			return null;
		Object val = null;
		String methodName = getAccessorName(fldName);
		Method method = getMethod(methodName, o.getClass(), true);
		if(method != null) {
			val = method.invoke(o);
		}
		return val;
	}	
	
	/**
	 * This method is an alternative to using the getMethod(String name, Class... parameterTypes) method
	 * if the name is known, only one parameter is expected but its Class is unknown.
	 * 
	 * @param name
	 * @param mutatorClass
	 * @return
	 */
	public static Method getMutatorMethod(String name, @SuppressWarnings("rawtypes") Class mutatorClass) {
		Method m = getMethod(name, mutatorClass);
		if(m.getParameterTypes().length == 1) {
			return m;
		}
		return null;
	}

	/**
	 * This method is an alternative to using the getMethod(String name, Class... parameterTypes) method
	 * 
	 * @param methodName
	 * @param clazz
	 * @return
	 */
	public static Method getMethod(String methodName, @SuppressWarnings("rawtypes") Class clazz) {
		return getMethod(methodName, clazz, false);
	}
	
	public static Method getMethod(String methodName, @SuppressWarnings("rawtypes") Class clazz, boolean ignorecase) {
		Method[] methods = clazz.getMethods();
		for(Method m : methods) {
			if(m.getName().equals(methodName) || (ignorecase && m.getName().equalsIgnoreCase(methodName))) {
				return m;
			}
		}
		return null;
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
