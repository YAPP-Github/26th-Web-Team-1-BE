package timeeat.domain.bookmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timeeat.domain.member.Member;
import timeeat.domain.store.Store;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BookmarkTest {

    @Nested
    @DisplayName("북마크 생성 시")
    class CreateBookmarkTest {

        @Test
        void 정상적인_멤버와_가게로_북마크를_생성한다() {
            Member member = new Member("socialId123");
            Store store = new Store(
                    "가게명",
                    "양식",
                    "주소",
                    37.5,
                    127.0,
                    "0212345678",
                    null,
                    LocalTime.of(11, 30),
                    LocalTime.of(21, 0),
                    null,
                    "강남구");

            Bookmark bookmark = new Bookmark(member, store);

            assertAll(
                    () -> assertThat(bookmark.getMember()).isEqualTo(member),
                    () -> assertThat(bookmark.getStore()).isEqualTo(store)
            );
        }

        @Test
        void 멤버가_null이면_예외를_던진다() {
            Store store = new Store(
                    "가게명",
                    "양식",
                    "주소",
                    37.5,
                    127.0,
                    "0212345678",
                    null,
                    LocalTime.of(11, 30),
                    LocalTime.of(21, 0),
                    null,
                    "강남구");

            assertThatThrownBy(() -> new Bookmark(null, store))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BOOKMARK_MEMBER_REQUIRED);
        }

        @Test
        void 가게가_null이면_예외를_던진다() {
            Member member = new Member("socialId123");

            assertThatThrownBy(() -> new Bookmark(member, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.BOOKMARK_STORE_REQUIRED);
        }
    }
}
