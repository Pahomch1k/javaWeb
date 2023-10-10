package step.learning.services.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5HashService implements HashService{
    @Override
    public String hash(String input) {
        try {
            MessageDigest diegest = MessageDigest.getInstance("Md5");
            StringBuilder sb = new StringBuilder();
            for(int b : diegest.digest(input.getBytes(StandardCharsets.UTF_8))){
                sb.append(String.format("%02x", b & 0xFF));
            }
            return  sb.toString();
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
