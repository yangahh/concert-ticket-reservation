package kr.hhplus.be.server.interfaces.api.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {
    @NotNull
    @Positive
    private long userId;

    @NotNull
    @Positive
    private long amount;
}
