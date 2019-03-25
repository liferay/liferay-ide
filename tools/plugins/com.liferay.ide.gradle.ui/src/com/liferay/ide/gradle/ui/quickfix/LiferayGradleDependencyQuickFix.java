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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ArtifactBuilder;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.LiferayGradleWorkspaceProject;
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
public class LiferayGradleDependencyQuickFix implements ArtifactBuilder, IQuickFixProcessor {

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] problemLocations)
		throws CoreException {

		if (ListUtil.isEmpty(problemLocations) ||
			!(LiferayWorkspaceUtil.getLiferayWorkspaceProject() instanceof LiferayGradleWorkspaceProject)) {

			return new IJavaCompletionProposal[0];
		}

		List<IJavaCompletionProposal> resultingCollections = new ArrayList<>();

		for (IProblemLocation problemLocation : problemLocations) {
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
	public boolean hasCorrections(ICompilationUnit compilationUit, int problemId) {
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

	private Set<IJavaCompletionProposal> _process(IInvocationContext context, IProblemLocation problemLocation)
		throws JavaModelException {

		int id = problemLocation.getProblemId();

		if (id == 0) {
			return Collections.emptySet();
		}

		ASTNode selectedASTNode = problemLocation.getCoveringNode(context.getASTRoot());

		if (selectedASTNode == null) {
			return Collections.emptySet();
		}

		if (id == IProblem.ImportNotFound) {
			ImportDeclaration importDeclaration = (ImportDeclaration)ASTNodes.getParent(
				selectedASTNode, ASTNode.IMPORT_DECLARATION);

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
			if (selectedASTNode instanceof Name) {
				Name node = (Name)selectedASTNode;

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

		IJavaSearchScope javaSearchScope = SearchEngine.createWorkspaceScope();

		ArrayList<TypeNameMatch> typeNameMatches = new ArrayList<>();

		TypeNameMatchCollector typeNameMatchCollector = new TypeNameMatchCollector(typeNameMatches);

		int matchMode = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;

		SearchEngine searchEngine = new SearchEngine();

		searchEngine.searchAllTypeNames(
			packageName, matchMode, typeName, matchMode, IJavaSearchConstants.TYPE, javaSearchScope,
			typeNameMatchCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);

		if (typeNameMatches.isEmpty()) {
			return Collections.emptySet();
		}

		Set<IJavaCompletionProposal> javaCompletionProposals = new HashSet<>();

		for (TypeNameMatch typeNameMatch : typeNameMatches) {
			IType type = typeNameMatch.getType();

			if (type != null) {
				IPackageFragmentRoot packageFragmentroot = (IPackageFragmentRoot)type.getAncestor(
					IJavaElement.PACKAGE_FRAGMENT_ROOT);

				IClasspathEntry classpathEntry = packageFragmentroot.getRawClasspathEntry();

				if (classpathEntry == null) {
					continue;
				}

				int entryKind = classpathEntry.getEntryKind();

				Artifact dependency = null;

				if (entryKind == IClasspathEntry.CPE_CONTAINER) {
					IPath entryPath = classpathEntry.getPath();

					if (_projectSpecificContainer(entryPath)) {
						continue;
					}

					IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(
						entryPath, packageFragmentroot.getJavaProject());

					if (classpathContainer != null) {
						classpathEntry = JavaModelUtil.findEntryInContainer(
							classpathContainer, packageFragmentroot.getPath());

						if (classpathEntry != null) {
							dependency = classpathEntryToArtifact(classpathEntry);
						}
					}
				}
				else if (entryKind == IClasspathEntry.CPE_LIBRARY) {
					dependency = classpathEntryToArtifact(classpathEntry);
				}

				if (dependency != null) {
					String displayName =
						"Add Dependency '" + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" +
							dependency.getVersion() + "' to Gradle";

					javaCompletionProposals.add(
						new DependencyCorrectionProposal(displayName, compilationUnit, dependency, gradleFile));
				}
			}
		}

		return javaCompletionProposals;
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