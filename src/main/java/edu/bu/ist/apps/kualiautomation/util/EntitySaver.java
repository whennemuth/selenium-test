package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * An entity that has been created by Jackson/Jersey through deserialization would not have any @ManyToOne fields
 * that contain collections that create bi-directional endless loop issues. However, if the entity has been persisted,
 * the bi-directional issue comes back. This class populates such an entity by traversing recursively only the
 * @OneToMany collection fields and persisting/merging any new entities found. This is a uni-directional procedure
 * and does not cause endless loop issues.
 * NOTE: 
 *    1) This does not account for @OneToMany fields
 *    2) Entities must have an "add" method for their @OneToMany collections containing the following convention:
 *          get[childType]s().add(child);
 *          child.set[thisType](this);
 *    3) all accessor and mutator methods for the entities that are used here must follow standard bean naming conventions.
 *    4) Do not set the CascadeType to persist. This class would not be necessary if that worked, but while the @OneToMany
 *       entities are persisted, their own @ManyToOne back-references remain null, which is not what we want. Ostensibly,
 *       you can remedy this by removing the mappedBy annotation attribute from the @OneToMany annotation and create one
 *       for the corresponding on the @ManyToOne annotation of the foreign key entity. I'm not sure what kind of weirdness
 *       may result if this is done, so I'm putting it on the side for now.
 * @author wrh
 *
 */
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
	
	@SuppressWarnings("unchecked")
	private <T> void handleChildren() throws Exception {
		for(Field manyFld : inspector.getOneToManyFields()) {
			
			/** 1) Cache the children */
			List<T> entities = new ArrayList<T>((Collection<T>) inspector.getValue(manyFld));			
			
			/** 2) Remove the children from the parent entity */
			Method getterMethod = Utils.getAccessorMethod(entity, manyFld);
			((Collection<T>) Utils.getAccessorValue(entity, manyFld.getName())).clear();
			
			/** 3)
			 * The adder method will not only add the child to the @OneToMany 
			 * collection of the parent, but is expected to also set the parent against to the @ManyToOne 
			 * field of the child. Only then can a persist of the child work without incurring a required 
			 * field exception.
			 */
			Method adderMethod = Utils.getMutator(entity, getterMethod, "add");
			if(adderMethod == null) {
				throw new IllegalStateException("Expected \"add\" method for collection " + 
						manyFld.getName() + " in entity " + entity.getClass().getSimpleName() + ", but not found.");
			}
			
			/** 3) Add the @OneToMany entities back one by one after being persisted/merged if needed. */
			for(T childEntity : entities) {
				
				adderMethod.invoke(entity, childEntity);
				
				if(em.contains(childEntity)) {
					/** The entity is managed and so its own @OneToMany field(s) should contain entities that are as well. */
				}
				else {
					Object id = (new EntityInspector(childEntity)).getPrimaryKeyValue();
					if(id == null) {
						em.persist(childEntity);
					}
					else {
						// Merging might also be handled by a CascadeType=MERGE further up the entity hierarchy.
						em.merge(childEntity);
					}
					
					/** 4) Recurse in case the childEntity has @OneToMany fields of its own. */
					EntitySaver saver = new EntitySaver(em, childEntity);
					saver.handleChildren();
				}
			}
		}
	}
}
