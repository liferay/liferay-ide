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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class OverrideFilePathPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public Status problem(Value<?> value) {
		ElementList<OverrideFilePath> currentFiles = _op().getOverrideFiles();

		int count = 0;

		Object o = value.content();

		for (OverrideFilePath currentFile : currentFiles) {
			String content = get(currentFile.getValue());

			if (content != null) {
				String v = o.toString();

				if (v.equals(content)) {
					count++;
				}
			}
		}

		if ((count >= 0) && (_possibleValues != null) &&
			(_possibleValues.contains(o.toString()) || _possibleValues.contains("resource-actions/default.xml"))) {

			return Status.createOkStatus();
		}
		else {
			return super.problem(value);
		}
	}

	@Override
	protected void compute(Set<String> values) {
		NewModuleFragmentOp op = _op();

		String hostOSGiBundle = get(op.getHostOsgiBundle());

		if (hostOSGiBundle == null) {
			return;
		}

		if ((_osgiBundleName == null) || !_osgiBundleName.equals(hostOSGiBundle) || (_possibleValues == null)) {
			_osgiBundleName = hostOSGiBundle;

			_possibleValues = new HashSet<>();

			String runtimeName = get(op.getLiferayRuntimeName());

			IRuntime runtime = ServerUtil.getRuntime(runtimeName);

			if (!hostOSGiBundle.endsWith("jar")) {
				hostOSGiBundle = hostOSGiBundle + ".jar";
			}

			ProjectCore projectCore = ProjectCore.getDefault();

			IPath tempLocation = projectCore.getStateLocation();

			File module = FileUtil.getFile(tempLocation.append(hostOSGiBundle));

			if (FileUtil.notExists(module)) {
				module = ServerUtil.getModuleFileFrom70Server(runtime, hostOSGiBundle, tempLocation);
			}

			if (FileUtil.exists(module)) {
				try (JarFile jar = new JarFile(module)) {
					Enumeration<JarEntry> enu = jar.entries();

					while (enu.hasMoreElements()) {
						JarEntry entry = enu.nextElement();

						String name = entry.getName();

						if ((name.startsWith("META-INF/resources/") &&
							 (name.endsWith(".jsp") || name.endsWith(".jspf"))) ||
							name.equals("portlet.properties") || name.equals("resource-actions/default.xml")) {

							_possibleValues.add(name);
						}
					}
				}
				catch (Exception e) {
				}
			}
		}

		if (_possibleValues != null) {
			Set<String> possibleValuesSet = new HashSet<>();

			possibleValuesSet.addAll(_possibleValues);

			ElementList<OverrideFilePath> currentFiles = op.getOverrideFiles();

			if (currentFiles != null) {
				for (OverrideFilePath cj : currentFiles) {
					String value = get(cj.getValue());

					if (value != null) {
						possibleValuesSet.remove(value);
					}
				}
			}

			String projectName = get(op.getProjectName());

			if (projectName != null) {
				IProject project = CoreUtil.getProject(projectName);

				IFolder javaFolder = project.getFolder("src/main/java");

				IFolder resourceFolder = project.getFolder("src/main/resources");

				IFolder resourceActionFolder = resourceFolder.getFolder("resource-actions");

				Iterator<String> it = possibleValuesSet.iterator();

				while (it.hasNext()) {
					String v = it.next();

					if (FileUtil.exists(resourceFolder.getFile(v))) {
						it.remove();
					}

					if (FileUtil.exists(javaFolder.getFile("portlet-ext.properties")) &&
						v.equals("portlet.properties")) {

						it.remove();
					}

					if (FileUtil.exists(resourceActionFolder) &&
						FileUtil.exists(resourceActionFolder.getFile("default-ext.xml")) && v.contains("default.xml")) {

						it.remove();
					}
				}
			}

			values.addAll(possibleValuesSet);
		}
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

	private static Set<String> _possibleValues;

	private String _osgiBundleName;

}