package com.kql.caffein.controller;

import com.kql.caffein.dto.Comment.CommentReqDto;
import com.kql.caffein.dto.Comment.CommentResDto;
import com.kql.caffein.dto.FollowDto;
import com.kql.caffein.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "댓글: 등록, 삭제, 조회, 좋아요")
@Slf4j
@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class CommentController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{feedNo}/{userNo}")
    @ApiOperation(value = "피드에 달린 댓글 전체 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "feedNo", value = "피드 번호", required = true,
                    dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "path")
    })
    public ResponseEntity listComment(@PathVariable int feedNo, @PathVariable String userNo) {
        try {
            List<CommentResDto> list = commentService.commentList(userNo, feedNo);
            if(list.isEmpty())
                return new ResponseEntity<>("댓글이 존재하지 않습니다.",HttpStatus.OK);
            else
                return new ResponseEntity<>(list,HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping
    @ApiOperation(value = "피드에 달린 댓글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "feedNo", value = "피드 번호", required = true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lastCommentNo", value = "마지막 댓글 번호", required = false,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "화면에 보여질 사이즈", required = true,
                    dataType = "int", paramType = "query")
    })
    public ResponseEntity listComment (@RequestParam(value = "userNo") String userNo, @RequestParam int feedNo,
                                       @RequestParam(required = false) Integer lastCommentNo, @RequestParam int size) {
        try {
            List<CommentResDto> list = commentService.pageComment(userNo, feedNo, lastCommentNo, size);
            if(list.isEmpty())
                return new ResponseEntity<>("댓글이 존재하지 않습니다.",HttpStatus.OK);
            else
                return new ResponseEntity<>(list,HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @ApiOperation(value = "댓글 등록")
    public ResponseEntity writeComment (@Validated @RequestBody CommentReqDto commentDto) {
        try {
            commentService.insertComment(commentDto);
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nested")
    @ApiOperation(value = "댓글에 달린 대댓글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "parentNo", value = "부모 댓글 번호", required = true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lastCommentNo", value = "마지막 댓글 번호", required = false,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "화면에 보여질 사이즈", required = true,
                    dataType = "int", paramType = "query")
    })
    public ResponseEntity listNestedComment (@RequestParam(value = "userNo") String userNo, @RequestParam int parentNo,
                                       @RequestParam(required = false) Integer lastCommentNo, @RequestParam int size) {
        try {
            List<CommentResDto> list = commentService.pageNestedComment(userNo, parentNo, lastCommentNo, size);
            if(list.isEmpty())
                return new ResponseEntity<>("댓글이 존재하지 않습니다.",HttpStatus.OK);
            else
                return new ResponseEntity<>(list,HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userNo}/{commentNo}")
    @ApiOperation(value = "댓글 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "commentNo", value = "댓글 번호", required = true,
                    dataType = "int", paramType = "path")
    })
    public ResponseEntity deleteComment (@PathVariable String userNo, @PathVariable int commentNo) {
        try {
            commentService.deleteComment(userNo, commentNo);
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/like/{userNo}/{commentNo}")
    @ApiOperation(value = "댓글 좋아요 / 좋아요 취소")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "commentNo", value = "댓글 번호", required = true,
                    dataType = "int", paramType = "path")
    })
    public ResponseEntity likeComment (@PathVariable String userNo, @PathVariable int commentNo){
        try {
            commentService.likeComment(userNo, commentNo);
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/likeUserList")
    @ApiOperation(value = "댓글 좋아요누른 회원 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNo", value = "회원 고유 번호", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "commentNo", value = "댓글 번호", required = true,
                    dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lastUserNo", value = "마지막 회원 고유 번호", required = false,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "화면에 보여질 사이즈", required = true,
                    dataType = "int", paramType = "query")
    })
    public ResponseEntity commentLikeUserList (@RequestParam(value = "userNo") String userNo, @RequestParam int commentNo,
                                       @RequestParam(required = false) String lastUserNo, @RequestParam int size) {
        try {
            List<FollowDto> list = commentService.commentLikeUserList(userNo, commentNo, lastUserNo, size);
            return new ResponseEntity<>(list,HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}