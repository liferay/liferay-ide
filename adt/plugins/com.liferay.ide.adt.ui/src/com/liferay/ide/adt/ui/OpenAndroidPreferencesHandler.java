/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.adt.ui;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.swt.presentation.SwtPresentation;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * @author Gregory Amerson
 */
public class OpenAndroidPreferencesHandler extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        PreferencesUtil.createPreferenceDialogOn(
            ( (SwtPresentation) context ).shell(), AndroidPreferencePage.ID, new String[] { AndroidPreferencePage.ID },
            null ).open();

        return Status.createOkStatus();
    }

}
