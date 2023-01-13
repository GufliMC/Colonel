package com.guflimc.colonel.common.test.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

public class MultiOutputStream extends OutputStream {

    private final Collection<OutputStream> out;

    public MultiOutputStream(Collection<OutputStream> outStreams) {
        this.out = List.copyOf(outStreams);
    }

    public MultiOutputStream(OutputStream... outStreams) {
        this.out = List.of(outStreams);
    }

    @Override
    public void write(int arg0) throws IOException {
        for (OutputStream var : out) {
            var.write(arg0);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream var : out) {
            var.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream var : out) {
            var.write(b, off, len);
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream var : out) {
            var.close();
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream var : out) {
            var.flush();
        }
    }

}