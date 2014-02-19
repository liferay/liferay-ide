/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.service.ui.actions;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;

/**
 * @author Kuo Zhang
 */
public class AddDefaultColumnsAction extends SapphireActionHandler
{

    public AddDefaultColumnsAction()
    {
        super();
    }

    @Override
    protected Object run( Presentation context )
    {
        final Element element = context.part().getLocalModelElement();

        if( element instanceof Entity )
        {
            final String entityName = ( (Entity) element ).getName().content();

            if( CoreUtil.isNullOrEmpty( entityName ) )
            {
                final String title = "Add Liferay Default Columns";
                final String message = "The entity name must be specified.";

                MessageDialog.openInformation( UIUtil.getActiveShell(), title, message );
            }
            else
            {
                final IFile serviceXML = element.adapt( IFile.class );

                new ServiceBuilderDescriptorHelper( serviceXML.getProject() ).addDefaultColumns( entityName );
            }
        }

        return null;
    }

}
