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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;

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
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
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

		Property property = modeElement.parent();

		PropertyDef propertyDef = property.definition();

		List<FileExtensions> exts = propertyDef.getAnnotations(FileExtensions.class);

		if (ListUtil.isEmpty(exts)) {
			return;
		}

		FileExtensions extensions = exts.get(0);

		_type = extensions.expr();

		IProject project = modeElement.adapt(IProject.class);

		if (project != null) {
			IFolder webappRoot = CoreUtil.getDefaultDocrootFolder(project);

			if (FileUtil.exists(webappRoot)) {
				values.addAll(new PropertiesVisitor().visitScriptFiles(webappRoot, _type, values));
			}
		}
	}

	private String _type;

	private static class PropertiesVisitor implements IResourceProxyVisitor {

		public boolean visit(IResourceProxy resourceProxy) {
			if ((resourceProxy.getType() == IResource.FILE) && StringUtil.endsWith(resourceProxy.getName(), _type)) {
				IResource resource = resourceProxy.requestResource();

				if (resource.exists()) {
					IPath location = resource.getLocation();

					IPath path = location.makeRelativeTo(_entryResource.getLocation());

					String relativePath = path.toString();

					try {
						if (!relativePath.startsWith("/")) {
							_values.add("/" + relativePath);
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
			_entryResource = container;
			_type = type;
			_values = values;

			try {
				container.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}

			return values;
		}

		private IResource _entryResource = null;
		private String _type = null;
		private Set<String> _values = null;

	}

}