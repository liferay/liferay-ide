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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.FileWriter;

import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Charles Wu
 * @author Simon Jiang
 */
public class LiferayMavenModuleProjectDeleteParticipant extends DeleteParticipant {

	public LiferayMavenModuleProjectDeleteParticipant() {
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
		throws OperationCanceledException {

		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		CompositeChange change = new CompositeChange(getName());

		change.add(new RemoveModuleFromParentChange(_parentMavenProject));

		return change;
	}

	@Override
	public String getName() {
		return "Remove module from parent maven project " + _parentMavenProject.getName();
	}

	public class RemoveModuleFromParentChange extends Change {

		public RemoveModuleFromParentChange(MavenProject mavenProject) {
			_mavenProject = mavenProject;
		}

		@Override
		public Object getModifiedElement() {
			return _mavenProject;
		}

		@Override
		public String getName() {
			return "Remove module project " + _moduleProject.getName();
		}

		@Override
		public void initializeValidationData(IProgressMonitor pm) {
		}

		@Override
		public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
			return new RefactoringStatus();
		}

		@Override
		public Change perform(IProgressMonitor pm) throws CoreException {
			String parentProjectName = _mavenProject.getName();

			IProject parentProject = CoreUtil.getProject(parentProjectName);

			if (FileUtil.notExists(parentProject)) {
				return null;
			}

			Model parentModel = _mavenProject.getOriginalModel();

			parentModel.removeModule(_moduleProject.getName());

			File pomFile = _mavenProject.getFile();

			if (FileUtil.notExists(pomFile)) {
				return null;
			}

			try (FileWriter fileWriter = new FileWriter(pomFile)) {
				MavenXpp3Writer mavenWriter = new MavenXpp3Writer();

				mavenWriter.write(fileWriter, parentModel);
			}
			catch (Exception e) {
			}

			return this;
		}

		private MavenProject _mavenProject;

	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject)) {
			return false;
		}

		_moduleProject = (IProject)element;

		IMavenProjectFacade mavenFacade = MavenUtil.getProjectFacade((IProject)element, new NullProgressMonitor());

		try {
			MavenProject selectedMavenProject = mavenFacade.getMavenProject(new NullProgressMonitor());

			if (!selectedMavenProject.hasParent()) {
				return false;
			}

			_parentMavenProject = selectedMavenProject.getParent();

			if (_parentMavenProject == null) {
				return false;
			}

			List<String> modules = _parentMavenProject.getModules();

			return modules.contains(_moduleProject.getName());
		}
		catch (CoreException ce) {
		}

		return false;
	}

	private IProject _moduleProject;
	private MavenProject _parentMavenProject;

}