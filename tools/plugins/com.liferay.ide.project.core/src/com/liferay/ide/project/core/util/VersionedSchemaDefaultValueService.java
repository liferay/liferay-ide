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

import com.liferay.ide.core.util.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public class VersionedSchemaDefaultValueService extends DefaultValueService {

	public VersionedSchemaDefaultValueService(Pattern namespacePattern, String defaultVersion) {
		_namespacePattern = namespacePattern;
		_defaultVersion = defaultVersion;
	}

	@Override
	protected String compute() {
		String version = _defaultVersion;

		Element element = context(Element.class);

		Resource elementResource = element.resource();

		RootXmlResource resource = elementResource.adapt(RootXmlResource.class);

		if (resource != null) {
			Document document = resource.getDomDocument();

			if (document != null) {
				Node node = document.getDocumentElement();

				if (node != null) {
					String namespace = node.getNamespaceURI();

					Matcher matcher = _namespacePattern.matcher(namespace);

					if (matcher.matches()) {
						version = matcher.group(1);
					}
				}
			}
		}

		return version.replaceAll(StringPool.UNDERSCORE, ".");
	}

	private final String _defaultVersion;
	private final Pattern _namespacePattern;

}