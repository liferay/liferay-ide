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
	System.err.println("Usage: GenerateP2Sbom.groovy --repository-dir <path> --project-root <path> [--output <path>] [--project-version <version>]")
	System.exit(1)
}

String repositoryDir = argsMap["repositoryDir"]
String projectRoot = argsMap["projectRoot"]
String outputFile = argsMap["output"] ?: (projectRoot + "/p2-sbom.cdx.json")

String basedir = new File(repositoryDir).parentFile.parentFile.canonicalPath
String contentJarPath = repositoryDir + "/content.jar"
String embeddedJarMappingFile = basedir + "/embedded-jar-coordinates.json"

// Determine project version

String projectVersion = argsMap["projectVersion"]

if (!projectVersion) {
	File rootPom = new File(projectRoot, "pom.xml")

	if (rootPom.exists()) {
		XmlParser pomParser = new XmlParser()

		pomParser.setTrimWhitespace(false)

		Node pomRoot = pomParser.parseText(rootPom.text)

		projectVersion = pomRoot.version?.text() ?: "unknown"
	}
	else {
		projectVersion = "unknown"
	}
}

// --- Shared utility: read Maven coordinates from META-INF/maven/**/pom.properties inside a JAR ---

Closure<Map<String, String>> readPomProperties = { File jarFile ->
	try {
		Map<String, String> result = new ZipFile(jarFile).withCloseable { ZipFile zipFile ->
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

		return result
	}
	catch (Exception ignored) {
	}

	return null
}

// --- Phase A: Parse content.xml ---

println "Generating SBOM from p2 repository metadata..."

if (!new File(contentJarPath).exists()) {
	System.err.println("ERROR: content.jar not found at ${contentJarPath}")
	System.err.println("Build the repository first: ./mvnw clean package -DskipTests")
	System.exit(1)
}

String contentXmlText

new ZipFile(contentJarPath).withCloseable { ZipFile zipFile ->
	ZipEntry contentEntry = zipFile.getEntry("content.xml")

	contentXmlText = zipFile.getInputStream(contentEntry).text
}

XmlParser contentParser = new XmlParser()

contentParser.setTrimWhitespace(false)

Node contentRoot = contentParser.parseText(contentXmlText)

List<Map<String, Object>> components = []
Map<String, List<String>> dependencyMap = [:]

Closure<String> getProperty = { Node unit, String propertyName ->
	List propertyNodes = unit.properties?.property

	if (!propertyNodes) {
		return null
	}

	Node matchingProperty = propertyNodes.find { it.'@name' == propertyName }

	return matchingProperty?.'@value'
}

contentRoot.units.unit.each { Node unit ->
	String unitId = unit.'@id'
	String unitVersion = unit.'@version'

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

	String mavenGroupId = getProperty(unit, "maven-groupId")
	String mavenArtifactId = getProperty(unit, "maven-artifactId")
	String mavenVersion = getProperty(unit, "maven-version")

	// If content.xml lacks Maven coords, check the plugin JAR for pom.properties

	if (!mavenGroupId && !unitId.endsWith(".feature.group")) {
		File pluginJar = new File(repositoryDir, "plugins").listFiles()?.find { File file ->
			file.name.startsWith("${unitId}_") && file.name.endsWith(".jar")
		}

		if (pluginJar) {
			Map<String, String> pomProps = readPomProperties(pluginJar)

			if (pomProps) {
				mavenGroupId = pomProps.groupId
				mavenArtifactId = pomProps.artifactId
				mavenVersion = pomProps.version
			}
		}
	}

	String displayName = getProperty(unit, "org.eclipse.equinox.p2.name")
	String provider = getProperty(unit, "org.eclipse.equinox.p2.provider")
	String description = getProperty(unit, "org.eclipse.equinox.p2.description")
	boolean isGroup = getProperty(unit, "org.eclipse.equinox.p2.type.group") == "true"

	// Resolve localized property placeholders from df_LT.* properties

	if (displayName?.startsWith("%")) {
		String localizedKey = displayName.substring(1)
		String resolvedValue = getProperty(unit, "df_LT.${localizedKey}")

		if (resolvedValue) {
			displayName = resolvedValue
		}
	}

	if (provider?.startsWith("%")) {
		String localizedKey = provider.substring(1)
		String resolvedValue = getProperty(unit, "df_LT.${localizedKey}")

		if (resolvedValue) {
			provider = resolvedValue
		}
	}

	if (description?.startsWith("%")) {
		String localizedKey = description.substring(1)
		String resolvedValue = getProperty(unit, "df_LT.${localizedKey}")

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

	Map<String, Object> component = [
		type: "library",
		"bom-ref": purl,
		name: mavenArtifactId ?: (isGroup ? unitId.replace(".feature.group", "") : unitId),
		version: mavenVersion ?: unitVersion,
		purl: purl
	]

	if (displayName && !displayName.startsWith("%")) {
		component["description"] = displayName
	}

	if (description && !description.startsWith("%") && description != displayName) {
		component["description"] = description
	}

	if (provider && !provider.startsWith("%")) {
		component["publisher"] = provider
	}

	if (mavenGroupId) {
		component["group"] = mavenGroupId
	}

	// Extract license from IU

	List licenseNodes = unit.licenses?.license

	if (licenseNodes) {
		List<Map<String, Object>> licenseEntries = []

		licenseNodes.each { Node licenseNode ->
			String licenseUri = licenseNode.'@uri'
			String licenseText = licenseNode.text()?.trim()

			if (licenseUri && !licenseUri.startsWith("%")) {
				Map<String, Object> licenseEntry = [license: [url: licenseUri]]

				licenseEntries.add(licenseEntry)
			}
			else if (licenseText && !licenseText.startsWith("%")) {
				String firstLine = licenseText.readLines().find { it.trim() }

				if (firstLine) {
					Map<String, Object> licenseEntry = [license: [name: firstLine.take(128).trim()]]

					licenseEntries.add(licenseEntry)
				}
			}
		}

		if (licenseEntries) {
			component["licenses"] = licenseEntries
		}
	}

	components.add(component)

	// Extract dependency relationships

	List requiredNodes = unit.requires?.required

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

Map embeddedJarMapping = [:]
File mappingFile = new File(embeddedJarMappingFile)

if (mappingFile.exists()) {
	embeddedJarMapping = new JsonSlurper().parse(mappingFile)
}

Pattern jarVersionPattern = Pattern.compile("(.+?)[-_](\\d+\\.\\d+[\\d.]*(?:-[A-Za-z0-9]+)?)\\.jar")

List<String> pluginDirs = ["tools/plugins", "enterprise/plugins", "maven/plugins", "portal/plugins"]
int embeddedCount = 0
int pomPropsCount = 0

pluginDirs.each { String pluginDirPath ->
	File pluginDir = new File(projectRoot, pluginDirPath)

	if (!pluginDir.exists()) {
		return
	}

	pluginDir.eachDir { File pluginRoot ->
		File manifest = new File(pluginRoot, "META-INF/MANIFEST.MF")

		if (!manifest.exists()) {
			return
		}

		String manifestText = manifest.text.replaceAll("\\r?\\n ", "")
		java.util.regex.Matcher bundleClassPathMatch = manifestText =~ /(?m)^Bundle-ClassPath:\s*(.+)$/

		if (!bundleClassPathMatch) {
			return
		}

		String bundleSymbolicName = null
		java.util.regex.Matcher symbolicNameMatch = manifestText =~ /(?m)^Bundle-SymbolicName:\s*([^;,\s]+)/

		if (symbolicNameMatch) {
			bundleSymbolicName = symbolicNameMatch[0][1]
		}

		List<String> classPathEntries = bundleClassPathMatch[0][1].split(",").collect { it.trim().replaceAll(",\$", "") }

		classPathEntries.each { String classPathEntry ->
			if (classPathEntry == "." || !classPathEntry.endsWith(".jar")) {
				return
			}

			String jarFileName = classPathEntry.contains("/") ? classPathEntry.substring(classPathEntry.lastIndexOf("/") + 1) : classPathEntry
			File jarFile = new File(pluginRoot, classPathEntry)

			String groupId = null
			String artifactId = null
			String version = null

			// Priority 1: Read pom.properties from inside the JAR

			if (jarFile.exists()) {
				Map<String, String> pomProps = readPomProperties(jarFile)

				if (pomProps) {
					groupId = pomProps.groupId
					artifactId = pomProps.artifactId
					version = pomProps.version
					pomPropsCount++
				}
			}

			// Priority 2: Check the mapping file

			if (!groupId) {
				Map mapping = embeddedJarMapping[jarFileName]

				if (mapping) {
					groupId = mapping.groupId
					artifactId = mapping.artifactId
					version = mapping.version
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

			// Check if already captured from p2 metadata

			if (components.any { it.purl == purl }) {
				return
			}

			Map<String, Object> component = [
				type: "library",
				"bom-ref": purl,
				name: artifactId ?: jarFileName.replace(".jar", ""),
				version: version ?: "unknown",
				purl: purl,
				scope: "required",
				description: "Embedded JAR in ${bundleSymbolicName ?: pluginRoot.name}"
			]

			if (groupId) {
				component["group"] = groupId
			}

			components.add(component)
			embeddedCount++
		}
	}
}

println "Found ${embeddedCount} embedded JARs not already in p2 metadata (${pomPropsCount} identified via pom.properties)"

// --- Phase C: Generate CycloneDX 1.5 JSON ---

println "Generating CycloneDX 1.5 SBOM..."

// Build dependency entries, resolving IU names to purls

Map<String, String> iuNameToPurl = [:]

components.each { Map<String, Object> component ->
	iuNameToPurl[component.name] = component.purl
}

List<Map<String, Object>> dependencies = []

dependencyMap.each { String parentPurl, List<String> dependencyNames ->
	List<String> resolvedDependencies = dependencyNames.collect { String dependencyName ->
		iuNameToPurl[dependencyName]
	}.findAll { it != null }.unique()

	if (resolvedDependencies) {
		dependencies.add([
			ref: parentPurl,
			dependsOn: resolvedDependencies
		])
	}
}

Map<String, Object> bom = [
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
	components: components.sort { Map left, Map right -> left.purl <=> right.purl },
	dependencies: dependencies.sort { Map left, Map right -> left.ref <=> right.ref }
]

String jsonOutput = new JsonBuilder(bom).toPrettyString()
File outputFileHandle = new File(outputFile)

outputFileHandle.text = jsonOutput

println ""
println "SBOM generated: ${outputFileHandle.canonicalPath}"
println ""
println "  Components:          ${components.size()}"
println "  With Maven coords:   ${components.count { it.purl.startsWith('pkg:maven/') }}"
println "  With p2 coords:      ${components.count { it.purl.startsWith('pkg:p2/') }}"
println "  Embedded JARs:       ${embeddedCount}"
println "  Dependency entries:  ${dependencies.size()}"
