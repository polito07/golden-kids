/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.services;

import com.goldenkids.springboot.web.app.controllers.AlumnoController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author lisandroscofano
 */
@Service
public class UploadService {

    org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    public String cargarArchivo(MultipartFile file) {

        String uniqueFileName = null;

        if (!file.isEmpty()) {

            uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path roothPath = Paths.get("uploads").resolve(uniqueFileName);
            Path absolutePath = roothPath.toAbsolutePath();

            log.info("path: " + roothPath);
            log.info("absolutepath: " + absolutePath);

            try {
                Files.copy(file.getInputStream(), absolutePath);
            } catch (IOException ex) {
                Logger.getLogger(AlumnoController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return uniqueFileName;

        } else {
            return null;
        }

    }

}
