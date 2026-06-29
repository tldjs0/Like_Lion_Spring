package com.example.demo.controller;

import com.example.demo.DTO.ArticleDTO;
import com.example.demo.domain.Article;
import com.example.demo.service.ArticleService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController // REST API 컨트롤러로 등록
@RequiredArgsConstructor //final 필드의 생성자를 자동 생성
@RequestMapping("/api") // 공통 URL 경로 설정
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtil jwtUtil;


    @PostMapping("/article/add")
    public ArticleDTO.ArticleRes createArticle(
            @RequestHeader("Authorization") String token,
            @RequestBody ArticleDTO.AddArticleReq request){
        if(!jwtUtil.validateJwt(token)){
            return null;
        }
        Article article = articleService.addArticle(token,request.getTitle(),request.getContent());
        return  new ArticleDTO.ArticleRes(article);
    }

    @PutMapping("/article/update")
    public ArticleDTO.ArticleRes updateArticle(
            @RequestHeader("Authorization") String token,
            @RequestBody ArticleDTO.ArticleReq request){
        if(!jwtUtil.validateJwt(token)){
            return null;
        }
        Article article = articleService.updateArticle(request.getArticleId(),request.getTitle(),request.getContent(),token);
        return new ArticleDTO.ArticleRes(article);
    }

    @DeleteMapping("/article/{articleId}")
    public String deleteArticle(
            @RequestHeader("Authorization") String token,
            @PathVariable("articleId") Long articleId) {
        if (!jwtUtil.validateJwt(token)) {
            return null;
        }
        return articleService.deleteArticle(articleId, token);
    }

    @GetMapping("/article/{articleId}")
    public ArticleDTO.ArticleRes getArticle(@PathVariable("articleId") Long articleId){
        Article article = articleService.findById(articleId);
        return new ArticleDTO.ArticleRes(article);
    }

    @GetMapping("/articles/all")
    public List<ArticleDTO.ArticleRes> allArticleList(){
        List<ArticleDTO.ArticleRes> responseArticles = new ArrayList<>();
        for (Article article : articleService.findAll()){
            responseArticles.add(new ArticleDTO.ArticleRes(article));
        }
        return responseArticles;
    }

    @GetMapping("/articles/all/{memberId}")
    public List<ArticleDTO.ArticleRes> writerArticleList(@PathVariable("memberId") String memberId){
        List<ArticleDTO.ArticleRes> responseArticles = new ArrayList<>();
        for (Article article : articleService.findAll()){
            responseArticles.add(new ArticleDTO.ArticleRes(article));
        }
        return responseArticles;
    }
}
