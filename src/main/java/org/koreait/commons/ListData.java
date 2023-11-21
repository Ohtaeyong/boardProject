package org.koreait.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListData<T> { // 목록데이터와 pagination데이터
    private List<T> content;
    private Pagination pagination;
}