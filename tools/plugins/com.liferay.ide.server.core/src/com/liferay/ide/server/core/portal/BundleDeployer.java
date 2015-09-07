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
package com.liferay.ide.server.core.portal;

import java.io.IOException;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.osgi.framework.dto.BundleDTO;


/**
 * @author Gregory Amerson
 */
public class BundleDeployer extends JMXBundleDeployer
{

    private final static String OBJECTNAME = "osgi.core";

    public BundleDeployer( int jmxRemotePort )
    {
        super( jmxRemotePort );
    }

    /**
     * Gets the current list of installed bsns, compares it to the bsn provided.
     * If bsn doesn't exist, then install it. If it does exist then update it.
     *
     * @param bsn
     *            Bundle-SymbolicName of bundle you are wanting to deploy
     * @param bundle
     *            the bundle
     * @return the id of the updated or installed bundle
     * @throws Exception
     */
    public long deploy(String bsn, String bundleUrl) throws Exception {
        MBeanServerConnection connection = mBeanServerConnection;

        final ObjectName framework = getFramework(connection);

        long bundleId = -1;

        for (BundleDTO osgiBundle : listBundles()) {
            if (osgiBundle.symbolicName.equals(bsn)) {
                bundleId = osgiBundle.id;
                break;
            }
        }

        // TODO serve bundle url over http so it works for non file:// urls

        if (bundleId > -1) {
            connection.invoke(framework, "stopBundle",
                    new Object[] { bundleId }, new String[] { "long" });

            connection.invoke(framework, "updateBundleFromURL",
                    new Object[] { bundleId,
                    bundleUrl },
                    new String[] { "long", String.class.getName() });

            connection.invoke(framework, "refreshBundle",
                    new Object[] { bundleId }, new String[] { "long" });
        } else {
            Object installed = connection.invoke(
                    framework,
                    "installBundleFromURL",
                    new Object[] { bundleUrl, bundleUrl },
                    new String[] { String.class.getName(),
                            String.class.getName() });

            bundleId = Long.parseLong(installed.toString());
        }

        connection.invoke(framework, "startBundle",
            new Object[] { bundleId }, new String[] { "long" });

        return bundleId;
    }

    private static ObjectName getFramework( MBeanServerConnection mBeanServerConnection )
        throws MalformedObjectNameException, IOException
    {
        final ObjectName objectName = new ObjectName( OBJECTNAME + ":type=framework,*" );
        final Set<ObjectName> objectNames = mBeanServerConnection.queryNames( objectName, null );

        if( objectNames != null && objectNames.size() > 0 )
        {
            return objectNames.iterator().next();
        }

        return null;
    }

    public boolean ping()
    {
        try
        {
            return mBeanServerConnection != null &&
                            mBeanServerConnection.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null ) != null;
        }
        catch( Exception e )
        {
            return false;
        }
    }

}
