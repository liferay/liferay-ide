package com.liferay.ide.eclipse.service.core.model.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.BasePathsProviderImpl;
import org.eclipse.sapphire.platform.PathBridge;


public class ImportsPathProvider extends BasePathsProviderImpl {

	@Override
	public List<Path> getBasePaths(IModelElement modelElement) {
		List<Path> paths = new ArrayList<Path>();

		// return path relative to root of SDK.
		IFile editorFile = modelElement.adapt(IFile.class);

		Path p = PathBridge.create(editorFile.getParent().getLocation());
		paths.add(p);

		return paths;
	}

}
