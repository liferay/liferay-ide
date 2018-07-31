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
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class CustomJspValidationService extends ValidationService {

	@Override
	public Status compute() {
		Value<?> value = (Value<?>)context(Element.class).property(context(Property.class).definition());

		ValueProperty property = value.definition();

		String label = property.getLabel(true, CapitalizationType.NO_CAPS, false);

		if (_isValueEmpty(value)) {
			String msg = NLS.bind(Msgs.nonEmptyValueRequired, label);

			return Status.createErrorStatus(msg);
		}
		else if (!_isValidPortalJsp(value) && !_isValidProjectJsp(value)) {
			String msg = NLS.bind(Msgs.customJspInvalidPath, label);

			return Status.createErrorStatus(msg);
		}

		return Status.createOkStatus();
	}

	protected Hook hook() {
		return context().find(Hook.class);
	}

	protected IProject project() {
		return hook().adapt(IProject.class);
	}

	private IPath _getPortalDir() {
		if (_portalDir == null) {
			try {
				Element element = context().find(Element.class);

				Hook hook = element.nearest(Hook.class);

				IFile file = hook.adapt(IFile.class);

				IProject project = file.getProject();

				ILiferayProject liferayProject = LiferayCore.create(project);

				if (liferayProject != null) {
					ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

					if (portal != null) {
						_portalDir = portal.getAppServerPortalDir();
					}
				}
			}
			catch (Exception e) {
				HookCore.logError(e);
			}
		}

		return _portalDir;
	}

	private boolean _isValidPortalJsp(Value<?> value) {
		Object fileName = value.content();

		IPath customJspPath = _getPortalDir().append(fileName.toString());

		if (FileUtil.exists(customJspPath)) {
			return true;
		}

		return false;
	}

	private boolean _isValidProjectJsp(Value<?> value) {
		Object fileName = value.content();

		IFolder customFolder = HookUtil.getCustomJspFolder(hook(), project());

		if (FileUtil.exists(customFolder)) {
			IFile customJspFile = customFolder.getFile(fileName.toString());

			if (FileUtil.exists(customJspFile)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isValueEmpty(Value<?> value) {
		if (value.content(false) == null) {
			return true;
		}

		return false;
	}

	private IPath _portalDir;

	private static class Msgs extends NLS {

		public static String customJspInvalidPath;
		public static String nonEmptyValueRequired;

		static {
			initializeMessages(CustomJspValidationService.class.getName(), Msgs.class);
		}

	}

}