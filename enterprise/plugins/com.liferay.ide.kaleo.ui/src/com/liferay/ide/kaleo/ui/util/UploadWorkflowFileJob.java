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

package com.liferay.ide.kaleo.ui.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class UploadWorkflowFileJob extends Job
{

    private IKaleoConnection kaleoConnection;
    private IFile workflowFile;
    private Runnable runnable;

    public UploadWorkflowFileJob( IKaleoConnection kaleoConnection, IFile workflowFile, Runnable runnable )
    {
        super( "Uploading new workflow draft definition" );

        this.kaleoConnection = kaleoConnection;
        this.workflowFile = workflowFile;
        this.runnable = runnable;
    }

    @Override
    protected IStatus run( IProgressMonitor monitor )
    {
        try
        {
            final String errorMsgs = KaleoUtil.checkWorkflowDefinitionForErrors( this.workflowFile );

            if( !CoreUtil.empty( errorMsgs ) )
            {
                UIUtil.async
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            MessageDialog.openError(
                                Display.getDefault().getActiveShell(), "Upload Kaleo Workflow",
                                    "Unable to upload kaleo workflow:\n\n" + errorMsgs );
                        }
                    }
               );

                return Status.OK_STATUS;
            }

            final JSONObject def = kaleoConnection.getKaleoDefinitions().getJSONObject( 0 );
            long userId = def.getLong( "userId" );
            int companyId = def.getInt("companyId");
            long groupId = def.getLong( "groupId" );

            WorkflowDefinition workflowDefinition =
                WorkflowDefinition.TYPE.instantiate(
                    new RootXmlResource( new XmlResourceStore( workflowFile.getContents() ) ) ).nearest(
                    WorkflowDefinition.class );

            String portalLocale = "en_US";

            try
            {
                portalLocale = kaleoConnection.getPortalLocale( userId );
            }
            catch( Exception e )
            {
            }

            String name = workflowDefinition.getName().content();
            String titleMap = KaleoUtil.createJSONTitleMap( name, portalLocale );
            String content = CoreUtil.readStreamToString( workflowFile.getContents() );

            JSONArray drafts = kaleoConnection.getKaleoDraftWorkflowDefinitions();

            // go through to see if the file that is being uploaded has any existing drafts
            JSONObject existingDraft = null;

            if( drafts != null && drafts.length() > 0 )
            {
                for( int i = 0; i < drafts.length(); i++ )
                {
                    JSONObject draft = drafts.getJSONObject( i );
                    String draftName = draft.getString( "name" );

                    if( name != null && name.equals( draftName ) )
                    {
                        if( existingDraft == null )
                        {
                            existingDraft = draft;
                        }
                        else
                        {
                            if( draft.getInt( "draftVersion" ) > existingDraft.getInt( "draftVersion" ) ||
                                draft.getInt("version") > existingDraft.getInt("version") )
                            {
                                existingDraft = draft;
                            }
                        }
                    }
                }
            }

            if( existingDraft != null )
            {
                kaleoConnection.updateKaleoDraftDefinition(
                    name, titleMap, content, existingDraft.getInt( "version" ), existingDraft.getInt( "draftVersion" ),
                    companyId, userId );
            }

            kaleoConnection.publishKaleoDraftDefinition(
                name, titleMap, content, companyId + "", userId + "", groupId + "" );
        }
        catch( Exception e )
        {
            return KaleoUI.createErrorStatus( "Error uploading new kaleo workflow file.", e );
        }

        if( this.runnable != null )
        {
            this.runnable.run();
        }

        return Status.OK_STATUS;
    }
}
