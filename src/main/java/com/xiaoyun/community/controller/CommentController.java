package com.xiaoyun.community.controller;

import com.xiaoyun.community.dto.CommentCreateDTO;
import com.xiaoyun.community.dto.CommentDTO;
import com.xiaoyun.community.dto.ResultDTO;
import com.xiaoyun.community.enums.CommentTypeEnum;
import com.xiaoyun.community.exception.CustomizeErrorCode;
import com.xiaoyun.community.model.Comment;
import com.xiaoyun.community.model.User;
import com.xiaoyun.community.service.CommemtService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private CommemtService commemtService;

    @PostMapping(value = "/comment")
    @ResponseBody
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(comment.getGmtModified());
        comment.setCommentator(user.getId());

        commemtService.insert(comment);
        return ResultDTO.okOf();
    }

    @GetMapping(value = "/comment/{id}")
    @ResponseBody
    public ResultDTO<List> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commemtService.listByTatgetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
