package yuanxi.yuanxi.counter;

import yuanxi.yuanxi.exceptions.WrongNextValueException;

/**
 * 计数器接口
 */
public interface NextValueAble {
    /**
     * 应该返回计数器的下一个值。
     * 该接口并不保证获取到的下一个值是否为原来对象(地址与this是否相同)
     * 但是对于有的实现类来说，这一点非常重要，应该予以说明
     *
     * @return 下一个值
     */
    NextValueAble nextValue() throws WrongNextValueException;


    /**
     * 将当前对象重置为初始
     *
     * @return 对象本身
     */
    default NextValueAble reset() {
        return this;
    }

    /**
     * 对象的String表示
     *
     * @return 以String来表示对象的值
     */
    default String toCode() {
        return toString();
    }
}
