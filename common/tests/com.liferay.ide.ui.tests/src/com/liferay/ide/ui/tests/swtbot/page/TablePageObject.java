
package com.liferay.ide.ui.tests.swtbot.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

/**
 * @author Li Lu
 */
public class TablePageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    public TablePageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public TablePageObject( SWTBot bot, int index )
    {

        super( bot, index );
    }

    protected SWTBotTable getWidget()
    {
        if( label != null )
            return bot.tableWithLabel( label, 0 );
        return bot.table( index );

    }

    public void click( int row, int column )
    {
        getWidget().click( row, column );
    }

    public void click( String itemText )
    {
        getWidget().getTableItem( itemText ).click();

    }

    public void doubleClick( int row, int column )
    {
        getWidget().doubleClick( row, column );
    }

    public void click( int row )
    {
        getWidget().getTableItem( row ).click();
    }

    public boolean containsItem( String item )
    {
        SWTBotTable table = getWidget();
        return table.containsItem( item );
    }

}
