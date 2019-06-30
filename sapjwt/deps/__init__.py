# pylint: disable=missing-docstring,invalid-name,missing-docstring,C0325,C0301,C0412,R0911,R1705
import os
import sys
import platform as localplatform
from sys import platform

def getLibraryName():
    if platform == "linux" or platform == "linux2":
        # linux
        return "libsapssoext.so"
    elif platform == "darwin":
        # OS X
        return "libsapssoext.dylib"
    elif platform == "win32":
        # Windows
        return "sapssoext.dll"
    elif platform == "win64":
        # Windows
        return "sapssoext.dll"
    elif platform == "cygwin":
        # Windows
        return "sapssoext.dll"

def getFolderName():
    if platform == "linux" or platform == "linux2":
        # linux
        if localplatform.machine() == "x86_64":
            return os.path.join("linux", "x64")
        else:
            return os.path.join("linux", localplatform.machine())
    elif platform == "darwin":
        # OS X
        return os.path.join("darwin", "x64")
    elif platform == "win32":
        # Windows
        if localplatform.machine() == "AMD64":
            return os.path.join("win32", "x64")
        return os.path.join("win32", "ia32")
    elif platform == "win64":
        # Windows
        return os.path.join("win32", "x64")
    elif platform == "cygwin":
            # Windows
        return os.path.join("win32", "ia32")
