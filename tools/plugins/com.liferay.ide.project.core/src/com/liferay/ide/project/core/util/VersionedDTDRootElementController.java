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
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import java.text.MessageFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author Gregory Amerson
 */
public class VersionedDTDRootElementController extends StandardRootElementController {

	public VersionedDTDRootElementController(
		String xmlBindingPath, String publicIdTemplate, String systemIdTemplate, Pattern publicIdPattern,
		Pattern systemIdPattern) {

		_xmlBindingPath = xmlBindingPath;
		_publicIdTemplate = publicIdTemplate;
		_systemIdTemplate = systemIdTemplate;
		_publicIdPattern = publicIdPattern;
		_systemIdPattern = systemIdPattern;
	}

	@Override
	public boolean checkRootElement() {
		boolean checkRoot = super.checkRootElement();

		if (checkRoot) {
			return _checkDocType();
		}

		return false;
	}

	@Override
	public void createRootElement() {
		super.createRootElement();

		if (!_checkDocType()) {
			IProject project = resource().adapt(IProject.class);

			String defaultVersion = LiferayDescriptorHelper.getDescriptorVersion(project);

			DocumentType existingDocType = _getDocument().getDoctype();

			if (existingDocType != null) {
				_getDocument().removeChild(existingDocType);
			}

			String publicId = MessageFormat.format(_publicIdTemplate, defaultVersion);
			String systemId = MessageFormat.format(_systemIdTemplate, defaultVersion.replaceAll("\\.", "_"));

			DOMImplementation domImpl = _getDocument().getImplementation();

			DocumentType newDocType = domImpl.createDocumentType(_xmlBindingPath, publicId, systemId);

			if (newDocType != null) {
				_getDocument().insertBefore(newDocType, _getDocument().getDocumentElement());
			}
		}
	}

	@Override
	protected RootElementInfo getRootElementInfo() {
		if (_rootElementInfo == null) {
			_rootElementInfo = new RootElementInfo(null, null, _xmlBindingPath, null);
		}

		return _rootElementInfo;
	}

	private boolean _checkDocType() {
		try {
			Document document = _getDocument();

			if (document == null) {
				return false;
			}

			DocumentType docType = document.getDoctype();

			if (docType == null) {
				return false;
			}

			Matcher publicIdMatcher = _publicIdPattern.matcher(docType.getPublicId());

			if (!publicIdMatcher.matches()) {
				return false;
			}

			String version = publicIdMatcher.group(1);

			if (version == null) {
				return false;
			}

			Matcher systemIdMatcher = _systemIdPattern.matcher(docType.getSystemId());

			if (!systemIdMatcher.matches()) {
				return false;
			}

			String systemIdVersion = systemIdMatcher.group(1);

			if (systemIdVersion == null) {
				return false;
			}

			systemIdVersion = systemIdVersion.replaceAll(StringPool.UNDERSCORE, StringPool.EMPTY);
			version = version.replaceAll("\\.", StringPool.EMPTY);

			if (systemIdVersion.equals(version)) {
				return true;
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	private Document _getDocument() {
		RootXmlResource rootXmlResource = (RootXmlResource)resource().root();

		return rootXmlResource.getDomDocument();
	}

	private final Pattern _publicIdPattern;
	private final String _publicIdTemplate;
	private RootElementInfo _rootElementInfo;
	private final Pattern _systemIdPattern;
	private final String _systemIdTemplate;
	private final String _xmlBindingPath;

}