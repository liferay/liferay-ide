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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=xml",
	"problem.summary=The classes from package com.liferay.util.bridges.mvc in util-bridges.jar were moved to a new " +
		"package com.liferay.portal.kernel.portlet.bridges.mvc in portal-service.jar.",
	"problem.tickets=LPS-50156",
	"problem.title=Moved MVCPortlet, ActionCommand and ActionCommandCache from util-bridges.jar to portal-service.jar",
	"problem.section=#moved-mvcportlet-actioncommand-and-actioncommandcache-from-util-bridges-jar",
	 "version=7.0"
},
	service = {AutoFileMigrator.class, FileMigrator.class})
@SuppressWarnings("restriction")
public class MVCPortletClassInPortletXML extends XMLFileMigrator implements AutoFileMigrator {

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
		int corrected = 0;
		IFile xmlFile = getXmlFile(file);
		IDOMModel xmlModel = null;

		if (xmlFile != null) {
			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				xmlModel = (IDOMModel)modelManager.getModelForEdit(xmlFile);

				List<IDOMElement> elementsToCorrect = new ArrayList<>();

				for (UpgradeProblem upgradeProblem : upgradeProblems) {
					if (_KEY.equals(upgradeProblem.getAutoCorrectContext())) {
						IndexedRegion region = xmlModel.getIndexedRegion(upgradeProblem.getStartOffset());

						if (region instanceof IDOMElement) {
							IDOMElement element = (IDOMElement)region;

							elementsToCorrect.add(element);
						}
					}
				}

				for (IDOMElement element : elementsToCorrect) {
					xmlModel.aboutToChangeModel();

					_removeChildren(element);

					Document document = element.getOwnerDocument();

					Text textContent = document.createTextNode(
						"com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet");

					element.appendChild(textContent);

					xmlModel.changedModel();

					corrected++;
				}

				xmlModel.save();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (xmlModel != null) {
					xmlModel.releaseFromEdit();
				}
			}
		}

		File xml = FileUtil.getFile(xmlFile);

		if ((corrected > 0) && !xml.equals(file)) {
			try (InputStream xmlFileContent = xmlFile.getContents()) {
				Files.copy(xmlFileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (Exception e) {
				throw new AutoFileMigrateException("Error writing corrected file", e);
			}
		}

		return corrected;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!"portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		List<FileSearchResult> results = new ArrayList<>();

		Collection<FileSearchResult> tags = xmlFileChecker.findElement(
			"portlet-class", "com.liferay.util.bridges.mvc.MVCPortlet");

		for (FileSearchResult tagResult : tags) {
			tagResult.autoCorrectContext = _KEY;
		}

		results.addAll(tags);

		return results;
	}

	private void _removeChildren(IDOMElement element) {
		while (element.hasChildNodes()) {
			element.removeChild(element.getFirstChild());
		}
	}

	private static final String _KEY = "portlet-xml-portlet-class";

}