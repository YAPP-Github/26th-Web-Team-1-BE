package eatda.repository;

import java.time.Duration;
import lombok.Getter;

@Getter
public enum CacheSetting {

    IMAGE("image", Duration.ofMinutes(25), 1_000),
    ;

    private final String name;
    private final Duration timeToLive;
    private final int maximumSize;

    CacheSetting(String image, Duration timeToLive, int maximumSize) {
        this.name = image;
        this.timeToLive = timeToLive;
        this.maximumSize = maximumSize;
    }
}
