/**
 * 
 */

package com.liferay.ide.portlet.ui.action.filters;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;

/**
 * @author kamesh.sampath
 */
public class AddPortletActionFilter extends SapphireActionHandlerFilter
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandlerFilter#check(org.eclipse.sapphire.ui.SapphireActionHandler)
     */
    @Override
    public boolean check( SapphireActionHandler handler )
    {
        boolean canHandle = true;
        // IModelElement iModelElement = handler.getModelElement();

        // System.out.println( String.format(
        // "AddPortletActionFilter.check() - Action Handler[Action-ID=%s, Handler-ID=%s,Model-Element=%s]",
        // handler.getAction().getId(), handler.getId(), iModelElement.getClass().getName() ) );

        if( "Sapphire.Add.IPortlet".equals( handler.getId() ) && "Sapphire.Add".equals( handler.getAction().getId() ) )
        {
            return false;
        }

        return canHandle;
    }
}
