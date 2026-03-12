package edu.team.carshopbackend.controller;

import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/")
public class ApiController {

    /**
     * Simple health check endpoint returning pong.
     *
     * @return string "pong!"
     */
    @GetMapping("v1/ping")
    @Operation(summary = "Simple ping endpoint", description = "return  pong, to test API")
    public String pingPong() {
        return "pong!";
    }

    /**
     * Secured health-check endpoint. Returns a greeting for the authenticated user.
     *
     * @param principal authenticated user's principal
     * @return greeting string
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("secured/ping")
    @Operation(summary = "Secured ping endpoint", description = "return secured_pong-string, to test secured API access")
    public String securedPingPong(@AuthenticationPrincipal UserDetailsImpl principal){
        return "secured_pong, hi! " + principal.getUsername();
    }

    /**
     * Echoes back the provided id path variable.
     *
     * @param id id to echo
     * @return the same id
     */
    @PostMapping("v1/test_post/{id}")
    @Operation(summary = "Echo id", description = "accepts ID and return it in the response")
    public Integer post(@PathVariable("id") Integer id) {
        return id;
    }

    /**
     * Echoes integer provided in the request body.
     *
     * @param id integer value
     * @return the same integer
     */
    @PutMapping("v1/test_put/")
    public Integer put(@RequestBody Integer id) {
        return id;
    }

}

