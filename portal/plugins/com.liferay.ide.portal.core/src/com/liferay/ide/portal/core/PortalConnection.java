/*******************************************************************************
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
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portal.core;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.RemoteConnection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class PortalConnection extends RemoteConnection implements IPortalConnection
{

    public JSONObject getCompanyIdByVirtualHost() throws APIException
    {
        JSONObject company = null;
        
        Object jsonResponse = getJSONAPI( GET_COMPANY_BY_VIRTUAL_HOST_API, "virtualHost", getHost() ); //$NON-NLS-1$
        
        if( jsonResponse instanceof JSONObject )
        {
            company = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( GET_COMPANY_BY_VIRTUAL_HOST_API, "Unable to get JSONObject" ); //$NON-NLS-1$
        }

        return company;
    }
    
    public JSONArray getJournalArticles( long groupId, long userId ) throws APIException
    {
        JSONArray journalArticles = null;
        
        Object jsonResponse = getJSONAPI( GET_JOURNAL_ARTICLES_API, "groupId", groupId, "userId", userId, "start", -1, "end", -1, "-obc", null ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        
        if( jsonResponse instanceof JSONArray )
        {
            journalArticles = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_JOURNAL_ARTICLES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return journalArticles;
    }

    public JSONArray getStructures( long groupId ) throws APIException
    {
        JSONArray structures = null;
        
        Object jsonResponse = getJSONAPI( GET_STRUCTURES_API, "groupId", groupId ); //$NON-NLS-1$
        
        if( jsonResponse instanceof JSONArray )
        {
            structures = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_STRUCTURES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return structures;
    }
    
    public JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException
    {
        JSONArray structureTemplates = null;
        
        Object jsonResponse = getJSONAPI( GET_STRUCTURE_TEMPLATES_API, "groupId", groupId, "structureId", structureId ); //$NON-NLS-1$ //$NON-NLS-2$
        
        if( jsonResponse instanceof JSONArray )
        {
            structureTemplates = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_STRUCTURE_TEMPLATES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }

        return structureTemplates;
    }

    public JSONObject getUserByEmailAddress(long companyId) throws APIException
    {
        JSONObject user = null;
        
        Object jsonResponse = getJSONAPI( GET_USER_BY_EMAIL_ADDRESS_API, "companyId", Long.toString( companyId), "emailAddress", getUsername() ); //$NON-NLS-1$ //$NON-NLS-2$
        
        if( jsonResponse instanceof JSONObject )
        {
            user = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( GET_USER_BY_EMAIL_ADDRESS_API, "Unable to get JSONObject" ); //$NON-NLS-1$
        }

        return user;
    }

    public JSONArray getUserSites() throws APIException
    {
        JSONArray sites = null;
        
        Object jsonResponse = getJSONAPI( GET_USER_SITES_API );
        
        if( jsonResponse instanceof JSONArray )
        {
            sites = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( GET_USER_SITES_API, "Unable to get JSONArray" ); //$NON-NLS-1$
        }
        
        return sites;
    }

}
