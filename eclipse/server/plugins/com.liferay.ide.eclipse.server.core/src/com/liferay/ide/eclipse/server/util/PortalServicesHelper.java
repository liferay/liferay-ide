package com.liferay.ide.eclipse.server.util;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.wsdl.Definition;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;

public class PortalServicesHelper {

	protected URL webServicesListURL;

	protected Map<String, String> wsdlNameURLMap = null;

	public PortalServicesHelper(URL webServicesListURL) {
		this.webServicesListURL = webServicesListURL;
	}

	public String[] getWebServiceNames() {
		if (wsdlNameURLMap == null) {
			initMap();
		}
		
		return wsdlNameURLMap.keySet().toArray(new String[0]);
	}
	
	protected void initMap() {
		try {
			wsdlNameURLMap = new HashMap<String, String>();
			String webServicesString = CoreUtil.readStreamToString(webServicesListURL.openStream());
			List<String> wsdlUrls = pullLinks(webServicesString);
			for (String url : wsdlUrls) {
				String name = pullServiceName(url);
				if (!CoreUtil.isNullOrEmpty(name)) {
					wsdlNameURLMap.put(name, url);
				}
			}
		} catch (IOException e1) {
			PortalServerCorePlugin.logError("Unable to initial web services list.");
		}
	}
	
	private List<String> pullLinks(String text) {  
		List<String> links = new ArrayList<String>();  
		  
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";  
		Pattern p = Pattern.compile(regex);  
		Matcher m = p.matcher(text);  
		while(m.find()) {  
		String urlStr = m.group();  
		if (urlStr.startsWith("(") && urlStr.endsWith(")"))  
		{  
		urlStr = urlStr.substring(1, urlStr.length() - 1);  
		}  
		links.add(urlStr);  
		}  
		return links;  
		}  
	
	private String pullServiceName(String wsdlUrl) {
		String regex = "axis/(\\w+)\\?wsdl$";  
		Pattern p = Pattern.compile(regex);  
		Matcher m = p.matcher(wsdlUrl); 
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}

	public String getWebServiceWSDLURLByName(String serviceName) {
		if (wsdlNameURLMap == null) {
			initMap();
		}
		
		return wsdlNameURLMap.get(serviceName);
	}

}
