/**
 * (C) Copyright 2019 SAP SE Walldorf
 *
 * Author:  SAP SE, Security Development
 * 
 * SAP SE DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL SAP SE BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 * 
 * This class provides wrapper functionality to SAPJWT for JSON Web Token 
 * (JWT) in Java.
 * 
 * @version 2 2019
 * 
 */

package com.sap.cloud.security.jwt;

import com.mysap.sso.JWT;

/**
 * X509 certificate parser
 */
public class X509Attribute {

    private static JWT jwtWrapper;
    private static final String UTF8 = "utf-8";

    public X509Attribute() {
        if(X509Attribute.jwtWrapper == null)
           X509Attribute.jwtWrapper = new JWT();
    }

    @SuppressWarnings("static-access")
    public String getSubject(String x509Cert) throws Exception {
        return jwtWrapper.parseCertificate(x509Cert.getBytes(X509Attribute.UTF8), JWT.ISSUER_CERT_SUBJECT);
    }

    @SuppressWarnings("static-access")
    public String getIssuer(String x509Cert) throws Exception {
        return jwtWrapper.parseCertificate(x509Cert.getBytes(X509Attribute.UTF8), JWT.ISSUER_CERT_ISSUER);
    }

    @SuppressWarnings("static-access")
    public String getFingerprint(String x509Cert) throws Exception {
        return jwtWrapper.parseCertificate(x509Cert.getBytes(X509Attribute.UTF8), JWT.ISSUER_CERT_FINGERPRINT);
    }
}
