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
public class KaleoConnection extends RemoteConnection implements IKaleoConnection
{

    private URL portalHomeUrl;
    private String serverName;
    private String portalContextPath;

    public JSONObject addKaleoDraftDefinition(
        String name, String titleMap, String definitionContent, int version, int draftVersion, long userId, long groupId )
        throws KaleoAPIException
    {
        JSONObject newKaleoDraftDefinition = null;

        HttpPost post = new HttpPost();

        try
        {
            Object response = httpJSONAPI
            (
                post,
                new Object[]
                {
                    addKaleoDraftDefinitionAPI(),
                    "name", name,
                    "groupId", groupId,
                    "titleMap", titleMap,
                    "content", definitionContent,
                    "version", version,
                    "draftVersion", draftVersion,
                    "userId", userId,
                    "serviceContext.userId", userId,
                }
            );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                JSONObject responseObject = (JSONObject) response;

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    throw new KaleoAPIException( addKaleoDraftDefinitionAPI(), responseObject.getString( "exception" ) );
                }

                newKaleoDraftDefinition = responseObject;
            }
        }
        catch( Exception e )
        {
            throw new KaleoAPIException( addKaleoDraftDefinitionAPI(), e );
        }

        return newKaleoDraftDefinition;
    }

    protected String addKaleoDraftDefinitionAPI()
    {
        return getKaleoDesignerAPIUrl() + ADD_KALEO_DRAFT_DEFINITION_API;
    }

    protected String addWorkflowDefinitionKaleoDraftDefinitionAPI()
    {
        return getKaleoDesignerAPIUrl() + ADD_WORKFLOW_DEFINITION_KALEO_DRAFT_DEFINITION_API;
    }

    private String getPortalContextPath()
    {
        return portalContextPath;
    }

    private String getCompanyByVirtualHostAPI()
    {
        return getPortalContextPath() + "/api/jsonws/company/get-company-by-virtual-host";
    }

    public JSONObject getCompanyIdByVirtualHost() throws KaleoAPIException
    {
        JSONObject company = null;
        Exception apiException = null;

        try
        {
            Object response = getJSONAPI( new Object[] { getCompanyByVirtualHostAPI(),"virtualHost", getHost() } );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                company = (JSONObject) response;
            }
        }
        catch( Exception e )
        {
            apiException = new KaleoAPIException( getKaleoDefinitionsAPI(), e );
        }

        if( company == null )
        {
            throw new KaleoAPIException( getCompanyByVirtualHostAPI(), apiException );
        }

        return company;
    }

    public JSONArray getKaleoDefinitions() throws KaleoAPIException
    {
        JSONArray definitions = null;
        Exception apiException = null;
        Object response = null;

        try
        {
            response = getJSONAPI( getKaleoDefinitionsAPI() + "/start/-1/end/-1" );

            // kaleoDefinitionsAPI.getParams().setParameter( "start", new Integer( -1 ) );
            // kaleoDefinitionsAPI.getParams().setParameter( "end", new Integer( -1 ) );

            if( ( response != null ) && ( response instanceof JSONArray ) )
            {
                definitions = (JSONArray) response;
            }
        }
        catch( Exception e )
        {
            apiException = new KaleoAPIException( getKaleoDefinitionsAPI(), e );
        }

        if( definitions == null )
        {
            throw new KaleoAPIException( getKaleoDefinitionsAPI(), apiException );
        }

        return definitions;
    }

    protected String getKaleoDefinitionsAPI()
    {
        return getKaleoWebAPIUrl() + GET_KALEO_DEFINITIONS_API;
    }

    private String getKaleoDesignerAPIUrl()
    {
        return "/api/jsonws";
    }

    protected String getKaleoDraftDefinitionsAPI()
    {
        return getKaleoDesignerAPIUrl() + GET_KALEO_DRAFT_DEFINITIONS_API;
    }

    public JSONArray getKaleoDraftWorkflowDefinitions() throws KaleoAPIException
    {
        JSONArray definitions = null;
        Exception apiException = null;
        Object response = null;

        try
        {
            response = getJSONAPI( getKaleoDraftDefinitionsAPI() );

            if( ( response != null ) && ( response instanceof JSONArray ) )
            {
                definitions = (JSONArray) response;
            }
        }
        catch( Exception e )
        {
            apiException = new KaleoAPIException( getKaleoDraftDefinitionsAPI(), e );
        }

        if( definitions == null )
        {
            throw new KaleoAPIException( getKaleoDraftDefinitionsAPI(), apiException );
        }

        return definitions;
    }

    private String getKaleoWebAPIUrl()
    {
        return "/api/jsonws";
    }

	public JSONObject getLatestKaleoDraftDefinition( String name, int version, long companyId )
        throws KaleoAPIException
    {
        JSONObject latestDraftDefinition = null;
        Exception apiException = null;

        try
        {
            Object response = getJSONAPI
            (
                new Object[]
                {
                    getLatestKaleoDraftDefinitionAPI(),
                    "name", name,
                    "version", version,
                    "serviceContext.companyId", companyId
                }
            );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                JSONObject responseObject = (JSONObject) response;

                if( responseObject != null && responseObject.has( "exception" ) )
                {
//                    throw new KaleoAPIException( addWorkflowDefinitionKaleoDraftDefinitionAPI(), responseObject.getString( "exception" ) );
                    latestDraftDefinition = null;
                }
                else {
                    latestDraftDefinition = responseObject;
                }
            }
        }
        catch( Exception e )
        {
            apiException = new KaleoAPIException( getLatestKaleoDraftDefinitionAPI(), e );
        }

        if (apiException != null)
        {
            throw new KaleoAPIException( getLatestKaleoDraftDefinitionAPI(), apiException );
        }
//        if( latestDraftDefinition == null )
//        {
//            throw new KaleoAPIException( getLatestKaleoDraftDefinitionAPI(), apiException );
//        }

        return latestDraftDefinition;
    }

    protected String getLatestKaleoDraftDefinitionAPI()
    {
        return getKaleoDesignerAPIUrl() + GET_LATEST_KALEO_DRAFT_DEFINITION_API;
    }

    private String getPortalAPIUrl()
    {
        return "/api/jsonws";
    }

    public URL getPortalHtmlUrl()
    {
        return this.portalHomeUrl;
    }

    public String getPortalLocale( long userId ) throws KaleoAPIException
    {
        JSONObject user = getPortalUserById( userId );

        String portalDefaultLocale = Locale.getDefault().toString();

        if( user != null )
        {
            //STUDIO-393
            try
            {
                portalDefaultLocale = user.getString( "languageId" );
            }
            catch( JSONException e )
            {
            }
        }

        return portalDefaultLocale;
    }

    protected String getPortalUserAPI()
    {
        return getPortalAPIUrl() + GET_PORTAL_USER_API;
    }

    public JSONObject getPortalUserById( long userId ) throws KaleoAPIException
    {
        JSONObject user = null;

        HttpPost post = new HttpPost();

        try
        {
            Object response = httpJSONAPI( post, new Object[] { getPortalUserAPI(), "userId", userId } );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                JSONObject responseObject = (JSONObject) response;

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    throw new KaleoAPIException( addKaleoDraftDefinitionAPI(), responseObject.getString( "exception" ) );
                }

                user = responseObject;
            }
        }
        catch( Exception e )
        {
            throw new KaleoAPIException( addKaleoDraftDefinitionAPI(), e );
        }

        return user;
    }

    public String getServerName()
    {
        return serverName;
    }

    @Override
    public JSONObject getUserByEmailAddress() throws KaleoAPIException
    {
        JSONObject user = null;
        Exception apiException = null;

        try
        {
            Object response = getJSONAPI(
                new Object[] { getUserByEmailAddressAPI(), "companyId",
                    getCompanyIdByVirtualHost().getLong( "companyId" ), "emailAddress", getUsername() } );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                user = (JSONObject) response;
            }
        }
        catch( Exception e )
        {
            apiException = new KaleoAPIException( getUserByEmailAddressAPI(), e );
        }

        if( user == null )
        {
            throw new KaleoAPIException( getUserByEmailAddressAPI(), apiException );
        }

        return user;
    }

    public String getUserByEmailAddressAPI()
    {
        return getPortalAPIUrl() + GET_USER_BY_EMAIL_ADDRESS_API;
    }

    public void publishKaleoDraftDefinition(
        String name, String titleMap, String content, String companyId, String userId, String groupId ) throws KaleoAPIException
    {
        try
        {
            HttpPost httpPost = new HttpPost();

            Object response =httpJSONAPI
            (
                httpPost,
                new Object[]
                {
                    publishKaleoDraftDefinitionAPI(),
                    "name", name,
                    "titleMap", titleMap,
                    "content", content,
                    "userId", userId,
                    "groupId", groupId,
                    "serviceContext.companyId", companyId,
                    "serviceContext.userId", userId
                }
            );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                JSONObject responseObject = (JSONObject) response;

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    throw new KaleoAPIException(
                        publishKaleoDraftDefinitionAPI(), responseObject.getString( "exception" ) );
                }
            }
        }
        catch( Exception e )
        {
            throw new KaleoAPIException( publishKaleoDraftDefinitionAPI(), e );
        }
    }

    protected String publishKaleoDraftDefinitionAPI()
    {
        return getKaleoDesignerAPIUrl() + PUBLISH_KALEO_DRAFT_DEFINITION_API;
    }

    public void setPortalHtmlUrl( URL portalHomeUrl )
    {
        this.portalHomeUrl = portalHomeUrl;
    }

    public void setPortalContextPath( String portalContextPath )
    {
        this.portalContextPath = portalContextPath;
    }

    public void setServerName( String serverName )
    {
        this.serverName = serverName;
    }

    public JSONObject updateKaleoDraftDefinition(
        String name, String titleMap, String content, int version, int draftVersion, long companyId, long userId )
        throws KaleoAPIException
    {
        JSONObject updatedKaleoDraftDefinition = null;

        try
        {
            HttpPost post = new HttpPost();

            Object response = httpJSONAPI
            (
                post,
                new Object[]
                {
                    updateKaleoDraftDefinitionAPI(),
                    "name", name,
                    "titleMap", titleMap,
                    "content", content,
                    "version", version,
                    "draftVersion", draftVersion,
                    "userId", userId,
                    "serviceContext.companyId", companyId,
                    "serviceContext.userId", userId,
                }
            );

            if( ( response != null ) && ( response instanceof JSONObject ) )
            {
                JSONObject responseObject = (JSONObject) response;

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    throw new KaleoAPIException( addWorkflowDefinitionKaleoDraftDefinitionAPI(), responseObject.getString( "exception" ) );
                }

                updatedKaleoDraftDefinition = responseObject;
            }
        }
        catch( Exception e )
        {
            throw new KaleoAPIException( publishKaleoDraftDefinitionAPI(), e );
        }

        return updatedKaleoDraftDefinition;
    }

    protected String updateKaleoDraftDefinitionAPI()
    {
        return getKaleoDesignerAPIUrl() + UPDATE_KALEO_DRAFT_DEFINITION;
    }
}
