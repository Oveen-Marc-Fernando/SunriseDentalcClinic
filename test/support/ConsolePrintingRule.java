package support;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Prints a plain "PASS"/"FAIL" line to the console for every test method it's
 * attached to — JUnit's default output is just a dot per test plus a summary
 * at the end, which doesn't show per-test success/failure the way a manual
 * println-based test would. Add one line to any test class to get this:
 *
 * <pre>
 *     &#64;Rule
 *     public ConsolePrintingRule printResult = new ConsolePrintingRule();
 * </pre>
 */
public class ConsolePrintingRule extends TestWatcher {

    @Override
    protected void succeeded(Description description) {
        System.out.println("PASS: " + description.getClassName() + "." + description.getMethodName());
    }

    @Override
    protected void failed(Throwable e, Description description) {
        System.out.println("FAIL: " + description.getClassName() + "." + description.getMethodName()
                + " — " + e.getMessage());
    }
}
