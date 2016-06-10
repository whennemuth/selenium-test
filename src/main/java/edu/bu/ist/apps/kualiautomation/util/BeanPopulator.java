package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class populates a simple bean by its setter methods with the corresponding values in a map (beanMap).
 * A match is determined by comparing the key value of map to setters of the bean - ie: "firstName" matches bean.setFirstName.
 *  
 * @author whennemuth
 *
 */
public class BeanPopulator {
	
	/** map of values to populate the bean with */
	private Map<String, String> valueMap;
	/** Don't populate the bean with values that are empty (true/false) */
	private boolean ignoreEmpties;

	/**
	 * BeanPopulator constructor.
	 * 
	 * @param valueMap map of values to populate the bean with.
	 * @param ignoreEmpties If a value to be set on the object being populated is empty, then do not set that value if ignoreEmpties is true.
	 */
	public BeanPopulator(Map<String, String> valueMap, boolean ignoreEmpties) {
		this.valueMap = valueMap;
		this.ignoreEmpties = ignoreEmpties;
	}

	public <T> T getPopulatedObject(AbstractFieldSetter valueFilter, Class<T> beanClass) throws Exception {
		T bean = beanClass.newInstance();
		populateObject(valueFilter, bean);
		return bean;
	}

	public <T> T getPopulatedObject(Class<T> beanClass) throws Exception {
		T bean = beanClass.newInstance();
		populateObject(bean);
		return bean;
	}

	
	public Object populateObject(Object bean) throws Exception {
		
		populateObject(new AbstractFieldSetter(bean) {

			@Override public Object getFieldValue(String fldName, Object existingValue) throws Exception {
				return existingValue;
			}
			@Override public boolean ignoreField(String fldName) {
				return false;
			}
			
		}, bean);
		
		return bean;
	}

	
	public void populateObject(AbstractFieldSetter valueFilter, Object bean) throws Exception {
		
		for(String fieldName : valueMap.keySet()) {
			
			String tempValue = String.valueOf(valueMap.get(fieldName));
			if(tempValue.equals("null")) {
				tempValue = null;
			}
			
			Object o = null;
			String stringValue = null;
			if(valueFilter == null) {
				if(tempValue != null) {
					stringValue = String.valueOf(tempValue);
				}
			}
			else {
				o = valueFilter.getFieldValue(fieldName, tempValue);
				if(o != null) {
					stringValue = String.valueOf(o);
				}
			}
			
			for(Method m : bean.getClass().getMethods()) {
				
				String methodName = m.getName();
				if(methodName.length() < 4 || !methodName.matches("set[A-Z].*")) {
					continue;
				}
				if(!methodName.substring(3).equalsIgnoreCase(fieldName)) {
					continue;
				}
				if(m.getParameterTypes().length == 0) {
					continue;
				}
				if(valueFilter != null && (valueFilter.ignoreField(fieldName))) {
					continue;
				}
				else if(isEmpty(stringValue) && ignoreEmpties) {
					continue;
				}
					
				Object fv = null;
				if(isEntity(o))
					fv = o;
				else 
					fv = valueFilter.convertStringValue(m.getParameterTypes()[0], stringValue, methodName, valueFilter);
				
				if(isEmpty(fv) && ignoreEmpties) {
					//logger.warn("Value not set on bean [" + bean.getClass().getSimpleName() + "] for field: " + methodName + "(" + parmType + ")");
				}
				else {
					m.invoke(bean, fv);
				}
			}
		}
	}
	
	private boolean isEntity(Object o) {
		if(o == null)
			return false;
		try {
			EntityInspector ei = new EntityInspector(o);
			return ei.isEntity();
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static Object populate(Object bean, Map<String, String> properties) throws Exception {
		BeanPopulator beanPopulator = new BeanPopulator(properties, true);
		bean = beanPopulator.populateObject(bean);
		return bean;
	}
	
	public static Object populate(Object bean, Map<String, String> properties, Map<String, String> dateFormatMap) throws Exception {
		
		AbstractFieldSetter fieldSetter = new AbstractFieldSetter(bean) {

			@Override public Object getFieldValue(String fldName, Object existingValue) throws Exception {
				return existingValue;
			}
			@Override public boolean ignoreField(String fldName) {
				return false;
			}			
		};
		
		fieldSetter.setDateFormatMap(dateFormatMap);
		BeanPopulator beanPopulator = new BeanPopulator(properties, true);
		beanPopulator.populateObject(fieldSetter, bean);
		return bean;		
	}
	
//	public static Object populate(Object bean, Map<String, String> properties) 
//			throws IllegalAccessException, InvocationTargetException {
//
//		if ((bean == null) || (properties == null)) {
//			return null;
//		}
//		String method = null;
//		Object value = null;
//		for (Method m : bean.getClass().getMethods()) {
//			method = m.getName();
//			value = properties.get(method);
//			if (properties.containsKey(method) && !isEmpty(value)){
//				m.invoke(bean, value);
//			}
//		}
//		return bean;
//	}
	
	public static boolean isNull(Object o) {
		return "null".equalsIgnoreCase(String.valueOf(o)) || o == null;
	}

	public static boolean isEmpty(Object o) {
		return isNull(o) || String.valueOf(o).length() == 0;
	}

}
