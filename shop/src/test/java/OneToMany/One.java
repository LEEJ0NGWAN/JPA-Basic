package OneToMany;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class One {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "one_id")
    private List<Many> manys = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Many> getManys() {
        return manys;
    }

    public void setManys(List<Many> manys) {
        this.manys = manys;
    }
}
