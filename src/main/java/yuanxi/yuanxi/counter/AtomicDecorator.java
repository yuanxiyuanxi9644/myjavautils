package yuanxi.yuanxi.counter;

import yuanxi.yuanxi.exceptions.WrongNextValueException;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子装饰器，装饰在计数器上，保证计数器的nextValue(返回新对象)方法的线程安全
 */
public class AtomicDecorator implements NextValueAble{

    private AtomicReference<NextValueAble> counter;
    private final int retryTime;

    public AtomicDecorator(NextValueAble counter, int retryTime) {
        this.counter = new AtomicReference<>(counter);
        this.retryTime = retryTime;
    }

    /**
     * 返回下一个值，全新的对象。请注意，该方法返回的对象类型并不是AtomicCounter
     * 返回的是被包装的对象的nextValue产生的对象
     */
    @Override
    public NextValueAble nextValue() throws WrongNextValueException {
        return nextValue(this.retryTime);
    }

    /**
     * 尝试retryCount次，如果仍旧不能正确获取下一个数值，抛出一个异常
     * @param retryCount 尝试的次数
     * @return 下一个值
     * @throws WrongNextValueException 超时，无法获取到下一个值
     */
    public NextValueAble nextValue(int retryCount) throws WrongNextValueException {
        //这里使用自旋锁来保证线程安全
        for (int i = 0; i < retryCount; ++i) {
            NextValueAble nowValue = counter.get();
            NextValueAble nextValue = nowValue.nextValue();
            if (counter.compareAndSet(nowValue, nextValue)) {
                return nextValue;
            }
        }
        throw new WrongNextValueException("Have retried " + retryCount + " times.");
    }

    @Override
    public NextValueAble reset() {
        return counter.get().reset();
    }

    @Override
    public String toCode() {
        return counter.get().toCode();
    }
}
