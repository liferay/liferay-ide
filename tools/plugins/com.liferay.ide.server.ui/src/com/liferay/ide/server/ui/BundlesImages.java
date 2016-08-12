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

package com.liferay.ide.server.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class BundlesImages
{
    public static final Image IMG_LOADING = createImage( "e16/waiting_16x16.gif" );
    public static final Image IMG_BUNDLE = createImage( "e16/bundle.png" );
    public static final Image IMG_BUNDLE_ERROR = createImage( "e16/bundle_error.png" );
    public static final Image IMG_BUNDLES_FOLDER = createImage( "e16/bundlefolder.png" );

    private static ImageDescriptor create( String key )
    {
        try
        {
            ImageDescriptor imageDescriptor = createDescriptor( key );
            ImageRegistry imageRegistry = getImageRegistry();

            if( imageRegistry != null )
            {
                imageRegistry.put( key, imageDescriptor );
            }

            return imageDescriptor;
        }
        catch( Exception ex )
        {
            LiferayServerUI.logError( ex );
            return null;
        }
    }

    private static ImageDescriptor createDescriptor( String image )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( LiferayServerUI.PLUGIN_ID, "icons/" + image );
    }

    private static Image createImage( String key )
    {
        create( key );
        ImageRegistry imageRegistry = getImageRegistry();

        return imageRegistry == null ? null : imageRegistry.get( key );
    }

    private static ImageRegistry getImageRegistry()
    {
        LiferayServerUI plugin = LiferayServerUI.getDefault();
        return plugin == null ? null : plugin.getImageRegistry();
    }

}
