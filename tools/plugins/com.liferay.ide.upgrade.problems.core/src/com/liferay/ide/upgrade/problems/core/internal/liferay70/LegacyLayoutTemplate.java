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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

/**
 * @author Seiphon Wang
 */
@Component(property = {
		"file.extensions=tpl", "problem.title=Layout Template breaking change",
		"problem.summary=The breaking change of Layout Template.",
		"problem.section=#layout-template", "auto.correct=layout-template", "implName=LegacyLayoutTemplate",
		"version=7.0"
	},
		service = {AutoFileMigrator.class, FileMigrator.class})
@SuppressWarnings("restriction")
public class LegacyLayoutTemplate extends XMLFileMigrator implements AutoFileMigrator {

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
		IFile tplFile = getXmlFile(file);

		if (tplFile == null) {
			return 0;
		}

		int problemsCorrected = 0;
		IDOMModel tplDOMModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			tplDOMModel = (IDOMModel)modelManager.getModelForEdit(tplFile);

			List<IDOMElement> elementsToCorrect = new ArrayList<>();

			for (UpgradeProblem upgradeProblem : upgradeProblems) {
				if (_KEY.equals(upgradeProblem.getAutoCorrectContext())) {
					IndexedRegion indexedRegion = tplDOMModel.getIndexedRegion(upgradeProblem.getStartOffset());

					if (indexedRegion instanceof IDOMElement) {
						IDOMElement element = (IDOMElement)indexedRegion;

						elementsToCorrect.add(element);
					}
				}
			}

			for (IDOMElement domElement : elementsToCorrect) {
				tplDOMModel.aboutToChangeModel();

				String content = _upgradeLayoutTplContent(domElement.getAttribute("class"));

				domElement.setAttribute("class", content);

				tplDOMModel.changedModel();

				problemsCorrected++;
			}

			tplDOMModel.save();
		}
		catch (Exception e) {
		}
		finally {
			if (tplDOMModel != null) {
				tplDOMModel.releaseFromEdit();
			}
		}

		return problemsCorrected;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		for (Pattern pattern : _PATTERNS) {
			results.addAll(xmlFileChecker.findElementAttribute("div", pattern));
		}

		return results;
	}

	private String _upgradeLayoutTplContent(String content) {
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

	private static final String _KEY = "layout-template:css-class";

	private static final Pattern[] _PATTERNS = {
		Pattern.compile(".*[\\s]{0,1}row-fluid", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
		Pattern.compile(".*[\\s]{0,1}span[0-9]{1,2}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
	};
}