
package com.liferay.ide.layouttpl.ui.actions;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;

public class AddLayoutTemplateActionHandlerFactory extends SapphireActionHandlerFactory
{
    private final static String ADD_LAYOUT_TEMPLATE_ACTION_ID = "LayoutTpl.Add.LayoutTemplate";
    private final static String ADD_1_2_I_ROWS_ACTION_HANDLDER_ID = "Add.1_2_I_Rows.ActionHandler";
    private final static String ADD_1_2_II_ROWS_ACTION_HANDLER_ID = "Add.1_2_II_Rows.ActionHandler";
    private final static String ADD_2_2_ROWS_ACTION_HANDLER_ID = "Add.2_2_Rows.ActionHandler";
    private final static String ADD_1_2_1_ROWS_ACTION_HANDLER_ID = "Add.1_2_1_Rows.ActionHandler";
    private boolean isBootstrapStyle;

    public AddLayoutTemplateActionHandlerFactory()
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
        if( ADD_LAYOUT_TEMPLATE_ACTION_ID.equals( getAction().getId() ) )
        {
            ArrayList<SapphireActionHandler> actionHandlers = new ArrayList<SapphireActionHandler>();
            actionHandlers.add( new Add_1_2_I_Rows_ActionHandler() );
            actionHandlers.add( new Add_1_2_II_Rows_ActionHandler() );
            actionHandlers.add( new Add_2_2_Rows_ActionHandler() );
            actionHandlers.add( new Add_1_2_1_Rows_ActionHandler() );
            return actionHandlers;
        }

        return null;
    }

    private class Add_1_2_I_Rows_ActionHandler extends SapphireActionHandler
    {
        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_1_2_I_ROWS_ACTION_HANDLDER_ID );
            setLabel();
        }
 
        protected void setLabel()
        {
            final String prefix = "Layout with 2 Rows "; 

            if( isBootstrapStyle )
            {
               super.setLabel( prefix + "(12, (4, 8))" );
            }
            else
            {
                super.setLabel( prefix + "(100, (30, 70))");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            LayoutTplElement element = context.part().getModelElement().nearest( LayoutTplElement.class );

            if( element.getPortletLayouts().size() == 0 || canOverride() )
            {
                element.getPortletLayouts().clear();
                LayoutTplTemplatesFacotry.add_1_2_I_Rows( element );
            }

            return null; 
        }
    }

    private class Add_1_2_II_Rows_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );

            setId( ADD_1_2_II_ROWS_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Layout with 2 Rows "; 

            if( isBootstrapStyle )
            {
               super.setLabel( prefix + "(12, (8, 4))" );
            }
            else
            {
                super.setLabel( prefix + "(100, (70, 30))");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            LayoutTplElement element = context.part().getModelElement().nearest( LayoutTplElement.class );

            if( element.getPortletLayouts().size() == 0 || canOverride() )
            {
                element.getPortletLayouts().clear();
                LayoutTplTemplatesFacotry.add_1_2_II_Rows( element );
            }

            return null;
        }
    }

    private class Add_2_2_Rows_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_2_2_ROWS_ACTION_HANDLER_ID ); 
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Layout with 2 Rows "; 

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "((4, 8), (8, 4))" );
            }
            else
            {
                super.setLabel( prefix + "((30, 70), (70, 30))");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            LayoutTplElement element = context.part().getModelElement().nearest( LayoutTplElement.class );

            if( element.getPortletLayouts().size() == 0 || canOverride()  )
            {
                element.getPortletLayouts().clear();
                LayoutTplTemplatesFacotry.add_2_2_Rows( element );
            }

            return null;
        }
    }

    private class Add_1_2_1_Rows_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_1_2_1_ROWS_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Layout with 3 Rows ";

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "(12, (6, 6), 12))" );
            }
            else
            {
                super.setLabel( prefix + "(100, (50, 50), 100)");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            LayoutTplElement element = context.part().getModelElement().nearest( LayoutTplElement.class );

            if( element.getPortletLayouts().size() == 0 || canOverride() )
            {
                element.getPortletLayouts().clear();
                LayoutTplTemplatesFacotry.add_1_2_1_Rows( element );
            }

            return null;
        }
    }

    private boolean canOverride()
    {
        return MessageDialog.openQuestion( UIUtil.getActiveShell(), "Warning", Msgs.addLayoutTplWarningMsg );
    }

    private static class Msgs extends NLS
    {
        public static String addLayoutTplWarningMsg;

        static
        {
            initializeMessages( AddLayoutTemplateActionHandlerFactory.class.getName(), Msgs.class );
        }
    }
}
