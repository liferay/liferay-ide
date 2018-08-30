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

package com.liferay.ide.project.core.util;

import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Gregory Amerson
 */
public class VersionedSchemaRootElementController extends StandardRootElementController {

	public VersionedSchemaRootElementController(
		String xmlBindingPath, Pattern namespacePattern, Pattern schemaPattern, String namespaceTemplate,
		String schemaTemplate, String defaultVersion) {

		_xmlBindingPath = xmlBindingPath;
		_namespacePattern = namespacePattern;
		_schemaPattern = schemaPattern;
		_namespaceTemplate = namespaceTemplate;
		_schemaTemplate = schemaTemplate;
		_defaultVersion = defaultVersion;
	}

	@Override
	protected RootElementInfo getRootElementInfo() {
		if (_rootElementInfo == null) {
			Map<String, String> schemas = new HashMap<>();
			Document doc = _getDocument();

			String namespace = null;
			String uri = null;
			String location = null;

			if (doc != null) {
				Element documentElement = doc.getDocumentElement();

				if (documentElement != null) {
					uri = documentElement.getNamespaceURI();

					Matcher m = _namespacePattern.matcher(uri);

					if ((uri != null) && m.matches()) {
						namespace = uri;

						String schema = documentElement.getAttribute("xsi:schemaLocation");

						Matcher matcher = _schemaPattern.matcher(schema);

						if ((schema != null) && matcher.matches()) {
							location = matcher.group(1);
						}
					}
				}
				else {
					String version = _defaultVersion;

					// no documentElement lets get default values

					IProject project = resource().adapt(IProject.class);

					if (project != null) {
						version = LiferayDescriptorHelper.getDescriptorVersion(project, _defaultVersion);
					}

					namespace = MessageFormat.format(_namespaceTemplate, version);

					uri = namespace;

					location = MessageFormat.format(_schemaTemplate, version.replaceAll("\\.", "_"));
				}
			}

			schemas.put(uri, location);

			_rootElementInfo = new RootElementInfo(namespace, "", _xmlBindingPath, schemas);
		}

		return _rootElementInfo;
	}

	private Document _getDocument() {
		Resource resource = resource().root();

		RootXmlResource rootXmlResource = resource.adapt(RootXmlResource.class);

		return rootXmlResource.getDomDocument();
	}

	private final String _defaultVersion;
	private final Pattern _namespacePattern;
	private final String _namespaceTemplate;
	private RootElementInfo _rootElementInfo;
	private final Pattern _schemaPattern;
	private final String _schemaTemplate;
	private final String _xmlBindingPath;

}