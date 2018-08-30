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
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

import java.util.List;

import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ServiceDefaultValuesService extends DefaultValueService {

	@Override
	protected String compute() {
		NewLiferayModuleProjectOp op = _op();

		String template = SapphireUtil.getContent(op.getProjectTemplateName());

		String retVal = "";

		if (template.equals("service-wrapper")) {
			try {
				ServiceContainer allServicesWrapper = TargetPlatformUtil.getServiceWrapperList();

				List<String> serviceWrapperList = allServicesWrapper.getServiceList();

				if (ListUtil.isNotEmpty(serviceWrapperList)) {
					retVal = serviceWrapperList.get(0);
				}
			}
			catch (Exception e) {
				ProjectCore.logError("Get service wrapper list error.", e);
			}
		}
		else if (template.equals("service")) {
			try {
				ServiceContainer allServices = TargetPlatformUtil.getServicesList();

				List<String> serviceList = allServices.getServiceList();

				if (ListUtil.isNotEmpty(serviceList)) {
					retVal = serviceList.get(0);
				}
			}
			catch (Exception e) {
				ProjectCore.logError("Get services list error. ", e);
			}
		}

		return retVal;
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

}