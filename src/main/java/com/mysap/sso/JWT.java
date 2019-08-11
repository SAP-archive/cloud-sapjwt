package com.mysap.sso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;
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

public class JWT
{
    public static final int ISSUER_CERT_SUBJECT    = 0;
    public static final int ISSUER_CERT_ISSUER     = 1;
    public static final int ISSUER_CERT_SERIALNO   = 2;
    public static final int ISSUER_CERT_SUMMARY    = 3;
    public static final int ISSUER_CERT_SERIAL     = 4;
    public static final int ISSUER_CERT_VALIDITY   = 5;
    public static final int ISSUER_CERT_FINGERPRINT= 6;
    public static final int ISSUER_CERT_ALGID      = 7;
    public static final int ISSUER_CERT_ALL        = 8;
 
    public static final int JWT_ALG_UNDEFINED      = 0;
    public static final int JWT_ALG_HS256          = 1;
    public static final int JWT_ALG_HS384          = 2;
    public static final int JWT_ALG_HS512          = 3;
    public static final int JWT_ALG_RS256          = 4;
    public static final int JWT_ALG_RS384          = 5;
    public static final int JWT_ALG_RS512          = 6;

    private final static String LOCATION           = JWT.class.getName();
    private final static Logger LOGGER             = Logger.getLogger(LOCATION); 
    private static boolean initialized             = false;
    private static String SECLIBRARY;
    private static String SECSUBDIR;
    private String header = "";
    private String payload = "";
    private String user_name = "";
    private int jwt_alg = 0;
    private static String version = null;
    private static String build_version = null;

    static {
        String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        /*
         * Registers the native methods from the native shared library
         */
        LOGGER.info(LOCATION + ": Before loading   : " + SECLIBRARY);
        LOGGER.info(LOCATION + ": Operation system : " + System.getProperty("os.name"));
        LOGGER.info(LOCATION + ": OS architecture  : " + System.getProperty("os.arch"));
        LOGGER.info(LOCATION + ": OS version       : " + System.getProperty("os.version"));
        LOGGER.info(LOCATION + ": Java version     : " + System.getProperty("java.version"));
        File temp = null;
        if (       osname.indexOf("windows") >= 0) {
            SECSUBDIR  = "windows";
            SECLIBRARY = "sapjwt.dll";       /* Windows */
        } else if (osname.indexOf("linux") >= 0){
            SECSUBDIR  = "linux";
            SECLIBRARY = "libsapjwt.so";     /* Linux */
        } else if (osname.indexOf("sun") >= 0){
            SECSUBDIR  = "sun";
            SECLIBRARY = "libsapjwt.so";     /* Sun-OS  */
        } else if (osname.indexOf("aix") >= 0){
            SECSUBDIR  = "aix";
            SECLIBRARY = "libsapjwt.so";     /* AIX */
        } else if (osname.indexOf("hp") >= 0)  {
            SECSUBDIR  = "hp";
            if (false == System.getProperty("os.arch").startsWith("IA64")) {
                SECLIBRARY = "libsapjwt.sl"; /* HP RISC */
            } else {
                SECLIBRARY = "libsapjwt.so"; /* HP IA64 */
            }
        } else if (osname.indexOf("mac") >= 0)  {
            SECSUBDIR  = "mac";
            SECLIBRARY = "libsapjwt.dylib";  /* MacOS */
        } else {
            SECSUBDIR  = ".";
            SECLIBRARY = "libsapjwt.so";     /* Default for Linux/Unix */
        }
        try {
            int bytesRead;
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/jni/" + SECSUBDIR + "/" + System.getProperty("os.arch") + "/" + SECLIBRARY);
            if( in == null ) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("jni/" + SECSUBDIR + "/" + System.getProperty("os.arch") + "/" + SECLIBRARY);
            }
            if( in == null ) {
                in = JWT.class.getResourceAsStream("/jni/" + SECSUBDIR + "/" + System.getProperty("os.arch") + "/" + SECLIBRARY);
            }
            if( in == null ) {
                in = JWT.class.getClass().getResourceAsStream("/jni/" + SECSUBDIR + "/" + System.getProperty("os.arch") + "/" + SECLIBRARY);
            }
            if( in == null ) {
                in = ClassLoader.getSystemResourceAsStream("/jni/" + SECSUBDIR + "/" + System.getProperty("os.arch") + "/" + SECLIBRARY);
            }
            if (in != null && in.available() > 0) {
                temp = new File(System.getProperty("java.io.tmpdir") + "/" + SECLIBRARY);
                if(temp.exists() == true) {
                   temp = File.createTempFile("jni",SECLIBRARY,new File(System.getProperty("java.io.tmpdir")));
                } else {
                   temp = File.createTempFile("jni", UUID.randomUUID().toString(), new File(System.getProperty("java.io.tmpdir")));
                }
                temp.deleteOnExit();
                FileOutputStream fo = new FileOutputStream(temp);
                byte[] buffer = new byte[65536];
                while ((bytesRead = in.read(buffer)) > 0) {
                    fo.write(buffer, 0, bytesRead);
                }
                fo.close();
                in.close();
                System.load(temp.getCanonicalPath());
                if(temp != null && init(temp.getCanonicalPath()))
                {
                    LOGGER.fine(LOCATION + ": After loading JNI: " + temp.getCanonicalPath() + " loaded ok");
                }
                else
                {
                    init(SECLIBRARY);
                }
            } else {
                SECLIBRARY = "sapjwt";
                LOGGER.info(LOCATION + ": java.library.path: " + System.getProperty("java.library.path"));
                System.loadLibrary(SECLIBRARY);
                LOGGER.info(LOCATION + ": After loading JNI: " + SECLIBRARY);
            }
        } catch (Throwable t) {
            // $JL-EXC$
            if (temp != null) {
                if(temp.delete())
                    LOGGER.severe(LOCATION + ": JNI file deleted");
            }
            LOGGER.severe(LOCATION + ": Error loading JNI: " + t.getMessage());
            LOGGER.severe(LOCATION + ": Could not load dynamic library " + SECLIBRARY);
        }
        LOGGER.info(LOCATION + ": static part ends.");
    }

    /**
     * Initialization
     * 
     * @param seclib location of SSF-implementation
     * 
     * @return true/false whether initialization was OK
     */
    private static native synchronized boolean init(String seclib);

    /**
     * Returns internal version.
     * 
     * @return version
     */
    public static native synchronized String getVersion();
    
    /**
     * Returns internal long version.
     * 
     * @return version
     */
    public static native synchronized String getStringVersion();

    /**
     * Class constructor
     */
    public JWT()
    {
        initSSO();
    }

    private static void initSSO()
    {
        if(initialized == false) {
           init(null);
        } else {
           return;
        }
        if(version == null) {
           version = getVersion();
        }
        if(build_version == null) {
           build_version = getStringVersion();
           LOGGER.info(LOCATION + ": SSO version      : " + version);
           LOGGER.info(LOCATION + ": Build information: " + build_version);
        }
        initialized = true;
    }

    /**
     * Evaluate a JSON Web Token (JWT) 
     * Use key as verification key.
     * 
     * @param jwt JWT token to be validated
     * @return String with JWT payload
     *
     * @throws Exception Text message with error.
     */
    public String checkRsaJwToken( String jwt, String rsa_key ) throws Exception {
        Object o[] =evalJWTokenEx(jwt, rsa_key, JWT_ALG_RS256);
        if(o.length > 0 && o[0] != null) {
            payload = (String)o[0];
        }
        if(o.length > 1 && o[1] != null) {
            jwt_alg = Integer.parseInt((String)o[1]);
            switch(jwt_alg) {
                case JWT_ALG_RS256:
                case JWT_ALG_RS384:
                case JWT_ALG_RS512:
                break;// ok
                default:
                    payload = null;
                    throw new Exception("Invalid hash algorithm");
            }
        }
        return payload;
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
    public String checkJwToken( String jwt, String rsa_key ) throws Exception {
        Object o[] =evalJWTokenEx(jwt, rsa_key, JWT_ALG_HS256);
        if(o.length > 0 && o[0] != null) {
            payload = (String)o[0];
        }
        if(o.length > 1 && o[1] != null) {
            jwt_alg = Integer.parseInt((String)o[1]);
        }
        return payload;
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
        Object o[] =evalJWHeader(jwt);
        if(o.length > 0 && o[0] != null) {
            header = (String)o[0];
        }
        if(o.length > 1 && o[1] != null) {
            jwt_alg = Integer.parseInt((String)o[1]);
        }
        if(jwt_alg == JWT_ALG_UNDEFINED) {
            payload = null;
            throw new Exception("No hash algorithm defined");
        }
        return header;
    }

    /**
     * Returns internal version.
     * 
     * @return version
     */
    public String getSapJwtVersion() {
        return version;
    }

    /**
     * Returns build version.
     * 
     * @return version
     */
    public String getBuilidVersion() {
        return build_version;
    }

    /**
     * Parse certificate
     * @param cert             Certificate received from evalLogonTicket
     * @param info_id       One of the request ids
     * 
     * @return Info string from certificate
     *  
     */
    public static native synchronized String parseCertificate(
        byte[] cert,
        int info_id);
        
    /**
     * Get SAPSSOEXT property
     * @param name   property name to be retrieved
     * 
     * @return property string from SAPSSOEXT
     *  
     */
    public static native synchronized String getProperty( String name );
    
    /**
     * Set SAPSSOEXT property
     * @param name   property name to be set
     * @param value  property value to be set
     * 
     * @return true/false whether set was OK
     *  
     */
    public static native synchronized boolean setProperty( String name, String value );


    /**
     * Evaluate a JSON Web Token (JWT) 
     * Use key verification key.
     * 
     * @param jwt JWT token to be validated
     * @param key Verification key
     * @param alg Minimum Algorithm
     * @return Object array with:
     *         [0] = (String)JWT payload, [1] = (String)algorithm id
     * 
     * @throws Exception Text message with error.
     */
    public static native synchronized Object[] evalJWTokenEx( String jwt, String key , int alg)
                                               throws Exception;


    /**
     * Parse the header of  a JSON Web Token (JWT) 
     * 
     * @param jwt JWT token to be validated
     * @return Object array with:
     *         [0] = (String)JWT payload, [1] = (String)algorithm id
     * 
     * @throws Exception Text message with error.
     */
    public static native synchronized Object[] evalJWHeader(String jwt)
                                               throws Exception;

    /**
     * Returns hash algorithm
     * 
     * @return alg_id
     */
    public int getHashAlg() {
        return jwt_alg;
    }
}
