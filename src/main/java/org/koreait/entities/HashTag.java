package org.koreait.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class HashTag { // @ManyToMany

    @Id
    @Column(length = 30)
    private String tag;

    @ToString.Exclude
    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    private List<BoardData> items = new ArrayList<>();

}
