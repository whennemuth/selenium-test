package edu.bu.ist.apps.kualiautomation.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

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
		String setter = null;
		if(fldName.matches("((get)|(is))[A-Z].*")) {
			setter = new String(fldName.replaceFirst("get", "set"));
		}
		else if(fldName.matches("(set)[A-Z].*")) {
			setter = new String(fldName);
		}
		else {
			setter = new String("set" + fldName.substring(0, 1).toUpperCase() + fldName.substring(1));
		}
		return setter;
	}
	
	/**
	 * Determine if given method is a mutator.
	 * The method must have one parameter and its name must be of either get[Xxxx...] or is[Xxxx...] formats
	 * @param setterMethod
	 * @return
	 */
	public static boolean isAccessor(Method m) {
		if(m == null)
			return false;
		if(m.getReturnType() == null)
			return false;
		if(!m.getName().matches("((get)|(is))[A-Z].*")) 
			return false;
		if(m.getParameterTypes().length != 0)
			return false;
		
		return true;
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
	 * Get the name of the corresponding mutator method given the accessor method.
	 * The accessor must have no parameters and must be of either get[Xxxx...] or is[Xxxx...] formats.
	 * The mutator must follow the set[Xxxx...] naming format and must have one parameter.
	 * 
	 * @param setterMethod
	 * @return
	 */
	public static Method getMutator(Method getterMethod, @SuppressWarnings("rawtypes") Class mutatorClass) {
		
		if(!isAccessor(getterMethod))
			return null;
		
		String setterName = null;
		if("boolean".equalsIgnoreCase(getterMethod.getReturnType().getSimpleName())) {
			setterName = getterMethod.getName().replaceFirst("is", "set");
			if(setterName.startsWith("get")) {
				setterName = setterName.replaceFirst("get", "set");
			}
		}
		else {
			setterName = getterMethod.getName().replaceFirst("get", "set");
		}
		try {
			Method mutator = getMutatorMethod(setterName, mutatorClass);
			if(mutator.getParameterTypes().length != 1) {
				return null;
			}
			return mutator;
		} 
		catch (Exception e) {
			return null;
		}
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
	
	public static boolean isEmpty(Object o, String fldName) throws Exception {
		Object value = getAccessorValue(o, fldName);
		return isEmpty(value);
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
					String fldName = getFieldName(method.getName());
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
