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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.project.core.ValidationPreferences.ValidationType;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;
import org.eclipse.wst.xml.search.core.queryspecifications.IXMLQuerySpecification;
import org.eclipse.wst.xml.search.core.queryspecifications.XMLQuerySpecificationManager;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestor;
import org.eclipse.wst.xml.search.core.util.DOMUtils;
import org.eclipse.wst.xml.search.editor.references.IXMLReference;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToJava;
import org.eclipse.wst.xml.search.editor.references.validators.IXMLReferenceValidator;
import org.eclipse.wst.xml.search.editor.references.validators.IXMLReferenceValidator2;
import org.eclipse.wst.xml.search.editor.searchers.IXMLSearcher;
import org.eclipse.wst.xml.search.editor.validation.IValidationResult;
import org.eclipse.wst.xml.search.editor.validation.LocalizedMessage;
import org.eclipse.wst.xml.search.editor.validation.ValidatorUtils;

import org.w3c.dom.Node;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class LiferayBaseValidator implements IXMLReferenceValidator, IXMLReferenceValidator2 {

	public static final String MARKER_QUERY_ID = "querySpecificationId";

	public static final String MESSAGE_METHOD_NOT_FOUND = Msgs.methodNotFound;

	public static final String MESSAGE_PROPERTY_NOT_FOUND = Msgs.propertyNotFound;

	public static final String MESSAGE_REFERENCE_NOT_FOUND = Msgs.referenceNotFound;

	public static final String MESSAGE_RESOURCE_NOT_FOUND = Msgs.resourceNotFound;

	public static final String MESSAGE_STATIC_VALUE_UNDEFINED = Msgs.staticValueUndefined;

	public static final String MESSAGE_SYNTAX_INVALID = Msgs.syntaxInvalid;

	public static final String MESSAGE_TYPE_HIERARCHY_INCORRECT = Msgs.typeHierarchyIncorrect;

	public static final String MESSAGE_TYPE_NOT_FOUND = Msgs.typeNotFound;

	@Override
	public boolean isValidTarget(IProject project) {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		IPreferenceStore preferenceStore = plugin.getPreferenceStore();

		String s = preferenceStore.getString(LiferayXMLSearchUI.PREF_KEY_IGNORE_PROJECTS_LIST);

		String[] ignoreList = s.split(",");

		for (String ignore : ignoreList) {
			ignore = ignore.trim();

			if (ignore.equals(project.getName())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void validate(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		if (reference != null) {
			setMarker(validator, file);
			doValidate(reference, node, file, validator, reporter, batchMode);
		}

		// clean old marker types added in 2.2.0 but removed in 2.2.2

		for (String type : _oldMarkerTypes) {
			try {
				IMarker[] markers = file.findMarkers(
					LiferayXMLSearchUI.PLUGIN_ID + "." + type, false, IResource.DEPTH_ONE);

				for (IMarker marker : markers) {
					try {
						marker.delete();
					}
					catch (CoreException ce) {

						// best effort

					}
				}
			}
			catch (CoreException ce) {

				// best effort

			}
		}
	}

	public class ReferencedPropertiesVisitor implements IResourceProxyVisitor {

		public IFile getReferencedFile(IPropertiesRequestor requestor, IResource rootResource) {
			_propertiesRequestor = requestor;
			_rootResource = rootResource;

			try {
				rootResource.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayXMLSearchUI.logError(ce);
			}

			return _retval;
		}

		public boolean visit(IResourceProxy proxy) {
			try {
				if (proxy.getType() == IResource.FILE) {
					IFile file = (IFile)proxy.requestResource();

					if (_propertiesRequestor.accept(file, _rootResource)) {
						_retval = file;

						return false;
					}
				}
			}
			catch (Exception e) {
				return true;
			}

			return true;
		}

		private IPropertiesRequestor _propertiesRequestor;
		private IFile _retval = null;
		private IResource _rootResource;

	}

	protected void addMessage(
		IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
		int severity, String liferayPluginValidationType) {

		addMessage(
			node, file, validator, reporter, batchMode, messageText, severity, liferayPluginValidationType, null);
	}

	protected void addMessage(
		IDOMNode node, IFile file, IValidator validator, IReporter reporter, boolean batchMode, String messageText,
		int severity, String liferayPluginValidationType, String querySpecificationId) {

		int startOffset = getStartOffset(node);

		int length = node.getEndOffset() - startOffset;

		LocalizedMessage message = createMessage(
			startOffset, length, messageText, severity, node.getStructuredDocument());

		if (message != null) {
			if (batchMode) {
				message.setTargetObject(file);
				message.setAttribute(MARKER_QUERY_ID, querySpecificationId);
				message.setAttribute(XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, liferayPluginValidationType);

				reporter.addMessage(validator, message);
			}
		}
	}

	protected LocalizedMessage createMessage(
		int start, int length, String messageText, int severity, IStructuredDocument structuredDocument) {

		return ValidatorUtils.createMessage(start, length, messageText, severity, structuredDocument);
	}

	protected void doValidate(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		if (reference.isExpression()) {
			return; // reference expression is not used in Liferay-IDE
		}

		if (!validateSyntax(reference, node, file, validator, reporter, batchMode)) {
			return; // if the syntax is incorrect, stop validating
		}

		List<IXMLReferenceTo> refTos = reference.getTo();

		// refTos.size() == 0 means the reference is only used for syntax
		// validation, which is already done in last
		// step, and multiple reference-tos are not used in Liferay-IDE

		if ((refTos == null) || (refTos.size() != 1)) {
			return;
		}

		IXMLReferenceTo referenceTo = refTos.get(0);

		switch (referenceTo.getType()) {
			case XML:
				validateReferenceToXML(referenceTo, node, file, validator, reporter, batchMode);

				break;
			case JAVA:
				validateReferenceToJava(referenceTo, node, file, validator, reporter, batchMode);

				break;
			case JAVA_METHOD:
				validateReferenceToJavaMethod(referenceTo, node, file, validator, reporter, batchMode);

				break;
			case RESOURCE:
				validateReferenceToResource(referenceTo, node, file, validator, reporter, batchMode);

				break;
			case PROPERTY:
				validateReferenceToProperty(referenceTo, node, file, validator, reporter, batchMode);

				break;
			case STATIC:
				validateReferenceToStatic(referenceTo, node, file, validator, reporter, batchMode);

				break;
			default:
				return;
		}
	}

	protected String getLiferayPluginValidationType(ValidationType validationType, IFile file) {
		return ValidationPreferences.getValidationPreferenceKey(file.getName(), validationType);
	}

	protected String getMessageText(ValidationType validationType, IXMLReferenceTo referenceTo, Node node, IFile file) {
		String nodeValue = DOMUtils.getNodeValue(node);

		String textContent = nodeValue == null ? "" : nodeValue;

		switch (validationType) {
			case SYNTAX_INVALID:
				return NLS.bind(MESSAGE_SYNTAX_INVALID, textContent);
			case TYPE_NOT_FOUND:
				return NLS.bind(MESSAGE_TYPE_NOT_FOUND, textContent);
			case METHOD_NOT_FOUND:
				return NLS.bind(MESSAGE_METHOD_NOT_FOUND, textContent);
			case TYPE_HIERARCHY_INCORRECT:
				if ((referenceTo != null) && (referenceTo.getType() == IXMLReferenceTo.ToType.JAVA) && (file != null)) {
					IType[] superTypes = ((IXMLReferenceToJava)referenceTo).getExtends(node, file);

					if (ListUtil.isNotEmpty(superTypes)) {
						StringBuilder sb = new StringBuilder();

						for (IType type : superTypes) {
							sb.append(type.getFullyQualifiedName());
							sb.append(", ");
						}

						String s = sb.toString();

						String superTypeNames = s.replaceAll(", $", "");

						return NLS.bind(MESSAGE_TYPE_HIERARCHY_INCORRECT, textContent, superTypeNames);
					}
				}

			case RESOURCE_NOT_FOUND:
				return NLS.bind(MESSAGE_RESOURCE_NOT_FOUND, textContent);
			case REFERENCE_NOT_FOUND:
				IFile referencedFile = getReferencedFile(referenceTo, node, file);

				return NLS.bind(
					MESSAGE_REFERENCE_NOT_FOUND, textContent, referencedFile != null ? referencedFile.getName() : "");
			case PROPERTY_NOT_FOUND:
				IFile languagePropertiesFile = getReferencedFile(referenceTo, node, file);

				return NLS.bind(
					MESSAGE_PROPERTY_NOT_FOUND, textContent,
					languagePropertiesFile != null ? languagePropertiesFile.getName() : "any resource files");
			case STATIC_VALUE_UNDEFINED:
				return NLS.bind(MESSAGE_STATIC_VALUE_UNDEFINED, textContent);
		}

		return null;
	}

	protected String getMessageText(ValidationType validationType, Node node) {
		return getMessageText(validationType, null, node, null);
	}

	/**
	 * get the exactly file which contains the element referenced by another xml
	 * element
	 */
	protected IFile getReferencedFile(IXMLReferenceTo referenceTo, Node node, IFile file) {
		XMLQuerySpecificationManager querySpecificationManager = XMLQuerySpecificationManager.getDefault();

		IXMLQuerySpecification querySpecification = querySpecificationManager.getQuerySpecification(
			referenceTo.getQuerySpecificationId());

		if (querySpecification.isMultiResource()) {
			return null;
		}

		IResource resource = querySpecification.getResource(node, file);

		IXMLSearchRequestor requestor = querySpecification.getRequestor();

		return new ReferencedFileVisitor().getReferencedFile(requestor, resource);
	}

	protected IScopeContext[] getScopeContexts(IProject project) {
		ProjectScope projectScope = new ProjectScope(project);

		IEclipsePreferences eclipsePreferences = projectScope.getNode(PREFERENCE_NODE_QUALIFIER);

		if (eclipsePreferences.getBoolean(ProjectCore.USE_PROJECT_SETTINGS, false)) {
			return new IScopeContext[] {projectScope, InstanceScope.INSTANCE, DefaultScope.INSTANCE};
		}
		else {
			return new IScopeContext[] {InstanceScope.INSTANCE, DefaultScope.INSTANCE};
		}
	}

	protected int getServerity(ValidationType validationType, IFile file) {
		String liferayPluginValidationType = getLiferayPluginValidationType(validationType, file);

		// get severity from users' settings

		IPreferencesService preferencesService = Platform.getPreferencesService();

		return preferencesService.getInt(
			PREFERENCE_NODE_QUALIFIER, liferayPluginValidationType, IMessage.NORMAL_SEVERITY,
			getScopeContexts(file.getProject()));
	}

	protected int getStartOffset(IDOMNode node) {
		int nodeType = node.getNodeType();

		switch (nodeType) {
			case Node.ATTRIBUTE_NODE:
				return ((IDOMAttr)node).getValueRegionStartOffset();
		}

		return node.getStartOffset();
	}

	protected ValidationType getValidationType(IXMLReferenceTo referenceTo, int nbElements) {
		switch (referenceTo.getType()) {
			case XML:
				return ValidationType.REFERENCE_NOT_FOUND;
			case JAVA:
				if (nbElements == -1) {
					return ValidationType.TYPE_HIERARCHY_INCORRECT;
				}

				return ValidationType.TYPE_NOT_FOUND;
			case JAVA_METHOD:
				return ValidationType.METHOD_NOT_FOUND;
			case RESOURCE:
				return ValidationType.RESOURCE_NOT_FOUND;
			case PROPERTY:
				return ValidationType.PROPERTY_NOT_FOUND;
			case STATIC:
				return ValidationType.STATIC_VALUE_UNDEFINED;
			default:
				return null;
		}
	}

	protected boolean isMultipleElementsAllowed(IDOMNode node, int nbElements) {
		return true;
	}

	/**
	 * Subclasses override the method to set their own markers
	 */
	protected void setMarker(IValidator validator, IFile file) {
	}

	/**
	 * default implementation of all kinds of validation
	 */
	protected void validateReferenceToAllType(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		String nodeValue = DOMUtils.getNodeValue(node);

		IXMLSearcher searcher = referenceTo.getSearcher();

		IValidationResult result = searcher.searchForValidation(node, nodeValue, -1, -1, file, referenceTo);

		if (result != null) {
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

			if (addMessage) {
				ValidationType validationType = getValidationType(referenceTo, nbElements);

				int severity = getServerity(validationType, file);
				String liferayPluginValidationType = getLiferayPluginValidationType(validationType, file);

				if (severity != ValidationMessage.IGNORE) {
					String messageText = getMessageText(validationType, referenceTo, node, file);

					addMessage(
						node, file, validator, reporter, batchMode, messageText, severity, liferayPluginValidationType,
						referenceTo.getQuerySpecificationId());
				}
			}
		}
	}

	protected void validateReferenceToJava(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected void validateReferenceToJavaMethod(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected void validateReferenceToProperty(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected void validateReferenceToResource(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected void validateReferenceToStatic(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected void validateReferenceToXML(
		IXMLReferenceTo referenceTo, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		validateReferenceToAllType(referenceTo, node, file, validator, reporter, batchMode);
	}

	protected boolean validateSyntax(
		IXMLReference reference, IDOMNode node, IFile file, IValidator validator, IReporter reporter,
		boolean batchMode) {

		return true;
	}

	protected static final String PREFERENCE_NODE_QUALIFIER = ProjectCore.PLUGIN_ID;

	protected static class Msgs extends NLS {

		public static String methodNotFound;
		public static String propertyNotFound;
		public static String referenceNotFound;
		public static String resourceNotFound;
		public static String staticValueUndefined;
		public static String syntaxInvalid;
		public static String typeHierarchyIncorrect;
		public static String typeNotFound;

		static {
			initializeMessages(LiferayBaseValidator.class.getName(), Msgs.class);
		}

	}

	private static String[] _oldMarkerTypes = {
		"liferayPortletDescriptorMarker", "liferayLayoutTplDescriptorMarker", "liferayDisplayDescriptorMarker",
		"liferayHookDescriptorMarker", "portletDescriptorMarker"
	};

	private class ReferencedFileVisitor implements IResourceProxyVisitor {

		public IFile getReferencedFile(IXMLSearchRequestor requestor, IResource rootResource) {
			_searchRequestor = requestor;
			_rootResource = rootResource;

			try {
				rootResource.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayXMLSearchUI.logError(ce);
			}

			return _retval;
		}

		public boolean visit(IResourceProxy proxy) {
			try {
				if (proxy.getType() == IResource.FILE) {
					IFile file = (IFile)proxy.requestResource();

					if (_searchRequestor.accept(file, _rootResource)) {
						IStructuredModel model = null;

						try {
							IModelManager modelManager = StructuredModelManager.getModelManager();

							model = modelManager.getModelForRead(file);

							if (_searchRequestor.accept(model)) {
								_retval = file;

								return false;
							}
						}
						finally {
							if (model != null) {
								model.releaseFromRead();
							}
						}
					}
				}
			}
			catch (Exception e) {
				return true;
			}

			return true;
		}

		private IFile _retval = null;
		private IResource _rootResource;
		private IXMLSearchRequestor _searchRequestor;

	}

}