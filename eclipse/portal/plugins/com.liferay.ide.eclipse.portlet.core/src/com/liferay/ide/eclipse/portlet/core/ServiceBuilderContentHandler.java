/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Greg Amerson
 */
public class ServiceBuilderContentHandler extends DefaultHandler {

	/**
	 * An exception indicating that the parsing should stop.
	 * 
	 * @since 3.1
	 */
	protected class StopParsingException extends SAXException {

		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs an instance of <code>StopParsingException</code> with a
		 * <code>null</code> detail message.
		 */
		public StopParsingException() {
			super((String) null);
		}
	}

	public static final String PUBLIC_ID_PREFIX = "-//Liferay//DTD Service Builder ";

	public static final String PUBLIC_ID_SUFFIX = "//EN";

	public static final String SERVICE_BUILDER = "service-builder";

	public static final String SYSTEM_ID_PREFIX = "http://www.liferay.com/dtd/liferay-service-builder_";

	public static final String SYSTEM_ID_SUFFIX = ".dtd";

	protected SAXParserFactory fFactory;

	protected int fLevel = -1;

	protected String fTopElementFound;

	protected boolean serviceBuilderDTD = false;

	protected boolean topLevelServiceElement = false;

	public boolean hasServiceBuilderDTD() {
		return this.serviceBuilderDTD;
	}

	public boolean hasTopLevelServiceElement() {
		return this.topLevelServiceElement;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
		throws IOException, SAXException {

		if (systemId != null && systemId.startsWith(SYSTEM_ID_PREFIX) && systemId.endsWith(SYSTEM_ID_SUFFIX) &&
			publicId != null && publicId.startsWith(PUBLIC_ID_PREFIX) && publicId.endsWith(PUBLIC_ID_SUFFIX)) {

			this.serviceBuilderDTD = true;
		}

		return new InputSource(new StringReader(""));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException {

		fLevel++;

		if (fTopElementFound == null) {
			fTopElementFound = localName;

			this.topLevelServiceElement = hasRootProjectElement();

			throw new StopParsingException();
		}

	}

	protected final SAXParser createParser(SAXParserFactory parserFactory)
		throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {

		// Initialize the parser.
		final SAXParser parser = parserFactory.newSAXParser();

		// final XMLReader reader = parser.getXMLReader();
		// disable DTD validation (bug 63625)
		// try {
		// be sure validation is "off" or the feature to ignore DTD's will not
		// apply
		//            reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		//            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		// } catch (SAXNotRecognizedException e) {
		// not a big deal if the parser does not recognize the features
		// } catch (SAXNotSupportedException e) {
		// not a big deal if the parser does not support the features
		// }

		return parser;
	}

	protected SAXParserFactory getFactory() {
		synchronized (this) {
			if (fFactory != null) {
				return fFactory;
			}

			fFactory = SAXParserFactory.newInstance();

			fFactory.setNamespaceAware(true);
		}

		return fFactory;
	}

	protected boolean hasRootProjectElement() {
		return SERVICE_BUILDER.equals(fTopElementFound);
	}

	protected boolean parseContents(InputSource contents)
		throws IOException, ParserConfigurationException, SAXException {

		// Parse the file into we have what we need (or an error occurs).
		try {
			fFactory = getFactory();

			if (fFactory == null) {
				return false;
			}

			final SAXParser parser = createParser(fFactory);
			parser.parse(contents, this);
		}
		catch (StopParsingException e) {
			// Abort the parsing normally. Fall through...
		}

		return true;
	}

}
