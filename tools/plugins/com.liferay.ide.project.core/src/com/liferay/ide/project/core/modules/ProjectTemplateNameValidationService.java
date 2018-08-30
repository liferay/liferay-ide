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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.ListUtil;

import java.util.Set;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class ProjectTemplateNameValidationService extends ValidationService {

	@Override
	protected Status compute() {
		return _templateNameStatus;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		Value<Object> value = op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME);

		ProjectTemplateNamePossibleValuesService pvs = value.service(ProjectTemplateNamePossibleValuesService.class);

		Set<String> templateNames = pvs.values();

		if (ListUtil.isNotEmpty(templateNames)) {
			_templateNameStatus = Status.createOkStatus();
		}
		else {
			pvs.attach(
				new Listener() {

					@Override
					public void handle(Event event) {
						_templateNameStatus = Status.createOkStatus();

						refresh();
					}

				});
		}
	}

	private Status _templateNameStatus = Status.createErrorStatus("Downloading templates, please wait...");

}