/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.CustomProjectSelectionDialog;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class BuildServicePage extends Page {

	public BuildServicePage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, buildservicePageId, true);

		Button buildServiceButton = new Button(this, SWT.PUSH);

		buildServiceButton.setText("Build Services");

		buildServiceButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					List<IProject> projects = _getServiceBuilderProjects();

					CustomProjectSelectionDialog dialog = new CustomProjectSelectionDialog(UIUtil.getActiveShell());

					dialog.setProjects(projects);

					URL imageUrl = bundle.getEntry("/icons/e16/service.png");

					ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(imageUrl);

					Image serviceXmlImage = imageDescriptor.createImage();

					dialog.setImage(serviceXmlImage);

					dialog.setTitle("Liferay Service Project");
					dialog.setMessage("Select Liferay Service Project");

					List<IProject> liferayServiceProjects = new ArrayList<>();

					if (dialog.open() == Window.OK) {
						final Object[] selectedProjects = dialog.getResult();

						if (selectedProjects != null) {
							for (Object project : selectedProjects) {
								if (project instanceof IJavaProject) {
									IJavaProject p = (IJavaProject)project;

									liferayServiceProjects.add(p.getProject());
								}
							}
						}
					}

					try {
						IProgressService progressService = UIUtil.getProgressService();

						progressService.busyCursorWhile(
							new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor) {
									try {
										for (IProject project : liferayServiceProjects) {
											_deleteLegacyFiles(project, monitor);

											final ILiferayProject liferayProject = LiferayCore.create(
												ILiferayProject.class, project);

											if (liferayProject != null) {
												IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

												builder.buildService(monitor);
											}

											IConsole console = CompileAction.getConsole("build-service");

											if (console != null) {
												IDocument document = ((ProcessConsole)console).getDocument();

												if (StringUtil.contains(document.get(), "BUILD FAILED")) {
													return;
												}
											}
										}
									}
									catch (CoreException ce) {
									}
								}

							});
					}
					catch (Exception e1) {
					}
				}

				private void _deleteLegacyFiles(IProject project, IProgressMonitor monitor) {
					try {
						String relativePath = "/docroot/WEB-INF/src/META-INF";

						IFile portletSpringXML = project.getFile(relativePath + "/portlet-spring.xml");
						IFile shardDataSourceSpringXML = project.getFile(
							relativePath + "/shard-data-source-spring.xml");

						if (portletSpringXML.exists()) {
							portletSpringXML.delete(true, monitor);
						}

						if (shardDataSourceSpringXML.exists()) {
							shardDataSourceSpringXML.delete(true, monitor);
						}

						// for 6.2 maven project

						IFolder metaInfFolder = project.getFolder("/src/main/resources/META-INF/");

						if (metaInfFolder.exists()) {
							metaInfFolder.delete(true, monitor);
						}
					}
					catch (CoreException ce) {
						ProjectUI.logError(ce);
					}
				}

				private List<IProject> _getServiceBuilderProjects() {
					List<IProject> results = new ArrayList<>();

					IProject[] projects = CoreUtil.getAllProjects();

					for (IProject project : projects) {
						IFile serviceFile = project.getFile("/docroot/WEB-INF/service.xml");

						if (!serviceFile.exists()) {
							serviceFile = project.getFile("src/main/webapp/WEB-INF/service.xml");
						}

						if (serviceFile.exists()) {
							results.add(project);
						}
					}

					return results;
				}

			});
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptorBuilder = new StringBuilder(
			"In this step, we will delete some legacy service builder related files");

		descriptorBuilder.append(" and re-run build-service on service builder projects.\n");
		descriptorBuilder.append(
			"Note: Please make sure the default installed JRE is JDK 8 (Preferences -> Java -> Installed JREs).");

		String url = "";

		Link link = SWTUtil.createHyperLink(this, style, descriptorBuilder.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public String getPageTitle() {
		return "Build Services";
	}

}