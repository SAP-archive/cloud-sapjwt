""" JwtValidation """ # pylint: disable=invalid-name
import os # pylint: disable=W0611
import platform # pylint: disable=W0611
import sapjwt.deps
import sapjwt

from ctypes import * # pylint: disable=W0401,W0611,W0614,C0411

sapjwtCDLL = 0
def getloadMySapLib():
    """ get static library handle """
    global sapjwtCDLL # pylint: disable=W0603
    return sapjwtCDLL

def setMySapLib(_lib):
    """ set static library handle """
    global sapjwtCDLL # pylint: disable=W0603
    if sapjwtCDLL == 0:
        sapjwtCDLL = _lib

def getStructSize():
    """ get structure size depending on platform """
    mystruct_size = 0
    if sizeof(c_voidp) == 8:
        mystruct_size = 644
    else:
        mystruct_size = 580
    return mystruct_size

ERROR_LENGTH = 512
ARRAY512 = c_ubyte * ERROR_LENGTH
class JWT_INFO(Structure): # pylint: disable=R0903
    """ C structure to SAPJWT native module. An internal class """
    _fields_ = [
        ("struct_size", c_size_t),
        ("pPayloadBuffer", c_char_p),
        ("lPayLoadBuffer", c_size_t),
        ("lastError", ARRAY512),
        ("pNameIdValue", c_char_p),
        ("lNameIdValue", c_size_t),
        ("pSubject", c_char_p),
        ("lSubject", c_size_t),
        ("pIssuer", c_char_p),
        ("lIssuer", c_size_t),
        ("pJTI", c_char_p),
        ("lJTI", c_size_t),
        ("pAudience", c_char_p),
        ("lAudience", c_size_t),
        ("issuedAt", c_size_t),
        ("tAlgId", c_uint),
        ("pKeyId", c_char_p),
        ("lKeyId", c_size_t)]

class jwtValidation(object): # pylint: disable=R0902
    """ JWT Validation Class. This is the public API for this module """
    sapjwtCDLL = 0
    mysapgetversion = sapjwtCDLL
    structsize = getStructSize()
    myJwtPayLoad = ""
    myKeyId = ""
    myKidBuff = create_string_buffer(255)
    validation_rc = 0
    errorString = ARRAY512()
    errorDescription = ""
    def __init__(self):
        self.sapjwtCDLL = getloadMySapLib()
        if  self.sapjwtCDLL == 0:
            self.sapjwtCDLL = cdll.LoadLibrary(sapjwt.getFullLibraryName())
            setMySapLib(self.sapjwtCDLL)
            #print("Loaded Library: " + sapjwt.getFullLibraryName())
        # MySapGetVersion
        self.mysapgetversion = self.sapjwtCDLL.MySapGetVersion
        self.mysapgetversion.argtypes = []
        self.mysapgetversion.restype = c_uint
        # MySapInitialize
        self.mysapinitialize = sapjwtCDLL.MySapInitialize
        self.mysapinitialize.argtypes = [c_char_p]
        self.mysapinitialize.restype = c_uint
        # MySapLoadTicketKey
        self.mysaploadticketkey = sapjwtCDLL.MySapLoadTicketKey
        self.mysaploadticketkey.argtypes = [c_char_p, c_int, c_char_p, c_int, c_int]
        self.mysaploadticketkey.restype = c_uint
        # MySapEvalJWToken
        self.mysapevaljwtoken = sapjwtCDLL.MySapEvalJWToken
        self.mysapevaljwtoken.argtypes = [c_char_p, c_size_t, c_char_p,
                                          c_size_t, c_char_p, POINTER(JWT_INFO)]
        self.mysapevaljwtoken.restype = c_uint
        # MySapParseCertificate
        self.mysapparsecertificate = sapjwtCDLL.MySapParseCertificate
        self.mysapparsecertificate.argtypes = [c_char_p, c_int, c_char_p, c_char_p, POINTER(c_int)]
        self.mysapparsecertificate.restype = c_uint
        # SsoExtSetProperty
        self.ssoextsetproperty = sapjwtCDLL.SsoExtSetProperty
        self.ssoextsetproperty.argtypes = [c_char_p, c_char_p]
        self.ssoextsetproperty.restype = c_uint
        # SsoExtGetProperty
        self.ssoextgetproperty = sapjwtCDLL.SsoExtGetProperty
        self.ssoextgetproperty.argtypes = [c_char_p]
        self.ssoextgetproperty.restype = c_char_p
        _rc = self.mysapinitialize(cast(0, c_char_p))
        if _rc != 0:
            print("error in initialize of SAPJWT") # pylint: disable=C0325

    def getLibraryVersion(self):
        """ return SAPJWT library version string """
        _version = self.mysapgetversion()
        if _version == "0":
            return "-1"
        return str(_version)

    def getLibraryVersionNum(self):
        """ return SAPJWT library version """
        _version = self.mysapgetversion()
        if _version == "0":
            return -1
        return int(_version)

    def loadPEM(self, pPEM):
        """ load or set a PEM for JWT verification """
        if not pPEM:
            return 15
        key = pPEM.encode("utf-8")
        keylen = len(key)
        _rc = self.mysaploadticketkey(key, keylen, cast(0, c_char_p), 0, 4)
        if _rc != 0:
            _rc = self.mysaploadticketkey(key, keylen, cast(0, c_char_p), 0, 2)
        return _rc

    def setVerificationKey(self, pSecret):
        """ set verification key, available in VCAP environment """
        if not pSecret:
            return 15
        key = pSecret.encode("utf-8")
        keylen = len(key)
        _rc = self.mysaploadticketkey(key, keylen, cast(0, c_char_p), 0, 2)
        return _rc

    def setSecret(self, pSecret):
        """ set verification key, available in VCAP environment """
        if not pSecret:
            return 15
        key = pSecret.encode("utf-8")
        keylen = len(key)
        _rc = self.mysaploadticketkey(key, keylen, cast(0, c_char_p), 0, 2)
        return _rc

    def setBase64Secret(self, pSecret):
        """ set verification key base64 encoded, available in VCAP environment """
        if not pSecret:
            return 15
        key = pSecret.encode("utf-8")
        keylen = len(key)
        _rc = self.mysaploadticketkey(key, keylen, cast(0, c_char_p), 0, 3)
        return _rc

    def getPayload(self):
        """ return JWT payload after successfull verification """
        return self.myJwtPayLoad

    def getJWPayload(self):
        """ return JWT payload after successfull verification """
        return self.myJwtPayLoad

    def getErrorDescription(self):
        """ return error reason after unsuccessfull verification """
        return self.errorDescription

    def getErrorRC(self):
        """ return error code after unsuccessfull verification """
        return self.validation_rc

    def evalJWToken(self, pJWT, pSecret=None):
        """ evaluate a JWT and return error code, 0 means success """
        _rc = 0
        if pSecret is None:
            _rc = self.checkToken(pJWT)
        else:
            _rc = self.checkToken(pJWT, pSecret)
        if _rc == 0:
            return self.myJwtPayLoad
        return ""

    def checkToken(self, pJWT, pSecret=None):
        """ evaluate a JWT and return JWT payload if succuessful else empty value """
        _rc = 0
        if not pJWT:
            return _rc
        self.myJwtPayLoad = create_string_buffer(len(pJWT))
        jwt_myInfo = JWT_INFO(self.structsize,
                              cast(self.myJwtPayLoad, c_char_p), len(self.myJwtPayLoad),
                              self.errorString,
                              cast(0, c_char_p), 0,
                              cast(0, c_char_p), 0,
                              cast(0, c_char_p), 0,
                              cast(0, c_char_p), 0,
                              cast(0, c_char_p), 0, 0, 0,
                              cast(self.myKidBuff, c_char_p), 255)
        jwts = pJWT.encode("utf-8")
        jwtslen = len(jwts)
        # start eval
        if pSecret is None:
            _rc = self.mysapevaljwtoken(jwts,
                                        jwtslen,
                                        cast(0, c_char_p), 0,
                                        cast(0, c_char_p),
                                        byref(jwt_myInfo))
        else:
            secrets = pSecret.encode("utf-8")
            secretslen = len(secrets)
            _rc = self.mysapevaljwtoken(jwts, jwtslen, secrets,
                                        secretslen, cast(0, c_char_p), byref(jwt_myInfo))
        # evaluation done
        if jwt_myInfo.lKeyId > 0:
            jwtKey = jwt_myInfo.pKeyId[0:jwt_myInfo.lKeyId].decode("utf-8")
            self.myKeyId = jwtKey
        else:
            self.myKeyId = ""
        if _rc != 0:
            self.myJwtPayLoad = ""
            es = cast(jwt_myInfo.lastError, c_char_p)
            self.errorDescription = es.value.decode("utf-8")
        else:
            jwt_string_buffer = jwt_myInfo.pPayloadBuffer[0:jwt_myInfo.lPayLoadBuffer].decode("utf-8")
            self.myJwtPayLoad = jwt_string_buffer

        self.validation_rc = _rc
        return _rc

    def getKeyId(self):
        """ return keyId from JWT header if available or empty value """
        if not self.myKeyId:
            return ""
        return self.myKeyId

    def parseCertAttribute(self, cert, attribute):
        """ parse attribute from x509 certificate """
        if not cert:
            return ""
        if not attribute:
            return ""
        _cert = cert.encode("utf-8")
        _certlen = len(_cert)
        _attribute = attribute.encode("utf-8")
        _certBuffer = create_string_buffer(_certlen)
        _certBufLen = c_int(_certlen)
        _rc = self.mysapparsecertificate(_cert, _certlen,
                                         _attribute, _certBuffer, byref(_certBufLen))
        if _rc != 0:
            return ""
        _retVal = _certBuffer.value.decode("utf-8")
        return _retVal

    def getSubject(self, cert):
        """ return subject from X509 """
        return self.parseCertAttribute(cert, "SUBJECT")

    def getIssuer(self, cert):
        """ return issuer from X509 """
        return self.parseCertAttribute(cert, "ISSUER")

    def getProperty(self, propName):
        """ return SAPJWT internal property """
        val = propName.encode("utf-8")
        ret = self.ssoextgetproperty(val)
        if ret is None:
            return ""
        return ret

    def setProperty(self, propName, value):
        """ set SAPJWT internal property """
        prop = propName.encode("utf-8")
        val = value.encode("utf-8")
        return self.ssoextsetproperty(prop, val)

    __del__ = lambda self: None
