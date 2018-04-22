/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.core;

import com.liferay.ide.core.util.StringPool;

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
 * @author Gregory Amerson
 */
public abstract class AbstractDefaultHandler extends DefaultHandler {

	public AbstractDefaultHandler(
		String publicIdPrefix, String publicIdSuffix, String systemIdPrefix, String systemIdSuffix,
		String rootElement) {

		this.publicIdPrefix = publicIdPrefix;
		this.publicIdSuffix = publicIdSuffix;
		this.systemIdPrefix = systemIdPrefix;
		this.systemIdSuffix = systemIdSuffix;
		this.rootElement = rootElement;
	}

	public boolean hasDTD() {
		return dtd;
	}

	public boolean hasTopLevelElement() {
		return topLevelElement;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
		if ((systemId != null) && systemId.startsWith(systemIdPrefix) && systemId.endsWith(systemIdSuffix) &&
			(publicId != null) && publicId.startsWith(publicIdPrefix) && publicId.endsWith(publicIdSuffix)) {

			dtd = true;
		}

		return new InputSource(new StringReader(StringPool.EMPTY));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		fLevel++;

		if (fTopElementFound != null) {
			return;
		}

		fTopElementFound = localName;

		topLevelElement = hasRootProjectElement();

		throw new StopParsingException();
	}

	protected final SAXParser createParser(SAXParserFactory parserFactory)
		throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {

		SAXParser parser = parserFactory.newSAXParser();

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
		if ((rootElement != null) && rootElement.equals(fTopElementFound)) {
			return true;
		}

		return false;
	}

	protected boolean parseContents(InputSource contents)
		throws IOException, ParserConfigurationException, SAXException {

		// Parse the file into we have what we need (or an error occurs).

		try {
			fFactory = getFactory();

			if (fFactory == null) {
				return false;
			}

			SAXParser parser = createParser(fFactory);

			parser.parse(contents, this);
		}
		catch (StopParsingException spe) {

			// Abort the parsing normally. Fall through...

		}

		return true;
	}

	protected boolean dtd = false;
	protected SAXParserFactory fFactory;
	protected int fLevel = -1;
	protected String fTopElementFound;
	protected String publicIdPrefix;
	protected String publicIdSuffix;
	protected String rootElement;
	protected String systemIdPrefix;
	protected String systemIdSuffix;
	protected boolean topLevelElement = false;

	protected class StopParsingException extends SAXException {

		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		public static final long serialVersionUID = 1L;

		public StopParsingException() {
			super((String)null);
		}

	}

}