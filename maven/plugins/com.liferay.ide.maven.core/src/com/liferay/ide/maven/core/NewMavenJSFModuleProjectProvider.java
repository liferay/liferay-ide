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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;
import org.apache.maven.archetype.ArchetypeManager;
import org.apache.maven.archetype.catalog.Archetype;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.platform.PathBridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewMavenJSFModuleProjectProvider
	extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp> {

	public NewMavenJSFModuleProjectProvider() {
	}

	@Override
	public IStatus createNewProject(NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor)
		throws CoreException, InterruptedException {

		IStatus retval = null;

		IPath projectLocation = createArchetypeProject(op, monitor);

		FileUtil.delete(projectLocation.append("build.gradle"));

		Value<String> projectNameValue = op.getProjectName();

		String projectName = projectNameValue.content();

		CoreUtil.openProject(projectName, projectLocation, monitor);

		MavenUtil.updateProjectConfiguration(projectName, projectLocation.toOSString(), monitor);

		retval = Status.OK_STATUS;

		return retval;
	}

	@Override
	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		if ("archetypeGAV".equals(key) && type.equals(String.class) && (params.length == 1)) {
			List<T> retval = new ArrayList<>();

			String templateName = params[0].toString();

			Version latestVersion = _extractLatestVersion("com.liferay.faces.archetype." + templateName + ".portlet");

			String gav =
				"com.liferay.faces.archetype:com.liferay.faces.archetype." + templateName + ".portlet:" +
					latestVersion.toString();

			retval.add(type.cast(gav));

			return retval;
		}

		return super.getData(key, type, params);
	}

	public String getHttpResponse(String request) {
		StringBuilder retVal = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();

		try {

			// create httpget

			HttpGet httpGet = new HttpGet(request);

			// execute httpRequest

			HttpResponse response = httpClient.execute(httpGet);

			StatusLine statusLine = response.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();

				String body = CoreUtil.readStreamToString(entity.getContent(), false);

				EntityUtils.consume(entity);

				retVal.append(body);
			}
			else {
				return statusLine.getReasonPhrase();
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Failed to http response from maven central", e);
		}

		return retVal.toString();
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		boolean liferayWorkspace = LiferayWorkspaceUtil.isValidWorkspaceLocation(path);

		if (liferayWorkspace) {
			retval = LiferayMavenCore.createErrorStatus(
				"Can not set WorkspaceProject root folder as project directory.");

			File workspaceDir = LiferayWorkspaceUtil.getWorkspaceDir(path.toFile());

			if (FileUtil.notExists(workspaceDir)) {
				return LiferayCore.createErrorStatus("The project location of Liferay Workspace shoule be existed.");
			}

			String[] folders = LiferayWorkspaceUtil.getLiferayWorkspaceProjectWarsDirs(workspaceDir.getAbsolutePath());

			if (folders != null) {
				boolean appendWarFolder = false;

				for (String folder : folders) {
					if (StringUtil.endsWith(path.lastSegment(), folder)) {
						appendWarFolder = true;

						break;
					}
				}

				if (!appendWarFolder) {
					return LiferayMavenCore.createErrorStatus(
						"The project location should be wars folder of Liferay workspace.");
				}
			}
			else {
				return LiferayMavenCore.createErrorStatus("The Liferay Workspace was not defined wars folder path.");
			}
		}

		return retval;
	}

	protected IPath createArchetypeProject(NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor)
		throws CoreException {

		IPath projectLocation = null;
		String javaPackage = "com.example";

		String projectName = SapphireUtil.getContent(op.getProjectName());

		IPath location = PathBridge.create(SapphireUtil.getContent(op.getLocation()));

		// for location we should use the parent location

		if (StringUtil.equals(location.lastSegment(), projectName)) {

			// use parent dir since maven archetype will generate new dir under this
			// location

			location = location.removeLastSegments(1);
		}

		String groupId = SapphireUtil.getContent(op.getProjectName());
		String artifactId = SapphireUtil.getContent(op.getProjectName());
		String version = "1.0.0";

		String archetypeArtifactId = SapphireUtil.getContent(op.getArchetype());

		Archetype archetype = new Archetype();

		String[] gav = archetypeArtifactId.split(":");

		String archetypeVersion = gav[gav.length - 1];

		archetype.setGroupId(gav[0]);
		archetype.setArtifactId(gav[1]);

		archetype.setVersion(archetypeVersion);

		Artifact artifact = AetherUtil.getLatestAvailableArtifact(archetypeArtifactId);

		Properties properties = new Properties();

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		if (location == null) {
			location = workspaceRoot.getLocation();
		}

		try {
			MavenPluginActivator pluginActivator = MavenPluginActivator.getDefault();

			ArchetypeGenerationRequest request = new ArchetypeGenerationRequest();

			MavenImpl mavenImpl = pluginActivator.getMaven();

			request.setTransferListener(mavenImpl.createTransferListener(monitor));

			request.setArchetypeGroupId(artifact.getGroupId());
			request.setArchetypeArtifactId(artifact.getArtifactId());
			request.setArchetypeVersion(artifact.getVersion());

			RemoteRepository remoteRepository = AetherUtil.newCentralRepository();

			request.setArchetypeRepository(remoteRepository.getUrl());

			request.setGroupId(groupId);
			request.setArtifactId(artifactId);
			request.setVersion(version);
			request.setPackage(javaPackage);

			// the model does not have a package field

			request.setLocalRepository(mavenImpl.getLocalRepository());
			request.setRemoteArtifactRepositories(mavenImpl.getArtifactRepositories(true));
			request.setProperties(properties);
			request.setOutputDirectory(location.toPortableString());

			ArchetypeGenerationResult result = _getArchetyper().generateProjectFromArchetype(request);

			Exception cause = result.getCause();

			if (cause != null) {
				throw new CoreException(LiferayCore.createErrorStatus("Unable to create project from archetype."));
			}

			projectLocation = location.append(artifactId);

			if (FileUtil.notExists(projectLocation)) {
				throw new CoreException(LiferayCore.createErrorStatus("Can not create gradle JSF project. "));
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayCore.createErrorStatus("Failed to create JSF project. ", e));
		}

		return projectLocation;
	}

	private Version _extractLatestVersion(String artifactId) {
		Version maxVersion = new Version("5.0.1");

		try {
			String responseString = getHttpResponse(
				"http://search.maven.org/solrsearch/select?q=g:com.liferay.faces.archetype+AND+a:" + artifactId +
					"&rows=20&wt=json");

			Object result = _getJSONResponse(responseString);

			if (result instanceof JSONObject) {
				JSONObject jsonResult = (JSONObject)result;

				Object response = jsonResult.get("response");

				if (response != null) {
					JSONObject jsonResponse = (JSONObject)response;

					Object docs = jsonResponse.get("docs");

					if ((docs != null) && docs instanceof JSONArray) {
						JSONArray jsonDocs = (JSONArray)docs;

						for (int i = 0; i < jsonDocs.length(); i++) {
							JSONObject jsonDoc = (JSONObject)jsonDocs.get(i);

							String versionString = (String)jsonDoc.get("latestVersion");

							Version tmpVersion = Version.parseVersion(versionString);

							if ((tmpVersion.compareTo(maxVersion) > 0) || (maxVersion == null)) {
								maxVersion = tmpVersion;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			LiferayMavenCore.logError("Failed to get latest JSF archtype version", e);
		}

		return maxVersion;
	}

	private ArchetypeManager _getArchetyper() {
		MavenPluginActivator plugin = MavenPluginActivator.getDefault();

		org.eclipse.m2e.core.internal.archetype.ArchetypeManager archetypeManager = plugin.getArchetypeManager();

		return archetypeManager.getArchetyper();
	}

	private Object _getJSONResponse(String response) {
		Object retval = null;

		try {
			retval = new JSONObject(response);
		}
		catch (JSONException e) {
			try {
				retval = new JSONArray(response);
			}
			catch (JSONException e1) {
			}
		}

		return retval;
	}

}