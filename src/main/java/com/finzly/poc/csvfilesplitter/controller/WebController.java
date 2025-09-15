package com.finzly.poc.csvfilesplitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "CSV File Splitter");
        return "index";
    }
    
    @GetMapping("/splitter")
    public String fileSplitter(Model model) {
        model.addAttribute("title", "CSV File Splitter - Upload & Split");
        return "index";
    }
}
