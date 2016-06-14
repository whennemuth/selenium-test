package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.persistence.EntityManager;

public class EntitySaver {

	private EntityManager em;
	private Object entity;
	private EntityInspector inspector;
	
	public EntitySaver(EntityManager em, Object entity) {
		this.em = em;
		this.entity = entity;
		this.inspector = new EntityInspector(entity);
	}
	
	public void persist() throws Exception {
		
		em.persist(entity);
		
		handleChildren();
	}
	
	private void handleChildren() throws Exception {
		for(Field manyFld : inspector.getOneToManyFields()) {
			
			// Method setterMethod = Utils.getMutator(collectionFld.getName(), entity.getClass());
			
			// 1) Cache the children
			Collection<?> entities = (Collection<?>) inspector.getValue(manyFld);
			
			// 2) Remove the children from the parent entity
			Method getterMethod = Utils.getMethod(manyFld.getName(), entity.getClass());
			((Collection<?>) Utils.getAccessorValue(entity, manyFld.getName())).clear();
			
			// 3) Add the children back one by one after being persisted/merged and becoming managed by the entity manager
			for(Object childEntity : entities) {
				if(!em.contains(childEntity)) {
					Object id = (new EntityInspector(childEntity)).getPrimaryKeyValue();
					if(id == null) {
						Method adderMethod = Utils.getMutator(entity, getterMethod, "add");
						// RESUME NEXT: Set the "@ManyToOne field of the child with the parent entity and then persist it, the recurse it.
					}
					else {
						// Merge the child.
					}
				}
			}
		}
	}
}
