package yuanxi.yuanxi.test.bits;

import org.junit.Test;
import yuanxi.yuanxi.bits.SerialBits;
import java.util.Random;

public class SerialBitsTest {
    //向数组中添加或者获取0个bit
    @Test
    public void test1() {
        SerialBits serialBits = new SerialBits(20);
        serialBits.fill((byte) 0x09, 0);
        serialBits.fill((byte) 0xF1, 0);
        serialBits.fill((byte) 0x12, 0);
        try {
            byte b = serialBits.get(0);
            System.out.println(b);
        } catch (Exception e) {
        }
        System.out.println(serialBits);
    }

    @Test
    public void test2() {
        SerialBits serialBits = new SerialBits(20);
        serialBits.fill((byte) 0x09, 1);//0b 0000 1001
        serialBits.fill((byte) 0xF1, 2);//0b 1111 0001
        serialBits.fill((byte) 0x12, 3);//0b 0001 0010
        //1010 1000 0000
        try {
            byte b = serialBits.get(0);
            System.out.println(b);
            b = serialBits.get(1);// 0b 0000 0001->1
            System.out.println(b);
            b = serialBits.get(3);// 0b 0000 0010->2
            System.out.println(b);
        } catch (Exception e) {
        }
        System.out.println(serialBits);
    }

    @Test
    public void test3() {
        SerialBits serialBits = new SerialBits(50);
        serialBits.fill((byte) 0xF1, 8);//0b 1111 0001
        serialBits.fill((byte) 0xF1, 8);//0b 1111 0001
        serialBits.fill((byte) 0x12, 8);//0b 0001 0010
        //1111 0001 1111 0001 0001 0010
        try {
            byte b = serialBits.get(0);
            System.out.println(b);
            b = serialBits.get(4);//0000 1111
            System.out.println(b);
            b = serialBits.get(8);//0001 1111
            System.out.println(b);
        } catch (Exception e) {
        }
        System.out.println(serialBits);
    }

    private static char[] getCharacter = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static String getRandomCode(int len, int bound) {
        StringBuilder sb = new StringBuilder(len);
        Random random = new Random();
        for (int i = 0; i < len; ++i) {
            int code = random.nextInt(bound);
            char c = getCharacter[code];
            sb.append(c);
        }
        return sb.toString();
    }

    @Test
    public void test4() {
        int totalCount = 1000;
        int bound = 62;//如果使用7位用62,使用6位用36
        int bitCount = 7;
        int len = 13;
        int errorCount = 0;
        for (int i = 0; i < totalCount; ++i) {
            String text = getRandomCode(len, bound);
            int arrayLen = len * bitCount / 8;
            if ((len * bitCount % 8) != 0) {
                arrayLen += 1;
            }
            SerialBits serialBits = new SerialBits(arrayLen * 8);
            for (char c : text.toCharArray()) {
                serialBits.fill((byte) c, bitCount);
            }
            StringBuilder sb = new StringBuilder(len);
            for (int a = 0; a < len; ++a) {
                try {
                    char code = (char) serialBits.get(bitCount);
                    sb.append(code);
                } catch (Exception e) {
                }
            }
            if (!text.equals(sb.toString())) {
                errorCount++;
                System.out.println("错误数据：" + sb.toString());
            }
        }
        System.out.println("总计错误量为：" + errorCount);
    }

    @Test
    public void test5() {
        int totalCount = 1000;
        int bound = 36;//如果使用7位用62,使用6位用36
        int bitCount = 6;
        int len = 12;
        int errorCount = 0;
        for (int i = 0; i < totalCount; ++i) {
            String text = getRandomCode(len, bound);
            int arrayLen = len * bitCount / 8;
            if ((len * bitCount % 8) != 0) {
                arrayLen += 1;
            }
            SerialBits serialBits = new SerialBits(arrayLen * 8);
            for (char c : text.toCharArray()) {
                serialBits.fill((byte) c, bitCount);
            }
            StringBuilder sb = new StringBuilder(len);
            for (int a = 0; a < len; ++a) {
                try {
                    char code = (char) serialBits.get(bitCount);
                    if ((code & 0x20) == 0x00) {
                        code |= 0x40;
                    }
                    sb.append(code);
                } catch (Exception e) {
                }
            }
            if (!text.equals(sb.toString())) {
                errorCount++;
                System.out.println("错误数据：" + sb.toString());
            }
        }
        System.out.println("总计错误量为：" + errorCount);
    }

    @Test
    public void test6() {
        SerialBits serialBits = new SerialBits(50);
        serialBits.fill("11101");
        serialBits.fill("000");
        serialBits.fill("00001111");
        try {
            byte b1 = serialBits.get(8);
            byte b2 = serialBits.get(8);
            System.out.println(b1);
            System.out.println(b2);
        } catch (Exception e) {
        }
    }

    @Test
    public void test7() {
        SerialBits serialBits = new SerialBits(50);
        serialBits.fill("01", 4);
        serialBits.fill("01", 4);
        try {
            byte b1 = serialBits.get(8);
            System.out.println(b1);
        } catch (Exception e) {
        }

    }
}