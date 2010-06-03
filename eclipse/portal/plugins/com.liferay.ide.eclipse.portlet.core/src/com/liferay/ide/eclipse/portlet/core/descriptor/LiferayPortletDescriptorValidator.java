/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.descriptor;

import com.liferay.ide.eclipse.core.util.NodeUtil;
import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;
import com.liferay.ide.eclipse.project.core.BaseValidator;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayPortletDescriptorValidator extends BaseValidator {

	public static final String FOOTER_PORTAL_CSS_ELEMENT = "footer-portal-css";

	public static final String FOOTER_PORTAL_JAVASCRIPT_ELEMENT = "footer-portal-javascript";

	public static final String FOOTER_PORTLET_CSS_ELEMENT = "footer-portlet-css";

	public static final String FOOTER_PORTLET_JAVASCRIPT_ELEMENT = "footer-portlet-javascript";

	public static final String HEADER_PORTAL_CSS_ELEMENT = "header-portal-css";

	public static final String HEADER_PORTAL_JAVASCRIPT_ELEMENT = "header-portal-javascript";

	public static final String HEADER_PORTLET_CSS_ELEMENT = "header-portlet-css";

	public static final String HEADER_PORTLET_JAVASCRIPT_ELEMENT = "header-portlet-javascript";

	public static final String ICON_ELEMENT = "icon";

	public static final String MARKER_TYPE = "com.liferay.ide.eclipse.portlet.core.liferayPortletDescriptorMarker";

	public static final String MESSAGE_FOOTER_PORTAL_CSS_NOT_FOUND =
		"The footer portal css resource {0} was not found in the docroot.";

	public static final String MESSAGE_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND =
		"The footer portal javascript resource {0} was not found in the docroot.";

	public static final String MESSAGE_FOOTER_PORTLET_CSS_NOT_FOUND =
		"The footer portlet css resource {0} was not found in the docroot.";

	public static final String MESSAGE_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND =
		"The footer portlet javascript resource {0} was not found in the docroot.";

	public static final String MESSAGE_HEADER_PORTAL_CSS_NOT_FOUND =
		"The header portal css resource {0} was not found in the docroot.";

	public static final String MESSAGE_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND =
		"The header portal javascript resource {0} was not found in the docroot.";

	public static final String MESSAGE_HEADER_PORTLET_CSS_NOT_FOUND =
		"The header portlet css resource {0} was not found in the docroot.";

	public static final String MESSAGE_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND =
		"The header portlet javascript resource {0} was not found in the docroot.";

	public static final String MESSAGE_ICON_NOT_FOUND = "The icon resource {0} was not found in the docroot.";

	public static final String PREFERENCE_NODE_QUALIFIER = PortletCore.getDefault().getBundle().getSymbolicName();

	public static final String VALIDATION_FOOTER_PORTAL_CSS_NOT_FOUND = "validation-footer-portal-css-not-found";

	public static final String VALIDATION_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND =
		"validation-footer-portal-javascript-not-found";

	public static final String VALIDATION_FOOTER_PORTLET_CSS_NOT_FOUND = "validation-footer-portlet-css-not-found";

	public static final String VALIDATION_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND =
		"validation-footer-portlet-javascript-not-found";

	public static final String VALIDATION_HEADER_PORTAL_CSS_NOT_FOUND = "validation-header-portal-css-not-found";

	public static final String VALIDATION_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND =
		"validation-header-portal-javascript-not-found";

	public static final String VALIDATION_HEADER_PORTLET_CSS_NOT_FOUND = "validation-header-portlet-css-not-found";

	public static final String VALIDATION_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND =
		"validation-header-portlet-javascript-not-found";

	public static final String VALIDATION_ICON_NOT_FOUND = "validation-icon-not-found";

	public LiferayPortletDescriptorValidator() {
		super();
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		ValidationResult result = new ValidationResult();

		IFile liferayPortletXml = (IFile) resource;

		if (liferayPortletXml.isAccessible() && ProjectUtil.isPortletProject(resource.getProject())) {
			final IJavaProject javaProject = JavaCore.create(liferayPortletXml.getProject());

			if (javaProject.exists()) {
				IScopeContext[] scopes = new IScopeContext[] {
					new InstanceScope(), new DefaultScope()
				};

				// ProjectScope projectScope = new
				// ProjectScope(portletXml.getProject());

				// boolean useProjectSettings =
				// projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(
				// JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS,
				// false);
				//				
				// if (useProjectSettings) {
				// scopes = new IScopeContext[] {
				// projectScope, new InstanceScope(), new DefaultScope()
				// };
				// }

				try {
					Map<String, Object>[] problems = detectProblems(javaProject, liferayPortletXml, scopes);

					for (int i = 0; i < problems.length; i++) {
						ValidatorMessage message =
							ValidatorMessage.create(problems[i].get(IMarker.MESSAGE).toString(), resource);
						message.setType(MARKER_TYPE);
						message.setAttributes(problems[i]);
						result.add(message);
					}

					// if (problems.length > 0) {
					// result.setDependsOn(new IResource[] {
					// portletXml
					// });
					// }
				}
				catch (Exception e) {
					PortletCore.logError(e);
				}
			}
		}

		return result;
	}

	protected Map<String, Object> checkClass(
		IJavaProject javaProject, Node classSpecifier, IScopeContext[] preferenceScopes, String preferenceKey,
		String errorMessage) {

		String className = NodeUtil.getTextContent(classSpecifier);

		if (className != null && className.length() > 0) {
			IType type = null;

			try {
				type = javaProject.findType(className);
			}
			catch (JavaModelException e) {
				return null;
			}

			if (type == null || !type.exists()) {
				String msg = MessageFormat.format(errorMessage, new Object[] {
					className
				});

				return createMarkerValues(
					PREFERENCE_NODE_QUALIFIER, preferenceScopes, preferenceKey, (IDOMNode) classSpecifier, msg);

			}
		}

		return null;
	}

	protected void checkDocrootElement(
		IDOMDocument document, String element, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		String validationKey, String messageKey, List<Map<String, Object>> problems) {

		NodeList elements = document.getElementsByTagName(element);

		for (int i = 0; i < elements.getLength(); i++) {
			Node item = elements.item(i);

			Map<String, Object> problem =
				checkDocrootResource(javaProject, item, preferenceScopes, validationKey, messageKey);

			if (problem != null) {
				problems.add(problem);
			}
		}
	}

	protected void checkDocrootElements(
		IDOMDocument document, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		List<Map<String, Object>> problems) {

		checkDocrootElement(
			document, ICON_ELEMENT, javaProject, preferenceScopes, VALIDATION_ICON_NOT_FOUND, MESSAGE_ICON_NOT_FOUND,
			problems);

		checkDocrootElement(
			document, HEADER_PORTAL_CSS_ELEMENT, javaProject, preferenceScopes, VALIDATION_HEADER_PORTAL_CSS_NOT_FOUND,
			MESSAGE_HEADER_PORTAL_CSS_NOT_FOUND, problems);

		checkDocrootElement(
			document, HEADER_PORTLET_CSS_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_HEADER_PORTLET_CSS_NOT_FOUND, MESSAGE_HEADER_PORTLET_CSS_NOT_FOUND, problems);

		checkDocrootElement(
			document, HEADER_PORTAL_JAVASCRIPT_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND, MESSAGE_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND, problems);

		checkDocrootElement(
			document, HEADER_PORTLET_JAVASCRIPT_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND, MESSAGE_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND, problems);

		checkDocrootElement(
			document, FOOTER_PORTAL_CSS_ELEMENT, javaProject, preferenceScopes, VALIDATION_FOOTER_PORTAL_CSS_NOT_FOUND,
			MESSAGE_FOOTER_PORTAL_CSS_NOT_FOUND, problems);

		checkDocrootElement(
			document, FOOTER_PORTLET_CSS_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_FOOTER_PORTLET_CSS_NOT_FOUND, MESSAGE_FOOTER_PORTLET_CSS_NOT_FOUND, problems);

		checkDocrootElement(
			document, FOOTER_PORTAL_JAVASCRIPT_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND, MESSAGE_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND, problems);

		checkDocrootElement(
			document, FOOTER_PORTLET_JAVASCRIPT_ELEMENT, javaProject, preferenceScopes,
			VALIDATION_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND, MESSAGE_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND, problems);
	}

	protected Map<String, Object> checkDocrootResource(
		IJavaProject javaProject, Node docrootResourceSpecifier, IScopeContext[] preferenceScopes,
		String preferenceKey, String errorMessage) {

		String docrootResource = NodeUtil.getTextContent(docrootResourceSpecifier);

		if (docrootResource != null && docrootResource.length() > 0) {
			IFolder docroot = PortletUtil.getDocroot(javaProject.getProject());

			IResource docrootResourceValue = docroot.findMember(new Path(docrootResource));

			if (docrootResourceValue == null) {
				String msg = MessageFormat.format(errorMessage, new Object[] {
					docrootResource
				});

				return createMarkerValues(
					PREFERENCE_NODE_QUALIFIER, preferenceScopes, preferenceKey, (IDOMNode) docrootResourceSpecifier,
					msg);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object>[] detectProblems(
		IJavaProject javaProject, IFile liferayPortletXml, IScopeContext[] preferenceScopes)
		throws CoreException {

		List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

		IStructuredModel m = null;

		try {
			m = StructuredModelManager.getModelManager().getModelForRead(liferayPortletXml);

			if (m != null && m instanceof IDOMModel) {
				IDOMDocument document = ((IDOMModel) m).getDocument();

				checkDocrootElements(document, javaProject, preferenceScopes, problems);
			}
		}
		catch (IOException e) {

		}
		finally {
			if (m != null) {
				m.releaseFromRead();
			}
		}

		Map<String, Object>[] retval = new Map[problems.size()];

		return (Map<String, Object>[]) problems.toArray(retval);
	}

}
