
package com.liferay.ide.core.templates;

import com.liferay.ide.core.LiferayCore;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The activator class controls the plugin life cycle
 */
public class TemplatesCore
{

    // The shared instance
    private static TemplatesCore plugin;

    private static Map<String, TemplateModel> templateModels = new HashMap<String, TemplateModel>();

    private static IConfigurationElement[] tplDefinitionElements;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static TemplatesCore getDefault()
    {
        return plugin;
    }

    public static ITemplateOperation getTemplateOperation( String templateId )
    {
        // look up the template
        // find the plugin
        // if it doesn't have an engine for that plugin, then create one
        // then load the template with default context

        TemplateModel model = getTemplateModel( templateId );

        return new TemplateOperation( model );
    }

    private static TemplateModel createTemplateModel( IConfigurationElement element, String pluginName )
    {
        TemplateModel templateModel = null;

        try
        {
            String id = element.getAttribute( "id" ); //$NON-NLS-1$
            String resource = element.getAttribute( "resource" ); //$NON-NLS-1$
            String templateFolder = null;
            List<TemplateVariable> paramList = new ArrayList<TemplateVariable>();

            IConfigurationElement[] items = ( (IExtension) element.getParent() ).getConfigurationElements();

            for( IConfigurationElement item : items )
            {
                if( "templatesFolder".equals( item.getName() ) ) //$NON-NLS-1$
                {
                    templateFolder = item.getAttribute( "path" ); //$NON-NLS-1$
                }

                if( "templateVariable".equals( item.getName() ) ) //$NON-NLS-1$
                {
                    String paramName = item.getAttribute( "name" ); //$NON-NLS-1$
                    String reqVal = item.getAttribute( "required" ); //$NON-NLS-1$
                    paramList.add( new TemplateVariable( paramName, reqVal ) );
                }
            }

            final Configuration config = new Configuration();
            final TemplateVariable[] vars = paramList.toArray( new TemplateVariable[0] );

            templateModel = new TemplateModel( pluginName,
                                               config,
                                               id,
                                               resource,
                                               templateFolder,
                                               vars );
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }

        return templateModel;
    }

    private static TemplateModel getTemplateModel( String templateId )
    {
        if( templateId == null )
        {
            return null;
        }

        TemplateModel model = templateModels.get( templateId );

        if( model == null )
        {
            final IConfigurationElement element = getTplDefinitionElement( templateId );
            final String pluginName = element.getContributor().getName();
            model = createTemplateModel( element, pluginName );

            try
            {
                initializeModel( model );
            }
            catch( Exception e )
            {
                LiferayCore.logError( e );
                model = null;
            }

            if( model != null )
            {
                templateModels.put( templateId, model );
            }
        }

        return model;
    }

    private static IConfigurationElement getTplDefinitionElement( String templateId )
    {
        if( templateId == null )
        {
            return null;
        }

        final IConfigurationElement[] elements = getTplDefinitionElements();

        for( IConfigurationElement element : elements )
        {
            if( "template".equals( element.getName() ) ) //$NON-NLS-1$
            {
                if( templateId.equals( element.getAttribute( "id" ) ) ) //$NON-NLS-1$
                {
                    return element;
                }
            }
        }

        return null;
    }

    private static IConfigurationElement[] getTplDefinitionElements()
    {
        if( tplDefinitionElements == null )
        {
            tplDefinitionElements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( LiferayCore.PLUGIN_ID + ".templateDefinitions" );
        }

        return tplDefinitionElements;
    }

    private static void initializeModel( TemplateModel templateModel ) throws Exception
    {
        final Configuration config = templateModel.getConfig();
        final String bundleId = templateModel.getBundleId();
        final Bundle bundle = Platform.getBundle( bundleId );

        if( bundle == null )
        {
            LiferayCore.logError( "Could not initialize template model: could not find bundle " + bundleId ); //$NON-NLS-1$
        }

        final URL loaderRoot = bundle.getEntry( templateModel.getTemplateFolder() );
        final URL fileUrl = FileLocator.toFileURL( loaderRoot );
        config.setDirectoryForTemplateLoading( new File( fileUrl.getFile().toString() ) );
        config.setObjectWrapper( ObjectWrapper.BEANS_WRAPPER );

        templateModel.setConfig( config );
    }

    /**
     * The constructor
     */
    private TemplatesCore()
    {
    }

}
