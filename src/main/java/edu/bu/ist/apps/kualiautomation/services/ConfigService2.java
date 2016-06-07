package edu.bu.ist.apps.kualiautomation.services;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.ConfigModule;
import edu.bu.ist.apps.kualiautomation.entity.ConfigTab;
import edu.bu.ist.apps.kualiautomation.entity.User;

public class ConfigService2 {

	public List<Config> getConfigs() {
		return null;
	}
	
	public Config getConfig(User user) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("kualiautomation-embedded");
        EntityManager em = factory.createEntityManager();
        try {
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
	
	private void setDummyModules(Config cfg) {
		ConfigTab tab1 = new ConfigTab();
		tab1.setId(1);
		tab1.setLabel("tab 1");
		
		ConfigTab tab2 = new ConfigTab();
		tab2.setId(2);
		tab2.setLabel("tab 2");
		
		ConfigTab tab3 = new ConfigTab();
		tab3.setId(3);
		tab3.setLabel("tab 3");
		
		ConfigTab tab4 = new ConfigTab();
		tab4.setId(4);
		tab4.setLabel("tab 4");
		
		ConfigTab tab5 = new ConfigTab();
		tab5.setId(5);
		tab5.setLabel("tab 5");
		
		ConfigTab tab6 = new ConfigTab();
		tab6.setId(6);
		tab6.setLabel("tab 6");
		
		ConfigTab tab7 = new ConfigTab();
		tab7.setId(7);
		tab7.setLabel("tab 7");
		
		ConfigTab tab8 = new ConfigTab();
		tab8.setId(8);
		tab8.setLabel("tab 8");
		
		ConfigTab tab9 = new ConfigTab();
		tab9.setId(9);
		tab9.setLabel("tab 9");
		tab9.setInclude((byte)0);
		
		ConfigModule m1 = new ConfigModule();
		m1.setId(1);
		m1.setLabel("module 1");
		m1.addConfigTab(tab1);
		m1.addConfigTab(tab2);
		m1.addConfigTab(tab3);
		
		ConfigModule m2 = new ConfigModule();
		m2.setId(2);
		m2.setLabel("module 2");
		m2.addConfigTab(tab4);
		m2.addConfigTab(tab5);
		m2.addConfigTab(tab6);
		
		ConfigModule m3 = new ConfigModule();
		m3.setId(3);
		m3.setLabel("module 3");
		m3.addConfigTab(tab7);
		m3.addConfigTab(tab8);
		m3.addConfigTab(tab9);
		
		cfg.addConfigModule(m1);
		cfg.addConfigModule(m2);
		cfg.addConfigModule(m3);
	}

	public Config saveConfig(Config cfg) {
		// TODO Auto-generated method stub
		return null;
	}
}
