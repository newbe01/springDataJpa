package spring.datajpa.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Member {

    @GeneratedValue
    @Id
    private Long id;

    private String username;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }

}
