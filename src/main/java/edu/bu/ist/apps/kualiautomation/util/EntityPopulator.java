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
		else {
			// Set the sourceEntity, but don't assume it is managed - getting a managed version first if it is not.
			Object managedSource = null;
			if(em.contains(sourceEntity)) {
				managedSource = sourceEntity;
			}
			else {
				Object id = (new EntityInspector(source.getEntity())).getPrimaryKeyValue();
				managedSource = em.find(sourceEntity.getClass(), id);
			}
			if(managedSource != null) {
				Method setterMethod = Utils.getMutator(getterMethodName, objectToPopulate.getClass());
				setterMethod.invoke(objectToPopulate, managedSource);
				return true;
			}
		}
		return false; // Means sourceEntity is detached and has no primary key set, which
					  // probably a new entity, which is an unaccounted use-case scenario for now.
	}

	/**
	 * An entity is provided so that one of its @OneToMany annotated collection fields (targetCollection)
	 * can be updated by a corresponding collection. By "updated" is meant that the targetCollection will be modified to
	 * look like the sourceCollection, but not by adding members of the sourceCollection or by modifying the targetCollection
	 * directly. Instead, use of adds, removals, and merges against the parent entity along with EntityManager methods 
	 * are favored so as not to introduce detached entities and other errors that would show up during persists, merges or commits.
	 * 
	 * @param parentEntity The entity in which targetCollection is a field.
	 * @param getterMethod The getter method that was used to obtaind the sourceCollection against the source entity.
	 * @param sourceCollection The collection that is used to populate the targetCollection.
	 * @throws Exception
	 */
	public <T> void populateCollection(Object parentEntity, Method getterMethod, Collection<T> sourceCollection) throws Exception {		
		List<EquatableEntity> sources = new ArrayList<EquatableEntity>();
		List<EquatableEntity> targets = new ArrayList<EquatableEntity>();
		
		// 1) Get the method to use to add entities to the parentEntity
		Method adderMethod = getAddRemoveFromGetter(parentEntity, getterMethod, "add");
		
		Method removeMethod = getAddRemoveFromGetter(parentEntity, getterMethod, "remove");
		
		// 2) Get the collection that is to be updated
		@SuppressWarnings("unchecked")
		Collection<T> targetCollection = (Collection<T>) Utils.getAccessorValue(parentEntity, getterMethod.getName());
		
		// 3) Populate equatable sources and targets lists
		for (Iterator<T> iterator = sourceCollection.iterator(); iterator.hasNext();) {
			sources.add(EquatableEntity.getInstance(iterator.next()));
		}
		for (Iterator<T> iterator = targetCollection.iterator(); iterator.hasNext();) {
			targets.add(EquatableEntity.getInstance(iterator.next()));
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
						System.out.println("Start populating entity [" + target.getEntity().getClass().getSimpleName() + "]...");
						populator.populate(target.getEntity(), source.getEntity());
						System.out.println("End populating entity [" + target.getEntity().getClass().getSimpleName() + "]...");
						// Assuming calling process will call merge on the EntityManager for an entity 
						// further up the parent hierchy. NOTE: This requires CascadeType=MERGE or CascadeType=ALL
						// em.persist(parentEntity);
					}
				}
			}
			else {
				// 4b) Add the source entity to the target collection
				// BIG ASSUMPTION: the target parent entity will have an "add" method for adding to corresponding collection.
				System.out.println("Invoking method: " + adderMethod.getName() + " [" + 
						source.getEntity().getClass().getSimpleName() + " to " + 
						parentEntity.getClass().getSimpleName() + "]");
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
				System.out.println("Invoking method: " + removeMethod.getName() + " [" + 
						target.getEntity().getClass().getSimpleName() + " from " + 
						parentEntity.getClass().getSimpleName() + "]");
				removeMethod.invoke(parentEntity, target.getEntity());
				em.remove(target.getEntity());
			}
		}	
	}
	
	private Method getAddRemoveFromGetter(Object parentEntity, Method getterMethod, String action) throws Exception {
		String methodName = getterMethod.getName().replaceFirst("get", action);
		if(methodName.endsWith("s")) {
			methodName = methodName.substring(0, methodName.length() - 1);
		}
		Method method = Utils.getMethod(methodName, parentEntity.getClass());
		Type collectionType = EntityInspector.getCollectionType(getterMethod);
		Class<?> collectionClass = Class.forName(collectionType.getTypeName());
		Type adderParmType = method.getParameterTypes()[0];
		Class<?> adderParmClass = Class.forName(adderParmType.getTypeName());
		if(!collectionClass.equals(adderParmClass)) {
			// failed check that adder method must take a class that is equal to the one returned by the getter method
			method = null;
		}
		
		return method;
	}
	
	public void checkMerge(Object o) {
		if((new EntityInspector(o)).isEntity()) {
			if(!em.contains(o)) {
				em.merge(o);
			}
		}
	}
}
