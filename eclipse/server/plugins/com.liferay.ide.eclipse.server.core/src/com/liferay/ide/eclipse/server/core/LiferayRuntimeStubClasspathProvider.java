package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;


public class LiferayRuntimeStubClasspathProvider extends RuntimeClasspathProviderDelegate {

	private static final NullProgressMonitor npm = new NullProgressMonitor();
	protected RuntimeClasspathProviderDelegate stubDelegate = null;

	public LiferayRuntimeStubClasspathProvider() {
		super();
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer( IProject project, IRuntime runtime ) {
		IClasspathEntry[] retval = null;

		if ( stubDelegate == null ) {
			LiferayRuntimeStubDelegate delegate =
				(LiferayRuntimeStubDelegate) runtime.loadAdapter( LiferayRuntimeStubDelegate.class, npm );

			String runtimeStubTypeId = delegate.getRuntimeStubTypeId();

			IConfigurationElement[] elements =
				Platform.getExtensionRegistry().getConfigurationElementsFor(
					"org.eclipse.jst.server.core.runtimeClasspathProviders" );

			for ( IConfigurationElement element : elements ) {
				String runtimeTypeIds = element.getAttribute( "runtimeTypeIds" );
				if ( runtimeTypeIds.contains( runtimeStubTypeId ) ) {
					try {
						stubDelegate = (RuntimeClasspathProviderDelegate) element.createExecutableExtension( "class" );
						break;
					}
					catch ( CoreException e ) {
						e.printStackTrace();
					}
				}
			}
		}

		if ( stubDelegate != null ) {
			retval = stubDelegate.resolveClasspathContainer( project, runtime );
		}

		return retval;
	}
}
