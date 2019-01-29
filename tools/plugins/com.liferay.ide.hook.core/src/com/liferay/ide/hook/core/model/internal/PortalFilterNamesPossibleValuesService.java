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
import com.liferay.ide.server.util.ServerUtil;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class PortalFilterNamesPossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		if (_servletFilterNames == null) {
			Element element = context().find(Element.class);

			IFile hookFile = element.adapt(IFile.class);

			if (hookFile != null) {
				try {
					ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, hookFile.getProject());

					if (liferayProject != null) {
						ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

						if (portal != null) {
							IPath appServerPortalDir = portal.getAppServerPortalDir();

							if (appServerPortalDir != null) {
								_servletFilterNames = ServerUtil.getServletFilterNames(appServerPortalDir);
							}
						}
					}
				}
				catch (Exception e) {
				}
			}
		}

		if (_servletFilterNames != null) {
			Collections.addAll(values, _servletFilterNames);
		}
	}

	private String[] _servletFilterNames;

}