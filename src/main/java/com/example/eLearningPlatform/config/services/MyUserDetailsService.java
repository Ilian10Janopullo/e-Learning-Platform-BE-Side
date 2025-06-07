package com.example.eLearningPlatform.config.services;

import com.example.eLearningPlatform.config.entities.UserPrincipal;
import com.example.eLearningPlatform.models.entities.User;
import com.example.eLearningPlatform.repositories.AdminRepository;
import com.example.eLearningPlatform.repositories.LecturerRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private AdminRepository adminRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = null;

        if(studentRepository.findByUsername(username).isPresent()){

            user = studentRepository.findByUsername(username).get();

        } else if(lecturerRepository.findByUsername(username).isPresent()){

            user = lecturerRepository.findByUsername(username).get();

        } else if(adminRepository.findByUsername(username).isPresent()){

            user = adminRepository.findByUsername(username).get();

        } else {

            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        return new UserPrincipal(user);
    }
}