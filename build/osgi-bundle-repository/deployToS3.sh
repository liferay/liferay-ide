#!/bin/bash

# Deploys the generated p2 repository to Linode
#
# S3cmd should be installed and configured

echo "please make sure your s3cmd has been configured"

if [ $# != 4 ]
then
  echo "Usage: $0 repo_path repo package version"
  exit 1
fi

REPO_PATH=$1
REPO=$2
PACKAGE=$3
VERSION=$4

echo "uploading to version $VERSION ..."
s3cmd --no-mime-magic --acl-public --delete-removed --delete-after sync $REPO_PATH/ s3://devtools-s3.liferay.com/$REPO/$PACKAGE/$VERSION/
echo