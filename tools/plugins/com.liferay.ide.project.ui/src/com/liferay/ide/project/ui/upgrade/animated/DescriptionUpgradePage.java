package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DescriptionUpgradePage extends Page
{
    
    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };
    
    public DescriptionUpgradePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        
        this.setLayout( new FillLayout() );
        
        Button button = new Button(this, SWT.PUSH);

        button.setText( "page1" );
        
        button.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                MessageDialog.openConfirm( UIUtil.getActiveShell(), "page1", "page1" );
            }
            
            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
                
            }
        } );

        setActions( actions );
    }

    @Override
    protected boolean showBackPage()
    {
        return false;
    }

    @Override
    protected boolean showNextPage()
    {
        return true;
    }
}
