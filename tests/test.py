# pylint: disable=invalid-name,C0325,C0301
""" test code snippet """
import os
import unittest
import sapjwt

class SapJjwtTest(unittest.TestCase):
    """ Test class for SAPJWT """
    jwtver = None

    @classmethod
    def setUpClass(cls):
        """ Test class static setup """
        os.environ["SAP_EXT_JWT_ALG"] = "*"
        cls.jwtver = sapjwt.jwtValidation()

    def setUp(self):
        """ Test class setup """
        verN = self.jwtver.getLibraryVersionNum()
        if verN is None:
            raise RuntimeError(
                'Failed to initialize library: {0}'.format(verN))

    def test_version(self):
        """ Test Version of SAPJWT """
        self.jwtver.getLibraryVersion()

    def test_hmac_verification(self):
        """ Test HMAC verification of SAPJWT """
        self.jwtver.setVerificationKey("secret")
        _rc = self.jwtver.checkToken("eyJhbGciOiJIUzI1NiIsImtpZCI6InRlc3QiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOmZhbHNlfQ.b2CDs7y56N9VWUh6wpLBdws-6omVyihJhpnBB7MdHCw")
        if _rc != 0:
            print("Validation error: " + self.jwtver.getErrorDescription())
        else:
            print("Validation key-Id from JWT: " + self.jwtver.getKeyId())
            print("Validation succeeded, payload from JWT: " + self.jwtver.getPayload())

    def test_check_kid(self):
        """ Test kid parser in SAPJWT """
        self.jwtver.setSecret("")
        jwt2 = sapjwt.jwtValidation()
        jwt2.checkToken("eyJhbGciOiJIUzI1NiIsImtpZCI6InRlc3QiLCJ0eXAiOiJKV1QifQ. .")
        if jwt2.getKeyId() != "test":
            raise RuntimeError(
                'Failed to parse out keyId, expected test, but was: {0}'.format(jwt2.getKeyId()))

    def test_invalid_signature(self):
        """ Negative test for signature validation of SAPJWT """
        self.jwtver.setVerificationKey("dontKnow")
        _rc = self.jwtver.checkToken("eyJhbGciOiJIUzI1NiIsImtpZCI6InRlc3QiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOmZhbHNlfQ.b2CDs7y56N9VWUh6wpLBdws-6omVyihJhpnBB7MdHCw")
        if _rc == 0:
            raise RuntimeError(
                'Failed signature check expected rc: {0}'.format(_rc))
        print("Error expected: " + self.jwtver.getErrorDescription())

    def test_valid_signature(self):
        """ Positive test for signature validation of SAPJWT """
        self.jwtver.setVerificationKey("secret")
        _rc = self.jwtver.checkToken("eyJhbGciOiJIUzI1NiIsImtpZCI6InRlc3QiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOmZhbHNlfQ.b2CDs7y56N9VWUh6wpLBdws-6omVyihJhpnBB7MdHCw")
        if _rc != 0:
            raise RuntimeError(
                'Signature check failed with rc: {0}'.format(_rc))
        print("Validation succeeded, payload from JWT: " + self.jwtver.getPayload())

    def test_library_version(self):
        """ Test version number of SAPJWT , currently expected to be 1 """
        verN = self.jwtver.getLibraryVersionNum()
        if verN != 1:
            raise RuntimeError(
                'Library version of SAPJWT not 1 but : {0}'.format(verN))

if __name__ == '__main__':
    unittest.main()
