package atm.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class public class TestUtil
 * @brief TestUtil class
 */
public class TestUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    public static void sleep (long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.warn("TestUtil: Thread.sleep.Exception", e);
        }
    }
    
}
