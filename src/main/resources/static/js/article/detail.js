function getCommentModalForm(obj, commentId){
	$.ajax({
		url : '/ajax/login_check',
		type : 'GET',
		success: function(data){
			var writer = $(obj).parents('.media-body').children('h6.mt-0').children('.comment-writer').html();
			var content = $(obj).parents('.media-body').children('.comment-content').html();
			$('#commentReportWriter').html(`작성자 : ${writer}`);
			$('#commentReportContent').html(`내용 : ${content.length <= 100 ? `${content.length}` : `${content.substr(0,100)} ...`}`);
			$('#commentReportId').val(commentId);
			$('#commentReportModal').modal('show');
			
		},
		complete : function(response){
			if(response.status == 302){
				alert("로그인이 필요한 작업입니다.");
				window.location.href=response.responseText;
			}
		}
	});
}

$(document).ready(function(){
	var email = getSessionEmail();
	
	var article_writer = document.getElementById('email').innerText;
	var articleId =  document.getElementById('articleId').innerText;
	
	getCommentList(articleId);
	
	if(email == null)
	{
		document.getElementById('commentInsert').style.display = 'none';
	}	
	
	$("#comment-insert").on("click", function(){
		commentInsert(articleId, $("#commentContent").val(), '');
	});
	
	document.getElementById('commentList').onclick = function(event){
		var current = event.target;
		var parent = current.parentElement;
		var commentId = parent.getAttribute("name");

		if(current.innerText == '삭제')
		{
//			console.log(current.innerText);
			commentDelete(commentId);
		}	
		else if(current.innerText == '등록')
		{
			var content = parent.parentElement.getElementsByTagName("textarea")[0].value;
			console.log(content);
			if(content.length <= 0){
				alert("내용을 입력해주세요.");
			}
			else{
				commentInsert(articleId, content , commentId);
			}
		}	
		else if(parent.getAttribute("name") == 'comment-like')
		{
			//console.log(parent.parentElement.getAttribute("name"));
			commentLikeInsert(parent.parentElement.getAttribute("name"));
		}
		else if(current.innerText == '댓글달기')
		{
			if(email == null)
			{
				alert("로그인이 필요한 작업입니다.");
				window.location.href='/login';
				return;
			}	
			
			var reply = document.getElementById('comment-reply')
			
			var html =
				`
				<div class="container pb-cmnt-container" id="comment-reply">
				    <div class="row" style="background-color:#F8F8F8">
				        <div class="col-md-10 col-md-offset-1">
				            <div class="panel panel-default">
				                <div class="panel-body" name='${commentId}'>
									<span><i class="mr-1 fas fa-user"></i><strong>${email}</strong></span>
				                    <textarea placeholder="댓글을 입력해주세요." class="pb-cmnt-textarea" id="commentContent" name="commentContent"
				                    onkeyup="checkLength(this,$('#reply-len-count'), 300)"></textarea>
			                    	<span id="reply-len-count">(0 / 300 자)</span>
			                        <button class="btn btn-primary pull-right" type="button" id="comment-insert">등록</button>
				                </div>
				            </div>
				        </div>
				    </div>
				</div>
				`
			if(reply == null)
			{
				parent.insertAdjacentHTML('afterend', html)
			}	
			else
			{
				if (parent.parentElement != reply.parentElement)
				{
					parent.insertAdjacentHTML('afterend', html)
				}
				reply.remove();
			}	
		}	
	};
	
	
	function commentReportForm(content, writer){
		$("#commentReportContent").html(`내용 : ${content.substr(0,4)}`);
		$("#commentReportWriter").html(`작성자 : ${writer}`);
		$("#commentReportModal").modal('show');
	}
	
	function getSessionEmail()
	{
		var email;
		
		$.ajax({
			url : '/ajax/login_check',
			type : 'GET',
			async: false,
			success: function(data){
				email = data;
			},
			complete : function(response){
				if(response.status == 302){
//					alert("로그인이 필요한 작업입니다.");
//					window.location.href=response.responseText;
				}
			}
		});
		
		return email;
	}
	
	function commentLikeInsert(commentId)
	{
		$.ajax({
			url: '/comment/like',
			type : 'POST',
			data : {
	        	'commentId': commentId
	        },
	        success : function(likeCount){
//	        	getCommentList(articleId);
//	        	getCommentGood();
	        	$(`[name=comment-like-count${commentId}]`).html(likeCount);
	        	likeImageToggle($(`[name=comment-like-img${commentId}]`));
	        }, 
	        complete : function(response){
				if(response.status == 302){
					alert("로그인이 필요한 작업입니다.");
					window.location.href=response.responseText;
				}
			}
		});
	}
	
	function likeImageToggle(obj){
		if( obj.prop("src").indexOf("like2.png") > 0){
			obj.prop("src", "/img/imgs/like1.png");
		} else{
			obj.prop("src", "/img/imgs/like2.png");
		}
	}
	
	function commentInsert(articleId, content, parentCommentId){
		$.ajax({
			url: '/comment',
			type : 'POST',
			data : {
	        	'articleId': articleId,
	        	'content': content, 
	        	'parentCommentId': parentCommentId
	        },
	        success : function(data){
	        	getCommentList(articleId);
	        	document.getElementById('commentContent').value = ''
	        }, 
	        complete : function(response){
				if(response.status == 302){
					alert("로그인이 필요한 작업입니다.");
					window.location.href=response.responseText;
				}
			}
		});
	}
	
	function commentDelete(commentId){
	    $.ajax({
	        url : '/comment/delete/'+commentId,
	        type : 'post',
	        success : function(data){
	        	getCommentList(articleId);
	        }, 
	        complete : function(response){
				if(response.status == 302){
					alert("로그인이 필요한 작업입니다.");
					window.location.href=response.responseText;
				}
			}
	    });
	}
	

	
	function getCommentList(articleId){
	    $.ajax({
	        type:'GET',
	        url : "/comment",
	        data : {
	        	'articleId': articleId
	        },
	        success : function(data){
	        	console.log(data);
	        	var count = data.length;
	            var html = "";
	            
	            if(data.length > 0){
	                for(i=0; i<data.length; i++){
	                	count += data[i].replies.length;
	                	html +=
	                		`
	                		<div class="media">
              					<i class="mr-3 fas fa-user fa-2x"></i>
	                			<div class="media-body">
		                			<h6 class="mt-0">
		                				<strong class="comment-writer">${data[i].writer == null ? `탈퇴한 회원입니다. </strong>` : `${data[i].writer} </strong>
		                				&nbsp ${data[i].registerDate}
		                				${data[i].writer == article_writer ? `<button style="background: #ff7e00; border-color: #ff7e00; color:white;" class="btn btn-sm">글쓴이</button>` : ''}`
		                				}
	                				</h6>
	                				<span class="comment-content">${data[i].updateDate != null ? '삭제된 댓글입니다' : `${data[i].content}`}</span>
	                				${data[i].updateDate != null ? '' : 
	                				`
	                				<div class="mt-2" name='${data[i].id}'>
		                				<button class="icon" name='comment-like'>
	                						<img name="comment-like-img${data[i].id}" ${data[i].isGood ? `" src="/img/imgs/like2.png">`: `src="/img/imgs/like1.png">`}
	                					</button>
	                					<span class="comment-like-count" name="comment-like-count${data[i].id}">${data[i].good}</span>
	                					
		                				${data[i].updateDate == null ? `&nbsp<span class="text-primary comment-event">댓글달기</span>` : ''}
	
		                				${data[i].writer == email ? `&nbsp<span class="text-danger comment-event">삭제</span>` : ''}
		                				${data[i].writer == email ? '': `<button class="icon icon-comment-report" onclick="getCommentModalForm(this, ${data[i].id})"><img class="report" src="/img/imgs/report.png">`}
	                				</div>
	                				`}
	                			
	                			${data[i].replies.map(reply =>
	                				`
                					<div class='media mt-3'>
                						<i class="mr-3 fas fa-user fa-2x"></i>
                						<div class="media-body">
			                				<h6 class="mt-0">
			                					<strong class="comment-writer"> ${reply.writer == null ? `탈퇴한 회원입니다. </strong>` : `${reply.writer} </strong>&nbsp ${reply.registerDate}
			                					${reply.writer == article_writer ? `<button style="background: #ff7e00; border-color: #ff7e00; color:white;" class="btn btn-sm">글쓴이</button>` : ''}`}
			                				</h6>
		                					<span class="comment-content">${reply.content}</span>
			                				<div class='mt-2' name='${reply.id}'>
			                					<button class="icon" name='comment-like'>
			                						<img name="comment-like-img${reply.id}" ${reply.isGood ? `src="/img/imgs/like2.png">` : `src="/img/imgs/like1.png">`}
			                					</button>
			                					<span class="comment-like-count" name="comment-like-count${reply.id}">${reply.good}</span>
			                					${reply.writer == email ? `&nbsp<span class="text-danger comment-event">삭제</span>` : ''}
			                					${reply.writer == email ? '': `<button class="icon icon-comment-report" onclick="getCommentModalForm(this, ${reply.id})"><img class="report" src="/img/imgs/report.png">`}
			                				</div>
		                				</div>
		                			</div>
	                				`
	                			).join("")}
	                			<div class="mt-2 mb-2" style='border-bottom: 1px dotted #ccc'></div>
                			</div>
                		</div>
	                		`
	                }
	            } else {
	            	html += 
	            	`
	            	<div>
	            		<div><h6><strong>등록된 댓글이 없습니다.</strong></h6></div>
	            	</div>
	            	`
	            }
	            $(".commentCount").html(count);
	            $("#commentList").html(html);
	        }
	    });
	}
});