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
 * @author SAP SE
 *
 */
public class JwtValidation {

    public static final int JWT_ALG_UNDEFINED      = 0;
    public static final int JWT_ALG_HS256          = 1;
    public static final int JWT_ALG_HS384          = 2;
    public static final int JWT_ALG_HS512          = 3;
    public static final int JWT_ALG_RS256          = 4;
    public static final int JWT_ALG_RS384          = 5;
    public static final int JWT_ALG_RS512          = 6;

    private static JWT jwtWrapper;

    public JwtValidation() {
        if(JwtValidation.jwtWrapper == null)
           JwtValidation.jwtWrapper = new JWT();
    }

    public String getBuilidVersion() {
        return jwtWrapper.getBuilidVersion();
    }

    public String  getSapJwtVersion() {
        return jwtWrapper.getSapJwtVersion();
    }

    /**
     * Parse the header of a JSON Web Token (JWT) 
     * 
     * @param jwt JWT token to be validated
     * @return String of JWT header
     *
     * @throws Exception Text message with error.
     */
    public String parseJwtHeader( String jwt ) throws Exception {
        return jwtWrapper.parseJwtHeader(jwt);
    }

    /**
     * Returns hash algorithm
     * 
     * @return alg_id
     */
    public int getHashAlg() {
        return jwtWrapper.getHashAlg();
    }

    /**
     * Evaluate a JSON Web Token (JWT) 
     * Use key as verification key.
     * 
     * @param jwt JWT token to be validated
     * @param rsa_key key alg
     * @return String with JWT payload
     *
     * @throws Exception Text message with error.
     */
    public String checkRsaJwToken( String jwt, String rsa_key ) throws Exception {
        return jwtWrapper.checkRsaJwToken(jwt, rsa_key);
    }

    /**
     * Evaluate a JSON Web Token (JWT) Use key as verification key.
     * 
     * @param jwt JWT token to be validated
     * @param rsa_key key alg
     * @return String with JWT payload
     *
     * @throws Exception Text message with error.
     */
    public String checkJwToken(String jwt, String rsa_key) throws Exception {
        return jwtWrapper.checkJwToken(jwt, rsa_key);
    }

    /**
     * Parse certificate
     * @param cert             Certificate received from evalLogonTicket
     * @param info_id       One of the request ids
     * 
     * @return Info string from certificate
     *  
     */
    public String parseCertificate(
        byte[] cert,
        int info_id) {
        return JWT.parseCertificate(cert, info_id);
    }
        
    /**
     * Get property
     * @param name   property name to be retrieved
     * 
     * @return property string
     *  
     */
    public String getProperty( String name ) {
        return JWT.getProperty(name);
    }

    /**
     * Set property
     * @param name   property name to be set
     * @param value  property value to be set
     * 
     * @return true/false whether set was OK
     *  
     */
    public boolean setProperty( String name, String value ) {
        return JWT.setProperty(name, value);
    }
}
