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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Haoyi Sun
 */
public class TargetLiferayVersionListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		String bundleVersion = property.toString();

		Element newLiferayWorkspaceOp = property.element();

		NewLiferayWorkspaceOp op = newLiferayWorkspaceOp.adapt(NewLiferayWorkspaceOp.class);

		String bundleUrl = SapphireUtil.getContent(op.getBundleUrl());

		List<String> bundleVersionList = new ArrayList<>();

		bundleVersionList.add(BaseLiferayWorkspaceOp.LIFERAY_71_BUNDLE_URL);
		bundleVersionList.add(BaseLiferayWorkspaceOp.LIFERAY_70_BUNDLE_URL);

		if (bundleVersionList.contains(bundleUrl)) {
			if (bundleVersion.equals("7.1")) {
				op.setBundleUrl(BaseLiferayWorkspaceOp.LIFERAY_71_BUNDLE_URL);
			}
			else {
				op.setBundleUrl(BaseLiferayWorkspaceOp.LIFERAY_70_BUNDLE_URL);
			}
		}
	}

}