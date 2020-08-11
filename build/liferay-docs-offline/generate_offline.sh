# Download liferay-docs repo for a specific tag
# https://github.com/gamerson/liferay-docs/archive/liferay-ide-offline-3.9-m2-20200730.zip
# unzip the liferay-docs content
# call the markdown-converter script to only convert this path
# liferay-docs/en/developer/tutorials/articles/03-upgrading-code-to-liferay-7.2/
#/bin/convert.sh /Users/greg/my\ repos/liferay-ide/build/liferay-docs-offline/liferay-docs-liferay-ide-offline-3.9-m2-20200730/en/developer/tutorials/articles/03-upgrading-code-to-liferay-7.2 upgrade_plan_export
# upload new zip to bintray at this location:https://dl.bintray.com/gamerson/liferay-ide-files/zips/

#!/bin/sh

if [ $# -lt 1 ]; then
    echo "Error: You need to provide liferay docs tag. './generate_offline_docs.sh liferay-ide-offline-3.9-m2-20200811'"
    exit 1
fi

pwd=$PWD
tag=$1
github_repo="https://github.com/gamerson/liferay-docs/archive/"

curl --proxy http://localhost:8001 -s -L ${github_repo}/${tag}.zip --retry 5 --output ${tag}.zip

if [ $? -ne 0 ]; then
    echo "Failed to download liferay docs zip file."
    exit 1
fi

offline_doc_zip=${tag}.zip

if [ -s "${offline_doc_zip}" ]; then
    #run unzip command
    unzip -q -o ${offline_doc_zip}
fi

offline_lfieray_doc_dir=liferay-docs-${tag}

if [ -d "${offline_lfieray_doc_dir}" ]; then
    #run convert shell
    ${pwd}/markdown-converter/bin/convertoffline.sh ${pwd}/${offline_lfieray_doc_dir}/en/developer/tutorials/articles/03-upgrading-code-to-liferay-7.2/ ${pwd}/output

    count=`ls ${pwd}/output|wc -w`
    if [ "$count" -gt "0" ]; then
        cd ${pwd}/output/
        zip -q -r "${pwd}/liferay-docs.zip" ./
        rm -rf *

        cd ${pwd}
        rm -rf ${offline_lfieray_doc_dir} "${offline_doc_zip}"

        if [ -s "liferay-docs.zip" ]; then
            curl -sSf  -H "X-JFrog-Art-Api:<API_KEY>" or -u "<USERNAME>:<PASSWORD>" -X PUT -T "${pwd}/liferay-docs.zip" "https://dl.bintray.com/gamerson/liferay-ide-files/zips/"
        else
            echo "Failed to create liferay docs offline file for upgrade planner."
        fi

        exit 0
    fi
fi


