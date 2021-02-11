package com.github.hartsock.toys.cmds;

public class CommandRuntimeException extends RuntimeException {
    public CommandRuntimeException(final Exception e) {
        super(e);
    }
}
