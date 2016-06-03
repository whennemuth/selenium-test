package edu.bu.ist.apps.kualiautomation.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transaction;

import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.entity.Module;
import edu.bu.ist.apps.kualiautomation.entity.Suite;
import edu.bu.ist.apps.kualiautomation.entity.Tab;
import edu.bu.ist.apps.kualiautomation.entity.User;

public class ScriptService {

	public Cycle addCycle(Cycle cycle) {
		// TODO: Have the suite populated with an id as if persisted with JPA and receiving an auto-incremented value from the db
		return cycle;
	}
	
	public Cycle saveCycle(Cycle cycle) {
		return cycle;
	}
	
	public Cycle getEmptyCycle() {
		Cycle cycle = new Cycle();
		Suite suite = new Suite();
		User user = new User();
		Module module = new Module();
		Tab tab = new Tab();
		LabelAndValue lv = new LabelAndValue();
		
		suite.setCycle(cycle);
		suite.setUser(user);
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
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("kualiautomation-embedded");
        EntityManager em = factory.createEntityManager();
        
        try {
			Cycle cycle = em.find(Cycle.class, 1);
			if(cycle == null) {
				System.out.println("Cycle not found, will create...");
			    EntityTransaction trans = em.getTransaction();
			    trans.begin();
			    cycle = new Cycle();
			    cycle.setName("TEST CYCLE");
			    cycle.setSequence(1);
			    em.persist(cycle);
			    
			    User user = new User();
			    user.setFirstName("Warren");
			    user.setLastName("Hennemuth");
			    em.persist(user);
			    
			    Suite suite = new Suite();
			    suite.setName("my suite");
			    suite.setSequence(1);
			    suite.setUser(user);
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
				System.out.println("user name: " + cycle.getSuites().get(0).getUser().getFirstName());
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
