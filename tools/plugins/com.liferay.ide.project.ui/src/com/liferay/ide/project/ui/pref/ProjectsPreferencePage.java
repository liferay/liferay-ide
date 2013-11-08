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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.ui.util.SWTUtil;

/**
 * @author Simon Jiang
 */
public class ProjectsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String ID = "com.liferay.ide.project.ui.ProjectsPreferencePage";

    private ScopedPreferenceStore prefStore;
    private RadioGroupFieldEditor radioGroupEditor;

    public ProjectsPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
        ILiferayProjectProvider[] providers = LiferayCore.getProviders();
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
                    LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION, "Default Project Build Type", 1,
                    labelAndValues, c, true );

            radioGroupEditor.fillIntoGrid( c, 1 );

            addField( radioGroupEditor );

            if( LiferayCore.getProvider( LiferayProjectCore.VALUE_PROJECT_MAVEN_BUILD_TYPE ) == null )
            {
                Group group = new Group( c, SWT.NONE );
                group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
                group.setText( "Maven Install shortcuts" );
                group.setLayout( new GridLayout( 1, false ) );

                Hyperlink link = new Hyperlink( group, SWT.NULL );
                link.setForeground( c.getDisplay().getSystemColor( SWT.COLOR_BLUE ) );
                link.setUnderlined( true );
                link.setText( "Maven Integration (m2e) HomePage" );
                link.addHyperlinkListener
                ( 
                    new HyperlinkAdapter()
                    {
                        public void linkActivated( HyperlinkEvent e )
                        {
                            try
                            {
                                IWorkbenchBrowserSupport supoprt = PlatformUI.getWorkbench().getBrowserSupport();
                                IWebBrowser browser =
                                    supoprt.createBrowser(
                                        0, "Maven Integration HomePage", "Maven Integration (m2e) HomePage", null );
                                browser.openURL( new URL( "http://www.eclipse.org/m2e/" ) );
                            }
                            catch( MalformedURLException malformException )
                            {
                                MessageDialog.openError(
                                    ProjectsPreferencePage.this.getShell(), "Liferay Project Preferences",
                                    "Unable open maven integration page." );
                            }
                            catch( PartInitException partInitException )
                            {
                                MessageDialog.openError(
                                    ProjectsPreferencePage.this.getShell(), "Liferay Project Preferences",
                                    "Unable initialize workbench." );
                            }
                        }
                    } 
                );
            }
        }
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

    @Override
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
    }

}
