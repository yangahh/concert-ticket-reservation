package kr.hhplus.be.server.interfaces.api.queuetoken.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenRequest {
    @NotNull(message = "userId는 필수값입니다.")
    @Positive
    private Long userId;
}
