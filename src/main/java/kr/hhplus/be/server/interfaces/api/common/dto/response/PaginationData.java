package kr.hhplus.be.server.interfaces.api.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaginationData<T> {
    private final Integer offset;
    private final Integer limit;
    private final Long count;
    private final List<T> items;
}
