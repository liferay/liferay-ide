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
    public static final String STEP_FIND_BREAKING_CHANGES = "Find Breaking Changes";
    public static final String STEP_IMPORT_PROJECT = "Import Project";
    public static final String STEP_WELCOME = "Welcome";

    protected CodeUpgradeOp op( Presentation context )
    {
        return context.part().getModelElement().nearest( CodeUpgradeOp.class );
    }

    protected void setOutlineSelection( Presentation context, String selection )
    {
        MasterDetailsEditorPagePart pagePart = getMasterPagePart( context );

        pagePart.outline().setSelection( selection );
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
