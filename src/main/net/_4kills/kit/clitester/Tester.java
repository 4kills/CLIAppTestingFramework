package net._4kills.kit.clitester;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public abstract class Tester {
    private final static String TIMEOUT_INDICATION = "---TIMEOUT---";

    private static long timeout = 1000;
    private static boolean hasTimeouted;
    private static boolean printTimeouted = false;

    /**
     * Gets whether a timeout should be reflected in the actual Result provided by
     * {@link #testAll(Consumer, String[], String...)}, {@link #testAllCmds(Consumer, String...)} and
     * {@link #testAllArgs(Consumer, String...)}.<br>
     * <br>If this is true, the above mentioned methods will add: <p>---TIMEOUT---</p> to the list of contents of
     * the Result which is returned, to indicate that a timeout has occurred.
     * <br>Alternatively one may call {@link #hasTimeouted()} to find out whether a timeout occurred
     * <br>Default is false
     *
     * @return true if ---TIMEOUT--- should be added to the result
     */
    public static boolean getPrintTimeouted() {
        return printTimeouted;
    }

    /**
     * Sets whether a timeout should be reflected in the actual Result provided by
     * {@link #testAll(Consumer, String[], String...)}, {@link #testAllCmds(Consumer, String...)} and
     * {@link #testAllArgs(Consumer, String...)}.<br>
     * <br>If set to true, the above mentioned methods will add: <p>---TIMEOUT---</p> to the list of contents of
     * the Result which is returned, to indicate that a timeout has occurred.
     * <br>Default is false
     *
     * @param printTimeouted if true: ---TIMEOUT--- is added to the result.
     */
    public static void setPrintTimeouted(boolean printTimeouted) {
        Tester.printTimeouted = printTimeouted;
    }

    /**
     * Returns whether the previous call to test the main method has timeouted in the sense of
     * {@link #setTimeout(long)} and {@link #getTimeout()}.
     *
     * @return true if it has timeouted, false otherwise
     */
    public static boolean hasTimeouted() {
        return hasTimeouted;
    }

    /**
     * Timeout specifies how long to wait for the main method to terminate.
     * The main method is forcefully terminated after the specified time in <b>ms</b> has passed.
     * Defaults to 1000 ms.
     * <p>
     * <b>A value of 0 means to wait indefinitely</b>
     * </p>
     *
     * @param t Timeout in ms. t = 0 means to wait indefinitely.
     */
    public static void setTimeout(long t) {
        timeout = t;
    }

    /**
     * Timeout specifies how long to wait for the main method to terminate.
     * The main method is forcefully terminated after the specified time in <b>ms</b> has passed.
     * Defaults to 1000 ms.
     * <p>
     * A value of 0 means to wait indefinitely.
     * </p>
     *
     * @return Time to wait for the main method in ms or 0 (wait indefinitely)
     */
    public static long getTimeout() {
        return timeout;
    }

    /**
     * testAllCmds runs the provided main method with no arguments and then sequentially feeds the provided commands
     * to stdin. Then it collects the results of main from stdout and returns them line-wise as String[].
     *
     * @param main The main method to be executed
     * @param cmds The line-wise commands to provide to main
     * @return The results main produced in the same order as the provided cmds
     */
    public static Result testAllCmds(Consumer<String[]> main, String... cmds) {
        return testAll(main, null, cmds);
    }

    /**
     * testAllArgs runs the provided main method with the provided arguments.
     * Then it collects the results of main from stdout and returns them line-wise as String[].
     *
     * @param main The main method to be executed
     * @param args The command line arguments provided to main
     * @return The results main produced for the given args
     */
    public static Result testAllArgs(Consumer<String[]> main, String... args) {
        return new Result(executeMain(main, args));
    }

    /**
     * testAll runs the provided main method with the provided args and then sequentially feeds the provided commands
     * to stdin. Then it collects the results of main from stdout and returns them line-wise as String[].
     *
     * @param main The main method to be executed
     * @param args The command line arguments provided to main
     * @param cmds The line-wise commands to provide to main
     * @return The results main produced in the same order as the provided cmds
     */
    public static Result testAll(Consumer<String[]> main, String[] args,
                                 String... cmds) {
        InputStream in = new ByteArrayInputStream(normalizeInput(cmds).getBytes());
        InputStream oldIn = System.in;
        System.setIn(in);
        edu.kit.informatik.Terminal.reload();
        Result out = new Result(executeMain(main, args));

        System.setIn(oldIn);
        return out;
    }

    /**
     * starts the main with provided arguments in a new thread that times out after the period specified
     * by {@link #timeout}. Returns the results in stdout
     */
    private static String[] executeMain(Consumer<String[]> main, String[] args) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        PrintStream oldOut = System.out;
        System.setOut(out);

        hasTimeouted = true;
        Thread t = new Thread(() -> {
            try {
                main.accept(args);
            } catch (EndOfStreamException ignored) {
            }
            hasTimeouted = false;
        });
        t.start();

        try {
            t.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace(); // this shouldn't happen
        }

        System.out.flush();
        System.setOut(oldOut);
        String ret = output.toString().replace("\r", "");
        if (getPrintTimeouted() && hasTimeouted()) ret = ret.concat(TIMEOUT_INDICATION);
        return ret.split("\n");
    }

    /**
     * Adds a newline after each provided command.
     *
     * @param cmds The commands to be modified
     * @return The commands with newlines
     */
    private static String normalizeInput(String[] cmds) {
        StringBuilder str = new StringBuilder();
        for (String s : cmds) {
            str.append(s).append("\n");
        }
        return str.toString();
    }
}
