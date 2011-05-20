
package com.liferay.ide.eclipse.service.core.model.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.RelativePathService;
import org.eclipse.sapphire.platform.PathBridge;

public class ImportPathService extends RelativePathService {

	@Override
	public List<Path> roots() {
		final File file = element().adapt( File.class );

		if ( file == null ) {
			return Collections.emptyList();
		}
		else {
			return Collections.singletonList( new Path( file.getParent() ) );
		}
	}

	@Override
	public boolean enclosed() {
		return false;
	}

	@Override
	public Path convertToAbsolute( Path path ) {
		if ( path != null ) {
			final IFile file = element().adapt( IFile.class );

			if ( file != null ) {
				final IPath baseLocation = file.getParent().getLocation();

				final IPath absoluteLocation = baseLocation.append( PathBridge.create( path ) );
				Path absolute = PathBridge.create( absoluteLocation.makeAbsolute() );

				return absolute;
			}
		}
		return super.convertToAbsolute( path );
	}

	@Override
	public Path convertToRelative( Path path ) {
		if ( path != null ) {
			final IFile file = element().adapt( IFile.class );

			if ( file != null ) {
				IPath baseLocation = file.getParent().getLocation();

				if ( baseLocation != null ) {
					return path.makeRelativeTo( PathBridge.create( baseLocation ) );
				}
			}
		}

		return null;
	}
}
