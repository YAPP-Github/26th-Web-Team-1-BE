package eatda.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import eatda.DatabaseCleaner;
import eatda.client.map.MapClient;
import eatda.client.oauth.OauthClient;
import eatda.fixture.CheerGenerator;
import eatda.fixture.MemberGenerator;
import eatda.fixture.StoreGenerator;
import eatda.repository.image.S3ImageRepository;
import eatda.repository.member.MemberRepository;
import eatda.repository.store.CheerRepository;
import eatda.repository.store.StoreRepository;
import eatda.repository.story.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    private static final String MOCKED_IMAGE_KEY = "mocked-image-path";
    private static final String MOCKED_IMAGE_URL = "https://example.com/image.jpg";

    @MockitoBean
    protected OauthClient oauthClient;

    @MockitoBean
    protected MapClient mapClient;

    @MockitoBean
    protected S3ImageRepository imageRepository;

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

    @BeforeEach
    void mockingImageService() {
        doReturn(MOCKED_IMAGE_URL).when(imageRepository).getPresignedUrl(anyString());
        doReturn(MOCKED_IMAGE_KEY).when(imageRepository).upload(any(), any());
    }
}
