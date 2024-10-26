package io.hhplus.javaconcerthancil.domain.concert;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConcertSchedule> schedules = new ArrayList<>();

    public Concert(final Long id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }

    public void addSchedule(ConcertSchedule schedule) {
        schedules.add(schedule);
        schedule.setConcert(this); // 양방향 관계 설정
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSchedules(List<ConcertSchedule> schedules) {
        this.schedules = schedules;
    }

    public List<ConcertSchedule> getSchedules() {
        return schedules;
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", schedules=" + schedules +
                '}';
    }
}
