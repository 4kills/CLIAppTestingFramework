package net._4kills.kit.clitester;

/**
 * This exception is thrown if and only if 'null' is read from the input stream by the main method.
 * It is used to stop the main method from executing when the input contents run out.
 */
public final class EndOfStreamException extends RuntimeException {

}
