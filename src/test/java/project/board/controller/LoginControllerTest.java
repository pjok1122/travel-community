package project.board.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.board.domain.Member;
import project.board.domain.dto.MemberDto;
import project.board.service.MemberService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {LoginController.class})
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MemberService memberService;
	
	private MemberDto memberDto;
	private MemberDto invalidMemberDto;
	private MockHttpSession session;
	
	@BeforeEach
	public void setUp() throws JsonProcessingException {
		String email = "pjok1122@naver.com";
		String password = "password";
		
		memberDto = new MemberDto(email,password,password);
		invalidMemberDto = new MemberDto();
		session = new MockHttpSession();
		session.setAttribute("memberId", 1L);
		
	}
	
	public ResultActions postContent(String url, Object param, int status, String expectedViewName) throws Exception {
		return mockMvc.perform(post(url)
				.flashAttr("memberDto", param)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.TEXT_HTML))
				.andExpect(status().is(status))
				.andExpect(view().name(expectedViewName));
	}
	
	/**
	 * POST   : /login
	 * @param : memberDto
	 */
	@Test
	public void postLoginSuccessTest() throws Exception {
		//Mocking
		when(memberService.login(memberDto))
		.thenReturn(Member.builder().email("email").build());
		
		postContent("/login", memberDto, 302, "redirect:/");
	}
	
	/**
	 * POST   : /login
	 * @param : memberDto
	 */	
	@Test
	public void postLoginFailTest() throws Exception{
		when(memberService.login(memberDto)).thenReturn(null);
		
		postContent("/login", memberDto, 200, "member/login")
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
	/**
	 * POST   : /login
	 * @param : invalidMemberDto
	 */
	@Test
	public void postLoginValidateFail() throws Exception {		
		postContent("/login", invalidMemberDto, 200, "member/login")
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
	/**
	 * POST   : /register
	 * @param : memberDto
	 */
	
	@Test
	public void registerMemberSuccess() throws Exception {
		when(memberService.existMember(memberDto)).thenReturn(false);
		when(memberService.equalPassword(memberDto)).thenReturn(true);
		when(memberService.save(memberDto)).thenReturn(new Member());
		
		postContent("/register", memberDto, 302, "redirect:/");
	}
	
	/**
	 * POST   : /register
	 * @param : invalidMemberDto
	 */
	@Test
	public void registerMemberValidateFail() throws Exception {
		postContent("/register", invalidMemberDto, 200, "member/register")
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
	/**
	 * POST   : /register
	 * @param : memberDto
	 */
	@Test
	public void registerMemberFail() throws Exception {
		when(memberService.existMember(memberDto)).thenReturn(true);
		
		postContent("/register", memberDto, 200, "member/register")
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
}
