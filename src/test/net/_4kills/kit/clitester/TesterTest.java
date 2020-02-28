package net._4kills.kit.clitester;

import org.junit.jupiter.api.*;

import static net._4kills.kit.clitester.TestMain.main;

class TesterTest {
    @Test @Disabled
    void nullPointerExceptionTestWithQuit() {
        Result res = Tester.testAllCmds((e) -> main(e), "whup", "dup", "blup", "s s s", "quit");
        Assertions.assertEquals(new Result("whup", "dup", "blup", "s s s"), res);
    }

    @Test
    void  nullPointerExceptionTestWithoutQuit() {
        Result res = Tester.testAllCmds((e) -> main(e), "whup", "dup", "blup", "s s s");
        Assertions.assertEquals(new Result("whup", "dup", "blup", "s s s"), res);
    }


}
