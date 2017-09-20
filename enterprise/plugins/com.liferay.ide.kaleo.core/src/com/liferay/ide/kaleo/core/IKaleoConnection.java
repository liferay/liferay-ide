/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
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
public interface IKaleoConnection extends IRemoteConnection
{

    final String _KALEODEFINITION = "/kaleo.kaleodefinition";
    final String _KALEODRAFTDEFINITION = "/kaleodesigner.kaleodraftdefinition";
    final String _USER = "/user";

    final String ADD_KALEO_DRAFT_DEFINITION_API = _KALEODRAFTDEFINITION + "/add-kaleo-draft-definition";
    final String ADD_WORKFLOW_DEFINITION_KALEO_DRAFT_DEFINITION_API = _KALEODRAFTDEFINITION +
        "/add-workflow-definition-kaleo-draft-definition";
    final String GET_KALEO_DEFINITIONS_API = _KALEODEFINITION + "/get-kaleo-definitions";
    final String GET_PORTAL_USER_API = _USER + "/get-user-by-id";
    final String GET_KALEO_DRAFT_DEFINITIONS_API = _KALEODRAFTDEFINITION + "/get-kaleo-draft-definitions";
    final String GET_LATEST_KALEO_DRAFT_DEFINITION_API = _KALEODRAFTDEFINITION + "/get-latest-kaleo-draft-definition";
    final String GET_USER_BY_EMAIL_ADDRESS_API = _USER + "/get-user-by-email-address";
    final String PUBLISH_KALEO_DRAFT_DEFINITION_API = _KALEODRAFTDEFINITION + "/publish-kaleo-draft-definition";
    final String UPDATE_KALEO_DRAFT_DEFINITION = _KALEODRAFTDEFINITION + "/update-kaleo-draft-definition";

    JSONObject addKaleoDraftDefinition(
        String name, String titleMap, String definitionContent, int version, int draftVersion, long userId, long groupId )
        throws KaleoAPIException;

    JSONObject getCompanyIdByVirtualHost() throws KaleoAPIException;

    String getHost();

    JSONArray getKaleoDefinitions() throws KaleoAPIException;

    JSONArray getKaleoDraftWorkflowDefinitions() throws KaleoAPIException;

    JSONObject getLatestKaleoDraftDefinition( String name, int version, long companyId ) throws KaleoAPIException;

    String getPortalLocale( long userId ) throws KaleoAPIException;

    JSONObject getPortalUserById( long userId ) throws KaleoAPIException;

    JSONObject getUserByEmailAddress() throws KaleoAPIException;

    void publishKaleoDraftDefinition( String name, String titleMap, String content, String companyId, String userId, String groupId )
        throws KaleoAPIException;

    void setHost( String host );

    void setHttpPort( int httpPort );

    void setPassword( String password );

    void setPortalHtmlUrl( URL portalHomeUrl );

    void setPortalContextPath( String path );

    void setUsername( String username );

    JSONObject updateKaleoDraftDefinition(
        String name, String titleMap, String content, int version, int draftVersion, long companyId, long userId )
        throws KaleoAPIException;

}
