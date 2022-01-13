package project.board.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;

import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class DummyDataController {
    private final ArticleRepositoryJpa articleRepository;
    private final MemberRepositoryJpa memberRepository;

    RestTemplate restTemplate = new RestTemplate();
    Random random = new Random();

    @GetMapping(value = "data")
    public ResponseEntity<?> insert(int size) {
        Member member = memberRepository.findAll().get(0);

        String url = "http://hangul.thefron.me/api/generator";
        ArrayList<Article> list = new ArrayList<>();
        ArrayList<String> length = new ArrayList<>();
        length.add("long");
        length.add("medium");
        length.add("short");

        for (int i = 0; i < size; i++) {
            URI uri = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("paragraphs", random.nextInt(9)+1)
                    .queryParam("text_source_ids[]", random.nextInt(2)+1)
                    .queryParam("length", length.get(random.nextInt(length.size())))
                    .build().toUri();
            TheFron response = restTemplate.postForObject(uri, null, TheFron.class);

            Article article = Article.create(response.ipsum.substring(0, 20), response.ipsum, Category.ATTRACTIONS, Nation.KR, ArticleStatus.PERMANENT, member);
            list.add(article);
        }
        articleRepository.saveAll(list);

        return ResponseEntity.ok().build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TheFron {
        private String ipsum;
    }
}
