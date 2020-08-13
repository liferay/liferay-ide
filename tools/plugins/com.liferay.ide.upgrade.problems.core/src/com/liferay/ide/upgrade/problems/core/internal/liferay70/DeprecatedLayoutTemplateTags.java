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

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=xml", "problem.title=Deprecated wap-template-path tag on 7.x for layout templates",
		"problem.summary=The <wap-template-path> tag was deprecated on 7.x for layout-templates.",
		"auto.correct=wap-template-path", "problem.section=#deprecated-wap-template-path-for-layout-templates",
		"version=7.0"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
@SuppressWarnings({"restriction", "deprecation"})
public class DeprecatedLayoutTemplateTags extends XMLFileMigrator implements AutoFileMigrator {

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigratorException {
		int problemsCorrected = 0;
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			try (InputStream input = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
				domModel = (IDOMModel)modelManager.getModelForEdit(file.getAbsolutePath(), input, null);
			}

			List<IDOMElement> elementsToCorrect = new ArrayList<>();

			for (UpgradeProblem upgradeProblem : upgradeProblems) {
				if (_KEY.equals(upgradeProblem.getAutoCorrectContext())) {
					IndexedRegion indexedRegion = domModel.getIndexedRegion(upgradeProblem.getStartOffset());

					if (indexedRegion instanceof IDOMElement) {
						IDOMElement element = (IDOMElement)indexedRegion;

						elementsToCorrect.add(element);
					}
				}
			}

			for (IDOMElement element : elementsToCorrect) {
				domModel.aboutToChangeModel();

				IDOMElement parentElement = (IDOMElement)element.getParentNode();

				parentElement.removeChild(element);

				domModel.changedModel();

				problemsCorrected++;
			}

			if (problemsCorrected > 0) {
				try (OutputStream output = Files.newOutputStream(
						file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.DSYNC)) {

					domModel.save(output);
				}
			}
		}
		catch (Exception e) {
			throw new AutoFileMigratorException("Error writing corrected file", e);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromEdit();
			}
		}

		return problemsCorrected;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		if (!Objects.equals("liferay-layout-templates.xml", file.getName())) {
			return Collections.emptyList();
		}

		List<FileSearchResult> results = new ArrayList<>();

		Collection<FileSearchResult> tags = xmlFileChecker.findElement(_KEY);

		for (FileSearchResult tagResult : tags) {
			tagResult.autoCorrectContext = _KEY;
		}

		results.addAll(tags);

		return results;
	}

	private static final String _KEY = "wap-template-path";

}