package com.liferay.ide.eclipse.server.remote;

import com.liferay.ide.eclipse.server.core.ILiferayServer;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.model.IURLProvider;


public interface IRemoteServer extends ILiferayServer, IURLProvider {

	public static final IEclipsePreferences defaultPrefs =
		new DefaultScope().getNode( LiferayServerCorePlugin.PLUGIN_ID );

	String ATTR_HOSTNAME = "hostname";

	String ATTR_HTTP_PORT = "http-port";

	String ATTR_SERVER_MANAGER_CONTEXT_PATH = "server-manager-context-path";

	String ATTR_LIFERAY_PORTAL_CONTEXT_PATH = "liferay-portal-context-path";

	String getHost();

	String getHTTPPort();

	String getId();

	String getServerManagerContextPath();

	String getLiferayPortalContextPath();

	String DEFAULT_HTTP_PORT = defaultPrefs.get( "default.http.port", "" );

	String DEFAULT_SERVER_MANAGER_CONTEXT_PATH = defaultPrefs.get( "default.server.manager.context.path", "" );

	String DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH = defaultPrefs.get( "default.liferay.portal.context.path", "" );

}
