-- PRODUCERS
INSERT INTO producer (producer_id, name, description, thumbnail_url, image_url) VALUES
('p1111111-1111-1111-1111-111111111111', 'TechMedia', 'Producer of technology podcasts.', 'https://cdn.example.com/producers/techmedia_thumb.jpg', 'https://cdn.example.com/producers/techmedia_image.jpg'),
('p2222222-2222-2222-2222-222222222222', 'LaughFactory', 'Producer of comedy podcasts.', 'https://cdn.example.com/producers/laughfactory_thumb.jpg', 'https://cdn.example.com/producers/laughfactory_image.jpg'),
('p3333333-3333-3333-3333-333333333333', 'CrimeMedia', 'Producer of true crime podcasts.', 'https://cdn.example.com/producers/crimemedia_thumb.jpg', 'https://cdn.example.com/producers/crimemedia_image.jpg');

-- LINK PODCASTS TO PRODUCERS
INSERT INTO producer_podcast_ids (producer_id, podcast_id) VALUES
('p1111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('p2222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('p3333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc');
