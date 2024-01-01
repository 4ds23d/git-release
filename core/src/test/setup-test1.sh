#!/usr/bin/env bash
set -e

SCRIPT_PATH=$(dirname "$(realpath "$0")")
DIR_PATH=$SCRIPT_PATH/../../build/test1

if [ -d $DIR_PATH/.git ]; then
 echo "Reset git"
 rm -rf $DIR_PATH/.git
fi

mkdir -p $DIR_PATH
cd $DIR_PATH
git init .
echo "Initial commit" > README.txt
git add -A && git commit -m "initial commit"
echo "Init repository"


function create_change {
   echo "$1" >> change.txt
   git add -A && git commit -m "$1"
}

git checkout -b develop
create_change "develop - 1"
create_change "ABC-1, ABC-2 multiple change due to fix in FIX-123"
git checkout -b feature/ABC-3
create_change "ABC-3 abc3 change"
create_change "ABC-3 review remarks"
git checkout -b feature/ABC-4 develop
create_change "ABC-4 implement change"
git checkout develop && git merge feature/ABC-4
git checkout -b release/r1
create_change "ABC-6 r1"
git checkout -b feature/ABC-5 develop
create_change "ABC-5 implement change"



