package com.liferay.ide.project.ui.upgrade.animated;

public class PageActionEvent
{
    private Page targetPage;
    
    private PageAction action;
    
    public Page getTargetPage()
    {
        return this.targetPage;
    }
   
    public void setTargetPage( Page targetPage )
    {
        this.targetPage = targetPage;
    }
    
    public void setAction( PageAction action)
    {
        this.action = action;
    }
    
    public PageAction getAction()
    {
        return this.action;
    }
}