/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring.boot.demo;

import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot Demo Rest Controller.
 */
@RestController
public class DemoController {

    /**
     * Spring Boot Demo Get Mapper.
     *
     * @param auth
     *            security authentication object
     *
     * @return response text
     */
    @GetMapping
    public String demo(final Authentication auth) {
        return String.format("Hello, %s. You have authorities: %s", auth.getPrincipal(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
    }

}
