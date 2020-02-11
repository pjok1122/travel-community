## LoginController

로그인과 회원가입에 대한 컨트롤러

### GET /register : 회원가입 페이지를 보여준다.

- method    : `getRegisterForm()`
- view		: `member/register`

### POST /register : 회원가입을 처리한다.

- method       		: `registerMember()`
- parameter 		: `MemberDto`

**success**

- view 			: `index`

**fail**

- view			: `member/register`
- model			: `String error`

### GET /login : 로그인 페이지를 보여준다.

- method       		: `getLoginForm()`
- view 				: `member/login`

### POST /login : 로그인을 처리한다.

- method       		: `login()`
- parameter 		: `MemberDto`

**success**
- view			: `index`

**fail**				
- view			: `member/login`
- model			: `String error`

### GET /logout : 로그아웃을 처리한다.

- method       		: `logout()`
- view				: `index`

<br><hr>

## MemberController

### GET /mypage : 마이 페이지를 보여준다.

- method       		: `getMypage()`
- parameter			: `MyInfo info `

**success**

- `/mypage?info=member`
  - view			: `member/mypage/member`
  - model			: `Member member`, `int like`

- `/mypage?info={article|bookmark}`
  - view			: `member/mypage/{article|bookmark}`
  - model			: `Article article`, `int commentCount`, `Page page` 

- `/mypage?info=comment`
  - view			: `member/mypage/comment`
  - model			: `Comment comment`, `Page page`

- `/mypage?info=temp_article`
  - view			: `member/mypage/temp_article`
  - model			: `Article article`
  
**fail**

- view			: `member/login`
  
### GET /mypage/auth : 회원정보 변경 전에 회원 인증 페이지를 보여준다.

- method		: `getAuthForm()`

**success**

- view          : `member/mypage/auth`

**fail**

- view          : `member/login`

### POST /mypage/auth : 회원정보 변경 전에 회원 인증을 처리한다.

- method        : `isOwner()`

**success**

- view          : `member/mypage/update`

**fail**

- view          : `member/mypage/auth`

### GET /mypage/update : 마이 페이지의 회원정보 변경 페이지를 보여준다.

- method       		: `getMypageUpdateForm()`

**success**	

- view              : `member/mypage/update`

**fail**

- view	        	: `member/login`

### POST /mypage/update : 회원 정보 변경을 처리한다. 

- method       		: `updateMemberInfo()`
- parameter 		: `MemberDto memberDto`

**success**

- view      		: `member/mypage/member`

**fail**

- view      		: `member/mypage/update`