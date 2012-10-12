package com.liferay.ide.maven.core;

import com.liferay.ide.project.core.facet.PortletPluginFacetInstallDataModelProvider;


/**
 * @author Gregory Amerson
 */
public class MavenPortletPluginFacetInstallDataModelProvider extends PortletPluginFacetInstallDataModelProvider
{

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( propertyName.equals( INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE ) )
        {
            return false;
        }
        else if( propertyName.equals( SETUP_DEFAULT_OUTPUT_LOCATION ) )
        {
            return false;
        }
 
        return super.getDefaultProperty( propertyName );
    }

}
