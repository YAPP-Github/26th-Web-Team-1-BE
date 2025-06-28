-- member 테이블: PK(id)가 중복될 경우, 나머지 컬럼을 스크립트의 값으로 업데이트합니다.
INSERT INTO member (id, social_id, nickname, phone_number, interest_area, opt_in_marketing)
VALUES (1, 123456789, '로컬개발자', '01012345678', 'GANGNAM', true),
       (2, 987654321, '단위테스터', '01087654321', 'SEOCHO', false)
ON DUPLICATE KEY UPDATE social_id        = VALUES(social_id),
                        nickname         = VALUES(nickname),
                        phone_number     = VALUES(phone_number),
                        interest_area    = VALUES(interest_area),
                        opt_in_marketing = VALUES(opt_in_marketing);

-- store 테이블: PK(id)가 중복될 경우, 나머지 컬럼을 스크립트의 값으로 업데이트합니다.
INSERT INTO store (id, name, category, latitude, longitude, address, phone_number, image_url, open_time, close_time,
                   introduction, interest_area)
VALUES (1, '로컬 한식당', '한식', 37.5665, 126.9780, '서울특별시 강남구 테헤란로 123', '0212345678', 'https://example.com/store1.jpg',
        '09:00:00', '22:00:00', '로컬 테스트용 한식당입니다.', 'GANGNAM'),
       (2, '로컬 중식당', '중식', 37.5645, 126.9770, '서울특별시 서초구 서초대로 456', '0287654321', 'https://example.com/store2.jpg',
        '11:00:00', '21:00:00', '로컬 테스트용 중식당입니다.', 'SEOCHO'),
       (3, '로컬 일식당', '일식', 37.5685, 126.9790, '서울특별시 송파구 올림픽로 789', '0245678912', 'https://example.com/store3.jpg',
        '11:30:00', '22:30:00', '로컬 테스트용 일식당입니다.', 'SONGPA')
ON DUPLICATE KEY UPDATE name          = VALUES(name),
                        category      = VALUES(category),
                        latitude      = VALUES(latitude),
                        longitude     = VALUES(longitude),
                        address       = VALUES(address),
                        phone_number  = VALUES(phone_number),
                        image_url     = VALUES(image_url),
                        open_time     = VALUES(open_time),
                        close_time    = VALUES(close_time),
                        introduction  = VALUES(introduction),
                        interest_area = VALUES(interest_area);

-- menu 테이블: PK(id)가 중복될 경우, 나머지 컬럼을 스크립트의 값으로 업데이트합니다.
INSERT INTO menu (id, store_id, name, description, price, discount_price, start_time, end_time,
                  image_url)
VALUES (1, 1, '불고기', '맛있는 불고기', 15000, 12000, '2025-06-28 14:00:00', '2025-06-28 16:00:00',
        'https://example.com/menu1.jpg'),
       (2, 1, '비빔밥', '신선한 채소가 들어간 비빔밥', 12000, NULL, NULL, NULL, 'https://example.com/menu2.jpg'),
       (3, 2, '짜장면', '정통 중국 짜장면', 8000, 6000, '2025-06-28 12:00:00', '2025-06-28 14:00:00',
        'https://example.com/menu3.jpg'),
       (4, 3, '초밥 세트', '신선한 회로 만든 초밥 세트', 25000, 20000, '2025-06-28 15:00:00', '2025-06-28 17:00:00',
        'https://example.com/menu5.jpg')
ON DUPLICATE KEY UPDATE store_id       = VALUES(store_id),
                        name           = VALUES(name),
                        description    = VALUES(description),
                        price          = VALUES(price),
                        discount_price = VALUES(discount_price),
                        start_time     = VALUES(start_time),
                        end_time       = VALUES(end_time),
                        image_url      = VALUES(image_url);

-- bookmark 테이블: PK(id)가 중복될 경우, 나머지 컬럼을 스크립트의 값으로 업데이트합니다.
INSERT INTO bookmark (id, member_id, store_id)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 2)
ON DUPLICATE KEY UPDATE member_id = VALUES(member_id),
                        store_id  = VALUES(store_id);
