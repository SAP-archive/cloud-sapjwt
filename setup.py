"""setup for sap-py-jwt
See:
https://github.com/SAP-samples/cloud-sapjwt
"""

# To use a consistent encoding
from codecs import open # pylint: disable=W0622
from os import path

# Always prefer setuptools over distutils
from setuptools import setup, find_packages

here = path.abspath(path.dirname(__file__)) # pylint: disable=invalid-name

# Get the long description from the README file
with open(path.join(here, 'README.md'), encoding='utf-8') as f:
    long_description = f.read() # pylint: disable=invalid-name

def get_version():
    """ get version """
    with open('version.txt') as ver_file:
        version_str = ver_file.readline().rstrip()
    return version_str


def get_install_requires():
    """ install requires """
    reqs = []
    with open('requirements.txt') as reqs_file:
        for line in iter(lambda: reqs_file.readline().rstrip(), ''):
            reqs.append(line)
    return reqs


def get_extras_require():
    """ extras """
    with open('test-requirements.txt') as reqs_file:
        reqs = [line.rstrip() for line in reqs_file.readlines()]
    return {'test': reqs}


setup(name="sap_py_jwt",
      version=get_version(),
      entry_points={"distutils.commands":
                    ["whitesource_update = plugin.WssPythonPlugin:SetupToolsCommand"]},
      packages=find_packages(exclude=['contrib', 'docs', 'tests*', 'coverage', 'scripts']),
      description="SAP CP Security Client Library for JWT offline validation",
      long_description=long_description,
      long_description_content_type="text/markdown",
      install_requires=get_install_requires(),
      extras_require=get_extras_require(),
      keywords="sap jwt sapjwt python",
      author="SAP SE",
      author_email="secure@sap.com",
      license="SAP Developer",
      url="https://github.com/SAP-samples/cloud-sapjwt",
      package=['sapjwt'],
      package_dir={'sapjwt': 'sapjwt'},
      package_data={'sapjwt': ['deps/linux/x64/libsapssoext.so',
                               'deps/linux/ppc64/libsapssoext.so',
                               'deps/linux/ppc64le/libsapssoext.so',
                               'deps/darwin/x64/libsapssoext.dylib',
                               'deps/win32/x64/sapssoext.dll'
                              ]},
      classifiers=[
        # http://pypi.python.org/pypi?%3Aaction=list_classifiers
        "Development Status :: 5 - Production/Stable",
        "Topic :: Security",
        "License :: OSI Approved :: Apache Software License",
        "Natural Language :: English",
        "Operating System :: MacOS :: MacOS X",
        "Operating System :: POSIX",
        "Operating System :: POSIX :: BSD",
        "Operating System :: POSIX :: Linux",
        "Operating System :: Microsoft :: Windows",
        "Programming Language :: Python",
        "Programming Language :: Python :: 2",
        "Programming Language :: Python :: 2.7",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.4",
        "Programming Language :: Python :: 3.5",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: Implementation :: CPython",
        "Programming Language :: Python :: Implementation :: PyPy",
    ], 
)
