package net.zomis.spring.games.generic;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TokenGenerator {

    private static final int TOKEN_LENGTH = 20;
    private final SecureRandom random = new SecureRandom();
    private final Set<String> usedTokens = new HashSet<>();

    public String generateToken() {
        byte[] token = new byte[TOKEN_LENGTH];
        String hex;
        do {
            random.nextBytes(token);
            UUID uuid = UUID.nameUUIDFromBytes(token);
            hex = uuid.toString(); //toHex(token);
        } while (!usedTokens.add(hex));
        return hex;
    }

    private String toHex(byte[] token) {
        StringBuilder str = new StringBuilder(token.length * 2);
        for (int i = 0; i < token.length; i++) {
            int value = Byte.toUnsignedInt(token[i]);
            String hex = Integer.toHexString(value);
            str.append(hex);
        }
        return str.toString();
    }

}
