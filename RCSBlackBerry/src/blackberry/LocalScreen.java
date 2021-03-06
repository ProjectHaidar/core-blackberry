//#preprocess

/* *************************************************
 * Copyright (c) 2010 - 2012
 * HT srl,   All rights reserved.
 * 
 * Project      : RCS, RCSBlackBerry
 * *************************************************/

package blackberry;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class LocalScreen extends MainScreen {

    public LocalScreen(Main main) {
        super();
        //#ifdef DEBUG
        LabelField title = new LabelField("BBMINject Demo", LabelField.ELLIPSIS
                | LabelField.USE_ALL_WIDTH);
        setTitle(title);
        //#endif
    }

}
