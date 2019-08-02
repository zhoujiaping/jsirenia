package org.jsirenia.date;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * java日期api用起来不够简洁，而且有些用法比较难记。
 * 所以做一些封装，应付常用场景。
 * 这个类有点“上帝类“的感觉，但是真的方便啊，比起java自带的难记的api。
 * @author Administrator
 *
 */
public class MyDateTime implements  Comparable<MyDateTime>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final ZoneId zone = ZoneId.systemDefault();
	private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
	private LocalDateTime dt;
	private static final Map<String,DateTimeFormatter> formatters = new ConcurrentHashMap<>();
	//第一部分：构造
	public static MyDateTime now(){
		return fromLocalDateTime(LocalDateTime.now());
	}
	public static MyDateTime yesterday(){
		return now().plusDays(-1);
	}
	public static MyDateTime tomorrow(){
		return now().plusDays(1);
	}
	public static MyDateTime fromLocalDateTime(LocalDateTime dt){
		MyDateTime d = new MyDateTime();
		d.dt = dt;
		return d;
	}
	public static MyDateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second,int millisecond,int nanoOfSecond){
		LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, millisecond, nanoOfSecond);
		return fromLocalDateTime(dt);
	}
	public static MyDateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second,int millisecond){
		return of(year,month,dayOfMonth,hour,minute,second,millisecond,0);
	}
	public static MyDateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second){
		return of(year,month,dayOfMonth,hour,minute,second,0,0);
	}
	public static MyDateTime of(int year,int month,int dayOfMonth,int hour,int minute){
		return of(year,month,dayOfMonth,hour,minute,0,0,0);
	}
	public static MyDateTime of(int year,int month,int dayOfMonth,int hour){
		return of(year,month,dayOfMonth,hour,0,0,0,0);
	}
	public static MyDateTime of(int year,int month,int dayOfMonth){
		return of(year,month,dayOfMonth,0,0,0,0,0);
	}
	public static MyDateTime of(LocalDate localDate,LocalTime localTime){
		return fromLocalDateTime(localTime.atDate(localDate));
	}
	public static MyDateTime ofInstant(Instant instant){
		return fromLocalDateTime(LocalDateTime.ofInstant(instant, zone));
	}
	public static MyDateTime ofEpochSecond(long epochSecond){
		return ofInstant(Instant.ofEpochSecond(epochSecond));
	}
	public static MyDateTime ofEpochMilli(long epochMilli) {
		return ofInstant(Instant.ofEpochMilli(epochMilli));
	}
	public static MyDateTime ofEpochSecond(long epochSecond, int nanoOfSecond){
		return ofInstant(Instant.ofEpochSecond(epochSecond,nanoOfSecond));
	}
	//第二部分：解析
	public static MyDateTime parseDateTime(String text){
		return fromLocalDateTime(LocalDateTime.parse(text));
	}
	public static MyDateTime parseDateTime(String text,String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return fromLocalDateTime(LocalDateTime.parse(text,format));
	}
	public static MyDateTime parseDate(String text){
		return fromLocalDateTime(LocalDate.parse(text).atStartOfDay());
	}
	public static MyDateTime parseDate(String text,String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return fromLocalDateTime(LocalDate.parse(text,format).atStartOfDay());
	}
	public static MyDateTime parseTime(String text){
		return fromLocalDateTime(LocalDate.now().atTime(LocalTime.parse(text)));
	}
	public static MyDateTime parseTime(String text,String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return fromLocalDateTime(LocalDate.now().atTime(LocalTime.parse(text,format)));
	}
	//第三部分：格式化
	public String formatDate(){
		return dt.toLocalDate().toString();
	}
	public String formatDate(String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return dt.toLocalDate().format(format);
	}
	public String formatDateTime(){
		return dt.toString();
	}
	public String formatDateTime(String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return dt.format(format);
	}
	public String formatTime(){
		return dt.toLocalTime().toString();
	}
	public String formatTime(String pattern){
		DateTimeFormatter format = findFormatter(pattern);
		return dt.toLocalTime().format(format);
	}
	//第四部分：转换成java日期
	public LocalDate toLocalDate(){
		return dt.toLocalDate();
	}
	public LocalTime toLocalTime(){
		return dt.toLocalTime();
	}
	public Instant toInstant(){
		return dt.toInstant(zoneOffset);
	}
	public Date toDate(){
		return Date.from(dt.toInstant(zoneOffset));
	}
	public LocalDateTime toLocalDateTime(){
		return dt;
	}
	//第五部分：计算
	public MyDateTime plusDays(int days){
		return fromLocalDateTime(dt.plusDays(days));
	}
	public MyDateTime plusYears(int years){
		return fromLocalDateTime(dt.plusYears(years));
	}
	public MyDateTime plusWeeks(int weeks){
		return fromLocalDateTime(dt.plusWeeks(weeks));
	}
	public MyDateTime plusMonths(int months){
		return fromLocalDateTime(dt.plusMonths(months));
	}
	public MyDateTime plusHours(int hours){
		return fromLocalDateTime(dt.plusHours(hours));
	}
	public MyDateTime plusMinutes(int minutes){
		return fromLocalDateTime(dt.plusMinutes(minutes));
	}
	public MyDateTime plusSeconds(int seconds){
		return fromLocalDateTime(dt.plusSeconds(seconds));
	}
	public MyDateTime plusNanos(int nanos){
		return fromLocalDateTime(dt.plusNanos(nanos));
	}
	//第六部分：调整值
	public MyDateTime previousOrSameMonday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
	}
	public MyDateTime previousOrSameTuesday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY)));
	}
	public MyDateTime previousOrSameWendesday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY)));
	}
	public MyDateTime previousOrSameThursday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY)));
	}
	public MyDateTime previousOrSameFriday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY)));
	}
	public MyDateTime previousOrSameSaturday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)));
	}
	public MyDateTime previousOrSameSunday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
	}
	public MyDateTime previousOrSameWeekDay(DayOfWeek dayOfWeek){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(dayOfWeek)));
	}
	public MyDateTime withYear(int year){
		return fromLocalDateTime(dt.withYear(year));
	}
	public MyDateTime withMonth(int month){
		return fromLocalDateTime(dt.withMonth(month));
	}
	public MyDateTime withDayOfMonth(int dayOfMonth){
		return fromLocalDateTime(dt.withDayOfMonth(dayOfMonth));
	}
	public MyDateTime withDayOfYear(int dayOfYear){
		return fromLocalDateTime(dt.withDayOfYear(dayOfYear));
	}
	public MyDateTime withHour(int hour){
		return fromLocalDateTime(dt.withHour(hour));
	}
	public MyDateTime withMinute(int minute){
		return fromLocalDateTime(dt.withMinute(minute));
	}
	public MyDateTime withSecond(int second){
		return fromLocalDateTime(dt.withSecond(second));
	}
	public MyDateTime withNanoOfSecond(int nanoOfSecond){
		return fromLocalDateTime(dt.withNano(nanoOfSecond));
	}
	//第七部分：时间段
	public long monthsBetween(MyDateTime date){
		return Period.between(date.toLocalDate(), dt.toLocalDate()).getMonths();
	}
	public long minusAsDays(MyDateTime date){
		return dt.toLocalDate().toEpochDay()-date.dt.toLocalDate().toEpochDay();
	}
	public long minusAsSeconds(MyDateTime date){
		return Duration.between(date.toInstant(), dt).getSeconds();
		//or return dt.toEpochSecond(zoneOffset)-date.dt.toEpochSecond(zoneOffset);
	}
	//第八部分：比较
	public boolean isBefore(MyDateTime date){
		return dt.isBefore(date.dt);
	}
	public boolean isAfter(MyDateTime date){
		return dt.isAfter(date.dt);
	}
	public boolean isEqual(MyDateTime date){
		return dt.isEqual(date.dt);
	}
	public int compareTo(MyDateTime date) {
		return dt.compareTo(date.dt);
	}
	//第九部分：提取值
	public long toEpochSecond(){
		return dt.toEpochSecond(zoneOffset);
	}
	public long toEpochMilli(){
		return dt.toInstant(zoneOffset).toEpochMilli();
	}
	public long getNano(){
		return dt.toInstant(zoneOffset).getNano();
	}
	//第十部分：
	
	private static DateTimeFormatter findFormatter(String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			format = DateTimeFormatter.ofPattern(pattern);
			formatters.put(pattern, format);
		}
		return format;
	}
}
