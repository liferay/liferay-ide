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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

/**
 * @author Drew Brokke
 */
public class SecureXMLFactoryUtil {

	public static DocumentBuilderFactory newDocumentBuilderFactory() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		try {
			documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		}
		catch (Exception exception) {
			LiferayCore.logError("Unable to configure secure DocumentBuilderFactory", exception);
		}

		return documentBuilderFactory;
	}

	public static SAXParserFactory newSAXParserFactory() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		try {
			saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		}
		catch (Exception exception) {
			LiferayCore.logError("Unable to configure secure SAXParserFactory", exception);
		}

		return saxParserFactory;
	}

	public static TransformerFactory newTransformerFactory() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		try {
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		}
		catch (Exception exception) {
			LiferayCore.logError("Unable to configure secure TransformerFactory", exception);
		}

		return transformerFactory;
	}

	private SecureXMLFactoryUtil() {
	}

}