package com.school.communication.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Course;
import com.school.communication.entity.CourseForum;
import com.school.communication.entity.ForumMessage;
import com.school.communication.repository.CourseForumRepository;
import com.school.communication.repository.ForumMessageRepository;
import com.school.core.entity.User;

@Service
@Transactional
public class CourseForumService {

    private final CourseForumRepository forumRepository;
    private final ForumMessageRepository messageRepository;

    public CourseForumService(CourseForumRepository forumRepository,
            ForumMessageRepository messageRepository) {
        this.forumRepository = forumRepository;
        this.messageRepository = messageRepository;
    }

    public CourseForum getOrCreateForum(@NonNull Course course) {
        return forumRepository.findByCourseId(course.getId())
                .orElseGet(() -> {
                    CourseForum forum = new CourseForum();
                    forum.setCourse(course);
                    forum.setName("Foro de " + course.getName());
                    forum.setDescription("Espacio de discusión académica para el curso " + course.getCode());
                    return forumRepository.save(forum);
                });
    }

    public List<ForumMessage> getMessagesByForum(@NonNull Long forumId) {
        return messageRepository.findByForumIdOrderByCreatedAtAsc(forumId);
    }

    public CourseForum getForumById(@NonNull Long id) {
        return forumRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foro no encontrado"));
    }

    public ForumMessage postMessage(@NonNull Long forumId, @NonNull User author, @NonNull String content) {
        CourseForum forum = getForumById(forumId);

        ForumMessage message = new ForumMessage();
        message.setForum(forum);
        message.setAuthor(author);
        message.setContent(content);

        return messageRepository.save(message);
    }
}
