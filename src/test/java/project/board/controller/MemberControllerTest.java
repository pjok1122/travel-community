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
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.board.domain.Member;
import project.board.dto.MemberDto;
import project.board.service.MemberService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {MemberController.class})
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MemberService memberService;
	
	@Autowired
	ObjectMapper objectMapper;
	
	
	private String email;
	private String password;
	private MemberDto memberDto;
	private String requestBody;
	
	@BeforeEach
	public void setUp() throws JsonProcessingException {
		email = "pjok1122@naver.com";
		password = "password";
		memberDto = new MemberDto(email,password,password);
		requestBody = objectMapper.writeValueAsString(memberDto);
	}
	
	public ResultActions postContent(String url, String content) throws Exception {
		return mockMvc.perform(post(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.TEXT_HTML));
	}
	
	@Test
	public void postLoginSuccessTest() throws Exception {
		
		//Mocking
		when(memberService.login(memberDto))
		.thenReturn(Member.builder().email(email).build());
		
		postContent("/login", requestBody)
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/index"));
		
		
		//세션 테스트
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("member", Member.builder().email(email).build());
		Member member = (Member) session.getAttribute("member");
		assertThat(member).isNotNull();
		assertThat(member.getEmail()).isEqualTo(email);
	}
	
	@Test
	public void postLoginFailTest() throws Exception{
		//Mocking
		when(memberService.login(memberDto))
		.thenReturn(null);
		
		// POST : /login
		postContent("/login", requestBody)
		.andExpect(status().isOk())
		.andExpect(view().name("member/login"))
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
	@Test
	public void postLoginValidate() throws Exception {
		MemberDto memberDto = MemberDto.builder().email("email").build();
		String errorRequestBody = objectMapper.writeValueAsString(memberDto);
		
		postContent("/login", errorRequestBody)
		.andExpect(status().isOk())
		.andExpect(view().name("member/login"))
		.andExpect(model().attribute("error", is(notNullValue())));
	}
	
	@Test
	public void registerMemberSuccess() throws Exception {
		when(memberService.existMember(memberDto)).thenReturn(false);
		when(memberService.equalPassword(memberDto)).thenReturn(true);
		when(memberService.save(memberDto)).thenReturn(new Member());
		
		
		postContent("/register", requestBody)
		.andExpect(status().is3xxRedirection())
		.andExpect(view().name("redirect:/index.html"));
	}
	
}
