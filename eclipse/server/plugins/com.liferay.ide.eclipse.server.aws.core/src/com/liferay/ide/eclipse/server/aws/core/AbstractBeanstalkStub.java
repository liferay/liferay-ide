/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.core;

import org.eclipse.core.runtime.IPath;

/**
 * @author Greg Amerson
 */
public abstract class AbstractBeanstalkStub implements IBeanstalkStub {

	protected IPath lastValidRuntimeLocation;

	protected IPath runtimeLocation;

	protected String runtimeTypeId;

	public AbstractBeanstalkStub() {
		// do nothing
	}

	public IPath getRuntimeLocation() {
		return this.runtimeLocation;
	}

	public String getRuntimeTypeId() {
		return this.runtimeTypeId;
	}

	public void setRuntimeLocation(IPath location) {
		this.runtimeLocation = location;
		this.lastValidRuntimeLocation = null;
	}

	public void setRuntimeTypeId(String runtimeTypeId) {
		this.runtimeTypeId = runtimeTypeId;
	}

}
