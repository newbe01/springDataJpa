package spring.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.datajpa.Entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
