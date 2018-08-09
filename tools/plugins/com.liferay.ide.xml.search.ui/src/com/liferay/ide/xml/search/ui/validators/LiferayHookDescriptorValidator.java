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

package com.liferay.ide.xml.search.ui.validators;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ValidationPreferences.ValidationType;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.util.JdtUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LiferayHookDescriptorValidator extends LiferayBaseValidator {

	public static final String MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES = Msgs.propertiesNotEndWithProperties;

	public static final String MESSAGE_SERVICE_IMPL_TYPE_INCORRECT = Msgs.serviceImplTypeIncorrect;

	public static final String MESSAGE_SERVICE_TYPE_INVALID = Msgs.serviceTypeInvalid;

	public static final String MESSAGE_SERVICE_TYPE_NOT_INTERFACE = Msgs.serviceTypeNotInterface;

	@Override
	protected void validateReferenceToJava(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		if (node.getNodeType() == Node.TEXT_NODE) {
			Node parentNode = node.getParentNode();

			String parentNodeName = parentNode.getNodeName();

			if ("service-type".equals(parentNodeName)) {
				ValidationInfo valInfo = _validateServiceType(node, file);

				if (valInfo != null) {
					addMessage(
						node, file, validator, reporter, batchMode, valInfo.getValidationMessge(),
						getServerity(valInfo.getValidationType(), file),
						getLiferayPluginValidationType(valInfo.getValidationType(), file));
				}

				return;
			}
			else if ("service-impl".equals(parentNodeName)) {
				ValidationInfo valInfo = _validateServiceImpl(node, file);

				if (valInfo != null) {
					addMessage(
						node, file, validator, reporter, batchMode, valInfo.getValidationMessge(),
						getServerity(valInfo.getValidationType(), file),
						getLiferayPluginValidationType(valInfo.getValidationType(), file));
				}

				return;
			}
		}

		super.validateReferenceToJava(referenceTo, node, file, validator, reporter, batchMode);
	}

	@Override
	protected boolean validateSyntax(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		// validate syntax of value of elements <portal-properties> and
		// <language-properties>

		int severity = getServerity(ValidationType.SYNTAX_INVALID, file);

		if (severity != ValidationMessage.IGNORE) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				String validationMsg = null;

				String nodeValue = DOMUtils.getNodeValue(node);

				if (CoreUtil.isNotNullOrEmpty(nodeValue)) {
					Node parentNode = node.getParentNode();

					String parentNodeName = parentNode.getNodeName();

					if ("portal-properties".equals(parentNodeName) || "language-properties".equals(parentNodeName)) {
						if (!nodeValue.endsWith(".properties")) {
							validationMsg = NLS.bind(MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES, nodeValue);
						}
					}

					if (validationMsg != null) {
						String liferayPluginValidationType = getLiferayPluginValidationType(
							ValidationType.SYNTAX_INVALID, file);

						addMessage(
							node, file, validator, reporter, batchMode, validationMsg, severity,
							liferayPluginValidationType);

						return false;
					}
				}
			}
		}

		return true;
	}

	private ValidationInfo _validateServiceImpl(IDOMNode node, IFile file) {
		String serviceImplContent = DOMUtils.getNodeValue(node);

		IType type = JdtUtils.getJavaType(file.getProject(), serviceImplContent);

		String msg = null;

		// validate type existence

		if (type == null) {
			msg = getMessageText(ValidationType.TYPE_NOT_FOUND, node);

			return new ValidationInfo(msg, ValidationType.TYPE_NOT_FOUND);
		}

		Node parentNode = node.getParentNode();

		parentNode = parentNode.getParentNode();

		NodeList siblingNodes = parentNode.getChildNodes();

		IDOMNode serviceTypeNode = null;

		for (int i = 0; i < siblingNodes.getLength(); i++) {
			Node siblingNode = siblingNodes.item(i);

			if ("service-type".equals(siblingNode.getNodeName())) {
				serviceTypeNode = (IDOMNode)siblingNode;

				break;
			}
		}

		try {
			Node firstChild = serviceTypeNode.getFirstChild();

			if ((serviceTypeNode != null) && (_validateServiceType((IDOMNode)firstChild, file) == null)) {

				// validate type hierarchy

				String serviceTypeContent = StringUtil.trim(firstChild.getNodeValue());

				String superTypeName = serviceTypeContent + "Wrapper";

				IType superType = JdtUtils.getJavaType(file.getProject(), superTypeName);

				boolean typeCorrect = false;

				if ((superType != null) &&
					JdtUtils.hierarchyContainsComponent(type, superType.getFullyQualifiedName())) {

					typeCorrect = true;
				}

				if (!typeCorrect) {
					msg = NLS.bind(MESSAGE_SERVICE_IMPL_TYPE_INCORRECT, serviceImplContent, superTypeName);

					return new ValidationInfo(msg, ValidationType.TYPE_HIERARCHY_INCORRECT);
				}
			}
		}
		catch (Exception e) {
			LiferayXMLSearchUI.logError(e);
		}

		return null;
	}

	private ValidationInfo _validateServiceType(IDOMNode node, IFile file) {
		try {
			String serviceTypeContent = DOMUtils.getNodeValue(node);

			IType type = JdtUtils.getJavaType(file.getProject(), serviceTypeContent);

			String msg = null;

			// validate type existence

			if (type == null) {
				msg = getMessageText(ValidationType.TYPE_NOT_FOUND, node);

				return new ValidationInfo(msg, ValidationType.TYPE_NOT_FOUND);
			}

			// validate if it is an interface

			if (!type.isInterface()) {
				msg = NLS.bind(MESSAGE_SERVICE_TYPE_NOT_INTERFACE, serviceTypeContent);

				return new ValidationInfo(msg, ValidationType.TYPE_HIERARCHY_INCORRECT);
			}

			// validate type hierarchy

			if (!serviceTypeContent.matches("com.liferay.*Service")) {
				msg = MESSAGE_SERVICE_TYPE_INVALID;

				return new ValidationInfo(msg, ValidationType.TYPE_HIERARCHY_INCORRECT);
			}
		}
		catch (Exception e) {
			LiferayXMLSearchUI.logError(e);
		}

		return null;
	}

	private static class Msgs extends NLS {

		public static String propertiesNotEndWithProperties;
		public static String serviceImplTypeIncorrect;
		public static String serviceTypeInvalid;
		public static String serviceTypeNotInterface;

		static {
			initializeMessages(LiferayHookDescriptorValidator.class.getName(), Msgs.class);

			initializeMessages(LiferayHookDescriptorValidator.class.getName(), Msgs.class);
		}

	}

	private class ValidationInfo {

		public ValidationInfo(String msg, ValidationType type) {
			_validationMessage = msg;
			_validationType = type;
		}

		public String getValidationMessge() {
			return _validationMessage;
		}

		public ValidationType getValidationType() {
			return _validationType;
		}

		private String _validationMessage;
		private ValidationType _validationType;

	}

}