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
 *******************************************************************************/

package com.liferay.ide.server.remote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IProgressMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Greg Amerson
 */
public class RemoteConnection implements IRemoteConnection
{
    private GetMethod debugPortMethod;
    private String host;
    private HttpClient httpClient;
    private String httpPort;
    private GetMethod isAliveMethod;
    private String managerContextPath;
    private String password;
    private String username;

    public RemoteConnection()
    {
        this( null, null, null, null, null );
    }

    public RemoteConnection( String host, String httpPort, String username, String pw, String managerContextPath )
    {
        this.host = host;
        this.httpPort = httpPort;
        this.managerContextPath = managerContextPath;
        this.username = username;
        this.password = pw;
    }

    public int getDebugPort()
    {
        if( isAlive() )
        {
            try
            {
                GetMethod method = getDebugPortMethod();
                int statusCode = getHttpClient().executeMethod( method );

                if( statusCode != HttpStatus.SC_OK )
                {
                    System.err.println( "Method failed: " + method.getStatusLine() );
                }
                else
                {
                    // Read the response body.
                    byte[] body = method.getResponseBody();

                    String response = new String( body );
                    JSONObject jsonObject = new JSONObject( response );

                    if( isSuccess( jsonObject ) )
                    {
                        String debugPortOutput = getJSONOutput( jsonObject );

                        return Integer.parseInt( new String( debugPortOutput ) );
                    }

                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

        }

        return -1;
    }

    public List<String> getLiferayPlugins()
    {
        List<String> retval = new ArrayList<String>();

        try
        {
            GetMethod method = new GetMethod( getPluginsURI() );

            int statusCode = getHttpClient().executeMethod( method );

            if( statusCode != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + method.getStatusLine() );
            }
            else
            {
                // Read the response body.
                byte[] body = method.getResponseBody();

                JSONObject json = new JSONObject( new String( body ) );

                if( isSuccess( json ) )
                {
                    String output = getJSONOutput( json );
                    JSONArray jsonPlugins = new JSONArray( output );

                    for( int i = 0; i < jsonPlugins.length(); i++ )
                    {
                        retval.add( jsonPlugins.get( i ).toString() );
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return retval;
    }

    public String getManagerURI()
    {
        return "http://" + host + ":" + httpPort + managerContextPath;
    }

    public String getPassword()
    {
        return password;
    }

    public String getServerState()
    {
        if( isAlive() )
        {
            return "STARTED";
        }
        else
        {
            return "STOPPED";
        }
    }

    public String getUsername()
    {
        return username;
    }

    public Object installApplication( String absolutePath, String appName, IProgressMonitor submon )
    {
        try
        {
            File f = new File( absolutePath );

            PostMethod filePost = new PostMethod( getDeployURI( appName ) );

            Part[] parts = { new FilePart( "deployWar", f ) };
            filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

            int status = getHttpClient().executeMethod( filePost );

            if( status != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + filePost.getStatusLine() );
            }

            String responseString = filePost.getResponseBodyAsString();

            JSONObject json = new JSONObject( responseString );

            if( isSuccess( json ) )
            {
                System.out.println( "installApplication: Sucess.\n\n" );
            }

            filePost.releaseConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return e.getMessage();
        }

        return null;
    }

    public boolean isAlive()
    {
        try
        {
            GetMethod method = getIsAliveMethod();
            int statusCode = getHttpClient().executeMethod( method );

            if( statusCode != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + method.getStatusLine() );
            }
            else
            {
                // Read the response body.
                byte[] body = method.getResponseBody();

                if( isSuccess( new JSONObject( new String( body ) ) ) )
                {
                    return true;
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isAppInstalled( String appName )
    {
        try
        {
            GetMethod method = new GetMethod( getPluginURI( appName ) );

            int statusCode = getHttpClient().executeMethod( method );

            if( statusCode != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + method.getStatusLine() );
            }
            else
            {
                // Read the response body.
                byte[] body = method.getResponseBody();

                JSONObject json = new JSONObject( new String( body ) );

                if( isSuccess( json ) )
                {
                    String output = getJSONOutput( json );
                    JSONObject jsonOutput = new JSONObject( output );

                    Boolean installed = Boolean.parseBoolean( jsonOutput.getString( "installed" ) );

                    if( installed )
                    {
                        return true;
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isLiferayPluginStarted( String appName )
    {
        try
        {
            GetMethod method = new GetMethod( getPluginURI( appName ) );

            int statusCode = getHttpClient().executeMethod( method );

            if( statusCode != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + method.getStatusLine() );
            }
            else
            {
                // Read the response body.
                byte[] body = method.getResponseBody();

                JSONObject json = new JSONObject( new String( body ) );

                if( isSuccess( json ) )
                {
                    String output = getJSONOutput( json );

                    JSONObject jsonOutput = new JSONObject( output );

                    Boolean installed = Boolean.parseBoolean( jsonOutput.getString( "started" ) );

                    if( installed )
                    {
                        return true;
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return false;
    }

    public void setHost( String host )
    {
        this.host = host;
        this.isAliveMethod = null;
        this.debugPortMethod = null;
    }

    public void setHttpPort( String httpPort )
    {
        this.httpPort = httpPort;
        this.isAliveMethod = null;
        this.debugPortMethod = null;
    }

    public void setManagerContextPath( String managerContextPath )
    {
        this.managerContextPath = managerContextPath;
        this.isAliveMethod = null;
        this.debugPortMethod = null;
    }

    public void setPassword( String pw )
    {
        this.httpClient = null;
        this.password = pw;
    }

    public void setUsername( String user )
    {
        this.httpClient = null;
        this.username = user;
    }

    public Object uninstallApplication( String appName, IProgressMonitor monitor )
    {
        try
        {
            DeleteMethod undeployMethod = new DeleteMethod( getUndeployURI( appName ) );

            int status = getHttpClient().executeMethod( undeployMethod );

            if( status != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + undeployMethod.getStatusLine() );
            }

            String responseString = undeployMethod.getResponseBodyAsString();

            if( isSuccess( new JSONObject( responseString ) ) )
            {
                System.out.println( "uninstallApplication: success." );
            }

            undeployMethod.releaseConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return e.getMessage();
        }

        return null;
    }

    public Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor )
    {
        try
        {
            File f = new File( absolutePath );

            PutMethod filePut = new PutMethod( getUpdateURI( appName ) );
            Part[] parts = { new FilePart( f.getName(), f ) };
            filePut.setRequestEntity( new MultipartRequestEntity( parts, filePut.getParams() ) );

            int status = getHttpClient().executeMethod( filePut );
            if( status != HttpStatus.SC_OK )
            {
                System.err.println( "Method failed: " + filePut.getStatusLine() );
            }

            String responseString = filePut.getResponseBodyAsString();
            if( isSuccess( new JSONObject( responseString ) ) )
            {
                System.out.println( "updateApplication: success." );
            }

            filePut.releaseConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return e.getMessage();
        }

        return null;
    }

    private GetMethod getDebugPortMethod()
    {
        if( debugPortMethod == null )
        {
            // debugPortMethod = new GetMethod( getDebugPortURI() );
            return new GetMethod( getDebugPortURI() );
        }

        return debugPortMethod;
    }

    private String getDebugPortURI()
    {
        return getManagerURI() + "/server/debug-port";
    }

    private String getDeployURI( String appName )
    {
        return getPluginsURI() + "/" + appName;
    }

    private HttpClient getHttpClient()
    {
        if( httpClient == null )
        {
            HttpClient httpClient2 = new HttpClient();
            httpClient2.getParams().setAuthenticationPreemptive( true );
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials( username, password );
            httpClient2.getState().setCredentials( new AuthScope( host, Integer.parseInt( httpPort ) ), creds );
            return httpClient2;
        }

        return httpClient;
    }

    private GetMethod getIsAliveMethod()
    {
        if( isAliveMethod == null )
        {
            // isAliveMethod = new GetMethod( getIsAliveURI() );
            return new GetMethod( getIsAliveURI() );
        }

        return isAliveMethod;
    }

    private String getIsAliveURI()
    {
        return getManagerURI() + "/status";
    }

    private String getJSONOutput( JSONObject json ) throws JSONException
    {
        return json.getString( "output" );
    }

    private String getPluginsURI()
    {
        return getManagerURI() + "/plugins";
    }

    private String getPluginURI( String appName )
    {
        return getPluginsURI() + "/" + appName;
    }

    private String getUndeployURI( String appName )
    {
        return getDeployURI( appName );
    }

    private String getUpdateURI( String appName )
    {
        return getDeployURI( appName );
    }

    private boolean isSuccess( JSONObject jsonObject ) throws JSONException
    {
        String success = jsonObject.getString( "status" );
        return "0".equals( success );
    }

}
