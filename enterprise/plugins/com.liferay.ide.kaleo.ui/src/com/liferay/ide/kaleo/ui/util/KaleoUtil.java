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

package com.liferay.ide.kaleo.ui.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayServer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationResultSummary;
import org.eclipse.wst.validation.internal.ValidationRunner;

import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class KaleoUtil {

	public static String checkWorkflowDefinitionForErrors(IFile workspaceFile) {
		String retval = null;

		// first perform manual validation to check for errors

		try {
			StringBuilder errorMsgs = new StringBuilder();

			ValOperation result = ValidationRunner.validate(workspaceFile, ValType.Manual, null, true);

			ValidationResultSummary validationResult = result.getResult();

			if (validationResult.getSeverityError() == 1) {
				ValidationResults results = result.getResults();

				for (ValidatorMessage message : results.getMessages()) {
					if (message.getAttribute(IMarker.SEVERITY, -1) == IMarker.SEVERITY_ERROR) {
						errorMsgs.append(message.getAttribute(IMarker.MESSAGE)).append('\n');
					}
				}
			}

			retval = errorMsgs.toString();
		}
		catch (Exception e) {
		}

		return retval;
	}

	public static String createJSONTitleMap(String title) throws JSONException {
		return createJSONTitleMap(title, Locale.getDefault().toString());
	}

	public static String createJSONTitleMap(String title, String portalLocale) throws JSONException {
		JSONObject jsonTitleMap = new JSONObject();

		try {
			ErrorHandler errorHandle = new ErrorHandler() {

				public void error(SAXParseException exception) throws SAXException {
				}

				public void fatalError(SAXParseException exception) throws SAXException {
				}

				public void warning(SAXParseException exception) throws SAXException {
				}

			};

			try (InputStream inputStream = new ByteArrayInputStream(title.getBytes())) {
				Document doc = FileUtil.readXML(inputStream, null, errorHandle);

				String defaultLocale = doc.getDocumentElement().getAttribute("default-locale");

				NodeList titles = doc.getElementsByTagName("Title");

				for (int i = 0; i < titles.getLength(); i++) {
					Node titleNode = titles.item(i);

					String titleValue = titleNode.getTextContent();

					NamedNodeMap nameNodeMap = titleNode.getAttributes();

					Node node = nameNodeMap.getNamedItem("language-id");

					String languageId = node.getNodeValue();

					if (languageId.equals(defaultLocale)) {
						jsonTitleMap.put(languageId, titleValue);

						break;
					}
				}
			}
		}
		catch (Exception e) {
			jsonTitleMap.put(portalLocale, title);
		}

		return jsonTitleMap.toString();
	}

	public static ILiferayServer getLiferayServer(IServer server, IProgressMonitor monitor) {
		ILiferayServer retval = null;

		if (server != null) {
			try {
				retval = (ILiferayServer)server.loadAdapter(ILiferayServer.class, monitor);
			}
			catch (Exception e) {
			}
		}

		return retval;
	}

}