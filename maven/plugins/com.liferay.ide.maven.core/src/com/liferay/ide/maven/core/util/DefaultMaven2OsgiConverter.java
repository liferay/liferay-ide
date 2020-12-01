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

package com.liferay.ide.maven.core.util;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Analyzer;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;
import java.io.IOException;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.maven.artifact.Artifact;

/**
 * Default implementation of {@link Maven2OsgiConverter}
 *
 * @plexus.component
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 */
public class DefaultMaven2OsgiConverter {

	// Bundle-Version must match this pattern

	public String getBundleFileName(Artifact artifact) {
		return getBundleSymbolicName(artifact) + "_" + getVersion(artifact.getVersion()) + ".jar";
	}

	// pattern used to change - to .
	// private static final Pattern P_VERSION =
	// Pattern.compile("([0-9]+(\\.[0-9])*)-(.*)");

	/**
	 * Get the symbolic name as groupId + "." + artifactId, with the following
	 * exceptions
	 * <ul>
	 * <li>if artifact.getFile is not null and the jar contains a OSGi Manifest with
	 * Bundle-SymbolicName property then that value is returned</li>
	 * <li>if groupId has only one section (no dots) and artifact.getFile is not
	 * null then the first package name with classes is returned. eg.
	 * commons-logging:commons-logging -> org.apache.commons.logging</li>
	 * <li>if artifactId is equal to last section of groupId then groupId is
	 * returned. eg. org.apache.maven:maven -> org.apache.maven</li>
	 * <li>if artifactId starts with last section of groupId that portion is
	 * removed. eg. org.apache.maven:maven-core -> org.apache.maven.core</li>
	 * </ul>
	 */
	public String getBundleSymbolicName(Artifact artifact) {
		File artifactFile = artifact.getFile();

		if (FileUtil.exists(artifactFile)) {
			try (JarFile jar = new JarFile(artifactFile); Analyzer analyzer = new Analyzer()) {
				if (jar.getManifest() != null) {
					Manifest manifestFile = jar.getManifest();

					Attributes attributes = manifestFile.getMainAttributes();

					String symbolicNameAttribute = attributes.getValue(Analyzer.BUNDLE_SYMBOLICNAME);

					Map<String, Attrs> bundleSymbolicNameHeader = analyzer.parseHeader(symbolicNameAttribute);

					Set<String> set = bundleSymbolicNameHeader.keySet();

					Iterator<String> it = set.iterator();

					if (it.hasNext()) {
						return (String)it.next();
					}
				}
			}
			catch (IOException ioe) {
				throw new RuntimeException("Error reading manifest in jar " + artifactFile.getAbsolutePath(), ioe);
			}
		}

		String groupId = artifact.getGroupId();

		int i = groupId.lastIndexOf('.');

		if ((i < 0) && FileUtil.exists(artifactFile)) {
			String groupIdFromPackage = _getGroupIdFromPackage(artifactFile);

			if (groupIdFromPackage != null) {
				return groupIdFromPackage;
			}
		}

		String lastSection = groupId.substring(++i);

		String id = artifact.getArtifactId();

		if (id.equals(lastSection)) {
			return groupId;
		}

		if (id.startsWith(lastSection)) {
			String artifactId = id.substring(lastSection.length());

			if (Character.isLetterOrDigit(artifactId.charAt(0))) {
				return _getBundleSymbolicName(groupId, artifactId);
			}

			return _getBundleSymbolicName(groupId, artifactId.substring(1));
		}

		return _getBundleSymbolicName(artifact.getGroupId(), artifact.getArtifactId());
	}

	public String getVersion(Artifact artifact) {
		return getVersion(artifact.getVersion());
	}

	public String getVersion(String version) {

		// Matcher m = P_VERSION.matcher(version);
		// if (m.matches()) {
		// osgiVersion = m.group(1) + "." + m.group(3);
		// }

		// TODO need a regexp guru here

		// if it's already OSGi compliant don't touch it

		Matcher m = _osgiVersionPattern.matcher(version);

		if (m.matches()) {
			return version;
		}

		String osgiVersion = version;

		//check for dated snapshot versions with only major or major and minor
		m = _datedSnapshot.matcher(osgiVersion);

		if (m.matches()) {
			String major = m.group(1);
			String minor = (m.group(3) != null) ? m.group(3) : "0";
			String service = (m.group(5) != null) ? m.group(5) : "0";

			String group = m.group(6);

			String qualifier = group.replaceAll("-", "_");

			qualifier = qualifier.replaceAll("\\.", "_");

			osgiVersion = major + "." + minor + "." + service + "." + qualifier;
		}

		// else transform first - to . and others to _

		osgiVersion = osgiVersion.replaceFirst("-", "\\.");
		osgiVersion = osgiVersion.replaceAll("-", "_");

		m = _osgiVersionPattern.matcher(osgiVersion);

		if (m.matches()) {
			return osgiVersion;
		}

		// remove dots in the middle of the qualifier

		m = _dotsInQualifier.matcher(osgiVersion);

		if (m.matches()) {
			String s1 = m.group(1);
			String s2 = m.group(2);
			String s3 = m.group(3);
			String s4 = m.group(4);

			Matcher qualifierMatcher = _onlyNumbers.matcher(s3);

			/**
			 * if last portion before dot is only numbers then it's not in the middle of the
			 * qualifier
			 */
			if (!qualifierMatcher.matches()) {
				osgiVersion = s1 + s2 + "." + s3 + "_" + s4;
			}
		}

		/**
		 * convert 1.string -> 1.0.0.string 1.2.string -> 1.2.0.string 1 -> 1.0.0 1.1 ->
		 * 1.1.0
		 */
		m = _needToFillZeros.matcher(osgiVersion);

		if (m.matches()) {
			String major = m.group(1);
			String minor = m.group(3);
			String service = null;
			String qualifier = m.group(5);

			// if there's no qualifier just fill with 0s

			if (qualifier == null) {
				osgiVersion = _getVersion(major, minor, service, qualifier);
			}
			else {

				// if last portion is only numbers then it's not a qualifier

				Matcher qualifierMatcher = _onlyNumbers.matcher(qualifier);

				if (qualifierMatcher.matches()) {
					if (minor == null) {
						minor = qualifier;
					}
					else {
						service = qualifier;
					}

					osgiVersion = _getVersion(major, minor, service, null);
				}
				else {
					osgiVersion = _getVersion(major, minor, service, qualifier);
				}
			}
		}

		m = _osgiVersionPattern.matcher(osgiVersion);

		// if still its not OSGi version then add everything as qualifier

		if (!m.matches()) {
			String major = "0";
			String minor = "0";
			String service = "0";
			String qualifier = osgiVersion.replaceAll("\\.", "_");

			osgiVersion = major + "." + minor + "." + service + "." + qualifier;
		}

		return osgiVersion;
	}

	private String _getBundleSymbolicName(String groupId, String artifactId) {
		return groupId + "." + artifactId;
	}

	private String _getGroupIdFromPackage(File artifactFile) {
		try (JarFile jar = new JarFile(artifactFile, false)) {

			// get package names from jar

			Set<String> packageNames = new HashSet<>();
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();

				String name = entry.getName();

				if (name.endsWith(".class")) {
					File f = new File(entry.getName());

					String packageName = f.getParent();

					if (packageName != null) {
						packageNames.add(packageName);
					}
				}
			}

			// find the top package

			String[] groupIdSections = null;

			for (Iterator<String> it = packageNames.iterator(); it.hasNext();) {
				String packageName = (String)it.next();

				String[] packageNameSections = packageName.split("\\" + _FILE_SEPARATOR);

				if (groupIdSections == null) {

					// first candidate

					groupIdSections = packageNameSections;
				}
				else {
					//if (packageNameSections.length < groupIdSections.length)
					/**
					 * find the common portion of current package and previous selected groupId
					 */
					int i;

					for (i = 0; (i < packageNameSections.length) && (i < groupIdSections.length); i++) {
						if (!packageNameSections[i].equals(groupIdSections[i])) {
							break;
						}
					}

					groupIdSections = new String[i];

					System.arraycopy(packageNameSections, 0, groupIdSections, 0, i);
				}
			}

			if (ListUtil.isEmpty(groupIdSections)) {
				return null;
			}

			// only one section as id doesn't seem enough, so ignore it

			if (groupIdSections.length == 1) {
				return null;
			}

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < groupIdSections.length; i++) {
				sb.append(groupIdSections[i]);

				if (i < (groupIdSections.length - 1)) {
					sb.append('.');
				}
			}

			return sb.toString();
		}
		catch (IOException ioe) {

			// we took all the precautions to avoid this

			throw new RuntimeException(ioe);
		}
	}

	private String _getVersion(String major, String minor, String service, String qualifier) {
		StringBuffer sb = new StringBuffer();

		sb.append((major != null) ? major : "0");
		sb.append('.');
		sb.append((minor != null) ? minor : "0");
		sb.append('.');
		sb.append((service != null) ? service : "0");

		if (qualifier != null) {
			sb.append('.');
			sb.append(qualifier);
		}

		return sb.toString();
	}

	private static final String _FILE_SEPARATOR = System.getProperty("file.separator");

	private static final Pattern _datedSnapshot = Pattern.compile(
		"([0-9])(\\.([0-9]))?(\\.([0-9]))?\\-([0-9]{8}\\.[0-9]{6}\\-[0-9]*)");
	private static final Pattern _dotsInQualifier = Pattern.compile(
		"([0-9])(\\.[0-9])?\\.([0-9A-Za-z_-]+)\\.([0-9A-Za-z_-]+)");
	private static final Pattern _needToFillZeros = Pattern.compile("([0-9])(\\.([0-9]))?(\\.([0-9A-Za-z_-]+))?");

	/**
	 * pattern that matches strings that contain only numbers
	 */
	private static final Pattern _onlyNumbers = Pattern.compile("[0-9]+");

	private static final Pattern _osgiVersionPattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+(\\.[0-9A-Za-z_-]+)?");

}