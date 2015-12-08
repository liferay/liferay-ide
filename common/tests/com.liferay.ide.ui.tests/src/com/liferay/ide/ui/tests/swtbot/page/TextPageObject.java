
package com.liferay.ide.ui.tests.swtbot.page;

import com.liferay.ide.ui.tests.swtbot.condition.WidgetEnabledCondition;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

public class TextPageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    public TextPageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public void setText( String text )
    {
        AbstractSWTBot<? extends Widget> widget = getWidget();

        if( widget instanceof SWTBotText )
        {
            SWTBotText swtBotText = (SWTBotText) widget;

            bot.waitUntil( new WidgetEnabledCondition( swtBotText, true ) );

            swtBotText.setText( text );
        }
    }

    @Override
    protected AbstractSWTBot<?> getWidget()
    {
        return bot.textWithLabel( label );
    }

}
