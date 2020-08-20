#!/bin/sh -x

scriptDir=$(dirname "$0")
classpathDir=$(dirname "${scriptDir}")/lib

if [ $# -eq 2 ]; then
	mdFile=$1
	targetDir=$2
	java -jar "${classpathDir}/com.liferay.knowledge.base.markdown.converter.cli-1.0.0.jar" "${mdFile}" "${targetDir}" --offline
elif [ $# -eq 3 ]; then
  mdFile=$1
  targetDir=$2
  imageDir=$3
	java -jar "${classpathDir}/com.liferay.knowledge.base.markdown.converter.cli-1.0.0.jar" "${mdFile}" "${targetDir}" "${imageDir}" --offline
else
	echo
	echo Usage: ./convert.sh \[Markdown file or directory to convert\] \[The target directory of the generated html file\] \[The image resources that markdown files may need\]
	echo
	echo The first argument is the path to the Markdown file to convert to HTML.
	echo
	echo The second argument is optional. It specifies the path to the HTML file to be created. If this argument is omitted, the HTML file to be created is created in the same directory as the Markdown file and has the same filename as the Markdown file except that the .markdown file extension is replaced by the .html file extension.
	echo
	echo The third argument is optional. It specifies a folder with Image resources referenced by the Image link appears in Markdown file.
	exit 1
fi

exit 0