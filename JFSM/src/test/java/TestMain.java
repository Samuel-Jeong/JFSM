import atm.BasicAtmStateTest;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @class public class TestMain
 * @brief Test Main class
 */
public class TestMain {

    @BeforeClass
    public static void setUp () {

    }

    @Test
    public void TotalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // # ATM state
        BasicAtmStateTest basicAtmStateTest = new BasicAtmStateTest();
        basicAtmStateTest.testStart();
    }

}
