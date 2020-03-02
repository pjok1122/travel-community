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
	        	console.log(data);
	        	getCommentList(articleId);
	        	document.getElementById('commentContent').value = ''
	        }
		})
	});
	
	document.getElementById('commentList').onclick = function(event){
		console.log(event.target);
//		var target = getEventTarget(event)
//		console.log(target.innerHTML);
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
	                					${data[i].writer == email ? `&nbsp<button type='button' class='btn btn-primary'>글쓴이</button>` : ''}
	                				</h6>
	                				${data[i].content}
	                			</div>
	                			<div class='col'>
	                				<button class='icon' id='icon-like-comment'>
	                					<img src='/img/imgs/like2.png'>
	                				</button>
	                				&nbsp댓글달기
	                				${data[i].writer == email ? `&nbsp<button type='button' class='btn btn-info' id='${data[i].id}'>삭제</button>` : ''}
	                				<button class='icon' id='icon-report-comment' onclick='$('#exampleModal').modal('show')'>
	                					<img class='report' src='/img/imgs/report.png'>
                					</button>
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