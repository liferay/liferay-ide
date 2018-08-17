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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
public class LayoutTemplatePage extends AbstractLiferayTableViewCustomPart {

	public LayoutTemplatePage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, layouttemplatePageId, true);
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final String descriptor =
			"This step will upgrade layout template files from 6.2 to 7.0.\n" +
				"The layout template's rows and columns are affected by the new grid system syntax of Bootstrap.\n" +
					"For more details, please see <a>Upgrading Layout Templates</a>.";

		String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/upgrading-layout-templates";

		Link link = SWTUtil.createHyperLink(this, style, descriptor, 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public int getGridLayoutCount() {
		return 2;
	}

	@Override
	public boolean getGridLayoutEqualWidth() {
		return false;
	}

	@Override
	public String getPageTitle() {
		return "Upgrade Layout Templates";
	}

	@Override
	protected void createTempFile(final IFile srcFile, final File templateFile, final String projectName) {
		try {
			String content = _upgradeLayouttplContent(FileUtil.readContents(srcFile.getLocation().toFile(), true));

			if (templateFile.exists()) {
				templateFile.delete();
			}

			templateFile.createNewFile();
			FileUtil.writeFile(templateFile, content, projectName);
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	@Override
	protected void doUpgrade(IFile srcFile, IProject project) {
		try {
			String content = _upgradeLayouttplContent(FileUtil.readContents(srcFile.getLocation().toFile(), true));

			FileUtils.writeStringToFile(srcFile.getLocation().toFile(), content, "UTF-8");
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	@Override
	protected IFile[] getAvaiableUpgradeFiles(IProject project) {
		List<IFile> files = new ArrayList<>();

		List<IFile> searchFiles = new LayoutSearchFilesVistor().searchFiles(project, ".tpl");

		files.addAll(searchFiles);

		return files.toArray(new IFile[files.size()]);
	}

	@Override
	protected CellLabelProvider getLableProvider() {
		return new LiferayUpgradeTabeViewLabelProvider("Upgrade Layouttpl") {

			@Override
			public Image getImage(Object element) {
				return getImageRegistry().get("layout");
			}

			@Override
			protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
				imageRegistry.put(
					"layout", ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/layout.png"));
			}

		};
	}

	@Override
	protected List<IProject> getSelectedProjects() {
		List<IProject> projects = new ArrayList<>();

		final JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog(
			Display.getCurrent().getActiveShell(), new LayoutProjectViewerFilter());

		URL imageUrl = bundle.getEntry("/icons/e16/layout.png");

		Image layouttplImage = ImageDescriptor.createFromURL(imageUrl).createImage();

		dialog.setImage(layouttplImage);

		dialog.setTitle("Layout Template Project");
		dialog.setMessage("Select Layout Template Project");

		if (dialog.open() == Window.OK) {
			final Object[] selectedProjects = dialog.getResult();

			if (selectedProjects != null) {
				for (Object project : selectedProjects) {
					if (project instanceof IJavaProject) {
						IJavaProject p = (IJavaProject)project;

						projects.add(p.getProject());
					}
				}
			}
		}

		return projects;
	}

	@Override
	protected boolean isUpgradeNeeded(IFile srcFile) {
		final String content = FileUtil.readContents(srcFile.getLocation().toFile());

		if ((content != null) && !content.equals("")) {
			if (content.contains("row-fluid") || content.contains("span")) {
				return true;
			}
		}

		return false;
	}

	private String _upgradeLayouttplContent(String content) {
		if ((content != null) && !content.equals("")) {
			if (content.contains("row-fluid")) {
				content = content.replaceAll("row-fluid", "row");
			}

			if (content.contains("span")) {
				content = content.replaceAll("span", "col-md-");
			}
		}

		return content;
	}

	private class LayoutProjectViewerFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IJavaProject) {
				IProject project = ((IJavaProject)element).getProject();

				if (project.getName().equals("External Plug-in Libraries")) {
					return false;
				}

				if (ProjectUtil.isLayoutTplProject(project)) {
					return true;
				}

				return false;
			}

			return false;
		}

	}

	private class LayoutSearchFilesVistor extends SearchFilesVisitor {

		@Override
		public boolean visit(IResourceProxy resourceProxy) {
			if ((resourceProxy.getType() == IResource.FILE) && resourceProxy.getName().endsWith(searchFileName)) {
				IResource resource = resourceProxy.requestResource();

				if (resource.exists()) {
					resources.add((IFile)resource);
				}
			}

			return true;
		}

	}

}