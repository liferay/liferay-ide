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

import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author Gregory Amerson
 */
public class ColorScriptPropertyEditorRenderer extends ScriptPropertyEditorRenderer
{

    public static final class Factory extends PropertyEditorPresentationFactory
    {
        @Override
        public PropertyEditorPresentation create( PropertyEditorPart part, SwtPresentation parent, Composite composite )
        {
            return new ColorScriptPropertyEditorRenderer( part, parent, composite );
        }
    }

    public ColorScriptPropertyEditorRenderer( PropertyEditorPart part, SwtPresentation parent, Composite composite )
    {
        super( part, parent, composite );
    }

    @Override
    protected IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IEditorPart editorPart = null;

        try
        {
            editorPart = new TextEditor();
            editorPart.init( editorSite, editorInput );
        }
        catch( PartInitException e )
        {
            KaleoUI.logError( "Could not initialize color editor", e );

            try
            {
                editorPart = new TextEditor();
                editorPart.init( editorSite, editorInput );
            }
            catch( PartInitException e1 )
            {
            }
        }

        return editorPart;
    }
}
