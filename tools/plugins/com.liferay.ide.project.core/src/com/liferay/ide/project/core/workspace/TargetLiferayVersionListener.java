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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Haoyi Sun
 * @author Terry Jia
 */
public class TargetLiferayVersionListener
	extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		String liferayVersion = property.toString();

		Element newLiferayWorkspaceOp = property.element();

		NewLiferayWorkspaceOp op = newLiferayWorkspaceOp.adapt(NewLiferayWorkspaceOp.class);

		Value<String> targetPlatform = op.getTargetPlatform();

		targetPlatform.clear();

		CompletableFuture<Map<String, String[]>> future = CompletableFuture.supplyAsync(
			() -> {
				try {
					return ProjectUtil.initMavenTargetPlatform();
				}
				catch (Exception exception) {
					return new HashMap<>();
				}
			});

		future.thenAccept(
			new Consumer<Map<String, String[]>>() {

				@Override
				public void accept(Map<String, String[]> mavenTargetPlatform) {
					op.setTargetPlatform(mavenTargetPlatform.get(liferayVersion)[0]);
				}

			});
	}

}