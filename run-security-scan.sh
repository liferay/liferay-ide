#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
	echo "Usage: $0 [--module <module-path>] [--bug-type <BUG_TYPE>] [--zip [<output>]]"
	echo ""
	echo "Options:"
	echo "  --module, -m <path>     Scan a single module (e.g. tools/plugins/com.liferay.ide.core)"
	echo "  --bug-type, -b <type>   Filter to a single bug type (e.g. XXE_SAXPARSER)"
	echo "  --zip, -z [<output>]    Zip all spotbugsXml.xml reports into an archive"
	echo "                          (default: spotbugs-reports.zip)"
	echo ""
	echo "All other arguments are passed through to Maven."
	exit 0
}

module=""
bug_type=""
zip_output=""
mvn_extra_args=()

while [ $# -gt 0 ]; do
	case "$1" in
		--module|-m)
			module="$2"
			shift 2
			;;
		--bug-type|-b)
			bug_type="$2"
			shift 2
			;;
		--zip|-z)
			shift
			if [ $# -gt 0 ] && [[ "$1" != -* ]]; then
				zip_output="$1"
				shift
			else
				zip_output="spotbugs-reports.zip"
			fi
			;;
		--help|-h)
			usage
			;;
		*)
			mvn_extra_args+=("$1")
			shift
			;;
	esac
done

mvn_args=(compile spotbugs:spotbugs -DskipTests --fail-at-end)

if [ -n "$module" ]; then
	mvn_args+=(-pl "$module")
fi

if [ -n "$bug_type" ]; then
	filter_file=$(mktemp "${TMPDIR:-/tmp}/spotbugs-filter-XXXXXX.xml")
	cat > "$filter_file" <<-EOF
		<FindBugsFilter>
		  <Match>
			<Bug pattern="${bug_type}" />
		  </Match>
		</FindBugsFilter>
	EOF
	mvn_args+=("-Dspotbugs.includeFilterFile=${filter_file}")
fi

# Determine which directories to search for reports
if [ -n "$module" ]; then
	search_dir="${SCRIPT_DIR}/${module}"
else
	search_dir="${SCRIPT_DIR}"
fi

# Clear stale reports before running
find "$search_dir" -path '*/target/spotbugsXml.xml' -type f -delete 2>/dev/null

# Use spotbugs:spotbugs (report-only) instead of spotbugs:check (fail-on-bugs)
# so that all modules get scanned even when some have violations
"${SCRIPT_DIR}/mvnw" "${mvn_args[@]}" "${mvn_extra_args[@]}"
mvn_exit_code=$?

if [ -n "$filter_file" ]; then
	rm -f "$filter_file"
fi

if [ $mvn_exit_code -ne 0 ]; then
	echo ""
	echo "WARNING: Maven build failed with exit code ${mvn_exit_code}. Reporting on partial results."
fi

echo ""
echo "========================================"
echo "  SpotBugs Security Scan Results"
echo "========================================"
echo ""

total_bugs=0
all_bug_types=""

while IFS= read -r xml_file; do
	module_dir="$(dirname "$(dirname "$xml_file")")"
	module_name="$(basename "$module_dir")"

	bug_count=$(xmllint --xpath 'count(//BugInstance)' "$xml_file" 2>/dev/null)

	if [ "$bug_count" -gt 0 ] 2>/dev/null; then
		echo "----------------------------------------"
		echo "Module: ${module_name} (${bug_count} bugs)"
		echo "----------------------------------------"

		for i in $(seq 1 "$bug_count"); do
			bt=$(xmllint --xpath "string(//BugInstance[$i]/@type)" "$xml_file" 2>/dev/null)
			priority=$(xmllint --xpath "string(//BugInstance[$i]/@priority)" "$xml_file" 2>/dev/null)
			source_file=$(xmllint --xpath "string(//BugInstance[$i]/SourceLine[@primary='true']/@sourcefile)" "$xml_file" 2>/dev/null)
			start_line=$(xmllint --xpath "string(//BugInstance[$i]/SourceLine[@primary='true']/@start)" "$xml_file" 2>/dev/null)
			short_message=$(xmllint --xpath "string(//BugInstance[$i]/ShortMessage)" "$xml_file" 2>/dev/null)

			case $priority in
				1) severity="High" ;;
				2) severity="Medium" ;;
				3) severity="Low" ;;
				*) severity="Unknown" ;;
			esac

			echo "  ${severity}: ${short_message} [${bt}]"
			echo "    At ${source_file}:${start_line}"

			all_bug_types="${all_bug_types}${bt}"$'\n'
		done

		total_bugs=$((total_bugs + bug_count))
		echo ""
	fi
done < <(find "$search_dir" -path '*/target/spotbugsXml.xml' -type f | sort)

echo "========================================"
echo "  Summary"
echo "========================================"
echo ""
echo "Total bugs: ${total_bugs}"
echo ""

if [ $total_bugs -gt 0 ]; then
	echo "Bugs by type:"
	echo "$all_bug_types" | sort | uniq -c | sort -rn | while read -r count bt; do
		if [ -n "$bt" ]; then
			printf "  %-50s %d\n" "$bt" "$count"
		fi
	done
	echo ""
fi

if [ -n "$zip_output" ]; then
	zip_path="${SCRIPT_DIR}/${zip_output}"

	rm -f "$zip_path"

	report_files=()
	while IFS= read -r f; do
		report_files+=("$f")
	done < <(find "$search_dir" -path '*/target/spotbugsXml.xml' -type f | sort)

	if [ ${#report_files[@]} -eq 0 ]; then
		echo "No spotbugsXml.xml reports found to zip."
	else
		# Use relative paths so each module's report is distinguishable
		relative_files=()
		for f in "${report_files[@]}"; do
			relative_files+=("${f#"${SCRIPT_DIR}/"}")
		done

		(cd "$SCRIPT_DIR" && zip "$zip_path" "${relative_files[@]}")

		echo ""
		echo "Zipped ${#report_files[@]} report(s) to ${zip_path}"
	fi
fi

if [ $mvn_exit_code -ne 0 ]; then
	exit $mvn_exit_code
fi

if [ $total_bugs -gt 0 ]; then
	exit 1
fi