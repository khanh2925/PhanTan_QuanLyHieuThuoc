package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"applications", "skills"})
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
@Builder

@Entity
@Table(name = "candidates")
public class Candidate  implements Serializable {

    @Id
    @Column(name = "cand_id")
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String email;
    private int experience;

    @OneToMany(mappedBy = "candidate")
    @JsonIgnore
    private Set<Application> applications;

    @ManyToMany(mappedBy = "candidates")
    @JsonIgnore
    private Set<Skill> skills;
}