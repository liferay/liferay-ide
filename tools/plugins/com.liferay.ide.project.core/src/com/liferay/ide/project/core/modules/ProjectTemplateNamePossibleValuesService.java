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

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ProjectTemplateNamePossibleValuesService extends PossibleValuesService {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	public Status problem(Value<?> value) {
		return Status.createOkStatus();
	}

	@Override
	protected void compute(Set<String> values) {
		try {
			String[] projectTemplates = BladeCLI.getProjectTemplatesNames();

			for (String projectTemplate : projectTemplates) {
				if (!projectTemplate.contains("fragment") && !projectTemplate.contains("modules-ext") &&
					!projectTemplate.contains("spring-mvc-portlet") && !projectTemplate.contains("client-extension")) {

					values.add(projectTemplate);
				}
			}
		}
		catch (BladeCLIException bclie) {
		}
	}

}