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
import com.liferay.ide.core.remote.IRemoteConnection;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Gregory Amerson
 */
public interface IPortalConnection extends IRemoteConnection
{
    String _COMPANY = "/company"; //$NON-NLS-1$
    String _GROUP = "/group"; //$NON-NLS-1$
    String _JOURNALARTICLE = "/journalarticle"; //$NON-NLS-1$
    String _JOURNALSTRUCTURE = "/journalstructure"; //$NON-NLS-1$
    String _JOURNALTEMPLATE = "/journaltemplate"; //$NON-NLS-1$
    String _USER = "/user"; //$NON-NLS-1$

    String GET_ARTICLES_BY_USER_ID_API = _API + _JOURNALARTICLE + "/get-articles-by-user-id"; //$NON-NLS-1$
    String GET_COMPANY_BY_VIRTUAL_HOST_API = _API + _COMPANY + "/get-company-by-virtual-host"; //$NON-NLS-1$
    String GET_JOURNAL_ARTICLES_API = _API + _JOURNALARTICLE + "/get-articles-by-user-id"; //$NON-NLS-1$
    String GET_STRUCTURE_TEMPLATES_API = _API + _JOURNALTEMPLATE + "/get-structure-templates"; //$NON-NLS-1$
    String GET_STRUCTURES_API = _API + _JOURNALSTRUCTURE + "/get-structures"; //$NON-NLS-1$
    String GET_USER_BY_EMAIL_ADDRESS_API = _API + _USER + "/get-user-by-email-address"; //$NON-NLS-1$
    String GET_USER_SITES_API = _API + _GROUP + "/get-user-sites"; //$NON-NLS-1$
    
    JSONObject getCompanyIdByVirtualHost() throws APIException;
    
    JSONArray getJournalArticles( long groupId, long userId ) throws APIException;
    
    JSONArray getStructures( long groupId ) throws APIException;

    JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException;

    JSONObject getUserByEmailAddress( long companyId ) throws APIException;

    JSONArray getUserSites() throws APIException;

}
