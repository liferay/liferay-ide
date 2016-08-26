package com.liferay.ide.project.ui.upgrade.animated;

public class PageNavigateEvent
{
    protected Page targetPage;
    
    public Page getTargetPage()
    {
        return this.targetPage;
    }
   
    public void setTargetPage( Page targetPage )
    {
        this.targetPage = targetPage;
    }
}
