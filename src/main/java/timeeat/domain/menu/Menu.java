package timeeat.domain.menu;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timeeat.exception.BusinessErrorCode;
import timeeat.exception.BusinessException;

@Table(name = "menu")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    private static final int MAX_NAME_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private Price price;

    @Embedded
    private Discount discount;

    public Menu(
            String name,
            String description,
            Integer price,
            String imageUrl,
            Integer discountPrice,
            LocalDateTime discountStartTime,
            LocalDateTime discountEndTime
    ) {
        validateName(name);
        validateNameLength(name);

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = new Price(price);
        this.discount = new Discount(this.price, discountPrice, discountStartTime, discountEndTime);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_MENU_NAME);
        }
    }

    private void validateNameLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(BusinessErrorCode.INVALID_MENU_LENGTH);
        }
    }
}
