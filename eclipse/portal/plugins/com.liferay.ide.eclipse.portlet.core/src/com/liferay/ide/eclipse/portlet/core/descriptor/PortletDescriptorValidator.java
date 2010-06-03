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
import com.liferay.ide.eclipse.project.core.BaseValidator;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
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
public class PortletDescriptorValidator extends BaseValidator {

	public static final String MARKER_TYPE = "com.liferay.ide.eclipse.portlet.core.portletDescriptorMarker";

	public static final String MESSAGE_PORTLET_CLASS_NOT_FOUND =
		"The portlet class {0} was not found on the Java Build Path";

	public static final String MESSAGE_RESOURCE_BUNDLE_NOT_FOUND =
		"The resource bundle {0} was not found on the Java Build Path";

	public static final String PORTLET_CLASS_ELEMENT = "portlet-class";

	public static final String PREFERENCE_NODE_QUALIFIER = PortletCore.getDefault().getBundle().getSymbolicName();

	public static final String RESOURCE_BUNDLE_ELEMENT = "resource-bundle";

	public static final String VALIDATION_PORTLET_CLASS_NOT_FOUND = "validation-portlet-class-not-found";

	public static final String VALIDATION_RESOURCE_BUNDLE_NOT_FOUND = "validation-resource-bundle-not-found";

	public PortletDescriptorValidator() {
		super();
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		ValidationResult result = new ValidationResult();

		IFile portletXml = (IFile) resource;

		if (portletXml.isAccessible() && ProjectUtil.isPortletProject(resource.getProject())) {
			final IJavaProject javaProject = JavaCore.create(portletXml.getProject());

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
					Map<String, Object>[] problems = detectProblems(javaProject, portletXml, scopes);

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

	protected Map<String, Object> checkClassResource(
		IJavaProject javaProject, Node classResourceSpecifier, IScopeContext[] preferenceScopes, String preferenceKey,
		String errorMessage) {

		String classResource = NodeUtil.getTextContent(classResourceSpecifier);

		if (classResource != null && classResource.length() > 0) {
			try {
				IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);

				IResource classResourceValue = null;
				for (IClasspathEntry entry : classpathEntries) {
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath entryPath = entry.getPath();
						IPath classResourcePath = entryPath.append(classResource);
						classResourceValue =
							javaProject.getJavaModel().getWorkspace().getRoot().findMember(classResourcePath);
						if (classResourceValue != null) {
							break;
						}
					}
				}

				if (classResourceValue == null) {
					String msg = MessageFormat.format(errorMessage, new Object[] {
						classResource
					});

					return createMarkerValues(
						PREFERENCE_NODE_QUALIFIER, preferenceScopes, preferenceKey, (IDOMNode) classResourceSpecifier,
						msg);
				}
			}
			catch (JavaModelException e1) {

			}

		}

		return null;
	}

	protected void checkPortletClassElements(
		IDOMDocument document, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		List<Map<String, Object>> problems) {

		NodeList classes = document.getElementsByTagName(PORTLET_CLASS_ELEMENT);

		for (int i = 0; i < classes.getLength(); i++) {
			Node item = classes.item(i);

			Map<String, Object> problem =
				checkClass(
					javaProject, item, preferenceScopes, VALIDATION_PORTLET_CLASS_NOT_FOUND,
					MESSAGE_PORTLET_CLASS_NOT_FOUND);

			if (problem != null) {
				problems.add(problem);
			}
		}
	}

	protected void checkResourceBundleElements(
		IDOMDocument document, IJavaProject javaProject, IScopeContext[] preferenceScopes,
		List<Map<String, Object>> problems) {

		NodeList resourceBundles = document.getElementsByTagName(RESOURCE_BUNDLE_ELEMENT);

		for (int i = 0; i < resourceBundles.getLength(); i++) {
			Node item = resourceBundles.item(i);

			Map<String, Object> problem =
				checkClassResource(
					javaProject, item, preferenceScopes, VALIDATION_RESOURCE_BUNDLE_NOT_FOUND,
					MESSAGE_RESOURCE_BUNDLE_NOT_FOUND);

			if (problem != null) {
				problems.add(problem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object>[] detectProblems(
		IJavaProject javaProject, IFile portletXml, IScopeContext[] preferenceScopes)
		throws CoreException {

		List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

		IStructuredModel m = null;

		try {
			m = StructuredModelManager.getModelManager().getModelForRead(portletXml);

			if (m != null && m instanceof IDOMModel) {
				IDOMDocument document = ((IDOMModel) m).getDocument();

				checkPortletClassElements(document, javaProject, preferenceScopes, problems);

				checkResourceBundleElements(document, javaProject, preferenceScopes, problems);
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
