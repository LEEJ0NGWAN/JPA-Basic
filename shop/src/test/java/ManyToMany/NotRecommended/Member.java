package ManyToMany.NotRecommended;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "member_product")
    private List<Product> products = new ArrayList<>();
}
