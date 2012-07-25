
package com.liferay.ide.theme.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 * 
 * @author Gregory Amerson
 */
public class ThemeUIPlugin extends AbstractUIPlugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.theme.ui"; //$NON-NLS-1$

    // The shared instance
    private static ThemeUIPlugin plugin;

    /**
     * The constructor
     */
    public ThemeUIPlugin()
    {
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

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ThemeUIPlugin getDefault()
    {
        return plugin;
    }

}
