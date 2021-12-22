//package project.board.controller;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import com.fasterxml.jackson.core.JsonProcessingException;
//
//import project.board.common.StringToMyInfoConverter;
//import project.board.domain.Member;
//import project.board.domain.dto.MemberDto;
//import project.board.service.ArticleService;
//import project.board.service.BookmarkService;
//import project.board.service.CommentService;
//import project.board.service.MemberService;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = { MemberController.class, StringToMyInfoConverter.class})
//public class MemberControllerTest {
//
//	@Autowired
//	MockMvc mockMvc;
//
//	@MockBean
//	MemberService memberService;
//
//	@MockBean
//	ArticleService articleService;
//
//	@MockBean
//	CommentService commentService;
//
//	@MockBean
//	BookmarkService bookmarkService;
//
//	private MemberDto memberDto;
//	private MockHttpSession session;
//	private String baseUrl;
//	private String baseViewName;
//
//	@BeforeEach
//	public void setUp() throws JsonProcessingException {
//		baseUrl = "/mypage";
//		baseViewName = "member/mypage/";
//		memberDto = MemberDto.builder().email("abc@naver.com").password("123").rePassword("123").build();
//		session = new MockHttpSession();
//		session.setAttribute("memberId", 1L);
//		session.setAttribute("email", "abc@naver.com");
//	}
//
//	/**
//	 * post 요청 시 검사할 요소들을 리팩토링하여 모아둠.
//	 * 리턴 값이 ResultActions이므로 chaining을 이용하여 추가적인 테스트가 가능하다.
//	 *
//	 * @param url : 요청을 보낼 url
//	 * @param expectedView : 컨트롤러가 반환할 것으로 예상되는 뷰 이름
//	 *
//	 * @return ResultActions
//	 */
//	public ResultActions postContent(String url, Object param, String expectedView) throws Exception {
//		return mockMvc.perform(post(baseUrl + url)
//				.session(session)
//				.flashAttr("memberDto", param)
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.accept(MediaType.TEXT_HTML))
//				.andExpect(view().name(baseViewName + expectedView));
//	}
//
//	/**
//	 * get 요청 시 비교할 요소들을 리팩토링하여 모아둠.
//	 * 리턴 값이 ResultActions이므로 chaining을 이용하여 추가적인 테스트가 가능하다.
//	 *
//	 * @param url : 요청을 보낼 url
//	 * @param expectedView : 컨트롤러가 반환할 것으로 예상되는 뷰 이름
//	 *
//	 * @return ResultActions
//	 */
//	public ResultActions getContent(String url, String expectedView) throws Exception {
//		return mockMvc.perform(get(baseUrl + url)
//				.session(session)
//				.accept(MediaType.TEXT_HTML))
//				.andExpect(status().isOk())
//				.andExpect(view().name(baseViewName + expectedView));
//	}
//
//	/**
//	 * GET	  : /mypage/auth
//	 * view	  : member/mypage/auth
//	 */
//	@Test
//	public void getAuthForm() throws Exception {
//		getContent("/auth", "auth");
//	}
//
//	/**
//	 * POST	  : /mypage/auth
//	 * @param : MemberDto memberDto
//	 * view	  : member/mypage/update
//	 */
//	@Test
//	public void isOwner() throws Exception {
//		when(memberService.login(memberDto)).thenReturn(new Member());
//
//		session.setAttribute("prevPage", "/mypage/update");
//		mockMvc.perform(post("/mypage/auth")
//				.flashAttr("memberDto", memberDto))
//		.andExpect(status().is3xxRedirection());
//
//	}
//
//	/**
//	 * GET	  : /mypage/update
//	 * view	  : member/mypage/update
//	 */
//	@Test
//	public void getMypageUpdateForm() throws Exception {
//		//Fail
//		getContent("/update", "auth");
//		//Success
//		session.setAttribute("isOwner", true);
//		getContent("/update", "update");
//	}
//
//	/**
//	 * POST	  : /mypage/update
//	 * @param : MemberDto memberDto
//	 * view	  : member/mypage/member
//	 */
//	@Test
//	public void updateMemberInfo() throws Exception {
//		//Fail
//		postContent("/update", memberDto, "auth")
//		.andExpect(status().isOk());
//
//		//Success
//		session.setAttribute("isOwner", true);
//		when(memberService.equalPassword(memberDto)).thenReturn(true);
//		mockMvc.perform(post("/mypage/update")
//				.session(session)
//				.flashAttr("memberDto", memberDto)
//				.accept(MediaType.TEXT_HTML))
//		.andExpect(status().is3xxRedirection())
//		.andExpect(view().name("redirect:/mypage/member"));
//	}
//
//
//
//}
