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
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;
import com.liferay.ide.kaleo.ui.editor.WorkflowTextEditor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

/**
 * @author Gregory Amerson
 */
public class TextEditorHelper extends AbstractKaleoEditorHelper
{

    @Override
    public IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IEditorPart editorPart = null;

        try
        {
            editorPart = new WorkflowTextEditor();

            editorPart.init( editorSite, editorInput );
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Could not create text editor.", e );

            editorPart = super.createEditorPart( editorInput, editorSite );
        }

        return editorPart;
    }

}
