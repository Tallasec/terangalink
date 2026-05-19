CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `username` varchar(255),
  `email` varchar(255),
  `password` varchar(255),
  `university` varchar(255),
  `field_of_study` varchar(255),
  `country` varchar(255),
  `role` varchar(255),
  `created_at` timestamp
);

CREATE TABLE `categories` (
  `id` integer PRIMARY KEY,
  `name` varchar(255),
  `description` text
);

CREATE TABLE `questions` (
  `id` integer PRIMARY KEY,
  `title` varchar(255),
  `content` text,
  `created_at` timestamp,
  `user_id` integer NOT NULL,
  `category_id` integer NOT NULL
);

CREATE TABLE `answers` (
  `id` integer PRIMARY KEY,
  `content` text,
  `votes` integer,
  `created_at` timestamp,
  `user_id` integer NOT NULL,
  `question_id` integer NOT NULL
);

CREATE TABLE `posts` (
  `id` integer PRIMARY KEY,
  `title` varchar(255),
  `content` text,
  `type` varchar(255),
  `city` varchar(255),
  `created_at` timestamp,
  `user_id` integer NOT NULL
);

CREATE TABLE `study_groups` (
  `id` integer PRIMARY KEY,
  `name` varchar(255),
  `subject` varchar(255),
  `description` text,
  `meeting_type` varchar(255),
  `created_at` timestamp,
  `creator_id` integer NOT NULL
);

CREATE TABLE `study_group_members` (
  `id` integer PRIMARY KEY,
  `study_group_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `joined_at` timestamp
);

CREATE TABLE `help_requests` (
  `id` integer PRIMARY KEY,
  `title` varchar(255),
  `content` text,
  `type` varchar(255),
  `status` varchar(255),
  `created_at` timestamp,
  `user_id` integer NOT NULL
);

CREATE TABLE `community_groups` (
  `id` integer PRIMARY KEY,
  `name` varchar(255),
  `description` text,
  `city` varchar(255),
  `contact` varchar(255),
  `whatsapp_link` varchar(255),
  `created_at` timestamp
);

ALTER TABLE `questions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `questions` ADD FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

ALTER TABLE `answers` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `answers` ADD FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`);

ALTER TABLE `posts` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `study_groups` ADD FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`);

ALTER TABLE `study_group_members` ADD FOREIGN KEY (`study_group_id`) REFERENCES `study_groups` (`id`);

ALTER TABLE `study_group_members` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `help_requests` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
