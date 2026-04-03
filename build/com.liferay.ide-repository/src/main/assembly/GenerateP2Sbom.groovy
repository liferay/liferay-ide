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
import java.util.zip.ZipFile

// --- Configuration (simple argument parsing) ---

def argsMap = [:]

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

def repositoryDir = argsMap["repositoryDir"]
def projectRoot = argsMap["projectRoot"]
def outputFile = argsMap["output"] ?: (projectRoot + "/p2-sbom.cdx.json")

def basedir = new File(repositoryDir).parentFile.parentFile.canonicalPath
def contentJar = repositoryDir + "/content.jar"
def embeddedJarMappingFile = basedir + "/embedded-jar-coordinates.json"

// Determine project version

def projectVersion = argsMap["projectVersion"]

if (!projectVersion) {
	def rootPom = new File(projectRoot, "pom.xml")

	if (rootPom.exists()) {
		def pomParser = new XmlParser()

		pomParser.setTrimWhitespace(false)

		def pomRoot = pomParser.parseText(rootPom.text)

		projectVersion = pomRoot.version?.text() ?: "unknown"
	}
	else {
		projectVersion = "unknown"
	}
}

// --- Shared utility: read Maven coordinates from META-INF/maven/**/pom.properties inside a JAR ---

def readPomProperties = { File jarFile ->
	try {
		def result = new ZipFile(jarFile).withCloseable { zip ->
			def pomPropsEntry = zip.entries().toList().find { entry ->
				entry.name.startsWith("META-INF/maven/") && entry.name.endsWith("/pom.properties")
			}

			if (pomPropsEntry) {
				def props = new Properties()

				props.load(zip.getInputStream(pomPropsEntry))

				def g = props.getProperty("groupId")
				def a = props.getProperty("artifactId")
				def v = props.getProperty("version")

				if (g && a && v) {
					return [groupId: g, artifactId: a, version: v]
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

if (!new File(contentJar).exists()) {
	System.err.println("ERROR: content.jar not found at ${contentJar}")
	System.err.println("Build the repository first: ./mvnw clean package -DskipTests")
	System.exit(1)
}

def contentXmlText

new ZipFile(contentJar).withCloseable { zip ->
	def entry = zip.getEntry("content.xml")

	contentXmlText = zip.getInputStream(entry).text
}

def parser = new XmlParser()

parser.setTrimWhitespace(false)

def root = parser.parseText(contentXmlText)

def components = []
def dependencyMap = [:]

def getProperty = { unit, name ->
	def props = unit.properties?.property

	if (!props) {
		return null
	}

	def prop = props.find { it.'@name' == name }

	return prop?.'@value'
}

root.units.unit.each { unit ->
	def id = unit.'@id'
	def version = unit.'@version'

	// Skip .feature.jar IUs (duplicates of .feature.group)

	if (id.endsWith(".feature.jar")) {
		return
	}

	// Skip category/config/tooling IUs

	if (id.startsWith("tooling")) {
		return
	}

	if (id.startsWith("configure.") || id.startsWith("a.jre.")) {
		return
	}

	if (id.matches("^\\d+\\..*")) {
		return
	}

	def mavenGroupId = getProperty(unit, "maven-groupId")
	def mavenArtifactId = getProperty(unit, "maven-artifactId")
	def mavenVersion = getProperty(unit, "maven-version")

	// If content.xml lacks Maven coords, check the plugin JAR for pom.properties

	if (!mavenGroupId && !id.endsWith(".feature.group")) {
		def pluginJar = new File(repositoryDir, "plugins").listFiles()?.find { f ->
			f.name.startsWith("${id}_") && f.name.endsWith(".jar")
		}

		if (pluginJar) {
			def pomProps = readPomProperties(pluginJar)

			if (pomProps) {
				mavenGroupId = pomProps.groupId
				mavenArtifactId = pomProps.artifactId
				mavenVersion = pomProps.version
			}
		}
	}

	def p2Name = getProperty(unit, "org.eclipse.equinox.p2.name")
	def p2Provider = getProperty(unit, "org.eclipse.equinox.p2.provider")
	def p2Description = getProperty(unit, "org.eclipse.equinox.p2.description")
	def isGroup = getProperty(unit, "org.eclipse.equinox.p2.type.group") == "true"

	// Resolve localized property placeholders from df_LT.* properties

	if (p2Name?.startsWith("%")) {
		def key = p2Name.substring(1)
		def resolved = getProperty(unit, "df_LT.${key}")

		if (resolved) {
			p2Name = resolved
		}
	}

	if (p2Provider?.startsWith("%")) {
		def key = p2Provider.substring(1)
		def resolved = getProperty(unit, "df_LT.${key}")

		if (resolved) {
			p2Provider = resolved
		}
	}

	if (p2Description?.startsWith("%")) {
		def key = p2Description.substring(1)
		def resolved = getProperty(unit, "df_LT.${key}")

		if (resolved) {
			p2Description = resolved
		}
	}

	// Build purl

	def purl

	if (mavenGroupId && mavenArtifactId && mavenVersion) {
		purl = "pkg:maven/${mavenGroupId}/${mavenArtifactId}@${mavenVersion}"
	}
	else {
		def p2Id = id

		if (p2Id.endsWith(".feature.group")) {
			p2Id = p2Id.replace(".feature.group", "")
		}

		purl = "pkg:p2/${p2Id}@${version}"
	}

	def component = [
		type: "library",
		"bom-ref": purl,
		name: mavenArtifactId ?: (isGroup ? id.replace(".feature.group", "") : id),
		version: mavenVersion ?: version,
		purl: purl
	]

	if (p2Name && !p2Name.startsWith("%")) {
		component["description"] = p2Name
	}

	if (p2Description && !p2Description.startsWith("%") && p2Description != p2Name) {
		component["description"] = p2Description
	}

	if (p2Provider && !p2Provider.startsWith("%")) {
		component["publisher"] = p2Provider
	}

	if (mavenGroupId) {
		component["group"] = mavenGroupId
	}

	// Extract license from IU

	def licenses = unit.licenses?.license

	if (licenses) {
		def licenseEntries = []

		licenses.each { lic ->
			def licUri = lic.'@uri'
			def licText = lic.text()?.trim()

			if (licUri && !licUri.startsWith("%")) {
				def licenseEntry = [license: [url: licUri]]

				licenseEntries.add(licenseEntry)
			}
			else if (licText && !licText.startsWith("%")) {
				def firstLine = licText.readLines().find { it.trim() }

				if (firstLine) {
					def licenseEntry = [license: [name: firstLine.take(128).trim()]]

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

	def requires = unit.requires?.required

	if (requires) {
		def deps = []

		requires.each { req ->
			def reqName = req.'@name'
			def reqNs = req.'@namespace'

			if (reqNs == "org.eclipse.equinox.p2.iu" || reqNs == "osgi.bundle") {
				deps.add(reqName)
			}
		}

		if (deps) {
			dependencyMap[purl] = deps
		}
	}
}

// --- Phase B: Scan for embedded JARs ---

println "Scanning for embedded JARs in Bundle-ClassPath..."

def embeddedJarMapping = [:]
def mappingFile = new File(embeddedJarMappingFile)

if (mappingFile.exists()) {
	embeddedJarMapping = new JsonSlurper().parse(mappingFile)
}

def jarVersionPattern = Pattern.compile("(.+?)[-_](\\d+\\.\\d+[\\d.]*(?:-[A-Za-z0-9]+)?)\\.jar")

def pluginDirs = ["tools/plugins", "enterprise/plugins", "maven/plugins", "portal/plugins"]
def embeddedCount = 0
def pomPropsCount = 0

pluginDirs.each { pluginDir ->
	def dir = new File(projectRoot, pluginDir)

	if (!dir.exists()) {
		return
	}

	dir.eachDir { pluginRoot ->
		def manifest = new File(pluginRoot, "META-INF/MANIFEST.MF")

		if (!manifest.exists()) {
			return
		}

		def manifestText = manifest.text.replaceAll("\\r?\\n ", "")
		def bcpMatch = manifestText =~ /(?m)^Bundle-ClassPath:\s*(.+)$/

		if (!bcpMatch) {
			return
		}

		def bundleId = null
		def bsnMatch = manifestText =~ /(?m)^Bundle-SymbolicName:\s*([^;,\s]+)/

		if (bsnMatch) {
			bundleId = bsnMatch[0][1]
		}

		def entries = bcpMatch[0][1].split(",").collect { it.trim().replaceAll(",\$", "") }

		entries.each { entry ->
			if (entry == "." || !entry.endsWith(".jar")) {
				return
			}

			def jarName = entry.contains("/") ? entry.substring(entry.lastIndexOf("/") + 1) : entry
			def jarFile = new File(pluginRoot, entry)

			def g = null
			def a = null
			def v = null

			// Priority 1: Read pom.properties from inside the JAR

			if (jarFile.exists()) {
				def pomProps = readPomProperties(jarFile)

				if (pomProps) {
					g = pomProps.groupId
					a = pomProps.artifactId
					v = pomProps.version
					pomPropsCount++
				}
			}

			// Priority 2: Check the mapping file

			if (!g) {
				def mapping = embeddedJarMapping[jarName]

				if (mapping) {
					g = mapping.groupId
					a = mapping.artifactId
					v = mapping.version
				}
			}

			// Priority 3: Parse version from filename

			if (!v) {
				Matcher vm = jarVersionPattern.matcher(jarName)

				if (vm.matches()) {
					if (!a) {
						a = vm.group(1)
					}

					v = vm.group(2)
				}
			}

			// Build purl

			def purl

			if (g && a && v) {
				purl = "pkg:maven/${g}/${a}@${v}"
			}
			else if (a && v) {
				purl = "pkg:maven/unknown/${a}@${v}"
			}
			else {
				def nameWithoutJar = jarName.replace(".jar", "")

				purl = "pkg:maven/unknown/${nameWithoutJar}@unknown"
			}

			// Check if already captured from p2 metadata

			if (components.any { it.purl == purl }) {
				return
			}

			def comp = [
				type: "library",
				"bom-ref": purl,
				name: a ?: jarName.replace(".jar", ""),
				version: v ?: "unknown",
				purl: purl,
				scope: "required",
				description: "Embedded JAR in ${bundleId ?: pluginRoot.name}"
			]

			if (g && g != "unknown") {
				comp["group"] = g
			}

			components.add(comp)
			embeddedCount++
		}
	}
}

println "Found ${embeddedCount} embedded JARs not already in p2 metadata (${pomPropsCount} identified via pom.properties)"

// --- Phase C: Generate CycloneDX 1.5 JSON ---

println "Generating CycloneDX 1.5 SBOM..."

// Build dependency entries, resolving IU names to purls

def iuToPurl = [:]

components.each { comp ->
	iuToPurl[comp.name] = comp.purl
}

def dependencies = []

dependencyMap.each { parentPurl, depNames ->
	def resolvedDeps = depNames.collect { depName ->
		iuToPurl[depName]
	}.findAll { it != null }.unique()

	if (resolvedDeps) {
		dependencies.add([
			ref: parentPurl,
			dependsOn: resolvedDeps
		])
	}
}

def bom = [
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
	components: components.sort { a, b -> a.purl <=> b.purl },
	dependencies: dependencies.sort { a, b -> a.ref <=> b.ref }
]

def json = new JsonBuilder(bom).toPrettyString()
def output = new File(outputFile)

output.text = json

println ""
println "SBOM generated: ${output.canonicalPath}"
println ""
println "  Components:          ${components.size()}"
println "  With Maven coords:   ${components.count { it.purl.startsWith('pkg:maven/') && !it.purl.contains('unknown/') }}"
println "  With p2 coords:      ${components.count { it.purl.startsWith('pkg:p2/') }}"
println "  Embedded JARs:       ${embeddedCount}"
println "  Dependency entries:  ${dependencies.size()}"
