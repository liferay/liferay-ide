#!/bin/sh -x

if [ $# -lt 1 ]; then
  echo "Error: You need to provide liferay docs tag. './generate_offline.sh liferay-ide-offline-3.9-m2-20200811'"
  exit 1
fi

if [ -z "$BINTRAY_USER" ]; then
  echo "Must provide env vars BINTRAY_USER BINTRAY_API_KEY"
  exit 1
fi

if [ -z "$BINTRAY_API_KEY" ]; then
  echo "Must provide env vars BINTRAY_USER BINTRAY_API_KEY"
  exit 1
fi

github_repo="https://github.com/gamerson/liferay-docs/archive/"
pwd=$PWD
tag=$1

curl -s -L ${github_repo}/${tag}.zip --retry 5 --output ${tag}.zip

if [ $? -ne 0 ]; then
    echo "Failed to download liferay docs zip file."
    exit 1
fi

zipfile=${tag}.zip

unzip -q -o ${zipfile}

if [ $? -ne 0 ]; then
  echo "Failed to unzip ${zipfile}"
  exit 1
fi

unzip_dir=liferay-docs-${tag}

if [ -d "${unzip_dir}" ]; then
  "${pwd}/markdown-converter/convert.sh" "${pwd}/${unzip_dir}/en/developer/tutorials/articles/03-upgrading-code-to-liferay-7.2/" "${pwd}/output"

  count=$(ls "${pwd}/output" | wc -w)

  if [ "$count" -gt "0" ]; then
    cd "${pwd}/output/"
    zip -q -r "${pwd}/code-upgrade-docs-${tag}.zip" ./

    cd "${pwd}"
    rm -rf "${unzip_dir}" "${zipfile}" "output"

    if [ -s "code-upgrade-docs-${tag}.zip" ]; then
      curl -X PUT -T code-upgrade-docs-${tag}.zip -u $BINTRAY_USER:$BINTRAY_API_KEY "https://api.bintray.com/content/gamerson/liferay-ide-files/docs/code-upgrade-docs-${tag}.zip;bt_package=contents;bt_version=1;publish=1"

      if [ $? -ne 0 ]; then
        echo "Failed to publish to bintray."
        exit 1
      fi
    else
      echo "Failed to create code upgrade offline file for upgrade planner."
      exit 1
    fi

    exit 0
  fi
fi