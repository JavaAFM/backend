create table  if not exists sources (
    id bigserial primary key,
    source_name text
);

create table  if not exists news (
    id bigserial primary key,
    news_title text,
    news_link text,
    source_id bigint not null,
    summary text,
    news_body text,
    created_at timestamp,
    constraint FK_news_source foreign key (source_id) references sources (id)
);

create table  if not exists comments (
    id bigserial primary key,
    author text,
    publication_time text,
    likes int default 0,
    content text,
    news_id bigint,
    constraint FK_comments_news foreign key (news_id) references news (id)
);

create table  if not exists main_tags (
    id bigserial primary key,
    group_name text,
    main_tag_name text
);

create table if not exists news_tags (
    id bigserial primary key,
    news_id bigint,
    tag text not null,
    constraint FK_news_tags_news foreign key (news_id) references news (id),
    unique (news_id, tag)
);

CREATE TABLE IF NOT EXISTS public.source_requests (
    id              SERIAL PRIMARY KEY,                      -- Auto-incrementing primary key
    reason          TEXT,                                    -- Reason for the request (optional)
    link            VARCHAR(255) NOT NULL UNIQUE,           -- Source link (must be unique)
    name            VARCHAR(255) NOT NULL,                  -- Source name (cannot be NULL)
    published_date  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Automatically set timestamp
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- Enum-like status field (PENDING, ACCEPTED, DECLINED)
    type            VARCHAR(50) NOT NULL DEFAULT 'telegram',-- Default type is 'telegram'
    user_id         BIGINT NOT NULL,                        -- References the user who made the request
    CONSTRAINT fk_source_requests_user
        FOREIGN KEY (user_id) REFERENCES public.users(id)
        ON DELETE CASCADE                                    -- Delete request if user is deleted
);
