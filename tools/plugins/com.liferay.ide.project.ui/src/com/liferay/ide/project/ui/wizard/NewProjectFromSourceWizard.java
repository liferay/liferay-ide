/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.ui.ProjectUIPlugin;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 */
public class NewProjectFromSourceWizard extends LiferayProjectImportWizard
{

    public NewProjectFromSourceWizard()
    {
        this( null );
    }

    public NewProjectFromSourceWizard( IDataModel dataModel )
    {
        super( dataModel );

        setWindowTitle( Msgs.newLiferayProject );
        setDefaultPageImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/plugin_project.png" ) ); //$NON-NLS-1$
        setNeedsProgressMonitor( true );
    }

    private static class Msgs extends NLS
    {
        public static String newLiferayProject;

        static
        {
            initializeMessages( NewProjectFromSourceWizard.class.getName(), Msgs.class );
        }
    }
}
