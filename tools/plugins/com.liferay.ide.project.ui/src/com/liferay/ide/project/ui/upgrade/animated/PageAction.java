
package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class PageAction
{

    protected Image[] images;

    protected abstract boolean done();

    public PageAction()
    {
        images = new Image[5];
    }
    
    public Point getSize()
    {
      Rectangle bounds = images[2].getBounds();
      return new Point(bounds.width, bounds.height);
    }
    
    
    public Image[] getImages()
    {
        return this.images;
    }
    
    public Image getBageImage()
    {
        return this.images[4];
    }
    
}
