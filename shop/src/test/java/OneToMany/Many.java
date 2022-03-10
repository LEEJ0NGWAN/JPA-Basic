package OneToMany;

import javax.persistence.*;

@Entity
public class Many {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "one_id", insertable = false, updatable = false)
    private One one;

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

    public One getOne() {
        return one;
    }

    public void setOne(One one) {
        this.one = one;
    }
}
