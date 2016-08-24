package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class TestCanvas extends Canvas
{

    public TestCanvas( Composite parent, int style )
    {
        super( parent, style );
        
        
        addPaintListener( new PaintListener()
        {

            @Override
            public void paintControl( PaintEvent e )
            {
                paint( e.gc );
            }


        } );
    }
    
    private void paint( GC gc )
    {
      gc.drawLine( 50, 50, 500, 600 );
        
    }
}
