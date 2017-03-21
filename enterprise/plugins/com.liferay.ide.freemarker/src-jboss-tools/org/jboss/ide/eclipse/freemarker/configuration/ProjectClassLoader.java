/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class ProjectClassLoader extends URLClassLoader {

	public ProjectClassLoader (IJavaProject project) throws JavaModelException  {
		super(getURLSFromProject(project, null), Thread.currentThread().getContextClassLoader());
	}

	public ProjectClassLoader (IJavaProject project, URL[] extraUrls) throws JavaModelException  {
		super(getURLSFromProject(project, extraUrls), Thread.currentThread().getContextClassLoader());
	}

	private static URL[] getURLSFromProject (IJavaProject project, URL[] extraUrls) throws JavaModelException {
		List list = new ArrayList();
		if (null != extraUrls) {
			for (int i=0; i<extraUrls.length; i++) {
				list.add(extraUrls[i]);
			}
		}
		
		IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
		String installLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().getAbsolutePath();
		installLoc = installLoc.replace('\\', '/');
		if (installLoc.endsWith("/")) installLoc = installLoc.substring(0, installLoc.length()-1); //$NON-NLS-1$

		for (int i=0; i<roots.length; i++) {
			try {
				if (roots[i].isArchive()) {
					File f = new File(Platform.resolve(roots[i].getPath().makeAbsolute().toFile().toURL()).getFile());
					if (f.exists()) {
						list.add(Platform.resolve(roots[i].getPath().makeAbsolute().toFile().toURL()));
					}
					else {
						String s = roots[i].getPath().toOSString().replace('\\', '/');
						if (!s.startsWith("/")) s = "/" + s; //$NON-NLS-1$ //$NON-NLS-2$
						f = new File(installLoc + s);
						if (f.exists()) {
							list.add(f.toURL());
						}
						else {
							f = new File("c:" + installLoc + s); //$NON-NLS-1$
							if (f.exists()) {
								list.add(f.toURL());
							}
							else {
								f = new File("d:" + installLoc + s); //$NON-NLS-1$
								if (f.exists()) {
									list.add(f.toURL());
								}
							}
						}
					}
				}
				else {
                    IPath path = roots[i].getJavaProject().getOutputLocation();
                    if (path.segmentCount() > 1) {
                        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                        path = root.getFolder(path).getLocation();
                        list.add(path.toFile().toURL());
                    }
                    else {
                        path = roots[i].getJavaProject().getProject().getLocation();
                        list.add(path.toFile().toURL());
                    }
				}
			}
			catch (Exception e) {}
		}
		
		URL[] urls = new URL[list.size()];
		int index = 0;
		for (Iterator i=list.iterator(); i.hasNext(); index++) {
			urls[index] = (URL) i.next();
		}
		return urls;
	}
}