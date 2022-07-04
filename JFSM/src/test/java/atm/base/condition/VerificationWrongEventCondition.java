package atm.base.condition;

import atm.base.AtmAccount;
import com.fsm.StateManager;
import com.fsm.event.base.StateEvent;
import com.fsm.module.base.EventCondition;
import com.fsm.unit.StateUnit;

/**
 * @class public class InactiveStopEventCondition extends EventCondition
 * @brief InactiveStopEventCondition class
 */
public class VerificationWrongEventCondition extends EventCondition {

    public VerificationWrongEventCondition(StateManager stateManager, StateEvent stateEvent) {
        super(stateManager, stateEvent);
    }

    @Override
    public boolean checkCondition () {
        StateUnit stateUnit = getCurStateUnit();
        if (stateUnit == null) { return false; }

        AtmAccount atmAccount = (AtmAccount) stateUnit.getData();
        if (atmAccount == null) { return false; }

        int curVerificationWrongCount = atmAccount.getVerificationWrongCount();
        return (curVerificationWrongCount > 0) &&
                (curVerificationWrongCount < AtmAccount.MAX_VERIFICATION_WRONG_COUNT);
    }

}
