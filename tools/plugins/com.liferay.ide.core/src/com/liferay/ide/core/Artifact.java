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

package com.liferay.ide.core;

import java.io.File;

import java.util.Map;
import java.util.Objects;

/**
 * @author Charles Wu
 */
public class Artifact {

	public Artifact(Map<String, String> dep) {
		_group = dep.get("group");
		_artifact = dep.get("name");
		_version = dep.get("version");
		_sourceFile = null;
	}

	public Artifact(String group, String artifact, String version) {
		this(group, artifact, version, null);
	}

	public Artifact(String group, String artifact, String version, File sourceFile) {
		_group = group;
		_artifact = artifact;
		_version = version;
		_sourceFile = sourceFile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}

		Artifact artifact = (Artifact)o;

		if (Objects.equals(_artifact, artifact._artifact) && Objects.equals(_group, artifact._group) &&
			Objects.equals(_version, artifact._version)) {

			return true;
		}

		return false;
	}

	public String getArtifact() {
		return _artifact;
	}

	public String getGroup() {
		return _group;
	}

	public File getSourceFile() {
		return _sourceFile;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_artifact, _group, _version);
	}

	@Override
	public String toString() {
		return _artifact + ":" + _version;
	}

	private final String _artifact;
	private final String _group;
	private final File _sourceFile;
	private final String _version;

}