#{if false} Arguments: _entry, _user #{/if}
#{if _entry.comments().size() != 0}
	<ul class="comments">
		#{list items:_entry.comments(), as:'comment'}
			<li class="comments" id="comment-${comment.id()}">
				<p>#{showProfile comment /}:
				${comment.content()}</p>
				#{date comment /}
				
				<!-- by team2 -->
				<p align="right">
		 		#{if _user && comment.owner() != _user && !_user.isBlocked() && !comment.getLikers().contains(_user)}
					<img alt="&{'comment.ilike'}" src="/public/images/thumbUp.png" />
					#{if _entry instanceof models.Answer}
						<a href="@{CAnswer.addLikerAnswerComment(comment.id(), _entry.getQuestion().id(), _entry.id())}#comment-${comment.id()}">&{'comment.ilike'}</a>
					#{/if}#{else}
						<a href="@{CQuestion.addLikerQuestionComment(comment.id(), _entry.id())}#comment-${comment.id()}">&{'comment.ilike'}</a>
					#{/else }
				#{/if}
				#{if _user && comment.owner() != _user && !_user.isBlocked() && comment.getLikers().contains(_user)}
					<img alt="&{'comment.idislike'}" src="/public/images/thumbDown.png" />
					#{if _entry instanceof models.Answer}
						<a href="@{CAnswer.removeLikerAnswerComment(comment.id(), _entry.getQuestion().id(), _entry.id())}#comment-${comment.id()}">&{'comment.idislike'}</a>
					#{/if}#{else}
						<a href="@{CQuestion.removeLikerQuestionComment(comment.id(), _entry.id())}#comment-${comment.id()}">&{'comment.idislike'}</a>
					#{/else }
				#{/if}
				<!-- count of users who like this -->
				#{if comment.countLikers()==1 }
				1 &{'comment.likecountone'}
				#{/if}
				#{if comment.countLikers()>1 }
				${comment.countLikers()} &{'comment.likecountmultiple'}
				#{/if}
				</p>
				<!-- EOT2 = end of team2 -->
				
				#{if _user && _user.canEdit(comment)}
					#{if _entry instanceof models.Answer}
						<a href="@{CAnswer.deleteCommentAnswer(_entry.getQuestion().id(), _entry.id(), comment.id())}#answer-${_entry.id()}">&{'delete'}</a>
					#{/if}#{else}
						<a href="@{CQuestion.deleteCommentQuestion(_entry.id(), comment.id())}">&{'delete'}</a>
					#{/else}
				#{/if}
				#{if _user && _user.canEdit(comment) && _user != comment.owner()}|#{/if}
				#{if _user && _user != comment.owner()}
					#{if _entry instanceof models.Answer}
						<a href="@{Application.confirmMarkSpamAnswerComment(_entry.getQuestion().id(), _entry.id(), comment.id())}#answer-${_entry.id()}">&{'spam.mark'}</a>
					#{/if}#{else}
						<a href="@{Application.confirmMarkSpamQuestionComment(_entry.id(), comment.id())}">&{'spam.mark'}</a>
					#{/else}
				#{/if}
			</li>	
		#{/list}
	</ul>
#{/if}
