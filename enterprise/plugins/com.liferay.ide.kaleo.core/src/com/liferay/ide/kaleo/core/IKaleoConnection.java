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

import com.liferay.ide.core.remote.IRemoteConnection;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface IKaleoConnection extends IRemoteConnection {

	public JSONObject addKaleoDraftDefinition(
			String name, String titleMap, String definitionContent, int version, int draftVersion, long userId,
			long groupId)
		throws KaleoAPIException;

	public JSONObject getCompanyIdByVirtualHost() throws KaleoAPIException;

	public String getHost();

	public JSONArray getKaleoDefinitions() throws KaleoAPIException;

	public JSONArray getKaleoDraftWorkflowDefinitions() throws KaleoAPIException;

	public JSONObject getLatestKaleoDraftDefinition(String name, int version, long companyId) throws KaleoAPIException;

	public String getPortalLocale(long userId) throws KaleoAPIException;

	public JSONObject getPortalUserById(long userId) throws KaleoAPIException;

	public JSONObject getUserByEmailAddress() throws KaleoAPIException;

	public void publishKaleoDraftDefinition(
			String name, String titleMap, String content, String companyId, String userId, String groupId)
		throws KaleoAPIException;

	public void setHost(String host);

	public void setHttpPort(String httpPort);

	public void setPassword(String password);

	public void setPortalContextPath(String path);

	public void setPortalHtmlUrl(URL portalHomeUrl);

	public void setUsername(String username);

	public JSONObject updateKaleoDraftDefinition(
			String name, String titleMap, String content, int version, int draftVersion, long companyId, long userId)
		throws KaleoAPIException;

	public final String KALEODRAFTDEFINITION = "/kaleodesigner.kaleodraftdefinition";

	public final String KALEODEFINITION = "/kaleo.kaleodefinition";

	public final String USER = "/user";

	public final String ADD_KALEO_DRAFT_DEFINITION_API = KALEODRAFTDEFINITION + "/add-kaleo-draft-definition";

	public final String ADD_WORKFLOW_DEFINITION_KALEO_DRAFT_DEFINITION_API =
		KALEODRAFTDEFINITION + "/add-workflow-definition-kaleo-draft-definition";

	public final String GET_KALEO_DEFINITIONS_API = KALEODEFINITION + "/get-kaleo-definitions";

	public final String GET_KALEO_DRAFT_DEFINITIONS_API = KALEODRAFTDEFINITION + "/get-kaleo-draft-definitions";

	public final String GET_LATEST_KALEO_DRAFT_DEFINITION_API =
		KALEODRAFTDEFINITION + "/get-latest-kaleo-draft-definition";

	public final String GET_PORTAL_USER_API = USER + "/get-user-by-id";

	public final String GET_USER_BY_EMAIL_ADDRESS_API = USER + "/get-user-by-email-address";

	public final String PUBLISH_KALEO_DRAFT_DEFINITION_API = KALEODRAFTDEFINITION + "/publish-kaleo-draft-definition";

	public final String UPDATE_KALEO_DRAFT_DEFINITION = KALEODRAFTDEFINITION + "/update-kaleo-draft-definition";

}