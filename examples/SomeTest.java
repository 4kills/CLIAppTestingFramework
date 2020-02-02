import net._4kills.kit.clitester.*;
import org.junit.jupiter.api.*;

import java.util.function.Consumer;

class MainTest {
    @Test
    void someTest () {
        // the main method of your application. If you import the package Main.main(e) is sufficient. 
        Consumer<String[]> mainMethod = (e) -> net._4kills.kit.somepackage.Main.main(e);

        // tests the given application against the provided commands in the provided order.
        // Thus 'lol' is fed first to the application, followed by 'start  torus' etc.
        // The result contains all the outputs of the application to stdout.
        Result actual = Tester.testAllCmds(mainMethod, "lol", "start  torus", "badg", "start torus", "quit");

        // now you can declare your expected results. The Result.ERR matches any output beginning with "Error, ".
        // So in this example we expect 3 errors followed by exactly one "OK" output.
        // After that we expect the app to exit due to the "quit" command, so there shouldn't be any further output!
        Result expected = new Result(Result.ERR, Result.ERR, Result.ERR, "OK");

        // finally the assertEquals tests if the actual and expected result are equal, in the sense that
        // all their entries are the same except for error-msgs in the form of "Error, ".
        Assertions.assertEquals(expected, actual);
    }

    // you could also set this as static attribute of your test class so you dont have to assign
    // it every time.
    static Consumer<String[]> mainMethod = (e) -> net._4kills.kit.somepackage.Main.main(e);

    @Test
    void someOtherTest () {
        // we can also test our application by providing additional command line arguments that will be
        // given to the main application.
        String[] args = new String[] {"argument1", "argument2"};
        // even though it is strongly recommanded to always use a "quit" call at the end you don't need to
        // call it explicitly! If you don't call it the main method will timeout after the period specified in
        // Tester.getTimeout(). Use Tester.setTimeout() so specify a custom timeout. 
        Result actual = Tester.testAll(mainMethod, args, "lol", "start  torus", "badg", "bs", "bs2");

        // this time we expect 5 errors, but instead of having to type them all out we can use the following overload:
        Result expected = new Result(5);
        // = new Result(Result.ERR, Result.ERR, Result.ERR, Result.ERR, Result.ERR);

        // There is also a mixed constructor that first takes n errors and then other result entries:
        // Result expected = new Result(3, "OK");
        // = new Result(Result.ERR, Result.ERR, Result.ERR, "OK");

        // With junit 5 we can even see the actual results and the expected ones if the tests fail.
        Assertions.assertEquals(expected, actual);
    }
}