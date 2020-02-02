package net._4kills.kit.clitester;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.function.Consumer;

public abstract class Tester {
    private static long timeout = 1000;

    /**
     * timeout specifies how long to wait for the main method to terminate. This is needed when no 'quit' command
     * is provided. The main method is forcefully terminated after the specified time in <b>ms</b> has passed.
     * Defaults to 1000 ms.
     * <p>
     * If you want the possibly fastest execution please use a "quit" command.
     * </p>
     *
     * @param t Timeout in ms. Must be greater than 30 ms but smaller than 60 000 ms (1 min).
     */
    public static void setTimeout(long t) {
        if (t < 30 || t > 60000) return;
        timeout = t;
    }

    /**
     * timeout specifies how long to wait for the main method to terminate. This is needed when no 'quit' command
     * is provided. The main method is forcefully terminated after the specified time in <b>ms</b> has passed.
     * Defaults to 1000 ms.
     * <p>
     * If you want the possibly fastest execution please use a "quit" command.
     * </p>
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
    public static Result testAllCmds(@NotNull Consumer<String[]> main, @NotNull String... cmds) {
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
    public static Result testAllArgs(@NotNull Consumer<String[]> main, @NotNull String... args) {
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
    public static Result testAll(@NotNull Consumer<String[]> main, @Nullable String[] args,
                                 @Nullable String... cmds) {
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

        Thread t = new Thread(() -> {
            try {
                main.accept(args);
            } catch (NullPointerException e) {
                /* this happens when no "quit" statement is provided, main is interrupted by join
                 and the main class reads null from the input stream.
                 I can't fix this because I have no access to the main method.
                 If the NullPointerException does not originate from the above mentioned cause, the test will fail
                */
                final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));
                try {
                    if (IN.readLine() != null) {
                        e.printStackTrace();
                        System.err.println(e.getMessage());
                    }

                } catch (IOException d) {
                    d.printStackTrace();
                    System.err.println(d.getMessage());
                }
            }
        });
        t.start();

        try {
            t.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace(); // this shouldn't happen
        }

        System.out.flush();
        System.setOut(oldOut);
        return output.toString().split("\r\n");
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
