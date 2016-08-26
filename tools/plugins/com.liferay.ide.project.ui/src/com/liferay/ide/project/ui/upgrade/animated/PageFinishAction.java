package com.liferay.ide.project.ui.upgrade.animated;

public class PageFinishAction extends PageAction
{
    private boolean canFinish;
    
    public PageFinishAction( final boolean canFinish )
    {
        super();
        this.canFinish = canFinish;
        images[0] = SWTUtil.loadImage("yes.png");
        images[1] = SWTUtil.loadImage("yes_select.png");
        images[2] = SWTUtil.loadImage("yes_hover.png");
        images[3] = SWTUtil.loadImage("yes_big.png");
        images[4] = SWTUtil.loadImage("yes_badge.png");            
    }
    
    @Override
    protected boolean done()
    {
        return canFinish;
    }

}
