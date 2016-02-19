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
package com.liferay.ide.core;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Andy Wu
 */
public class LiferayProjectImporterReader extends ExtensionReader<ILiferayProjectImporter>
{
    private static final String ATTRIBUTE_BUILDTYPE = "buildType";
    private static final String EXTENSION = "liferayProjectImporters";
    private static final String PROVIDER_ELEMENT = "liferayProjectImporter";

    public LiferayProjectImporterReader()
    {
        super( LiferayCore.PLUGIN_ID, EXTENSION, PROVIDER_ELEMENT );
    }

    public ILiferayProjectImporter[] getImporters()
    {
        return getExtensions().toArray( new ILiferayProjectImporter[0] );
    }

    @Override
    protected ILiferayProjectImporter initElement( IConfigurationElement configElement, ILiferayProjectImporter importer )
    {
        final String buildType = configElement.getAttribute( ATTRIBUTE_BUILDTYPE );

        final AbstractLiferayProjectImporter projectImporter = (AbstractLiferayProjectImporter) importer;

        projectImporter.setBuildType( buildType );

        return importer;
    }

    public ILiferayProjectImporter getImporter( String buildType )
    {
        ILiferayProjectImporter retval = null;

        ILiferayProjectImporter[] importers = getImporters();

        for( ILiferayProjectImporter importer : importers )
        {
            if( importer.getBuildType().equals( buildType ) )
            {
                retval = importer;
            }
        }

        return retval;
    }

}
