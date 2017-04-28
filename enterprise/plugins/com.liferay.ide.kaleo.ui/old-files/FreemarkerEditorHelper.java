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

package com.liferay.ide.kaleo.ui.helpers;

import com.liferay.ide.kaleo.ui.AbstractKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.WorkflowContextConstants;
import com.liferay.ide.kaleo.ui.editor.KaleoFreemarkerEditor;
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;

/**
 * @author Gregory Amerson
 */
public class FreemarkerEditorHelper extends AbstractKaleoEditorHelper
{

    public IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IEditorPart editorPart = null;

        try
        {
            editorPart = new KaleoFreemarkerEditor();
            editorPart.init( editorSite, editorInput );
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Could not create freemarker editor.", e );

            editorPart = super.createEditorPart( editorInput, editorSite );
        }

        return editorPart;
    }

    @Override
    public void openEditor( ISapphirePart sapphirePart, Element modelElement, ValueProperty valueProperty )
    {
        IProject project = sapphirePart.adapt( IProject.class );

        ConfigurationManager configManager = ConfigurationManager.getInstance( project );

        ContextValue[] contextValues =
        {
            new ContextValue( WorkflowContextConstants.SERVICE_CONTEXT, Map.class, Map.class ),
            new ContextValue( WorkflowContextConstants.WORKFLOW_CONTEXT, Map.class, Map.class ),
            new ContextValue( WorkflowContextConstants.ENTRY_CLASS_NAME, String.class, String.class ),
            new ContextValue( WorkflowContextConstants.GROUP_ID, Long.class, Long.class ),
            new ContextValue( WorkflowContextConstants.ENTRY_TYPE, String.class, String.class ),
            new ContextValue( WorkflowContextConstants.USER_ID, Long.class, Long.class ),
            new ContextValue( WorkflowContextConstants.TASK_COMMENTS, String.class, String.class ),
            new ContextValue( WorkflowContextConstants.COMPANY_ID, Long.class, Long.class ),
            new ContextValue( WorkflowContextConstants.ENTRY_CLASS_PK, Long.class, Long.class ),
            new ContextValue( WorkflowContextConstants.TRANSITION_NAME, String.class, String.class ),
            new ContextValue( WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES, List.class, List.class ),
        };

        for (ContextValue cv : contextValues)
        {
            configManager.addContextValue( cv, project );
        }

        super.openEditor( sapphirePart, modelElement, valueProperty );
    }

}
