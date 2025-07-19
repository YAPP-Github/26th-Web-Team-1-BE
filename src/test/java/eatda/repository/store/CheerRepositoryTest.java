package eatda.repository.store;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.BaseJpaRepositoryTest;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerRepositoryTest extends BaseJpaRepositoryTest {

    @Nested
    class FindRecentImageKey {

        @Test
        void 응원들_중_최근_null이_아닌_이미지_키를_조회한다() throws InterruptedException {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, "image-key-1");
            Thread.sleep(5);
            cheerGenerator.generateCommon(member, store, "image-key-2");
            cheerGenerator.generateCommon(member, store, null);

            Optional<String> imageKey = cheerRepository.findRecentImageKey(store);

            assertThat(imageKey).contains("image-key-2");
        }

        @Test
        void 응원들의_이미지가_모두_비어있다면_해당_값이_없다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);

            Optional<String> imageKey = cheerRepository.findRecentImageKey(store);

            assertThat(imageKey).isEmpty();
        }
    }
}
