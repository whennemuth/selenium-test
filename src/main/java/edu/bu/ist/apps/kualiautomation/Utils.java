package edu.bu.ist.apps.kualiautomation;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;

public class Utils {

	public static String stackTraceToString(Throwable e) {
		if(e == null)
			return null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		String trace = sw.getBuffer().toString();
		return trace;
	}
	
	/**
	 * Get the directory containing the jar file whose code is currently running.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static File getRootDirectory() throws Exception {
		String path = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		File f = new File(decodedPath);
		if(f.isFile() && f.getName().endsWith(".jar")) {
			return f.getParentFile();
		}
		return f;
    }
}
