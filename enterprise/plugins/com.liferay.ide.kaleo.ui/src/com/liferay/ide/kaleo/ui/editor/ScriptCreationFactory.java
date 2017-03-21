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

import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Gregory Amerson
 */
public class ScriptCreationFactory implements CreationFactory
{
    private File scriptFile;

    public ScriptCreationFactory( File file )
    {
        this.scriptFile = file;
    }

    public Object getNewObject()
    {
        String contents = FileUtil.readContents( scriptFile, true );

        if (contents.endsWith( "\n" ))
        {
            contents = contents.substring( 0, contents.length() - 1 );
        }

        return contents;
    }

    public Object getObjectType()
    {
        return File.class;
    }

}
