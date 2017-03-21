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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.UniversalConversionService;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
public class EditorSiteAdapterService extends UniversalConversionService
{

    @Override
    public <A> A convert( Object object, Class<A> adapterType )
    {
        A retval = null;

        if( IEditorSite.class.equals( adapterType ) )
        {
            SapphirePart sapphirePart = context( SapphirePart.class );
            Element localElement = sapphirePart.getLocalModelElement();
            ITextEditor editor = localElement.adapt( ITextEditor.class );

            IWorkbenchPartSite editorSite = editor.getSite();

            if( editorSite instanceof IEditorSite )
            {
                retval = adapterType.cast( editorSite );
            }
        }

        return retval;
    }

}
