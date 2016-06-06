package edu.bu.ist.apps.kualiautomation.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.User;

public class ConfigService2 {

	public List<Config> getConfigs() {
		return null;
	}
	
	public Config getConfig(User user) {
		if(user == null) {
			// The assumption here is that the app is being used as a desktop app, in which case 
			// there will be only one user, and there is no need to know the userid
			List<Config> configs = getConfigs();
			if(configs.isEmpty()) {
				return getEmptyConfig(true);
			}
			return  configs.get(0);
		}
		else {
	        EntityManagerFactory factory = Persistence.createEntityManagerFactory("kualiautomation-embedded");
	        EntityManager em = factory.createEntityManager();
	        try {
				TypedQuery<Config> query = em.createNamedQuery("Config.findByUserId", Config.class);
				query.setParameter("userid", user.getId());		
				List<Config> list = query.getResultList();		
				return list.get(0);
			} 
		    finally {
		    	if(em != null && em.isOpen())
		    		em.close();
		    	if(factory != null && factory.isOpen())
		    		factory.close();
			}
		}
	}

	private Config getEmptyConfig(boolean defaultServers) {
		Config cfg = new Config();
		if(defaultServers) {
			for(ConfigEnvironment.Defaults d : ConfigEnvironment.Defaults.values()) {
				ConfigEnvironment env = new ConfigEnvironment();
				env.setName(d.name());
				env.setUrl(d.getUrl());
				cfg.addConfigEnvironment(env);
			}
		}
		return cfg;
	}

	public Config saveConfig(Config cfg) {
		// TODO Auto-generated method stub
		return null;
	}
}
