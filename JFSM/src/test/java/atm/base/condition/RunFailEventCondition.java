package atm.base.condition;

import com.fsm.StateManager;
import com.fsm.event.base.StateEvent;
import com.fsm.module.base.EventCondition;

/**
 * @class public class InactiveStopEventCondition extends EventCondition
 * @brief InactiveStopEventCondition class
 */
public class RunFailEventCondition extends EventCondition {

    public RunFailEventCondition(StateManager stateManager, StateEvent stateEvent) {
        super(stateManager, stateEvent);
    }

    @Override
    public boolean checkCondition () {
        return true;
    }

}
