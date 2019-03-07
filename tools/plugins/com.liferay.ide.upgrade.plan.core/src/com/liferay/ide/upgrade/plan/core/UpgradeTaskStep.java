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

package com.liferay.ide.upgrade.plan.core;

import java.util.List;

import org.apache.http.MethodNotSupportedException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public interface UpgradeTaskStep extends UpgradePlanElement {

	public boolean completed();

	public boolean enabled();

	public List<UpgradeTaskStepAction> getActions();

	public double getOrder();

	public UpgradeTaskStepRequirement getRequirement();

	public default UpgradeTaskStepStatus getStatus() {
		return UpgradeTaskStepStatus.INCOMPLETE;
	}

	public String getTaskId();

	public String getUrl();

	public default IStatus perform(IProgressMonitor progressMonitor) {
		return Status.OK_STATUS;
	}

	public default void setStatus(UpgradeTaskStepStatus status) {
		MethodNotSupportedException exception = new MethodNotSupportedException(
			"Implementer must implement this method.");

		throw new RuntimeException(exception);
	}

}