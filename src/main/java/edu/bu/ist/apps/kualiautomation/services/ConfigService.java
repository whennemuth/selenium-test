package edu.bu.ist.apps.kualiautomation.services;

import java.io.File;
import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.bu.ist.apps.kualiautomation.Utils;
import edu.bu.ist.apps.kualiautomation.model.Config;
import edu.bu.ist.apps.kualiautomation.model.ConfigDefaults;

public class ConfigService {

	public static final String CFG_NAME = "kualiautomation.cfg";
	
	public Config getConfig() throws Exception {
		return getConfigInstance(getConfigFile());
	}

	private File getConfigFile() throws Exception {
		File rootdir = Utils.getRootDirectory();
		File cfgfile = null;
		if(rootdir != null && rootdir.isDirectory()) {
			File configdir = new File(rootdir, "config");
			if(!configdir.isDirectory()) {
				configdir.mkdir();
			}
			
			cfgfile = new File(configdir, CFG_NAME);
			if(!cfgfile.isFile()) {
				createDefaultConfigFile(cfgfile);
			}
		}
		return cfgfile;
	}
	
	/**
	 * Read json from a file (entire file content) and convert to a Config instance.
	 * 
	 * @param cfgfile
	 * @return
	 * @throws Exception
	 */
	private Config getConfigInstance(File cfgfile) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Config cfg = mapper.readValue(cfgfile, Config.class);
		return cfg;
	}

	/**
	 * Convert a Config instance to json and save that json to a file.
	 * 
	 * @param cfgfile
	 * @throws Exception
	 */
	private void createDefaultConfigFile(File cfgfile) throws Exception {
		Config cfg = new Config();			
		ConfigDefaults.populate(cfg);
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(cfgfile);
			mapper.writerWithDefaultPrettyPrinter().writeValue(out, cfg);
			out.close();
		} 
		finally {
			if(out != null) {
				out.close();
			}
		}		
	}

	public static void main(String[] args) throws Exception {
		ConfigService svc = new ConfigService();
		Config cfg = svc.getConfig();
		System.out.println(cfg);
	}
}
