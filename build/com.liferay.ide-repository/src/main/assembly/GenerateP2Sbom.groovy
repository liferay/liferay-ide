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

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.xml.XmlParser

import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

// --- Configuration (simple argument parsing) ---

Map<String, String> argsMap = [:]

for (int i = 0; i < args.length; i++) {
	if (args[i] == "--repository-dir" && i + 1 < args.length) {
		argsMap["repositoryDir"] = args[++i]
	}
	else if (args[i] == "--project-root" && i + 1 < args.length) {
		argsMap["projectRoot"] = args[++i]
	}
	else if (args[i] == "--output" && i + 1 < args.length) {
		argsMap["output"] = args[++i]
	}
	else if (args[i] == "--project-version" && i + 1 < args.length) {
		argsMap["projectVersion"] = args[++i]
	}
}

if (!argsMap["repositoryDir"] || !argsMap["projectRoot"]) {
	println "Usage: GenerateP2Sbom.groovy --repository-dir <path> --project-root <path> [--output <path>] [--project-version <version>]"
	System.exit(1)
}

String repositoryDir = argsMap["repositoryDir"]
String projectRoot = argsMap["projectRoot"]
String outputFilePath = argsMap["output"] ?: (projectRoot + "/p2-sbom.cdx.json")

String contentJarPath = repositoryDir + "/content.jar"
String embeddedJarMappingFilePath = projectRoot + "/build/com.liferay.ide-repository/embedded-jar-coordinates.json"

// Determine project version

String projectVersion = argsMap["projectVersion"]

if (!projectVersion) {
	File rootPomFile = new File(projectRoot, "pom.xml")

	if (rootPomFile.exists()) {
		XmlParser pomParser = new XmlParser()

		pomParser.setTrimWhitespace(false)

		Node pomRootNode = pomParser.parseText(rootPomFile.text)

		projectVersion = pomRootNode.version?.text() ?: "unknown"
	}
	else {
		projectVersion = "unknown"
	}
}

// --- Shared utility: read Maven coordinates from META-INF/maven/**/pom.properties inside a JAR ---

Closure<Map<String, String>> readPomProperties = { File jarFile ->
	try {
		Map<String, String> resultMap = new ZipFile(jarFile).withCloseable { ZipFile zipFile ->
			ZipEntry pomPropsEntry = zipFile.entries().toList().find { ZipEntry zipEntry ->
				zipEntry.name.startsWith("META-INF/maven/") && zipEntry.name.endsWith("/pom.properties")
			}

			if (pomPropsEntry) {
				Properties properties = new Properties()

				properties.load(zipFile.getInputStream(pomPropsEntry))

				String groupId = properties.getProperty("groupId")
				String artifactId = properties.getProperty("artifactId")
				String version = properties.getProperty("version")

				if (groupId && artifactId && version) {
					return [groupId: groupId, artifactId: artifactId, version: version]
				}
			}

			return null
		}

		return resultMap
	}
	catch (Exception exception) {
		println "WARNING: Could not read pom.properties from ${jarFile.name}: ${exception.message}"
	}

	return null
}

// --- Phase A: Parse content.xml ---

println "Generating SBOM from p2 repository metadata..."

if (!new File(projectRoot).isDirectory()) {
	println "ERROR: project root not found at ${projectRoot}"
	System.exit(1)
}

if (!new File(contentJarPath).exists()) {
	println "ERROR: content.jar not found at ${contentJarPath}"
	println "Build the repository first: ./mvnw clean package -DskipTests"
	System.exit(1)
}

String contentXmlText

new ZipFile(contentJarPath).withCloseable { ZipFile zipFile ->
	ZipEntry contentEntry = zipFile.getEntry("content.xml")

	contentXmlText = zipFile.getInputStream(contentEntry).text
}

XmlParser contentParser = new XmlParser()

contentParser.setTrimWhitespace(false)

Node contentRootNode = contentParser.parseText(contentXmlText)

List<Map<String, Object>> components = []
Map<String, List<String>> dependencyMap = [:]
Set<String> purlSet = new HashSet<>()
Map<String, String> unitIdToPurlMap = [:]

Closure<String> getProperty = { Node unitNode, String propertyName ->
	List propertyNodes = unitNode.properties?.property

	if (!propertyNodes) {
		return null
	}

	Node matchingPropertyNode = propertyNodes.find { it.'@name' == propertyName }

	return matchingPropertyNode?.'@value'
}

contentRootNode.units.unit.each { Node unitNode ->
	String unitId = unitNode.'@id'
	String unitVersion = unitNode.'@version'

	// Skip .feature.jar IUs (duplicates of .feature.group)

	if (unitId.endsWith(".feature.jar")) {
		return
	}

	// Skip category/config/tooling IUs

	if (unitId.startsWith("tooling")) {
		return
	}

	if (unitId.startsWith("configure.") || unitId.startsWith("a.jre.")) {
		return
	}

	if (unitId.matches("^\\d+\\..*")) {
		return
	}

	String mavenGroupId = getProperty(unitNode, "maven-groupId")
	String mavenArtifactId = getProperty(unitNode, "maven-artifactId")
	String mavenVersion = getProperty(unitNode, "maven-version")

	// If content.xml lacks Maven coords, check the plugin JAR for pom.properties

	if (!mavenGroupId && !unitId.endsWith(".feature.group")) {
		File pluginJarFile = new File(repositoryDir, "plugins").listFiles()?.find { File file ->
			file.name.startsWith("${unitId}_") && file.name.endsWith(".jar")
		}

		if (pluginJarFile) {
			Map<String, String> pomPropertiesMap = readPomProperties(pluginJarFile)

			if (pomPropertiesMap) {
				mavenGroupId = pomPropertiesMap.groupId
				mavenArtifactId = pomPropertiesMap.artifactId
				mavenVersion = pomPropertiesMap.version
			}
		}
	}

	// Skip Eclipse platform bundles (not on Maven Central, covered by Eclipse's own security process)

	if (mavenGroupId?.startsWith("org.eclipse.") || (!mavenGroupId && unitId.startsWith("org.eclipse."))) {
		return
	}

	// Skip first-party Liferay IDE modules (the product itself, not third-party dependencies)

	if (mavenGroupId?.startsWith("com.liferay.ide.") || (!mavenGroupId && unitId.startsWith("com.liferay.ide."))) {
		return
	}

	String displayName = getProperty(unitNode, "org.eclipse.equinox.p2.name")
	String provider = getProperty(unitNode, "org.eclipse.equinox.p2.provider")
	String description = getProperty(unitNode, "org.eclipse.equinox.p2.description")
	boolean isGroup = getProperty(unitNode, "org.eclipse.equinox.p2.type.group") == "true"

	// Resolve localized property placeholders from df_LT.* properties

	if (displayName?.startsWith("%")) {
		String localizedKey = displayName.substring(1)
		String resolvedValue = getProperty(unitNode, "df_LT.${localizedKey}")

		if (resolvedValue) {
			displayName = resolvedValue
		}
	}

	if (provider?.startsWith("%")) {
		String localizedKey = provider.substring(1)
		String resolvedValue = getProperty(unitNode, "df_LT.${localizedKey}")

		if (resolvedValue) {
			provider = resolvedValue
		}
	}

	if (description?.startsWith("%")) {
		String localizedKey = description.substring(1)
		String resolvedValue = getProperty(unitNode, "df_LT.${localizedKey}")

		if (resolvedValue) {
			description = resolvedValue
		}
	}

	// Build purl

	String purl

	if (mavenGroupId && mavenArtifactId && mavenVersion) {
		purl = "pkg:maven/${mavenGroupId}/${mavenArtifactId}@${mavenVersion}"
	}
	else {
		String p2Id = unitId

		if (p2Id.endsWith(".feature.group")) {
			p2Id = p2Id.replace(".feature.group", "")
		}

		purl = "pkg:p2/${p2Id}@${unitVersion}"
	}

	Map<String, Object> componentMap = [
		type: "library",
		"bom-ref": purl,
		name: mavenArtifactId ?: (isGroup ? unitId.replace(".feature.group", "") : unitId),
		version: mavenVersion ?: unitVersion,
		purl: purl
	]

	if (description && !description.startsWith("%")) {
		componentMap["description"] = description
	}
	else if (displayName && !displayName.startsWith("%")) {
		componentMap["description"] = displayName
	}

	if (provider && !provider.startsWith("%")) {
		componentMap["publisher"] = provider
	}

	if (mavenGroupId) {
		componentMap["group"] = mavenGroupId
	}

	// Extract license from IU

	List licenseNodes = unitNode.licenses?.license

	if (licenseNodes) {
		List<Map<String, Object>> licenseEntries = []

		licenseNodes.each { Node licenseNode ->
			String licenseUri = licenseNode.'@uri'
			String licenseText = licenseNode.text()?.trim()

			if (licenseUri && !licenseUri.startsWith("%")) {
				Map<String, Object> licenseEntryMap = [license: [url: licenseUri]]

				licenseEntries.add(licenseEntryMap)
			}
			else if (licenseText && !licenseText.startsWith("%")) {
				String firstLine = licenseText.readLines().find { it.trim() }

				if (firstLine) {
					Map<String, Object> licenseEntryMap = [license: [name: firstLine.take(128).trim()]]

					licenseEntries.add(licenseEntryMap)
				}
			}
		}

		if (licenseEntries) {
			componentMap["licenses"] = licenseEntries
		}
	}

	components.add(componentMap)
	purlSet.add(purl)
	unitIdToPurlMap[unitId] = purl

	// Extract dependency relationships

	List requiredNodes = unitNode.requires?.required

	if (requiredNodes) {
		List<String> dependencyNames = []

		requiredNodes.each { Node requiredNode ->
			String requiredName = requiredNode.'@name'
			String requiredNamespace = requiredNode.'@namespace'

			if (requiredNamespace == "org.eclipse.equinox.p2.iu" || requiredNamespace == "osgi.bundle") {
				dependencyNames.add(requiredName)
			}
		}

		if (dependencyNames) {
			dependencyMap[purl] = dependencyNames
		}
	}
}

// --- Phase B: Scan for embedded JARs ---

println "Scanning for embedded JARs in Bundle-ClassPath..."

Map embeddedJarCoordinatesMap = [:]
File mappingFile = new File(embeddedJarMappingFilePath)

if (mappingFile.exists()) {
	embeddedJarCoordinatesMap = new JsonSlurper().parse(mappingFile)
}

Pattern jarVersionPattern = Pattern.compile("(.+?)[-_](\\d+\\.\\d+[\\d.]*(?:-[A-Za-z0-9]+)?)\\.jar")

List<String> pluginDirPaths = ["tools/plugins", "enterprise/plugins", "maven/plugins", "portal/plugins"]
int embeddedCount = 0
int pomPropsCount = 0

pluginDirPaths.each { String pluginDirPath ->
	File pluginDirFile = new File(projectRoot, pluginDirPath)

	if (!pluginDirFile.exists()) {
		return
	}

	pluginDirFile.eachDir { File pluginRootDir ->
		File manifestFile = new File(pluginRootDir, "META-INF/MANIFEST.MF")

		if (!manifestFile.exists()) {
			return
		}

		String manifestText = manifestFile.text.replaceAll("\\r?\\n ", "")
		Matcher bundleClassPathMatcher = manifestText =~ /(?m)^Bundle-ClassPath:\s*(.+)$/

		if (!bundleClassPathMatcher) {
			return
		}

		String bundleSymbolicName = null
		Matcher symbolicNameMatcher = manifestText =~ /(?m)^Bundle-SymbolicName:\s*([^;,\s]+)/

		if (symbolicNameMatcher) {
			bundleSymbolicName = symbolicNameMatcher[0][1]
		}

		List<String> classPathEntries = bundleClassPathMatcher[0][1].split(",").collect { it.trim().replaceAll(",\$", "") }

		classPathEntries.each { String classPathEntry ->
			if (classPathEntry == "." || !classPathEntry.endsWith(".jar")) {
				return
			}

			String jarFileName = classPathEntry.contains("/") ? classPathEntry.substring(classPathEntry.lastIndexOf("/") + 1) : classPathEntry
			File jarFile = new File(pluginRootDir, classPathEntry)

			String groupId = null
			String artifactId = null
			String version = null

			// Priority 1: Read pom.properties from inside the JAR

			if (jarFile.exists()) {
				Map<String, String> pomPropertiesMap = readPomProperties(jarFile)

				if (pomPropertiesMap) {
					groupId = pomPropertiesMap.groupId
					artifactId = pomPropertiesMap.artifactId
					version = pomPropertiesMap.version
					pomPropsCount++
				}
			}

			// Priority 2: Check the mapping file

			if (!groupId) {
				Map coordinatesMap = embeddedJarCoordinatesMap[jarFileName]

				if (coordinatesMap) {
					groupId = coordinatesMap.groupId
					artifactId = coordinatesMap.artifactId
					version = coordinatesMap.version
				}
			}

			// Priority 3: Parse version from filename

			if (!version) {
				Matcher versionMatcher = jarVersionPattern.matcher(jarFileName)

				if (versionMatcher.matches()) {
					if (!artifactId) {
						artifactId = versionMatcher.group(1)
					}

					version = versionMatcher.group(2)
				}
			}

			// Build purl

			String purl

			if (groupId && artifactId && version) {
				purl = "pkg:maven/${groupId}/${artifactId}@${version}"
			}
			else {
				String p2ComponentName = artifactId ?: jarFileName.replace(".jar", "")
				String p2ComponentVersion = version ?: "unknown"

				purl = "pkg:p2/${p2ComponentName}@${p2ComponentVersion}"
			}

			// Skip first-party Liferay IDE embedded JARs

			if (groupId?.startsWith("com.liferay.ide.")) {
				return
			}

			// Check if already captured from p2 metadata

			if (purlSet.contains(purl)) {
				return
			}

			Map<String, Object> componentMap = [
				type: "library",
				"bom-ref": purl,
				name: artifactId ?: jarFileName.replace(".jar", ""),
				version: version ?: "unknown",
				purl: purl,
				scope: "required",
				description: "Embedded JAR in ${bundleSymbolicName ?: pluginRootDir.name}"
			]

			if (groupId) {
				componentMap["group"] = groupId
			}

			components.add(componentMap)
			purlSet.add(purl)
			embeddedCount++
		}
	}
}

println "Found ${embeddedCount} embedded JARs not already in p2 metadata (${pomPropsCount} identified via pom.properties)"

// --- Phase C: Generate CycloneDX 1.5 JSON ---

println "Generating CycloneDX 1.5 SBOM..."

// Build dependency entries, resolving IU names to purls

List<Map<String, Object>> dependencies = []

dependencyMap.each { String parentPurl, List<String> dependencyNames ->
	List<String> resolvedDependencies = dependencyNames.collect { String dependencyName ->
		unitIdToPurlMap[dependencyName]
	}.findAll { it != null }.unique()

	if (resolvedDependencies) {
		dependencies.add([
			ref: parentPurl,
			dependsOn: resolvedDependencies
		])
	}
}

Map<String, Object> bomMap = [
	bomFormat: "CycloneDX",
	specVersion: "1.5",
	serialNumber: "urn:uuid:" + UUID.randomUUID().toString(),
	version: 1,
	metadata: [
		timestamp: Instant.now().toString(),
		tools: [
			components: [
				[
					type: "application",
					name: "generate-p2-sbom",
					version: "1.0.0",
					publisher: "Liferay"
				]
			]
		],
		component: [
			type: "application",
			"bom-ref": "pkg:maven/com.liferay.ide/liferay-ide@${projectVersion}",
			name: "Liferay IDE",
			version: projectVersion,
			purl: "pkg:maven/com.liferay.ide/liferay-ide@${projectVersion}",
			publisher: "Liferay, Inc.",
			description: "Liferay IDE is the official set of Eclipse plugins for supporting Liferay Portal development."
		]
	],
	components: components.sort { Map componentMap1, Map componentMap2 -> componentMap1.purl <=> componentMap2.purl },
	dependencies: dependencies.sort { Map dependencyMap1, Map dependencyMap2 -> dependencyMap1.ref <=> dependencyMap2.ref }
]

String jsonOutput = new JsonBuilder(bomMap).toPrettyString()
File outputFile = new File(outputFilePath)

outputFile.text = jsonOutput

println ""
println "SBOM generated: ${outputFile.canonicalPath}"
println ""
println "  Components:          ${components.size()}"
println "  With Maven coords:   ${components.count { it.purl.startsWith('pkg:maven/') }}"
println "  With p2 coords:      ${components.count { it.purl.startsWith('pkg:p2/') }}"
println "  Embedded JARs:       ${embeddedCount}"
println "  Dependency entries:  ${dependencies.size()}"
