/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * The Class NegotiateSecurityFilterLoadTest.
 */
class NegotiateSecurityFilterLoadTest {

    /**
     * Launch load test.
     *
     * @throws RunnerException
     *             the runner exception
     */
    @Test
    void launchLoadTest() throws RunnerException {
        final Options opt = new OptionsBuilder().threads(10).measurementIterations(10).build();
        new Runner(opt).run();
    }

    /**
     * The Class St.
     */
    @State(Scope.Thread)
    public static class St {

        /** The tests. */
        private final NegotiateSecurityFilterTest tests = new NegotiateSecurityFilterTest();

        /**
         * Initialize.
         *
         * @throws ServletException
         *             the servlet exception
         */
        @Setup(Level.Trial)
        public void initialize() throws ServletException {
            this.tests.setUp();
        }

        /**
         * Benchmark.
         *
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws ServletException
         *             the servlet exception
         */
        @Benchmark
        public void benchmark() throws IOException, ServletException {
            this.tests.testNegotiate();
        }

        /**
         * Cleanup.
         */
        @TearDown(Level.Trial)
        public void cleanup() {
            this.tests.tearDown();
        }

    }

}
