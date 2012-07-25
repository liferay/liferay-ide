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
package org.jboss.ide.eclipse.freemarker.linetracker;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

/**
 * @author <a href="mailto:joe&binamics.net">Joe Hudson </a>
 */
public class ConsoleLineTracker implements IConsoleLineTracker {

	private IConsole console;
	// This assumes console parsing in English only
	private static final String CHECK = "freemarker"; //$NON-NLS-1$
	private static final String CHECK_LINE = "line:"; //$NON-NLS-1$
	private static final String CHECK_LINE2 = "on line "; //$NON-NLS-1$
	private static final String CHECK_TEMPLATE = "template:"; //$NON-NLS-1$
	private static final String CHECK_TEMPLATE2 = " in "; //$NON-NLS-1$
	
	public void init(IConsole console) {
		this.console = console;
	}

	public void lineAppended(IRegion line) {
		try {
			String text = console.getDocument().get(line.getOffset(), line.getLength());
			if (text.indexOf(CHECK) >= 0) {
				// it might relate to us
				int i1 = text.lastIndexOf(CHECK_TEMPLATE);
				if (i1 < 0) {
					int i2 = text.lastIndexOf(CHECK_LINE2);
					if (i2 >= 0) {
						i1 = text.indexOf(CHECK_TEMPLATE2, i2);
						i1 -= 6;
					}
				}
				if (i1 > 0) {
					// this is most likely an error message
					int linkOffset = i1 + 10;
					int linkLength = text.length() - linkOffset;
					String fileName = text.substring(linkOffset, text.length()).trim();
					if (fileName.endsWith(".")) fileName = fileName.substring(0, fileName.length()-1); //$NON-NLS-1$
					int lineNumber = -1;
					try {
						int i2 = text.lastIndexOf(CHECK_LINE);
						if (i2 > 0) {
							i2 += CHECK_LINE.length();
							int i3 = text.indexOf(",", i2); //$NON-NLS-1$
							if (i3 > 0) {
								lineNumber = Integer.parseInt(text.substring(i2, i3).trim());
							}
						}
						else {
							i2 = text.lastIndexOf(CHECK_LINE2);
							if (i2 > 0) {
								i2 += CHECK_LINE2.length();
								int i3 = text.indexOf(",", i2); //$NON-NLS-1$
								if (i3 > 0) {
									lineNumber = Integer.parseInt(text.substring(i2, i3).trim());
								}
							}
						}
					}
					catch (Exception e) {
						// we can still proceed if we don't get the line number
					}
					
					IPath path = new Path(fileName);
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					
					List files = new ArrayList();
					for (int i = 0; i < projects.length; i++) {
						IProject project = projects[i];
						IJavaProject javaProject = JavaCore.create(project);
						fileName = fileName.replace('\\', '/');
						try {
							populateMatchingFiles(project, files, fileName.split("/")); //$NON-NLS-1$
						}
						catch (CoreException e) {
							// TODO log this exception
						}
					}

					if (files.size() != 0) {
						IFile file = (IFile) files.get(0);
						if (file != null && file.exists()) {
							FileLink link = new FileLink(file, null, -1, -1, lineNumber);
							console.addLink(link, linkOffset, linkLength);
						}
					}
				}
			}
		} catch (BadLocationException e) {
		}
	}

	public void populateMatchingFiles(IContainer container, List files, String[] fileNameSeq) throws CoreException {
		IResource[] resources = container.members();
		for (int i=0; i<resources.length; i++) {
			IResource resource = resources[i];
			if (resource instanceof IContainer) {
				populateMatchingFiles((IContainer) resource, files, fileNameSeq);
			}
			else if (resource instanceof IFile) {
				if (isCorrectFile((IFile) resource, fileNameSeq)) {
					boolean doAdd = true;
					try {
						IJavaProject javaProject = JavaCore.create(resource.getProject());
						if (javaProject.getOutputLocation().isPrefixOf(((IFile) resource).getFullPath())) doAdd = false;
					}
					catch (JavaModelException e) {}
					if (doAdd) files.add(resource);
				}
			}
		}
	}

	private boolean isCorrectFile(IFile file, String[] filenameSeqs) {
		IResource temp = file;
		for (int i = filenameSeqs.length - 1; i >= 0; i--) {
			String seq = filenameSeqs[i];
			if (!seq.equals(temp.getName())) {
				return false;
			}
			temp = temp.getParent();
		}
		return true;
	}

	public void dispose() {
	}
}