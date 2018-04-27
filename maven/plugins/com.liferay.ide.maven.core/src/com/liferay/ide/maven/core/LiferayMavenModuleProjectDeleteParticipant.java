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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;

/**
 * @author Charles Wu
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

		IPath moduleLocation = _moduleProject.getLocation();

		File pomFile = moduleLocation.removeLastSegments(1).append("pom.xml").toFile();

		change.add(new RemoveModuleFromParentChange(pomFile));

		return change;
	}

	@Override
	public String getName() {
		return _REMOVE_MAVEN_MODULE_FROM_WORKSPACE;
	}

	public class RemoveModuleFromParentChange extends Change {

		public RemoveModuleFromParentChange(File pomFile) {
			_pomFile = pomFile;
		}

		@Override
		public String getName() {
			return _REMOVE_MAVEN_MODULE_FROM_WORKSPACE;
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
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();

			try (FileReader reader = new FileReader(_pomFile)) {
				Model model = mavenReader.read(reader);

				List<String> modules = new ArrayList<>();

				for (String name : model.getModules()) {
					if (_moduleProject.getName().equals(name)) {
						continue;
					}

					modules.add(name);
				}

				model.setModules(modules);

				try (FileWriter fileWriter = new FileWriter(_pomFile)) {
					MavenXpp3Writer mavenWriter = new MavenXpp3Writer();

					mavenWriter.write(fileWriter, model);
				}
			}
			catch (Exception e) {
			}

			return null;
		}

		@Override public Object getModifiedElement() {
			return _pomFile;
		}

		private File _pomFile;

	}

	@Override
	protected boolean initialize(Object element) {
		if (!(element instanceof IProject)) {
			return false;
		}

		_moduleProject = (IProject)element;

		IPath moduleLocation = _moduleProject.getLocation();

		File pomFile = moduleLocation.removeLastSegments(1).append("pom.xml").toFile();

		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

		try (FileReader reader = new FileReader(pomFile)) {
			Model model = mavenReader.read(reader);

			for (String name : model.getModules()) {
				if (_moduleProject.getName().equals(name)) {
					return true;
				}
			}

		}
		catch (Exception e) {
			return false;
		}

		return false;
	}

	private static final String _REMOVE_MAVEN_MODULE_FROM_WORKSPACE = "Remove module from parent maven pom.xml";

	private IProject _moduleProject;

}