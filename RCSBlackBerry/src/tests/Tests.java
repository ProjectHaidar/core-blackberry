//#preprocess
/* *************************************************
 * Copyright (c) 2010 - 2010
 * HT srl,   All rights reserved.
 * Project      : RCS, RCSBlackBerry
 * Package      : tests
 * File         : Tests.java
 * Created      : 28-apr-2010
 * *************************************************/
package tests;

import java.util.Vector;

import tests.unit.UT_Crypto;
import tests.unit.UT_File;
import tests.unit.UT_LogCollector;
import tests.unit.UT_Markup;
import tests.unit.UT_Path;
import tests.unit.UT_Recorder;
import tests.unit.UT_Self;
import tests.unit.UT_Utils;
import blackberry.config.Conf;
import blackberry.debug.Debug;
import blackberry.debug.DebugLevel;

/**
 * The Class Tests.
 */
public final class Tests {
    //#ifdef DEBUG
    static Debug debug = new Debug("Tests", DebugLevel.VERBOSE);
    //#endif

    private static Tests instance = null;

    /**
     * Gets the single instance of Tests.
     * 
     * @return single instance of Tests
     */
    public static synchronized Tests getInstance() {
        if (instance == null) {
            instance = new Tests();
        }

        return instance;
    }

    protected Vector testUnits = new Vector();

    private Tests() {

        Debug.init();

        Conf.SD_ENABLED = true;

        addTest(new UT_Self("Self", this));
        addTest(new UT_Markup("Markup", this));
        
        addTest(new UT_Utils("Utils", this));
        addTest(new UT_Crypto("Crypto", this));

        addTest(new UT_File("File", this));
       

        addTest(new UT_Path("Path", this));

        addTest(new UT_LogCollector("LogCollector", this));

        addTest(new UT_Recorder("Recorder", this));

    }

    private void addTest(final TestUnit unitTest) {
        testUnits.addElement(unitTest);
    }

    /**
     * Execute.
     * 
     * @param i
     *            the i
     * @return true, if successful
     */
    public boolean execute(final int i) {

        final TestUnit unit = (TestUnit) testUnits.elementAt(i);
        //#ifdef DEBUG
        debug.info("--== Executing: " + unit.name + " ==--");
        //#endif

        boolean ret;

        try {
            ret = unit.execute();
        } catch (final Exception ex) {
            //#ifdef DEBUG
            debug.error("Exception: " + ex);
            //#endif
            unit.result += " EXCPT";
            ret = false;
        }

        return ret;
    }

    /**
     * Gets the count.
     * 
     * @return the count
     */
    public int getCount() {

        return testUnits.size();
    }

    /**
     * Result.
     * 
     * @param i
     *            the i
     * @return the string
     */
    public String result(final int i) {
        final TestUnit unit = (TestUnit) testUnits.elementAt(i);
        String resUnit = "OK";
        if (!unit.passed) {
            resUnit = "FAIL";
        }
        final String ret = unit.name + ":" + resUnit + ":" + unit.result;
        return ret;
    }

}
