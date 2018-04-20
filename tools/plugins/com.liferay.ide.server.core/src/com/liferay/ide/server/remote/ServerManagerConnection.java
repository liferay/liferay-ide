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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.RemoteConnection;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import org.eclipse.core.runtime.IProgressMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class ServerManagerConnection extends RemoteConnection implements IServerManagerConnection {

	public ServerManagerConnection() {
		this(null, null, null, null, null);
	}

	public ServerManagerConnection(
		String host, String httpPort, String username, String pw, String managerContextPath) {

		setHost(host);
		setHttpPort(httpPort);
		setUsername(username);
		setPassword(pw);
		_managerContextPath = managerContextPath;
	}

	public int getDebugPort() throws APIException {
		String debugPort = getRemoteServerConfig(_getDebugPortAPI());

		if (debugPort != null) {
			return Integer.parseInt(debugPort);
		}

		return -1;
	}

	public String getFMDebugPassword() throws APIException {
		return getRemoteServerConfig(_getFMDebugPasswordAPI());
	}

	public int getFMDebugPort() throws APIException {
		String fmDebugPort = getRemoteServerConfig(_getFMDebugPortAPI());

		if (fmDebugPort != null) {
			return Integer.parseInt(fmDebugPort);
		}

		return -1;
	}

	public List<String> getLiferayPlugins() {
		List<String> retval = new ArrayList<>();

		Object response = null;

		try {
			response = getJSONAPI(_getPluginsAPI());
		}
		catch (APIException apie) {
			LiferayServerCore.logError(apie);
		}

		if (response instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)response;

			try {
				if (_isSuccess(jsonObject)) {
					JSONArray jsonPlugins = _getJSONOutput(jsonObject);

					for (int i = 0; i < jsonPlugins.length(); i++) {
						Object jsonPlugin = jsonPlugins.get(i);

						retval.add(jsonPlugin.toString());
					}
				}
			}
			catch (Exception e) {
				LiferayServerCore.logError(e);
			}
		}

		return retval;
	}

	public String getManagerURI() {
		return "http://" + getHost() + ":" + getHttpPort() + _managerContextPath;
	}

	public String getRemoteServerConfig(String configAPI) throws APIException {
		if (isAlive()) {
			Object response = getJSONAPI(configAPI);

			if (response instanceof JSONObject) {
				JSONObject jsonResponse = (JSONObject)response;

				try {
					if (_isSuccess(jsonResponse)) {
						return _getJSONOutput(jsonResponse);
					}
				}
				catch (JSONException jsone) {
					throw new APIException(configAPI, jsone);
				}
			}
		}

		return null;
	}

	public String getServerState() throws APIException {
		if (isAlive()) {
			return "STARTED";
		}
		else {
			return "STOPPED";
		}
	}

	public Object installApplication(String absolutePath, String appName, IProgressMonitor submon) throws APIException {
		try {
			FileBody fileBody = new FileBody(new File(absolutePath));

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("deployWar", fileBody);

			HttpPost httpPost = new HttpPost();

			httpPost.setEntity(entity);

			Object response = httpJSONAPI(httpPost, _getDeployURI(appName));

			if (response instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)response;

				if (_isSuccess(jsonObject)) {
					System.out.println("installApplication: Sucess.\n\n");
				}
				else {
					if (_isError(jsonObject)) {
						return jsonObject.getString("error");
					}
					else {
						return "installApplication error " + _getDeployURI(appName);
					}
				}
			}

			httpPost.releaseConnection();
		}
		catch (Exception e) {
			e.printStackTrace();

			return e.getMessage();
		}

		return null;
	}

	public boolean isAlive() throws APIException {
		JSONObject status = null;

		Object jsonResponse = getJSONAPI(_getIsAliveAPI());

		if (jsonResponse instanceof JSONObject) {
			status = (JSONObject)jsonResponse;
		}
		else {
			throw new APIException(_getIsAliveAPI(), "Unable to connect to server manager.");
		}

		if (_isSuccess(status)) {
			return true;
		}

		return false;
	}

	public boolean isAppInstalled(String appName) throws APIException {
		Object response = getJSONAPI(_getPluginURI(appName));

		if (response instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)response;

			try {
				if (_isSuccess(jsonObject)) {
					JSONObject output = _getJSONOutput(jsonObject);

					return output.getBoolean("installed");
				}
			}
			catch (Exception e) {
				throw new APIException(_getPluginURI(appName), e);
			}
		}

		return false;
	}

	public boolean isLiferayPluginStarted(String appName) throws APIException {
		Object response = getJSONAPI(_getPluginURI(appName));

		if (response instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)response;

			try {
				if (_isSuccess(jsonObject)) {
					JSONObject jsonOutput = _getJSONOutput(jsonObject);

					boolean installed = jsonOutput.getBoolean("started");

					if (installed) {
						return true;
					}
				}
			}
			catch (Exception e) {
				throw new APIException(_getPluginURI(appName), e);
			}
		}

		return false;
	}

	public void setManagerContextPath(String managerContextPath) {
		_managerContextPath = managerContextPath;
	}

	public Object uninstallApplication(String appName, IProgressMonitor monitor) throws APIException {
		Object response = deleteJSONAPI(_getUndeployURI(appName));

		if (response instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)response;

			try {
				if (_isSuccess(jsonObject)) {
					System.out.println("uninstallApplication: success\n\n");
				}
				else {
					if (_isError(jsonObject)) {
						return jsonObject.getString("error");
					}
					else {
						return "uninstallApplication error " + _getDeployURI(appName);
					}
				}
			}
			catch (Exception e) {
				throw new APIException(_getUndeployURI(appName), e);
			}
		}

		return null;
	}

	public Object updateApplication(String appName, String absolutePath, IProgressMonitor monitor) throws APIException {
		try {
			File file = new File(absolutePath);

			FileBody fileBody = new FileBody(file);

			MultipartEntity entity = new MultipartEntity();

			entity.addPart(file.getName(), fileBody);

			HttpPut httpPut = new HttpPut();

			httpPut.setEntity(entity);

			Object response = httpJSONAPI(httpPut, _getUpdateURI(appName));

			if (response instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)response;

				if (_isSuccess(jsonObject)) {
					System.out.println("updateApplication: success.\n\n");
				}
				else {
					if (_isError(jsonObject)) {
						return jsonObject.getString("error");
					}
					else {
						return "updateApplication error " + _getDeployURI(appName);
					}
				}
			}

			httpPut.releaseConnection();
		}
		catch (Exception e) {
			e.printStackTrace();

			return e.getMessage();
		}

		return null;
	}

	private String _getDebugPortAPI() {
		return _managerContextPath + "/server/debug-port";
	}

	private String _getDeployURI(String appName) {
		return _getPluginsAPI() + "/" + appName;
	}

	private String _getFMDebugPasswordAPI() {
		return _managerContextPath + "/server/freemarker/debug-password";
	}

	private String _getFMDebugPortAPI() {
		return _managerContextPath + "/server/freemarker/debug-port";
	}

	private String _getIsAliveAPI() {
		return _managerContextPath + "/status";
	}

	@SuppressWarnings("unchecked")
	private <T> T _getJSONOutput(JSONObject jsonObject) throws JSONException {
		if (jsonObject.has("output")) {
			return (T)jsonObject.get("output");
		}
		else {
			return null;
		}
	}

	private String _getPluginsAPI() {
		return _managerContextPath + "/plugins";
	}

	private String _getPluginURI(String appName) {
		return _getPluginsAPI() + "/" + appName;
	}

	private String _getUndeployURI(String appName) {
		return _getDeployURI(appName);
	}

	private String _getUpdateURI(String appName) {
		return _getDeployURI(appName);
	}

	private boolean _isError(JSONObject jsonObject) {
		try {
			String error = jsonObject.getString("error");

			return !CoreUtil.isNullOrEmpty(error);
		}
		catch (JSONException jsone) {
		}

		return false;
	}

	private boolean _isSuccess(JSONObject jsonObject) {
		try {
			if (jsonObject.getInt("status") == 0) {
				return true;
			}

			return false;
		}
		catch (JSONException jsone) {
		}

		return false;
	}

	private String _managerContextPath;

}