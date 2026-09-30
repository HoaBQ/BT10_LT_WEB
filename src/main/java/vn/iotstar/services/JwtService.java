package vn.iotstar.services;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = parseToken(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT format", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + expiration);

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(expirationDate);

            if (extraClaims != null) {
                extraClaims.forEach(builder::claim);
            }

            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            JWSSigner signer = new MACSigner(getSecretBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error creating JWT token using Nimbus", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            SignedJWT signedJWT = parseToken(token);
            JWSVerifier verifier = new MACVerifier(getSecretBytes());

            boolean isSignatureValid = signedJWT.verify(verifier);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean isExpired = expirationTime != null && expirationTime.before(new Date());
            String username = signedJWT.getJWTClaimsSet().getSubject();

            return isSignatureValid && !isExpired && username != null && username.equals(userDetails.getUsername());
        } catch (Exception e) {
            return false;
        }
    }

    public SignedJWT parseToken(String token) throws ParseException {
        return SignedJWT.parse(token);
    }

    private byte[] getSecretBytes() {
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            return padded;
        }
        return bytes;
    }
}
