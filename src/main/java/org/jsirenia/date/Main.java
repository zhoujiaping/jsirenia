package org.jsirenia.date;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Date;

import org.junit.Test;
/**
 * java8 日期api主要涉及的类和概念
 * 日期的概念，包含年月日，不包含时分秒。
 * 时间的概念，包含时分秒[毫秒微妙纳秒]，不包含年月日。
 * 日期时间的概念，就是把上面两个包含的都包含了。
 * LocalDateTime 本地日期时间
 * LocalDate 本地日期
 * LocalTime 本地时间
 * ZonedDateTime 带时区的日期时间
 * OffsetDateTime 带时区偏移的日期时间
 * OffsetTime 带时区偏移的时间
 * ZoneId 时区
 * ZoneOffset 时区偏移
 * Instant 瞬间
 * ChronoUnit 时间单位（如秒、分、时、半年、周等）
 * ChronoField 时间字段（如MILLI_OF_SECOND表示除去秒之后剩余的毫秒数）
 * TemporalAdjuster 日期时间调整器（比如用于将日期调整到上周一，上个月第一天等）
 * TemporalAdjusters 用于生成常用的日期时间调整期（题外话：一般带s结尾的类是其不带s结尾的类的工具类，比如Arrays,Collections,Collectors）
 * DateTimeFormatter 日期/时间/日期时间 和 字符串之间转换的格式
 * Period 持续日期
 * Duration 持续时间
 * Clock 时钟
 * 
 * 总之，java8的date api，相对以前的Date和Calendar、SimpleDateFormatter体系，更精确了，同时概念也更多了，
 * 要正确使用，不经过系统总结是很容易踩坑的。
 */
public class Main {
	public void printf(String template,Object... args){
		System.out.printf(template+"\r\n", args);
	}
	/**
	 * 获取日期时间
	 * */
	@Test
	public void testLocalDateTimeNow(){
		//通过 LocalDateTime的静态工厂方法
		//获取当前日期时间
		LocalDateTime localDateTime = LocalDateTime.now();
		printf("testLocalDateTimeNow=%s",localDateTime);
		//结果
		//testLocalDateTimeNow=2018-07-21T09:57:31.868
		//获取指定日期时间
		localDateTime = LocalDateTime.of(2018, 12, 12, 10, 05);
		printf("testLocalDateTimeNow=%s",localDateTime);
	}
	/**
	 * 日期时间格式化为字符串
	 * */
	@Test
	public void testLocalDateTimeFormatter(){
		//通过 LocalDateTime的静态工厂方法
		LocalDateTime localDateTime = LocalDateTime.now();
		String str = localDateTime.toString();
		printf("testLocalDateTimeFormatter=%s",str);
		//结果
		//testLocalDateTimeNow=2018-07-21T09:57:31.868
		//默认格式化为yyyy-MM-ddTHH:mm:ss.SSS格式
		//接下来我们格式化为指定格式
		String pattern = "SSS.ss:mm:HH dd-MM-yyyy";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		str = localDateTime.format(formatter);
		printf("str=%s",str);
		//结果 str=457.30:04:10 21-07-2018
		//ps：坑 pattern中不是可以包含任意字符的。比如
		pattern = "yyyy-MM-ddTHH:mm:ss.SSS";
		formatter = DateTimeFormatter.ofPattern(pattern);
		str = localDateTime.format(formatter);
		printf("str=%s",str);
		//结果 java.lang.IllegalArgumentException: Unknown pattern letter: T
		//我指定的格式不能和你默认的格式一样？
		//同样 pattern = "yyyy-MM-ddbHH:mm:ss.SSS"; 也是不行的。但是pattern = "yyyy-MM-dd啥HH:mm:ss.SSS";却是可以的
		//而 pattern = "yyyy-MM-ddaHH:mm:ss.SSS"; 会格式化为2018-07-21上午10:10:16.856
		//建议 格式尽量不要包含其他字母。
	}
	/**
	 * 字符串解析为日期时间
	 * */
	@Test
	public void testLocalDateTimeParse(){
		//如果是默认格式，则不需要指定格式
		String text = "2018-01-01T01:15:01.123123123";
		LocalDateTime localDateTime = LocalDateTime.parse(text);
		printf("localDateTime=%s",localDateTime);
		//结果 localDateTime=2018-01-01T01:15:01.123123123
		//注意：最多精确到纳秒，也就是秒后面最多9位，多了会抛异常
		//指定格式
		text = "2018年01月01日01时15分01秒123456微秒";
		String pattern = "yyyy年MM月dd日HH时mm分ss秒SSSSSS微秒";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern );//格式通过静态工厂方法生成
		localDateTime = LocalDateTime.parse(text,formatter );
		printf("localDateTime=%s",localDateTime);
		//注意：pattern 有一些规则限制。如果其中只包含年月日，是不能格式化为LocalDateTime的。
		text = "2018年01月01日";
		pattern = "yyyy年MM月dd日";
		formatter = DateTimeFormatter.ofPattern(pattern );//格式通过静态工厂方法生成
		localDateTime = LocalDateTime.parse(text,formatter );
		printf("localDateTime=%s",localDateTime);
		/*结果：异常  但是
		text = "2018年01月01日01时";
		pattern = "yyyy年MM月dd日HH时";
		却是可以的
		*/
		//建议：严格区分日期、时间、日期时间的概念。
	}
	/**
	 * 日期时间常用api
	 * */
	@Test
	public void testLocalDateTimeApi(){
		LocalDateTime localDateTime = LocalDateTime.now();
		//获取 自1970-01-01 00:00:00的秒数
		ZoneOffset offset = ZoneOffset.systemDefault().getRules().getOffset(localDateTime);
		//这种代码，怎么知道的，我是看源码知道的。蛋疼不？
		long second = localDateTime.toEpochSecond(offset);
		printf("second=%s",second);
		//有没有一种脱裤子放屁的感觉？先从LocalDateTime获取时区偏移,再传给它的toEpochSecond方法。
		//当然也可以用下面的方法
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = localDateTime.atZone(zone);
		second = zonedDateTime.toEpochSecond();
		printf("second=%s",second);
		//还可以用下面的方法
		Instant instant = localDateTime.toInstant(offset);
		second = instant.getEpochSecond();
		printf("second=%s",second);
		//总之，就是先要通过时区或者时区偏移，确定时区，然后再获取秒数.
		//如果要获取毫秒数，或者纳秒数，那就要先转成Instant
		//转成瞬间
		offset = ZoneOffset.systemDefault().getRules().getOffset(localDateTime);
		instant = localDateTime.toInstant(offset);
		printf("instant=%s",instant);
		//转成瞬间
		instant = localDateTime.atZone(zone).toInstant();
		printf("instant=%s",instant);
		//转成日期
		LocalDate localDate = localDateTime.toLocalDate();
		printf("localDate=%s",localDate);
		//转成时间
		LocalTime localTime = localDateTime.toLocalTime();
		printf("localTime=%s",localTime);
		//转成带时区的日期时间
		zone = ZoneId.systemDefault();
		zonedDateTime = localDateTime.atZone(zone);
		//转成带时区偏移的日期时间
		 offset = ZoneOffset.systemDefault().getRules().getOffset(localDateTime);
		OffsetDateTime offsetDateTime = localDateTime.atOffset(offset);
		printf("offsetDateTime=%s",offsetDateTime);
		//加3天
		TemporalAmount amountToAdd = Period.ofDays(3);
		localDateTime = localDateTime.plus(amountToAdd);
		//加3天
		localDateTime = localDateTime.plusDays(3);//可以为负数，就是减
		//其他api，包括 比较、加减某个字段上的值、获取某个字段上的值、设置某个字段上的值，就不一一介绍了。
	}
	@Test
	public void test0() {
		// 获取当前日期
		LocalDate nowdate = LocalDate.now();
		System.out.println(nowdate.toString());
		// 日期格式化为字符串，如果使用默认格式yyyy-MM-ddTHH:mm:ssZ，直接toString即可
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String nowdatestr = nowdate.format(formatter);
		System.out.println(nowdatestr);
		// 字符串解析为日期，如果使用默认格式，不需要穿formatter
		LocalDate date = LocalDate.parse(nowdatestr, formatter);
		System.out.println(date);
		// 日期运算
		TemporalAmount amountToAdd = Period.ofDays(3);
		date = date.plus(amountToAdd);
		System.out.println(date);

		date = date.plusDays(-3);
		date = date.plus(3, ChronoUnit.DAYS);

		LocalDateTime dt = LocalDateTime.now();
		System.out.println(dt);
		dt = LocalDateTime.parse("2018-07-21T15:41:03");
		dt = LocalDateTime.parse("2018-07-21T15:41:03.1");
		dt = LocalDateTime.parse("2018-07-21T15:41:03.12");
		dt = LocalDateTime.parse("2018-07-21T15:41:03.123");
		dt = LocalDateTime.parse("2018-07-21T15:41:03.123400035");// 不能比纳秒还精确，否则异常
		System.out.println(dt);
		long milli = Instant.now().toEpochMilli();// 得到相对于1970-01-01T00:00:00的UTC时间
		System.out.println(milli);
		System.out.println("==================");
		System.out.println(dt);
		ZonedDateTime zdt = dt.atZone(ZoneId.of("UTC+08:00"));
		System.out.println(zdt);
		OffsetDateTime odt = dt.atOffset(ZoneOffset.of("+08:00"));
		System.out.println(odt);
		date = odt.toLocalDate();
		System.out.println(dt);
		zdt.toLocalDate();
		ZoneOffset offset = ZoneOffset.ofHours(8);
		date.atStartOfDay().atOffset(offset);
		Clock clock = Clock.systemDefaultZone();
		long millis = clock.millis();
		long millis2 = System.currentTimeMillis();
		printf("millis=%s,millis2=%s", millis, millis2);
		TemporalField field = ChronoField.NANO_OF_SECOND;
		Instant clockInstant = clock.instant();
		TemporalUnit unit = ChronoUnit.HOURS;
		// printf("nextTick=%s,nextTick2=%s",Clock.tickMinutes(ZoneId.systemDefault()).instant(),clockInstant.plusNanos(clockInstant.getLong(field)));
		printf("nextTick=%s,nextTick2=%s", Clock.tick(clock, Duration.of(1, unit)).instant(),
				clockInstant.plusNanos(clockInstant.getLong(field)));
		ValueRange range = LocalDate.now().range(ChronoField.DAY_OF_MONTH);
		printf("range=%s", range);
		// 获取当天所属星期的第一天，即获取当周的星期一。
		// 调整字段值
		dt = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		printf("MONDAY=%s", dt.toLocalDate());
		// 调整字段值
		dt = dt.with(ChronoField.HOUR_OF_DAY, 0);
		printf("HOUR_OF_DAY=%s", dt);
		// 比较是否相等
		dt.equals(dt);
		// 部分字段组合
		MonthDay md = MonthDay.now();
		printf("md=%s", md);
		// 获取下周的今天
		dt.plus(1, ChronoUnit.WEEKS);
		// 获取某个时区的日期时间
		ZoneId america = ZoneId.of("America/New_York");
		LocalDateTime localtDateAndTime = LocalDateTime.now();
		ZonedDateTime dateAndTimeInNewYork = ZonedDateTime.of(localtDateAndTime, america);
		System.out.println("Current date and time in a particular timezone : " + dateAndTimeInNewYork);
		// 某个月有多少天
		YearMonth currentYearMonth = YearMonth.now();
		System.out.printf("Days in month year %s: %d%n", currentYearMonth, currentYearMonth.lengthOfMonth());
		YearMonth creditCardExpiry = YearMonth.of(2018, Month.FEBRUARY);
		System.out.printf("Your credit card expires on %s %n", creditCardExpiry);
		//日期之间月数差
		LocalDate today = LocalDate.now();
		LocalDate java8Release = LocalDate.of(2014, Month.MARCH, 14);
		Period periodToNextJavaRelease = Period.between(java8Release,today);
		System.out.println("Months left between today and Java 8 release : " + periodToNextJavaRelease.toTotalMonths());
		System.out.println("Months left between today and Java 8 release : " + periodToNextJavaRelease.getMonths());
		//天数差,秒差,毫秒差
		printf("today.toEpochDay() - java8Release.toEpochDay() = %s",today.toEpochDay() - java8Release.toEpochDay());
		printf("today.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond() = %s",today.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond());
		printf("Instant.now().toEpochMilli() = %s",Instant.now().toEpochMilli());
		//互转 https://www.cnblogs.com/exmyth/p/6425878.html
		Duration duration = Duration.between(LocalTime.now().plusHours(-5), LocalTime.now());
		printf("duration.toDays()=%s",duration.toHours());
		/*踩坑记：
		 * 有些api必须按某些方式调用，如果不按这些方式使用，有时候它也能让你编译通过，但是就是运行报错。
		 * 可能你读代码觉得这样很合理，但是运行时就是异常。
		 * 所以，你需要总结这些固定的用法。
		 * */
	}

	@Test
	public void testLocalDate() {
		TemporalAccessor temporal = Instant.now().atZone(ZoneId.systemDefault());
		LocalDate localDate = LocalDate.from(temporal);
		System.out.println(localDate);
	}

	@Test
	public void testLocalDateTime() {
		ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneId.systemDefault());
		OffsetDateTime odt = zdt.toOffsetDateTime();
		LocalDateTime dt = odt.toLocalDateTime();
		printf("dt=%s",dt);
	}

	@Test
	public void testInstant() {
		//获取Instant 以及转为ZonedDateTime
		Instant.now().atZone(ZoneId.systemDefault());
		Instant i = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
		System.out.println(i);
		i = LocalDateTime.now().toInstant(ZoneOffset.UTC);
		LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
		System.out.println(i);
		Date date = new Date();
		date.toInstant();
		date.getTime();
		Instant.ofEpochMilli(i.toEpochMilli());
		Instant instant =LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Date.from(instant);
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime.ofInstant(instant, zone );
	}

}