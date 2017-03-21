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

package com.liferay.ide.kaleo.ui.wizard;

import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Gregory Amerson
 */
public class NewWorkflowDefinitionWizard extends SapphireWizard<NewWorkflowDefinitionOp>
    implements INewWizard, IWorkbenchWizard
{

    private static NewWorkflowDefinitionOp getModelElement( NewWorkflowDefinitionOp modelElement )
    {
        if( modelElement == null )
        {
            modelElement = NewWorkflowDefinitionOp.TYPE.instantiate();
        }

        return modelElement;
    }

    public NewWorkflowDefinitionWizard()
    {
        this( null );
    }

    public NewWorkflowDefinitionWizard( NewWorkflowDefinitionOp modelElement )
    {
        super( getModelElement( modelElement ), DefinitionLoader.context( NewWorkflowDefinitionWizard.class ).sdef(
            "com.liferay.ide.kaleo.ui.wizard.WorkflowDefinitionWizards" ).wizard( "newWorkflowDefinitionWizard" ) );
    }

    public void init( IWorkbench workbench, final IStructuredSelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            IProject selectedProject = null;
            IContainer selectedFolder = null;

            final IStructuredSelection sel = (IStructuredSelection) selection;

            final Object selectedObject = sel.getFirstElement();

            if( selectedObject instanceof IProject )
            {
                selectedProject = (IProject) selectedObject;
            }
            else if( selectedObject instanceof IJavaProject )
            {
                selectedProject = ( (IJavaProject) selectedObject ).getProject();
            }
            else if( selectedObject instanceof IContainer )
            {
                selectedFolder = (IContainer) selectedObject;
                selectedProject = selectedFolder.getProject();
            }
            else if( selectedObject instanceof IFile )
            {
                selectedFolder = ( (IFile) selectedObject ).getParent();
                selectedProject = selectedFolder.getProject();
            }

            if( selectedProject != null )
            {
                final NewWorkflowDefinitionOp op = element().nearest( NewWorkflowDefinitionOp.class );
                op.setProject( selectedProject.getName() );

                if( selectedFolder != null )
                {
                    op.setFolder( selectedFolder.getProjectRelativePath().toPortableString() );
                }
            }
        }
    }

    @Override
    protected void performPostFinish()
    {
        final String newFilePath = element().getNewFilePath().content( false );

        if( newFilePath != null )
        {
            openFileEditors( ResourcesPlugin.getWorkspace().getRoot().getFile( new Path( newFilePath ) ) );
        }
    }

}
