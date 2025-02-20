package kr.hhplus.be.server.tests.infra;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestEvent (
    @JsonProperty("id") Integer id,
    @JsonProperty("message") String message
) { }
