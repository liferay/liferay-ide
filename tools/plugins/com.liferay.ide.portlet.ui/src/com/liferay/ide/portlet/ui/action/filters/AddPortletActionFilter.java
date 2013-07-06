/**
 * 
 */

package com.liferay.ide.portlet.ui.action.filters;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;

/**
 * @author Kamesh Sampath
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
        // Element Element = handler.getModelElement();

        // System.out.println( String.format(
        // "AddPortletActionFilter.check() - Action Handler[Action-ID=%s, Handler-ID=%s,Model-Element=%s]",
        // handler.getAction().getId(), handler.getId(), Element.getClass().getName() ) );

        if( "Sapphire.Add.IPortlet".equals( handler.getId() ) && "Sapphire.Add".equals( handler.getAction().getId() ) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return false;
        }

        return canHandle;
    }
}
