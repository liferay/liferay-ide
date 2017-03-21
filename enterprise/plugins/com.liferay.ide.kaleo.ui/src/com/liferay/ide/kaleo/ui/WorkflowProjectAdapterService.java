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

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditorInput;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.WorkflowSupportManager;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.UniversalConversionService;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class WorkflowProjectAdapterService extends UniversalConversionService
{

    @Override
    public <A> A convert( Object object, Class<A> adapterType )
    {
        A retval = null;

        if( IProject.class.equals( adapterType ) )
        {
            ISapphirePart sapphirePart = context().find( ISapphirePart.class );

            WorkflowDefinition workflowDefinition = sapphirePart.getLocalModelElement().nearest( WorkflowDefinition.class );

            IFile file = workflowDefinition.adapt( IFile.class );

            if( file != null )
            {
                retval = adapterType.cast( file.getProject() );
            }
            else
            {
                // create support project
                WorkflowSupportManager workflowSupportManager = KaleoCore.getDefault().getWorkflowSupportManager();

                IEditorInput editorInput = workflowDefinition.adapt( IEditorInput.class );

                if( editorInput instanceof WorkflowDefinitionEditorInput )
                {
                    WorkflowDefinitionEditorInput workflowInput = (WorkflowDefinitionEditorInput) editorInput;
                    IServer server = workflowInput.getWorkflowDefinitionEntry().getParent().getParent();

                    workflowSupportManager.setCurrentServer( server );
                }

                IJavaProject supportProject = workflowSupportManager.getSupportProject();

                retval = adapterType.cast( supportProject.getProject() );
            }
        }

        return retval;
    }

}
