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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;
import com.liferay.blade.upgrade.XMLFileMigrator;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.service.component.annotations.Component;

import org.w3c.dom.Text;

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
	"implName=MVCPortletClassInPortletXML", "auto.correct=portlet-xml-portlet-class", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
@SuppressWarnings("restriction")
public class MVCPortletClassInPortletXML extends XMLFileMigrator implements AutoMigrator {

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int corrected = 0;
		IFile xmlFile = getXmlFile(file);
		IDOMModel xmlModel = null;

		if (xmlFile != null) {
			try {
				xmlModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForEdit(xmlFile);

				List<IDOMElement> elementsToCorrect = new ArrayList<>();

				for (Problem problem : problems) {
					if (_KEY.equals(problem.autoCorrectContext)) {
						IndexedRegion region = xmlModel.getIndexedRegion(problem.startOffset);

						if (region instanceof IDOMElement) {
							IDOMElement element = (IDOMElement)region;

							elementsToCorrect.add(element);
						}
					}
				}

				for (IDOMElement element : elementsToCorrect) {
					xmlModel.aboutToChangeModel();

					_removeChildren(element);

					Text textContent = element.getOwnerDocument().createTextNode(
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

		IPath location = xmlFile.getLocation();

		if ((corrected > 0) && !location.toFile().equals(file)) {
			try (InputStream xmlFileContent = xmlFile.getContents()) {
				Files.copy(xmlFileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (Exception e) {
				throw new AutoMigrateException("Error writing corrected file.", e);
			}
		}

		return corrected;
	}

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!"portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		List<SearchResult> results = new ArrayList<>();

		Collection<SearchResult> tags = xmlFileChecker.findElement(
			"portlet-class", "com.liferay.util.bridges.mvc.MVCPortlet");

		for (SearchResult tagResult : tags) {
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