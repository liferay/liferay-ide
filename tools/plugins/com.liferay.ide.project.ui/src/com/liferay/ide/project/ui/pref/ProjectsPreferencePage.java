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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.project.core.LiferayProjectCore;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Simon Jiang
 */
public class ProjectsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String ID = "com.liferay.ide.project.ui.ProjectsPreferencePage";

    private ScopedPreferenceStore prefStore;
    //private RadioGroupFieldEditor radioGroupEditor;
    private BooleanFieldEditor useSnapshotVersion;

    public ProjectsPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
/*      
        final ILiferayProjectProvider[] providers = LiferayCore.getProviders();

        Arrays.sort( providers );

        if( providers != null )
        {
            String[][] labelAndValues = new String[providers.length][2];

            for( int i = 0; i < providers.length; i++ )
            {
                ILiferayProjectProvider provider = providers[i];
                labelAndValues[i][0] = provider.getDisplayName();
                labelAndValues[i][1] = provider.getShortName();
            }

            Composite c = SWTUtil.createComposite( getFieldEditorParent(), 1, 1, SWT.FILL );
            c.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

            radioGroupEditor =
                new RadioGroupFieldEditor(
                    LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION, "Default new project build type", 1,
                    labelAndValues, c, true );

            radioGroupEditor.fillIntoGrid( c, 1 );

            addField( radioGroupEditor );

            if( LiferayCore.getProvider( LiferayProjectCore.VALUE_PROJECT_MAVEN_BUILD_TYPE ) == null )
            {
                Hyperlink link = new Hyperlink( radioGroupEditor.getRadioBoxControl( c ), SWT.NULL );
                link.setForeground( c.getDisplay().getSystemColor( SWT.COLOR_BLUE ) );
                link.setUnderlined( true );
                link.setText( "To add support for maven, please install the m2e-liferay feature." );
                link.addHyperlinkListener
                ( 
                    new HyperlinkAdapter()
                    {
                        @Override
                        public void linkActivated( HyperlinkEvent event )
                        {
                            try
                            {
                                IWorkbenchBrowserSupport supoprt = PlatformUI.getWorkbench().getBrowserSupport();
                                IWebBrowser browser =
                                    supoprt.createBrowser( 0, "Liferay IDE Download", "Liferay IDE Download Page", null );
                                browser.openURL( 
                                    new URL( "https://www.liferay.com/downloads/liferay-projects/liferay-ide" ) );
                            }
                            catch( Exception e )
                            {
                                ProjectUIPlugin.logError( "Unable to open Liferay IDE download page", e );
                            }
                        }
                    } 
                );
            }
        }
*/

        useSnapshotVersion =
            new BooleanFieldEditor(
                LiferayProjectCore.PREF_USE_SNAPSHOT_VERSION, "Allow snapshot versions for new maven projects",
                getFieldEditorParent() );

        useSnapshotVersion.fillIntoGrid( getFieldEditorParent(), 1 );

        addField( useSnapshotVersion );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, LiferayProjectCore.PLUGIN_ID );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
        noDefaultAndApplyButton();
    }

/*    @Override
    protected void performDefaults()
    {
        final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( LiferayProjectCore.PLUGIN_ID );
        prefs.remove( LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            LiferayProjectCore.logError( e );
        }

        super.performDefaults();
    }*/

}
