package edu.bu.ist.apps.kualiautomation.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.entity.Module;
import edu.bu.ist.apps.kualiautomation.entity.Suite;
import edu.bu.ist.apps.kualiautomation.entity.Tab;
import edu.bu.ist.apps.kualiautomation.entity.User;
import edu.bu.ist.apps.kualiautomation.entity.util.Entity;
import edu.bu.ist.apps.kualiautomation.entity.util.EntityPopulator;
import edu.bu.ist.apps.kualiautomation.util.Utils;

public class ScriptService {

	public static final String PERSISTENCE_NAME = "kualiautomation-HSQLDB";

	public Cycle getCycle(Integer cycleId) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
        	Cycle cycle = em.find(Cycle.class, cycleId);
        	return cycle;
		} 
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}
	}

	public List<Cycle> getCycles(Integer userId) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
        	List<Cycle> cycles = new ArrayList<Cycle>();
			TypedQuery<Cycle> query = em.createNamedQuery("Cycle.findByUserId", Cycle.class);
			query.setParameter("userid", userId);		
			cycles = query.getResultList();	
    		return cycles;
		} 
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}
	}
	
	public List<Cycle> saveCycle(Cycle cycle) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        EntityTransaction trans = null;
        try {
        	boolean persist = Utils.isEmpty(cycle.getId()) || cycle.getId() == 0;
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
            Cycle cycleEntity = null;
            
		    trans = em.getTransaction();
		    trans.begin();
		    
		    if(persist) {
		    	fixBidirectionalFields(cycle);
			    em.persist(cycle);
			    em.merge(cycle); // Causes child entities to be persisted as CascadeType does not include persist.
		    }
		    else {
		    	cycleEntity = em.find(Cycle.class, cycle.getId());
		    	Entity ep = new Entity(em, true);
		    	EntityPopulator populator = new EntityPopulator(ep, true);
		    	populator.populate(cycleEntity, cycle);
		    	em.merge(cycleEntity);
		    }
		    
		    if(trans.isActive()) {
			    System.out.println("Committing...");
			    trans.commit();
		    }
			
		    if(cycleEntity == null)
		    	return getCycles(cycle.getUser().getId());
		    else
		    	return getCycles(cycleEntity.getUser().getId());
		} 
        catch(Exception e) {
        	e.printStackTrace(System.out);
        	if(trans.isActive()) {
        		System.out.println("Config Service rolling back!!!");
        		trans.rollback();
        	}
        	throw e;
        }
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}			
	}
	
	/**
	 * The cycle entity may have been constituted by jersey de-serialization of json at web service resource endpoints
	 * and @ManyToOne annotated fields may be null to avoid Jackson parsing recursion issues and need to be reset.
	 * 
	 * @param cfg
	 */
	private void fixBidirectionalFields(Cycle cycle) {
		for(Suite suite : cycle.getSuites()) {
			suite.setCycle(cycle);
			for(Module module : suite.getModules()) {
				module.setSuite(suite);
				for(Tab tab : module.getTabs()) {
					tab.setModule(module);
					for(LabelAndValue lv : tab.getLabelAndValues()) {
						lv.setTab(tab);
					}
				}
			}
		}
	}

	public Cycle getEmptyCycle(Integer userId) {
		Cycle cycle = new Cycle();
		Suite suite = new Suite();
		User user = new User();
		user.setTransitory(true);
		if(userId != null) 
			user.setId(userId);
		Module module = new Module();
		Tab tab = new Tab();
		LabelAndValue lv = new LabelAndValue();
		
		suite.setCycle(cycle);
		cycle.setUser(user);
		cycle.addSuite(suite);
		module.setSuite(suite);
		suite.addModule(module);
		tab.setModule(module);
		module.addTab(tab);
		lv.setTab(tab);
		tab.addLabelAndValue(lv);

		return cycle;
	}
	
	private User getUser(Integer userId) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
        	User user = em.find(User.class, userId);
        	return user;
		} 
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}
	}

	public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
        EntityManager em = factory.createEntityManager();
        
        try {
			Cycle cycle = em.find(Cycle.class, 1);
			if(cycle == null) {
				System.out.println("Cycle not found, will create...");
			    EntityTransaction trans = em.getTransaction();
			    trans.begin();
			    
			    User user = new User();
			    user.setFirstName("Warren");
			    user.setLastName("Hennemuth");
			    em.persist(user);
			    
			    cycle = new Cycle();
			    cycle.setName("TEST CYCLE");
			    cycle.setSequence(1);
			    cycle.setUser(user);
			    em.persist(cycle);
			    
			    Suite suite = new Suite();
			    suite.setName("my suite");
			    suite.setSequence(1);
			    cycle.addSuite(suite);
			    em.persist(suite);
			    
			    Module module = new Module();
			    module.setName("my module");
			    module.setSequence(1);
			    suite.addModule(module);
			    em.persist(module);
			    
			    Tab tab = new Tab();
			    tab.setName("my tab");
			    tab.setSequence(1);
			    module.addTab(tab);
			    em.persist(tab);
			    
			    LabelAndValue lv = new LabelAndValue();
			    lv.setLabel("my label");
			    lv.setValue("my value");
			    lv.setSequence(1);
			    tab.addLabelAndValue(lv);
			    em.persist(lv);
			    
			    trans.commit();
			}
			else {
				System.out.println("Cycle found");
				System.out.println("cycle name: " + cycle.getName());
				System.out.println("suite name: " + ((Cycle) cycle.getSuites().toArray()[0]).getName());
				System.out.println("user name: " + cycle.getUser().getFirstName());
				System.out.println("module name: " + ((Cycle) ((Suite) cycle.getSuites().toArray()[0]).getModules().toArray()[0]).getName());
				System.out.println("tab name: " + ((Cycle) ((Module) ((Suite) cycle.getSuites().toArray()[0]).getModules().toArray()[0]).getTabs().toArray()[0]).getName());
				System.out.println("label name/value: " + 
						((LabelAndValue) ((Tab) ((Module) ((Suite) cycle.getSuites().toArray()[0]).getModules().toArray()[0]).getTabs().toArray()[0]).getLabelAndValues().toArray()[0]).getLabel() + "/" +
						((LabelAndValue) ((Tab) ((Module) ((Suite) cycle.getSuites().toArray()[0]).getModules().toArray()[0]).getTabs().toArray()[0]).getLabelAndValues().toArray()[0]).getValue());
			}
			System.out.println(cycle.getId());
		} 
        finally {
        	if(em != null && em.isOpen())
        		em.close();
        	if(factory != null && factory.isOpen())
        		factory.close();
		}
        
	}
}
