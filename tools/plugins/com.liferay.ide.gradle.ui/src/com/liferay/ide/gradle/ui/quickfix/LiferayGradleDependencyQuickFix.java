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

package com.liferay.ide.gradle.ui.quickfix;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.LiferayGradleWorkspaceProject;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.manipulation.TypeNameMatchCollector;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;

/**
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class LiferayGradleDependencyQuickFix implements IQuickFixProcessor {

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations)
		throws CoreException {

		if (ListUtil.isEmpty(locations) ||
			!(LiferayWorkspaceUtil.getLiferayWorkspaceProject() instanceof LiferayGradleWorkspaceProject)) {

			return new IJavaCompletionProposal[0];
		}

		List<IJavaCompletionProposal> resultingCollections = new ArrayList<>();

		for (IProblemLocation problemLocation : locations) {
			try {
				Set<IJavaCompletionProposal> newProposals = _process(context, problemLocation);

				resultingCollections.addAll(newProposals);
			}
			catch (JavaModelException jme) {

				// ignore

			}
		}

		return resultingCollections.toArray(new IJavaCompletionProposal[resultingCollections.size()]);
	}

	@Override
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		switch (problemId) {
			case IProblem.ImportNotFound:
			case IProblem.UndefinedType:
			case IProblem.UndefinedField:
			case IProblem.UndefinedName:
			case IProblem.UnresolvedVariable:
				return true;
			default:
				return false;
		}
	}

	private GradleDependency _parseGradleDependency(IClasspathEntry entry) {
		try {
			IPath path = entry.getPath();

			String[] items = path.segments();

			//parse from file path "**/**/group name/artifact name/version/sha1 value/jar name"

			return new GradleDependency(items[items.length - 5], items[items.length - 4], items[items.length - 3]);
		}
		catch (Exception e) {
			return null;
		}
	}

	private Set<IJavaCompletionProposal> _process(IInvocationContext context, IProblemLocation problem)
		throws JavaModelException {

		int id = problem.getProblemId();

		if (id == 0) {
			return Collections.emptySet();
		}

		ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());

		if (selectedNode == null) {
			return Collections.emptySet();
		}

		if (id == IProblem.ImportNotFound) {
			ImportDeclaration importDeclaration = (ImportDeclaration)ASTNodes.getParent(
				selectedNode, ASTNode.IMPORT_DECLARATION);

			if (importDeclaration == null) {
				return Collections.emptySet();
			}

			String name = ASTNodes.asString(importDeclaration.getName());

			if (importDeclaration.isOnDemand()) {
				name = JavaModelUtil.concatenateName(name, "*");
			}

			return _processProposals(name, context);
		}
		else {
			if (selectedNode instanceof Name) {
				Name node = (Name)selectedNode;

				return _processProposals(node.getFullyQualifiedName(), context);
			}
		}

		return Collections.emptySet();
	}

	private Set<IJavaCompletionProposal> _processProposals(String name, IInvocationContext context)
		throws JavaModelException {

		ICompilationUnit compilationUnit = context.getCompilationUnit();

		IJavaProject javaProject = compilationUnit.getJavaProject();

		IProject project = javaProject.getProject();

		IFile gradleFile = project.getFile("build.gradle");

		if (!gradleFile.exists()) {
			return Collections.emptySet();
		}

		int index = name.lastIndexOf('.');

		char[] packageName = null;

		if (index != -1) {
			String substring = name.substring(0, index);

			packageName = substring.toCharArray();
		}

		String substring = name.substring(index + 1);

		char[] typeName = substring.toCharArray();

		if ((typeName.length == 1) && (typeName[0] == '*')) {
			typeName = null;
		}

		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();

		ArrayList<TypeNameMatch> result = new ArrayList<>();

		TypeNameMatchCollector requestor = new TypeNameMatchCollector(result);

		int matchMode = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;

		new SearchEngine().searchAllTypeNames(
			packageName, matchMode, typeName, matchMode, IJavaSearchConstants.TYPE, scope, requestor,
			IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);

		if (result.isEmpty()) {
			return Collections.emptySet();
		}

		Set<IJavaCompletionProposal> proposals = new HashSet<>();

		for (TypeNameMatch item : result) {
			IType type = item.getType();

			if (type != null) {
				IPackageFragmentRoot packageFragmentroot = (IPackageFragmentRoot)type.getAncestor(
					IJavaElement.PACKAGE_FRAGMENT_ROOT);

				IClasspathEntry entry = packageFragmentroot.getRawClasspathEntry();

				if (entry == null) {
					continue;
				}

				int entryKind = entry.getEntryKind();

				GradleDependency dependency = null;

				if (entryKind == IClasspathEntry.CPE_CONTAINER) {
					IPath entryPath = entry.getPath();

					if (_projectSpecificContainer(entryPath)) {
						continue;
					}

					IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(
						entryPath, packageFragmentroot.getJavaProject());

					if (classpathContainer != null) {
						entry = JavaModelUtil.findEntryInContainer(classpathContainer, packageFragmentroot.getPath());

						if (entry != null) {
							dependency = _parseGradleDependency(entry);
						}
					}
				}
				else if (entryKind == IClasspathEntry.CPE_LIBRARY) {
					dependency = _parseGradleDependency(entry);
				}

				if (dependency != null) {
					String displayName =
						"Add Dependency '" + dependency.getGroup() + ":" + dependency.getName() + ":" +
							dependency.getVersion() + "' to Gradle";

					proposals.add(
						new DependencyCorrectionProposal(displayName, compilationUnit, dependency, gradleFile));
				}
			}
		}

		return proposals;
	}

	private boolean _projectSpecificContainer(IPath containerPath) {
		if (containerPath.segmentCount() > 0) {
			String id = containerPath.segment(0);

			if (id.equals(JavaCore.USER_LIBRARY_CONTAINER_ID) || id.equals(JavaRuntime.JRE_CONTAINER)) {
				return true;
			}
		}

		return false;
	}

}