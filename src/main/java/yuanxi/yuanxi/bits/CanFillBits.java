package yuanxi.yuanxi.bits;

/**
 * 可以按位写入读取数据的接口
 */
public interface CanFillBits {
    /**
     * 向其中填充bit
     * 比如对于一个字节的二进制数item = 0x 0000 1010,当bitCount=6时，就是向数组中填充6位，这6位是0b001010
     *
     * @param element  需要填写的元素
     * @param bitCount 需要填写几位
     * @return 填充成功返回true, 否则返回false
     */
    boolean fill(byte element, int bitCount);

    /**
     * 将binaryString填写到数组中
     */
    boolean fill(String binaryString);

    /**
     * 将16进制字符串的小端count位填写进入
     * 比如"1",4,填写的是0001这4位
     */
    boolean fill(String hexString, int count);

    /**
     * 从数组中读取bitCount个bit
     *
     * @param bitCount 需要读取到bit数量
     * @return 读取出来的数，高位补0
     * @throws Exception 读取错误异常
     */
    byte get(int bitCount) throws Exception;

    /**
     * 获取底层数组的一个拷贝(底层数组不应该被暴露出去)
     *
     * @return
     */
    byte[] getCopyOfRowData();
}
