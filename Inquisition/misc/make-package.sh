#!/bin/bash
export VERSION=0.11
set -o verbose
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/Java.Inquisition.jar mykey
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/lib/AbsoluteLayout.jar mykey
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/lib/jdom.jar mykey
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/lib/squareness.jar mykey
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/lib/swing-layout-1.0.1.jar mykey
/usr/java/jdk1.5.0_09/bin/jarsigner -storepass password dist/lib/javahighlite.jar mykey
cp -v src/javainquisition/LICENSE dist
cp -v src/javainquisition/*LICENSE.txt dist
zip -r inquisition-$VERSION.zip dist 
tar cvvzf inquisition-$VERSION.tar.gz dist

