package edu.bu.ist.apps.kualiautomation.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This class wraps and entity class and provides for reflective analysis on its fields, methods and annotations
 * 
 * @author whennemuth
 *
 */
public class EntityInspector {
	
	private Object inspectableObj;
	private Class<?> inspectableClass;
	private Class<?> primaryKeyType;
	
	public EntityInspector(Object inspectableObj) {
		this.inspectableObj = inspectableObj;
		this.inspectableClass = inspectableObj.getClass();
	}
	
	public EntityInspector(Class<?> inspectableClass) {
		this.inspectableClass = inspectableClass;
	}

	public boolean isEntity() {
		return inspectableClass.getAnnotation(Entity.class) != null;
	}

	public List<Method> getEntitiesAccessors() {
		List<Method> accessors = new ArrayList<Method>();
		Method[] methods = inspectableClass.getMethods();
		for(Method method : methods) {
			if(Utils.isAccessor(method)) {
				EntityInspector inspector = new EntityInspector(method.getReturnType());
				if(inspector.isEntity() && inspector.hasPrimaryKey()) {
					String setterName = method.getName().replaceFirst("get", "set");
					Class<?> entityParm = inspector.getEntityParm(setterName);
					if(entityParm != null) {
						accessors.add(method);
					}
				}
			}
		}
		return accessors;
	}
	
	/**
	 * Iterate over the entity type being inspected for a specified setter method and return its 
	 * parameter if it is an entity.
	 * @param setterMethodName
	 * @return
	 */
	public Class<?> getEntityParm(String setterMethodName) {
		String setterName = Utils.getMutatorName(setterMethodName);
		Method[] methods = inspectableClass.getMethods();
		for(Method method : methods) {
			if(method.getName().equals(setterName)) {
				if(method.getParameterTypes().length == 1) {
					Class<?> parmClass = method.getParameterTypes()[0];
					EntityInspector parm = new EntityInspector(parmClass);
					if(parm.isEntity()) {
						return parmClass;
					}
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 * Indicate if the entity type being inspected has a field or method annotated 
	 * with @Id (entity primary key).
	 * @return
	 */
	public boolean hasPrimaryKey() {
		if(primaryKeyType == null) {
			primaryKeyType = getPrimaryKeyType();
		}
		return primaryKeyType != null;
	}
	
	/**
	 * Get the type (or return type) of the field or method inside the entity type being inspected 
	 * that is annotated with @Id (entity primary key).
	 * @return
	 */
	public Class<?> getPrimaryKeyType() {
		if(primaryKeyType == null) {
			/**
			 * TODO: Add @EmbeddedId as a primary key annotation.
			 */
			primaryKeyType = getAnnotatedMemberType(Id.class);
		}
		return primaryKeyType;
	}
	
	/**
	 * Iterate over fields and methods of the 
	 * @param annotationClass
	 * @return
	 */
	public Class<?> getAnnotatedMemberType(Class<?> annotationClass) {
		Field f = getAnnotatedField(inspectableClass, (Class<Annotation>) annotationClass);
		if(f == null) {
			Method m = getAnnotatedMethod(inspectableClass, (Class<Annotation>) annotationClass);
			if(m == null) {
				return null;
			}
			else {
				return m.getReturnType() == null ? null : m.getReturnType();
			}
		}
		else {
			return f.getType();
		}
	}
	
	/**
	 * Search a specified class for a Field that is annotated with the specified annotation.
	 * Search up through the super-classes as well.
	 * @param clazz
	 * @param annotationClass
	 * @return
	 */
	private Field getAnnotatedField(Class<?> clazz, Class<Annotation> annotationClass) {
		for(Field f : clazz.getDeclaredFields()) {
			Annotation a = f.getAnnotation(annotationClass);
			if(a != null && a.annotationType().equals(annotationClass)) {
				return f;
			}
		}
		if(clazz.getSuperclass() != null && clazz.getSuperclass().equals(Object.class) == false) {
			return getAnnotatedField(clazz.getSuperclass(),  annotationClass);
		}
		return null;
	}
	
	/**
	 * Search a specified class for a Method that is annotated with the specified annotation.
	 * Search up through the super-classes as well.
	 * @param clazz
	 * @param annotationClass
	 * @return
	 */
	private Method getAnnotatedMethod(Class<?> clazz, Class<Annotation> annotationClass) {
		for(Method m : clazz.getMethods()) {
			Annotation a = m.getAnnotation(annotationClass);
			if(a != null && a.annotationType().equals(annotationClass)) {
				return m;
			}
		}
		if(clazz.getSuperclass() != null && clazz.getSuperclass().equals(Object.class) == false) {
			return getAnnotatedMethod(clazz.getSuperclass(),  annotationClass);
		}
		return null;
	}
	
	/**
	 * Indicate if the entity being inspected has a specified annotation.
	 * @param annotationClass
	 * @return
	 */
	private boolean hasAnnotation(Class<Annotation> annotationClass) {
		Annotation a = inspectableClass.getAnnotation(annotationClass);
		return a != null;
	}
	
}

