
package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.widgets.Composite;

public class Page extends Composite
{
    public Page( Composite parent, int style )
    {
        super( parent, style );
    }
    public static final int NONE = -1;

    private int pageId;
    
    private int index;

    private String title = "title";

    private int choice = NONE;
    
    protected PageAction[] actions;
    
    private PageAction selectedAction;

    public final int getIndex()
    {
      return index;
    }
    
    public void setIndex( int index )
    {
        this.index = index;
    }
    
    public void setTitle( String title )
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return this.title;
    }

    public PageAction[] getActions()
    {
        return this.actions;
    }

    public final void setActions(PageAction[] actions)
    {
      this.actions = actions;
    }

    protected  boolean showBackPage()
    {
        return false;
    }
    protected  boolean showNextPage()
    {
        return false;
    }

    @Override
    public boolean equals( Object obj )
    {
        Page comp = (Page)obj;
        return this.pageId == comp.pageId;
    }
    
    public PageAction getSelectedAction()
    {
        return selectedAction;
    }

    
    public void setSelectedAction( PageAction selectedAction )
    {
        this.selectedAction = selectedAction;
    }
    
}
