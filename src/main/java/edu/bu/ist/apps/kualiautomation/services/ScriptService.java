package edu.bu.ist.apps.kualiautomation.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
	
	public Cycle saveCycle(Cycle cycle) {
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
		    	return cycle;
		    else
		    	return cycleEntity;
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
	
	public Cycle getEmptyCycle() {
		Cycle cycle = new Cycle();
		Suite suite = new Suite();
		User user = new User();
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
				System.out.println("suite name: " + cycle.getSuites().get(0).getName());
				System.out.println("user name: " + cycle.getUser().getFirstName());
				System.out.println("module name: " + cycle.getSuites().get(0).getModules().get(0).getName());
				System.out.println("tab name: " + cycle.getSuites().get(0).getModules().get(0).getTabs().get(0).getName());
				System.out.println("label name/value: " + 
						cycle.getSuites().get(0).getModules().get(0).getTabs().get(0).getLabelAndValues().get(0).getLabel() + "/" +
						cycle.getSuites().get(0).getModules().get(0).getTabs().get(0).getLabelAndValues().get(0).getValue());
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
