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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.project.core.util.VersionedSchemaRootElementController;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionRootElementController extends VersionedSchemaRootElementController {

	public WorkflowDefinitionRootElementController() {
		super(_xmlBindingPath, _namespacePattern, _schemaPattern, _namespaceTemplate, _schemaTemplate, _defaultVersion);
	}

	@Override
	protected void createRootElement(Document document, RootElementInfo rinfo) {
		super.createRootElement(document, rinfo);

		/*
		 * remove the http://www.w3.org/XML/1998/namespace that breaks
		 * validation in the schemaLocation
		 */
		Element documentElement = document.getDocumentElement();

		String oldValue = documentElement.getAttributeNS(XSI_NAMESPACE, "schemaLocation");

		if (!CoreUtil.isNullOrEmpty(oldValue) && oldValue.startsWith(_XML_1998_NAMESPACE)) {
			String clearOldValue = oldValue.substring(_XML_1998_NAMESPACE.length());

			String newValue = clearOldValue.trim();

			documentElement.setAttributeNS(XSI_NAMESPACE, XSI_SCHEMA_LOCATION_ATTR, newValue);
		}
	}

	private static final String _XML_1998_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

	private static final String _defaultVersion = KaleoCore.DEFAULT_KALEO_VERSION;
	private static final Pattern _namespacePattern = Pattern.compile("urn:liferay.com:liferay-workflow_(.*)$");
	private static final String _namespaceTemplate = "urn:liferay.com:liferay-workflow_{0}";
	private static final Pattern _schemaPattern = Pattern.compile(
		"urn:liferay.com:liferay-workflow_.*(http://www.liferay.com/dtd/liferay-workflow-definition_(.*).xsd)$");
	private static final String _schemaTemplate = "http://www.liferay.com/dtd/liferay-workflow-definition_{0}.xsd";
	private static final String _xmlBindingPath = WorkflowDefinition.class.getAnnotation(XmlBinding.class).path();

}