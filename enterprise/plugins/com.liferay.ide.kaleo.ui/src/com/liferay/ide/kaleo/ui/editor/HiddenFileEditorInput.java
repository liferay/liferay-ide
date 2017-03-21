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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Gregory Amerson
 */
public class HiddenFileEditorInput extends FileEditorInput
{

    public HiddenFileEditorInput( IFile file )
    {
        super( file );
    }

    @Override
    public String getName()
    {
        String retval = super.getName();

        if( retval.startsWith( "." ) )
        {
            retval = retval.substring( 1, retval.length() );
        }

        return retval;
    }

    @Override
    public String getToolTipText()
    {
        return this.getFile().getName();
    }

}
