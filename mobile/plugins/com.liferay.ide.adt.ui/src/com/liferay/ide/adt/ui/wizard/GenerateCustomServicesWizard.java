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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.GenerateCustomServicesOp;
import com.liferay.ide.adt.core.model.ServerInstance;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.FileResourceStore;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class GenerateCustomServicesWizard extends SapphireWizard<GenerateCustomServicesOp>
{
    private static final String OK_STATUS = "OK";

    private static final String WIZARD_SETTINGS_FOLDER = ".metadata/.plugins/com.liferay.ide.adt.ui/wizards";

    protected static void applySettings( GenerateCustomServicesOp targetOp )
    {
        final GenerateCustomServicesWizardSettings settings = loadSettings( targetOp );

        for( ServerInstance instance : settings.getPreviousServerInstances() )
        {
            targetOp.getPreviousServerInstances().insert().copy( instance );
        }
    }

    private static String computeSettingsFileName( final IJavaProject project )
    {
        // Compute a unique path for the settings file based on a hash associated with the project
        final String uniquePath =
            GenerateCustomServicesWizard.class.getName() + project.getProject().getLocationURI().getPath();

        return uniquePath != null ? MiscUtil.createStringDigest( uniquePath ) : null;
    }

    private static boolean containsInstance( GenerateCustomServicesOp sourceOp, ElementList<ServerInstance> instances )
    {
        for( ServerInstance instance : instances )
        {
            if( instance.getUrl().content().equals( sourceOp.getUrl().content() ) )
            {
                return true;
            }
        }

        return false;
    }

    protected static IJavaProject getJavaProject( String projectName )
    {
        IJavaProject retval = null;

        final IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project != null && project.exists() )
        {
            retval = JavaCore.create( project );
        }

        return retval;
    }

    private static File getWizardPersistenceFile( String fileName ) throws CoreException
    {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final File layoutFolder = new File( workspaceRoot.getLocation().toFile(), WIZARD_SETTINGS_FOLDER );

        if( ! layoutFolder.exists() )
        {
            FileUtil.mkdirs( layoutFolder );
        }

        final File layoutFile = new File( layoutFolder, fileName );

        return layoutFile;
    }

    private static GenerateCustomServicesOp initElement( IJavaProject project )
    {
        GenerateCustomServicesOp op = GenerateCustomServicesOp.TYPE.instantiate();

        op.setProjectName( project.getProject().getName() );
        applySettings( op );

        return op;
    }

    private static GenerateCustomServicesWizardSettings loadSettings( GenerateCustomServicesOp op )
    {
        final IJavaProject project = getJavaProject( op.getProjectName().content() );

        GenerateCustomServicesWizardSettings retval = null;

        try
        {
            final String fileName = computeSettingsFileName( project );

            if( fileName != null )
            {
                final File settingsFile = getWizardPersistenceFile( fileName );
                final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore( settingsFile ) );

                retval = GenerateCustomServicesWizardSettings.TYPE.instantiate( new RootXmlResource( resourceStore ) );
            }
        }
        catch( Exception e )
        {
            ADTUI.logError( "Unable to load wizard settings", e );
        }

        return retval;
    }

    private boolean needsUpdate = true;

    public GenerateCustomServicesWizard( final IJavaProject project )
    {
        super( initElement( project ), DefinitionLoader.sdef( GenerateCustomServicesWizard.class ).wizard( "wizard" ) );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] pages = super.getPages();

        if( this.needsUpdate )
        {
            this.needsUpdate = false;

            new Thread( "status update" )
            {
                public void run()
                {
                    element().updateServerStatus();
                }
            }.start();
        }

        return pages;
    }

    @Override
    protected void performPostFinish()
    {
        saveSettings( element() );
    }

    protected void saveSettings( GenerateCustomServicesOp sourceOp )
    {
        final GenerateCustomServicesWizardSettings settings = loadSettings( sourceOp );

        if( ! CoreUtil.isNullOrEmpty( sourceOp.getUrl().content() ) && OK_STATUS.equals( sourceOp.getStatus().content() ) )
        {
            final ElementList<ServerInstance> previousServerInstances = settings.getPreviousServerInstances();

            if( ! containsInstance( sourceOp, previousServerInstances ) )
            {
                final ServerInstance instance = settings.getPreviousServerInstances().insert();

                instance.setUrl( sourceOp.getUrl().content() );
                instance.setOmniUsername( sourceOp.getOmniUsername().content() );
                instance.setOmniPassword( sourceOp.getOmniPassword().content() );
                instance.setSummary( sourceOp.getSummary().content() );
            }

            try
            {
                settings.resource().save();
            }
            catch( ResourceStoreException e )
            {
                ADTUI.logError( "Unable to persist wizard settings", e );
            }
        }
    }
}
