package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import edu.bu.ist.apps.kualiautomation.util.EquatableEntity.ObjectMatcher;

/**
 * This class is used in conjuction with SimpleBeanPopulator to recursively populate
 * @author wrh
 *
 */
public class EntityPopulator {
	private boolean ignoreEmpties;
	private EntityManager em;
	private ObjectMatcher matcher;
	
	@SuppressWarnings("unused")
	private EntityPopulator() { /* Restrict default constructor */ }
	
	public EntityPopulator(EntityManager em, boolean ignoreEmpties) {
		this.em = em;
		this.ignoreEmpties = ignoreEmpties;
	}

	public EntityPopulator(EntityManager em, boolean ignoreEmpties, ObjectMatcher matcher) {
		this.em = em;
		this.ignoreEmpties = ignoreEmpties;
		this.matcher = matcher;
	}
	
	public boolean populate(Object objectToPopulate, String getterMethodName, Object sourceEntity) throws Exception {
		Object targetEntity = Utils.getAccessorValue(objectToPopulate, getterMethodName);
		SimpleBeanPopulator populator = new SimpleBeanPopulator(this, ignoreEmpties);
		
		EquatableEntity source = EquatableEntity.getInstance(sourceEntity);
		EquatableEntity target = EquatableEntity.getInstance(targetEntity);
		if(source.equals(target)) {
			populator.populate(target.getEntity(), source.getEntity());
			return true;
		}
		return false;
	}

	public <T> void populateCollection(Object parentEntity, Method getterMethod, Collection<T> sourceCollection) throws Exception {		
		List<EquatableEntity> sources = new ArrayList<EquatableEntity>();
		List<EquatableEntity> targets = new ArrayList<EquatableEntity>();
		
		// 1) Get the method to use to add entities to the parentEntity
		String adderMethodName = getterMethod.getName().replaceFirst("get", "add");
		if(adderMethodName.endsWith("s")) {
			adderMethodName = adderMethodName.substring(0, adderMethodName.length() - 1);
		}
		Type collectionType = EntityInspector.getCollectionType(getterMethod);
		Method adderMethod = Utils.getMethod(adderMethodName, collectionType.getClass());
		
		// 2) Get the collection that is to be updated
		@SuppressWarnings("unchecked")
		Collection<T> targetCollection = (Collection<T>) Utils.getAccessorValue(parentEntity.getClass(), getterMethod.getName());
		
		// 3) Populate equatable sources and targets lists
		for (Iterator<T> iterator = sourceCollection.iterator(); iterator.hasNext();) {
			sources.add(EquatableEntity.getInstance(iterator.next()));
		}
		for (Iterator<T> iterator = targetCollection.iterator(); iterator.hasNext();) {
			sources.add(EquatableEntity.getInstance(iterator.next()));
		}
		
		// 4) Perform additions and updates to entities in the target collection
		for (Iterator<EquatableEntity> iterator = sources.iterator(); iterator.hasNext();) {
			EquatableEntity source = (EquatableEntity) iterator.next();
			if(targets.contains(source)) {
				// 4a) Populate the target entity with changes in the source entity
				for (Iterator<EquatableEntity> iterator2 = targets.iterator(); iterator2.hasNext();) {
					EquatableEntity target = (EquatableEntity) iterator2.next();
					if(target.equals(source)) {
						SimpleBeanPopulator populator = new SimpleBeanPopulator(this, ignoreEmpties);
						populator.populate(target.getEntity(), source.getEntity());
						// Assuming calling process will call merge on the EntityManager for an entity 
						// further up the parent hierchy. NOTE: This requires CascadeType=MERGE or CascadeType=ALL
						// em.persist(parentEntity);
					}
				}
			}
			else {
// RESUME NEXT: Additions not working - addition of environment results in new environment replacing an existing one.
				// 4b) Add the source entity to the target collection
				// BIG ASSUMPTION: the target parent entity will have an "add" method for adding to corresponding collection.
				adderMethod.invoke(parentEntity, source.getEntity());
				// Assuming calling process will call persist on the EntityManager for an entity 
				// further up the parent hierchy. NOTE: This requires CascadeType=PERSIST or CascadeType=ALL
				// em.merge(parentEntity);
			}
		}

		// 5) Perform entity removals from the target collection
		for (Iterator<EquatableEntity> iterator = targets.iterator(); iterator.hasNext();) {
			EquatableEntity target = (EquatableEntity) iterator.next();
			if(!sources.contains(target)) {
				em.remove(target.getEntity());
			}
		}
	}
	
}
