/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * wait condition, which does a refresh on a tree node and waits for a specific sub node to appear after refresh this
 * conditions assumes that the parentTreeItem has a context menu which does a refresh this is useful in situations where
 * sub nodes only become visible after refresh, e.q. package explorer eclipse ide
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class RefreshForSubnodeCondition implements ICondition
{

    private final String itsSubnodeText;

    private SWTBotTreeItem itsParentItem;

    private final String itsRefreshContextMenuText;

    /**
     * @param parentItem
     *            - the parent item
     * @param subnodeText
     *            - the text for the expected child tree item
     * @param refreshContextMenuText
     *            - the text for the refresh context menu item of the parent item
     */
    public RefreshForSubnodeCondition( SWTBotTreeItem parentItem, String subnodeText, String refreshContextMenuText )
    {
        itsParentItem = parentItem;

        itsSubnodeText = subnodeText;

        itsRefreshContextMenuText = refreshContextMenuText;
    }

    public String getFailureMessage()
    {
        return "sub node " + itsSubnodeText + " not found after refresh";
    }

    public void init( SWTBot bot )
    {
    }

    public boolean test() throws Exception
    {
        boolean ret = false;

        for( String itemText : itsParentItem.getNodes() )
        {
            if( itemText.equals( itsSubnodeText ) )
            {
                ret = true;

                break;
            }
        }

        if( !ret )
        {
            itsParentItem.contextMenu( itsRefreshContextMenuText ).click();

            itsParentItem = itsParentItem.select().expand();
        }

        return ret;
    }
}
