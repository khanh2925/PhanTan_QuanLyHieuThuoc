package entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder

@Entity
@Table(name = "applications")
@IdClass(Application.ApplicationId.class)
public class Application implements Serializable{
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @Id
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    @Id
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Setter
    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor
    @ToString
    @Builder
    public static class ApplicationId implements Serializable {
        private String candidate;
        private String job;
    }
}