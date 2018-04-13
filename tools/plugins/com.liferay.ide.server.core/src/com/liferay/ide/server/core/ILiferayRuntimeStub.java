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

package com.liferay.ide.server.core;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Greg Amerson
 */
public interface ILiferayRuntimeStub {

	public String getName();

	public String getRuntimeStubTypeId();

	public IStatus validate();

	public String DEFAULT = "default";

	public String EXTENSION_ID = "com.liferay.ide.server.core.runtimeStubs";

	public String NAME = "name";

	public String RUNTIME_TYPE_ID = "runtimeTypeId";

}