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
package com.liferay.ide.project.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.ui.DelayedTasksExecutor;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * @author Gregory Amerson
 *
 * This class can be removed once https://bugs.eclipse.org/bugs/show_bug.cgi?id=422437 is finished
 */
public class SafeSapphireDialog extends SapphireDialog
{

    public SafeSapphireDialog( Shell shell, Element element, Reference<DialogDef> definition )
    {
        super( shell, element, definition );
    }

    @Override
    protected void okPressed()
    {
        DelayedTasksExecutor.sweep();

        final Status status = element().validation();

        if( status.severity() == Severity.ERROR )
        {
            MessageDialog.openError( getParentShell(), "Validation Error", status.message() );

            return;
        }

        super.okPressed();
    }

}
