/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldenkids.springboot.web.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lisandroscofano
 */
@Controller
public class ErrorController {
    
    @RequestMapping("/error_403")
    public String error403(){
        
        return "error_403";
    }
    
}
