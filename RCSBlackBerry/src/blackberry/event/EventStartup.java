//#preprocess
package blackberry.event;

import blackberry.config.ConfEvent;

public class EventStartup extends Event {

    protected boolean parse(ConfEvent event) {
        setDelay(SOON);
        setPeriod(NEVER);
        return true;
    }

    protected void actualStart() {
        onEnter();
    }

    protected void actualLoop() {
    
    }

    protected void actualStop() {
        onExit(); // di sicurezza
    }

}
