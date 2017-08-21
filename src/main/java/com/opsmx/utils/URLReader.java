package com.opsmx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class URLReader {
	
	private static String url;
	private static Properties prop = null;
	private static InputStream  input  = null;
	
	public static String getUrlFromConfigFile(String property){
		prop = new Properties();
			input  = URLReader.class.getResourceAsStream("/UrlConfig.properties");
			try {
				prop.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		url = prop.getProperty(property);
		System.out.println("!!!!!!!!!!Config File URL = " + url);
		return url;
	}
}
