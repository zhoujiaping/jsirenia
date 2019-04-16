package org.jsirenia.date;

import java.io.Serializable;
import java.time.DayOfWeek;
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
import java.util.HashMap;
import java.util.Map;
/**
 * java日期api用起来不够简洁，而且有些用法比较难记。
 * 所以做一些封装，应付常用场景。
 * 这个类有点“上帝类“的感觉，但是真的方便啊。比起java自带的难记的api，这个真的方便啊。
 * @author Administrator
 *
 */
public class DateTime implements  Comparable<DateTime>, Serializable {
	private static final long serialVersionUID = 1L;
	private static final ZoneId zone = ZoneId.systemDefault();
	private LocalDateTime dt;
	private static final Map<String,DateTimeFormatter> formatters = new HashMap<>();
	static{
		formatters.put("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		formatters.put("yyyy-MM-dd", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		formatters.put("HH:mm:ss", DateTimeFormatter.ofPattern("HH:mm:ss"));
		formatters.put("yyyyMMdd", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		formatters.put("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	//第一部分：构造
	public static DateTime now(){
		return fromLocalDateTime(LocalDateTime.now());
	}
	public static DateTime yesterday(){
		return now().plusDays(-1);
	}
	public static DateTime tomorrow(){
		return now().plusDays(1);
	}
	public static DateTime fromLocalDateTime(LocalDateTime dt){
		DateTime d = new DateTime();
		d.dt = dt;
		return d;
	}
	public static DateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second,int millisecond,int nanoOfSecond){
		LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, millisecond, nanoOfSecond);
		return fromLocalDateTime(dt);
	}
	public static DateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second,int millisecond){
		return of(year,month,dayOfMonth,hour,minute,second,millisecond,0);
	}
	public static DateTime of(int year,int month,int dayOfMonth,int hour,int minute,int second){
		return of(year,month,dayOfMonth,hour,minute,second,0,0);
	}
	public static DateTime of(int year,int month,int dayOfMonth,int hour,int minute){
		return of(year,month,dayOfMonth,hour,minute,0,0,0);
	}
	public static DateTime of(int year,int month,int dayOfMonth,int hour){
		return of(year,month,dayOfMonth,hour,0,0,0,0);
	}
	public static DateTime of(int year,int month,int dayOfMonth){
		return of(year,month,dayOfMonth,0,0,0,0,0);
	}
	public static DateTime of(LocalDate localDate,LocalTime localTime){
		return fromLocalDateTime(localTime.atDate(localDate));
	}
	public static DateTime ofInstant(Instant instant){
		return fromLocalDateTime(LocalDateTime.ofInstant(instant, zone));
	}
	public static DateTime ofEpochSecond(long epochSecond){
		return ofInstant(Instant.ofEpochSecond(epochSecond));
	}
	public static DateTime ofEpochMilli(long epochMilli) {
		return ofInstant(Instant.ofEpochMilli(epochMilli));
	}
	public static DateTime ofEpochSecond(long epochSecond, int nanoOfSecond){
		return ofInstant(Instant.ofEpochSecond(epochSecond,nanoOfSecond));
	}
	//第二部分：解析
	public static DateTime parseDateTime(String text){
		return fromLocalDateTime(LocalDateTime.parse(text));
	}
	public static DateTime parseDateTime(String text,String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
		return fromLocalDateTime(LocalDateTime.parse(text,format));
	}
	public static DateTime parseDate(String text){
		return fromLocalDateTime(LocalDate.parse(text).atStartOfDay());
	}
	public static DateTime parseDate(String text,String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
		return fromLocalDateTime(LocalDate.parse(text,format).atStartOfDay());
	}
	public static DateTime parseTime(String text){
		return fromLocalDateTime(LocalDate.now().atTime(LocalTime.parse(text)));
	}
	public static DateTime parseTime(String text,String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
		return fromLocalDateTime(LocalDate.now().atTime(LocalTime.parse(text,format)));
	}
	//第三部分：格式化
	public String formatDate(){
		return dt.toLocalDate().toString();
	}
	public String formatDate(String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
		return dt.toLocalDate().format(format);
	}
	public String formatDateTime(){
		return dt.toString();
	}
	public String formatDateTime(String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
		return dt.format(format);
	}
	public String formatTime(){
		return dt.toLocalTime().toString();
	}
	public String formatTime(String pattern){
		DateTimeFormatter format = formatters.get(pattern);
		if(format==null){
			DateTimeFormatter.ofPattern(pattern);
		}
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
		return dt.toInstant(ZoneOffset.UTC);
	}
	public Date toDate(){
		return Date.from(dt.toInstant(ZoneOffset.UTC));
	}
	public LocalDateTime toLocalDateTime(){
		return dt;
	}
	//第五部分：计算
	public DateTime plusDays(int days){
		return fromLocalDateTime(dt.plusDays(days));
	}
	public DateTime plusYears(int years){
		return fromLocalDateTime(dt.plusYears(years));
	}
	public DateTime plusWeeks(int weeks){
		return fromLocalDateTime(dt.plusWeeks(weeks));
	}
	public DateTime plusMonths(int months){
		return fromLocalDateTime(dt.plusMonths(months));
	}
	public DateTime plusHours(int hours){
		return fromLocalDateTime(dt.plusHours(hours));
	}
	public DateTime plusMinutes(int minutes){
		return fromLocalDateTime(dt.plusMinutes(minutes));
	}
	public DateTime plusSeconds(int seconds){
		return fromLocalDateTime(dt.plusSeconds(seconds));
	}
	public DateTime plusNanos(int nanos){
		return fromLocalDateTime(dt.plusNanos(nanos));
	}
	//第六部分：调整值
	public DateTime previousOrSameMonday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
	}
	public DateTime previousOrSameTuesday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY)));
	}
	public DateTime previousOrSameWendesday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY)));
	}
	public DateTime previousOrSameThursday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY)));
	}
	public DateTime previousOrSameFriday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY)));
	}
	public DateTime previousOrSameSaturday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)));
	}
	public DateTime previousOrSameSunday(){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
	}
	public DateTime previousOrSameWeekDay(DayOfWeek dayOfWeek){
		return fromLocalDateTime(dt.with(TemporalAdjusters.previousOrSame(dayOfWeek)));
	}
	public DateTime withYear(int year){
		return fromLocalDateTime(dt.withYear(year));
	}
	public DateTime withMonth(int month){
		return fromLocalDateTime(dt.withMonth(month));
	}
	public DateTime withDayOfMonth(int dayOfMonth){
		return fromLocalDateTime(dt.withDayOfMonth(dayOfMonth));
	}
	public DateTime withDayOfYear(int dayOfYear){
		return fromLocalDateTime(dt.withDayOfYear(dayOfYear));
	}
	public DateTime withHour(int hour){
		return fromLocalDateTime(dt.withHour(hour));
	}
	public DateTime withMinute(int minute){
		return fromLocalDateTime(dt.withMinute(minute));
	}
	public DateTime withSecond(int second){
		return fromLocalDateTime(dt.withSecond(second));
	}
	public DateTime withNanoOfSecond(int nanoOfSecond){
		return fromLocalDateTime(dt.withNano(nanoOfSecond));
	}
	//第七部分：时间段
	public long daysBetween(DateTime date){
		return dt.toLocalDate().toEpochDay()-date.dt.toLocalDate().toEpochDay();
	}
	public long secondsBetween(DateTime date){
		return dt.toEpochSecond(ZoneOffset.UTC)-date.dt.toEpochSecond(ZoneOffset.UTC);
	}
	//第八部分：比较
	public boolean isBefore(DateTime date){
		return true;
	}
	public boolean isAfter(DateTime date){
		return true;
	}
	public boolean isEqual(DateTime date){
		return true;
	}
	public int compareTo(DateTime date) {
		return 0;
	}
	//第九部分：提取值
	public long toEpochSecond(){
		return 0;
	}
	public long toEpochMilli(){
		return 0;
	}
	public long toNano(){
		return 0;
	}
	//第十部分：
}
