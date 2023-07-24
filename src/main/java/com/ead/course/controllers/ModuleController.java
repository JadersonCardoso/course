package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                             @RequestBody @Valid ModuleDto moduleDto) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course nt found!");
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCourse(courseModelOptional.get());
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
    }


    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course!");
        }
        moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted sucessfully!");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable(value = "courseid") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid ModuleDto moduleDto ) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not fount for this course.");
        }
        var moduleModel = moduleModelOptional.get();
        moduleModel.setTitle(moduleDto.getTitle());
        moduleModel.setDescription(moduleDto.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/coureses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable(value = "courseid") UUID courseId,
                                                           @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }

}
