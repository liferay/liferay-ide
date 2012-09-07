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
    final String _API = "/api/jsonws";
    final String _COMPANY = "/company";
    final String _GROUP = "/group";
    final String _USER = "/user";

    final String GET_COMPANY_BY_VIRTUAL_HOST_API = _API + _COMPANY + "/get-company-by-virtual-host";
    final String GET_USER_BY_EMAIL_ADDRESS_API = _API + _USER + "/get-user-by-email-address";
    final String GET_USER_SITES_API = _API + _GROUP + "/get-user-sites";
    
    JSONObject getCompanyIdByVirtualHost() throws APIException;
    
    void setHost( String host );

    void setHttpPort( String httpPort );

    void setPassword( String password );

    void setPortalHtmlUrl( URL portalHomeUrl );

    void setUsername( String username );

    JSONObject getUserByEmailAddress( long companyId ) throws APIException;
    
    JSONArray getUserSites() throws APIException;
}
