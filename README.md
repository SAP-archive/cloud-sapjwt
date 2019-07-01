# cloud-sapjwt
==============

JSON Web Token (JWT) offline validation for SAPCP client applications with current binaries of SAPJWT verification library.

This project contains the JWT binding. It also includes the native libraries to run on XSA platforms. 
If you need another platforms, please write to the author.

# Platforms

Supported platforms: Windows | Linux | MacOS

This package contains a ctypes based wrapper for Python to use SAPJWT library

#### Hello World

This standard example is from http://jwt.io 

```python
    import sapjwt
        
    jwtver = sapjwt.jwtValidation()
        
    print("SAPJWT version : " + jwtver.getLibraryVersion())
        
    jwtver.setVerificationKey("secret")
    _rc = jwtver.checkToken("eyJhbGciOiJIUzI1NiIsImtpZCI6InRlc3QiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOmZhbHNlfQ.b2CDs7y56N9VWUh6wpLBdws-6omVyihJhpnBB7MdHCw")

    if _rc != 0:
        print("Validation error: " + jwtver.getErrorDescription())
    else:
        print("Validation key-Id from JWT: " +jwtver.getKeyId())
        print("Validation succeeded, payload from JWT: " + jwtver.getPayload())

```

# Getting started

This project should not be included but you should include sap_xssec as API project. This project provides a wrapper to the native 
validation library for JWT.
From your project directory, run (see below for requirements):

```python
    from sap import xssec
```

# Error situations

The standard error for signature operations is the situation, that the signature is not valid. This error is typical and you should handle
it carefully! and not as fatal error or assert.
If you think, it must work, but it does not, then you can trace the native functions.
SAPSSOEXT library allows you to set the environment variables:
* SAP_EXT_TRC to define a trace file in your file system
* SAP_EXT_TRL an integer 0 to 3

```
set SAP_EXT_TRC=stdout
set SAP_EXT_TRL=3
```

If you run your application in CloudFoundry or XSA then you can define environment variables with client command tool cf / xs, see
https://docs.run.pivotal.io/devguide/deploy-apps/manifest.html#env-block 

In cf landscapes you can then cf logs <your-app> and you will see trace from JWT validation


# Install via pip

In order to configure the sap pypi registry you need to issue the following command:

```
pip install --user sap_xssec
```

If you have not yet configured any sap pypi registry use this call:

```
pip install sap_xssec
```

# CF Deployment 

The deployment of python code to cloudfoundry executes 'pip install'. The package sap_xssec vendor dependent package which means it is not available in public repositories. The solution for this is a deployment with a local vendor folder, see
https://docs.cloudfoundry.org/buildpacks/python/index.html#vendoring 

Your application should have a so called requirements.txt file. In this file you define your dependencies, e.g. sap_xssec (this package includes sap_py_jwt). So before a push to cf you create a local vendor folder and put all dependend binaries into this folder.

The push command uploads the complete local folder to CF and the python buildpack then installs the private packages from the vendor folder.

# Known issues
This project provide binary language bindings for cloud platforms. Linux x64 can be used in docker images which depend on Debian derivates of Linux. There is currently no Alpine binary available, because of different linker, see https://www.musl-libc.org/.

# License
Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the SAP Sample Code License, except as noted otherwise in the [LICENSE](LICENSE) file.
