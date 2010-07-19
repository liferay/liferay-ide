package com.liferay.ide.eclipse.server.util;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PortalServicesHelper {

	protected class PortalServicesHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {

			if (localName != null && localName.toLowerCase().equals("a")) {
				System.out.println(MessageFormat.format("{0} {1} {2} {3}", new Object[] {
					uri, localName, qName, attributes
				}));
				for (int i = 0; i < attributes.getLength(); i++) {
					System.out.println(attributes.getValue(i));
				}
			}

		}

	}

	protected static SAXParserFactory factory = SAXParserFactory.newInstance();

	protected URL webServicesListURL;

	protected Map<String, Definition> wsdlNameDefinitionMap = new HashMap<String, Definition>();

	public PortalServicesHelper(URL webServicesListURL) {
		this.webServicesListURL = webServicesListURL;
	}

	public String[] getWebServiceNames() {
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(webServicesListURL.openStream(), new PortalServicesHandler());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
