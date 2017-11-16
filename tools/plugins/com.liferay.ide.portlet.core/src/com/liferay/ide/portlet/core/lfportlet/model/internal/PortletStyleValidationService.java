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
import com.liferay.ide.portlet.core.lfportlet.model.PortletStyleElement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class PortletStyleValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Element modelElement = context(Element.class);

		if (!modelElement.disposed() && modelElement instanceof PortletStyleElement) {
			Path path = (Path)modelElement.property(context(ValueProperty.class)).content();

			if (path != null) {
				String name = path.lastSegment();
				IProject project = modelElement.adapt(IProject.class);

				boolean fileExisted = new FileCheckVisitor().checkFiles(project, name);

				if (!fileExisted) {
					return Status.createErrorStatus("File " + path.toPortableString() + " is not existed");
				}
			}
			else {
				return Status.createErrorStatus("Can not set empty value");
			}
		}

		return Status.createOkStatus();
	}

	private static class FileCheckVisitor implements IResourceProxyVisitor {

		public boolean checkFiles(IResource container, String searchFileName) {
			this.searchFileName = searchFileName;

			try {
				container.accept(this, IContainer.EXCLUDE_DERIVED);
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}

			return fileExisted;
		}

		public boolean visit(IResourceProxy resourceProxy) {
			if ((resourceProxy.getType() == IResource.FILE) && resourceProxy.getName().equals(searchFileName)) {
				IResource resource = resourceProxy.requestResource();

				if (resource.exists()) {
					fileExisted = true;
				}
			}

			return true;
		}

		public boolean fileExisted = false;
		public String searchFileName = null;

	}

}