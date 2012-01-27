
package com.liferay.ide.eclipse.server.jboss.core;

import com.liferay.ide.eclipse.core.CorePlugin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.core.JBossServerCorePlugin;
import org.jboss.ide.eclipse.as.core.extensions.descriptors.XPathCategory;
import org.jboss.ide.eclipse.as.core.extensions.descriptors.XPathModel;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.osgi.framework.BundleContext;

/**
 * @author kamesh
 */
public class LiferayJBossServerCorePlugin extends CorePlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.jboss.core"; //$NON-NLS-1$

	// The shared instance
	private static LiferayJBossServerCorePlugin plugin;

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY = "ADDED_EXT_PLUGIN_TOGGLE_KEY";

	public static final String PREFERENCES_ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY =
		"ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY";

	public static final String PREFERENCES_REMOVE_EXT_PLUGIN_TOGGLE_KEY = "REMOVE_EXT_PLUGIN_TOGGLE_KEY";

	public static final String PREFERENCES_EE_UPGRADE_MSG_TOGGLE_KEY = "EE_UPGRADE_MSG_TOGGLE_KEY";

	/**
	 * The constructor
	 */
	public LiferayJBossServerCorePlugin() {
	}

	public static IStatus createErrorStatus( String msg ) {
		return createErrorStatus( PLUGIN_ID, msg );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start( BundleContext context ) throws Exception {
		super.start( context );
		plugin = this;
		addToJBossServerLifecycle();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop( BundleContext context ) throws Exception {

		plugin = null;
		super.stop( context );
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static LiferayJBossServerCorePlugin getDefault() {
		return plugin;
	}

	public static IPersistentPreferenceStore getPreferenceStore() {
		return new ScopedPreferenceStore( new InstanceScope(), PLUGIN_ID );
	}

	public static void logError( String msg, Exception e ) {
		getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, e ) );
	}

	private void addToJBossServerLifecycle() {

		ServerCore.addServerLifecycleListener( new IServerLifecycleListener() {

			public void serverRemoved( IServer server ) {
				// TODO Auto-generated method stub

			}

			public void serverChanged( IServer server ) {
				// TODO Auto-generated method stub

			}

			public void serverAdded( IServer server ) {
				String runtimeTypeId = server.getRuntime().getRuntimeType().getId();
				String serverType = server.getServerType().getId();
				// Some strict naming rules here to help us locate the jboss server type
				URL querysFileURL = null;
				if ( runtimeTypeId.contains( "70" ) ) {
					querysFileURL = getURLFor( IJBossToolingConstants.DEFAULT_PROPS_70 );

				}
				else if ( runtimeTypeId.contains( "71" ) ) {
					querysFileURL = getURLFor( IJBossToolingConstants.DEFAULT_PROPS_71 );
				}
				if ( querysFileURL != null ) {
					XPathModel.addServerTypeToURLMapping( serverType, querysFileURL );
					XPathModel.getDefault().addCategory( server,  XPathModel.PORTS_CATEGORY_NAME );
					XPathCategory portsCategory =
						XPathModel.getDefault().getCategory( server, XPathModel.PORTS_CATEGORY_NAME );
					try {
						XPathModel.addQueriesToCategoryFromDefaultFile( server, portsCategory, "", querysFileURL );
						XPathModel.getDefault().save( server );
					}
					catch ( IOException e ) {
						LiferayJBossServerCorePlugin.logError( e );
					}
				}
			}
		} );

	}

	private URL getURLFor( String props ) {
		IPath properties = new Path( IJBossToolingConstants.PROPERTIES ).append( props );
		URL url = FileLocator.find( JBossServerCorePlugin.getDefault().getBundle(), properties, null );
		return url;
	}

}
