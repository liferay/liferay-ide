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

package com.liferay.ide.kaleo.ui.diagram;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewStateNode;
import com.liferay.ide.kaleo.core.op.NewStateNodeOp;
import com.liferay.ide.kaleo.core.op.NewStateType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class StateNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newStateNodeWizard";

    public StateNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        return NewStateNodeOp.TYPE.instantiate();
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        NewStateNodeOp newStateNodeOp = op.nearest( NewStateNodeOp.class );

        NewStateNode newStateNode = newNodeFromWizard.nearest( NewStateNode.class );

        State state = newNode.nearest( State.class );

        if( newStateNode != null && state != null )
        {
            state.setName( newStateNode.getName().content() );

            final NewStateType newStateType = newStateNodeOp.getType().content();

            if( newStateType.equals( NewStateType.START ) )
            {
                state.setInitial( true );
            }
            else if( newStateType.equals( NewStateType.END ) )
            {
                state.setEnd( true );
            }

            String workflowStatus = newStateNodeOp.getWorkflowStatus().content( false );

            if( !empty( workflowStatus ) )
            {
                final Action newAction = state.getActions().insert();
                newAction.setName( state.getName().content() );
                newAction.setScriptLanguage( KaleoModelUtil.getDefaultValue(
                    state, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY ) );
                newAction.setExecutionType( Executable.DEFAULT_EXECUTION_TYPE );

                final IKaleoEditorHelper editorHelper =
                    KaleoUI.getKaleoEditorHelper( newAction.getScriptLanguage().text() );

                if( editorHelper != null )
                {
                    try
                    {
                        File statusUpdatesFolder =
                            new File( FileLocator.toFileURL(
                                Platform.getBundle( editorHelper.getContributorName() ).getEntry(
                                    "palette/Status Updates" ) ).getFile() );

                        File statusSnippet =
                            new File( statusUpdatesFolder, workflowStatus + "." + editorHelper.getFileExtension() );

                        if( statusSnippet.exists() )
                        {
                            newAction.setScript( FileUtil.readContents( statusSnippet, true ) );
                        }
                    }
                    catch( Exception e )
                    {
                        // ignore
                    }
                }
            }

            if( !newStateType.equals( NewStateType.END ) && newStateNodeOp.getExitTransitionName().content() != null )
            {
                Transition newTransition = state.getTransitions().insert();
                newTransition.setTarget( newStateNodeOp.getExitTransitionName().content() );
                newTransition.setName( newStateNodeOp.getExitTransitionName().content() );
            }
        }
    }

}
