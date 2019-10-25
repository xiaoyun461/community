package com.xiaoyun.community.controller;

import com.xiaoyun.community.dto.CommentDTO;
import com.xiaoyun.community.dto.QuestionDTO;
import com.xiaoyun.community.enums.CommentTypeEnum;
import com.xiaoyun.community.service.CommemtService;
import com.xiaoyun.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired()
    private QuestionService questionService;

    @Autowired
    private CommemtService commemtService;

    @GetMapping("/question/{id}")
    private String question(@PathVariable(name = "id") Long id, Model model) {
        QuestionDTO questionDTO = questionService.getById(id);
        List<CommentDTO> comments = commemtService.listByTatgetId(id, CommentTypeEnum.QUESTION);

        //累加阅读数
        questionService.incVic(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);

        return "question";
    }
}
