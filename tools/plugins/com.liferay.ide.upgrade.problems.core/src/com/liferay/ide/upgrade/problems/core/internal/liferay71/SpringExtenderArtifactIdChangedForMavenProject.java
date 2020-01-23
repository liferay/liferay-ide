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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;
import com.liferay.ide.upgrade.problems.core.internal.XMLFileMigrator;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.service.component.annotations.Component;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=xml", "problem.title=The artifactId of Spring Extender has changed",
		"problem.tickets=LPS-85710", "problem.summary=The artifactid of spring extender has been changed",
		"problem.section=#spring-extender-artifactid-changed", "version=7.1", "auto.correct=dependency-maven"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
@SuppressWarnings("restriction")
public class SpringExtenderArtifactIdChangedForMavenProject extends XMLFileMigrator implements AutoFileMigrator {

	@Override
	@SuppressWarnings("deprecation")
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
		int problemsFixed = 0;
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			try (InputStream input = Files.newInputStream(Paths.get(file.toURI()), StandardOpenOption.READ)) {
				domModel = (IDOMModel)modelManager.getModelForEdit(file.getAbsolutePath(), input, null);
			}

			List<IDOMElement> elementsToCorrect = new ArrayList<>();

			for (UpgradeProblem upgradeProblem : upgradeProblems) {
				if (_autoCorrectContext.equals(upgradeProblem.getAutoCorrectContext())) {
					IndexedRegion indexedRegion = domModel.getIndexedRegion(upgradeProblem.getStartOffset());

					if (indexedRegion instanceof IDOMElement) {
						IDOMElement element = (IDOMElement)indexedRegion;

						elementsToCorrect.add(element);
					}
				}
			}

			for (IDOMElement domElement : elementsToCorrect) {
				domModel.aboutToChangeModel();

				NodeList artifactNodeChilds = domElement.getChildNodes();

				Node artifactIdNode = artifactNodeChilds.item(0);

				artifactIdNode.setNodeValue(_newSpringExtenderArtifactId);

				Node parentNode = domElement.getParentNode();

				NodeList childNodes = parentNode.getChildNodes();

				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);

					if ("version".equals(childNode.getNodeName())) {
						NodeList versionNodeChilds = childNode.getChildNodes();

						Node versionNode = versionNodeChilds.item(0);

						versionNode.setNodeValue("3.0.0");
					}
				}

				domModel.changedModel();

				problemsFixed++;
			}

			if (problemsFixed > 0) {
				try (OutputStream output = Files.newOutputStream(
						Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.DSYNC)) {

					domModel.save(output);
				}
			}
		}
		catch (Exception e) {
			throw new AutoFileMigrateException("Error writing corrected file", e);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromEdit();
			}
		}

		return problemsFixed;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		results.addAll(xmlFileChecker.findElement("artifactId", _springExtenderArtifactId));

		for (FileSearchResult result : results) {
			result.autoCorrectContext = _autoCorrectContext;
		}

		return results;
	}

	private String _autoCorrectContext = "dependency-maven:artifactId";
	private String _newSpringExtenderArtifactId = "com.liferay.portal.spring.extender.api";
	private String _springExtenderArtifactId = "com.liferay.portal.spring.extender";

}