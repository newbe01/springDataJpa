package spring.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.datajpa.Entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
