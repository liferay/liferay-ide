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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class KaleoEditorPaletteFactory
{

    public static PaletteRoot createPalette( AbstractUIPlugin bundle, String folderName, ImageDescriptor entryImage )
    {
        PaletteRoot root = new PaletteRoot();

        try
        {
            File paletteFolder = getPaletteFolder( bundle, folderName );

            if( paletteFolder != null )
            {
                for( File file : paletteFolder.listFiles() )
                {
                    createPaletteEntries( root, file, entryImage );
                }
            }
        }
        catch( Exception e )
        {
        }

        return root;
    }

    private static void createPaletteEntries( PaletteContainer container, File paletteFile, ImageDescriptor image )
    {
        if( paletteFile.isDirectory() )
        {
            PaletteContainer newDrawer = new PaletteDrawer( paletteFile.getName() );

            for( File file : paletteFile.listFiles() )
            {
                createPaletteEntries( newDrawer, file, image );
            }

            container.add( newDrawer );
        }
        else
        {
            CreationFactory factory = new ScriptCreationFactory( paletteFile );

            String label = new Path( paletteFile.getName() ).removeFileExtension().toPortableString();

            CombinedTemplateCreationEntry entry =
                new CombinedTemplateCreationEntry( label, label, factory, image, image );
            // entry.setToolClass( CreationTool.class );
            container.add( entry );
        }
    }

    private static File getPaletteFolder( AbstractUIPlugin bundle, String folderName )
    {
        try
        {
            return new File( FileLocator.toFileURL( bundle.getBundle().getEntry( folderName ) ).getFile() );
        }
        catch( IOException e )
        {
        }

        return null;
    }

}
