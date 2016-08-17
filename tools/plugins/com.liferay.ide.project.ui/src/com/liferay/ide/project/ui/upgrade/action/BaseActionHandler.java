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
 *
 *******************************************************************************/

package com.liferay.ide.project.ui.upgrade.action;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPagePart;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;

/**
 * @author Terry Jia
 */
public abstract class BaseActionHandler extends SapphireActionHandler
{

    public static final String STEP_BUILD_SERVICE = "Build Service";
    public static final String STEP_COMPILE = "Compile";
    public static final String STEP_CONFIG_SOURCE = "Config Source";
    public static final String STEP_CUSTOME_JSP = "Custom Jsp";
    public static final String STEP_DESCRIPTORS = "Descriptors";
    public static final String STEP_DEPLOY = "Deploy";
    public static final String STEP_EXT = "Ext";
    public static final String STEP_THEME = "Theme";
    public static final String STEP_FIND_BREAKING_CHANGES = "Find Breaking Changes";
    public static final String STEP_IMPORT_PROJECT = "Import Project";
    public static final String STEP_LAYOUT_TEMPLATE = "Layout Template";
    public static final String STEP_WELCOME = "Welcome";

    public static final String[] STEPS = { STEP_WELCOME, STEP_CONFIG_SOURCE, STEP_IMPORT_PROJECT, STEP_DESCRIPTORS,
        STEP_FIND_BREAKING_CHANGES, STEP_BUILD_SERVICE, STEP_LAYOUT_TEMPLATE, STEP_CUSTOME_JSP, STEP_EXT, STEP_THEME,
        STEP_COMPILE, STEP_DEPLOY };

    protected CodeUpgradeOp op( Presentation context )
    {
        return context.part().getModelElement().nearest( CodeUpgradeOp.class );
    }

    protected void setNextOutlineSelection( Presentation context, String selection )
    {
        CodeUpgradeOp op = op( context );

        MasterDetailsEditorPagePart pagePart = getMasterPagePart( context );

        int index = -1;

        for( int i = 0; i < STEPS.length; i++ )
        {
            if( STEPS[i].equals( selection ) )
            {
                index = i;
                break;
            }
        }

        String nextStep = STEP_COMPILE;

        for( int i = index; i < STEPS.length; i++ )
        {
            nextStep = STEPS[i + 1];

            if( nextStep.equals( STEP_CONFIG_SOURCE ) || nextStep.equals( STEP_IMPORT_PROJECT ) ||
                nextStep.equals( STEP_COMPILE ) || nextStep.equals( STEP_DEPLOY ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_DESCRIPTORS ) &&
                ( ( op.getHasHook() != null && op.getHasHook().content().booleanValue() ) ||
                    ( op.getHasPortlet() != null && op.getHasPortlet().content().booleanValue() ) ||
                    ( op.getHasServiceBuilder() != null && op.getHasServiceBuilder().content().booleanValue() ) ||
                    ( op.getHasLayout() != null && op.getHasLayout().content().booleanValue() ) ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_FIND_BREAKING_CHANGES ) &&
                ( ( op.getHasHook() != null && op.getHasHook().content().booleanValue() ) ||
                    ( op.getHasPortlet() != null && op.getHasPortlet().content().booleanValue() ) ||
                    ( op.getHasServiceBuilder() != null && op.getHasServiceBuilder().content().booleanValue() ) ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_BUILD_SERVICE ) &&
                ( op.getHasServiceBuilder() != null && op.getHasServiceBuilder().content().booleanValue() ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_LAYOUT_TEMPLATE ) &&
                ( op.getHasLayout() != null && op.getHasLayout().content().booleanValue() ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_CUSTOME_JSP ) &&
                ( op.getHasHook() != null && op.getHasHook().content().booleanValue() ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_EXT ) &&
                ( op.getHasExt() != null && op.getHasExt().content().booleanValue() ) )
            {
                break;
            }
            else if( nextStep.equals( STEP_THEME ) &&
                ( op.getHasTheme() != null && op.getHasTheme().content().booleanValue() ) )
            {
                break;
            }
        }

        pagePart.outline().setSelection( nextStep );
    }

    private MasterDetailsEditorPagePart getMasterPagePart( Presentation presentation )
    {
        try
        {
            MasterDetailsEditorPagePart pagePart = (MasterDetailsEditorPagePart) presentation.part();

            return pagePart;
        }
        catch( Exception e )
        {
        }

        return getMasterPagePart( presentation.parent() );
    }

}
