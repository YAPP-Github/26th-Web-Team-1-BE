package eatda.repository;

import eatda.fixture.CheerGenerator;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({MemberGenerator.class, StoreGenerator.class, CheerGenerator.class})
@DataJpaTest
public abstract class BaseJpaRepositoryTest {

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected StoreGenerator storeGenerator;

    @Autowired
    protected CheerGenerator cheerGenerator;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected CheerRepository cheerRepository;

    @Autowired
    protected StoryRepository storyRepository;
}
