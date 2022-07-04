package atm.base;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @class public class AtmAccount
 * @brief AtmAccount class
 */
public class AtmAccount {

    public static final int MAX_VERIFICATION_WRONG_COUNT = 5;
    private static final String FIXED_PIN_STRING = "1234";

    private final String id;
    private final String name;
    private final String address;
    private final String phoneNumber;

    private final String atmStateUnitId;

    private final AtomicBoolean isVerified = new AtomicBoolean(false);
    private final AtomicInteger verificationWrongCount = new AtomicInteger(0);
    private String pinString = null;

    public AtmAccount(String id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;

        this.atmStateUnitId = id + "_atm";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAtmStateUnitId() {
        return atmStateUnitId;
    }

    public boolean isVerified() {
        return isVerified.get();
    }

    public void setVerified(boolean verified) {
        isVerified.set(verified);
    }

    public boolean verifyPinString (String pinString) {
        return pinString.equals(FIXED_PIN_STRING);
    }

    public int addAndGetVerificationWrongCount(int delta) {
        return verificationWrongCount.addAndGet(delta);
    }

    public void setVerificationWrongCount(int delta) {
        if (delta < 0) { delta = 0; }
        verificationWrongCount.set(delta);
    }

    public int getVerificationWrongCount() {
        return verificationWrongCount.get();
    }

    public boolean isVerificationFailed () {
        return getVerificationWrongCount() >= MAX_VERIFICATION_WRONG_COUNT;
    }

    public String getPinString() {
        return pinString;
    }

    public void setPinString(String pinString) {
        this.pinString = pinString;
    }
}
