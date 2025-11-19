-- PRODUCERS
INSERT INTO producer (Producer_id, name, description, thumbnail_url, image_url) VALUES
('91111111-1111-1111-1111-111111111111', 'TechMedia', 'Producer of technology podcasts.', 'https://cdn.example.com/producers/techmedia_thumb.jpg', 'https://cdn.example.com/producers/techmedia_image.jpg'),
('92222222-2222-2222-2222-222222222222', 'LaughFactory', 'Producer of comedy podcasts.', 'https://cdn.example.com/producers/laughfactory_thumb.jpg', 'https://cdn.example.com/producers/laughfactory_image.jpg'),
('93333333-3333-3333-3333-333333333333', 'CrimeMedia', 'Producer of true crime podcasts.', 'https://cdn.example.com/producers/crimemedia_thumb.jpg', 'https://cdn.example.com/producers/crimemedia_image.jpg');

-- LINK PODCASTS TO PRODUCERS
INSERT INTO producer_podcast_ids (Producer_id, podcast_id) VALUES
('91111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('92222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('93333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc');
