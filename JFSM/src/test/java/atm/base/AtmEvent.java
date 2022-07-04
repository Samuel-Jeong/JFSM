package atm.base;

/**
 * @class public class CoinEvent
 * @brief CoinEvent class
 */
public class AtmEvent {

    public static final String RUN = "run";
    public static final String RUN_FAIL = "run_fail";

    public static final String INSERT_CARD = "insert_card";
    public static final String INSERT_CARD_FAIL = "insert_card_fail";

    public static final String READ_CARD = "read_card";
    public static final String READ_CARD_FAIL = "read_card_fail";

    public static final String INPUT_PIN = "input_pin";
    public static final String INPUT_PIN_FAIL = "input_pin_fail";

    public static final String VERIFY_ACCOUNT_SUCCESS = "verify_account_success";
    public static final String VERIFY_ACCOUNT_WRONG = "verify_account_wrong";
    public static final String VERIFY_ACCOUNT_FAIL = "verify_account_fail";

    public static final String EXIT = "exit";

}
