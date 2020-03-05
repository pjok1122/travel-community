$(document).ready(function(){
	var email = getSessionEmail();
	
	var article_writer = document.getElementById('email').innerText;
	var articleId =  document.getElementById('articleId').innerText;
	document.getElementById('writer').innerText = email;
	
	getCommentList(articleId);
	
	$("#comment-insert").on("click", function(){
		commentInsert(articleId, $("#commentContent").val(), '');
	});
	
	document.getElementById('commentList').onclick = function(event){
		var current = event.target;
		var parent = current.parentElement;
		var commentId = parent.getAttribute("name");
		
		console.log(current);
		
		if(current.innerText == '삭제')
		{
			console.log(current.innerText);
			commentDelete(commentId);
		}	
		else if(current.innerText == '등록')
		{
			commentInsert(articleId, parent.parentElement.getElementsByTagName("textarea")[0].value, commentId);
		}	
		else if(parent.getAttribute("name") == 'comment-like')
		{
			//console.log(parent.parentElement.getAttribute("name"));
			commentLikeInsert(parent.parentElement.getAttribute("name"));
		}
		else if(current.innerText == '댓글달기')
		{
			var reply = document.getElementById('comment-reply')
			
			var html =
				`
				<div class="container pb-cmnt-container" id="comment-reply">
				    <div class="row" style="background-color:#F8F8F8">
				        <div class="col-md-10 col-md-offset-1">
				            <div class="panel panel-default">
				                <div class="panel-body" name='${commentId}'>
									<span><strong>${email}</strong></span>
				                    <textarea placeholder="댓글을 입력해주세요" class="pb-cmnt-textarea" name="commentReplyContent"></textarea>
			                        <button class="btn btn-primary pull-right" type="button">등록</button>
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
	            var html = "";
	            
	            if(data.length > 0){
	                for(i=0; i<data.length; i++){
	                	html +=
	                		`
	                		<div>
	                			<div>
	                				<h6>
	                					<strong> ${data[i].writer} </strong>&nbsp ${data[i].registerDate}
	                					${data[i].writer == article_writer ? `&nbsp[글쓴이]` : ''}
	                				</h6>
	                				${data[i].updateDate == null ? `${data[i].content}` : '삭제된 댓글입니다'}
	                			</div>
	                			<div class='col' name='${data[i].id}'>
	                				<button class="icon" name='comment-like'>
                						${data[i].isGood ? `<img src="/img/imgs/like2.png">`: `<img src="/img/imgs/like1.png">`}
                					</button>
                					
	                				${data[i].updateDate == null ? `&nbsp<a href='#'>댓글달기</a>` : ''}

	                				${data[i].writer == email ? `&nbsp<a href='#'>삭제</a>` : ''}
	                				${data[i].writer == email ? '':`${data[i].writer == email ? '':`<a class='icon' onclick='$('#exampleModal').modal('show')'>신고</a>`}`}
	                				
	                				<!--
			                		<button type="button" class="btn btn-default" aria-label="Left Align">
			                			<span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>
			                		</button>
			                		<button type="button" class="btn btn-default" aria-label="Left Align">
			                			<span class="glyphicon glyphicon-comment"></span>
			                		</button>
			                		<button type="button" class="btn btn-default" aria-label="Left Align">
			                			<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
			                		</button>
			                		-->
		                		
	                			${data[i].replies.map(reply =>
	                				`
                					<div class='container'>
		                				<h6>
		                					<strong> ${reply.writer} </strong>&nbsp ${reply.registerDate}
		                					${reply.writer == article_writer ? `&nbsp[글쓴이]` : ''}
		                				</h6>
		                				${reply.content}
		                				<div class='col' name='${reply.id}'>
		                					<button class="icon" name='comment-like'>
		                						${reply.isGood ? `<img src="/img/imgs/like2.png">`: `<img src="/img/imgs/like1.png">`}
		                					</button>
		                					${reply.writer == email ? `&nbsp<a href='#'>삭제</a>` : ''}
		                					<a href="#">신고</a>
		                				</div>
		                			</div>
	                				`
	                			).join("")}
	                			</div>
	                			<div style='border-bottom: 1px dotted #ccc'></div>
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
	            $("#commentList").html(html);
	        }
	    });
	}
});