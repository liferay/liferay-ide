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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Simon Jiang
 */
public class LiferayUpgradeImportNextActionPart extends  AbstractLiferayUpgradeNavigatorPart
{
    private final String buttonLabel = "&Next";
    
    

    protected CodeUpgradeOp op()
    {
        return getLocalModelElement().nearest( CodeUpgradeOp.class );
    }
    
    @Override
    protected void createButton( Composite composite, Presentation parent )
    {

        button = createButtonField( composite );
        button.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                setOutlineSelection( parent, STEP_IMPORT_PROJECT );
            }
            
        } );
        button.setText( buttonLabel );

    }
}
