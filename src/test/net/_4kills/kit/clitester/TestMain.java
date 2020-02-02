package net._4kills.kit.clitester;

import edu.kit.informatik.Terminal;

class TestMain {
    public static void main(String... args) {
        while (true) {
            String in = Terminal.readLine();
            if (in.equals("quit")) return;
            Terminal.printLine(in);
        }
    }
}
