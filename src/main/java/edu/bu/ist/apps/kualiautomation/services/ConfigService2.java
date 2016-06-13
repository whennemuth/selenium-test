package edu.bu.ist.apps.kualiautomation.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.ConfigModule;
import edu.bu.ist.apps.kualiautomation.entity.ConfigTab;
import edu.bu.ist.apps.kualiautomation.entity.User;
import edu.bu.ist.apps.kualiautomation.util.BeanPopulator;
import edu.bu.ist.apps.kualiautomation.util.EntityPopulator;
import edu.bu.ist.apps.kualiautomation.util.SimpleBeanPopulator;
import edu.bu.ist.apps.kualiautomation.util.Utils;

public class ConfigService2 {

	public static final String PERSISTENCE_NAME = "kualiautomation-HSQLDB";

	public List<Config> getConfigs() {
		return null;
	}
	
	public Config getConfig(User user) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
        	List<Config> configs = new ArrayList<Config>();
        	if(user == null) {
    			TypedQuery<Config> query = em.createNamedQuery("Config.findAll", Config.class);
    			configs = query.getResultList();
        	}
        	else {
    			TypedQuery<Config> query = em.createNamedQuery("Config.findByUserId", Config.class);
    			query.setParameter("userid", user.getId());		
    			configs = query.getResultList();		
        	}
			if(configs.isEmpty()) {
				return getEmptyConfig(true);
			}
			else {
				return configs.get(0);
			}
		} 
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}
	}

	private Config getEmptyConfig(boolean defaultServers) {
		Config cfg = new Config();
		if(defaultServers) {
			ConfigEnvironment currentEnv = null;
			for(ConfigEnvironment.Defaults d : ConfigEnvironment.Defaults.values()) {
				ConfigEnvironment env = new ConfigEnvironment();
				env.setName(d.name());
				env.setUrl(d.getUrl());
				cfg.addConfigEnvironment(env);
				if(currentEnv == null)
					currentEnv = env;
			}
			cfg.setCurrentEnvironment(currentEnv);
		}
		setDummyModules(cfg);
		return cfg;
	}

	public Config saveConfig(Config cfg) throws Exception {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        EntityTransaction trans = null;
        try {
    		boolean persist = cfg.getUser() == null || cfg.getUser().getId() == null;
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
            Config cfgEntity = null;
            
		    trans = em.getTransaction();
		    trans.begin();
		    
		    if(persist) {
		    	// Must null out the currentEnvironment as it will be persisted twice since it also exists in the environments collection.
		    	if(cfg.getCurrentEnvironment() != null && cfg.getCurrentEnvironment().getId() == null) {
		    		cfg.setCurrentEnvironment(null);
		    	}
			    em.persist(cfg.getUser());
			    fixBidirectionalFields(cfg);
			    em.persist(cfg);
			    if(!cfg.getConfigEnvironments().isEmpty()) {
			    	cfg.setCurrentEnvironment((ConfigEnvironment) cfg.getConfigEnvironments().toArray()[0]);
			    	cfg.getCurrentEnvironment().setParentConfig(cfg);
			    	em.merge(cfg);
			    	System.out.println("NUMBER OF CONFIGS: " + cfg.getUser().getConfigs().size());
			    }			    
		    }
		    else {
		    	cfgEntity = em.find(Config.class, cfg.getId());
		    	EntityPopulator ep = new EntityPopulator(em, true);
		    	SimpleBeanPopulator populator = new SimpleBeanPopulator(ep, true);
		    	populator.populate(cfgEntity, cfg);
		    	em.merge(cfgEntity);
		    }
		    
		    if(trans.isActive()) {
			    System.out.println("Committing...");
			    trans.commit();
		    }
			
		    if(cfgEntity == null)
		    	return cfg;
		    else
		    	return cfgEntity;
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
	 * The config entity may have been constituted by jersey de-serialization of json at web service resource endpoints
	 * and @ManyToOne annotated fields may be null to avoid Jackson parsing recursion issues and need to be reset.
	 * 
	 * @param cfg
	 */
	private void fixBidirectionalFields(Config cfg) {
	    for(ConfigEnvironment env : cfg.getConfigEnvironments()) {
	    	env.setParentConfig(cfg);
	    }
	    
	    for(ConfigModule mdl : cfg.getConfigModules()) {
	    	if(mdl.getConfig() == null)
	    		mdl.setConfig(cfg);
	    	for(ConfigTab tab : mdl.getConfigTabs()) {
	    		if(tab.getConfigModule() == null)
	    			tab.setConfigModule(mdl);
	    	}
	    }		
	}
	
	private void setDummyModules(Config cfg) {
		ConfigTab tab1 = new ConfigTab();
		tab1.setLabel("tab 1");
		
		ConfigTab tab2 = new ConfigTab();
		tab2.setLabel("tab 2");
		
		ConfigTab tab3 = new ConfigTab();
		tab3.setLabel("tab 3");
		
		ConfigTab tab4 = new ConfigTab();
		tab4.setLabel("tab 4");
		
		ConfigTab tab5 = new ConfigTab();
		tab5.setLabel("tab 5");
		
		ConfigTab tab6 = new ConfigTab();
		tab6.setLabel("tab 6");
		
		ConfigTab tab7 = new ConfigTab();
		tab7.setLabel("tab 7");
		
		ConfigTab tab8 = new ConfigTab();
		tab8.setLabel("tab 8");
		
		ConfigTab tab9 = new ConfigTab();
		tab9.setLabel("tab 9");
		tab9.setInclude((byte)0);
		
		ConfigModule m1 = new ConfigModule();
		m1.setLabel("module 1");
		m1.addConfigTab(tab1);
		m1.addConfigTab(tab2);
		m1.addConfigTab(tab3);
		
		ConfigModule m2 = new ConfigModule();
		m2.setLabel("module 2");
		m2.addConfigTab(tab4);
		m2.addConfigTab(tab5);
		m2.addConfigTab(tab6);
		
		ConfigModule m3 = new ConfigModule();
		m3.setLabel("module 3");
		m3.addConfigTab(tab7);
		m3.addConfigTab(tab8);
		m3.addConfigTab(tab9);
		
		cfg.addConfigModule(m1);
		cfg.addConfigModule(m2);
		cfg.addConfigModule(m3);
	}
}
