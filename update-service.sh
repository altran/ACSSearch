#!/bin/sh

A=AcsSearch
V=0.0.1-SNAPSHOT
JARFILE=$A-$V.jar

pkill -f $A

# Artifactory location
server="http://mvnrepo.cantara.no"
repo="content/repositories/snapshots/com/altran"

# Maven artifact location
name=$A-$V
path="$server/$repo/$A"
version=`curl -s "$path/maven-metadata.xml" | grep "<version>" | sed "s/.*<version>\([^<]*\)<\/version>.*/\1/"`
echo "Version $version"
build=`curl -s "$path/$version/maven-metadata.xml" | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
jar="$A-$build.jar"
url="$path/$version/$jar"

# Download
echo $url
wget -O $JARFILE -q -N $url
