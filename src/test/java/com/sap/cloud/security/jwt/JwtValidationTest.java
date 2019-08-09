package com.sap.cloud.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;

class JwtValidationTest {

	static JwtValidation jwt;
	private static volatile String Token = null;
	private static volatile String PublicKey = null;
	private static volatile String PublicKeyWrong = null;
	private static final String cert = "MIIHyDCCBrCgAwIBAgIQCgZI07+PfgKmPvnpnRPZ1jANBgkqhkiG9w0BAQsFADBEMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMR4wHAYDVQQDExVEaWdpQ2VydCBHbG9iYWwgQ0EgRzIwHhcNMTkwMTIzMDAwMDAwWhcNMjEwMTIzMTIwMDAwWjCBhDELMAkGA1UEBhMCREUxETAPBgNVBAcTCFdhbGxkb3JmMQ8wDQYDVQQKEwZTQVAgU0UxHDAaBgNVBAsTE1NBUCBUcnVzdCBDb21tdW5pdHkxMzAxBgNVBAMTKmJhcmtsZXktaXQtcnQuY2ZhcHBzLnNhcC5oYW5hLm9uZGVtYW5kLmNvbTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAOfmeKgwjqv6eJxLG2GyGQTjW+8M7CsfShIqLXQngL5+wEO1qlhtSrzF6U/9YxvYovyXhNge2mfKwIasvwQbF6AXeLGHjb0Asj702OxGb5MU+8LXnX2aoOdj9SBTigCZpUyCHzQ0d4fOMAwcd/VG49XRa60v8s69bylsyCR3L3Bty8/NLV6wRZ7NTgLbvmX8I1uIFyB9spFbF7XmKzqe3ZB801V1dPb/51jH0YFevHYLN9e8ylNpoIfOyY7XAe7dN8HZOb2nMpWvNq71CV8As3X/hywsRoc//K0r7tEbZszSw1BEXtb/PY9cDb1rOTwKr+ISa67E5Kd00+KGvDsxTIZuwXXZQ2F+qo6t8AcuMKHY+vC9PeKe9xnwqrgmIXivj3FO1vTHbkDQbc890/uCoubxSbZSdK1jdfrjylVXU7ZyESXgcOAC/6RJwQjn8VoNyJK5XDp1A7mLo1zbl62+Yvq86RbwHDw7xEFu1yjWOhBlLJn0lvMEi/NnAchBhyUT23Tlx+/aZoU8jy12YJmtJEeNXpy74FUEwda1RzjuynA5BiTDzAtmX3IIebWgTzsPHPVjvGOXzFvscQJzHM7OOkR7w2q14iVZrY/biZw5lQo4nVYTS12f6Dqzri31KDWTUMSzZWxsGNn2c5aYyB8uMMiTg4py2DbNw7eiwZyoPPOfAgMBAAGjggNzMIIDbzAfBgNVHSMEGDAWgBQkbist0GqSUVElaQGqmkemiedAIDAdBgNVHQ4EFgQUeJ2WR8AtOh6b0pqL0xldjmu/YbEwNQYDVR0RBC4wLIIqYmFya2xleS1pdC1ydC5jZmFwcHMuc2FwLmhhbmEub25kZW1hbmQuY29tMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwdwYDVR0fBHAwbjA1oDOgMYYvaHR0cDovL2NybDMuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0R2xvYmFsQ0FHMi5jcmwwNaAzoDGGL2h0dHA6Ly9jcmw0LmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydEdsb2JhbENBRzIuY3JsMEwGA1UdIARFMEMwNwYJYIZIAYb9bAEBMCowKAYIKwYBBQUHAgEWHGh0dHBzOi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwCAYGZ4EMAQICMHQGCCsGAQUFBwEBBGgwZjAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuZGlnaWNlcnQuY29tMD4GCCsGAQUFBzAChjJodHRwOi8vY2FjZXJ0cy5kaWdpY2VydC5jb20vRGlnaUNlcnRHbG9iYWxDQUcyLmNydDAJBgNVHRMEAjAAMIIBfQYKKwYBBAHWeQIEAgSCAW0EggFpAWcAdgCkuQmQtBhYFIe7E6LMZ3AKPDWYBPkb37jjd80OyA3cEAAAAWh62ql/AAAEAwBHMEUCIQDTETr+SXnS3yQTm4SY1+OiR92Pnp6ML7a8Qa0BeV8rwAIgM3NCRLxAlIcagOiK+9lYLx4uiNg6blJ9bv2G2F2rvYEAdQCHdb/nWXz4jEOZX73zbv9WjUdWNv9KtWDBtOr/XqCDDwAAAWh62qpaAAAEAwBGMEQCIAwUmHK4Z/LHSvjMfvxwtFPFNZj4RChxARl/25fOvwTuAiAoOxTQJl53cU8/yfwA3RWc7bahyaRZm1NcAQEHWDTJvwB2AG9Tdqwx8DEZ2JkApFEV/3cVHBHZAsEAKQaNsgiaN9kTAAABaHraquEAAAQDAEcwRQIhAIzksM/UBHzG6M+4sIoN1y+UOPARFx7XmV/dvmb4OhpLAiAVvMyutle0XIyyN2q3A3B8hPXgs9T/yYR/Z2cak6tKNzANBgkqhkiG9w0BAQsFAAOCAQEAujUb5uKit3y+jYyKth35mz81llBESlBnYyINIOcGMDIYm0QHmPnKX/xezz2tKifeE4A4NFm0nnNsVSoh8AeDzKf9rRB8nDq9LtztTtZ7NYA//RSA9p0nsw4xH/lx5iAcNUWIMb0G4d4dQV+TIgNlDLYZ1twZAgnFkRnaRijX3EmInRAH83eb0WxHUq6WVx3TuOz2ZTas0NNT1qCIQvROjjiYVQmMyzvWmUxVZAzPiUmpdfKAnB0B55CtaJLNLQsTeWVANGjikMVlXi++u/NzK7FDsHdqdFikDv5ZLf7+98OOFnI3qlao4f/Bg37wMdMCAq8Tn7kSFDmVnrXe6v8hcQ==";


	private static String readData(String path) throws IOException {
		InputStream is = null;
		try {
			is = JwtValidationTest.class.getResourceAsStream(path);
			return IOUtils.toString(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		jwt = new JwtValidation();
		String privateKey = readData("/privateKey.txt"); // PEM format
		PublicKey = readData("/publicKey.txt");
		PublicKeyWrong = readData("/publicKeyWrong.txt");
		RsaSigner signer = new RsaSigner(privateKey);
		Jwt jwt = JwtHelper.encode(readData("/claims.txt"), signer);
		Token = jwt.getEncoded();
	}

	@Test
	void testVersionAvailable() {
		assertNotNull(jwt.getBuilidVersion());
	}

	@Test
	void testgetSapJwtVersion() {
		assertNotNull(jwt.getSapJwtVersion());
	}

	@Test
	void testVersionJwtValid() throws Exception {
		assertNotNull(jwt.checkRsaJwToken(Token, PublicKey));
	}

	@Test
	void testVersionJwtValidHmac() throws Exception {
		assertNotNull(jwt.checkJwToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XbPfbIHMI6arZ3Y922BhjWgQzWXcXNrz0ogtVhfEd2o", "secret"));
	}

	@Test
	void testVersionJwtInValid() throws Exception {
		Assertions.assertThrows(Exception.class, () ->  jwt.checkRsaJwToken(Token, PublicKeyWrong));
	}

	@Test
	void testVersionJwtInValidHash() throws Exception {
		Assertions.assertThrows(Exception.class, () ->  jwt.checkRsaJwToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c", PublicKey));
	}

	@Test
	void testVersionJwtInValidNoHash() throws Exception {
		Assertions.assertThrows(Exception.class, () ->  jwt.checkRsaJwToken("eyJ0eXAiOiJKV1QifQ.cjsUZD3_owdeqnUPM-AEtW5kw6LE_cMlJ5HLpDcGhluwXhppsRPKokwJZWqR44cEu6R8gGc6k76ZdivI-8WZxw", PublicKeyWrong));
	}

	@Test
	void testHeaderInValidNoHash() throws Exception {
		Assertions.assertThrows(Exception.class, () ->  jwt.parseJwtHeader("eyJ0eXAiOiJKV1QifQ.cjsUZD3_owdeqnUPM-AEtW5kw6LE_cMlJ5HLpDcGhluwXhppsRPKokwJZWqR44cEu6R8gGc6k76ZdivI-8WZxw"));
	}

	@Test
	void testVersionJwtInValidHmac() throws Exception {
		Assertions.assertThrows(Exception.class, () ->  jwt.checkRsaJwToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XbPfbIHMI6arZ3Y922BhjWgQzWXcXNrz0ogtVhfEd2o", "secret"));
	}

	@Test
	void testParseCert() throws Exception {
		assertNotNull(jwt.parseCertificate(cert.getBytes("utf-8"), 0));
	}

	@Test
	void testProperties() throws Exception {
		jwt.setProperty("SAP_EXT_TRC", null);
		jwt.getProperty("SAP_EXT_TRC");
	}

	@Test
	void testParseHeader() throws Exception {
		assertNotNull(jwt.parseJwtHeader(Token));
	}

	@Test
	void testHeaderAlg() throws Exception {
		jwt.parseJwtHeader(Token);
		assertEquals(JwtValidation.JWT_ALG_RS256, jwt.getHashAlg());
	}
}
