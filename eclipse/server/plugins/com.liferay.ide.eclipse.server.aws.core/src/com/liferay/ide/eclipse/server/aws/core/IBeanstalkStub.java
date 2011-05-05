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
public interface IBeanstalkStub {

	String ATTR_RUNTIME_TYPE_ID = "runtimeTypeId";

	String getRuntimeTypeId();

	void setRuntimeLocation(IPath location);

	void setRuntimeTypeId(String runtimeTypeId);

}
