#!/bin/bash

# current Git branch
branch=$(git symbolic-ref HEAD | sed -e 's,.*/\(.*\),\1,')

versionLabel=$1

git tag $versionLabel
git push origin $versionLabel
cd documentation && mkdocs gh-deploy