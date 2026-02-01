/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring.boot.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is a Spring Boot demo application that configures the WAFFLE Spring Boot Starter to use Negotiate single sign
 * on.
 */
@SpringBootApplication
public class Application {

    /**
     * Spring boot main entry point.
     *
     * @param args
     *            arguments passed into spring boot application.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
