package org.koreait.entities;

import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
//@Data // 이것만 넣어도 알아서 EqualsAndHashCode추가 -> getter, setter, EqualsAndHashCode, toString (여기선 getter와 setter가 필요없으므로)
@AllArgsConstructor @NoArgsConstructor
@IdClass(BoardViewId.class)
public class BoardViewId {

    private Long seq;
    private Integer uid;
}
