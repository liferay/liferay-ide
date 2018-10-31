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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.WorkspaceConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Haoyi Sun
 * @author Terry Jia
 */
public class TargetLiferayVersionListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		String liferayVersion = property.toString();

		Element newLiferayWorkspaceOp = property.element();

		NewLiferayWorkspaceOp op = newLiferayWorkspaceOp.adapt(NewLiferayWorkspaceOp.class);

		String bundleUrl = SapphireUtil.getContent(op.getBundleUrl());

		List<String> bundleUrls = new ArrayList<>();

		bundleUrls.add(WorkspaceConstants.BUNDLE_URL_CE_7_1);
		bundleUrls.add(WorkspaceConstants.BUNDLE_URL_CE_7_0);

		if (bundleUrls.contains(bundleUrl)) {
			if (liferayVersion.equals("7.1")) {
				op.setBundleUrl(WorkspaceConstants.BUNDLE_URL_CE_7_1);
			}
			else {
				op.setBundleUrl(WorkspaceConstants.BUNDLE_URL_CE_7_0);
			}
		}

		op.setTargetPlatform(WorkspaceConstants.liferayVersionsToTargetPlatformVersions.get(liferayVersion)[0]);
	}

}