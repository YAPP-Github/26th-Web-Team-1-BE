package timeeat.domain.store;

import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.enums.InterestArea;
import timeeat.enums.StoreCategory;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private StoreCategory category;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "address", nullable = false)
    private String address;

    @Embedded
    private StorePhoneNumber storePhoneNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private StoreHours storeHours;

    @Column(name = "introduction")
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_area", nullable = false)
    private InterestArea interestArea;

    //TODO 빌더 패턴으로 변경 합의, 다음 도메인 수정시 반영 필요

    public Store(
            String name,
            String category,
            String address,
            Double latitude,
            Double longitude,
            String phoneNumber,
            String imageUrl,
            LocalTime openTime,
            LocalTime closeTime,
            String introduction,
            String interestArea
    ) {

        validateName(name);
        validateAddress(address);

        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.introduction = introduction;

        this.category = StoreCategory.from(category);
        this.interestArea = InterestArea.from(interestArea);
        this.coordinates = new Coordinates(latitude, longitude);
        this.storePhoneNumber = new StorePhoneNumber(phoneNumber);
        this.storeHours = new StoreHours(openTime, closeTime);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_NAME);
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_STORE_ADDRESS);
        }
    }
}
