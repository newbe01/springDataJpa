package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.Entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback(value = false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Test
    void testMember() {
        System.out.println("repository.getClass() = " + repository.getClass());
        Member member = new Member("username");
        Member savedMember = repository.save(member);

        Member findMember = repository.findById(savedMember.getId()).get();

        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        repository.save(member1);
        repository.save(member2);

        Member findMember1 = repository.findById(member1.getId()).get();
        Member findMember2 = repository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!");
        Member updatedMember = repository.findById(member1.getId()).get();
        assertThat(updatedMember.getUsername()).isEqualTo("member!!");

        List<Member> all = repository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = repository.count();
        assertThat(count).isEqualTo(2);

        repository.delete(member1);
        repository.delete(member2);

        long deletedCount = repository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThen() {

        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<Member> result = repository.findByUsernameAndAgeGreaterThan("testA", 10);
        assertThat(result.get(0).getUsername()).isEqualTo("testA");
        assertThat(result.get(0).getAge()).isEqualTo(15);
    }

    @Test
    void findTmpBy() {
        List<Member> tmpBy = repository.findTopTmpBy();
    }

    @Test
    void testNamedQuery() {
        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<Member> result = repository.findByUsername("testA");
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    void testQuery() {
        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<Member> result = repository.findUser("testA", 15);
        assertThat(result.get(0)).isEqualTo(m1);
    }
}