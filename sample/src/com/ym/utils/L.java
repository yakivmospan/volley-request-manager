package com.ym.utils;

import android.util.Log;

/**
 * This class is used to send Logs messages. The main feature of this class is TAG (calling class name and method name) - it's added automatically. Also it wraps default log methods such as Log.e, Log.i and log.d.
 * 
 * @see android.util.Log
 *
 * @author Yakiv Mospan, Dmytro Danylyk
 * @author yakiv.mospan@gmail.com
 * */
public class L
{
	private static enum State
	{
		ALL, NONE, ERROR, INFO, DEBUG;
	}

	private static State state = State.ALL;

	/**
	 * Call an {@link android.util.Log.ERROR} method.
	 *
	 * @see android.util.Log.e(String tag, String msg)
	 */

	public static void e(final String theTag, final String theMsg)
	{
		switch (state)
        {
		case ALL:
		case ERROR:
			Log.e(theTag, theMsg);
			break;
		}
	}

	/**
	 * Call an {@link android.util.Log.INFO} method.
	 *
	 * @see android.util.Log.i(String tag, String msg)
	 */

	public static void i(final String theTag, final String theMsg)
	{
		switch (state)
        {
		case ALL:
		case INFO:
			Log.i(theTag, theMsg);
			break;
		}
	}

	/**
	 * Send a {@link android.util.Log.DEBUG} log message.
	 *
	 * @see android.util.Log.d(String tag, String msg)
	 */

	public static void d(final String theTag, final String theMsg)
	{
		switch (state)
        {
		case ALL:
		case DEBUG:
			Log.d(theTag, theMsg);
			break;
		}
	}

    /**
     * Send an {@link android.util.Log.ERROR} log message.
     *
     * @param theMsg The message you would like logged.
     * @see android.util.Log.e(String tag, String msg)
     */

    public static void e(final String theMsg)
    {
		switch (state)
        {
		case ALL:
		case ERROR:
			final Info info = getMethodInfo();
		    Log.e(info.getCallerClassName(), "[" + info.getCallerMethodName() + "] " + theMsg);
			break;
		}
    }

    /**
     * Send an {@link android.util.Log.INFO} log message.
     *
     * @param theMsg The message you would like logged.
     * @see android.util.Log.i(String tag, String msg)
     */

    public static void i(final String theMsg)
    {
		switch (state)
        {
		case ALL:
		case INFO:
	        final Info info = getMethodInfo();
	        Log.i(info.getCallerClassName(), "[" + info.getCallerMethodName() + "] " + theMsg);
			break;
		}
    }

    /**
     * Send a {@link android.util.Log.DEBUG} log message.
     * 
     * @param theMsg The message you would like logged.
     * @see android.util.Log.d(String tag, String msg)
     */
    
    public static void d(final String theMsg)
    {
		switch (state)
        {
		case ALL:
		case DEBUG:	
	        final Info info = getMethodInfo();
	        Log.d(info.getCallerClassName(), "[" + info.getCallerMethodName() + "] " + theMsg);
			break;
		}
    }

    /**
     * Returns caller method and class names.
     */
    private static Info getMethodInfo()
    {
        final Info info = new Info();

        final Throwable t = new Throwable();
        final StackTraceElement[] elements = t.getStackTrace();

        final String callerClassName = elements[2].getClassName();
        final String callerMethodName = elements[2].getMethodName();

        info.setCallerClassName(callerClassName);
        info.setCallerMethodName(callerMethodName);

        return info;
    }

    private static class Info
    {
        private String callerClassName;
        private String callerMethodName;

        public String getCallerClassName()
        {
            return callerClassName;
        }

        public void setCallerClassName(String callerClassName)
        {
            this.callerClassName = callerClassName;
        }

        public String getCallerMethodName()
        {
            return callerMethodName;
        }

        public void setCallerMethodName(String callerMethodName)
        {
            this.callerMethodName = callerMethodName;
        }
    }

}
