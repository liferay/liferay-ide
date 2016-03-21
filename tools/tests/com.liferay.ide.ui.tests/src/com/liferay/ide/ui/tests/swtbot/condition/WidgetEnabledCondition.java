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

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;

/**
 * ICondition implementation to wait for the enabling/disabling of a widget This is useful when enabling/disabling a
 * certain widget takes a while after the initiating user action has been carried out
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public class WidgetEnabledCondition implements ICondition
{

    private final AbstractSWTBot<? extends Widget> widget;
    private final boolean enabled;

    public WidgetEnabledCondition( AbstractSWTBot<? extends Widget> widget, boolean enabled )
    {
        this.widget = widget;

        this.enabled = enabled;
    }

    public String getFailureMessage()
    {
        if( enabled )
        {
            return "widget not enabled";
        }
        else
        {
            return "widget not disabled";
        }
    }

    public void init( SWTBot bot )
    {
    }

    public boolean test() throws Exception
    {
        return widget.isEnabled() == enabled;
    }
}
