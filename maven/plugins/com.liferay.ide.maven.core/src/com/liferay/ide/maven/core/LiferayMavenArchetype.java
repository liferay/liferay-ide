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

package com.liferay.ide.maven.core;

import org.apache.maven.archetype.catalog.Archetype;

import org.eclipse.m2e.core.project.IArchetype;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class LiferayMavenArchetype implements IArchetype {

	public LiferayMavenArchetype(Archetype archetype) {
		_archetype = archetype;
	}

	@Override
	public String getArtifactId() {
		return _archetype.getArtifactId();
	}

	@Override
	public String getGroupId() {
		return _archetype.getGroupId();
	}

	@Override
	public String getVersion() {
		return _archetype.getVersion();
	}

	private Archetype _archetype;

}