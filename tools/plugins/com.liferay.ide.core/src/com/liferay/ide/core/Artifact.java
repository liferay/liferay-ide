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

import java.util.Objects;

/**
 * @author Charles Wu
 * @author Terry Jia
 */
public class Artifact {

	public Artifact() {
	}

	public Artifact(String groupId, String artifactId, String version, String configuration, File source) {
		_groupId = groupId;
		_artifactId = artifactId;
		_version = version;
		_configuration = configuration;
		_source = source;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (!(object instanceof Artifact)) {
			return false;
		}

		Artifact artifact = (Artifact)object;

		if (Objects.equals(_artifactId, artifact._artifactId) && Objects.equals(_groupId, artifact._groupId)) {
			if ((_version != null) && (artifact._version != null)) {
				return Objects.equals(_version, artifact._version);
			}

			return true;
		}

		return false;
	}

	public String getArtifactId() {
		return _artifactId;
	}

	public String getConfiguration() {
		return _configuration;
	}

	public String getGroupId() {
		return _groupId;
	}

	public File getSource() {
		return _source;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_artifactId, _groupId, _version);
	}

	public void setArtifactId(String artifactId) {
		_artifactId = artifactId;
	}

	public void setConfiguration(String configuration) {
		_configuration = configuration;
	}

	public void setGroupId(String groupId) {
		_groupId = groupId;
	}

	public void setSource(File source) {
		_source = source;
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Override
	public String toString() {
		return _artifactId + ":" + _version;
	}

	private String _artifactId;
	private String _configuration;
	private String _groupId;
	private File _source;
	private String _version;

}