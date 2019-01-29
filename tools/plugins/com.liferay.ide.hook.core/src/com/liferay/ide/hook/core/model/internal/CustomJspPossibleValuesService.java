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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;
import java.io.FileFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class CustomJspPossibleValuesService extends PossibleValuesService {

	@Override
	public Status problem(Value<?> value) {
		return Status.createOkStatus();
	}

	@Override
	protected void compute(final Set<String> values) {
		if (_possibleValues == null) {
			IProject project = project();

			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			if (liferayProject != null) {
				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					_portalDir = new Path(FileUtil.toPortableString(portal.getAppServerPortalDir()));

					if (_portalDir != null) {
						File portalDirFile = _portalDir.toFile();

						File htmlDirFile = new File(portalDirFile, "html");

						List<File> fileValues = new LinkedList<>();

						if (FileUtil.exists(htmlDirFile)) {
							_findJSPFiles(new File[] {htmlDirFile}, fileValues);
						}
						else {
							File[] files = portalDirFile.listFiles(_jspfilter);

							_findJSPFiles(files, fileValues);
						}

						_possibleValues = fileValues.toArray(new File[0]);
					}
				}
			}
		}

		if (_possibleValues != null) {
			for (File file : _possibleValues) {
				Path path = new Path(file.getAbsolutePath()).makeRelativeTo(_portalDir);

				values.add(path.toPortableString());
			}
		}
	}

	protected IProject project() {
		Element root = context(Element.class).root();

		IFile file = root.adapt(IFile.class);

		return file.getProject();
	}

	private void _findJSPFiles(File[] files, List<File> fileValues) {
		if (ListUtil.isNotEmpty(files)) {
			for (File file : files) {
				if (file.isDirectory()) {
					_findJSPFiles(file.listFiles(_jspfilter), fileValues);
				}
				else {
					fileValues.add(file);
				}
			}
		}
	}

	private final FileFilter _jspfilter = new FileFilter() {

		public boolean accept(File pathname) {
			String name = pathname.getName();

			if (pathname.isDirectory() || name.endsWith(".jsp") || name.endsWith(".jspf")) {
				return true;
			}

			return false;
		}

	};

	private Path _portalDir;
	private File[] _possibleValues;

}