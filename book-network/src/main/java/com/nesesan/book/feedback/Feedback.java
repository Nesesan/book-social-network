package com.nesesan.book.feedback;

import com.nesesan.book.book.Book;
import com.nesesan.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback extends BaseEntity {

    private double note;
    private String comment;

    @ManyToOne
    @JoinColumn(name ="book_id" )
    private Book book;

}
