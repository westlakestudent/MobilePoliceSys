package com.zjedu.mobilepolicesys;

/**
 * 
 * @author westlakeboy
 *
 */
public class SystemConfig {

	/**
	 * 所有用到的全局变量
	 */
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EVENT_PULLED = "pulled";
	public static final String EVENTID = "eventid";
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String ACCEPTED = "accepted";
	public static final String WHICH = "which";
	public static final String LISTENER = "listener";
	public static final String HELPRECV = "helprecv";
	public static final String HELP = "help";
	public static final String NOTIFICATIONID = "notificatioid";
	
	/**
	 * Handler 传值
	 */
	public static final int FIRST_IMG = 0;
	public static final int SECOND_IMG = 1;
	public static final int POI = 2;
	public static final int NEWS = 3;
	public static final int UPDATE = 4;
	public static final int ALREADY_NEW = 5;
	public final static int MSG_WEATHER_INFO_READY = 6;
	public final static int MSG_TEMP_INFO_READY = 7;
	public final static int WEATHER_UPDATE = 8;
	public final static int MSG_ARRIVE = 9;
	
	/**
	 * 客户端用到的所有要访问服务器的URL
	 */
	public static final String URL = "http://192.168.137.78:8080/MobilePoiceSysServer/actions/";
	public static final String URL_LOGIN = URL + "login_login.do";
	public static final String URL_REGISTER = URL + "register.do";
	public static final String URL_GET_EVENT = URL + "event_getallevents.do";
	public static final String URL_UPDATE_REV = URL + "event_updateReceive.do"; 
	public static final String URL_RELEASE = URL + "msg_release.do";
	public static final String URL_GETMSG = URL + "msg_getmsgs.do";
	public static final String URL_FORHELP = URL + "help_forhelp.do";
	public static final String URL_HELPMSG = URL + "help_queryhelp.do";
	public static final String URL_HELPRECV = URL + "help_saveHelpReceive.do";
	public static final String URL_EVENT_RELEASE = URL + "event_release.do";
}
