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

import com.liferay.ide.kaleo.ui.AbstractKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class DefaultScriptEditorHelper extends AbstractKaleoEditorHelper
{

    public IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IEditorPart editorPart = null;

        try
        {
            final String fileName = editorInput.getName();

            IContentType contentType = null;

            // if ( editorInput.getProperty().hasAnnotation( ContentType.class ) )
            // {
            // String contentTypeId = editorInput.getProperty().getAnnotation( ContentType.class ).contentTypeId();
            // contentType = Platform.getContentTypeManager().getContentType( contentTypeId );
            // }
            // else
            {
                IContentDescription contentDescription =
                    Platform.getContentTypeManager().getDescriptionFor(
                        editorInput.getStorage().getContents(), fileName, IContentDescription.ALL );

                if( contentDescription != null )
                {
                    contentType = contentDescription.getContentType();
                }
            }

            if( contentType == null )
            {
                // use basic text content type
                contentType = Platform.getContentTypeManager().getContentType( "org.eclipse.core.runtime.text" );
            }

            IEditorDescriptor defaultEditorDescriptor =
                PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor( fileName, contentType );
            String editorId = defaultEditorDescriptor.getId();
            IConfigurationElement[] editorConfigs =
                Platform.getExtensionRegistry().getConfigurationElementsFor( "org.eclipse.ui.editors" );

            for( IConfigurationElement config : editorConfigs )
            {
                if( editorId.equals( config.getAttribute( "id" ) ) )
                {
                    editorPart = (IEditorPart) config.createExecutableExtension( "class" );
                    break;
                }
            }

            editorPart.init( editorSite, editorInput );
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Could not create default script editor.", e );
        }

        return editorPart;
    }

    public String getEditorId()
    {
        IContentType contentType = Platform.getContentTypeManager().getContentType( "org.eclipse.core.runtime.text" );

        IEditorDescriptor defaultEditorDescriptor =
            PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor( "default.txt", contentType );

        return defaultEditorDescriptor.getId();
    }

}
