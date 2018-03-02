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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseInstanceScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseProjectScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class JSPMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		IMarkerResolution[] retval = new IMarkerResolution[0];

		if (hasResolutions(marker)) {
			List<IMarkerResolution> resolutions = new ArrayList<>();
			IProject project = marker.getResource().getProject();

			if (_isResourceBundleQuery(marker)) {
				_collectResourceBundleResolutions(marker, resolutions, project);
			}
			else if (_isPortletActionMethodQuery(marker)) {
				_collectPortletActionMethodResolutions(marker, resolutions, project);
			}

			_collectDecreaseValidationLevelResolutions(resolutions);

			retval = resolutions.toArray(new IMarkerResolution[0]);
		}

		return retval;
	}

	public boolean hasResolutions(IMarker marker) {
		try {
			if (_isJSPMarker(marker) && _isSupportedQuery(marker)) {
				return true;
			}

			String valKey = marker.getAttribute(XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, null);

			if ((valKey != null) && ValidationPreferences.containsKey(valKey)) {
				return true;
			}
		}
		catch (CoreException ce) {
		}

		return false;
	}

	public class TypeInProjectRequestor extends SearchRequestor {

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();

			if (element instanceof IType) {
				IType type = (IType)element;

				if (type.getCompilationUnit() != null) {
					_results.add(type);
				}
			}
		}

		public List<IType> getResults() {
			return _results;
		}

		private List<IType> _results = new ArrayList<>();

	}

	private void _collectDecreaseValidationLevelResolutions(List<IMarkerResolution> resolutions) {
		resolutions.add(new DecreaseProjectScopeXmlValidationLevel());
		resolutions.add(new DecreaseInstanceScopeXmlValidationLevel());
	}

	private void _collectPortletActionMethodResolutions(
		IMarker marker, List<IMarkerResolution> resolutions, IProject project) {

		IJavaProject javaProject = JavaCore.create(project);

		List<IType> mvcPortlets = _findTypes(javaProject, "com.liferay.util.bridges.mvc.MVCPortlet");

		for (IType mvcPortlet : mvcPortlets) {
			resolutions.add(new AddMVCPortletActionMethodMarkerResolution(marker, mvcPortlet));
		}

		List<IType> jsrPortlets = _findTypes(javaProject, "javax.portlet.GenericPortlet");

		for (IType jsrPortlet : jsrPortlets) {
			if (!mvcPortlets.contains(jsrPortlet)) {
				resolutions.add(new AddJSRPortletActionMethodMarkerResolution(marker, jsrPortlet));
			}
		}
	}

	private void _collectResourceBundleResolutions(
		IMarker marker, List<IMarkerResolution> resolutions, IProject project) {

		List<IFile> files = PropertiesUtil.getDefaultLanguagePropertiesFromProject(project);

		if (ListUtil.isEmpty(files)) {
			String[] portletNames = new PortletDescriptorHelper(project).getAllPortletNames();

			if (ListUtil.isNotEmpty(portletNames)) {
				for (String portletName : portletNames) {
					resolutions.add(new AddResourceBundleFileMarkerResolution(marker, portletName));
				}
			}
		}
		else {
			for (IFile file : files) {
				resolutions.add(new AddResourceKeyMarkerResolution(marker, file));
			}
		}
	}

	private List<IType> _findTypes(IJavaProject javaProject, String typeName) {
		List<IType> retval = Collections.emptyList();

		try {
			IType type = javaProject.findType(typeName);

			if (type != null) {
				TypeInProjectRequestor requestor = new TypeInProjectRequestor();

				IJavaSearchScope scope = SearchEngine.createStrictHierarchyScope(javaProject, type, true, false, null);

				SearchPattern search = SearchPattern.createPattern(
					"*", IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, 0);

				new SearchEngine().search(
					search, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope, requestor,
					new NullProgressMonitor());

				retval = requestor.getResults();
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	private boolean _isJSPMarker(IMarker marker) throws CoreException {
		return XMLSearchConstants.LIFERAY_JSP_MARKER_ID.equals(marker.getType());
	}

	private boolean _isPortletActionMethodQuery(IMarker marker) {
		return XMLSearchConstants.PORTLET_ACTION_METHOD_QUERY_ID.equals(
			marker.getAttribute(LiferayBaseValidator.MARKER_QUERY_ID, ""));
	}

	private boolean _isResourceBundleQuery(IMarker marker) {
		return XMLSearchConstants.RESOURCE_BUNDLE_QUERY_ID.equals(
			marker.getAttribute(LiferayBaseValidator.MARKER_QUERY_ID, ""));
	}

	private boolean _isSupportedQuery(IMarker marker) {
		if (_isResourceBundleQuery(marker) || _isPortletActionMethodQuery(marker)) {
			return true;
		}

		return false;
	}

}