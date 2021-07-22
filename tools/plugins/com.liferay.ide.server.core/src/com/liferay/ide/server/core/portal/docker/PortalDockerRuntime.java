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

package com.liferay.ide.server.core.portal.docker;

import com.github.dockerjava.api.model.Image;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.Objects;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */
public class PortalDockerRuntime extends PortalRuntime implements IPortalDockerRuntime {

	public static final String ID = "com.liferay.ide.server.portal.docker.runtime";

	public static final String PROP_DOCKER_IMAGE_ID = "docker-image-id";

	public static final String PROP_DOCKER_IMAGE_REPO = "docker-image-repo";

	public static final String PROP_DOCKER_IMAGE_TAG = "docker-image-tag";

	@Override
	public String getImageId() {
		return getAttribute(PROP_DOCKER_IMAGE_ID, (String)null);
	}

	@Override
	public String getImageRepo() {
		return getAttribute(PROP_DOCKER_IMAGE_REPO, (String)null);
	}

	@Override
	public String getImageTag() {
		return getAttribute(PROP_DOCKER_IMAGE_TAG, (String)null);
	}

	public void setImageId(String imageId) {
		setAttribute(PROP_DOCKER_IMAGE_ID, imageId);
	}

	public void setImageRepo(String imageRepo) {
		setAttribute(PROP_DOCKER_IMAGE_REPO, imageRepo);
	}

	public void setImageTag(String imageTag) {
		setAttribute(PROP_DOCKER_IMAGE_TAG, imageTag);
	}

	@Override
	public IStatus validate() {
		String runtimeName = getRuntime().getName();

		if ((runtimeName == null) || (runtimeName.length() == 0)) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorRuntimeName, null);
		}

		if (_isNameInUse()) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _errorDuplicateRuntimeName, null);
		}

		if (CoreUtil.isNullOrEmpty(getImageTag())) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, _nullRuntimeName, null);
		}

		String imageRepoTag = String.join(":", getImageRepo(), getImageTag());

		Image dockerImage = LiferayDockerClient.getDockerImageByName(imageRepoTag);

		if (Objects.isNull(dockerImage)) {
			return LiferayServerCore.createErrorStatus("Image is not existed");
		}

		return Status.OK_STATUS;
	}

	private boolean _isNameInUse() {
		IRuntime originRuntime = getRuntime();

		if (getRuntimeWorkingCopy() != null) {
			originRuntime = getRuntimeWorkingCopy().getOriginal();
		}

		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (runtimes != null) {
			int size = runtimes.length;

			String runtimeName = getRuntime().getName();

			for (int i = 0; i < size; i++) {
				if ((originRuntime != runtimes[i]) && runtimeName.equals(runtimes[i].getName())) {
					return true;
				}
			}
		}

		return false;
	}

	private static String _errorDuplicateRuntimeName = "The name is already in use. Specify a different name.";
	private static String _errorRuntimeName = "Enter a name for the runtime environment.";
	private static String _nullRuntimeName = "The docker image of runtime can be found.";

}