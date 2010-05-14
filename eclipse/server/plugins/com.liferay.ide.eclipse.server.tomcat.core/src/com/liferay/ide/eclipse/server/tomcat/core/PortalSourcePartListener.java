/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.server.core.IPortalRuntime;
import com.liferay.ide.eclipse.server.core.IPortalSource;
import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PortalSourcePartListener implements IPartListener2 {

	public static class PortalSourceUpdate implements IRunnableWithProgress {

		protected IClassFile classFile;
		
		protected ClassFileEditor classFileEditor;
		
		protected IPath parentPath;
		
		protected IJavaElement primaryElement;
		
		protected IPath sourcePath;

		public PortalSourceUpdate(ClassFileEditor cfe) {
			this.classFileEditor = cfe;
			
			IEditorInput editorInput = cfe.getEditorInput();
			
			final IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) editorInput;
			
			this.classFile = classFileEditorInput.getClassFile();
			
			IJavaElement parent = classFile.getParent();
			
			parentPath = parent.getPath();
			
			String packageName = primaryElement.getParent().getElementName();
			
			String packagePath = packageName.replaceAll("\\.", "/");
			
			String className = primaryElement.getElementName();
			
			String classPath = className.replaceAll("\\.class", "\\.java");
			
			sourcePath = new Path(packagePath).append(classPath);
		}

		public void run(final IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
			
			String jarFolder = parentPath.removeFileExtension().lastSegment();
			
			if (shouldGetSVNFile(jarFolder, sourcePath)) {
				String version = "trunk";
				
				try {
					IPortalRuntime portalRuntime =
						ServerUtil.getPortalRuntime(this.classFile.getJavaProject().getProject());
					
					version = portalRuntime.getPortalVersion();
				}
				catch (CoreException e1) {
					PortalTomcatPlugin.logError(e1);
				}

				getSVNFile(jarFolder, sourcePath, version, monitor);
				
				classFileEditor.getSite().getShell().getDisplay().asyncExec(new Runnable() {

					public void run() {
						try {
							JavaModelManager.getExternalManager().refreshReferences(
								primaryElement.getJavaProject().getProject(), monitor);
						}
						catch (Exception e) {
						}
						
						InternalClassFileEditorInput newInput = new InternalClassFileEditorInput(classFile);
						
						classFileEditor.setInput(newInput);
						classFileEditor.inputChanged(newInput);
					}
				});
			}
		}
	}

	protected static void getSVNFile(String jar, IPath fullPath, String version, IProgressMonitor monitor) {
		IPath sourcePath = PortalServerCorePlugin.getDefault().getPortalSourcePath(new Path(jar));
		
		File outputFile = sourcePath.append(fullPath).toFile();
		
		if (outputFile.exists()) {
			return;
		}
		
		HttpClient client = new HttpClient();
		
		client.getState().setCredentials(
			new AuthScope(null, 80, null), new UsernamePasswordCredentials("guest", "guest"));
		
		GetMethod get =
			new GetMethod("http://svn.liferay.com/repos/public/portal/" + version + jar + "/src/" + fullPath);
		
		get.setDoAuthentication(true);
		
		try {
			client.executeMethod(get);
			
			outputFile.getParentFile().mkdirs();
			
			FileOutputStream fos = new FileOutputStream(outputFile);
			
			FileUtil.transferStreams(get.getResponseBodyAsStream(), fos, outputFile.getPath(), monitor);
			
			fos.flush();
			fos.close();
		}
		catch (Exception e) {
			PortalTomcatPlugin.logError(e);
		}
		finally {
			get.releaseConnection();
		}
	}

	protected static boolean shouldGetSVNFile(String path, IPath sourcePath) {
		IPath portalSourcePath = PortalServerCorePlugin.getDefault().getPortalSourcePath(new Path(path));
		
		File outputFile = portalSourcePath.append(sourcePath).toFile();
		
		return !outputFile.exists();
	}

	public PortalSourcePartListener() {
	}

	public void partActivated(IWorkbenchPartReference partRef) {
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	public void partClosed(IWorkbenchPartReference partRef) {
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		IWorkbenchPart workbenchPart = partRef.getPart(false);
		
		if (workbenchPart instanceof ClassFileEditor) {
			final ClassFileEditor cfe = (ClassFileEditor) workbenchPart;
			
			IEditorInput editorInput = cfe.getEditorInput();
			
			final IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) editorInput;
			
			final IClassFile classFile = classFileEditorInput.getClassFile();
			
			IJavaElement parent = classFile.getParent();
			
			final IPath parentPath = parent.getPath();
			
			for (String portalSourceFolder : IPortalSource.SOURCE_FOLDERS) {
				if (parentPath.removeFileExtension().lastSegment().equals(portalSourceFolder)) {
					try {
						PortalSourceUpdate update = new PortalSourceUpdate(cfe);
						
						partRef.getPage().getWorkbenchWindow().getWorkbench().getProgressService().run(
							true, true, update);
					}
					catch (Exception e) {
						PortalTomcatPlugin.logError(e);
					}
				}
			}
		}
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

}
