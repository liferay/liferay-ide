/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.server.core.ILiferayServer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Greg Amerson
 */
public class WorkflowDefinitionsFolder
{

    private WorkflowDefinitionEntry[] cachedDefinitions;
    private Job cacheDefinitionsJob;
    private IStatus currentStatus;
    private IServer input;
    private ICommonContentExtensionSite site;

    public WorkflowDefinitionsFolder( ICommonContentExtensionSite site, IServer server )
    {
        this.site = site;
        this.input = server;
        KaleoCore.updateKaleoConnectionSettings( getLiferayServer() );
        scheduleCacheDefinitionsJob();
    }

    public void clearCache()
    {
        this.cachedDefinitions = null;
    }

    private Object createLoadingNode()
    {
        WorkflowDefinitionEntry node = new WorkflowDefinitionEntry();
        node.setLoadingNode( true );
        return node;
    }

    public Object[] getChildren()
    {
        if( this.input.getServerState() != IServer.STATE_STARTED )
        {
            return null;
        }

        if( getLiferayServer() == null )
        {
            return null;
        }

        if( this.cachedDefinitions == null )
        {
            scheduleCacheDefinitionsJob();

            return new Object[] { createLoadingNode() };
        }

        return this.cachedDefinitions;
    }

    private ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) this.input.loadAdapter( ILiferayServer.class, null );
    }

    public IServer getParent()
    {
        return this.input;
    }

    public IStatus getStatus()
    {
        return this.currentStatus;
    }

    private boolean same( JSONObject json1, JSONObject json2, String field ) throws JSONException
    {
        return json1 != null && json2 != null && json1.get( field ) != null &&
            json1.get( field ).equals( json2.get( field ) );
    }

    private void scheduleCacheDefinitionsJob()
    {
        if( cacheDefinitionsJob == null )
        {
            cacheDefinitionsJob = new Job( "Loading kaleo workflows..." )
            {

                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    currentStatus = KaleoUI.createInfoStatus( "Loading kaleo workflows..." );

                    site.getService().update();

                    IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection( getLiferayServer() );

                    List<WorkflowDefinitionEntry> definitionEntries = new ArrayList<WorkflowDefinitionEntry>();

                    IStatus errorStatus = null;

                    try
                    {
                        JSONArray kaleoDefinitions = kaleoConnection.getKaleoDefinitions();
                        JSONArray kaleoDraftDefinitions = kaleoConnection.getKaleoDraftWorkflowDefinitions();

                        for( int i = 0; i < kaleoDefinitions.length(); i++ )
                        {
                            JSONObject definition = (JSONObject) kaleoDefinitions.get( i );

                            // if( definition.has( "active" ) && !definition.getBoolean( "active" ) )
                            // {
                            // continue;
                            // }

                            // kaleoConnection.getLatestKaleoDraftDefinition(
                            // definition.getString( "name" ), definition.getString( "version" ) );

                            WorkflowDefinitionEntry definitionEntry =
                                WorkflowDefinitionEntry.createFromJSONObject( definition );
                            definitionEntry.setParent( WorkflowDefinitionsFolder.this );

                            if( kaleoDraftDefinitions != null )
                            {
                                for( int j = 0; j < kaleoDraftDefinitions.length(); j++ )
                                {
                                    JSONObject draftDefinition = kaleoDraftDefinitions.getJSONObject( j );

                                    if( same( definition, draftDefinition, "name" ) &&
                                        same( definition, draftDefinition, "version" ) )
                                    {
                                        if( definitionEntry.getDraftVersion() < draftDefinition.getInt( "draftVersion" ) )
                                        {
                                            if (draftDefinition.has( "title" ))
                                            {
                                                definitionEntry.setTitle( draftDefinition.getString("title") );
                                            }

                                            if (draftDefinition.has( "titleMap" ))
                                            {
                                                definitionEntry.setTitleMap( draftDefinition.getString("titleMap") );
                                            }
                                            else
                                            {
                                                definitionEntry.setTitleMap( KaleoUtil.createJSONTitleMap( definitionEntry.getTitle() ) );
                                            }

                                            definitionEntry.setCompanyId( draftDefinition.getLong( "companyId" ) );
                                            definitionEntry.setContent( draftDefinition.getString( "content" ) );
                                            definitionEntry.setDraftVersion( draftDefinition.getInt( "draftVersion" ) );
                                            definitionEntry.setTitleCurrentValue( draftDefinition.getString( "titleCurrentValue" ) );
                                            definitionEntry.setUserId( draftDefinition.getLong( "userId" ) );
                                            definitionEntry.setGroupId( draftDefinition.getLong( "groupId" ) );
                                            definitionEntry.setLocation( kaleoConnection.getHost() + ":" +
                                                kaleoConnection.getHttpPort() );
                                        }
                                    }

                                }
                            }

                            definitionEntries.add( definitionEntry );
                        }

                        if( kaleoDraftDefinitions != null )
                        {
                            for( int i = 0; i < kaleoDraftDefinitions.length(); i++ )
                            {
                                JSONObject draftDefinition = kaleoDraftDefinitions.getJSONObject( i );
                                WorkflowDefinitionEntry draftEntry = null;

                                for( WorkflowDefinitionEntry entry : definitionEntries )
                                {
                                    if( entry.getName().equals( draftDefinition.getString( "name" ) ) &&
                                        entry.getVersion() == draftDefinition.getInt( "version" ) )
                                    {
                                        draftEntry = entry;

                                        if( entry.getDraftVersion() < draftDefinition.getInt( "draftVersion" ) )
                                        {
                                            entry.setCompanyId( draftDefinition.getLong( "companyId" ) );
                                            entry.setContent( draftDefinition.getString( "content" ) );
                                            entry.setDraftVersion( draftDefinition.getInt( "draftVersion" ) );
                                            entry.setTitleCurrentValue( draftDefinition.getString( "titleCurrentValue" ) );
                                            entry.setUserId( draftDefinition.getLong( "userId" ) );
                                            entry.setLocation( kaleoConnection.getHost() + ":" +
                                                kaleoConnection.getHttpPort() );
                                        }
                                    }
                                }

                                if( draftEntry == null )
                                {
                                    // draftEntry = WorkflowDefinitionEntry.createFromJSONObject( draftDefinition );
                                    // draftEntry.setParent( WorkflowDefinitionsFolder.this );
                                    // draftEntry.setDraftVersion( draftDefinition.getInt( "draftVersion" ) );
                                    // definitionEntries.add( draftEntry );
                                }
                            }
                        }

                        cachedDefinitions = definitionEntries.toArray( new WorkflowDefinitionEntry[0] );

                    }
                    catch( Exception e )
                    {
                        errorStatus = KaleoUI.createErrorStatus( e );
                    }

                    if( errorStatus != null )
                    {
                        currentStatus = errorStatus;
                    }
                    else
                    {
                        currentStatus = null;
                    }

                    site.getService().update();

                    cacheDefinitionsJob = null;

                    return Status.OK_STATUS;
                }
            };

            cacheDefinitionsJob.schedule();
        }
    }

}
