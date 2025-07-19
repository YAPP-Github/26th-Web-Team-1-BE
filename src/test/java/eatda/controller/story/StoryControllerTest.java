package eatda.controller.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import eatda.controller.BaseControllerTest;
import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StoryControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUpMock() {
        doNothing()
                .when(storyService)
                .registerStory(any(), any(), any());
    }

    @Nested
    class RegisterStory {

        @Test
        void 스토리를_등록할_수_있다() {
            String requestJson = """
                        {
                          "query": "농민백암순대",
                          "storeKakaoId": "123",
                          "description": "여기 진짜 맛있어요!"
                        }
                    """;

            byte[] imageBytes = "dummy image content".getBytes(StandardCharsets.UTF_8);

            Response response = given()
                    .contentType("multipart/form-data")
                    .header("Authorization", accessToken())
                    .multiPart("request", "request.json", requestJson.getBytes(StandardCharsets.UTF_8),
                            "application/json")
                    .multiPart("image", "image.png", imageBytes, "image/png")
                    .when()
                    .post("/api/stories");

            response.then().statusCode(201);
        }
    }

    @Nested
    class GetStories {

        @Test
        void 스토리_목록을_조회할_수_있다() {
            StoriesResponse mockResponse = new StoriesResponse(List.of(
                    new StoriesResponse.StoryPreview(1L, "https://s3.bucket.com/story/dummy/1.jpg"),
                    new StoriesResponse.StoryPreview(2L, "https://s3.bucket.com/story/dummy/2.jpg")
            ));

            doReturn(mockResponse)
                    .when(storyService)
                    .getPagedStoryPreviews(5);

            StoriesResponse response = given()
                    .queryParam("size", 5)
                    .when()
                    .get("/api/stories")
                    .then().statusCode(200)
                    .extract().as(StoriesResponse.class);

            assertAll(
                    () -> assertThat(response.stories()).hasSize(2),
                    () -> assertThat(response.stories().getFirst().storyId()).isEqualTo(1L),
                    () -> assertThat(response.stories().getFirst().imageUrl()).isEqualTo("https://s3.bucket.com/story/dummy/1.jpg")
            );
        }
    }

    @Nested
    class GetStory {

        @Test
        void 해당_스토리를_상세_조회할_수_있다() {
            long storyId = 1L;

            doReturn(new StoryResponse(
                    "123456",
                    "한식",
                    "진또곱창집",
                    "성동구",
                    "성수동",
                    "곱창은 여기",
                    "https://s3.bucket.com/story1.jpg"
            )).when(storyService).getStory(storyId);

            Response response = given()
                    .pathParam("storyId", storyId)
                    .when()
                    .get("/api/stories/{storyId}");

            response.then()
                    .statusCode(200)
                    .body("storeKakaoId", equalTo("123456"))
                    .body("category", equalTo("한식"))
                    .body("storeName", equalTo("진또곱창집"))
                    .body("storeDistrict", equalTo("성동구"))
                    .body("storeNeighborhood", equalTo("성수동"))
                    .body("description", equalTo("곱창은 여기"))
                    .body("imageUrl", equalTo("https://s3.bucket.com/story1.jpg"));
        }

        @Test
        void 존재하지_않는_스토리를_조회하면_404_응답한다() {
            long nonexistentId = 999L;

            doThrow(new BusinessException(BusinessErrorCode.STORY_NOT_FOUND))
                    .when(storyService).getStory(nonexistentId);

            Response response = given()
                    .pathParam("storyId", nonexistentId)
                    .when()
                    .get("/api/stories/{storyId}");

            response.then()
                    .statusCode(404)
                    .body("errorCode", equalTo(BusinessErrorCode.STORY_NOT_FOUND.getCode()))
                    .body("message", equalTo(BusinessErrorCode.STORY_NOT_FOUND.getMessage()));
        }
    }
}
