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

package com.liferay.ide.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class PropertiesUtil {

	public static final String ELEMENT_LANGUAGE_PROPERTIES = "language-properties";

	public static final String ELEMENT_PORTAL_PROPERTIES = "portal-properties";

	public static final String ELEMENT_PORTLET = "portlet";

	public static final String ELEMENT_RESOURCE_BUNDLE = "resource-bundle";

	public static final String ELEMENT_SUPPORTED_LOCALE = "supported-locale";

	public static final String PROPERTIES_FILE_SUFFIX = ".properties";

	public static void encodeLanguagePropertiesFilesToDefault(IResource resource, IProgressMonitor monitor) {
		if (resource.getType() == IResource.PROJECT) {
			IFile[] languagePropertiesFiles = getAllLanguagePropertiesFiles((IProject)resource);

			for (IFile file : languagePropertiesFiles) {
				_encodeLanguagePropertyFile(file, monitor);
			}
		}
		else if (resource.getType() == IResource.FILE) {
			IFile file = (IFile)resource;

			_encodeLanguagePropertyFile(file, monitor);
		}
	}

	/**
	 * Convert the element values of <resource-bundle> in portlet.xml and
	 * <language-properties> in liferay-hook.xml to the corresponding regular
	 * expression to match the local files. The return values is: String[0] is base
	 * value of normal format without suffix, String[1] is a regex. Both may be
	 * null, check them before using them.
	 */
	public static String[] generatePropertiesNamePatternsForEncoding(String baseValue, String elementName) {
		baseValue = baseValue.replaceAll("(^\\s*)|(\\s*$)", StringPool.BLANK);

		String regex = null;

		if (elementName.equals(ELEMENT_RESOURCE_BUNDLE)) {
			if (baseValue.endsWith(PROPERTIES_FILE_SUFFIX) || baseValue.contains(IPath.SEPARATOR + "") ||
				(CoreUtil.isWindows() && baseValue.contains("\\"))) {

				return new String[0];
			}

			baseValue = new Path(baseValue.replace(".", IPath.SEPARATOR + "")).toString();

			if (!baseValue.contains("_")) {
				regex = baseValue + "_.*";
			}
		}
		else if (elementName.equals(ELEMENT_LANGUAGE_PROPERTIES)) {
			if (!baseValue.endsWith(PROPERTIES_FILE_SUFFIX)) {
				return new String[0];
			}

			baseValue = new Path(baseValue.replace(PROPERTIES_FILE_SUFFIX, "")).toString();

			if (baseValue.contains("*")) {
				regex = baseValue.replace("*", ".*");

				baseValue = null;
			}
			else {
				if (!baseValue.contains("_")) {
					regex = baseValue + "_.*";
				}
			}
		}

		return new String[] {baseValue, regex};
	}

	/**
	 *  Cleaning the baseValue has been done in the validator, no need to do the same as method generatePropertiesNamePatternsForEncoding()
	 */
	public static String[] generatePropertiesNamePatternsForValidation(String baseValue, String elementName) {
		String regex = null;

		if (elementName.equals(ELEMENT_RESOURCE_BUNDLE)) {
			baseValue = new Path(baseValue.replace(".", IPath.SEPARATOR + "")).toString();
		}
		else if (elementName.equals(ELEMENT_PORTAL_PROPERTIES)) {
			baseValue = new Path(baseValue.replace(PROPERTIES_FILE_SUFFIX, "")).toString();
		}
		else if (elementName.equals(ELEMENT_LANGUAGE_PROPERTIES)) {
			baseValue = new Path(baseValue.replace(PROPERTIES_FILE_SUFFIX, "")).toString();

			if (baseValue.contains("*")) {
				regex = baseValue.replace("*", ".*");

				baseValue = null;
			}
		}

		String[] retval = {baseValue, regex};

		return retval;
	}

	/**
	 *  Get all the language properties files referenced from portlet.xml and liferay-hook.xml
	 */
	public static IFile[] getAllLanguagePropertiesFiles(IProject project) {
		List<IFile> retval = new ArrayList<>();

		if (!CoreUtil.isLiferayProject(project)) {
			project = CoreUtil.getLiferayProject(project);
		}

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if (webProject == null) {
			return new IFile[0];
		}

		IFile[] resourceFiles = getLanguagePropertiesFromPortletXml(
			webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE));

		IFile[] languageFiles = getLanguagePropertiesFromLiferayHookXml(
			webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE));

		if (ListUtil.isNotEmpty(resourceFiles)) {
			Collections.addAll(retval, resourceFiles);
		}

		if (ListUtil.isNotEmpty(languageFiles)) {
			Collections.addAll(retval, languageFiles);
		}

		return retval.toArray(new IFile[0]);
	}

	public static List<IFile> getDefaultLanguagePropertiesFromModuleProject(IProject project) {
		IJavaProject javaProject = JavaCore.create(project);

		IType portletType = null;

		List<IFile> retvals = new ArrayList<>();

		try {
			portletType = javaProject.findType("javax.portlet.Portlet");

			ITypeHierarchy typeHierarchy = portletType.newTypeHierarchy(javaProject, new NullProgressMonitor());

			IPackageFragmentRoot[] packageRoots = javaProject.getPackageFragmentRoots();

			List<String> packages = new ArrayList<>();

			List<IType> srcJavaTypes = new ArrayList<>();

			for (IPackageFragmentRoot packageRoot : packageRoots) {
				if (packageRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
					IJavaElement[] javaElements = packageRoot.getChildren();

					for (IJavaElement javaElement : javaElements) {
						IPackageFragment packageFragment = (IPackageFragment)javaElement;

						packages.add(packageFragment.getElementName());
					}
				}
			}

			IType[] subtypes = typeHierarchy.getAllSubtypes(portletType);

			for (IType type : subtypes) {
				if (_isInPackage(packages, type.getFullyQualifiedName())) {
					srcJavaTypes.add(type);
				}
			}

			String resourceBundleValue = null;

			for (IType type : srcJavaTypes) {
				IPath path = type.getResource().getLocation();

				File file = path.toFile();

				String content = FileUtil.readContents(file);

				String key = "javax.portlet.resource-bundle=";

				int i = content.indexOf(key);

				if (i == -1) {
					continue;
				}
				else {
					i += key.length();

					StringBuilder strBuilder = new StringBuilder();

					for (; i < content.length(); i++) {
						char ch = content.charAt(i);

						if (ch != '"') {
							strBuilder.append(ch);
						}
						else {
							break;
						}
					}

					resourceBundleValue = strBuilder.toString();

					// find the first language config

					break;
				}
			}

			String resourceBundle = resourceBundleValue.replaceAll("(^\\s*)|(\\s*$)", StringPool.BLANK);

			if (!resourceBundle.endsWith(PROPERTIES_FILE_SUFFIX) && !resourceBundle.contains(IPath.SEPARATOR + "") &&
				!(CoreUtil.isWindows() && resourceBundle.contains("\\"))) {

				resourceBundle = new Path(resourceBundle.replace(".", IPath.SEPARATOR + "")).toString();
			}

			ILiferayProject lrproject = LiferayCore.create(project);

			IFolder[] srcFolders = lrproject.getSourceFolders();

			for (IFolder srcFolder : srcFolders) {
				IPath path = srcFolder.getFullPath().append(resourceBundle + PROPERTIES_FILE_SUFFIX);

				IFile languageFile = CoreUtil.getWorkspaceRoot().getFile(path);

				if (FileUtil.exists(languageFile)) {
					retvals.add(languageFile);
				}
			}
		}
		catch (Exception e) {
		}

		return retvals;
	}

	public static List<IFile> getDefaultLanguagePropertiesFromPortletXml(IFile portletXml) {
		IProject proj = CoreUtil.getLiferayProject(portletXml);

		if (proj == null) {
			return Collections.emptyList();
		}

		List<IFile> retvals = new ArrayList<>();

		if (FileUtil.notExists(portletXml)) {
			return Collections.emptyList();
		}

		ILiferayProject lrproject = LiferayCore.create(proj);

		IFolder[] srcFolders = lrproject.getSourceFolders();

		ResourceNodeInfo resourceNodeInfo = _getResourceNodeInfo(portletXml);

		Set<String> resourceBundles = resourceNodeInfo.getResourceBundles();

		if (ListUtil.isEmpty(resourceBundles)) {
			return Collections.emptyList();
		}

		for (int i = 0; i < resourceBundles.size(); i++) {
			String resourceBundleValue = (String)resourceBundles.toArray()[i];

			for (IFolder srcFolder : srcFolders) {
				IPath path = srcFolder.getFullPath().append(resourceBundleValue + PROPERTIES_FILE_SUFFIX);

				IFile languageFile = CoreUtil.getWorkspaceRoot().getFile(path);

				if (FileUtil.exists(languageFile)) {
					retvals.add(languageFile);
				}
			}
		}

		return retvals;
	}

	public static List<IFile> getDefaultLanguagePropertiesFromProject(IProject project) {
		IResourceBundleProject resourceBundleProject = LiferayCore.create(IResourceBundleProject.class, project);

		if (resourceBundleProject != null) {
			return resourceBundleProject.getDefaultLanguageProperties();
		}

		return Collections.emptyList();
	}

	public static IFile[] getLanguagePropertiesFromLiferayHookXml(IFile liferayHookXml) {
		List<IFile> retval = new ArrayList<>();

		IProject project = CoreUtil.getLiferayProject(liferayHookXml);

		if (project == null) {
			return new IFile[0];
		}

		ILiferayProject lrproject = LiferayCore.create(project);

		IFolder[] srcFolders = lrproject.getSourceFolders();

		if (ListUtil.isEmpty(srcFolders)) {
			return new IFile[0];
		}

		if (FileUtil.notExists(liferayHookXml)) {
			return new IFile[0];
		}

		LanguageFileInfo languageFileInfo = _getLanguageFileInfo(liferayHookXml);

		for (String languagePropertiesVal : languageFileInfo.getLanguagePropertyPatterns()) {
			for (IFolder srcFolder : srcFolders) {
				if (FileUtil.notExists(srcFolder)) {
					continue;
				}

				IFile[] languagePropertiesFiles = visitPropertiesFiles(srcFolder, languagePropertiesVal);

				if (ListUtil.isNotEmpty(languagePropertiesFiles)) {
					Collections.addAll(retval, languagePropertiesFiles);
				}
			}
		}

		return retval.toArray(new IFile[0]);
	}

	/**
	 *  Search all resource bundle and supported locale files referenced by portlet.xml.
	 */
	public static IFile[] getLanguagePropertiesFromPortletXml(IFile portletXml) {
		List<IFile> retval = new ArrayList<>();

		IProject project = CoreUtil.getLiferayProject(portletXml);

		if (project == null) {
			return new IFile[0];
		}

		ILiferayProject lrproject = LiferayCore.create(project);

		IFolder[] srcFolders = lrproject.getSourceFolders();

		if (ListUtil.isEmpty(srcFolders)) {
			return new IFile[0];
		}

		if (FileUtil.notExists(portletXml)) {
			return new IFile[0];
		}

		for (IFolder srcFolder : srcFolders) {
			if (FileUtil.notExists(srcFolder)) {
				continue;
			}

			ResourceNodeInfo resourceNodeInfo = _getResourceNodeInfo(portletXml);

			for (String resourceBundleValue : resourceNodeInfo.getResourceBundlePatterns()) {
				IFile[] resourceBundleFiles = visitPropertiesFiles(srcFolder, resourceBundleValue);

				if (ListUtil.isNotEmpty(resourceBundleFiles)) {
					Collections.addAll(retval, resourceBundleFiles);
				}
			}

			for (String supportedLocaleValue : resourceNodeInfo.getSupportedLocalePatterns()) {
				IFile[] supportedLocaleFiles = visitPropertiesFiles(srcFolder, supportedLocaleValue);

				if (ListUtil.isNotEmpty(supportedLocaleFiles)) {
					Collections.addAll(retval, supportedLocaleFiles);
				}
			}
		}

		return retval.toArray(new IFile[0]);
	}

	public static boolean hasNonDefaultEncodingLanguagePropertiesFile(IProject project) {
		if (!CoreUtil.isLiferayProject(project)) {
			project = CoreUtil.getLiferayProject(project);
		}

		if (project == null) {
			return false;
		}

		try {
			IWebProject webProject = LiferayCore.create(IWebProject.class, project);

			if (webProject == null) {
				return false;
			}

			IFile[] resourceFiles = getLanguagePropertiesFromPortletXml(
				webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE));

			for (IFile file : resourceFiles) {
				if (!ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals(file.getCharset())) {
					return true;
				}
			}

			IFile[] languageFiles = getLanguagePropertiesFromLiferayHookXml(
				webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE));

			for (IFile file : languageFiles) {
				if (!ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals(file.getCharset())) {
					return true;
				}
			}
		}
		catch (CoreException ce) {
			LiferayCore.logError(ce);
		}

		return false;
	}

	/**
	 *  Check if the file is a language properties file referenced from portlet.xml or liferay-hook.xml
	 */
	public static boolean isLanguagePropertiesFile(IFile targetFile) {
		if (!targetFile.getName().endsWith(PROPERTIES_FILE_SUFFIX)) {
			return false;
		}

		IProject project = CoreUtil.getLiferayProject(targetFile);

		if (project == null) {
			return false;
		}

		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if (webProject == null) {
			return false;
		}

		IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);
		IFile liferayHookXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE);
		IFolder[] srcFolders = webProject.getSourceFolders();

		IPath targetFileLocation = targetFile.getLocation();

		try {
			if (FileUtil.exists(portletXml)) {
				String[] resourceBundleValues = _getResourceNodeInfo(portletXml).getResourceBundlePatterns();

				for (String resourceBundleValue : resourceBundleValues) {
					for (IFolder srcFolder : srcFolders) {
						String location = targetFileLocation.makeRelativeTo(srcFolder.getLocation()).toString();

						if (location.replace(PROPERTIES_FILE_SUFFIX, "").matches(resourceBundleValue)) {
							return true;
						}
					}
				}

				String[] supportedLocaleValues = _getResourceNodeInfo(portletXml).getSupportedLocalePatterns();

				for (String suportedLocaleValue : supportedLocaleValues) {
					for (IFolder srcFolder : srcFolders) {
						String location = targetFileLocation.makeRelativeTo(srcFolder.getLocation()).toString();

						if (location.replace(PROPERTIES_FILE_SUFFIX, "").matches(suportedLocaleValue)) {
							return true;
						}
					}
				}
			}

			if (FileUtil.exists(liferayHookXml)) {
				String[] languagePropertyValues = _getLanguageFileInfo(liferayHookXml).getLanguagePropertyPatterns();

				for (String languagePropertyValue : languagePropertyValues) {
					for (IFolder srcFolder : srcFolders) {
						String location = targetFileLocation.makeRelativeTo(srcFolder.getLocation()).toString();

						if (location.replace(PROPERTIES_FILE_SUFFIX, "").matches(languagePropertyValue)) {
							return true;
						}
					}
				}
			}
		}
		catch (Exception e) {
			return false;
		}

		return false;
	}

	public static Properties loadProperties(File f) {
		Properties p = new Properties();

		try (InputStream stream = Files.newInputStream(f.toPath())) {
			p.load(stream);

			return p;
		}
		catch (IOException ioe) {
			return null;
		}
	}

	public static void saveProperties(Properties props, File resultFile) {
		try (OutputStream fos = Files.newOutputStream(resultFile.toPath())) {
			props.store(fos, "");
		}
		catch (Exception e) {
			LiferayCore.logError("Could not save file " + resultFile.getName());
		}
	}

	public static IFile[] visitPropertiesFiles(IResource container, String relativePath) {
		if (relativePath.contains("*")) {
			return new PropertiesVisitor().visitPropertiesFiles(container, relativePath);
		}

		try {
			IPath path = container.getFullPath().append(relativePath + PROPERTIES_FILE_SUFFIX);

			IFile file = CoreUtil.getWorkspaceRoot().getFile(path);

			if (FileUtil.exists(file)) {
				return new IFile[] {file};
			}
		}
		catch (Exception e) {
		}

		return new IFile[0];
	}

	private static void _encodeLanguagePropertyFile(IFile file, IProgressMonitor monitor) {
		try {
			String contents = CoreUtil.readStreamToString(file.getContents());

			file.setCharset(null, monitor);

			try( InputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"))){
				file.setContents(inputStream, IResource.FORCE, monitor);	
			}
		}
		catch (Exception e) {
			LiferayCore.logError(e);
		}
	}

	/**
	 * Search all language properties files referenced by liferay-hook.xml
	 */
	private static synchronized LanguageFileInfo _getLanguageFileInfo(IFile liferayHookXml) {
		if ((_tmpLanguageFileInfo == null) || !_tmpLanguageFileInfo.getLiferayHookXml().equals(liferayHookXml) ||
			(_tmpLanguageFileInfo.getModificationStamp() != liferayHookXml.getModificationStamp())) {

			LanguageFileInfo retval = new LanguageFileInfo(liferayHookXml);

			try {
				DefaultHandler handler = new DefaultHandler() {

					@Override
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (!_langPropElem) {
							return;
						}

						String languagePropertiesValue = new String(ch, start, length);

						if (languagePropertiesValue.endsWith(PROPERTIES_FILE_SUFFIX)) {
							String[] languagePropertiesPatterns = generatePropertiesNamePatternsForEncoding(
								languagePropertiesValue, ELEMENT_LANGUAGE_PROPERTIES);

							for (String pattern : languagePropertiesPatterns) {
								if (pattern != null) {
									retval.addLanguagePropertiesPattern(pattern);
								}
							}
						}
					}

					@Override
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equals(ELEMENT_LANGUAGE_PROPERTIES)) {
							_langPropElem = false;
						}
					}

					@Override
					public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

						if (qName.equals(ELEMENT_LANGUAGE_PROPERTIES)) {
							_langPropElem = true;
						}
					}

					private boolean _langPropElem = false;

				};

				try(InputStream contents = liferayHookXml.getContents()){
					SAXParser saxParser = _saxParserFactory.newSAXParser();

					XMLReader xmlReader = saxParser.getXMLReader();

					xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					xmlReader.setFeature("http://xml.org/sax/features/validation", false);

					saxParser.parse(contents, handler);
				}
			}
			catch (Exception e) {
				LiferayCore.logError("Error resolving " + ILiferayConstants.LIFERAY_HOOK_XML_FILE, e);
			}

			_tmpLanguageFileInfo = retval;
		}

		return _tmpLanguageFileInfo;
	}

	private static synchronized ResourceNodeInfo _getResourceNodeInfo(IFile portletXml) {
		if ((_tmpResourceNodeInfo == null) || !_tmpResourceNodeInfo.getPortletXml().equals(portletXml) ||
			(_tmpResourceNodeInfo.getModificationStamp() != portletXml.getModificationStamp())) {

			ResourceNodeInfo retval = new ResourceNodeInfo(portletXml);

			try {
				DefaultHandler handler = new DefaultHandler() {

					@Override
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (_supportedLocaleElem) {
							_supportedLocaleValues.add(new String(ch, start, length));
						}

						if (_resourceBundleElem) {
							_resourceBundleValue = new String(ch, start, length);
						}
					}

					@Override
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equals(ELEMENT_RESOURCE_BUNDLE)) {
							_resourceBundleElem = false;
						}

						if (qName.equals(ELEMENT_SUPPORTED_LOCALE)) {
							_supportedLocaleElem = false;
						}

						if (qName.equals(ELEMENT_PORTLET)) {
							if (!CoreUtil.isNullOrEmpty(_resourceBundleValue)) {
								String[] resourceBundlesPatterns = generatePropertiesNamePatternsForEncoding(
									_resourceBundleValue, ELEMENT_RESOURCE_BUNDLE);

								for (String pattern : resourceBundlesPatterns) {
									if (!CoreUtil.isNullOrEmpty(pattern)) {
										retval.addResourceBundlePattern(pattern);
									}
								}

								if (ListUtil.isNotEmpty(_supportedLocaleValues) && ListUtil.isNotEmpty(resourceBundlesPatterns)) {
									String resourceBundleValueBase = resourceBundlesPatterns[0];

									for (String supportedLocaleValue : _supportedLocaleValues) {
										retval.addSupportedLocalePattern(
											resourceBundleValueBase + "_" + supportedLocaleValue);
									}
								}

								String resourceBundle = _resourceBundleValue.replaceAll(
									"(^\\s*)|(\\s*$)", StringPool.BLANK);

								if (!resourceBundle.endsWith(PROPERTIES_FILE_SUFFIX) &&
									!resourceBundle.contains(IPath.SEPARATOR + "") &&
									 !(CoreUtil.isWindows() && resourceBundle.contains("\\"))) {

									resourceBundle = new Path(
										resourceBundle.replace(".", IPath.SEPARATOR + "")).toString();
								}

								retval.putResourceBundle(resourceBundle);
							}

							_resourceBundleValue = null;
							_supportedLocaleValues.clear();
						}
					}

					@Override
					public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

						if (qName.equals(ELEMENT_RESOURCE_BUNDLE)) {
							_resourceBundleElem = true;
						}

						if (qName.equals(ELEMENT_SUPPORTED_LOCALE)) {
							_supportedLocaleElem = true;
						}
					}

					private boolean _resourceBundleElem = false;
					private String _resourceBundleValue = null;
					private boolean _supportedLocaleElem = false;
					private List<String> _supportedLocaleValues = new ArrayList<>();

				};

				try(InputStream contents = portletXml.getContents()){
					SAXParser saxParser = _saxParserFactory.newSAXParser();

					XMLReader xmlReader = saxParser.getXMLReader();

					xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					xmlReader.setFeature("http://xml.org/sax/features/validation", false);

					saxParser.parse(contents, handler);
				}
			}
			catch (SAXException saxe) {
			}
			catch (Exception e) {
				LiferayCore.logError("Error resolving" + ILiferayConstants.PORTLET_XML_FILE, e);
			}

			_tmpResourceNodeInfo = retval;
		}

		return _tmpResourceNodeInfo;
	}

	private static boolean _isInPackage(List<String> packages, String className) {
		for (String element : packages) {
			if (!element.isEmpty() && className.startsWith(element) &&
				!className.startsWith("com.liferay.portal.kernel.portlet") && !className.startsWith("javax.portlet")) {

				return true;
			}
		}

		return false;
	}

	private static final SAXParserFactory _saxParserFactory = SAXParserFactory.newInstance();
	private static LanguageFileInfo _tmpLanguageFileInfo = null;
	private static ResourceNodeInfo _tmpResourceNodeInfo = null;

	private static class LanguageFileInfo {

		public LanguageFileInfo(IFile file) {
			_liferayHookXml = file;

			_modificationStamp = _liferayHookXml.getModificationStamp();
		}

		public void addLanguagePropertiesPattern(String languagePropertiesVal) {
			_vals.add(languagePropertiesVal);
		}

		public String[] getLanguagePropertyPatterns() {
			return _vals.toArray(new String[0]);
		}

		public IFile getLiferayHookXml() {
			return _liferayHookXml;
		}

		public long getModificationStamp() {
			return _modificationStamp;
		}

		private final IFile _liferayHookXml;
		private final long _modificationStamp;
		private final List<String> _vals = new ArrayList<>();

	}

	private static class PropertiesVisitor implements IResourceProxyVisitor {

		@Override
		public boolean visit(IResourceProxy resourceProxy) {
			if ((resourceProxy.getType() != IResource.FILE) ||
				!resourceProxy.getName().endsWith(PROPERTIES_FILE_SUFFIX)) {

				return true;
			}

			IResource resource = resourceProxy.requestResource();

			if (FileUtil.notExists(resource)) {
				return true;
			}

			IPath path = resource.getLocation().makeRelativeTo(_entryResource.getLocation());

			String relativePath = path.toString().replace(PROPERTIES_FILE_SUFFIX, "");

			try {
				if (relativePath.matches(_matchedRelativePath)) {
					_resources.add((IFile)resource);
				}
			}
			catch (Exception e) {

				// in case something is wrong when doing match regular expression

				return true;
			}

			return true;
		}

		public IFile[] visitPropertiesFiles(IResource container, String matchedRelativePath) {
			_entryResource = container;
			_matchedRelativePath = matchedRelativePath;

			try {
				container.accept(this, IContainer.EXCLUDE_DERIVED | IContainer.DO_NOT_CHECK_EXISTENCE);
			}
			catch (CoreException ce) {
				LiferayCore.logError(ce);
			}

			return _resources.toArray(new IFile[_resources.size()]);
		}

		private IResource _entryResource = null;
		private String _matchedRelativePath = null;
		private List<IFile> _resources = new ArrayList<>();

	}

	private static class ResourceNodeInfo {

		public ResourceNodeInfo(IFile file) {
			_portletXml = file;

			_modificationStamp = _portletXml.getModificationStamp();
		}

		public void addResourceBundlePattern(String resourceBundlePattern) {
			_resourceBundlesPatterns.add(resourceBundlePattern);
		}

		public void addSupportedLocalePattern(String supportedLocalePattern) {
			_supportedLocalePatterns.add(supportedLocalePattern);
		}

		public long getModificationStamp() {
			return _modificationStamp;
		}

		public IFile getPortletXml() {
			return _portletXml;
		}

		public String[] getResourceBundlePatterns() {
			return _resourceBundlesPatterns.toArray(new String[0]);
		}

		public Set<String> getResourceBundles() {
			return _resourceBundles;
		}

		public String[] getSupportedLocalePatterns() {
			return _supportedLocalePatterns.toArray(new String[0]);
		}

		public void putResourceBundle(String resourceBundle) {
			_resourceBundles.add(resourceBundle);
		}

		private final long _modificationStamp;
		private final IFile _portletXml;
		private final Set<String> _resourceBundles = new HashSet<>();
		private final List<String> _resourceBundlesPatterns = new ArrayList<>();
		private final List<String> _supportedLocalePatterns = new ArrayList<>();

	}

}