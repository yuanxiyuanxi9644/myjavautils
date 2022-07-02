package yuanxi.yuanxi.bits;

import org.apache.commons.codec.binary.Hex;

/**
 * 该类用来向向一个数组填写连续的比特(而非字节)
 *
 * @author wangruici
 */
public class SerialBits implements CanFillBits{
    private byte[] bitArray;
    private int index;//当前指针指向的地址
    private int offset;//当前指针指向的位
    private final int len;//需要存储的位数
    private int readIndex;//读指针指向的地址
    private int readOffset;//读指针指向的位

    /**
     * 需要填写多少个bit，注意单位是bit
     *
     * @param len 分配多少bit长度的空间
     */
    public SerialBits(int len) {
        int arrayLen = len / 8;
        if (len % 8 != 0) {
            ++arrayLen;
        }
        this.len = len;
        bitArray = new byte[arrayLen];
        this.index = 0;
        this.offset = 0;
        this.readIndex = 0;
        this.readOffset = 0;
    }

    /**
     * 这个是为了对原有的数组进行取数设置的构造函数
     *
     * @param data 需要进行取数的数组
     */
    public SerialBits(byte[] data) {
        bitArray = new byte[data.length];
        this.len = data.length * 8;
        System.arraycopy(data, 0, bitArray, 0, data.length);
        this.index = data.length - 1;
        this.offset = 8;
        this.readIndex = 0;
        this.readOffset = 0;
    }

    /**
     * 复制一份底层数据
     *
     * @return 返回底层的数组的复制
     */
    @Override
    public byte[] getCopyOfRowData() {
        byte[] result = new byte[bitArray.length];
        System.arraycopy(this.bitArray, 0, result, 0, bitArray.length);
        return result;
    }

    /**
     * 后缀掩码，用来从一个字节中提取指定长度的bit
     */
    private static int suffixMask[] = {
            0x00, 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F, 0xFF
    };

    /**
     * 判断是否可以将指定长度的bit写入(预先分配的底层数组是否足够)。
     *
     * @param bitCount 想要写入的长度
     * @return 是否可以写入
     */
    public boolean canFillItem(int bitCount) {
        if (bitCount > 8) {
            return false;
        }
        if ((howManyBits() + bitCount) > len) {
            return false;
        }
        return true;
    }

    /**
     * 还可以存储多少bit
     *
     * @return 还可以存储多少bit
     */
    public int howManyBitsToWrite() {
        return len - howManyBits();
    }

    /**
     * 已经存储了多少bit
     *
     * @return 已经存储了多少bit
     */
    public int howManyBits() {
        return index * 8 + offset;
    }

    /**
     * 向其中填充bit
     * 比如对于一个字节的二进制数item = 0x 0000 1010,当bitCount=6时，就是向数组中填充6位，这6位是0b001010
     *
     * @param item     需要填写的元素
     * @param bitCount 需要填写几位
     * @return 填充成功返回true, 否则返回false
     */
    @Override
    public boolean fill(byte item, int bitCount) {
        //检查是否可以填充，空间是否充裕到足够填充bitCount数量的bit
        if (!canFillItem(bitCount)) {
            return false;
        }
        item = (byte) (item & suffixMask[bitCount]);
        int d = 8 - offset;//该字节还能填充的bit的数量
        if (d >= bitCount) {
            //如果当前字节足够填充
            bitArray[index] |= (((item << (8 - bitCount)) & 0xFF) >>> offset);
            offset = offset + bitCount;
        } else {
            //如果当前字节不够填充，就需要分别填写到两个字节
            /*
             * 为什么闲着没事就要&0xFF呢？
             * java没有unsigned型的整数，而计算机对于负数使用补码，所以负数右移时左侧最高位会补1
             * 而在大部分情况下，这种右侧操作都希望左侧最高位是0
             */
            bitArray[index] |= ((item & 0xFF) >>> (bitCount - d) << (8 - d - offset)) & 0xFF;
            bitArray[++index] |= (item << (d + 8 - bitCount)) & 0xFF;
            offset = bitCount - d;
        }
        return true;
    }

    @Override
    public boolean fill(String binaryString) {
        if (null == binaryString || binaryString.isEmpty()) {
            return false;
        }
        if (!canFillItem(binaryString.length())) {
            return false;
        }
        byte item = (byte) (int) Integer.valueOf(binaryString, 2);
        return fill(item, binaryString.length());
    }

    @Override
    public boolean fill(String hexString, int count) {
        if (null == hexString || hexString.length() < 1 || hexString.length() > 2) {
            return false;
        }
        if (!canFillItem(count)) {
            return false;
        }
        byte item = (byte) (int) Integer.valueOf(hexString, 16);
        return fill(item, count);
    }

    /**
     * 前缀掩码
     */
    private static int[] prefixMask = {
            0x00, 0x80, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC, 0xFE, 0xFF
    };

    /**
     * 重置读指针，可以从头再读一遍数据
     */
    public void reset() {
        this.readIndex = 0;
        this.readOffset = 0;
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        //由于需要使用&,|等操作，必须保证数组是全为0的干净状态
        int arrayLen = len / 8;
        if (len % 8 != 0) {
            ++arrayLen;
        }
        bitArray = new byte[arrayLen];
        this.readIndex = 0;
        this.readOffset = 0;
        this.index = 0;
        this.offset = 0;
    }

    /**
     * 已经读取了多少位
     *
     * @return 已经读取了多少位
     */
    private int howManyBitsHasRead() {
        return readIndex * 8 + readOffset;
    }

    /**
     * 还能读取多少bit
     *
     * @return 还能读取多少位
     */
    public int howManyBitsToRead() {
        return howManyBits() - howManyBitsHasRead();
    }

    /**
     * 从数组中读取bitCount个bit
     *
     * @param bitCount 需要读取到bit数量
     * @return 读取出来的数，高位补0
     * @throws Exception 读取错误异常
     */
    @Override
    public byte get(int bitCount) throws Exception {
        if (bitCount > 8) {
            throw new Exception("一次不能读取超过8位!");
        }
        if (bitCount > howManyBitsToRead()) {
            throw new Exception("没有足够的bit读取!");
        }
        byte code = 0;
        int d = 8 - readOffset;
        if (d >= bitCount) {
            //可以从一个字节中读取
            code = (byte) (((bitArray[readIndex] << readOffset) & prefixMask[bitCount]) >>> (8 - bitCount));
            readOffset = readOffset + bitCount;
        } else {
            //从两个字节中提取
            code = (byte) (((bitArray[readIndex] << readOffset) & 0xFF) >>> (8 - bitCount));
            code += (byte) ((bitArray[++readIndex] & 0xFF) >>> (8 - bitCount + d));
            readOffset = bitCount - d;
        }
        return code;
    }

    @Override
    public String toString() {
        return "SerialBits{" +
                "bitArray=" + Hex.encodeHexString(bitArray) +
                ", index=" + index +
                ", offset=" + offset +
                ", readIndex=" + readIndex +
                ", readOffset=" + readOffset +
                '}';
    }
}
