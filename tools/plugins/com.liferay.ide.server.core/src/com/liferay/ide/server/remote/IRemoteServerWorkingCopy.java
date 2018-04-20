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

package com.liferay.ide.server.remote;

import com.liferay.ide.server.core.ILiferayServerWorkingCopy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Greg Amerson
 */
public interface IRemoteServerWorkingCopy extends ILiferayServerWorkingCopy, IRemoteServer {

	public void setHTTPPort(String httpPort);

	public void setLiferayPortalContextPath(String path);

	public void setServerManagerContextPath(String path);

	public IStatus validate(IProgressMonitor monitor);

}