package yuanxi.yuanxi.test.counter;

import org.junit.Assert;
import org.junit.Test;
import yuanxi.yuanxi.counter.AtomicDecorator;
import yuanxi.yuanxi.counter.DateAndNumCounter;
import yuanxi.yuanxi.counter.NextValueAble;
import yuanxi.yuanxi.exceptions.WrongNextValueException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

public class AtomicDecoratorAndDateAndNumCounterTest {

    //测试计数器能否正常获取到当天的第一个值
    @Test
    public void test1() throws WrongNextValueException {
        LocalDate date = LocalDate.now().minusDays(5);
        NextValueAble counter = new DateAndNumCounter(22, date);
        NextValueAble decorator = new AtomicDecorator(counter, 5);
        NextValueAble todayFirst = new DateAndNumCounter(0, LocalDate.now()).nextValue();
        NextValueAble nextValue = decorator.nextValue();
        Assert.assertEquals(todayFirst.toCode(), nextValue.toCode());
    }

    //测试计数器能否正确从字符串中解析出值
    @Test
    public void test2() throws WrongNextValueException {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        String todayStr = formatter.format(today);
        String code1 = todayStr + "005";
        DateAndNumCounter counter1 = DateAndNumCounter.parseCode(code1);
        Assert.assertEquals(todayStr + "0006", counter1.nextValue().toCode());
        String code2 = todayStr + "1233";
        DateAndNumCounter counter2 = DateAndNumCounter.parseCode(code2);
        Assert.assertEquals(todayStr + "1234", counter2.nextValue().toCode());
    }

    //测试原子计数器是否线程安全
    @Test
    public void test3() throws InterruptedException {
        NextValueAble counter = new DateAndNumCounter(0, LocalDate.now());
        NextValueAble ac = new AtomicDecorator(counter, 5);

        int threadCount = 6000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> statisticsSet = new CopyOnWriteArraySet<>();
        Thread[] ts = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            ts[i] = new Thread(() -> {
                try {
                    NextValueAble nowValue = ac.nextValue();
                    String code = nowValue.toCode();
                    statisticsSet.add(code);
                } catch (Exception e) {
                    System.out.println("========超时或者越界========");
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        for (int i = 0; i < threadCount; ++i) {
            ts[i].start();
        }
        latch.await();
        System.out.println("应该生成:" + threadCount + "个不同的数值");
        System.out.println("总计生成了:" + statisticsSet.size() + "个不同的数值");
        Assert.assertEquals(threadCount, statisticsSet.size());
    }
}