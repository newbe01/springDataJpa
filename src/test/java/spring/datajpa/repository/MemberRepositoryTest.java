package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.Entity.Member;
import spring.datajpa.Entity.Team;
import spring.datajpa.dto.MemberDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback(value = false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Autowired
    TeamRepository teamRepository;

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

    @Test
    void findUsernameList() {
        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<String> result = repository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("testA", 15);
        m1.setTeam(team);
        repository.save(m1);


        List<MemberDto> memberDto = repository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNames() {
        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<Member> result = repository.findByNames(Arrays.asList("testA", "testB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        Member m1 = new Member("testA", 15);
        Member m2 = new Member("testB", 10);
        repository.save(m1);
        repository.save(m2);

        List<Member> testA = repository.findListByUsername("testA");
        Member testB = repository.findMemByUsername("testB");
        Optional<Member> optionalMem = repository.findOptionalByUsername("testB");

    }

    @Test
    void paging() {
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 10));
        repository.save(new Member("member3", 10));
        repository.save(new Member("member4", 10));
        repository.save(new Member("member5", 10));
        repository.save(new Member("member6", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = repository.findByAge(10, pageRequest);

        // page -> dto
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void slice() {
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 10));
        repository.save(new Member("member3", 10));
        repository.save(new Member("member4", 10));
        repository.save(new Member("member5", 10));
        repository.save(new Member("member6", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> page = repository.findSliceByAge(10, pageRequest);

        List<Member> content = page.getContent();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

}
