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

package com.liferay.ide.kaleo.ui.editor;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.util.IWorkflowValidation;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.KaleoUIPreferenceConstants;
import com.liferay.ide.kaleo.ui.WorkflowDesignerPerspectiveFactory;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionEditor extends SapphireEditorForXml
{

    private class PropertyListener implements IPropertyListener
    {
        public void propertyChanged( Object source, int propId )
        {
            switch( propId ) {
            case IEditorPart.PROP_INPUT:
            {
                if( source == getXmlEditor() )
                {
                    setInput( getXmlEditor().getEditorInput() );
                }
            }
            case IEditorPart.PROP_DIRTY:
            {
                if( source == getXmlEditor() )
                {
                    if( getXmlEditor().getEditorInput() != getEditorInput() )
                    {
                        setInput( getXmlEditor().getEditorInput() );
                        /*
                         * title should always change when input changes. create runnable for following post call
                         */
                        Runnable runnable = new Runnable()
                        {
                            public void run()
                            {
                                _firePropertyChange( IWorkbenchPart.PROP_TITLE );
                            }
                        };
                        /*
                         * Update is just to post things on the display queue (thread). We have to do this to get the
                         * dirty property to get updated after other things on the queue are executed.
                         */
                        ( (Control) getXmlEditor().getAdapter( Control.class ) ).getDisplay().asyncExec( runnable );
                    }
                }
                break;
            }
            case IWorkbenchPart.PROP_TITLE:
            {
                // update the input if the title is changed
                if( source == getXmlEditor() )
                {
                    if( getXmlEditor().getEditorInput() != getEditorInput() )
                    {
                        setInput( getXmlEditor().getEditorInput() );
                    }
                }
                break;
            }
            default:
            {
                // propagate changes. Is this needed? Answer: Yes.
                if( source == getXmlEditor() )
                {
                    _firePropertyChange( propId );
                }
                break;
            }
            }
        }
    }

    public static final String EDITOR_ID = "com.liferay.ide.kaleo.ui.editor.workflowDefinition";

    public static final int PROP_NODE_WIZARDS_ENABLED = 0x13303;
    public static int PROP_UPDATE_VERSION = 0x404;

    private SapphireDiagramEditor diagramEditor;
    private boolean gridVisible = true;
    private boolean nodeWizardsEnabled = true;

    private com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor.PropertyListener propertyListener;

    private boolean showGuides = true;

    public WorkflowDefinitionEditor()
    {
        super( WorkflowDefinition.TYPE, DefinitionLoader.sdef( WorkflowDefinitionEditor.class ).page( "DiagramPage" ) );

        String gridVisibleValue = getPersistentProperty( KaleoCore.GRID_VISIBLE_KEY );

        if( !empty( gridVisibleValue ) )
        {
            this.gridVisible = Boolean.parseBoolean( gridVisibleValue );
        }
    }

    void _firePropertyChange(int property)
    {
        super.firePropertyChange(property);
    }

    @Override
    public <A> A adapt( Class<A> adapterType )
    {
        if (WorkflowDefinitionEditor.class.equals(adapterType))
        {
            return adapterType.cast( this );
        }
        else if (IServer.class.equals( adapterType ))
        {
           if (getEditorInput() instanceof WorkflowDefinitionEditorInput)
           {
               WorkflowDefinitionEditorInput workflowInput = (WorkflowDefinitionEditorInput) getEditorInput();

               IServer server = workflowInput.getWorkflowDefinitionEntry().getParent().getParent();

               return adapterType.cast( server );
           }
        }

        return super.adapt( adapterType );
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        this.diagramEditor = new SapphireDiagramEditor( this, this.getModelElement(), DefinitionLoader.sdef( WorkflowDefinitionEditor.class ).page( "DiagramPage" ) );

        addEditorPage( 0, this.diagramEditor );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        // don't create any form page for definition editor

        if( this.propertyListener == null )
        {
            this.propertyListener = new PropertyListener();
        }

        this.getXmlEditor().addPropertyListener( this.propertyListener );
    }

    @Override
    protected void createPages()
    {
        super.createPages();

        try
        {
            String id = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();

            if( WorkflowDesignerPerspectiveFactory.ID.equals( id ) )
            {
                return;
            }
        }
        catch( Exception e )
        {
        }

        String perspectiveSwitch = KaleoUI.getDefault().getPreferenceStore().getString(
            KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH );

        boolean remember = false;
        boolean openPerspective = false;

        if( MessageDialogWithToggle.PROMPT.equals( perspectiveSwitch ) )
        {
            MessageDialogWithToggle toggleDialog =
                MessageDialogWithToggle.openYesNoQuestion(
                    this.getSite().getShell(),
                    "Open Kaleo Designer Perspective?",
                    "This kind of file is associated with the Kaleo Designer perspective.\n\n"
                        + "This perspective is designed to support Kaleo Workflow development. "
                        + "It places the Properties and Palette views in optimal location relative to the editor area.\n\n"
                        + "Do you want to open this perspective now?", "Remember my decision", false,
                    KaleoUI.getDefault().getPreferenceStore(), KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH );

            remember = toggleDialog.getToggleState();
            openPerspective = toggleDialog.getReturnCode() == IDialogConstants.YES_ID;

            if( remember )
            {
                KaleoUI.getPrefStore().setValue(
                    KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH,
                    openPerspective ? MessageDialogWithToggle.ALWAYS : MessageDialogWithToggle.NEVER );
            }
        }
        else if( MessageDialogWithToggle.ALWAYS.equals( perspectiveSwitch ) )
        {
            openPerspective = true;
        }

        if( openPerspective )
        {
            switchToKaleoDesignerPerspective();
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( ( this.getXmlEditor() != null ) && ( this.propertyListener != null ) )
        {
            this.getXmlEditor().removePropertyListener( this.propertyListener );
        }
    }

    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        IEditorInput editorInput = this.getEditorInput();

        if( editorInput instanceof WorkflowDefinitionEditorInput )
        {
            WorkflowDefinitionEditorInput definitionInput = (WorkflowDefinitionEditorInput) editorInput;

            try
            {
                IRuntime runtime = definitionInput.getWorkflowDefinitionEntry().getParent().getParent().getRuntime();

                IWorkflowValidation workflowValidation = KaleoCore.getWorkflowValidation( runtime );

                Exception error = workflowValidation.validate( new ByteArrayInputStream(this.getXmlEditor().getDocumentProvider().getDocument( definitionInput ).get().getBytes()));

                // ignore errors with empty messages, likely an error in validation routine itself STUDIO-263
                if (error != null && ! CoreUtil.isNullOrEmpty( error.getMessage() ) )
                {
                    MessageDialog.openError( Display.getDefault().getActiveShell(), "Kaleo Workflow Validation",
                        "Unable to save kaleo workflow:\n\n" + error.getMessage());

                    return;
                }
            }
            catch (Exception e)
            {
                //do nothing
            }
        }

        if( this.diagramEditor != null )
        {
            this.diagramEditor.doSave( monitor );
        }

        super.doSave( monitor );

        if( editorInput instanceof WorkflowDefinitionEditorInput )
        {
            WorkflowDefinitionEditorInput definitionInput = (WorkflowDefinitionEditorInput) editorInput;

            saveWorkflowDefinitionEntry( definitionInput );
        }
    }

    @Override
    public void doSaveAs()
    {
        this.getXmlEditor().doSaveAs();
    }

    public SapphireDiagramEditor getDiagramEditor()
    {
        return this.diagramEditor;
    }

    private String getPersistentProperty( QualifiedName key )
    {
        String retval = null;

        try
        {
            IFile workspaceFile = this.getLocalModelElement().adapt( IFile.class );
            retval = workspaceFile.getPersistentProperty( key );
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    public boolean isGridVisible()
    {
        return this.gridVisible;
    }

    public boolean isNodeWizardsEnabled()
    {
        return this.nodeWizardsEnabled;
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    public boolean isShowGuides()
    {
        return this.showGuides;
    }

    private void saveWorkflowDefinitionEntry( WorkflowDefinitionEditorInput definitionInput )
    {
        StructuredTextEditor sourceEditor = this.getXmlEditor();

        final WorkflowDefinitionEntry definition = definitionInput.getWorkflowDefinitionEntry();

        final String titleMap = definition.getTitleMap();
        final String titleCurrentValue = definition.getTitleCurrentValue();
        final String definitionContent = sourceEditor.getDocumentProvider().getDocument( this.getEditorInput() ).get();
        final String[] newTitleMap = new String[1];

        if( titleCurrentValue != null && titleMap != null )
        {
            try
            {
                String localeCode = Locale.getDefault().toString();
                JSONObject jsonTitleMap = new JSONObject( titleMap );

                Iterator<?> keys = jsonTitleMap.keys();

                while (keys != null && keys.hasNext())
                {
                    Object key = keys.next();

                    String value = jsonTitleMap.getString( key.toString() );

                    if (value != null && value.contains(titleCurrentValue))
                    {
                        localeCode = key.toString();
                        break;
                    }
                }

                jsonTitleMap.put( localeCode, titleCurrentValue );
                newTitleMap[0] = jsonTitleMap.toString();
            }
            catch( Exception e )
            {
            }
        }
        else
        {
            newTitleMap[0] = definition.getTitleMap();
        }

        if( empty( newTitleMap[0] ) )
        {
            try
            {
                newTitleMap[0] = KaleoUtil.createJSONTitleMap( definition.getTitle() );
            }
            catch( JSONException e )
            {
            }
        }

        int draftVersion = definition.getDraftVersion() + 1;

        if( draftVersion == 0 )
        {
            draftVersion = 1;
        }

        final int newDraftVersion = draftVersion;

        Job saveWorkflowEntry = new Job( "Saving kaleo workflow entry." )
        {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                IStatus retval = Status.OK_STATUS;

                try
                {
                    JSONObject updatedDraftDefinition = null;

                    IKaleoConnection kaleoConnection =
                        KaleoCore.getKaleoConnection( definition.getParent().getParent() );

                    try
                    {
                        JSONObject latestDraftDefinition = kaleoConnection.getLatestKaleoDraftDefinition(
                            definition.getName(), definition.getVersion(), definition.getCompanyId() );

                        if( latestDraftDefinition != null )
                        {
                            updatedDraftDefinition = kaleoConnection.updateKaleoDraftDefinition(
                                definition.getName(), newTitleMap[0], definitionContent,
                                latestDraftDefinition.getInt( "version" ),
                                latestDraftDefinition.getInt( "draftVersion" ), definition.getCompanyId(),
                                definition.getUserId() );
                        }
                        else
                        {
                            updatedDraftDefinition = kaleoConnection.addKaleoDraftDefinition(
                                definition.getName(), newTitleMap[0], definitionContent, definition.getVersion(),
                                newDraftVersion, definition.getUserId(), definition.getGroupId() );
                        }
                    }
                    catch( Exception e )
                    {
                        updatedDraftDefinition = kaleoConnection.addKaleoDraftDefinition(
                            definition.getName(), newTitleMap[0], definitionContent, definition.getVersion(),
                            newDraftVersion, definition.getUserId(), definition.getGroupId() );
                    }

                    final WorkflowDefinitionEntry newNode =
                        WorkflowDefinitionEntry.createFromJSONObject( updatedDraftDefinition );

                    newNode.setParent( definition.getParent() );

                    newNode.setCompanyId( definition.getCompanyId() );
                    newNode.setContent( definitionContent );
                    newNode.setDraftVersion( newDraftVersion );
                    newNode.setName( definition.getName() );
                    newNode.setLocation( definition.getLocation() );
                    newNode.setTitleCurrentValue( titleCurrentValue );
                    newNode.setTitleMap( newTitleMap[0] );
                    newNode.setUserId( definition.getUserId() );
                    newNode.setVersion( definition.getVersion() );
                    newNode.setGroupId( definition.getGroupId() );

                    Display.getDefault().asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            WorkflowDefinitionEditorInput workflowEditorInput =
                                (WorkflowDefinitionEditorInput) getEditorInput();

                            workflowEditorInput.setWorkflowDefinitionEntry( newNode );

                            setPartName( workflowEditorInput.getName() );

                            firePropertyChange( PROP_UPDATE_VERSION );
                        }
                    } );

                }
                catch( Exception e )
                {
                    retval = KaleoUI.createErrorStatus( "Could not save kaleo workflow entry.", e );
                }

                return retval;
            }
        };

        saveWorkflowEntry.schedule();

    }

    public void setGridVisible( Boolean gridVisible )
    {
        this.gridVisible = gridVisible;
        setPersistentProperty( KaleoCore.GRID_VISIBLE_KEY, gridVisible.toString() );
    }

    public void setNodeWizardsEnabled( boolean enabled )
    {
        this.nodeWizardsEnabled = enabled;
        _firePropertyChange( PROP_NODE_WIZARDS_ENABLED );
    }

    private void setPersistentProperty( QualifiedName key, String value )
    {
        try
        {
            IFile workspaceFile = this.getLocalModelElement().adapt( IFile.class );
            workspaceFile.setPersistentProperty( key, value );
        }
        catch( Exception e )
        {
        }
    }

    public void setShowGuides( boolean showGuides )
    {
        this.showGuides = showGuides;
    }

    private void switchToKaleoDesignerPerspective()
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            public void run()
            {
                IPerspectiveDescriptor perspective =
                    PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                        WorkflowDesignerPerspectiveFactory.ID );
                getSite().getWorkbenchWindow().getActivePage().setPerspective( perspective );
            }
        } );
    }

}
