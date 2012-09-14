package com.liferay.ide.portal.ui.navigator;

import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import org.eclipse.core.resources.IWorkspaceRoot;


/**
 * @author Gregory Amerson
 */
public class PortalResourcesContentProvider extends AbstractNavigatorContentProvider
{
    
    public PortalResourcesContentProvider()
    {
        super();
    }

    public Object[] getChildren( Object parentElement )
    {
        return new Object[0];
    }

    public void dispose()
    {
    }

    public boolean hasChildren( Object element )
    {
        return false;
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        if( inputElement instanceof IWorkspaceRoot )
        {
            return new Object[] { new PortalResourcesRootNode( (IWorkspaceRoot) inputElement ) };
        }
        
        return new Object[0];
    }    
    
}
