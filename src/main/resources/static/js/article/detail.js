$(document).ready(function(){
	var email = document.getElementById('email').innerText;
	var articleId =  document.getElementById('articleId').innerText;
	getCommentList(articleId, email);
	
	$("#comment-insert").on("click", function(){
		$.ajax({
			url: '/comment',
			type : 'POST',
			data : {
	        	'articleId': articleId,
	        	'content': $("#commentContent").val(), 
	        	'parentCommentId': ''
	        },
	        success : function(data){
	        	getCommentList(articleId, email);
	        	document.getElementById('commentContent').value = ''
	        }
		})
	});
	
	document.getElementById('commentList').onclick = function(event){
		
		var current = event.target;
		var parent = current.parentNode;
		var commentId = parent.getAttribute("name");
		
//		console.log(parent);
		
		if(current.innerText == '삭제')
		{
			$.ajax({
		        url : '/comment/delete/'+commentId,
		        type : 'post',
		        success : function(data){
		        	getCommentList(articleId, email);
		        }
		    });
		}	
		else if(current.innerText == '등록')
		{
			console.log(parent.parentNode)
//			console.log(parent.parentNode.getElementsByTagName("input")[0])
			console.log(parent.parentNode.getElementsByTagName("input")[0].value);
			console.log(parent.parentNode.getAttribute("name"));
			
			$.ajax({
				url: '/comment',
				type : 'POST',
				data : {
		        	'articleId': articleId,
		        	'content': parent.parentNode.getElementsByTagName("input")[0].value, 
		        	'parentCommentId': parent.parentNode.getAttribute("name")
		        },
		        success : function(data){
		        	getCommentList(articleId, email);
		        	parent.parentNode.getElementsByTagName("input")[0].value = '';
		        }
			})
		}	
		else if(current.innerText == '댓글달기')
		{
			var reply = document.getElementById('comment-reply')
			
			var html =
				`
				<div class="container" id="comment-reply">
					<label for="content">comment</label>
					<div class="input-group" name='${commentId}'>
						<input type="text" class="form-control" name="commentReplyContent" placeholder="내용을 입력하세요.">
						<span class="input-group-btn">
						<button class="btn btn-default" type="button" name="commentReplyInsert">등록</button>
					</span>
					</div>
				</div>
				`
			if(reply == null)// && parent == reply.parentNode)
			{
				parent.insertAdjacentHTML('beforeend', html)
			}	
			else
			{
				if (parent != reply.parentNode)
				{
					parent.insertAdjacentHTML('beforeend', html)
				}
				reply.remove();
			}	
		}	
		
	};
	
	function commentDelete(commentId){
		console.log(commentId);
	    $.ajax({
	        url : '/comment/delete/'+commentId,
	        type : 'post',
	        success : function(data){
	        	getCommentList(articleId);
	        }
	    });
	}
	
	function getCommentList(articleId, email){
	    $.ajax({
	        type:'GET',
	        url : "/comment",
	        data : {
	        	'articleId': articleId
	        },
	        success : function(data){
//	          	console.log(data);  
	            var html = "";
	            
	            if(data.length > 0){
	                for(i=0; i<data.length; i++){
	                	html +=
	                		`
	                		<div>
	                			<div>
	                				<h6>
	                					<strong> ${data[i].writer} </strong>&nbsp ${data[i].registerDate}
	                					${data[i].writer == email ? `&nbsp[글쓴이]` : ''}
	                				</h6>
	                				${data[i].content}
	                			</div>
	                			<div class='col' name='${data[i].id}' >
	                				<button class='icon' id='icon-like-comment'>
	                					<img src='/img/imgs/like2.png'>
	                				</button>
	                				&nbsp<a href='#'>댓글달기</a>
	                				${data[i].writer == email ? `&nbsp<button type='button' class='btn btn-info'>삭제</button>` : ''}
	                				${data[i].writer == email ? '':`<button class='icon' id='icon-report-comment' onclick='$('#exampleModal').modal('show')'><img class='report' src='/img/imgs/report.png'></button>`}
	                			</div>
		                		
	                			<div style='border-bottom: 1px dotted #ccc'></div>
                			</div>
	                		`
	                }
	            } else {
	                html += "<div>";
	                html += "<div><table class='table'><h6><strong>등록된 댓글이 없습니다.</strong></h6>";
	                html += "</table></div>";
	                html += "</div>";
	            }
	            $("#commentList").html(html);
	        },
	        error:function(request,status,error){
	       }
	    });
	}
});