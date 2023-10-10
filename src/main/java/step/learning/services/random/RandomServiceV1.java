package step.learning.services.random;

import java.util.Random;

public class RandomServiceV1 implements RandomService{
    private String iv;
    private final Random random;

    public RandomServiceV1() {
        random = new Random();

    }

    @Override
    public String randomHex (int charLenght) {
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        if (charLenght < 1) {
            throw new RuntimeException("positive");
        }
        char[] res = new char[charLenght];
        for (int i = 0; i< charLenght;i++){
            res[i] =  hexChars[random.nextInt(hexChars.length)];
        }
        return new String(res);
    }

    @Override
    public void seed (String iv) {
       this.iv = iv;
        random.setSeed(iv == null ? 0 : iv.length());
    }
}
