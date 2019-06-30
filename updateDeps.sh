#!/bin/bash


SRC_VERION=
if [[ -z "$1" ]]; then
   SRC_VERION=8.5.27
else
   SRC_VERION="$1"
fi
echo "start update to version: ${SRC_VERION}"
CURRENT_PWD=$(pwd)
function download ( )
{
   wget -q https://nexus.wdf.sap.corp:8443/nexus/content/repositories/deploy.releases.cclmake/com/sap/commoncryptolib/${1}/commoncryptolib/${SRC_VERION}/commoncryptolib-${SRC_VERION}-${1}.tar.gz -O  ${2}/ccl.tar.gz
   if [ $? != 0 ]; then
      echo "Download of version ${SRC_VERION} failed"
     exit 1
   fi
   cd ${2}
   tar -xzvf ccl.tar.gz ${3}  &> /dev/null
   if [ $? != 0 ]; then
      echo "Could not extract file ${3}"
     exit 1
   fi
   rm -f ccl.tar.gz  &> /dev/null
   echo "Update file ${4} in ${2}"
   mv ${3} ${4}  &> /dev/null
   cd ${CURRENT_PWD}
}

download 'windows_x86_64' 'sapjwt/deps/win32/x64' 'sapjwt.dll' 'sapssoext.dll'
download 'linux_x86_64' 'sapjwt/deps/linux/x64' 'libsapjwt.so' 'libsapssoext.so'
download 'macosx_10_7_x86_64' 'sapjwt/deps/darwin/x64' 'libsapjwt.dylib' 'libsapssoext.dylib'
download 'linux_ppc_64' 'sapjwt/deps/linux/ppc64' 'libsapjwt.so' 'libsapssoext.so'
download 'linux_ppcle_64' 'sapjwt/deps/linux/ppc64le' 'libsapjwt.so' 'libsapssoext.so'

echo "update finished..."
