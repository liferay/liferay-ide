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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.ui.ProjectUI;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.correction.CUCorrectionProposal;
import org.eclipse.jface.text.IDocument;

/**
 * @author Charles Wu
 */
public class DependencyCorrectionProposal extends CUCorrectionProposal {

	public DependencyCorrectionProposal(
		String name, ICompilationUnit compiletionUnit, GradleDependency gradleDependency, IFile file) {

		super(name, compiletionUnit, 30, ProjectUI.getPluginImageRegistry().get(ProjectUI.LIFERAY_LOGO_IMAGE_ID));

		_gradleDependency = gradleDependency;
		_gradleFile = file;
	}

	@Override
	public void apply(IDocument document) {
		try {
			GradleDependencyUpdater gradleDependencyUpdater = new GradleDependencyUpdater(_gradleFile);

			StringBuilder sb = new StringBuilder();

			sb.append("compileOnly '");
			sb.append(_gradleDependency.getGroup());
			sb.append(":");
			sb.append(_gradleDependency.getName());

			IWorkspaceProject workspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

			if (workspaceProject.getTargetPlatformVersion() == null) {
				sb.append(":");
				sb.append(_gradleDependency.getVersion());
			}

			sb.append("'");

			gradleDependencyUpdater.updateDependency(sb.toString());

			GradleUtil.refreshProject(_gradleFile.getProject());
		}
		catch (IOException ioe) {
			LiferayGradleCore.logError(ioe);
		}

		super.apply(document);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		DependencyCorrectionProposal otherDependencyCorrectionProposal = (DependencyCorrectionProposal)obj;

		if ((_gradleDependency == null) || (otherDependencyCorrectionProposal._gradleDependency == null)) {
			return false;
		}

		if (!_gradleDependency.equals(otherDependencyCorrectionProposal._gradleDependency)) {
			return false;
		}

		return true;
	}

	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		return "Add Gradle Dependency to build.gradle file";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_gradleDependency == null) ? 0 : _gradleDependency.hashCode());

		return result;
	}

	private final GradleDependency _gradleDependency;
	private final IFile _gradleFile;

}