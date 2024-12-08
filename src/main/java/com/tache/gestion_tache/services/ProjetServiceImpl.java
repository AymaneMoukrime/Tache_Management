package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetServiceImpl implements ProjectService {
    private final   ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;

    private final MailSender mailSender;

    @Override
    public Project addproject(UserDetails userDetails,Project project){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        if(project.getEndDate().before(new Date())){
            throw new RuntimeException("End date cannot be before start date");
        }
        project.setOwner(user);
        project.setStartDate(new Date());
        project.getUsers().add(user);
        //sendWelcomeEmail(project);
        return  projectRepository.save(project);

    }
    @Override
    public Project updateProject(UserDetails userDetails ,Long projectId, Project updatedProject) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if(updatedProject.getEndDate().before(new Date())){
            throw new RuntimeException("End date cannot be before start date");
        }

        if (!existingProject.getOwner().equals(user)) {
            throw new RuntimeException("You do not have permission ");
        }
        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setEndDate(updatedProject.getEndDate());

        return projectRepository.save(existingProject);
    }

    @Override
    public void deleteProject(UserDetails userDetails, Long projectId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));


        if (!existingProject.getOwner().equals(user)) {
            throw new RuntimeException("You do not have permission ");
        }

        projectRepository.delete(existingProject);
    }
    @Override
    public Project addTeamToProject(UserDetails userDetails, Long projectId, Long teamId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));



        if (!project.getOwner().equals(user)) {
            throw new RuntimeException("You do not have permission");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        if (project.getTeams().contains(team)) {
            throw new RuntimeException("team is already in this project");
        }


        project.getTeams().add(team);

        return projectRepository.save(project);
    }
    @Override
    public Project addTaskToProject(UserDetails userDetails, Long projectId, Long taskId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (!project.getOwner().equals(user)) {
            throw new RuntimeException("You do not have permission ");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (project.getTasks().contains(task)) {
            throw new RuntimeException("task is already in this project");
        }

        project.getTasks().add(task);
        return projectRepository.save(project);
    }

    @Override
    public List <Project> getAllProjectsForUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        return projectRepository.findAllByUserAccess(user.getId());
    }


    @Override
    public Project getProject(long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    /*
    public void sendWelcomeEmail(Project project) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(project.getOwner().getEmail());
        message.setSubject("Votre Project est creer par le nom  ! "+project.getName()+ " le "+ project.getStartDate());
        message.setText("Bonjour "+project.getOwner().getName()+"\n\n"+
                "Votre Prject " + project.getName() + ",\n\n" +
                "avec la description:"+"\n\n"+project.getDescription()+ "\n\n");
        mailSender.send(message);
    }*/
}