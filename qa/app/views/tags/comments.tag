#{if false} Arguments: _entry, _user #{/if}
#{if _entry.comments().size() != 0}
	<ul class="comments">
		#{list items:_entry.comments(), as:'comment'}
			<li class="comments">
				<p>#{showProfile comment /}:
				${comment.content().raw()}</p>
				#{date comment /}
				<!-- by team2 -->
				<p align="right">
		 		#{if _user && comment.owner() != _user && !_user.isBlocked() && !comment.getLikers().contains(_user)}
					#{if _entry instanceof models.Answer}
						<img alt="I like" src="/public/images/thumbUp.png" />
						<a href="@{Secured.addLikerAnswerComment(comment.id(), _entry.getQuestion().id(), _entry.id())}">I like</a>
					#{/if}#{else}
						<img alt="I like" src="/public/images/thumbUp.png" />
						<a href="@{Secured.addLikerQuestionComment(comment.id(), _entry.id())}">I like</a>
					#{/else }
				#{/if }
				#{if _user && comment.owner() != _user && !_user.isBlocked() && comment.getLikers().contains(_user)}
					#{if _entry instanceof models.Answer}
						<img alt="I like" src="/public/images/thumbDown.png" />
						<a href="@{Secured.removeLikerAnswerComment(comment.id(), _entry.getQuestion().id(), _entry.id())}">I don't like</a>
					#{/if}#{else}
						<img alt="I like" src="/public/images/thumbDown.png" />
						<a href="@{Secured.removeLikerQuestionComment(comment.id(), _entry.id())}">I don't like</a>
					#{/else }
				#{/if }
				<!-- count of users who like this -->
				#{if comment.countLikers()==1 }
				1 user likes this
				#{/if}
				#{if comment.countLikers()>1 }
				${comment.countLikers()} users like this
				#{/if }
				</p>
				<!-- EOT2 = end of team2 -->
				#{if _user && _user.canEdit(comment)}
					#{if _entry instanceof models.Answer}
						<a href="@{Secured.deleteCommentAnswer(_entry.getQuestion().id(), _entry.id(), comment.id())}">Delete</a>
					#{/if}#{else}
						<a href="@{Secured.deleteCommentQuestion(_entry.id(), comment.id())}">Delete</a>
					#{/else}
				#{/if}
			</li>	
		#{/list}
	</ul>
#{/if}
