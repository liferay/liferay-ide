
package com.liferay.ide.ui.tests.swtbot.page.impl;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;

import com.liferay.ide.ui.tests.swtbot.condition.WidgetEnabledCondition;
import com.liferay.ide.ui.tests.swtbot.page.ICheckBoxPageObject;

public class CheckBoxPageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
    implements ICheckBoxPageObject
{

    public CheckBoxPageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    @Override
    protected AbstractSWTBot<?> getWidget()
    {
        return bot.checkBox( label );
    }

    @Override
    public void select()
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        if( widget instanceof SWTBotCheckBox )
        {
            SWTBotCheckBox checkBox = (SWTBotCheckBox) widget;
            bot.waitUntil( new WidgetEnabledCondition( checkBox, true ) );
            checkBox.select();
        }
    }

    @Override
    public void deselect()
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        if( widget instanceof SWTBotCheckBox )
        {
            SWTBotCheckBox checkBox = (SWTBotCheckBox) widget;
            bot.waitUntil( new WidgetEnabledCondition( checkBox, true ) );
            checkBox.deselect();;
        }
    }

}
