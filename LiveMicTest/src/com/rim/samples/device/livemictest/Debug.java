package com.rim.samples.device.livemictest;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.ui.UiApplication;

public class Debug {
    public static long loggerEventId = 0x98f217b7dbfd6ae4L;
    private static LiveMicScreen liveMicScreen;
    private String base;

    public Debug(String string, int verbose) {
        base = string;
        EventLogger.register(loggerEventId, "LIVEMICDEMO",
                EventLogger.VIEWER_STRING);
        EventLogger.setMinimumLevel(EventLogger.DEBUG_INFO);
        logToEvents("Start Debug: " + string);
    }

    public void trace(String string) {
        System.out.println("### TRAC " + base + " : " + string);

        //logToScreen(base + " DEBUG " + string);
        logToEvents(base + " DEBUG " + string);
    }

    public void info(String string) {
        System.out.println("### INFO " + base + " : " + string);

        //logToScreen(base + " INFO " + string);
        logToEvents(base + " INFO " + string);
    }

    public void warn(String string) {
        System.out.println("### WARN " + base + " : " + string);

        //logToScreen(base + " WARN " + string);
        logToEvents(base + " WARN " + string);
    }

    private void logToScreen(final String string) {

        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                if (liveMicScreen != null) {
                    //liveMicScreen = (LiveMicScreen) RuntimeStore
                    //        .getRuntimeStore().get(LiveMicScreen.getGuid());

                    //synchronized (Application.getEventLock()) {
                    //System.out.println("log to screen");

                    liveMicScreen.addText(string);
                }

            }
        });

    }

    private void logToEvents(final String logMessage) {
        //#ifdef EVENTLOGGER
        //EventLogger.register(loggerEventId, "BBB", EventLogger.VIEWER_STRING);

        if (!EventLogger.logEvent(loggerEventId, logMessage.getBytes(),
                EventLogger.DEBUG_INFO)) {

            System.out.println("cannot write to EventLogger");
        }
        //#endif
    }

    public static void setScreen(LiveMicScreen _liveMicScreen) {
        liveMicScreen = _liveMicScreen;
    }

}
