package pl.auctane.order.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int state; // 0 - new, 1 - in progress, 2 - completed

    private String name;

    @Override
    public boolean equals(Object o) {
        if(o instanceof Status)
            return ((Status) o).getId().equals(this.getId()) && ((Status) o).getState() == this.getState() && ((Status) o).getName().equals(this.getName());
        return false;
    }
}
