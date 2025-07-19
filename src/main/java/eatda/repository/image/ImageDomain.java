package eatda.repository.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageDomain {
    ARTICLE("article"),
    STORE("store"),
    MEMBER("member"),
    STORY("story");

    private final String name;
}
