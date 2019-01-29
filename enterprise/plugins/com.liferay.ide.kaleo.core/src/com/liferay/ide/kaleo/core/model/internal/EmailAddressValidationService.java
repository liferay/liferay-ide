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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class EmailAddressValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		if (_shouldValidate) {
			Value<?> value = context(Value.class);

			if (!value.empty()) {
				if (!_emailAddressPattern.matcher(value.content().toString()).matches()) {
					retval = Status.createErrorStatus("Email address syntax is not valid");
				}
			}
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		Version schemaVersion = _getSchemaVersion();

		_shouldValidate = schemaVersion.compareTo(new Version("6.2")) >= 0;
	}

	private Version _getSchemaVersion() {
		Version schemaVersion = new Version(KaleoCore.DEFAULT_KALEO_VERSION);

		if (context(WorkflowDefinition.class) != null) {
			WorkflowDefinition workflowDefinition = context(WorkflowDefinition.class);

			Value<Version> version = workflowDefinition.getSchemaVersion();

			schemaVersion = version.content();
		}
		else if (context(NewNodeOp.class) != null) {
			NewNodeOp newNodeOp = context(NewNodeOp.class);

			ElementHandle<WorkflowDefinition> workflowDef = newNodeOp.getWorkflowDefinition();

			WorkflowDefinition workflowDefinition = workflowDef.content();

			Value<Version> version = workflowDefinition.getSchemaVersion();

			schemaVersion = version.content();
		}
		else if (context(NewWorkflowDefinitionOp.class) != null) {
			NewWorkflowDefinitionOp newWorkflowDenitionOp = context(NewWorkflowDefinitionOp.class);

			ReferenceValue<String, IProject> opProject = newWorkflowDenitionOp.getProject();

			IProject project = opProject.target();

			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				schemaVersion = new Version(portal.getVersion());
			}
		}

		return schemaVersion;
	}

	private static final Pattern _emailAddressPattern = Pattern.compile("[^@]+@[^\\.]+\\..+");

	private boolean _shouldValidate;

}