package project.board.service


import org.thymeleaf.util.StringUtils
import project.board.entity.Comment
import project.board.entity.Member
import project.board.jpa.ArticleRepositoryJpa
import project.board.jpa.CommentLikeRepositoryJpa
import project.board.jpa.CommentRepositoryJpa
import project.board.jpa.MemberRepositoryJpa
import spock.lang.Specification

import java.time.LocalDateTime

class CommentServiceV2Test extends Specification {

    CommentServiceV2 service
    def commentRepository = Mock(CommentRepositoryJpa.class)
    def articleRepository = Mock(ArticleRepositoryJpa.class)
    def memberRepository = Mock(MemberRepositoryJpa.class)
    def commentLikeRepository = Mock(CommentLikeRepositoryJpa.class)

    def setup() {
        service = new CommentServiceV2(commentRepository, articleRepository, memberRepository, commentLikeRepository)
    }

    def "[lengthLimit] null"() {
        given:
        String content = null

        when:
        def result = service.cutLength(content)

        then:
        result == null
    }

    def "[lengthLimit] 300글자 이상"() {
        given:
        String content = StringUtils.repeat("*", 400);

        when:
        def result = service.cutLength(content)

        then:
        result.length() == 300
    }

    def "[delete] 자식만 삭제"() {
        given:
        def member = new Member(1, "test@email.com", "pass", "salt", null, "user")
        def parent = new Comment(id: 1L, article: null, member: member, parent: null, content: "parent")
        def child = new Comment(id: 2L, article: null, member: member, parent: parent, content: "child")

        memberRepository.findById(member.id) >> Optional.of(member)
        commentRepository.findByMemberAndId(member, child.id) >> Optional.of(child)
        commentRepository.findById(child.getParent().id) >> Optional.empty()

        when:
        service.delete(child.id, member.id)

        then:
        1 * commentRepository.delete(_)
    }

    def "[delete] 자식 삭제할 때 삭제된 부모도 삭제"() {
        given:
        def member = new Member(1, "test@email.com", "pass", "salt", null, "user")
        def parent = new Comment(id: 1L, article: null, member: member, parent: null, content: "parent", updateDate: LocalDateTime.now())
        def child = new Comment(id: 2L, article: null, member: member, parent: parent, content: "child")

        memberRepository.findById(member.id) >> Optional.of(member)
        commentRepository.findByMemberAndId(member, child.id) >> Optional.of(child)
        commentRepository.findById(child.getParent().id) >> Optional.of(parent)

        when:
        service.delete(child.id, member.id)

        then:
        2 * commentRepository.delete(_)
    }

    def "[delete] 자식 없는 부모 삭제"() {
        given:
        def member = new Member(1, "test@email.com", "pass", "salt", null, "user")
        def parent = new Comment(id: 1L, article: null, member: member, parent: null, content: "parent", updateDate: LocalDateTime.now())

        memberRepository.findById(member.id) >> Optional.of(member)
        commentRepository.findByMemberAndId(member, parent.id) >> Optional.of(parent)

        when:
        service.delete(parent.id, member.id)

        then:
        1 * commentRepository.delete(_)
        0 * parent.parentDelete(_)
    }

    def "[delete] 자식 있는 부모 삭제"() {
        given:
        def member = new Member(1, "test@email.com", "pass", "salt", null, "user")
        def parent = new Comment(id: 1L, article: null, member: member, parent: null, content: "parent", updateDate: LocalDateTime.now())
        def child = new Comment(id: 2L, article: null, member: member, parent: parent, content: "child")
        parent.getChildren().add(child)

        memberRepository.findById(member.id) >> Optional.of(member)
        commentRepository.findByMemberAndId(member, parent.id) >> Optional.of(parent)

        when:
        service.delete(parent.id, member.id)

        then:
        0 * commentRepository.delete(_)
    }
}
