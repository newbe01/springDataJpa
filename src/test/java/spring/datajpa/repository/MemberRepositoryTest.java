package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;
import spring.datajpa.dto.MemberDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(value = false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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


    @Test
    void bulkUpdate() {
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 10));
        repository.save(new Member("member3", 10));
        repository.save(new Member("member4", 20));
        repository.save(new Member("member5", 20));
        repository.save(new Member("member6", 20));

        int count = repository.bulkAgePlus(11);

        List<Member> member5 = repository.findByUsername("member5");
        System.out.println("member5 = " + member5.get(0)); // db에선 21 1차캐시에서는 20 clearAutomatically = true 사용시 21

//        em.flush();
//        em.clear();

//        List<Member> flushMember5 = repository.findByUsername("member5");
//        System.out.println("flushMember5.get(0) = " + flushMember5.get(0));
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        repository.save(member1);
        repository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = repository.findEntityGraphByUsername("member1");
//        List<Member> members = repository.findAll();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberFetch() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        repository.save(member1);
        repository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = repository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        Member member = new Member("member1", 10);
        repository.save(member);
        em.flush();
        em.clear();

//        Member findMember = repository.findById(member.getId()).get();
        Member findMember = repository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // readOnly 라서 update X
    }

    @Test
    void lock() {
        Member member = new Member("member1", 10);
        repository.save(member);
        em.flush();
        em.clear();

        List<Member> member1 = repository.findLockByUsername("member1");
    }

    @Test
    void callCustom() {
        List<Member> memberCustom = repository.findMemberCustom();

    }

    @Test
    void specBase() {

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = repository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    void queryByEx() {

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        Example<Member> ex = Example.of(member, matcher);

        List<Member> result = repository.findAll(ex);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    void projections() {


        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

//        List<UsernameOnly> result = repository.findProjectionByUsername("m1");
//        List<UsernameOnlyDto> result2 = repository.findDtoByUsername("m1");
//        List<UsernameOnlyDto> result3 = repository.findGenericByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result4 = repository.findGenericByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections results : result4) {
            System.out.println("results.getUsername() = " + results.getUsername());
            System.out.println("results.getTeam().getName() = " + results.getTeam().getName());
        }
    }

    @Test
    void nativeQuery() {

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

//        Member result = repository.findByNativeQuery("m1");
//        System.out.println("result = " + result);

        Page<MemberProjection> result = repository.findByNativeProjection(PageRequest.of(0, 10));
        for (MemberProjection memberProjection : result.getContent()) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }

    }
}
