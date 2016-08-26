package com.liferay.ide.project.ui.upgrade.animated;


public class PageSkipAction extends PageAction
{
    private boolean canSkip;
    
    protected PageSkipAction( final boolean canSkip )
    {
        super();
        this.canSkip = canSkip;
        images[0] = SWTUtil.loadImage("no.png");
        images[1] = SWTUtil.loadImage("no_select.png");
        images[2] = SWTUtil.loadImage("no_hover.png");
        images[3] = SWTUtil.loadImage("no_big.png");
        images[4] = SWTUtil.loadImage("no_badge.png");
    }

    @Override
    protected boolean done()
    {
        return canSkip;
    }
}
