package ManyToOne;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class One {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "one")
    private List<Many> manys = new ArrayList<>();
}
