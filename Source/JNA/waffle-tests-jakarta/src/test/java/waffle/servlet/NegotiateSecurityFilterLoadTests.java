/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet;

import jakarta.servlet.ServletException;

import java.io.IOException;

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
 * The Class NegotiateSecurityFilterLoadTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterLoadTests {

    /**
     * Launch load test.
     *
     * @throws RunnerException
     *             the runner exception
     */
    @Test
    public void launchLoadTest() throws RunnerException {
        final Options opt = new OptionsBuilder().threads(10).measurementIterations(10).build();
        new Runner(opt).run();
    }

    /**
     * The Class St.
     */
    @State(Scope.Thread)
    public static class St {

        /** The tests. */
        private final NegotiateSecurityFilterTests tests = new NegotiateSecurityFilterTests();

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
         *
         * @throws ServletException
         *             the servlet exception
         */
        @TearDown(Level.Trial)
        public void cleanup() throws ServletException {
            this.tests.tearDown();
        }

    }

}
