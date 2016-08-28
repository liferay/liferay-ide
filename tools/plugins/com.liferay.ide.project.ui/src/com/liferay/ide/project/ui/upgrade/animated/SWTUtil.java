package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class SWTUtil
{

    public static final Image loadImage( String name )
    {
        URL url = null;
        
        try
        {
            url  = ProjectUI.getDefault().getBundle().getEntry( "images/" + name );
        }
        catch( Exception e )
        {
        }

        ImageDescriptor imagedesc = ImageDescriptor.createFromURL( url );

        Image image = imagedesc.createImage();

        return image;
    }
    
    public static Rectangle drawImage(GC gc, Image image, int cX, int cY)
    {
      Rectangle bounds = image.getBounds();
      cX -= bounds.width / 2;
      cY -= bounds.height / 2;
      gc.drawImage(image, cX, cY);
      return new Rectangle(cX, cY, bounds.width, bounds.height);
    }
}
