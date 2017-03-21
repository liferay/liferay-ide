/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.ui.editor.WorkflowTextEditor;
import com.liferay.ide.kaleo.ui.helpers.TextEditorHelper;
import com.liferay.ide.kaleo.ui.xml.KaleoTemplateContext;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Gregory Amerson
 */
public class KaleoUI extends AbstractUIPlugin
{

    // The shared instance
    private static KaleoUI plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "com.liferay.ide.kaleo.ui"; //$NON-NLS-1$

    private static final String TEMPLATES_KEY = PLUGIN_ID + ".templates"; //$NON-NLS-1$

    public static ImageDescriptor createDescriptor( String pluginId, String image )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( pluginId, "icons/" + image );
    }

    public static IStatus createErrorStatus( Exception e )
    {
        return createErrorStatus( e.getMessage(), e );
    }

    public static IStatus createErrorStatus( String string )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, string );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createInfoStatus( String msg )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg, null );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static KaleoUI getDefault()
    {
        return plugin;
    }

    public static String getDefaultScriptForType( ScriptLanguageType scriptType, String nodeType )
    {
        URL scriptFileUrl =
            getDefault().getBundle().getEntry(
                "/scripts/default." + nodeType.toLowerCase() + "." + scriptType.toString().toLowerCase() );

        try
        {
            return CoreUtil.readStreamToString( scriptFileUrl.openStream() );
        }
        catch( Exception e )
        {
        }

        return null;
    }

    public static IKaleoEditorHelper getKaleoEditorHelper( String languageType )
    {
        IKaleoEditorHelper retval = null;

        for( IKaleoEditorHelper editoHelper : getDefault().getKaleoEditorHelpers() )
        {
            if( editoHelper.getLanguageType().equals( languageType ) )
            {
                retval = editoHelper;
                break;
            }
        }

        if( retval == null )
        {
            retval = getDefault().getDefaultTextEditorHelper();
        }

        return retval;
    }

    public static IKaleoEditorHelper getKaleoEditorHelperByEditorId( String editorId )
    {
        IKaleoEditorHelper retval = null;

        for( IKaleoEditorHelper editoHelper : getDefault().getKaleoEditorHelpers() )
        {
            if( editoHelper.getEditorId().equals( editorId ) )
            {
                retval = editoHelper;
                break;
            }
        }

        return retval;
    }

    public static IPreferenceStore getPrefStore()
    {
        return getDefault().getPreferenceStore();
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, msg, e ) );
    }

    public static IStatus logInfo( String msg, IStatus status )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg, status.getException() );
    }

    private ContextTypeRegistry contextTypeRegistry;

    private TextEditorHelper defaultEditorHelper;

    private IKaleoEditorHelper[] kaleoEditorHelpers;

    private TemplateStore templateStore;

    public KaleoUI()
    {
    }

    public ContextTypeRegistry getContextTypeRegistry()
    {
        if( contextTypeRegistry == null )
        {
            contextTypeRegistry = new ContributionContextTypeRegistry();
            KaleoTemplateContextType contextType = new KaleoTemplateContextType();
            contextTypeRegistry.addContextType( contextType );
        }

        return contextTypeRegistry;
    }

    private IKaleoEditorHelper getDefaultTextEditorHelper()
    {
        if( this.defaultEditorHelper == null )
        {
            this.defaultEditorHelper = new TextEditorHelper();
            this.defaultEditorHelper.setLanguageType( "text" );
            this.defaultEditorHelper.setEditorId( WorkflowTextEditor.ID );
            this.defaultEditorHelper.setFileExtension( "txt" );
            this.defaultEditorHelper.setContributorName( this.getBundle().getSymbolicName() );
        }

        return this.defaultEditorHelper;
    }

    public IKaleoEditorHelper[] getKaleoEditorHelpers()
    {
        if( kaleoEditorHelpers == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IKaleoEditorHelper.EXTENSION_ID );

            try
            {
                List<IKaleoEditorHelper> helpers = new ArrayList<IKaleoEditorHelper>();

                for( IConfigurationElement element : elements )
                {
                    final Object o = element.createExecutableExtension( "class" );

                    if( o instanceof AbstractKaleoEditorHelper )
                    {
                        AbstractKaleoEditorHelper kaleoEditorHelper = (AbstractKaleoEditorHelper) o;
                        kaleoEditorHelper.setLanguageType( element.getAttribute( "languageType" ) );
                        kaleoEditorHelper.setEditorId( element.getAttribute( "editorId" ) );
                        kaleoEditorHelper.setFileExtension( element.getAttribute( "fileExtension" ) );
                        kaleoEditorHelper.setContributorName( element.getContributor().getName() );

                        helpers.add( kaleoEditorHelper );
                    }
                }

                kaleoEditorHelpers = helpers.toArray( new IKaleoEditorHelper[0] );
            }
            catch( Exception e )
            {
                logError( e );
            }
        }

        return kaleoEditorHelpers;
    }

    public ContextTypeRegistry getTemplateContextRegistry()
    {
        if( contextTypeRegistry == null )
        {
            ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();

            for( KaleoTemplateContext contextType : KaleoTemplateContext.values() )
            {
                registry.addContextType( contextType.getContextTypeId() );
            }

            contextTypeRegistry = registry;
        }

        return contextTypeRegistry;
    }

    public TemplateStore getTemplateStore()
    {
        if( templateStore == null )
        {
            templateStore =
                new ContributionTemplateStore( getTemplateContextRegistry(), getPreferenceStore(), TEMPLATES_KEY );
            try
            {
                templateStore.load();
            }
            catch( IOException ex )
            {
                logError( "Unable to load pom templates", ex ); //$NON-NLS-1$
            }
        }
        return templateStore;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }
}
