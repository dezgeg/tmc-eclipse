package fi.helsinki.cs.plugin.tmc.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.helsinki.cs.plugin.tmc.domain.Course;
import fi.helsinki.cs.plugin.tmc.domain.Exercise;
import fi.helsinki.cs.plugin.tmc.domain.Project;
import fi.helsinki.cs.plugin.tmc.io.FileIO;
import fi.helsinki.cs.plugin.tmc.io.ProjectScanner;
import fi.helsinki.cs.plugin.tmc.storage.CourseStorage;
import fi.helsinki.cs.plugin.tmc.storage.DataSource;
import fi.helsinki.cs.plugin.tmc.storage.ProjectStorage;

public class DAOManager {

    public static final String DEFAULT_COURSES_PATH = "courses.tmp";
    public static final String DEFAULT_PROJECTS_PATH = "projects.tmp";

    private FileIO coursesPath;
    private FileIO projectsPath;

    private CourseDAO courseDAO;
    private ProjectDAO projectDAO;

    public DAOManager() {
        this(new FileIO(DEFAULT_COURSES_PATH), new FileIO(DEFAULT_PROJECTS_PATH));
    }

    public DAOManager(FileIO coursesPath, FileIO projectsPath) {
        this.coursesPath = coursesPath;
        this.projectsPath = projectsPath;
    }

    public CourseDAO getCourseDAO() {
        if (this.courseDAO == null) {
            initialize();
        }

        return courseDAO;
    }

    public ProjectDAO getProjectDAO() {
        if (this.projectDAO == null) {
            initialize();
        }

        return projectDAO;
    }

    private void initialize() {
        DataSource<Course> courseStorage = new CourseStorage(coursesPath);
        this.courseDAO = new CourseDAO(courseStorage);

        DataSource<Project> projectStorage = new ProjectStorage(projectsPath);
        this.projectDAO = new ProjectDAO(projectStorage);

        linkCoursesAndExercises();
        scanProjectFiles();
    }

    private void scanProjectFiles() {
        ProjectScanner projectScanner = new ProjectScanner(projectDAO);
        projectScanner.updateProjects();
        projectDAO.save();
    }

    private void linkCoursesAndExercises() {
        Map<Course, List<Exercise>> exercisesMap = new HashMap<Course, List<Exercise>>();

        for (Project project : projectDAO.getProjects()) {
            Exercise exercise = project.getExercise();
            Course course = courseDAO.getCourseByName(exercise.getCourseName());
            exercise.setCourse(course);

            if (!exercisesMap.containsKey(course)) {
                exercisesMap.put(course, new ArrayList<Exercise>());
            }
            exercisesMap.get(course).add(exercise);
        }

        for (Course course : courseDAO.getCourses()) {
            if (exercisesMap.containsKey(course)) {
                course.setExercises(exercisesMap.get(course));
            }
        }
    }

}
