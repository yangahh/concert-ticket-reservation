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
    private Long userId;

    @NotNull
    @Positive
    private Long amount;
}
