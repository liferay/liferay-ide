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
import com.liferay.ide.core.util.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class RemoteConnection implements IRemoteConnection
{

    private String hostname;
    private DefaultHttpClient httpClient;
    private int httpPort;
    private String password;
    private String username;

    protected Object deleteJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." ); //$NON-NLS-1$
        }

        HttpDelete deleteAPIMethod = new HttpDelete();

        return httpJSONAPI( deleteAPIMethod, args );
    }

    public String getHost()
    {
        return hostname;
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

    public int getHttpPort()
    {
        return httpPort;
    }

    protected String getHttpResponse( HttpUriRequest request ) throws Exception
    {
        HttpResponse response = getHttpClient().execute( request );

        int statusCode = response.getStatusLine().getStatusCode();

        if( statusCode == HttpStatus.SC_OK )
        {
            HttpEntity entity = response.getEntity();

            String body = CoreUtil.readStreamToString( entity.getContent() );

            EntityUtils.consume( entity );

            return body;
        }
        else
        {
            return response.getStatusLine().getReasonPhrase();
        }
    }

    protected Object getJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." ); //$NON-NLS-1$
        }

        HttpGet getAPIMethod = new HttpGet();

        return httpJSONAPI( getAPIMethod, args[0] );
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
            throw new IllegalArgumentException( "First argument must be a HttpRequestBase." ); //$NON-NLS-1$
        }

        Object retval = null;

        HttpRequestBase request = (HttpRequestBase) args[0];

        String api = (String) args[1];

        try
        {
            URIBuilder builder = new URIBuilder();
            builder.setScheme( "http" ); //$NON-NLS-1$
            builder.setHost( getHost() );
            builder.setPort( getHttpPort() );
            builder.setPath( api );

            if( args.length >= 3 )
            {
                for( int i = 1; i < args.length; i += 2 )
                {
                    String name = null;
                    String value = StringUtil.EMPTY;

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
                    throw new APIException( api, "Unable to get response: " + response ); //$NON-NLS-1$
                }
                else
                {
                    retval = jsonResponse;
                }
            }
        }
        catch( APIException e )
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new APIException( api, e );
        }
        finally
        {
            try
            {
                request.releaseConnection();
            }
            finally
            {
                // no need to log error
            }
        }

        return retval;
    }

    protected Object postJSONAPI( Object... args ) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException( "First argument must be a string." ); //$NON-NLS-1$
        }

        HttpPost post = new HttpPost();

        return httpJSONAPI( post, args );
    }

    public void setHost( String host )
    {
        this.hostname = host;
        this.httpClient = null;
    }

    public void setHttpPort( String httpPort )
    {
        if( httpPort != null )
        {
            this.httpPort = Integer.parseInt( httpPort );
        }
        else
        {
            this.httpPort = -1;
        }
        
        this.httpClient = null;
    }

    public void setPassword( String password )
    {
        this.password = password;
        this.httpClient = null;
    }

    public void setUsername( String username )
    {
        this.username = username;
        this.httpClient = null;
    }

}
