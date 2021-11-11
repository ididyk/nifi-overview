package ua.lviv.javaclub.processors;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;


public class ToDoProcessorTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(ToDoProcessor.class);
    }

    @Test
    public void testProcessor() {

    }

}
