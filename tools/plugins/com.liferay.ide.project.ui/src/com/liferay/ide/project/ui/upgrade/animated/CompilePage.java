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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.upgrade.action.CompileAction;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
public class CompilePage extends Page
{

    public CompilePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, COMPILE_PAGE_ID, true );

        Button compileButton = new Button( this, SWT.PUSH );
        compileButton.setText( "Compile" );
        compileButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                Action compile = new CompileAction( "compile", UIUtil.getActiveShell() );
                compile.run();
            }
        } );
    }

    @Override
    public String getDescriptor()
    {
        return "This step will try to package your upgraded projects to see if it can run successfully.\n" +
            "If it failed, you can see error logs in console view.\n";
    }

    @Override
    public String getPageTitle()
    {
        return "Compile";
    }

}
