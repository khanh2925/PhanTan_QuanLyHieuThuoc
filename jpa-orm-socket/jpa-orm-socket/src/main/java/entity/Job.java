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
@ToString(exclude = {"company", "skills", "applications"})
@Builder

@Entity
@Table(name = "jobs")
public class Job  implements Serializable {

    @Id
    @Column(name = "job_id")
    private String id;
    private String title;
    private String description;
    private double salary;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    @ManyToMany(mappedBy = "jobs")
    @JsonIgnore
    private Set<Skill> skills;

    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private Set<Application> applications;
}