package yuanxi.yuanxi.counter;

import yuanxi.yuanxi.exceptions.WrongNextValueException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 根据日期和数值来计数的计数器
 * 2022-06-13计数器值为202206130017,如果下一次调用next发生在第二天(2022-06-14)，获取到的值是202206140001。
 */
public class DateAndNumCounter implements NextValueAble{
    private final static DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    private final static String strFormatter = "%s%04d";
    private final static int MAX_VALUE = 10000;

    private final int num;
    private final LocalDate date;

    public DateAndNumCounter(int num, LocalDate date) {
        this.num = num;
        this.date = date;
    }

    /**
     * 下一个值
     * @return 下一个值，一个全新的对象
     * @throws WrongNextValueException 计数器溢出
     */
    @Override
    public NextValueAble nextValue() throws WrongNextValueException {
        int num;
        LocalDate today = LocalDate.now();
        if (today.compareTo(this.date) > 0) {
            num = 0;
        } else {
            num = this.num;
            today = this.date;
        }
        ++num;
        if (num >= MAX_VALUE) {
            throw new WrongNextValueException("Counter overflow!");
        }
        return new DateAndNumCounter(num, today);
    }

    /**
     * 获取到字符串形式为日期+4位整数
     * 比如，2022-06-22第3次调用next,则获取到的值为202206220003
     *
     * @return 日期+4位整数
     */
    @Override
    public String toCode() {
        String dateStr = formatter.format(date);
        return String.format(strFormatter, dateStr, num);
    }

    public static DateAndNumCounter parseCode(String code) {
        LocalDate date = LocalDate.parse(code.substring(0, 8), formatter);
        String numStr = code.substring(8);
        int num = 0;
        try {
            num = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
        }
        return new DateAndNumCounter(num, date);
    }
}
