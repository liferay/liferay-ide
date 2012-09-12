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

package com.liferay.ide.core.remote;

import com.liferay.ide.core.util.CoreUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class RemoteConnection implements IRemoteConnection
{

    private String host;
    private DefaultHttpClient httpClient;
    private BasicHttpContext httpContext;
    private int httpPort;
    private String password;
    private HttpHost targetHost;
    private String username;

    protected void clearFields()
    {
        this.httpClient = null;
        this.targetHost = null;
        this.httpContext = null;
    }

    protected Object deleteJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." );
        }

        HttpDelete deleteAPIMethod = new HttpDelete();

        return httpJSONAPI( deleteAPIMethod, args );
    }

    public String getHost()
    {
        return host;
    }

    private HttpClient getHttpClient()
    {
        if( httpClient == null )
        {
            httpClient = new DefaultHttpClient();

            if( getUsername() != null || getPassword() != null )
            {
                httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope( getHost(), getHttpPort() ),
                    new UsernamePasswordCredentials( getUsername(), getPassword() ) );
            }
        }

        return httpClient;
    }

    private BasicHttpContext getHttpContext()
    {
        if( httpContext == null )
        {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put( getHttpHost(), basicAuth );
            httpContext = new BasicHttpContext();
            httpContext.setAttribute( ClientContext.AUTH_CACHE, authCache );
        }

        return httpContext;
    }

    private HttpHost getHttpHost()
    {
        if( targetHost == null )
        {
            targetHost = new HttpHost( getHost(), getHttpPort(), "http" );
        }

        return targetHost;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    protected String getHttpResponse( HttpUriRequest request ) throws Exception
    {
        HttpResponse response = getHttpClient().execute( request, getHttpContext() );

        int statusCode = response.getStatusLine().getStatusCode();

        if( statusCode == HttpStatus.SC_OK )
        {
            HttpEntity entity = response.getEntity();

            String body = CoreUtil.readStreamToString( entity.getContent() );

            EntityUtils.consume( entity );

            return body;
        }

        return null;
    }

    protected Object getJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." );
        }

        HttpGet getAPIMethod = new HttpGet();

        return httpJSONAPI( getAPIMethod, args );
    }

    private Object getJSONResponse( String response )
    {
        Object retval = null;

        try
        {
            retval = new JSONObject( response );
        }
        catch( JSONException e )
        {
            try
            {
                retval = new JSONArray( response );
            }
            catch( JSONException e1 )
            {
            }
        }

        return retval;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }

    protected Object httpJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof HttpRequestBase ) )
        {
            throw new IllegalArgumentException( "First argument must be a HttpRequestBase." );
        }

        Object retval = null;

        HttpRequestBase request = (HttpRequestBase) args[0];

        String api = (String) args[1];

        try
        {
            URIBuilder builder = new URIBuilder();
            builder.setScheme( "http" );
            builder.setHost( getHost() );
            builder.setPort( getHttpPort() );
            builder.setPath( api );

            if( args.length >= 3 )
            {
                for( int i = 1; i < args.length; i += 2 )
                {
                    String name = null;
                    String value = "";

                    if( args[i] != null )
                    {
                        name = args[i].toString();
                    }

                    if( args[i + 1] != null )
                    {
                        value = args[i + 1].toString();
                    }

                    builder.setParameter( name, value );
                }
            }

            request.setURI( builder.build() );

            String response = getHttpResponse( request );

            if( response != null && response.length() > 0 )
            {
                Object jsonResponse = getJSONResponse( response );

                if( jsonResponse == null )
                {
                    throw new APIException( api, "Unable to get API." );
                }
                else
                {
                    retval = jsonResponse;
                }
            }
        }
        catch( Exception e )
        {
            throw new APIException( api, e );
        }

        return retval;
    }

    protected Object postJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." );
        }

        HttpPost post = new HttpPost();

        return httpJSONAPI( post, args );
    }

    public void setHost( String host )
    {
        this.host = host;
        clearFields();
    }

    public void setHttpPort( String httpPort )
    {
        this.httpPort = Integer.parseInt( httpPort );
        clearFields();
    }

    public void setPassword( String password )
    {
        this.password = password;
        clearFields();
    }

    public void setUsername( String username )
    {
        this.username = username;
        clearFields();
    }

}
