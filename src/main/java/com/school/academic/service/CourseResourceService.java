package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Course;
import com.school.academic.entity.CourseResource;
import com.school.academic.repository.CourseResourceRepository;

@Service
@Transactional
public class CourseResourceService {

    private final CourseResourceRepository courseResourceRepository;

    public CourseResourceService(CourseResourceRepository courseResourceRepository) {
        this.courseResourceRepository = courseResourceRepository;
    }

    public List<CourseResource> getResourcesByCourseId(Long courseId) {
        return courseResourceRepository.findByCourseId(courseId);
    }

    public CourseResource saveResource(@NonNull CourseResource resource) {
        return courseResourceRepository.save(resource);
    }

    public void deleteResource(@NonNull Long id) {
        courseResourceRepository.deleteById(id);
    }

    public Optional<CourseResource> getResourceById(@NonNull Long id) {
        return courseResourceRepository.findById(id);
    }

    public CourseResource createResource(Course course, String title, String url, String type) {
        CourseResource resource = new CourseResource();
        resource.setCourse(course);
        resource.setTitle(title);
        resource.setUrl(url); // In a real app, this might be a file path after upload
        resource.setResourceType(type);
        return saveResource(resource);
    }
}
