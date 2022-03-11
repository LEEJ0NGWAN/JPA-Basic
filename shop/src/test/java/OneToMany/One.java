package OneToMany;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class One {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "one_id")
    private List<Many> manys = new ArrayList<>();
}
