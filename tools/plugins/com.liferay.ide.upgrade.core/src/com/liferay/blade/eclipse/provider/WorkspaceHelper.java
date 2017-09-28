/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.eclipse.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Gregory Amerson
 */
public class WorkspaceHelper {

	private void addNaturesToProject( IProject proj, String[] natureIds, IProgressMonitor monitor )
	        throws CoreException {
        IProjectDescription description = proj.getDescription();

        String[] prevNatures = description.getNatureIds();
        String[] newNatures = new String[prevNatures.length + natureIds.length];

        System.arraycopy( prevNatures, 0, newNatures, 0, prevNatures.length );

        for( int i = prevNatures.length; i < newNatures.length; i++ ) {
            newNatures[i] = natureIds[i - prevNatures.length];
        }

        description.setNatureIds( newNatures );
        proj.setDescription( description, monitor );
    }

	public IFile createIFile(String projectName, File file) throws CoreException, IOException {
		IJavaProject project = getJavaProject(projectName);

		IFile projectFile = project.getProject().getFile( "/temp/" + file.getName() );

		final IProgressMonitor npm = new NullProgressMonitor();

		if (projectFile.exists()) {
			projectFile.delete(IFile.FORCE, npm);
		}

		if( !projectFile.getParent().exists() && projectFile.getParent() instanceof IFolder )
		{
			IFolder parentFolder = (IFolder) projectFile.getParent();

			parentFolder.create( true, true, npm );
		}

		byte[] bytes = Files.readAllBytes(file.toPath());

		projectFile.create(new ByteArrayInputStream(bytes), IFile.FORCE, npm);

		return projectFile;
	}

	private IJavaProject getJavaProject(String projectName) throws CoreException {
		IProject javaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		IProgressMonitor monitor = new NullProgressMonitor();

		if (!javaProject.exists()) {
			IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
			javaProject.create(monitor);
			javaProject.open(monitor);
			javaProject.setDescription(description, monitor);
		}

		javaProject.open(monitor);
		addNaturesToProject(javaProject, new String[] { JavaCore.NATURE_ID }, monitor);

		return JavaCore.create(javaProject);
	}

}
