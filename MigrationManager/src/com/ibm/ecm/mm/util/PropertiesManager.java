package com.ibm.ecm.mm.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * The <code>PropertiesManager</code> is a {@link ManagedBean} utility class
 * that loads a standard properties file into a {@link Properties} object. The
 * default properties file name is specified in {@link #DEFAULT_CONFIG_FILE} and
 * used by the default constructor.<br>
 * The {@link #PropertiesManager(String)} constructor serves as a second option
 * in case the default name is used by another application, using this
 * constructor the config filename can be configured during the compile time of
 * the application<br>
 * This class will first search the parent class loader for the properties file;
 * if the parent is null the path of the class loader built-in to the virtual
 * machine is searched.
 * 
 * @author mreda
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class PropertiesManager {
	private Properties prop = new Properties();
	/**
	 * Default properties file name
	 */
	public static final String DEFAULT_CONFIG_FILE = "ecmconfig.properties";

	public PropertiesManager() throws IOException {
		loadPropertiesFile(DEFAULT_CONFIG_FILE);
	}

	/**
	 * This parameterized constructor serves as a second option in case the
	 * default name is used by another application, using this constructor the
	 * config filename can be configured during the compile time of the
	 * application
	 * 
	 * @param propFileName
	 *            The name of the properties file to be loaded
	 * @throws IOException
	 */
	public PropertiesManager(String propFileName) throws IOException {
		loadPropertiesFile(propFileName);
	}

	private void loadPropertiesFile(String propFileName) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		inputStream.close();
		System.out.println("Properties were loaded, " + prop.size() + " property found");
	}

	/**
	 * Searches for the property with the specified key in the loaded properties
	 * file and returns its value. the method returns null if the property is
	 * not found.
	 * 
	 * @param propertyName
	 *            The name of the property exactly as mentioned in the
	 *            properties file
	 * @return String that represents the value of the propertyName passed, null
	 *         if the key was not found
	 */
	public String getProperty(String propertyName) {
		return prop.getProperty(propertyName);
	}
}