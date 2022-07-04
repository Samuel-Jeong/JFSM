package atm;

import atm.base.AtmAccount;
import atm.base.AtmEvent;
import atm.base.AtmState;
import atm.base.TestUtil;
import atm.base.callback.InputPinCallBack;
import atm.base.condition.RunFailEventCondition;
import atm.base.condition.VerificationFailEventCondition;
import atm.base.condition.VerificationWrongEventCondition;
import com.fsm.StateManager;
import com.fsm.module.StateHandler;
import com.fsm.module.StateTaskManager;
import com.fsm.unit.StateUnit;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class public class BasicProcessStateTest
 * @brief BasicProcessStateTest class
 */
public class BasicAtmStateTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicAtmStateTest.class);

    private final StateManager stateManager = new StateManager(100);

    ////////////////////////////////////////////////////////////////////////////////

    public void testStart () {
        init();

        normalTest();

        insertCardFailTest();
        readCardFailTest();
        inputPinFailTest();
        verificationFailTest();

        stop();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void init () {
        StateTaskManager stateTaskManager = stateManager.getStateTaskManager();

        stateManager.addStateHandler(AtmState.name);
        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);

        // RUN
        atmStateHandler.addState(
                AtmEvent.RUN,
                AtmState.IDLE, AtmState.READY,
                null,
                null,
                AtmEvent.INSERT_CARD_FAIL, 1000, 0
        );

        // RUN_FAIL
        atmStateHandler.addState(
                AtmEvent.RUN_FAIL,
                AtmState.ERROR, AtmState.IDLE,
                null,
                null,
                null, 0, 0
        );

        // INSERT_CARD
        atmStateHandler.addState(
                AtmEvent.INSERT_CARD,
                AtmState.READY, AtmState.CARD_READ,
                null,
                null,
                AtmEvent.READ_CARD_FAIL, 1000, 0
        );

        // INSERT_CARD_FAIL
        atmStateHandler.addState(
                AtmEvent.INSERT_CARD_FAIL,
                AtmState.READY , AtmState.IDLE,
                null,
                null,
                null, 0, 0
        );

        // READ_CARD
        atmStateHandler.addState(
                AtmEvent.READ_CARD,
                AtmState.CARD_READ, AtmState.PIN_ENTRY,
                null,
                null,
                AtmEvent.INPUT_PIN_FAIL, 1000, 0
        );

        // READ_CARD_FAIL
        atmStateHandler.addState(
                AtmEvent.READ_CARD_FAIL,
                AtmState.CARD_READ , AtmState.ERROR,
                null,
                null,
                null, 0, 0
        );

        // INPUT_PIN
        atmStateHandler.addState(
                AtmEvent.INPUT_PIN,
                AtmState.PIN_ENTRY, AtmState.VERIFICATION,
                new InputPinCallBack(stateManager, InputPinCallBack.class.getSimpleName()),
                null,
                AtmEvent.VERIFY_ACCOUNT_WRONG, 1000, 0
        );

        // INPUT_PIN_FAIL
        atmStateHandler.addState(
                AtmEvent.INPUT_PIN_FAIL,
                AtmState.PIN_ENTRY , AtmState.ERROR,
                null,
                null,
                null, 0, 0
        );

        // VERIFY_ACCOUNT_SUCCESS
        atmStateHandler.addState(
                AtmEvent.VERIFY_ACCOUNT_SUCCESS,
                AtmState.VERIFICATION, AtmState.SESSION,
                null,
                null,
                null, 0, 0
        );

        // VERIFY_ACCOUNT_WRONG
        atmStateHandler.addState(
                AtmEvent.VERIFY_ACCOUNT_WRONG,
                AtmState.VERIFICATION , AtmState.PIN_ENTRY,
                null,
                null,
                null, 0, 0
        );

        // VERIFY_ACCOUNT_FAIL
        atmStateHandler.addState(
                AtmEvent.VERIFY_ACCOUNT_FAIL,
                AtmState.VERIFICATION , AtmState.ERROR,
                null,
                null,
                null, 0, 0
        );

        // EXIT
        atmStateHandler.addState(
                AtmEvent.EXIT,
                AtmState.SESSION , AtmState.IDLE,
                null,
                null,
                null, 0, 0
        );

        Assert.assertFalse(atmStateHandler.getEventList().isEmpty());

        ////////////////////////////////////////////////////////////////////////////

        atmStateHandler.addEventCondition(
                new VerificationWrongEventCondition(
                        stateManager, atmStateHandler.getEvent(AtmEvent.VERIFY_ACCOUNT_WRONG)
                ),
                100
        );

        atmStateHandler.addEventCondition(
                new VerificationFailEventCondition(
                        stateManager, atmStateHandler.getEvent(AtmEvent.VERIFY_ACCOUNT_FAIL)
                ),
                100
        );

        atmStateHandler.addEventCondition(
                new RunFailEventCondition(
                        stateManager, atmStateHandler.getEvent(AtmEvent.RUN_FAIL)
                ),
                100
        );
    }

    public void stop () {
        stateManager.removeStateHandler(AtmState.name);
        stateManager.stop();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        AtmAccount atmAccount = new AtmAccount("account1", "james", "Seoul", "01012345678");

        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);
        stateManager.addStateUnit(atmAccount.getAtmStateUnitId(), atmStateHandler.getName(), AtmState.IDLE, atmAccount);
        /////////////////////////////////////////////////////////////

        // 1) RUN
        Assert.assertEquals(AtmState.READY, runAtm(atmAccount));
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 2) INSERT_CARD
        Assert.assertEquals(AtmState.CARD_READ, insertCard(atmAccount));
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 3) READ_CARD
        Assert.assertEquals(AtmState.PIN_ENTRY, readCard(atmAccount));
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 4) INPUT_PIN
        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "1234"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 5) VERIFY_ACCOUNT_SUCCESS
        Assert.assertEquals(AtmState.SESSION, verifyAccountSuccess(atmAccount));
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.SESSION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 6) EXIT
        Assert.assertEquals(AtmState.IDLE, exit(atmAccount));
        Assert.assertEquals(AtmState.SESSION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        /////////////////////////////////////////////////////////////
        stateManager.removeStateUnit(atmAccount.getAtmStateUnitId());
    }

    public void insertCardFailTest () {
        AtmAccount atmAccount = new AtmAccount("account1", "james", "Seoul", "01012345678");

        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);
        stateManager.addStateUnit(atmAccount.getAtmStateUnitId(), atmStateHandler.getName(), AtmState.IDLE, atmAccount);
        /////////////////////////////////////////////////////////////

        // 1) RUN
        Assert.assertEquals(AtmState.READY, runAtm(atmAccount));
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(2000);

        // 2) INSERT_CARD_FAIL
        //Assert.assertEquals(AtmState.CARD_READ, insertCard(atmAccount));
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        /////////////////////////////////////////////////////////////
        stateManager.removeStateUnit(atmAccount.getAtmStateUnitId());
    }

    public void readCardFailTest () {
        AtmAccount atmAccount = new AtmAccount("account1", "james", "Seoul", "01012345678");

        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);
        stateManager.addStateUnit(atmAccount.getAtmStateUnitId(), atmStateHandler.getName(), AtmState.IDLE, atmAccount);
        /////////////////////////////////////////////////////////////

        // 1) RUN
        Assert.assertEquals(AtmState.READY, runAtm(atmAccount));
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 2) INSERT_CARD
        Assert.assertEquals(AtmState.CARD_READ, insertCard(atmAccount));
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(2000);

        // 3) READ_CARD_FAIL
        Assert.assertEquals(AtmState.ERROR, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        /////////////////////////////////////////////////////////////
        stateManager.removeStateUnit(atmAccount.getAtmStateUnitId());
    }

    public void inputPinFailTest () {
        AtmAccount atmAccount = new AtmAccount("account1", "james", "Seoul", "01012345678");

        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);
        stateManager.addStateUnit(atmAccount.getAtmStateUnitId(), atmStateHandler.getName(), AtmState.IDLE, atmAccount);
        /////////////////////////////////////////////////////////////

        // 1) RUN
        Assert.assertEquals(AtmState.READY, runAtm(atmAccount));
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 2) INSERT_CARD
        Assert.assertEquals(AtmState.CARD_READ, insertCard(atmAccount));
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 3) READ_CARD
        Assert.assertEquals(AtmState.PIN_ENTRY, readCard(atmAccount));
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(2000);

        // 4) INPUT_PIN
        //Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount));
        Assert.assertEquals(AtmState.ERROR, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        /////////////////////////////////////////////////////////////
        stateManager.removeStateUnit(atmAccount.getAtmStateUnitId());
    }

    public void verificationFailTest () {
        AtmAccount atmAccount = new AtmAccount("account1", "james", "Seoul", "01012345678");

        StateHandler atmStateHandler = stateManager.getStateHandler(AtmState.name);
        stateManager.addStateUnit(atmAccount.getAtmStateUnitId(), atmStateHandler.getName(), AtmState.IDLE, atmAccount);
        /////////////////////////////////////////////////////////////

        // 1) RUN
        Assert.assertEquals(AtmState.READY, runAtm(atmAccount));
        Assert.assertEquals(AtmState.IDLE, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 2) INSERT_CARD
        Assert.assertEquals(AtmState.CARD_READ, insertCard(atmAccount));
        Assert.assertEquals(AtmState.READY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 3) READ_CARD
        Assert.assertEquals(AtmState.PIN_ENTRY, readCard(atmAccount));
        Assert.assertEquals(AtmState.CARD_READ, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        // 4) INPUT_PIN for 5 times
        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "4321"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(500);

        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "4321"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(500);

        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "4321"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(500);

        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "4321"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(500);

        Assert.assertEquals(AtmState.VERIFICATION, inputPin(atmAccount, "4321"));
        Assert.assertEquals(AtmState.PIN_ENTRY, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getPrevState());
        Assert.assertEquals(AtmState.VERIFICATION, stateManager.getStateUnit(atmAccount.getAtmStateUnitId()).getCurState());

        TestUtil.sleep(2000);

        // 5) VERIFY_ACCOUNT_FAIL

        /////////////////////////////////////////////////////////////
        stateManager.removeStateUnit(atmAccount.getAtmStateUnitId());
    }


    ////////////////////////////////////////////////////////////////////////////////

    public String runAtm (AtmAccount atmAccount) {
        logger.debug("* ATM is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.RUN,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String insertCard (AtmAccount atmAccount) {
        logger.debug("* Insert card!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.INSERT_CARD,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String readCard (AtmAccount atmAccount) {
        logger.debug("* Read card!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.READ_CARD,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String inputPin (AtmAccount atmAccount, String pinString) {
        logger.debug("* Input pin!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        StateUnit stateUnit = stateManager.getStateUnit(atmAccount.getAtmStateUnitId());
        atmAccount.setPinString(pinString);
        stateUnit.setData(atmAccount);
        return callStateHandler.fire(
                AtmEvent.INPUT_PIN,
                stateUnit
        );
    }

    public String verifyAccountSuccess (AtmAccount atmAccount) {
        logger.debug("* Success to verify the account!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.VERIFY_ACCOUNT_SUCCESS,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String verifyAccountWrong (AtmAccount atmAccount) {
        logger.debug("* Fail to verify the account! (Wrong)");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.VERIFY_ACCOUNT_WRONG,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String verifyAccountFail (AtmAccount atmAccount) {
        logger.debug("* Fail to verify the account!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.VERIFY_ACCOUNT_FAIL,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

    public String exit (AtmAccount atmAccount) {
        logger.debug("* Exit!");
        StateHandler callStateHandler = stateManager.getStateHandler(AtmState.name);

        return callStateHandler.fire(
                AtmEvent.EXIT,
                stateManager.getStateUnit(atmAccount.getAtmStateUnitId())
        );
    }

}
