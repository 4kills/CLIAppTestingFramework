package net._4kills.kit.clitester;

import com.sun.istack.internal.NotNull;

import java.util.Objects;

public final class Result {
    /**
     * Represents an error output by the tested app. This field matches any output starting with "Error, " as
     * required by the Praktomat. Aside from this field the equals method is a bijection, matching only the
     * exact provided output.
     */
    public static final String ERR = "Error, .*";

    private final String[] res;

    // forbidden
    private Result() {res = new String[0];}

    /**
     * Creates a new result with the provided Strings as entries. You can pass {@link #ERR} here to compare
     * against any error message. Two results may be checked for equality by the {@links #equals(Object o)} method.
     * Two results may be passed safely to JUNIT AssertEquals method.
     * @param res The commands to be fed to the main method. First argument = first command executed.
     */
    public Result (@NotNull String... res) {
        this.res = res;
        sanitize();
    }

    /**
     * Creates a new Result that has n-times {@link #ERR} as its entries. This is especially useful if you
     * want to test for a bunch of error-reports of the tested CLI-Application
     * @param n The number of expected Errors
     * @return The result with n-errors
     */
    public Result (int n) {
        this.res = new String[n];
        for (int i = 0; i < n; i++) {
            this.res[i] = ERR;
        }
    }

    /**
     * A combined constructor of {@link #Result(int)} and {@link #Result(String...)}. n-errors followed by the entries
     * in res.
     * @param n The number of expected Errors
     * @param res The result with n-errors followed by the entries in res.
     */
    public Result (int n, @NotNull String... res) {
        this.res = new String[n + res.length];
        for (int i = 0; i < n + res.length; i++) {
            if (i < n) {
                this.res[i] = ERR;
                continue;
            }
            this.res[i] = res[i - n];
        }
        sanitize();
    }

    /**
     * Deletes all \r characters in the string that might lead to problems with different implementations of the JVM
     */
    private void sanitize() {
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replace("\r", "");
        }
    }

    /**
     * Returns the result as a list of its entries separated by \n as String
     * @return The result as list of entries
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(String s : res) {
            str.append(s).append("\n");
        }
        return str.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) res);
    }

    /**
     * Compares the two results by checking whether their entries are equal. Also returns true if two
     * entries are Errors as specified by {@link #ERR}.
     */
    @Override
    public boolean equals(Object o) {
        final String[] res;
        if (!(o instanceof Result)) {
            return false;
        }
        res = ((Result) o).res;

        if(this.res.length != res.length) return false;
        for(int i = 0; i < this.res.length; i++) {
            if (this.res[i] == null && res[i] == null) continue;
            if (this.res[i] == null && res[i] != null) return false;
            if (this.res[i] != null && res[i] == null) return false;

            if (this.res[i].matches(ERR) && res[i].matches(ERR)) continue;
            if (!this.res[i].equals(res[i])) return false;
        }
        return true;
    }
}
