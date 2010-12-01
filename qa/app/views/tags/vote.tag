#{if false} Arguments: _entry, _user #{/if}
<span class="vote">
	
	#{if _entry.hasDownVote(_user)}
		<a href="
			#{if _entry instanceof models.Answer}
				@{CAnswer.voteAnswerCancel(_entry.getQuestion().id(), _entry.id())}#answer-${_entry.id()}
			#{/if}#{else}
				@{CQuestion.voteQuestionCancel(_entry.id())}#question-${_entry.id()}
			#{/else}
		" class="voted" title="remove your vote">-</a>
	#{/if}#{else}
		<a href="
			#{if _entry instanceof models.Answer}
				@{CAnswer.voteAnswerDown(_entry.getQuestion().id(), _entry.id())}#answer-${_entry.id()}
			#{/if}#{else}
				@{CQuestion.voteQuestionDown(_entry.id())}#question-${_entry.id()}
			#{/else}
		" title="vote down">-</a>
	#{/else}
	
	${_entry.rating()}

	#{if _entry.hasUpVote(_user)}
		<a href="
			#{if _entry instanceof models.Answer}
				@{CAnswer.voteAnswerCancel(_entry.getQuestion().id(), _entry.id())}#answer-${_entry.id()}
			#{/if}#{else}
				@{CQuestion.voteQuestionCancel(_entry.id())}#question-${_entry.id()}
			#{/else}
		" class="voted" title="remove your vote">-</a>
	#{/if}#{else}
	<a href="
		#{if _entry instanceof models.Answer}
			@{CAnswer.voteAnswerUp(_entry.getQuestion().id(), _entry.id())}#answer-${_entry.id()}
		#{/if}#{else}
			@{CQuestion.voteQuestionUp(_entry.id())}#question-${_entry.id()}
		#{/else}
	" title="vote up">+</a>
	#{/else}

</span>
