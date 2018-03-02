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

package com.liferay.ide.service.ui.editor;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.service.core.util.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ServiceMethodHyperlinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IHyperlink[] retval = null;

		ITextEditor textEditor = (ITextEditor)getAdapter(ITextEditor.class);

		if (textEditor == null) {
			return retval;
		}

		ITypeRoot input = EditorUtility.getEditorInputJavaElement(textEditor, false);
		IAction openAction = textEditor.getAction("OpenEditor");

		if (_shouldDetectHyperlinks(textEditor, input, openAction, region)) {
			IDocumentProvider documentProvider = textEditor.getDocumentProvider();
			IEditorInput editorInput = textEditor.getEditorInput();

			IDocument document = documentProvider.getDocument(editorInput);

			int offset = region.getOffset();

			IRegion wordRegion = JavaWordFinder.findWord(document, offset);

			if (_isRegionValid(document, wordRegion)) {
				IJavaElement[] elements = new IJavaElement[0];
				long modStamp = documentProvider.getModificationStamp(editorInput);

				if (input.equals(_lastInput) && (modStamp == _lastModStamp) && wordRegion.equals(_lastWordRegion)) {
					elements = _lastElements;
				}
				else {
					try {
						elements = ((ICodeAssist)input).codeSelect(wordRegion.getOffset(), wordRegion.getLength());

						elements = _selectOpenableElements(elements);

						_lastInput = input;
						_lastModStamp = modStamp;
						_lastWordRegion = wordRegion;
						_lastElements = elements;
					}
					catch (JavaModelException jme) {
					}
				}

				if (elements.length != 0) {
					List<IHyperlink> links = new ArrayList<>(elements.length);

					for (IJavaElement element : elements) {
						if (element instanceof IMethod) {
							_addHyperlinks(
								links, wordRegion, (SelectionDispatchAction)openAction, (IMethod)element,
								elements.length > 1, (JavaEditor)textEditor);
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

		return retval;
	}

	@Override
	public void dispose() {
		super.dispose();
		_lastElements = null;
		_lastInput = null;
		_lastWordRegion = null;
	}

	private void _addHyperlinks(
		List<IHyperlink> links, IRegion word, SelectionDispatchAction openAction, IMethod method, boolean qualify,
		JavaEditor editor) {

		if (_shouldAddServiceHyperlink(editor)) {
			IMethod implMethod = _getServiceImplMethod(method);

			if (implMethod != null) {
				links.add(new ServiceMethodImplementationHyperlink(word, openAction, implMethod, qualify));
			}

			IMethodWrapper wrapperMethod = _getServiceWrapperMethod(method);

			if (wrapperMethod == null) {
				return;
			}

			if (wrapperMethod._base) {
				links.add(
					new ServiceMethodWrapperLookupHyperlink(editor, word, openAction, wrapperMethod._method, qualify));
			}
			else {
				links.add(new ServiceMethodWrapperHyperlink(word, openAction, wrapperMethod._method, qualify));
			}
		}
	}

	private IType _findType(IJavaElement parent, String fullyQualifiedName) throws JavaModelException {
		IType retval = parent.getJavaProject().findType(fullyQualifiedName);

		if (retval == null) {
			IJavaProject[] serviceProjects = ServiceUtil.getAllServiceProjects();

			for (IJavaProject sp : serviceProjects) {
				try {
					retval = sp.findType(fullyQualifiedName);
				}
				catch (Exception e) {
				}

				if (retval != null) {
					break;
				}
			}
		}

		return retval;
	}

	private IMethod _getServiceImplMethod(IMethod method) {
		IMethod retval = null;

		try {
			IJavaElement methodClass = method.getParent();
			IType methodClassType = method.getDeclaringType();
			String methodClassName = methodClass.getElementName();

			if (methodClassName.endsWith("Util") && JdtFlags.isPublic(method) && JdtFlags.isStatic(method)) {
				String packageName = methodClassType.getPackageFragment().getElementName();
				String baseServiceName = methodClassName.substring(0, methodClassName.length() - 4);

				/*
				 * as per liferay standard real implementation will be in impl package and Impl suffix
				 * e.g. com.example.service.FooUtil.getBar() --> com.example.service.impl.FooImpl.getBar()
				 */
				String fullyQualifiedName = packageName + ".impl." + baseServiceName + "Impl";

				IType implType = _findType(methodClass, fullyQualifiedName);

				if (implType != null) {
					IMethod[] methods = implType.findMethods(method);

					if (ListUtil.isEmpty(methods)) {
						ITypeHierarchy hierarchy = implType.newSupertypeHierarchy(new NullProgressMonitor());
						IType currentType = implType;

						while ((retval == null) && (currentType != null)) {
							methods = currentType.findMethods(method);

							if (ListUtil.isNotEmpty(methods)) {
								retval = methods[0];
							}
							else {
								currentType = hierarchy.getSuperclass(currentType);
							}
						}
					}
					else {
						retval = methods[0];
					}
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	private IMethodWrapper _getServiceWrapperMethod(IMethod method) {
		IMethodWrapper retval = null;

		try {
			IJavaElement methodClass = method.getParent();
			IType methodClassType = method.getDeclaringType();
			String methodClassName = methodClass.getElementName();

			if (methodClassName.endsWith("Util") && JdtFlags.isPublic(method) && JdtFlags.isStatic(method)) {
				String packageName = methodClassType.getPackageFragment().getElementName();
				String baseServiceName = methodClassName.substring(0, methodClassName.length() - 4);

				/*
				 * as per liferay standard wrapper type will be in service package with Wrapper suffix
				 * e.g com.example.service.FooUtil.getBar() --> com.example.service.FooWrapper.getBar()
				 */
				String fullyQualifiedName = packageName + "." + baseServiceName + "Wrapper";

				IType wrapperType = _findType(methodClass, fullyQualifiedName);

				if (wrapperType != null) {
					IMethod[] wrapperBaseMethods = wrapperType.findMethods(method);

					if (ListUtil.isNotEmpty(wrapperBaseMethods)) {

						// look for classes that implement this wrapper

						List<IMethod> overrides = new ArrayList<>();

						SearchRequestor requestor = new WrapperMethodCollector(overrides, method);

						IJavaSearchScope scope = SearchEngine.createStrictHierarchyScope(
							null, wrapperType, true, false, null);

						SearchPattern search = SearchPattern.createPattern(
							method.getElementName(), IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS,
							SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);

						new SearchEngine().search(
							search, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope,
							requestor, new NullProgressMonitor());

						if (overrides.size() > 1) {
							retval = new IMethodWrapper(wrapperBaseMethods[0], true);
						}
						else if (overrides.size() == 1) {
							retval = new IMethodWrapper(overrides.get(0), false);
						}
					}
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	private boolean _isInheritDoc(IDocument document, IRegion wordRegion) {
		try {
			String word = document.get(wordRegion.getOffset(), wordRegion.getLength());

			return "inheritDoc".equals(word);
		}
		catch (BadLocationException ble) {
			return false;
		}
	}

	private boolean _isRegionValid(IDocument document, IRegion wordRegion) {
		if ((wordRegion != null) && (wordRegion.getLength() != 0) && !_isInheritDoc(document, wordRegion)) {
			return true;
		}

		return false;
	}

	private IJavaElement[] _selectOpenableElements(IJavaElement[] elements) {
		List<IJavaElement> result = new ArrayList<>(elements.length);

		for (int i = 0; i < elements.length; i++) {
			IJavaElement element = elements[i];

			switch (element.getElementType()) {
				case IJavaElement.PACKAGE_DECLARATION:
				case IJavaElement.PACKAGE_FRAGMENT:
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				case IJavaElement.JAVA_PROJECT:
				case IJavaElement.JAVA_MODEL:
					break;
				default:
					result.add(element);
					break;
			}
		}

		return result.toArray(new IJavaElement[result.size()]);
	}

	private boolean _shouldAddServiceHyperlink(JavaEditor editor) {
		return SelectionConverter.canOperateOn(editor);
	}

	private boolean _shouldDetectHyperlinks(
		ITextEditor textEditor, ITypeRoot input, IAction openAction, IRegion region) {

		if ((region != null) && (textEditor instanceof JavaEditor) && (openAction instanceof SelectionDispatchAction) &&
			(input != null)) {

			return true;
		}

		return false;
	}

	private IJavaElement[] _lastElements;
	private ITypeRoot _lastInput;
	private long _lastModStamp;
	private IRegion _lastWordRegion;

	private static class IMethodWrapper {

		public IMethodWrapper(IMethod method, boolean base) {
			_method = method;
			_base = base;
		}

		private boolean _base;
		private IMethod _method;

	}

	private static class WrapperMethodCollector extends SearchRequestor {

		public WrapperMethodCollector(List<IMethod> results, IMethod method) {
			_results = results;
			_method = method;
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();

			if (element instanceof IMethod && _matches((IMethod)element)) {
				_results.add((IMethod)element);
			}
		}

		private boolean _matches(IMethod element) throws JavaModelException {
			boolean matches = false;

			if (this._method.getNumberOfParameters() == element.getNumberOfParameters()) {
				matches = true;

				for (int i = 0; i < this._method.getTypeParameters().length; i++) {
					if (!this._method.getParameterTypes()[i].equals(element.getParameterTypes()[i])) {
						matches = false;
						break;
					}
				}
			}

			return matches;
		}

		private IMethod _method;
		private List<IMethod> _results;

	}

}