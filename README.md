# CLIAppTestingFramework

This library comprises a small collection of classes for conviently black-box testing a Java CLI app.

Please note that this project was originally created as a support tool for the 
final examinations in the programming module at the [Karlsruhe Institute of Technology (KIT)](https://www.kit.edu/english/index.php), Germany; winter semester 2019/20. 
It was designed to help students conduct integration, as well as acceptance tests, in order to aid them in finding bugs in their software before submitting it for evaluation.  
Originally, this library was **not** designed as a <ins>general purpose</ins> testing framework. 

# Getting started:

For now you can either integrate the repo into your own project or just [download](https://github.com/4kills/CLIAppTestingFramework/releases) the library as .jar
from the releases section.  

**IMPORTANT (especially for students at KIT): Use the edu.kit.informatik.Terminal provided with <ins>this</ins> project for your terminal I/O.**
This is both convenient, so you don't have to integrate the Terminal class anymore, and it 
ensures <ins>smooth operation</ins> by this package. 

# Usage:
 
The following example elaborates on the usage of this package. You only need a hook to your application's main method in order to test it thoroughly. 

```java
import net._4kills.kit.clitester.*;
import org.junit.jupiter.api.*;

import net._4kills.kit.somepackage.Main;

import java.util.function.Consumer;

class MainTest {
    @Test
    void someTest () {
        // the main method of your application. If you import the package, Main.main(e) is sufficient. 
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

    @Test
    void someOtherTest () {
        // we can also test our application by providing additional command line arguments that will be
        // given to the main application.
        String[] args = new String[] {"argument1", "argument2"};
        // even though it is strongly recommanded to always use a "quit" call at the end you don't need to
        // call it explicitly! If you don't call it the main method will timeout after the period specified in
        // Tester.getTimeout(). Use Tester.setTimeout() so specify a custom timeout. 
        Result actual = Tester.testAll(Main::main, args, "lol", "start  torus", "badg", "bs", "bs2");
        //                                 ^
        //                                 |
        // You can also just pass the main method as method reference.

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
```
