package edu.bu.ist.apps.kualiautomation.services.config;

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
import edu.bu.ist.apps.kualiautomation.entity.util.Entity;
import edu.bu.ist.apps.kualiautomation.entity.util.EntityPopulator;

public class ConfigService {

	public static final String PERSISTENCE_NAME = "kualiautomation-HSQLDB";

	public List<Config> getConfigs() {
		return null;
	}
	
	/**
	 * Find a single configuration based on user id. If the user is null, then it is assumed that
	 * the application is running on a desktop and there is only one user, so the first configuration
	 * found is returned, else the first configuration bearing bearing the provided user id is returned.
	 * 
	 * NOTE: It is assumed that any user has only one configuration.
	 *       While this one-to-one feature is functionally enforced, a user can be store with many configurations.
	 * 
	 * @param user
	 * @return
	 */
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
	
	public Config getConfigById(Integer configId) {
        EntityManagerFactory factory = null;
        EntityManager em = null;
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
            em = factory.createEntityManager();
        	Config cfg = em.find(Config.class, configId);
        	return cfg;
		} 
	    finally {
	    	if(em != null && em.isOpen())
	    		em.close();
	    	if(factory != null && factory.isOpen())
	    		factory.close();
		}
	}

	private Config getEmptyConfig(boolean defaultEnvironments) {
		Config cfg = new Config();
		ConfigDefaults.populate(cfg);
		return cfg;
	}
	
	public ConfigModule getEmptyConfigModule(Integer configId) {
		ConfigModule mdl = new ConfigModule();
		Config cfg = new Config();
		if(configId > 0)
			cfg.setId(configId);
		cfg.setTransitory(true);
		mdl.setConfig(cfg);
		mdl.setLabel("");
		ConfigTab tab = new ConfigTab();
		tab.setLabel("");
		mdl.addConfigTab(tab);
		return mdl;
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
			    em.persist(cfg.getUser());
			    fixBidirectionalFields(cfg);
			    em.persist(cfg);
			    em.merge(cfg); // Causes child entities to be persisted as CascadeType does not include persist.
		    }
		    else {
		    	cfgEntity = em.find(Config.class, cfg.getId());
		    	Entity entity = new Entity(em, true);
		    	EntityPopulator populator = new EntityPopulator(entity, true);
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
}
