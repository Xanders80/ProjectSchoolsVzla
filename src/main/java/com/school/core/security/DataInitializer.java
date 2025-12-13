/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025 [Tu Nombre o Empresa]
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.school.core.security;

import com.school.core.entity.Role;
import com.school.core.entity.User;
import com.school.core.repository.RoleRepository;
import com.school.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize default roles
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(new Role("ADMIN", "Administrador del sistema"));
            }
            if (roleRepository.findByName("DIRECTOR").isEmpty()) {
                roleRepository.save(new Role("DIRECTOR", "Director de la institución"));
            }
            if (roleRepository.findByName("TEACHER").isEmpty()) {
                roleRepository.save(new Role("TEACHER", "Profesor"));
            }
            if (roleRepository.findByName("STAFF").isEmpty()) {
                roleRepository.save(new Role("STAFF", "Personal administrativo"));
            }
            if (roleRepository.findByName("STUDENT").isEmpty()) {
                roleRepository.save(new Role("STUDENT", "Estudiante"));
            }
            if (roleRepository.findByName("PARENT").isEmpty()) {
                roleRepository.save(new Role("PARENT", "Padre de familia"));
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("P@ssw0rd123!"));
                admin.setRole(com.school.core.enums.Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                logger.info("Default Admin user created: admin / P@ssw0rd123!");
            }
        };
    }
}
