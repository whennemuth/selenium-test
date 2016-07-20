package edu.bu.ist.apps.kualiautomation.entity.util;

import java.lang.reflect.Method;
import java.util.Collection;

import edu.bu.ist.apps.kualiautomation.entity.User;
import edu.bu.ist.apps.kualiautomation.util.ReflectionUtils;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This is the top-level class for populating an entity.
 * Its done with reflection through its mutator methods with values of updated fields of a corresponding "shallow" version
 * of the entity, probably de-serialized by Jersey/Jackson enroute through a web service endpoint.
 * 
 * Essentially the effect is to perform what you would expect of an entity with CascadeType=ALL set on all of
 * its foreign key bi-directionally annotated fields. Problems occur attempting that approach with an entity
 * that has a deep, parent, grandparent setup of contained entities, where edits, removals and additions can
 * all have been made to any of them. In other words, standard cascading would work for simple single field 
 * or entity changes. There may be a way to have native cascading functionality work for the more complex
 * use-case, but it eludes me for now.
 * 
 * @author whennemuth
 *
 */
public class EntityPopulator {
	
	private boolean ignoreEmpties;
	private Entity entity;
	
	public EntityPopulator() { }
		
	public EntityPopulator(boolean ignoreEmpties) {
		this.ignoreEmpties = ignoreEmpties;
	}
	
	public EntityPopulator(Entity entityPopulator, boolean ignoreEmpties) {
		this.ignoreEmpties = ignoreEmpties;
		this.entity = entityPopulator;
	}
		
	public void populate(Object entityToPopulate, Object sourceBean) {
		
		if(entity != null && entity.isTransactionActive() == false) {
			System.out.println("No active transaction! Cancelling bean population");
			return;
		}
		
		StringBuilder errors = new StringBuilder();
		
		for(Method getterMethod : sourceBean.getClass().getMethods()) {
			
			try {
				Method setterMethod = ReflectionUtils.getMutator(getterMethod, entityToPopulate.getClass());
				
				if(setterMethod == null) {
					continue;
				}
				
				Object val = getterMethod.invoke(sourceBean);

				if(handledAsEntity(entityToPopulate, val, getterMethod, setterMethod)) {
					continue;
				}
				
				if(handledAsEntityCollection(entityToPopulate, val, getterMethod, sourceBean)) {
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
				setterMethod.invoke(entityToPopulate, val);				
			} 
			catch (Exception e) {
				e.printStackTrace(System.out);
				if(errors.length() > 0)
					errors.append(", ");
				errors.append(getterMethod.getName());
				if(entity != null) {
					entity.rollback();
					break;
				}
			}
		}
		
		if(errors.length() > 0) {
			System.out.println("Field setter failures for " + entityToPopulate.getClass().getName() + ": " + errors);
		}
	}
	
	public static int count = 0;
	private boolean handledAsEntity(Object entityToPopulate, Object val, Method getterMethod, Method setterMethod) throws Exception {
		if(EntityInspector.isEntity(val)) {
			if(!isTransitory(val)) {
				count++;
				boolean populated = entity.populate(entityToPopulate, getterMethod.getName(), val);
				if(!populated) {
					setterMethod.invoke(entityToPopulate, val);
					if(entity != null) {
						entity.checkMerge(val);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean handledAsEntityCollection(Object entityToPopulate, Object val, Method getterMethod, Object sourceBean) throws Exception {
		if(val instanceof Collection) { 
			if(EntityInspector.returnsEntityCollection(getterMethod)) {
				if(!isTransitory(sourceBean)) {
					Collection<?> sourceCollection = (Collection<?>) val;
					entity.populateCollection(entityToPopulate, getterMethod, sourceCollection);
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isTransitory(Object bean) throws Exception {
		Boolean transitory = (Boolean) ReflectionUtils.getAccessorValue(bean, "transitory");
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
		(new EntityPopulator()).populate(user1, user2);
	}
}
