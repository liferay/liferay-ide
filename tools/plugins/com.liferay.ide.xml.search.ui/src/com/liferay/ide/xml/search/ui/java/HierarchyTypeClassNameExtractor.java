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

package com.liferay.ide.xml.search.ui.java;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.wst.xml.search.core.xpath.NamespaceInfos;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.classnameprovider.AbstractClassNameExtractor;

import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public class HierarchyTypeClassNameExtractor extends AbstractClassNameExtractor {

	public HierarchyTypeClassNameExtractor(String typeName) {
		_typeName = typeName;
	}

	@Override
	protected String[] doExtractClassNames(
			Node node, IFile file, String pathForClass, String findByAttrName, boolean findByParentNode,
			String xpathFactoryProviderId, NamespaceInfos namespaceInfo)
		throws XPathExpressionException {

		String[] retval = null;

		IJavaProject project = JavaCore.create(file.getProject());

		try {
			IType type = project.findType(_typeName);

			if (type != null) {
				TypeNameRequestor requestor = new TypeNameRequestor();

				IJavaSearchScope scope = SearchEngine.createStrictHierarchyScope(project, type, true, false, null);

				SearchPattern search = SearchPattern.createPattern(
					"*", IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS, 0);

				new SearchEngine().search(
					search, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope, requestor,
					new NullProgressMonitor());

				List<String> results = requestor.getResults();

				retval = results.toArray(new String[0]);
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	private final String _typeName;

	private static class TypeNameRequestor extends SearchRequestor {

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();

			if (element instanceof IType) {
				IType type = (IType)element;

				this._results.add(type.getFullyQualifiedName());
			}
		}

		public List<String> getResults() {
			return _results;
		}

		private List<String> _results = new ArrayList<>();

	}

}