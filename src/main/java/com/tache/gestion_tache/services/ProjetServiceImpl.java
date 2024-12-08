package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProjetServiceImpl implements ProjectService {
    private final   ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final MailSender mailSender;

    @Override
    public Project addproject(UserDetails userDetails,Project project){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));
        project.setOwner(user);
        project.setStartDate(new Date());
        project.getUsers().add(user);
        //sendWelcomeEmail(project);
        return  projectRepository.save(project);

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
