package com.xiaoyun.community.controller;

import com.xiaoyun.community.dto.PaginationDTO;
import com.xiaoyun.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        HttpServletRequest request
    ) {
        PaginationDTO pagination = questionService.list(search, page, size);
        model.addAttribute("search", search);
        request.getSession().setAttribute("search", search);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
