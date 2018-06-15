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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.gradle.ui.GradleUI;
import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.util.TargetPlatformUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jdt.ui.text.java.correction.CUCorrectionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.osgi.framework.Bundle;

/**
 * @author Lovett Li
 */
@SuppressWarnings("restriction")
public class QuickFixGradleDep implements IQuickFixProcessor {

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) {
		if (ListUtil.isEmpty(locations)) {
			return new IJavaCompletionProposal[0];
		}

		ICompilationUnit unit = context.getCompilationUnit();

		IResource resource = unit.getResource();

		IProject project = resource.getProject();

		_gradleFile = project.getFile("build.gradle");

		List<IJavaCompletionProposal> resultingCollections = new ArrayList<>();

		if (FileUtil.exists(_gradleFile)) {
			for (IProblemLocation curr : locations) {
				_process(context, curr, resultingCollections);
			}
		}

		return resultingCollections.toArray(new IJavaCompletionProposal[resultingCollections.size()]);
	}

	@Override
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		switch (problemId) {
			case IProblem.ImportNotFound:
			case IProblem.UndefinedType:
				return true;
			default:
				return false;
		}
	}

	private void _createDepProposal(
		IInvocationContext context, Collection<IJavaCompletionProposal> proposals, ServiceContainer bundle) {

		String bundleGroup = bundle.getBundleGroup();
		String bundleName = bundle.getBundleName();
		String bundleVersion = bundle.getBundleVersion();

		proposals.add(
			new CUCorrectionProposal("Add Gradle Dependence " + bundleName, context.getCompilationUnit(), null, -0) {

				@Override
				public void apply(IDocument document) {
					try {
						File gradleFile = FileUtil.getFile(_gradleFile);

						GradleDependencyUpdater updater = new GradleDependencyUpdater(gradleFile);

						List<GradleDependency> existDependencies = updater.getAllDependencies();

						GradleDependency gd = new GradleDependency(bundleGroup, bundleName, bundleVersion);

						if (!existDependencies.contains(gd)) {
							updater.insertDependency(gd);

							Files.write(gradleFile.toPath(), updater.getGradleFileContents(), StandardCharsets.UTF_8);

							ICompilationUnit unit = context.getCompilationUnit();

							IResource resource = unit.getResource();

							GradleUtil.refreshProject(resource.getProject());
						}
					}
					catch (Exception e) {
						GradleCore.logError("Gradle dependence got error", e);
					}
				}

				@Override
				public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
					return "Add dependenece";
				}

				@Override
				public Image getImage() {
					Display display = UIUtil.getActiveShellDisplay();

					try {
						Bundle bundle = GradleUI.getDefaultBundle();

						URL url = FileLocator.toFileURL(bundle.getEntry("icons/e16/liferay_logo_16.png"));

						return new Image(display, url.getFile());
					}
					catch (IOException ioe) {
						GradleUI.logError(ioe);
					}

					return null;
				}

			});
	}

	private void _importNotFoundProposal(
		IInvocationContext context, IProblemLocation problem, Collection<IJavaCompletionProposal> proposals) {

		ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());

		if (selectedNode == null) {
			return;
		}

		ImportDeclaration importDeclaration = (ImportDeclaration)ASTNodes.getParent(
			selectedNode, ASTNode.IMPORT_DECLARATION);

		if (importDeclaration == null) {
			return;
		}

		try {
			List<String> serviceWrapperList = TargetPlatformUtil.getServiceWrapperList().getServiceList();

			Name name = importDeclaration.getName();

			String importName = name.toString();

			boolean depWrapperCanFixed = false;

			if (serviceWrapperList.contains(importName)) {
				ServiceContainer bundle = TargetPlatformUtil.getServiceWrapperBundle(importName);
				depWrapperCanFixed = true;

				_createDepProposal(context, proposals, bundle);
			}

			if (!depWrapperCanFixed) {
				List<String> servicesList = TargetPlatformUtil.getServicesList().getServiceList();

				if (servicesList.contains(importName)) {
					ServiceContainer bundle = TargetPlatformUtil.getServiceBundle(importName);

					_createDepProposal(context, proposals, bundle);
				}
			}

			if (TargetPlatformUtil.getThirdPartyBundleList(importName) != null) {
				ServiceContainer bundle = TargetPlatformUtil.getThirdPartyBundleList(importName);

				_createDepProposal(context, proposals, bundle);
			}
		}
		catch (Exception e) {
			GradleCore.logError("Gradle dependence got error", e);
		}
	}

	private void _process(
		IInvocationContext context, IProblemLocation problem, List<IJavaCompletionProposal> proposals) {

		int id = problem.getProblemId();

		if (id == 0) {
			return;
		}

		switch (id) {
			case IProblem.ImportNotFound:
				_importNotFoundProposal(context, problem, proposals);

				break;
			case IProblem.UndefinedType:
				_undefinedType(context, problem, proposals);

				break;
		}
	}

	private void _undefinedType(
		IInvocationContext context, IProblemLocation problem, Collection<IJavaCompletionProposal> proposals) {

		ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
		String fullyQualifiedName = null;

		if (selectedNode instanceof Name) {
			Name node = (Name)selectedNode;

			fullyQualifiedName = node.getFullyQualifiedName();
		}

		boolean depWrapperCanFixed = false;

		try {
			List<String> serviceWrapperList = TargetPlatformUtil.getServiceWrapperList().getServiceList();

			for (String wrapper : serviceWrapperList) {
				if (wrapper.endsWith(fullyQualifiedName)) {
					ServiceContainer bundle = TargetPlatformUtil.getServiceWrapperBundle(wrapper);

					_createDepProposal(context, proposals, bundle);
				}
			}

			if (!depWrapperCanFixed) {
				List<String> servicesList = TargetPlatformUtil.getServicesList().getServiceList();

				for (String service : servicesList) {
					if (service.endsWith(fullyQualifiedName)) {
						ServiceContainer bundle = TargetPlatformUtil.getServiceBundle(service);

						_createDepProposal(context, proposals, bundle);
					}
				}
			}
		}
		catch (Exception e) {
			GradleCore.logError("Gradle dependence got error", e);
		}
	}

	private IFile _gradleFile;

}