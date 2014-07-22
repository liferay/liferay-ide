
package com.liferay.ide.layouttpl.ui.actions;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;

public class AddRowTemplateActionHandlerFactory extends SapphireActionHandlerFactory
{
    private static final String ADD_2_I_COLUMNS_ACTION_HANDLER_ID = "Add.2_I_Columns.ActionHandler";
    private static final String ADD_2_II_COLUMNS_ACTION_HANDLER_ID = "Add.2_II_Columns.ActionHandler";
    private static final String ADD_2_III_COLUMNS_ACTION_HANDLER_ID = "Add.2_III_Columns.ActionHandler";
    private static final String ADD_3_COLUMNS_ACTION_HANDLER_ID = "Add.3_Columns.ActionHandler";

    private boolean isBootstrapStyle; 

    public AddRowTemplateActionHandlerFactory()
    {
        super();
    }

    @Override
    public void init( SapphireAction action, ActionHandlerFactoryDef def )
    {
        super.init( action, def );
        isBootstrapStyle = getModelElement().nearest( LayoutTplElement.class ).getBootstrapStyle().content();
    }

    @Override
    public List<SapphireActionHandler> create()
    {
        if( "LayoutTpl.Add.RowTemplate".equals( getAction().getId() ) )
        {
            ArrayList<SapphireActionHandler> actionHandlers = new ArrayList<SapphireActionHandler>();
            actionHandlers.add( new Add_2_I_Columns_ActionHandler() );
            actionHandlers.add( new Add_2_II_Columns_ActionHandler() );
            actionHandlers.add( new Add_2_III_Columns_ActionHandler() );
            actionHandlers.add( new Add_3_Columns_ActionHandler() );
            return actionHandlers;
        }

        return null;
    }

    private class Add_2_I_Columns_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_2_I_COLUMNS_ACTION_HANDLER_ID );
            setLabel();
        }
 
        protected void setLabel()
        {
            final String prefix = "Row with 2 Columns ";

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "(6, 6)" );
            }
            else
            {
                super.setLabel( prefix + "(50, 50)");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            CanAddPortletLayouts element = getModelElement().nearest( CanAddPortletLayouts.class );
            LayoutTplTemplatesFacotry.add_2_I_Columns( element );
            return null;
        }
    }

    private class Add_2_II_Columns_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_2_II_COLUMNS_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Row with 2 Columns ";

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "(4, 8)" );
            }
            else
            {
                super.setLabel( prefix + "(30, 70)");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            CanAddPortletLayouts element = getModelElement().nearest( CanAddPortletLayouts.class );
            LayoutTplTemplatesFacotry.add_2_II_Columns( element );
            return null;
        }
    }

    private class Add_2_III_Columns_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_2_III_COLUMNS_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Row with 2 Columns ";

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "(8, 4)" );
            }
            else
            {
                super.setLabel( prefix + "(70, 30)");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            CanAddPortletLayouts element = getModelElement().nearest( CanAddPortletLayouts.class );
            LayoutTplTemplatesFacotry.add_2_III_Columns( element );
            return null;
        }
    }

    private class Add_3_Columns_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_3_COLUMNS_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Row with 3 Columns ";

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "(4, 4, 4)" );
            }
            else
            {
                super.setLabel( prefix + "(33, 33, 33)" );
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            CanAddPortletLayouts element = getModelElement().nearest( CanAddPortletLayouts.class );
            LayoutTplTemplatesFacotry.add_3_Columns( element );
            return null;
        }
    }
}
