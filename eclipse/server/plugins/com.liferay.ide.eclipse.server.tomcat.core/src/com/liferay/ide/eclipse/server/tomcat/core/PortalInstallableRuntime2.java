/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package com.liferay.ide.eclipse.server.tomcat.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.internal.InstallableRuntime2;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;


@SuppressWarnings("restriction")
public class PortalInstallableRuntime2 extends InstallableRuntime2 {

	private byte[] BUFFER = null;
	private Object mutex = null;

	public PortalInstallableRuntime2(IConfigurationElement element) {
		super(element);

		mutex = this;
	}

	@Override
	public void install(final IPath path) {
		try {
			IRunnableContext context = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			context.run(true, true, new IRunnableWithProgress() {
				
				public void run(final IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

					monitor.beginTask("Downloading portal runtime...", IProgressMonitor.UNKNOWN);

					new Thread() {

						@Override
						public void run() {
							try {
								install(path, monitor);
							}
							catch (Exception ce) {
								// ignore
							}

							synchronized (mutex) {
								mutex.notifyAll();
							}
						}

					}.start();

					synchronized (mutex) {
						mutex.wait();
					}

					monitor.done();
				}
			});
		}
		catch (Exception e) {
			// ignore
		}
		
	}

	public void install(IPath path, IProgressMonitor monitor)
		throws CoreException {
		URL url = null;
		File temp = null;
		try {
			url = new URL(getArchiveUrl());
			temp = File.createTempFile("runtime", "");
			temp.deleteOnExit();
		}
		catch (IOException e) {
			Trace.trace(Trace.WARNING, "Error creating url and temp file", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(
				Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
		String name = url.getPath();

		// download
		FileOutputStream fout = null;
		try {
			InputStream in = url.openStream();
			fout = new FileOutputStream(temp);
			copy(in, fout, monitor);
		}
		catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error downloading runtime", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(
				Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
		finally {
			try {
				if (fout != null)
					fout.close();
			}
			catch (IOException e) {
				// ignore
			}
		}

		if (monitor.isCanceled()) {
			return;
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(temp);
			if (name.endsWith("zip"))
				unzip(in, path, monitor);
			else if (name.endsWith("tar"))
				untar(in, path, monitor);
		}
		catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error uncompressing runtime", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(
				Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
		finally {
			try {
				if (in != null)
					in.close();
			}
			catch (IOException e) {
				// ignore
			}
		}
	}

	
	protected void copy(InputStream in, OutputStream out, IProgressMonitor monitor)
		throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		int r = in.read(BUFFER);
		while (r >= 0 && !monitor.isCanceled()) {
			out.write(BUFFER, 0, r);
			r = in.read(BUFFER);
		}
	}

	private void unzip(InputStream in, IPath path, IProgressMonitor monitor)
		throws IOException {
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		ZipInputStream zin = new ZipInputStream(bin);
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			monitor.setTaskName("Unzipping: " + name);
			if (archivePath != null && name.startsWith(archivePath)) {
				name = name.substring(archivePath.length());
				if (name.length() > 1)
					name = name.substring(1);
			}

			if (name != null && name.length() > 0) {
				if (entry.isDirectory())
					path.append(name).toFile().mkdirs();
				else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copy(zin, fout, monitor);
					fout.close();
				}
			}
			zin.closeEntry();
			entry = zin.getNextEntry();
		}
		zin.close();
	}

}
