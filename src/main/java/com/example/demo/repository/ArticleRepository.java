package com.example.demo.repository;

import com.example.demo.domain.Article;
import com.example.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ArticleRepository extends JpaRepository<Article,Long>{
        List<Article> findAllByWriter(Member member);
}
