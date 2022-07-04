package atm.base;

/**
 * @class public class CoinState
 * @brief CoinState class
 */
public class AtmState {

    public static final String name = "atm_state";

    public static final String IDLE = "idle";
    public static final String READY = "ready";
    public static final String ERROR = "error";
    public static final String CARD_READ = "card_read";
    public static final String PIN_ENTRY = "pin_entry";
    public static final String VERIFICATION = "verification";
    public static final String SESSION = "session";

}
