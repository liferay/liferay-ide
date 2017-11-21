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

package com.liferay.ide.kaleo.core;

import com.liferay.ide.core.remote.RemoteConnection;

import java.net.URL;

import java.util.Locale;

import org.apache.http.client.methods.HttpPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class KaleoConnection extends RemoteConnection implements IKaleoConnection {

	public JSONObject addKaleoDraftDefinition(
			String name, String titleMap, String definitionContent, int version, int draftVersion, long userId,
			long groupId)
		throws KaleoAPIException {

		JSONObject newKaleoDraftDefinition = null;

		HttpPost post = new HttpPost();

		try {
			Object objects = new Object[] {
				addKaleoDraftDefinitionAPI(), "name", name, "groupId", groupId, "titleMap", titleMap, "content",
				definitionContent, "version", version, "draftVersion", draftVersion, "userId", userId,
				"serviceContext.userId", userId
			};

			Object response = httpJSONAPI(post, objects);

			if ((response != null) && (response instanceof JSONObject)) {
				JSONObject responseObject = (JSONObject)response;

				if ((responseObject != null) && responseObject.has("exception")) {
					throw new KaleoAPIException(addKaleoDraftDefinitionAPI(), responseObject.getString("exception"));
				}

				newKaleoDraftDefinition = responseObject;
			}
		}
		catch (Exception e) {
			throw new KaleoAPIException(addKaleoDraftDefinitionAPI(), e);
		}

		return newKaleoDraftDefinition;
	}

	public JSONObject getCompanyIdByVirtualHost() throws KaleoAPIException {
		JSONObject company = null;
		Exception apiException = null;

		try {
			Object[] objects = {_getCompanyByVirtualHostAPI(), "virtualHost", getHost()};

			Object response = getJSONAPI(objects);

			if ((response != null) && (response instanceof JSONObject)) {
				company = (JSONObject)response;
			}
		}
		catch (Exception e) {
			apiException = new KaleoAPIException(getKaleoDefinitionsAPI(), e);
		}

		if (company == null) {
			throw new KaleoAPIException(_getCompanyByVirtualHostAPI(), apiException);
		}

		return company;
	}

	public JSONArray getKaleoDefinitions() throws KaleoAPIException {
		JSONArray definitions = null;
		Exception apiException = null;
		Object response = null;

		try {
			response = getJSONAPI(getKaleoDefinitionsAPI() + "/start/-1/end/-1");

			if ((response != null) && (response instanceof JSONArray)) {
				definitions = (JSONArray)response;
			}
		}
		catch (Exception e) {
			apiException = new KaleoAPIException(getKaleoDefinitionsAPI(), e);
		}

		if (definitions == null) {
			throw new KaleoAPIException(getKaleoDefinitionsAPI(), apiException);
		}

		return definitions;
	}

	public JSONArray getKaleoDraftWorkflowDefinitions() throws KaleoAPIException {
		JSONArray definitions = null;
		Exception apiException = null;
		Object response = null;

		try {
			response = getJSONAPI(getKaleoDraftDefinitionsAPI());

			if ((response != null) && (response instanceof JSONArray)) {
				definitions = (JSONArray)response;
			}
		}
		catch (Exception e) {
			apiException = new KaleoAPIException(getKaleoDraftDefinitionsAPI(), e);
		}

		if (definitions == null) {
			throw new KaleoAPIException(getKaleoDraftDefinitionsAPI(), apiException);
		}

		return definitions;
	}

	public JSONObject getLatestKaleoDraftDefinition(String name, int version, long companyId) throws KaleoAPIException {
		JSONObject latestDraftDefinition = null;
		Exception apiException = null;

		try {
			Object[] objects = {
				getLatestKaleoDraftDefinitionAPI(), "name", name, "version", version, "serviceContext.companyId",
				companyId
			};

			Object response = getJSONAPI(objects);

			if ((response != null) && (response instanceof JSONObject)) {
				JSONObject responseObject = (JSONObject)response;

				if ((responseObject != null) && responseObject.has("exception")) {
					/*
					 * throw new KaleoAPIException(
					 * addWorkflowDefinitionKaleoDraftDefinitionAPI(),
					 * responseObject.getString( "exception" ) );
					 */
					latestDraftDefinition = null;
				}
				else {
					latestDraftDefinition = responseObject;
				}
			}
		}
		catch (Exception e) {
			apiException = new KaleoAPIException(getLatestKaleoDraftDefinitionAPI(), e);
		}

		if (apiException != null) {
			throw new KaleoAPIException(getLatestKaleoDraftDefinitionAPI(), apiException);
		}
		/*
		 * if( latestDraftDefinition == null ) { throw new KaleoAPIException(
		 * getLatestKaleoDraftDefinitionAPI(), apiException ); }
		 */
		return latestDraftDefinition;
	}

	public URL getPortalHtmlUrl() {
		return _portalHomeUrl;
	}

	public String getPortalLocale(long userId) throws KaleoAPIException {
		JSONObject user = getPortalUserById(userId);

		Locale defaultLocal = Locale.getDefault();

		String portalDefaultLocale = defaultLocal.toString();

		if (user != null) {
			try {
				portalDefaultLocale = user.getString("languageId");
			}
			catch (JSONException jsone) {
			}
		}

		return portalDefaultLocale;
	}

	public JSONObject getPortalUserById(long userId) throws KaleoAPIException {
		JSONObject user = null;

		HttpPost post = new HttpPost();

		try {
			Object objects = new Object[] {getPortalUserAPI(), "userId", userId};

			Object response = httpJSONAPI(post, objects);

			if ((response != null) && (response instanceof JSONObject)) {
				JSONObject responseObject = (JSONObject)response;

				if ((responseObject != null) && responseObject.has("exception")) {
					throw new KaleoAPIException(addKaleoDraftDefinitionAPI(), responseObject.getString("exception"));
				}

				user = responseObject;
			}
		}
		catch (Exception e) {
			throw new KaleoAPIException(addKaleoDraftDefinitionAPI(), e);
		}

		return user;
	}

	public String getServerName() {
		return _serverName;
	}

	@Override
	public JSONObject getUserByEmailAddress() throws KaleoAPIException {
		JSONObject user = null;
		Exception apiException = null;

		try {
			Object[] objects = {
				getUserByEmailAddressAPI(), "companyId", getCompanyIdByVirtualHost().getLong("companyId"),
				"emailAddress", getUsername()
			};

			Object response = getJSONAPI(objects);

			if ((response != null) && (response instanceof JSONObject)) {
				user = (JSONObject)response;
			}
		}
		catch (Exception e) {
			apiException = new KaleoAPIException(getUserByEmailAddressAPI(), e);
		}

		if (user == null) {
			throw new KaleoAPIException(getUserByEmailAddressAPI(), apiException);
		}

		return user;
	}

	public String getUserByEmailAddressAPI() {
		return _getPortalAPIUrl() + GET_USER_BY_EMAIL_ADDRESS_API;
	}

	public void publishKaleoDraftDefinition(
			String name, String titleMap, String content, String companyId, String userId, String groupId)
		throws KaleoAPIException {

		try {
			HttpPost httpPost = new HttpPost();

			Object objects = new Object[] {
				publishKaleoDraftDefinitionAPI(), "name", name, "titleMap", titleMap, "content", content, "userId",
				userId, "groupId", groupId, "serviceContext.companyId", companyId, "serviceContext.userId", userId
			};

			Object response = httpJSONAPI(httpPost, objects);

			if ((response != null) && (response instanceof JSONObject)) {
				JSONObject responseObject = (JSONObject)response;

				if ((responseObject != null) && responseObject.has("exception")) {
					throw new KaleoAPIException(
						publishKaleoDraftDefinitionAPI(), responseObject.getString("exception"));
				}
			}
		}
		catch (Exception e) {
			throw new KaleoAPIException(publishKaleoDraftDefinitionAPI(), e);
		}
	}

	public void setPortalContextPath(String portalContextPath) {
		_portalContextPath = portalContextPath;
	}

	public void setPortalHtmlUrl(URL portalHomeUrl) {
		_portalHomeUrl = portalHomeUrl;
	}

	public void setServerName(String serverName) {
		_serverName = serverName;
	}

	public JSONObject updateKaleoDraftDefinition(
			String name, String titleMap, String content, int version, int draftVersion, long companyId, long userId)
		throws KaleoAPIException {

		JSONObject updatedKaleoDraftDefinition = null;

		try {
			HttpPost post = new HttpPost();

			Object objects = new Object[] {
				updateKaleoDraftDefinitionAPI(), "name", name, "titleMap", titleMap, "content", content, "version",
				version, "draftVersion", draftVersion, "userId", userId, "serviceContext.companyId", companyId,
				"serviceContext.userId", userId
			};

			Object response = httpJSONAPI(post, objects);

			if ((response != null) && (response instanceof JSONObject)) {
				JSONObject responseObject = (JSONObject)response;

				if ((responseObject != null) && responseObject.has("exception")) {
					throw new KaleoAPIException(
						addWorkflowDefinitionKaleoDraftDefinitionAPI(), responseObject.getString("exception"));
				}

				updatedKaleoDraftDefinition = responseObject;
			}
		}
		catch (Exception e) {
			throw new KaleoAPIException(publishKaleoDraftDefinitionAPI(), e);
		}

		return updatedKaleoDraftDefinition;
	}

	protected String addKaleoDraftDefinitionAPI() {
		return _getKaleoDesignerAPIUrl() + ADD_KALEO_DRAFT_DEFINITION_API;
	}

	protected String addWorkflowDefinitionKaleoDraftDefinitionAPI() {
		return _getKaleoDesignerAPIUrl() + ADD_WORKFLOW_DEFINITION_KALEO_DRAFT_DEFINITION_API;
	}

	protected String getKaleoDefinitionsAPI() {
		return _getKaleoWebAPIUrl() + GET_KALEO_DEFINITIONS_API;
	}

	protected String getKaleoDraftDefinitionsAPI() {
		return _getKaleoDesignerAPIUrl() + GET_KALEO_DRAFT_DEFINITIONS_API;
	}

	protected String getLatestKaleoDraftDefinitionAPI() {
		return _getKaleoDesignerAPIUrl() + GET_LATEST_KALEO_DRAFT_DEFINITION_API;
	}

	protected String getPortalUserAPI() {
		return _getPortalAPIUrl() + GET_PORTAL_USER_API;
	}

	protected String publishKaleoDraftDefinitionAPI() {
		return _getKaleoDesignerAPIUrl() + PUBLISH_KALEO_DRAFT_DEFINITION_API;
	}

	protected String updateKaleoDraftDefinitionAPI() {
		return _getKaleoDesignerAPIUrl() + UPDATE_KALEO_DRAFT_DEFINITION;
	}

	private String _getCompanyByVirtualHostAPI() {
		return _getPortalContextPath() + "/api/jsonws/company/get-company-by-virtual-host";
	}

	private String _getKaleoDesignerAPIUrl() {
		return "/api/jsonws";
	}

	private String _getKaleoWebAPIUrl() {
		return "/api/jsonws";
	}

	private String _getPortalAPIUrl() {
		return "/api/jsonws";
	}

	private String _getPortalContextPath() {
		return _portalContextPath;
	}

	private String _portalContextPath;
	private URL _portalHomeUrl;
	private String _serverName;

}