/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.APIException;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Gregory Amerson
 */
public interface IPortalConnection
{
    String _API = "/api/jsonws";
    String _COMPANY = "/company";
    String _JOURNALARTICLE = "/journalarticle";
    String _JOURNALSTRUCTURE = "/journalstructure";
    String _JOURNALTEMPLATE = "/journaltemplate";
    String _GROUP = "/group";
    String _USER = "/user";

    String GET_ARTICLES_BY_USER_ID_API = _API + _JOURNALARTICLE + "/get-articles-by-user-id";
    String GET_COMPANY_BY_VIRTUAL_HOST_API = _API + _COMPANY + "/get-company-by-virtual-host";
    String GET_JOURNAL_ARTICLES_API = _API + _JOURNALARTICLE + "/get-articles-by-user-id";
    String GET_STRUCTURES_API = _API + _JOURNALSTRUCTURE + "/get-structures";
    String GET_STRUCTURE_TEMPLATES_API = _API + _JOURNALTEMPLATE + "/get-structure-templates";
    String GET_USER_BY_EMAIL_ADDRESS_API = _API + _USER + "/get-user-by-email-address";
    String GET_USER_SITES_API = _API + _GROUP + "/get-user-sites";
    
    JSONObject getCompanyIdByVirtualHost() throws APIException;
    
    JSONArray getStructures( long groupId ) throws APIException;
    
    JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException;

    JSONObject getUserByEmailAddress( long companyId ) throws APIException;

    JSONArray getUserSites() throws APIException;

    void setHost( String host );

    void setHttpPort( String httpPort );

    void setPassword( String password );
    
    void setPortalHtmlUrl( URL portalHomeUrl );

    void setUsername( String username );

    JSONArray getJournalArticles( long groupId, long userId ) throws APIException;

}
