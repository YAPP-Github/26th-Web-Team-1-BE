package eatda.repository.store;

import static org.assertj.core.api.Assertions.assertThat;

import eatda.domain.ImageKey;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.BaseRepositoryTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheerRepositoryTest extends BaseRepositoryTest {

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

            Optional<ImageKey> imageKey = cheerRepository.findRecentImageKey(store);

            assertThat(imageKey)
                    .map(ImageKey::getValue)
                    .contains("image-key-2");
        }

        @Test
        void 응원들의_이미지가_모두_비어있다면_해당_값이_없다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);

            Optional<ImageKey> imageKey = cheerRepository.findRecentImageKey(store);

            assertThat(imageKey).isEmpty();
        }
    }

    @Nested
    class FindAllImageKey {

        @Test
        void 응원들_중_모든_null이_아닌_이미지_키를_조회한다() throws InterruptedException {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, "image-key-1");
            Thread.sleep(5);
            cheerGenerator.generateCommon(member, store, "image-key-2");
            cheerGenerator.generateCommon(member, store, null);

            List<ImageKey> imageKeys = cheerRepository.findAllImageKey(store);

            assertThat(imageKeys)
                    .extracting(ImageKey::getValue)
                    .containsExactly("image-key-2", "image-key-1");
        }

        @Test
        void 응원들의_이미지가_모두_비어있다면_빈_리스트를_반환한다() {
            Member member = memberGenerator.generate("111");
            Store store = storeGenerator.generate("농민백암순대", "서울 강남구 대치동 896-33");
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);
            cheerGenerator.generateCommon(member, store, null);

            List<ImageKey> imageKeys = cheerRepository.findAllImageKey(store);

            assertThat(imageKeys).isEmpty();
        }
    }
}
