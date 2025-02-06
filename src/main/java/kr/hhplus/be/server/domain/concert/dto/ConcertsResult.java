package kr.hhplus.be.server.domain.concert.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kr.hhplus.be.server.domain.concert.entity.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConcertsResult.class, name = "ConcertsResult")
})

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(as = ConcertsResult.class)
public class ConcertsResult{
    List<ConcertResult> concerts;
    long total;
    int offset;
    int limit;

    public static ConcertsResult fromPage(Page<Concert> pageData) {
        List<ConcertResult> concerts = pageData.getContent().stream()
                .map(ConcertResult::fromEntity)
                .toList();
        return ConcertsResult.builder()
                .concerts(concerts)
                .total(pageData.getTotalElements())
                .offset(pageData.getNumber() * pageData.getSize())
                .limit(pageData.getSize())
                .build();
    }
}
