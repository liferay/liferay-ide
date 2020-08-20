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

import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;
import com.liferay.ide.upgrade.problems.core.CUCache;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JavaFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("rawtypes")
public abstract class JavaImportsMigrator extends AbstractFileMigrator<JavaFile> implements AutoFileMigrator {

	public static String getPrefix() {
		return _PREFIX;
	}

	public JavaImportsMigrator() {
		super(JavaFile.class);
	}

	public JavaImportsMigrator(Map<String, String> importFixes) {
		super(JavaFile.class);

		_importFixes = importFixes;
	}

	@Override
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigratorException {
		IProject project = _getProject(file);

		IFile buildGradle = null;

		if (project != null) {
			buildGradle = project.getFile("build.gradle");
		}

		int problemsFixed = 0;

		List<String> importsToRewrite = new ArrayList<>();

		for (UpgradeProblem upgradeProblem : upgradeProblems) {
			boolean problemFound = false;

			if (upgradeProblem.getAutoCorrectContext() instanceof String) {
				String importData = upgradeProblem.getAutoCorrectContext();

				if ((importData != null) && importData.startsWith(_PREFIX)) {
					String importValue = importData.substring(_PREFIX.length());

					if (_importFixes.containsKey(importValue)) {
						importsToRewrite.add(upgradeProblem.getLineNumber() + "," + importValue);

						problemFound = true;
					}
				}
			}

			if (problemFound) {
				problemsFixed++;
			}
		}

		if (_isNotEmpty(importsToRewrite)) {
			List<GradleDependency> needToAddDependencies = new ArrayList<>();

			try (InputStream inputStream = Files.newInputStream(file.toPath())) {
				String[] lines = _readLines(inputStream);

				String[] editedLines = new String[lines.length];

				System.arraycopy(lines, 0, editedLines, 0, lines.length);

				for (String importData : importsToRewrite) {
					String[] importMap = importData.split(",");

					try {
						int lineNumber = Integer.parseInt(importMap[0]);

						String importName = importMap[1];

						if ((lineNumber > 0) && (lineNumber < editedLines.length)) {
							String importFix = _importFixes.get(importName);

							String[] s = importFix.split(",");

							String newImport = s[0];

							editedLines[lineNumber - 1] = editedLines[lineNumber - 1].replaceAll(importName, newImport);

							if (s.length == 3) {
								needToAddDependencies.add(
									new GradleDependency("compileOnly", s[1], s[2], null, -1, -1));
							}
						}
					}
					catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
				}

				StringBuilder sb = new StringBuilder();

				for (String editedLine : editedLines) {
					sb.append(editedLine);
					sb.append(System.getProperty("line.separator"));
				}

				try (FileWriter writer = new FileWriter(file)) {
					writer.write(sb.toString());
				}

				if (_exists(buildGradle) && !needToAddDependencies.isEmpty()) {
					File buildGradleFile = _getFile(buildGradle);

					GradleBuildScript gradleBuildScript = new GradleBuildScript(buildGradleFile);

					for (GradleDependency dependency : needToAddDependencies) {
						gradleBuildScript.insertDependency(dependency);
					}

					String contents = gradleBuildScript.getFileContents(
					).stream(
					).collect(
						Collectors.joining(System.lineSeparator())
					);

					Files.write(buildGradleFile.toPath(), contents.getBytes());
				}

				_clearCache(file);

				return problemsFixed;
			}
			catch (IOException ioe) {
				throw new AutoFileMigratorException("Unable to auto-correct", ioe);
			}
		}

		return 0;
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		Path path = new Path(file.getAbsolutePath());

		JavaFile javaFile = createFileService(type, file, path.getFileExtension());

		javaFile.setFile(file);

		return upgradeProblems.stream(
		).map(
			problem -> {
				try {
					javaFile.appendComment(problem.getLineNumber(), problem.getTicket());

					return 1;
				}
				catch (IOException e) {
					e.printStackTrace(System.err);
				}

				return 0;
			}
		).reduce(
			0, Integer::sum
		);
	}

	@Override
	public List<FileSearchResult> searchFile(File file, JavaFile javaFile) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		for (String importName : _importFixes.keySet()) {
			FileSearchResult importResult = javaFile.findImport(importName);

			if (importResult != null) {
				importResult.autoCorrectContext = _PREFIX + importName;

				searchResults.add(importResult);
			}
		}

		return searchResults;
	}

	public void setImportFixes(Map<String, String> importFixes) {
		_importFixes = importFixes;
	}

	private static boolean _exists(IFile file) {
		if ((file != null) && file.exists()) {
			return true;
		}

		return false;
	}

	private static IResource _filterIResouece(IResource[] resources) {
		IResource result = null;

		for (IResource resource : resources) {
			if (result == null) {
				result = resource;
			}
			else {
				IPath filePath = resource.getProjectRelativePath();
				IPath resourcePath = result.getProjectRelativePath();

				if (filePath.segmentCount() < resourcePath.segmentCount()) {
					result = resource;
				}
			}
		}

		if (result == null) {
			return null;
		}

		return result;
	}

	private static File _getFile(IFile file) {
		if (file == null) {
			return null;
		}

		IPath location = file.getLocation();

		return location.toFile();
	}

	private static IProject _getProject(File file) {
		IWorkspaceRoot ws = _getWorkspaceRoot();

		IResource[] containers = ws.findContainersForLocationURI(file.toURI());

		IResource resource = _filterIResouece(containers);

		if (resource == null) {
			return null;
		}

		return resource.getProject();
	}

	private static IWorkspaceRoot _getWorkspaceRoot() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		return workspace.getRoot();
	}

	private static boolean _isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

	private static boolean _isNotEmpty(List<?> list) {
		return !_isEmpty(list);
	}

	private static String[] _readLines(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputReader)) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[0]);
	}

	private void _clearCache(File file) {
		try {
			Collection<ServiceReference<CUCache>> src = context.getServiceReferences(CUCache.class, null);

			for (ServiceReference<CUCache> sr : src) {
				CUCache cache = context.getService(sr);

				cache.unget(file);
			}
		}
		catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
	}

	private static final String _PREFIX = "import:";

	private Map<String, String> _importFixes;

}