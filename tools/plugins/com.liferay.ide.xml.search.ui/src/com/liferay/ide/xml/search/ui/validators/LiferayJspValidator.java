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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.xml.search.ui.PortalLanguagePropertiesCacheUtil;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.properties.IPropertiesQuerySpecification;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToProperty;
import org.eclipse.wst.xml.search.editor.searchers.IXMLSearcher;
import org.eclipse.wst.xml.search.editor.util.PropertiesQuerySpecificationUtil;
import org.eclipse.wst.xml.search.editor.validation.IValidationResult;
import org.eclipse.wst.xml.search.editor.validation.LocalizedMessage;
import org.eclipse.wst.xml.search.editor.validation.XMLReferencesBatchValidator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class LiferayJspValidator extends LiferayBaseValidator {

	public static final String MESSAGE_CLASS_ATTRIBUTE_NOT_WORK = Msgs.classAttributeNotWork;

	protected void addMessage(
		IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
		int severity, String liferayPluginValidationType, String querySpecificationId) {

		String textContent = DOMUtils.getNodeValue(node);
		int startOffset = getStartOffset(node);

		if (textContent != null) {
			textContent = textContent.trim();

			int length = textContent.length() + 2;

			LocalizedMessage message = createMessage(
				startOffset, length, messageText, severity, node.getStructuredDocument());

			if (message != null) {
				message.setAttribute(MARKER_QUERY_ID, querySpecificationId);
				message.setAttribute(XMLSearchConstants.FULL_PATH, FileUtil.getFullPathPortableString(file));
				message.setAttribute(XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, liferayPluginValidationType);
				message.setAttribute(XMLSearchConstants.MARKER_TYPE, XMLSearchConstants.LIFERAY_JSP_MARKER_ID);
				message.setAttribute(XMLSearchConstants.TEXT_CONTENT, textContent);
				message.setTargetObject(file);

				reporter.addMessage(validator, message);
			}
		}
	}

	@Override
	protected String getLiferayPluginValidationType(ValidationPreferences.ValidationType validationType, IFile file) {
		String retval = null;

		if (ValidationPreferences.ValidationType.PROPERTY_NOT_FOUND.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_PROPERTY_NOT_FOUND;
		}
		else if (ValidationPreferences.ValidationType.METHOD_NOT_FOUND.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_METHOD_NOT_FOUND;
		}
		else if (ValidationPreferences.ValidationType.REFERENCE_NOT_FOUND.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_REFERENCE_NOT_FOUND;
		}
		else if (ValidationPreferences.ValidationType.RESOURCE_NOT_FOUND.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_RESOURCE_NOT_FOUND;
		}
		else if (ValidationPreferences.ValidationType.STATIC_VALUE_UNDEFINED.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_STATIC_VALUE_UNDEFINED;
		}
		else if (ValidationPreferences.ValidationType.TYPE_HIERARCHY_INCORRECT.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_TYPE_HIERARCHY_INCORRECT;
		}
		else if (ValidationPreferences.ValidationType.TYPE_NOT_FOUND.equals(validationType)) {
			retval = ValidationPreferences.LIFERAY_JSP_TYPE_NOT_FOUND;
		}

		return retval;
	}

	protected String getMessageText(
		ValidationPreferences.ValidationType validationType, IXMLReferenceTo referenceTo, Node node, IFile file) {

		if (StringUtil.equals("class", node) &&
			validationType.equals(ValidationPreferences.ValidationType.STATIC_VALUE_UNDEFINED)) {

			return NLS.bind(MESSAGE_CLASS_ATTRIBUTE_NOT_WORK, null);
		}

		return super.getMessageText(validationType, referenceTo, node, file);
	}

	@Override
	protected IFile getReferencedFile(IXMLReferenceTo referenceTo, Node node, IFile file) {
		if (referenceTo instanceof IXMLReferenceToProperty) {
			IXMLReferenceToProperty referenceToProperty = (IXMLReferenceToProperty)referenceTo;

			IPropertiesQuerySpecification[] querySpecifications =
				PropertiesQuerySpecificationUtil.getQuerySpecifications(referenceToProperty);

			if ((querySpecifications == null) || (querySpecifications.length < 1)) {
				return null;
			}

			IPropertiesQuerySpecification querySpecification = querySpecifications[0];

			IPropertiesRequestor requestor = querySpecification.getRequestor();

			IResource resource = querySpecification.getResource(node, file);

			return new ReferencedPropertiesVisitor().getReferencedFile(requestor, resource);
		}

		return null;
	}

	@Override
	protected int getServerity(ValidationPreferences.ValidationType validationType, IFile file) {
		int retval = -1;
		String liferayPluginValidationType = getLiferayPluginValidationType(validationType, file);

		if (liferayPluginValidationType != null) {
			IPreferencesService preferencesService = Platform.getPreferencesService();

			retval = preferencesService.getInt(
				PREFERENCE_NODE_QUALIFIER, liferayPluginValidationType, IMessage.NORMAL_SEVERITY,
				getScopeContexts(file.getProject()));
		}
		else {
			retval = super.getServerity(validationType, file);
		}

		return retval;
	}

	@Override
	protected void setMarker(IValidator validator, IFile file) {
		String extension = file.getFileExtension();

		if ((validator instanceof XMLReferencesBatchValidator) && extension.equals("jsp")) {
			XMLReferencesBatchValidator xmlValidator = (XMLReferencesBatchValidator)validator;

			Validator.V2 parent = xmlValidator.getParent();

			parent.setMarkerId(XMLSearchConstants.LIFERAY_JSP_MARKER_ID);
		}
	}

	@Override
	protected void validateReferenceToJava(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		if (node instanceof AttrImpl) {
			AttrImpl attrNode = (AttrImpl)node;

			Node parentNode = attrNode.getOwnerElement();

			if (_isSupportedTag(parentNode.getNodeName())) {
				IDOMAttr nameAttr = DOMUtils.getAttr((IDOMElement)parentNode, "name");

				if (StringUtil.contains(nameAttr.getNodeValue(), _action_request_action_name) ||
					StringUtil.contains(nameAttr.getNodeValue(), _javax_portlet_action)) {

					super.validateReferenceToJava(referenceTo, attrNode, file, validator, reporter, batchMode);
				}
			}
		}
	}

	@Override
	protected void validateReferenceToProperty(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		String languageKey = DOMUtils.getNodeValue(node);

		if (file.exists() && !languageKey.contains(_jsp_rag_start) && !languageKey.contains(_jsp_rag_end)) {
			IXMLSearcher searcher = referenceTo.getSearcher();

			IValidationResult result = searcher.searchForValidation(node, languageKey, -1, -1, file, referenceTo);

			if (result == null) {
				return;
			}

			boolean addMessage = false;

			int nbElements = result.getNbElements();

			if (nbElements > 0) {
				if ((nbElements > 1) && !isMultipleElementsAllowed(node, nbElements)) {
					addMessage = true;
				}
			}
			else {
				addMessage = true;
			}

			if (!addMessage) {
				return;
			}

			Properties properties = PortalLanguagePropertiesCacheUtil.getPortalLanguageProperties(
				LiferayCore.create(ILiferayProject.class, file.getProject()));

			if (properties != null) {
				try {
					String languageValue = (String)properties.get(languageKey);

					if (!languageValue.equals("")) {
						addMessage = false;
					}
				}
				catch (Exception e) {
				}
			}

			if (addMessage) {
				ValidationPreferences.ValidationType validationType = getValidationType(referenceTo, nbElements);

				int severity = getServerity(validationType, file);

				if (severity != ValidationMessage.IGNORE) {
					String liferayPluginValidationType = getLiferayPluginValidationType(validationType, file);
					String querySpecificationId = referenceTo.getQuerySpecificationId();
					String messageText = getMessageText(validationType, referenceTo, node, file);

					addMessage(
						node, file, validator, reporter, batchMode, messageText, severity, liferayPluginValidationType,
						querySpecificationId);
				}
			}
		}
	}

	protected void validateReferenceToStatic(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		if (node instanceof AttrImpl) {
			AttrImpl attrNode = (AttrImpl)node;

			Element ownerElement = attrNode.getOwnerElement();

			if (StringUtil.startsWith(ownerElement.getNodeName(), _aui_prefix)) {
				String nodeValue = node.toString();

				boolean addMessage = false;

				if (nodeValue.equals("class")) {
					addMessage = true;
				}

				if (addMessage) {
					ValidationPreferences.ValidationType validationType = getValidationType(referenceTo, 0);

					int severity = getServerity(validationType, file);

					if (severity != ValidationMessage.IGNORE) {
						String liferayPluginValidationType = getLiferayPluginValidationType(validationType, file);
						String querySpecificationId = referenceTo.getQuerySpecificationId();
						String messageText = getMessageText(validationType, referenceTo, node, file);

						addMessage(
							node, file, validator, reporter, batchMode, messageText, severity,
							liferayPluginValidationType, querySpecificationId);
					}
				}
			}
			else {
				super.validateReferenceToStatic(referenceTo, attrNode, file, validator, reporter, batchMode);
			}
		}
	}

	protected static class Msgs extends NLS {

		public static String classAttributeNotWork;

		static {
			initializeMessages(LiferayJspValidator.class.getName(), Msgs.class);
		}

	}

	private boolean _isSupportedTag(String tagName) {
		for (String supportTag : _supported_tags) {
			if (supportTag.equals(tagName)) {
				return true;
			}
		}

		return false;
	}

	private final String _action_request_action_name = "ActionRequest.ACTION_NAME";
	private final String _aui_prefix = "aui";
	private final String _javax_portlet_action = "javax.portlet.action";
	private final String _jsp_rag_end = "%>";
	private final String _jsp_rag_start = "<%";
	private final String[] _supported_tags = {"liferay-portlet:param", "portlet:param"};

}