package com.liferay.ide.server.core.jmx;

import javax.management.ObjectName;

import org.osgi.framework.dto.BundleDTO;

public class PortalBundleDeployer extends JMXBundleDeployer
{

    public PortalBundleDeployer( int jmxPort )
    {
        super( jmxPort );
    }

    public BundleDTO installBundleFromURL( String url ) throws Exception
    {
        final ObjectName framework = getFramework(mBeanServerConnection);

        Object installed = mBeanServerConnection.invoke(framework, "installBundleFromURL", new Object[] {
            url, url,
        }, new String[] {
                String.class.getName(), String.class.getName()
        });

        long bundleId = Long.parseLong(installed.toString());

        BundleDTO retval = new BundleDTO();

        retval.id = bundleId;

        return retval;
    }

    public void updateBundleFromURL( long bundleId, String bundleUrl ) throws Exception
    {
        final ObjectName framework = getFramework(mBeanServerConnection);

        mBeanServerConnection.invoke(framework, "updateBundleFromURL", new Object[] {
                bundleId, bundleUrl
        }, new String[] {
                "long", String.class.getName()
        });

        mBeanServerConnection.invoke(framework, "refreshBundle", new Object[] {
                bundleId
        }, new String[] {
                "long"
        });
    }

}
