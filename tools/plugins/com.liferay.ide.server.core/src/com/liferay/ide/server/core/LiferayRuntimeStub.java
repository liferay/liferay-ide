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
import org.eclipse.core.runtime.Status;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStub implements ILiferayRuntimeStub {

	public String getName() {
		return name;
	}

	public String getRuntimeStubTypeId() {
		return runtimeStubTypeId;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRuntimeTypeId(String runtimeTypeId) {
		runtimeStubTypeId = runtimeTypeId;
	}

	public IStatus validate() {
		return Status.OK_STATUS;
	}

	protected boolean isDefault;
	protected String name;
	protected String runtimeStubTypeId;

}