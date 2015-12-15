package com.ibm.ecm.mm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PropertiesManagerTest {
	private static Path configFilePath;

	@BeforeClass
	public static void createConfigTestStub() throws IOException {
		String query = PropertiesManager.class.getClassLoader().getResource("").getPath();
		File cmConfigSfile = new File(query + "ecmpropertiestest.properties");
		if (cmConfigSfile.exists()) {
			cmConfigSfile.delete();
		}

		configFilePath = Files.createFile(cmConfigSfile.toPath());
		String content = "key=value";
		Files.write(configFilePath, content.getBytes());
	}

	@AfterClass
	public static void cleanupConfigFiles() throws IOException {
		if (configFilePath.toFile().exists()) {
			configFilePath.toFile().delete();
		}
	}

	@Test
	public void testGetProperty() throws IOException {
		PropertiesManager manager = new PropertiesManager(configFilePath.getFileName().toString());
		String value = manager.getProperty("key");
		Assert.assertEquals("value", value);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testConfigFileNotFound() throws IOException {
		PropertiesManager manager = new PropertiesManager("Invalid Path");
	}

}
