/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *******************************************************************************/
package com.liferay.ide.layouttpl.ui.actions;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.ui.util.LayoutTemplatesFactory;
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


/**
 * @author Kuo Zhang
 */
public class AddLayoutTemplateActionHandlerFactory extends SapphireActionHandlerFactory
{
    private final static String ADD_LAYOUT_TEMPLATE_ACTION_ID = "LayoutTpl.Add.LayoutTemplate";
    private final static String ADD_LAYOUT_1_2_I_ACTION_HANDLER_ID = "Add.Layout.1_2_I.ActionHandler";
    private final static String ADD_LAYOUT_1_2_II_ACTION_HANDLER_ID = "Add.Layout.1_2_II.ActionHandler";
    private final static String ADD_LAYOUT_2_2_ACTION_HANDLER_ID = "Add.Layout.2_2.ActionHandler";
    private final static String ADD_LAYOUT_1_2_1_ACTION_HANDLER_ID = "Add.Layout.1_2_1.ActionHandler";
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
            actionHandlers.add( new Add_Layout_1_2_I_ActionHandler() );
            actionHandlers.add( new Add_Layout_1_2_II_ActionHandler() );
            actionHandlers.add( new Add_Layout_2_2_ActionHandler() );
            actionHandlers.add( new Add_Layout_1_2_1_ActionHandler() );
            return actionHandlers;
        }

        return null;
    }

    private class Add_Layout_1_2_I_ActionHandler extends SapphireActionHandler
    {
        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_LAYOUT_1_2_I_ACTION_HANDLER_ID );
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
                LayoutTemplatesFactory.add_Layout_1_2_I( element );
            }

            return null;
        }
    }

    private class Add_Layout_1_2_II_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );

            setId( ADD_LAYOUT_1_2_II_ACTION_HANDLER_ID );
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
                LayoutTemplatesFactory.add_Layout_1_2_II( element );
            }

            return null;
        }
    }

    private class Add_Layout_2_2_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_LAYOUT_2_2_ACTION_HANDLER_ID );
            setLabel();
        }

        protected void setLabel()
        {
            final String prefix = "Layout with 2 Rows "; 

            if( isBootstrapStyle )
            {
                super.setLabel( prefix + "((8, 4), (4, 8))" );
            }
            else
            {
                super.setLabel( prefix + "((70, 30), (30, 70))");
            }
        }

        @Override
        protected Object run( Presentation context )
        {
            LayoutTplElement element = context.part().getModelElement().nearest( LayoutTplElement.class );

            if( element.getPortletLayouts().size() == 0 || canOverride()  )
            {
                element.getPortletLayouts().clear();
                LayoutTemplatesFactory.add_Layout_2_2( element );
            }

            return null;
        }
    }

    private class Add_Layout_1_2_1_ActionHandler extends SapphireActionHandler
    {

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            setId( ADD_LAYOUT_1_2_1_ACTION_HANDLER_ID );
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
                LayoutTemplatesFactory.add_Layout_1_2_1( element );
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
