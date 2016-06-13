package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Method;
import java.util.Collection;

import edu.bu.ist.apps.kualiautomation.entity.User;

/**
 * Populate a bean through its setters from the getters of another bean.
 * Where the setters of the bean to populate match the getters of the source bean, an attempt to set the value will be made if 
 * the data types are the same or both are one of the following: string, integer, or boolean (conversion will occur).
 * 
 * @author whennemuth
 *
 */
public class SimpleBeanPopulator {
	
	private boolean ignoreEmpties;
	private EntityPopulator entityPopulator;
	
	public SimpleBeanPopulator() { }
		
	public SimpleBeanPopulator(boolean ignoreEmpties) {
		this.ignoreEmpties = ignoreEmpties;
	}
	
	public SimpleBeanPopulator(EntityPopulator entityPopulator, boolean ignoreEmpties) {
		this.ignoreEmpties = ignoreEmpties;
		this.entityPopulator = entityPopulator;
	}
		
	public void populate(Object beanToPopulate, Object sourceBean) {
		StringBuilder errors = new StringBuilder();
		
		for(Method getterMethod : sourceBean.getClass().getMethods()) {
			
			try {
				Method setterMethod = Utils.getMutator(getterMethod, beanToPopulate.getClass());
				
				if(setterMethod == null) {
					continue;
				}
				
				Object val = getterMethod.invoke(sourceBean);

				if(handledAsEntity(beanToPopulate, val, getterMethod, setterMethod)) {
					continue;
				}
				
				if(handledAsEntityCollection(beanToPopulate, val, getterMethod, sourceBean)) {
					continue;
				}
				
				if(isTransitory(sourceBean)) {
					continue;
				}
				
				val = getConvertedValue(val, getterMethod, setterMethod.getParameterTypes()[0]);
				
				if(val == null) {
					continue;	// Insufficient functionality to convert the accessor output to the mutator parameter type.
								// More work can be done to increase conversion capability, but it captures the common scenarios.
				}
				
				if(ignoreEmpties && Utils.isEmpty(val)) {
					continue;
				}
				
				System.out.println("Invoking setter: " + setterMethod.getName() + " = " + String.valueOf(val));
				setterMethod.invoke(beanToPopulate, val);				
			} 
			catch (Exception e) {
				e.printStackTrace(System.out);
				if(errors.length() > 0)
					errors.append(", ");
				errors.append(getterMethod.getName());
			}
		}
		
		if(errors.length() > 0) {
			System.out.println("Field setter failures for " + beanToPopulate.getClass().getName() + ": " + errors);
		}
	}
	
	private boolean handledAsEntity(Object beanToPopulate, Object val, Method getterMethod, Method setterMethod) throws Exception {
		if(EntityInspector.isEntity(val)) {
			if(!isTransitory(val)) {
				boolean populated = entityPopulator.populate(beanToPopulate, getterMethod.getName(), val);
				if(!populated) {
					setterMethod.invoke(beanToPopulate, val);
					if(entityPopulator != null) {
						entityPopulator.checkMerge(val);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean handledAsEntityCollection(Object beanToPopulate, Object val, Method getterMethod, Object sourceBean) throws Exception {
		if(val instanceof Collection) {
			if(EntityInspector.returnsEntityCollection(getterMethod)) {
				if(!isTransitory(sourceBean)) {
					Collection<?> sourceCollection = (Collection<?>) val;
					entityPopulator.populateCollection(beanToPopulate, getterMethod, sourceCollection);
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isTransitory(Object bean) throws Exception {
		Boolean transitory = (Boolean) Utils.getAccessorValue(bean, "isTransitory");
		if(transitory == null)
			return false;
		return transitory;
	}
	
	/**
	 * Convert from a String, Integer, Boolean or Byte to either a String, Integer, Boolean or Byte.
	 * If the name of the class to convert from or to is not among these 4, then return null - that is,
	 * for now the populating only affects fields where the source and destination values are of these data types.
	 * 
	 * @param val
	 * @param getterClass
	 * @param targetClass
	 * @return
	 * @throws Exception
	 */
	private Object getConvertedValue(Object val, Method getterMethod, Class<?> targetClass) throws Exception {
		if(val == null) {
			return null;
		}
		String from = getterMethod.getReturnType().getSimpleName().toLowerCase();
		String to = targetClass.getSimpleName().toLowerCase();
		boolean toInt = "integer".equals(to) || "int".equals(to);
		boolean toString = "string".equals(to);
		boolean toBoolean = "boolean".equals(to);
		boolean toByte = "byte".equals(to);
		
		switch(from) {
		case "string":
			if(toString) {
				return String.valueOf(val);
			}
			if(toInt && Utils.isNumeric(String.valueOf(val))) {
				return Integer.valueOf(String.valueOf(val));
			}
			if(toBoolean) {
				return new Boolean(String.valueOf(val).matches("(true)|(1)|(yes)"));
			}
			if(toByte) {
				return new Byte(String.valueOf(val).matches("(true)|(1)|(yes)") ? (byte) 1 : (byte) 0);
			}
			break;
		case "integer": case "int":
			if(toString) {
				return String.valueOf(val);
			}
			if(toInt) {
				return (Integer) val;
			}
			if(toBoolean) {
				return (new Integer(1)).equals(val);
			}
			if(toByte) {
				return new Byte((new Integer(1)).equals(val) ? (byte) 1 : (byte) 0);
			}
			break;
		case "boolean":
			if("isTransitory".equals(getterMethod.getName()))
				break;
			if(toString) {
				return String.valueOf(val);
			}
			if(toInt) {
				return new Integer(((Boolean) val) ? 1 : 0);
			}
			if(toBoolean) {
				return (Boolean) val;
			}
			if(toByte) {
				return ((Boolean) val) ? (byte) 1 : (byte) 0;
			}
			break;
		case "byte":
			Byte b = (Byte) val;
			if(toString) {
				return String.valueOf(b.intValue());
			}
			if(toInt) {
				return new Integer(b.intValue());
			}
			if(toBoolean) {
				return b.intValue() == 1;
			}
			if(toByte) {
				return b;
			}
			break;
		case "date":
			// For future enhancement. May require injecting in a field/date expression map.
			break;
		default:
			if(targetClass.isAssignableFrom(getterMethod.getReturnType())) {
				/**
				 * The class or interface represented by targetClass is either the same as,
				 * or is a superclass or superinterface of, getterMethod.getReturnType().
				 */
				return val;
			}
			break;
		}

		return null;
	}
	
	public static void main(String[] args) {
		User user1 = new User();
		User user2 = new User();
		(new SimpleBeanPopulator()).populate(user1, user2);
	}
}
