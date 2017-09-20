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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.remote.RemoteConnection;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.eclipse.core.runtime.IProgressMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class ServerManagerConnection extends RemoteConnection implements IServerManagerConnection
{
    private String managerContextPath;

    public ServerManagerConnection()
    {
        this( null, -1, null, null, null );
    }

    public ServerManagerConnection( String host, int httpPort, String username, String pw, String managerContextPath )
    {
        setHost( host );
        setHttpPort( httpPort );
        setUsername( username );
        setPassword( pw );
        this.managerContextPath = managerContextPath;
    }

    public int getDebugPort() throws APIException
    {
        String debugPort = getRemoteServerConfig( getDebugPortAPI() );

        if( debugPort != null )
        {
            return Integer.parseInt( debugPort );
        }

        return -1;
    }

    private String getDebugPortAPI()
    {
        return managerContextPath + "/server/debug-port"; //$NON-NLS-1$
    }

    private String getDeployURI( String appName )
    {
        return getPluginsAPI() + "/" + appName; //$NON-NLS-1$
    }

    public String getFMDebugPassword() throws APIException
    {
        return getRemoteServerConfig( getFMDebugPasswordAPI() );
    }

    private String getFMDebugPasswordAPI()
    {
        return managerContextPath + "/server/freemarker/debug-password"; //$NON-NLS-1$
    }

    public int getFMDebugPort() throws APIException
    {
        String fmDebugPort = getRemoteServerConfig( getFMDebugPortAPI() );

        if( fmDebugPort != null )
        {
            return Integer.parseInt( fmDebugPort );
        }

        return -1;
    }

    private String getFMDebugPortAPI()
    {
        return managerContextPath + "/server/freemarker/debug-port"; //$NON-NLS-1$
    }

    private String getIsAliveAPI()
    {
        return managerContextPath + "/status"; //$NON-NLS-1$
    }

    @SuppressWarnings( "unchecked" )
    private <T> T getJSONOutput( JSONObject jsonObject ) throws JSONException
    {
        if( jsonObject.has( "output" ) )
        {
            return (T) jsonObject.get( "output" );
        }
        else
        {
            return null;
        }
    }

    public List<String> getLiferayPlugins()
    {
        List<String> retval = new ArrayList<String>();

        Object response = null;

        try
        {
            response = getJSONAPI( getPluginsAPI() );
        }
        catch( APIException e1 )
        {
            LiferayServerCore.logError( e1);
        }

        if( response instanceof JSONObject )
        {
            JSONObject json = (JSONObject) response;

            try
            {
                if( isSuccess( json ) )
                {
                    JSONArray jsonPlugins = getJSONOutput( json );

                    for( int i = 0; i < jsonPlugins.length(); i++ )
                    {
                        retval.add( jsonPlugins.get( i ).toString() );
                    }
                }
            }
            catch( Exception e )
            {
                LiferayServerCore.logError( e );
            }
        }

        return retval;
    }

    public String getManagerURI()
    {
        return "http://" + getHost() + ":" + getHttpPort() + managerContextPath; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String getPluginsAPI()
    {
        return managerContextPath + "/plugins"; //$NON-NLS-1$
    }

    private String getPluginURI( String appName )
    {
        return getPluginsAPI() + "/" + appName; //$NON-NLS-1$
    }

    public String getRemoteServerConfig( String configAPI ) throws APIException
    {
        if( isAlive() )
        {
            Object response = getJSONAPI( configAPI );

            if( response instanceof JSONObject )
            {
                JSONObject jsonResponse = (JSONObject) response;

                try
                {
                    if( isSuccess( jsonResponse ) )
                    {
                        return getJSONOutput( jsonResponse );
                    }
                }
                catch (JSONException e)
                {
                    throw new APIException( configAPI, e );
                }
            }
        }

        return null;
    }

    public String getServerState() throws APIException
    {
        if( isAlive() )
        {
            return "STARTED"; //$NON-NLS-1$
        }
        else
        {
            return "STOPPED"; //$NON-NLS-1$
        }
    }


    private String getUndeployURI( String appName )
    {
        return getDeployURI( appName );
    }

    private String getUpdateURI( String appName )
    {
        return getDeployURI( appName );
    }

    public Object installApplication( String absolutePath, String appName, IProgressMonitor submon ) throws APIException
    {
        try
        {
            FileBody fileBody = new FileBody( new File( absolutePath ) );

            MultipartEntity entity = new MultipartEntity();
            entity.addPart( "deployWar", fileBody ); //$NON-NLS-1$

            HttpPost httpPost = new HttpPost();
            httpPost.setEntity( entity );

            Object response = httpJSONAPI( httpPost, getDeployURI(appName) );

            if( response instanceof JSONObject )
            {
                JSONObject json = (JSONObject) response;

                if( isSuccess( json ) )
                {
                    System.out.println( "installApplication: Sucess.\n\n" ); //$NON-NLS-1$
                }
                else
                {
                    if( isError( json ) )
                    {
                        return json.getString( "error" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return "installApplication error " + getDeployURI( appName ); //$NON-NLS-1$
                    }
                }
            }

            httpPost.releaseConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return e.getMessage();
        }

        return null;
    }

    public boolean isAlive() throws APIException
    {
        JSONObject status = null;

        Object jsonResponse = getJSONAPI( getIsAliveAPI() );

        if( jsonResponse instanceof JSONObject )
        {
            status = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( getIsAliveAPI(), "Unable to connect to server manager." ); //$NON-NLS-1$
        }

        if( isSuccess( status ) )
        {
            return true;
        }

        return false;
    }

    public boolean isAppInstalled( String appName ) throws APIException
    {
        Object response = getJSONAPI( getPluginURI(appName) );

        if( response instanceof JSONObject )
        {
            JSONObject json = (JSONObject) response;

            try
            {
                if( isSuccess( json ) )
                {
                    JSONObject output = getJSONOutput( json );

                    return output.getBoolean( "installed" );
               }
            }
            catch( Exception e )
            {
                throw new APIException( getPluginURI(appName), e );
            }
        }

        return false;
    }

    private boolean isError( JSONObject jsonObject )
    {
        try
        {
            final String error = jsonObject.getString( "error" ); //$NON-NLS-1$

            return !CoreUtil.isNullOrEmpty( error );
        }
        catch( JSONException e )
        {
        }

        return false;
    }

    public boolean isLiferayPluginStarted( String appName ) throws APIException
    {
        final Object response = getJSONAPI( getPluginURI(appName) );

        if( response instanceof JSONObject )
        {
            JSONObject json = (JSONObject) response;

            try
            {
                if( isSuccess( json ) )
                {
                    JSONObject jsonOutput = getJSONOutput( json );

                    boolean installed = jsonOutput.getBoolean( "started" );

                    if( installed )
                    {
                        return true;
                    }
                }
            }
            catch( Exception e )
            {
                throw new APIException( getPluginURI(appName), e );
            }
        }

        return false;
    }

    private boolean isSuccess( JSONObject jsonObject )
    {
        try
        {
            return 0 == jsonObject.getInt( "status" );
        }
        catch( JSONException e )
        {
        }

        return false;
    }

    public void setManagerContextPath( String managerContextPath )
    {
        this.managerContextPath = managerContextPath;
    }

    public Object uninstallApplication( String appName, IProgressMonitor monitor ) throws APIException
    {
        Object response = deleteJSONAPI( getUndeployURI( appName ) );

        if( response instanceof JSONObject )
        {
            JSONObject json = (JSONObject) response;

            try
            {
                if( isSuccess( json ) )
                {
                    System.out.println( "uninstallApplication: success\n\n" ); //$NON-NLS-1$
                }
                else
                {
                    if( isError( json ) )
                    {
                        return json.getString( "error" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return "uninstallApplication error " + getDeployURI( appName ); //$NON-NLS-1$
                    }
                }
            }
            catch( Exception e )
            {
                throw new APIException( getUndeployURI(appName), e );
            }
        }

        return null;
    }

    public Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor ) throws APIException
    {
        try
        {
            final File file = new File( absolutePath );
            final FileBody fileBody = new FileBody( file );

            final MultipartEntity entity = new MultipartEntity();
            entity.addPart( file.getName(), fileBody );

            final HttpPut httpPut = new HttpPut();
            httpPut.setEntity( entity );

            Object response = httpJSONAPI( httpPut, getUpdateURI( appName ) );

            if( response instanceof JSONObject )
            {
                JSONObject json = (JSONObject)response;

                if( isSuccess( json ) )
                {
                    System.out.println( "updateApplication: success.\n\n" ); //$NON-NLS-1$
                }
                else
                {
                    if( isError( json ) )
                    {
                        return json.getString( "error" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return "updateApplication error " + getDeployURI( appName ); //$NON-NLS-1$
                    }
                }
            }

            httpPut.releaseConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return e.getMessage();
        }

        return null;
    }

}
