package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Article;
import project.board.entity.AuditEntity;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;
import project.board.params.ArticleSearchParam;
import project.board.service.dto.ArticleRegisterParam;
import project.board.service.validator.ArticleInquiryValidator;
import project.board.util.UploadFileUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceV2 {

    private final ArticleRepositoryJpa articleRepository;
    private final MemberRepositoryJpa memberRepository;
    private final UploadFileService uploadFileService;
    private final UploadFileUtils uploadFileUtils;

    private final ArticleInquiryValidator inquiryValidator;
    @Value("${image.temp-storage.uri}")
    private String tempStorageUri;

    @Value("${image.storage.uri}")
    private String storageUri;

    public Page<Article> getArticlesByMember(Member member, ArticleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, AuditEntity.REGISTER_DATE));
        return articleRepository.findByMemberAndStatus(member, status, pageable);
    }

    public Page<Article> searchArticles(Category category, Nation nation, PageAndSort ps, String searchText) {
        ArticleSearchParam searchParam =
                ArticleSearchParam.builder()
                                  .category(category)
                                  .nation(nation)
                                  .searchText(searchText)
                                  .pageable(PageRequest.of(ps.getPage() - 1, ps.getSize()))
                                  .build();

        switch (ps.getSort()) {
            case NEWEST:
                return articleRepository.searchOrderByNewest(searchParam);
            case POPULAR:
                return articleRepository.searchOrderByPopular(searchParam);
        }

        return null;
    }

    public Article findMyTempArticle(Member member, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(
                () -> new IllegalArgumentException());

        inquiryValidator.checkTempArticle(article);
        inquiryValidator.checkArticleOwner(article, member);

        return article;
    }

    @Transactional
    public Long save(ArticleRegisterParam param) {
        Member member = memberRepository.findById(param.getMemberId())
                                        .orElseThrow(() -> new IllegalArgumentException());

        List<PostFile> postFiles = uploadFileService.createPostFileByFileNameIn(param.getImages());

        Article article = Article.create(param.getTitle(),
                                         param.getContent().replaceAll("src=\"" + tempStorageUri, "src=\"" + storageUri),
                                         param.getCategory(), param.getNation(), ArticleStatus.PERMANENT, member, postFiles);

        articleRepository.save(article);

        return article.getId();
    }

    @Transactional
    public Long saveFromTemp(ArticleRegisterParam param) {
        Article article = articleRepository.findById(param.getId())
                                           .orElseThrow(() -> new IllegalArgumentException());

        Member member = memberRepository.findById(param.getMemberId())
                                        .orElseThrow(() -> new IllegalArgumentException());

        inquiryValidator.checkTempArticle(article);
        inquiryValidator.checkArticleOwner(article, member);

        List<PostFile> postFiles = uploadFileService.createPostFileByFileNameIn(param.getImages());

        article.update(param.getTitle(),
                       param.getContent().replaceAll("src=\"" + tempStorageUri, "src=\"" + storageUri),
                       param.getCategory(), param.getNation(), ArticleStatus.PERMANENT, postFiles);

        return article.getId();
    }

    public void diskCopyFromTempStorage(List<String> images) {
        List<PostFile> postFiles = uploadFileService.createPostFileByFileNameIn(images);
        uploadFileUtils.tempFileCopyAsPostFile(postFiles);
    }
}
