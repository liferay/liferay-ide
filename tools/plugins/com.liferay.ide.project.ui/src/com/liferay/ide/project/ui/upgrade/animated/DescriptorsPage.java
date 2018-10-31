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
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class DescriptorsPage extends AbstractLiferayTableViewCustomPart {

	public DescriptorsPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, descriptorsPageId, true);
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptorBuilder = new StringBuilder(
			"This step upgrades descriptor XML DTD versions from 6.2 to 7.0 or 7.1 and ");

		descriptorBuilder.append("deletes the wap-template-path \ntag from liferay-layout-template.xml files.\n");
		descriptorBuilder.append(
			"Double click the file in the list. It will popup a comparison page which shows the differences\n");
		descriptorBuilder.append("between your original source file and the upgrade preview file.");

		String url = "";

		Link link = SWTUtil.createHyperLink(this, style, descriptorBuilder.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public int getGridLayoutCount() {
		return 2;
	}

	@Override
	public String getPageTitle() {
		return "Upgrade Descriptor Files";
	}

	@Override
	protected void createTempFile(IFile srcFile, File templateFile, String projectName) {
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForEdit(srcFile);

			domModel.aboutToChangeModel();

			IDOMDocument document = domModel.getDocument();

			DocumentTypeImpl docType = (DocumentTypeImpl)document.getDoctype();

			if (docType != null) {
				final String publicId = docType.getPublicId();

				final String newPublicId = _getNewDoctTypeSetting(publicId, getUpgradeVersion(), _PUBLICID_REGREX);

				docType.setPublicId(newPublicId);

				final String systemId = docType.getSystemId();

				final String newSystemId = _getNewDoctTypeSetting(
					systemId, getUpgradeVersion().replaceAll("\\.", "_"), _SYSTEMID_REGREX);

				docType.setSystemId(newSystemId);
			}

			_removeLayoutWapNode(srcFile, document);

			try (OutputStream tmpOutputStream = Files.newOutputStream(
					templateFile.toPath(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW)) {

				domModel.save(tmpOutputStream);
			}
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}
		finally {
			domModel.releaseFromEdit();
		}
	}

	@Override
	protected void doUpgrade(IFile srcFile, IProject project) {
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForEdit(srcFile);

			domModel.aboutToChangeModel();

			IDOMDocument document = domModel.getDocument();

			DocumentTypeImpl docType = (DocumentTypeImpl)document.getDoctype();

			if (docType != null) {
				final String publicId = docType.getPublicId();

				final String newPublicId = _getNewDoctTypeSetting(publicId, getUpgradeVersion(), _PUBLICID_REGREX);

				docType.setPublicId(newPublicId);

				final String systemId = docType.getSystemId();

				final String newSystemId = _getNewDoctTypeSetting(
					systemId, getUpgradeVersion().replaceAll("\\.", "_"), _SYSTEMID_REGREX);

				docType.setSystemId(newSystemId);
			}

			_removeLayoutWapNode(srcFile, document);
			domModel.save();
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	@Override
	protected IFile[] getAvaiableUpgradeFiles(IProject project) {
		if (FileUtil.notExists(project)) {
			return new IFile[0];
		}

		if (!project.isOpen()) {
			return new IFile[0];
		}

		List<IFile> files = new ArrayList<>();

		for (String[] descriptors : _DESCRIPTORS_AND_IMAGES) {
			final String searchName = descriptors[0];

			List<IFile> searchFiles = new SearchFilesVisitor().searchFiles(project, searchName);

			files.addAll(searchFiles);
		}

		return files.toArray(new IFile[files.size()]);
	}

	@Override
	protected CellLabelProvider getLableProvider() {
		return new LiferayUpgradeTabeViewLabelProvider("Upgrade Descriptors") {

			@Override
			public Image getImage(Object element) {
				if (element instanceof LiferayUpgradeElement) {
					final String itemName = ((LiferayUpgradeElement)element).getFileName();

					return getImageRegistry().get(itemName);
				}

				return null;
			}

			@Override
			protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
				for (String[] descriptorsAndImages : _DESCRIPTORS_AND_IMAGES) {
					final String descName = descriptorsAndImages[0];
					final String descImage = descriptorsAndImages[1];

					imageRegistry.put(descName, ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, descImage));
				}
			}

		};
	}

	@Override
	protected boolean isUpgradeNeeded(IFile srcFile) {
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(srcFile);

			domModel.aboutToChangeModel();

			IDOMDocument document = domModel.getDocument();

			DocumentType docType = document.getDoctype();

			if (docType != null) {
				final String publicId = docType.getPublicId();

				String oldPublicIdVersion = _getOldVersion(publicId, _PUBLICID_REGREX);

				final String systemId = docType.getSystemId();

				String oldSystemIdVersion = _getOldVersion(systemId, _SYSTEMID_REGREX);

				if (((publicId != null) && !oldPublicIdVersion.equals(getUpgradeVersion())) ||
					((systemId != null) && !oldSystemIdVersion.equals(getUpgradeVersion().replaceAll("\\.", "_")))) {

					return true;
				}
			}
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
		finally {
			domModel.releaseFromRead();
		}

		return false;
	}

	private String _getNewDoctTypeSetting(String doctypeSetting, String newValue, String regrex) {
		String newDoctTypeSetting = null;

		Pattern p = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher m = p.matcher(doctypeSetting);

		if (m.find()) {
			String oldVersionString = m.group(m.groupCount());

			newDoctTypeSetting = doctypeSetting.replace(oldVersionString, newValue);
		}

		return newDoctTypeSetting;
	}

	private String _getOldVersion(final String sourceDTDVersion, final String regrex) {
		if (sourceDTDVersion == null) {
			return null;
		}

		Pattern p = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher m = p.matcher(sourceDTDVersion);

		if (m.find()) {
			String oldVersionString = m.group(m.groupCount());

			return oldVersionString;
		}

		return null;
	}

	private void _removeLayoutWapNode(IFile srcFile, IDOMDocument document) {
		if (FileUtil.nameEquals(srcFile, "liferay-layout-templates.xml")) {
			NodeList nodeList = document.getElementsByTagName("wap-template-path");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				Node parentNode = node.getParentNode();

				parentNode.removeChild(node);
			}
		}
	}

	private static final String[][] _DESCRIPTORS_AND_IMAGES = {
		{"liferay-portlet.xml", "/icons/e16/portlet.png"},
		{"liferay-display.xml", "/icons/e16/liferay_display_xml.png"}, {"service.xml", "/icons/e16/service.png"},
		{"liferay-hook.xml", "/icons/e16/hook.png"}, {"liferay-layout-templates.xml", "/icons/e16/layout.png"},
		{"liferay-look-and-feel.xml", "/icons/e16/theme.png"}, {"liferay-portlet-ext.xml", "/icons/e16/ext.png"}
	};

	private static final String _PUBLICID_REGREX =
		"-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

	private static final String _SYSTEMID_REGREX =
		"^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

}