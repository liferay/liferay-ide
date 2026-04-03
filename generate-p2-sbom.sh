#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
	echo "Usage: $0 [--output <path>]"
	echo ""
	echo "Generates a CycloneDX SBOM from the built p2 repository metadata."
	echo "The p2 repository must already exist (run ./mvnw clean package -DskipTests first)."
	echo ""
	echo "Options:"
	echo "  --output, -o <path>   Output file path (default: p2-sbom.cdx.json)"
	echo ""
	exit 0
}

output_file=""

while [ $# -gt 0 ]; do
	case "$1" in
		--output|-o)
			if [ -z "$2" ] || [[ "$2" == -* ]]; then
				echo "ERROR: --output requires a file path argument"
				exit 1
			fi

			output_file="$2"
			shift 2
			;;
		--help|-h)
			usage
			;;
		*)
			echo "Unknown option: $1"
			usage
			;;
	esac
done

REPOSITORY_DIR="${SCRIPT_DIR}/build/com.liferay.ide-repository/target/repository"
CONTENT_JAR="${REPOSITORY_DIR}/content.jar"

if [ ! -f "$CONTENT_JAR" ]; then
	echo "ERROR: p2 repository not found at ${REPOSITORY_DIR}/"
	echo ""
	echo "Build the repository first:"
	echo "  ./mvnw clean package -DskipTests"
	exit 1
fi

GROOVY_SCRIPT="${SCRIPT_DIR}/build/com.liferay.ide-repository/src/main/assembly/GenerateP2Sbom.groovy"
M2_REPO="${HOME}/.m2/repository"
GROOVY_BASE="${M2_REPO}/org/codehaus/groovy"

# Build classpath from individual Groovy module JARs

GROOVY_CP=""

for module in groovy groovy-json groovy-xml; do
	jar=$(find "${GROOVY_BASE}/${module}" -name "${module}-*.jar" \
		-not -name "*-sources.jar" -not -name "*-javadoc.jar" \
		2>/dev/null | sort -V | tail -1)

	if [ -z "$jar" ]; then
		echo "ERROR: Required Groovy JAR not found: ${module}"
		echo ""
		echo "Run a full build first (./mvnw clean package -DskipTests) to download dependencies."
		exit 1
	fi

	GROOVY_CP="${GROOVY_CP:+${GROOVY_CP}:}${jar}"
done

groovy_args=(
	--repository-dir "$REPOSITORY_DIR"
	--project-root "$SCRIPT_DIR"
)

if [ -n "$output_file" ]; then
	groovy_args+=(--output "$output_file")
fi

java -cp "$GROOVY_CP" groovy.ui.GroovyMain "$GROOVY_SCRIPT" "${groovy_args[@]}"