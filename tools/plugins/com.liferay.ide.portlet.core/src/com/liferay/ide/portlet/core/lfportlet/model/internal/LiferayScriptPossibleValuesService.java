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

package com.liferay.ide.portlet.core.lfportlet.model.internal;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;

/**
 * @author Simon Jiang
 */
public class LiferayScriptPossibleValuesService extends PossibleValuesService {

	@Override
	public boolean strict() {
		return false;
	}

	@Override
	protected void compute(Set<String> values) {
		Element modeElement = context(Element.class);

		List<FileExtensions> exts = modeElement.parent().definition().getAnnotations(FileExtensions.class);

		if ((exts != null) && (exts.size() > 0)) {
			this.type = exts.get(0).expr();
			IProject project = modeElement.adapt(IProject.class);

			if (project != null) {
				IFolder webappRoot = CoreUtil.getDefaultDocrootFolder(project);

				if (webappRoot != null) {
					IPath location = webappRoot.getLocation();

					if (location != null) {
						if (location.toFile().exists()) {
							values.addAll(new PropertiesVisitor().visitScriptFiles(webappRoot, type, values));
						}
					}
				}
			}
		}
	}

	private String type;

	private static class PropertiesVisitor implements IResourceProxyVisitor {

		IResource entryResource = null;
		String type = null;
		Set<String> values = null;

		public boolean visit(IResourceProxy resourceProxy) {
			if (resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith(type)) {
				IResource resource = resourceProxy.requestResource();

				if (resource.exists()) {
					String relativePath = resource.getLocation().makeRelativeTo(entryResource.getLocation()).toString();

					try {
						if (!relativePath.startsWith("/")) {
							values.add("/" + relativePath);
						}
					}
					catch (Exception e) {
						return true;
					}
				}
			}

			return true;
		}

		public Set<String> visitScriptFiles(IResource container, String type, Set<String> values) {
			this.entryResource = container;
			this.type = type;
			this.values = values;
			try {
				container.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}

			return values;
		}

	}

}