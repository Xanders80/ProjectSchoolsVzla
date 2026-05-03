package com.school.web.controller.communication;

import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.service.CourseService;
import com.school.communication.entity.CourseForum;
import com.school.communication.service.CourseForumService;
import com.school.core.entity.User;
import com.school.core.service.UserService;

@Controller
@RequestMapping("/academic/forum")
public class CourseForumController {

	private final CourseForumService forumService;
	private final CourseService courseService;
	private final UserService userService;

	public CourseForumController(CourseForumService forumService,
			CourseService courseService,
			UserService userService) {
		this.forumService = forumService;
		this.courseService = courseService;
		this.userService = userService;
	}

	@GetMapping("/{courseId}")
	public String viewForum(@PathVariable @NonNull Long courseId, Model model) {
		com.school.academic.entity.Course course = courseService.getCourseById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

		CourseForum forum = forumService
				.getOrCreateForum(java.util.Objects.requireNonNull(course, "El curso no puede ser null"));
		model.addAttribute("forum", forum);
		model.addAttribute("course", course);

		Long forumIdFromForum = forum.getId();
		if (forumIdFromForum == null) {
			throw new IllegalStateException("El foro no tiene un ID válido");
		}
		model.addAttribute("messages", forumService.getMessagesByForum(forumIdFromForum));

		return "communication/course-forum";
	}

	@PostMapping("/{forumId}/message")
	public String postMessage(@PathVariable @NonNull Long forumId,
			@RequestParam @NonNull String content,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		try {
			if (userDetails == null || userDetails.getUsername() == null) {
				throw new IllegalStateException("Usuario no autenticado");
			}
			String username = userDetails.getUsername();
			User author = userService.findByUsername(username)
					.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

			forumService.postMessage(forumId, java.util.Objects.requireNonNull(author, "El autor no puede ser null"),
					content);
			redirectAttributes.addFlashAttribute("successMessage", "Mensaje publicado");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al publicar mensaje: " + e.getMessage());
		}

		CourseForum forum = forumService.getForumById(forumId);
		com.school.academic.entity.Course forumCourse = forum.getCourse();
		return "redirect:/academic/forum/" + (forumCourse != null ? forumCourse.getId() : forumId);
	}
}
