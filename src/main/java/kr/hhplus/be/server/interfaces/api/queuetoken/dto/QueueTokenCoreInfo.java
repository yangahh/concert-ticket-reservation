package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import java.util.UUID;

public record QueueTokenCoreInfo(
    Long concertId,
    UUID tokenUuid
) { }
