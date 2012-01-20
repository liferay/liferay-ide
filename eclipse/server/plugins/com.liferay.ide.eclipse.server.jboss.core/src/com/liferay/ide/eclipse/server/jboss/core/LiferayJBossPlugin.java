
package com.liferay.ide.eclipse.server.jboss.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import com.liferay.ide.eclipse.core.CorePlugin;

/**
 * @author kamesh
 */
public class LiferayJBossPlugin extends CorePlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.jboss.core"; //$NON-NLS-1$

	// The shared instance
	private static LiferayJBossPlugin plugin;

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY = "ADDED_EXT_PLUGIN_TOGGLE_KEY";

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY =
		"ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY";

	public static final String PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY = "REMOVE_EXT_PLUGIN_TOGGLE_KEY";

	public static final String PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY = "EE_UPGRADE_MSG_TOGGLE_KEY";

	/**
	 * The constructor
	 */
	public LiferayJBossPlugin()
	{
	}

	public static IStatus createErrorStatus( String msg )
	{
		return createErrorStatus( PLUGIN_ID, msg );
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
	public static LiferayJBossPlugin getDefault()
	{
		return plugin;
	}

	public static IPersistentPreferenceStore getPreferenceStore()
	{
		return new ScopedPreferenceStore( new InstanceScope(), PLUGIN_ID );
	}

	public static void logError( String msg, Exception e )
	{
		getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, e ) );
	}
}
