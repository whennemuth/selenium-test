package edu.bu.ist.apps.kualiautomation.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * This class is provided an JPA entity to persist.
 * The entity will may have one or more @OneToMany annotated collections containing entities that are themselves not persisted.
 * Any attempt to persist the parent entity will result in the following exception:
 *     "javax.persistence.PersistenceException: org.hibernate.PropertyValueException: 
 *         not-null property references a null or transient value : [type of @ManyToOne annotated variable]"
 * This is due to the fact that the corresponding @ManyToOne annotated field in the collection entities will 
 * be null but cannot be when persisting the parent entity because this field is a reference back to that parent entity
 * which itself is not persisted, and you get a cat chasing its tail issue.
 * 
 * The solution here is to:
 *    1) empty out the @OneToMany annotated collections into a cache
 *    2) persist the parent entity
 *    3) Add the cached entities back to the parent entity
 *    4) Persist each formerly cached entity.
 *    5) Recursively execute steps 1-4 for each entity in step 4
 *    
 * @author wrh
 *
 */
public class EntityPersister {
	
	private String persistenceName;
    private EntityManagerFactory factory;
    private EntityManager em;
    private EntityTransaction trans;
    private boolean error;
	
	public EntityPersister(String persistenceName) {
		this.persistenceName = persistenceName;
	}

	public <T> T persist(T entity, boolean commit) throws Exception {
        try {
    		if(factory == null || factory.isOpen() == false)
    			factory = Persistence.createEntityManagerFactory(persistenceName);
    		if(em == null || em.isOpen() == false)
    			em = factory.createEntityManager();
    		if(trans == null || trans.isActive() == false) {
    		    trans = em.getTransaction();
    		    trans.begin();    			
    		}

		    persistAll(entity, em);
			return entity;
		} 
        catch(Exception e) {
        	error = true;
        	e.printStackTrace(System.out);
        	throw e;
        }
	    finally {
	    	if(commit || error) {
	    		if(trans.isActive())
	    			trans.rollback();
		    	if(em != null && em.isOpen())
		    		em.close();
		    	if(factory != null && factory.isOpen())
		    		factory.close();
	    	}
		}			
		
	}
	
	private <T> T persistAll(T entity, EntityManager em) throws Exception {
		List<Method> accessors = (new EntityInspector(entity)).getEntitiesAccessors();
		
		// cache all child entity collections (exist in parent entity annotated with @OneToMany)
		List<List<?>> childCollections = new ArrayList<List<?>>();
		for(Method accessor : accessors) {
			List<?> entities = (List<?>) accessor.invoke(entity);
			if(entities != null && entities.isEmpty() == false) {
				childCollections.add(entities);
			}
		}
		
		// Empty out the child entity collections as the parent entity cannot be persisted while they remain themselves unpersisted.
		for(Method accessor : accessors) {
			Method mutator = Utils.getMutator(accessor, entity.getClass());
			Class<?> listType = accessor.getReturnType();
			List<?> emptyList = (List<?>) listType.newInstance();
			mutator.invoke(entity, emptyList);
		}
		
		// Persist the parent entity
		em.persist(entity);
		
		// Add back every "Many" entity to "One" parent entity for the @OneToMany annotated collection
		for (ListIterator<List<?>> iterator = childCollections.listIterator(); iterator.hasNext();) {
			List<?> childCollection = (List<?>) iterator.next();
			Method accessor = accessors.get(iterator.previousIndex()+1);
			for(Object childEntity : childCollection) {
				restoreChildEntity(entity, childEntity, accessor);
				// recurse
				persistAll(childEntity, em);
			}
		}
		
		return entity;
	}
	
	/**
	 * Find the "add" entity method of a parent entity using the "get" entity accessor as a naming guide.
	 * Then invoke the "add" entity method providing the supplied child entity.
	 * 
	 * @param parentEntity
	 * @param childEntity
	 * @param accessor
	 * @throws Exception
	 */
	private <T> void restoreChildEntity(Object parentEntity, T childEntity, Method accessor) throws Exception {
		String adderName = accessor.getName().replaceFirst("get", "add");
		Class<List<T>> addClass = (Class<List<T>>) accessor.getReturnType();
		Method adder = Utils.getMethod(adderName, childEntity.getClass());
		adder.invoke(parentEntity, childEntity);
	}
}
