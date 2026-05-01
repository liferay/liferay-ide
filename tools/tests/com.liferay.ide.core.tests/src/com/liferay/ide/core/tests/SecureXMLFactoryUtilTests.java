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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.SecureXMLFactoryUtil;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Anthony Chu
 */
public class SecureXMLFactoryUtilTests extends BaseTests {

	@Test
	public void parseDoctypeXml() throws Exception {
		String xml =
			"<?xml version=\"1.0\"?>\n" +
				"<!DOCTYPE struts-config PUBLIC " +
					"\"-//Apache Software Foundation//DTD Struts Configuration 1.2//EN\" " +
					"\"http://struts.apache.org/dtds/struts-config_1_2.dtd\">\n" +
				"<struts-config>" +
					"<action-mappings>" +
						"<action path=\"/foo\"/>" +
					"</action-mappings>" +
				"</struts-config>";

		DocumentBuilderFactory documentBuilderFactory = SecureXMLFactoryUtil.newDocumentBuilderFactory();

		documentBuilderFactory.setValidating(false);

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));

		NodeList actions = document.getElementsByTagName("action");

		Assert.assertEquals(1, actions.getLength());
		Assert.assertEquals("/foo", actions.item(0).getAttributes().getNamedItem("path").getNodeValue());
	}

}