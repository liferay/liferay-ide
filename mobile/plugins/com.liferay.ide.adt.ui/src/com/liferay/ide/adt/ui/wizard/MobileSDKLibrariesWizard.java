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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.modeling.FileResourceStore;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class MobileSDKLibrariesWizard extends SapphireWizard<MobileSDKLibrariesOp>
{
    private static final String WIZARD_SETTINGS_FOLDER = ".metadata/.plugins/com.liferay.ide.adt.ui/wizards";

    public MobileSDKLibrariesWizard( final IJavaProject project )
    {
        super( initElement( project ), DefinitionLoader.sdef( MobileSDKLibrariesWizard.class ).wizard( "wizard" ) );

        this.element().setProjectName( project.getProject().getName() );
    }

    private static MobileSDKLibrariesOp initElement( IJavaProject project )
    {
        MobileSDKLibrariesOp op = null;

        try
        {
            final String fileName = computeSettingsFileName( project );

            if( fileName != null )
            {
                final File settingsFile = getWizardPersistenceFile( fileName );
                final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore( settingsFile ) );
                op = MobileSDKLibrariesOp.TYPE.instantiate( new RootXmlResource( resourceStore ) );

                op.getUrl().clear();
                op.getOmniUsername().clear();
                op.getOmniPassword().clear();
            }
        }
        catch( Exception e )
        {
            ADTUI.logError( "Unable to load MobileSDKLibrariesOp element", e );
        }

        return op;
    }

    private static String computeSettingsFileName( final IJavaProject project )
    {
        // Compute a unique path for the settings file based on a hash associated with the project
        final String uniquePath =
            MobileSDKLibrariesWizard.class.getName() + project.getProject().getLocationURI().getPath();

        return uniquePath != null ? MiscUtil.createStringDigest( uniquePath ) : null;
    }

    private static File getWizardPersistenceFile( String fileName ) throws CoreException
    {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final File layoutFolder = new File( workspaceRoot.getLocation().toFile(), WIZARD_SETTINGS_FOLDER );

        if( !layoutFolder.exists() )
        {
            FileUtil.mkdirs( layoutFolder );
        }

        final File layoutFile = new File( layoutFolder, fileName );

        return layoutFile;
    }

}
