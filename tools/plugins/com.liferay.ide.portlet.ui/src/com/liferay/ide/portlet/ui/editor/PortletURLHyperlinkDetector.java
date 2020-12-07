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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortletURLHyperlinkDetector extends AbstractHyperlinkDetector {

	public PortletURLHyperlinkDetector() {
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IHyperlink[] retval = null;

		if (_shouldDetectHyperlinks(textViewer, region)) {
			IDocument document = textViewer.getDocument();

			IDOMNode currentNode = DOMUtils.getNodeByOffset(document, region.getOffset());

			IRegion nodeRegion = new Region(
				currentNode.getStartOffset(), currentNode.getEndOffset() - currentNode.getStartOffset());

			if (_isActionURL(currentNode)) {
				NamedNodeMap attributes = currentNode.getAttributes();

				Node name = attributes.getNamedItem("name");

				if (name != null) {
					IDocumentExtension4 docExtension = (IDocumentExtension4)document;

					long modStamp = docExtension.getModificationStamp();

					IFile file = DOMUtils.getFile(document);

					IMethod[] actionUrlMethods = null;

					if (file.equals(_lastFile) && (modStamp == _lastModStamp) && nodeRegion.equals(_lastNodeRegion)) {
						actionUrlMethods = _lastActionUrlMethods;
					}
					else {
						String nameValue = name.getNodeValue();

						// search for this method in any portlet classes

						actionUrlMethods = _findPortletMethods(document, nameValue);

						_lastModStamp = modStamp;
						_lastFile = file;
						_lastNodeRegion = nodeRegion;
						_lastActionUrlMethods = actionUrlMethods;
					}

					if (ListUtil.isNotEmpty(actionUrlMethods)) {
						List<IHyperlink> links = new ArrayList<>();

						for (IMethod method : actionUrlMethods) {
							if (method.exists()) {
								links.add(new BasicJavaElementHyperlink(nodeRegion, method));
							}
						}

						if (ListUtil.isNotEmpty(links)) {
							if (canShowMultipleHyperlinks) {
								retval = links.toArray(new IHyperlink[0]);
							}
							else {
								retval = new IHyperlink[] {links.get(0)};
							}
						}
					}
				}
			}
		}

		return retval;
	}

	private static boolean _isActionMethod(IMethod method) {
		String[] paramTypes = method.getParameterTypes();

		if ((paramTypes.length == 2) && StringUtil.contains(paramTypes[0].toLowerCase(), "request") &&
			StringUtil.contains(paramTypes[1].toLowerCase(), "response")) {

			return true;
		}

		return false;
	}

	private IMethod[] _findPortletMethods(IDocument document, String nameValue) {
		IMethod[] retval = null;

		IFile file = DOMUtils.getFile(document);

		if ((file != null) && file.exists()) {
			IJavaProject project = JavaCore.create(file.getProject());

			if ((project != null) && project.exists()) {
				try {
					IType portlet = project.findType("javax.portlet.Portlet");

					if (portlet != null) {
						List<IMethod> methods = new ArrayList<>();

						SearchRequestor requestor = new ActionMethodCollector(methods);

						IJavaSearchScope scope = SearchEngine.createStrictHierarchyScope(
							project, portlet, true, false, null);

						SearchPattern search = SearchPattern.createPattern(
							nameValue, IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS,
							SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);

						SearchParticipant[] searchParticipant = {SearchEngine.getDefaultSearchParticipant()};

						new SearchEngine().search(
							search, searchParticipant, scope, requestor, new NullProgressMonitor());

						retval = methods.toArray(new IMethod[0]);
					}
				}
				catch (JavaModelException jme) {
				}
				catch (CoreException ce) {
				}
			}
		}

		return retval;
	}

	private boolean _isActionURL(Node currentNode) {
		if ((currentNode != null) && (currentNode.getNodeName() != null) &&
			(currentNode.getNodeType() == Node.ELEMENT_NODE) &&
			StringUtil.endsWith(currentNode.getNodeName(), "actionURL")) {

			return true;
		}

		return false;
	}

	private boolean _shouldDetectHyperlinks(ITextViewer textViewer, IRegion region) {
		if ((region != null) && (textViewer != null)) {
			return true;
		}

		return false;
	}

	private IMethod[] _lastActionUrlMethods;
	private IFile _lastFile;
	private long _lastModStamp;
	private IRegion _lastNodeRegion;

	private static class ActionMethodCollector extends SearchRequestor {

		public ActionMethodCollector(List<IMethod> results) {
			_results = results;
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();

			if ((element instanceof IMethod) && _isActionMethod((IMethod)element)) {
				this._results.add((IMethod)element);
			}
		}

		private List<IMethod> _results;

	}

}