/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ExecutionType;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;
import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public final class NewWorkflowDefinitionOpMethods
{

    public static final Status execute( final NewWorkflowDefinitionOp op, final ProgressMonitor monitor )
    {
        try
        {
            final IProject project = op.getProject().target();

            final Path projectPath = op.getFolder().content();

            IContainer folder = null;

            if( projectPath != null )
            {
                folder = project.getFolder( projectPath.toPortableString() );
            }
            else
            {
                folder = project;
            }

            if( !folder.exists() )
            {
                folder = folder.getParent();
            }

            final String name = op.getName().content();

            final String[] segments = name.toLowerCase().split( "\\s+" );

            final StringBuilder newName = new StringBuilder();

            for( String segment : segments )
            {
                newName.append( segment ).append( '-' );
            }

            final String fileNameBase = newName.toString() + "definition";
            final String extension = ".xml";
            String fileName = fileNameBase + extension;

            IFile newDefinitionFile = project.getFile( folder.getProjectRelativePath().append( fileName ) );

            int count = 1;

            while( newDefinitionFile.exists() )
            {
                fileName = fileNameBase + " (" + count + ")" + extension;

                newDefinitionFile = project.getFile( folder.getProjectRelativePath().append( fileName ) );

                count++;
            }

            newDefinitionFile.create( new ByteArrayInputStream( "".getBytes() ), true, null );

            newDefinitionFile.setPersistentProperty(
                KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, op.getDefaultScriptLanguage().text( true ) );

            newDefinitionFile.setPersistentProperty(
                KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, op.getDefaultTemplateLanguage().text( true ) );

            final RootXmlResource rootXmlResource =
                new RootXmlResource( new XmlResourceStore( new WorkspaceFileResourceStore( newDefinitionFile ) ) );

            final WorkflowDefinition workflowDefinition = WorkflowDefinition.TYPE.instantiate( rootXmlResource );

            workflowDefinition.setName( name );
            workflowDefinition.setVersion( "1" );

            final String initialStateName = op.getInitialStateName().content( true );

            final State state = workflowDefinition.getStates().insert();
            state.setName( initialStateName );
            state.setInitial( true );

            final String initialTaskName = op.getInitialTaskName().content( true );
            final Task task = workflowDefinition.getTasks().insert();
            task.setName( initialTaskName );

            KaleoModelUtil.changeTaskAssignments( task, op );

            final Transition transition = state.getTransitions().insert();

            transition.setName( initialTaskName );
            transition.setTarget( initialTaskName );

            final String finalStateName = op.getFinalStateName().content( true );

            final State finalState = workflowDefinition.getStates().insert();
            finalState.setName( finalStateName );
            finalState.setEnd( true );

            final Action finalAction = finalState.getActions().insert();
            finalAction.setName( "approve" );
            finalAction.setExecutionType( ExecutionType.ON_ENTRY );
            finalAction.setScriptLanguage( ScriptLanguageType.JAVASCRIPT );
            finalAction.setScript( "Packages.com.liferay.portal.kernel.workflow.WorkflowStatusManagerUtil.updateStatus(Packages.com.liferay.portal.kernel.workflow.WorkflowConstants.toStatus(\"approved\"), workflowContext);" );

            final Transition taskTransition = task.getTransitions().insert();

            taskTransition.setName( finalStateName );
            taskTransition.setTarget( finalStateName );

            final WorkflowNodeMetadata stateMetadata = state.getMetadata().content();
            stateMetadata.getPosition().setX( 100 );
            stateMetadata.getPosition().setY( 50 );
            stateMetadata.getTransitionsMetadata().insert().setName( initialTaskName );

            final WorkflowNodeMetadata taskMetadata = task.getMetadata().content();
            taskMetadata.getPosition().setX( 300 );
            taskMetadata.getPosition().setY( 35 );
            taskMetadata.getTransitionsMetadata().insert().setName( finalStateName );

            final WorkflowNodeMetadata finalStateMetadata = finalState.getMetadata().content();
            finalStateMetadata.getPosition().setX( 520 );
            finalStateMetadata.getPosition().setY( 50 );

            /*Document document = rootXmlResource.getDomDocument();
            Element docElement = document.getDocumentElement();
            Attr schemaLocation = docElement.getAttributeNode( "xsi:schemaLocation" );
            schemaLocation.setNodeValue( schemaLocation.getNodeValue().replaceAll(
                " http://www.w3.org/XML/1998/namespace ", "" ) );*/

            rootXmlResource.save();

            op.setNewFilePath( newDefinitionFile.getFullPath().toPortableString() );

            return Status.createOkStatus();

        }
        catch( Exception e )
        {
            KaleoCore.logError( "Error creating new kaleo workflow file.", e );
            return Status.createErrorStatus( "Error creating new kaleo workflow file.", e );
        }

    }

}
