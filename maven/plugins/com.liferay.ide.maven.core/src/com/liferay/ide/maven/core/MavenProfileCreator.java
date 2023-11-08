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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.xml.core.internal.provisional.format.NodeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public interface MavenProfileCreator extends SapphireContentAccessor {

	public default void addToActiveProfiles(
		final NewLiferayPluginProjectOp op, final NewLiferayProfile newLiferayProfile) {

		// should append to current list

		String activeProfilesValue = get(op.getActiveProfilesValue());

		StringBuilder sb = new StringBuilder();

		if (CoreUtil.isNotNullOrEmpty(activeProfilesValue)) {
			sb.append(activeProfilesValue);
			sb.append(',');
		}

		sb.append(get(newLiferayProfile.getId()));

		op.setActiveProfilesValue(sb.toString());
	}

	public default Node createNewLiferayProfileNode(Document pomDocument, NewLiferayProfile newLiferayProfile) {
		Node newNode = null;

		String liferayVersion = get(newLiferayProfile.getLiferayVersion());

		try {
			String runtimeName = get(newLiferayProfile.getRuntimeName());

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(ServerUtil.getRuntime(runtimeName));

			Element root = pomDocument.getDocumentElement();

			Element profiles = NodeUtil.findChildElement(root, "profiles");

			if (profiles == null) {
				newNode = profiles = NodeUtil.appendChildElement(root, "profiles");
			}

			Element newProfile = null;

			if (profiles != null) {
				NodeUtil.appendTextNode(profiles, "\n");

				newProfile = NodeUtil.appendChildElement(profiles, "profile");

				NodeUtil.appendTextNode(profiles, "\n");

				if (newNode == null) {
					newNode = newProfile;
				}
			}

			if (newProfile != null) {
				NodeUtil.appendTextNode(newProfile, "\n\t");

				NodeUtil.appendChildElement(newProfile, "id", get(newLiferayProfile.getId()));
				NodeUtil.appendTextNode(newProfile, "\n\t");

				Element activationElement = NodeUtil.appendChildElement(newProfile, "activation");

				NodeUtil.appendTextNode(activationElement, "\n\t\t");
				NodeUtil.appendChildElement(activationElement, "activeByDefault", "true");
				NodeUtil.appendTextNode(activationElement, "\n\t");

				NodeUtil.appendTextNode(newProfile, "\n\t");

				Element propertiesElement = NodeUtil.appendChildElement(newProfile, "properties");

				NodeUtil.appendTextNode(newProfile, "\n\t");
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(propertiesElement, "liferay.version", liferayVersion);
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(propertiesElement, "liferay.maven.plugin.version", liferayVersion);
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");

				IPath serverDir = liferayRuntime.getAppServerDir();

				IPath rootPath = serverDir.removeLastSegments(1);

				IPath autoDeployDir = rootPath.append("deploy");

				NodeUtil.appendChildElement(propertiesElement, "liferay.auto.deploy.dir", autoDeployDir.toOSString());

				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.deploy.dir",
					FileUtil.toOSString(liferayRuntime.getAppServerDeployDir()));
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.lib.global.dir",
					FileUtil.toOSString(liferayRuntime.getAppServerLibGlobalDir()));
				NodeUtil.appendTextNode(propertiesElement, "\n\t\t");
				NodeUtil.appendChildElement(
					propertiesElement, "liferay.app.server.portal.dir",
					FileUtil.toOSString(liferayRuntime.getAppServerPortalDir()));
				NodeUtil.appendTextNode(propertiesElement, "\n\t");

				NodeFormatter formatter = new NodeFormatter();

				formatter.format(root);
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Unable to add new liferay profile.", e);
		}

		return newNode;
	}

}