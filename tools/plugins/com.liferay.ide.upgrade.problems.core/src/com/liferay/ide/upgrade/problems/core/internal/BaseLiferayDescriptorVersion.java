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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Seiphon Wang
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public abstract class BaseLiferayDescriptorVersion extends XMLFileMigrator implements AutoFileMigrator {

	public BaseLiferayDescriptorVersion(Pattern publicIDPattern, String version) {
		_idPattern = publicIDPattern;
		_version = version;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
		int problemsCorrected = 0;
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			try (InputStream input = Files.newInputStream(Paths.get(file.toURI()), StandardOpenOption.READ)) {
				domModel = (IDOMModel)modelManager.getModelForEdit(file.getAbsolutePath(), input, null);
			}

			IDOMDocument domDocument = domModel.getDocument();

			IDOMDocumentType domDocumentType = (IDOMDocumentType)domDocument.getDoctype();

			if (domDocumentType != null) {
				String publicId = domDocumentType.getPublicId();

				String newPublicId = _getNewDoctTypeSetting(publicId, _version, _publicPattern);

				domDocumentType.setPublicId(newPublicId);

				String systemId = domDocumentType.getSystemId();

				String newSystemId = _getNewDoctTypeSetting(systemId, _version.replaceAll("\\.", "_"), _systemPattern);

				domDocumentType.setSystemId(newSystemId);

				problemsCorrected++;
			}

			if (problemsCorrected > 0) {
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

		return problemsCorrected;
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, XMLFile xmlFileChecker) {
		List<FileSearchResult> results = new ArrayList<>();

		for (String liferayDtdName : _liferayDtdNames) {
			results.add(xmlFileChecker.findDocumentTypeDeclaration(liferayDtdName, _idPattern));
		}

		return results;
	}

	private String _getNewDoctTypeSetting(String doctypeSetting, String newValue, Pattern pattern) {
		String newDoctTypeSetting = null;

		Matcher m = pattern.matcher(doctypeSetting);

		if (m.find()) {
			String oldVersionString = m.group(m.groupCount());

			newDoctTypeSetting = doctypeSetting.replace(oldVersionString, newValue);
		}

		return newDoctTypeSetting;
	}

	private static final Pattern _publicPattern = Pattern.compile(
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[A-z]+)",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern _systemPattern = Pattern.compile(
		"^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private Pattern _idPattern;
	private String[] _liferayDtdNames = {
		"liferay-portlet-app", "display", "service-builder", "hook", "layout-templates", "look-and-feel"
	};
	private String _version;

}