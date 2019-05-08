package com.turkcell.blockmail.util;

import java.util.Calendar;

public class CalendarUtil {
	
	public static long endTimeMilisInWorkTime (long endTimeMilis) {
		Calendar endTimeCalendar = Calendar.getInstance();
		endTimeCalendar.setTimeInMillis(endTimeMilis);
		int weekDay = endTimeCalendar.get(Calendar.DAY_OF_WEEK);
		if(weekDay>= Calendar.MONDAY && weekDay <= Calendar.FRIDAY) {
			if((endTimeCalendar.get(Calendar.HOUR_OF_DAY) >= 0 && endTimeCalendar.get(Calendar.HOUR_OF_DAY) < 7) 
					|| (endTimeCalendar.get(Calendar.HOUR_OF_DAY) == 7 && endTimeCalendar.get(Calendar.MINUTE) < 30) ) {
				endTimeCalendar.add(Calendar.DAY_OF_YEAR, -1);
				endTimeCalendar.set(Calendar.HOUR_OF_DAY, 16);
				endTimeCalendar.set(Calendar.MINUTE, 30);
			} else if((endTimeCalendar.get(Calendar.HOUR_OF_DAY) > 16 && endTimeCalendar.get(Calendar.HOUR_OF_DAY) <= 23) 
					|| (endTimeCalendar.get(Calendar.HOUR_OF_DAY) == 16 && endTimeCalendar.get(Calendar.MINUTE) >= 30)) {
				endTimeCalendar.set(Calendar.HOUR_OF_DAY, 16);
				endTimeCalendar.set(Calendar.MINUTE, 30);
			}
		} else if(weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
			if(weekDay == Calendar.SUNDAY) {
				endTimeCalendar.add(Calendar.DAY_OF_YEAR, -2);
			} else if(weekDay == Calendar.SATURDAY) {
				endTimeCalendar.add(Calendar.DAY_OF_YEAR, -1);
			}
			endTimeCalendar.set(Calendar.HOUR_OF_DAY, 16);
			endTimeCalendar.set(Calendar.MINUTE, 30);
		}
		
		return endTimeCalendar.getTimeInMillis();
	}

}
