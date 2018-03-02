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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 */
public class JavaPackageNameDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String retval = null;

		NewLiferayComponentOp op = _op();

		IJavaProject project = op.adapt(IJavaProject.class);

		if (project == null) {
			return retval;
		}

		try {
			List<IFolder> srcFolders = CoreUtil.getSourceFolders(project);

			IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();

			if (ListUtil.isEmpty(roots)) {
				return retval;
			}

			for (IPackageFragmentRoot root : roots) {
				IJavaElement[] packages = root.getChildren();

				if (ListUtil.isNotEmpty(packages)) {
					for (IJavaElement element : packages) {
						if (element instanceof IPackageFragment) {
							IPackageFragment fragment = (IPackageFragment)element;

							boolean inSourceFolder = false;

							for (IFolder srcFolder : srcFolders) {
								if (srcFolder.getFullPath().isPrefixOf(fragment.getPath())) {
									inSourceFolder = true;

									break;
								}
							}

							if (inSourceFolder) {
								String elementName = fragment.getElementName();

								if (!CoreUtil.isNullOrEmpty(elementName)) {
									retval = elementName;
								}
							}
						}
					}
				}
			}
		}
		catch (JavaModelException jme) {
		}

		return retval;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayComponentOp op = _op();

		op.property(NewLiferayComponentOp.PROP_PROJECT_NAME).attach(listener);
	}

	private NewLiferayComponentOp _op() {
		return context(NewLiferayComponentOp.class);
	}

}