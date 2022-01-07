package project.board.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {

    public static final String REGISTER_DATE = "registerDate";
    public static final String UPDATE_DATE = "updateDate";

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime registerDate;

    @LastModifiedDate
    @Column
    protected LocalDateTime updateDate;
}
