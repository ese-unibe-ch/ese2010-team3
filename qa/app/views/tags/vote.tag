<span class="vote">
	
	<a href="
		#{if _arg instanceof models.Answer}
			@{Secured.voteAnswerDown(_arg.question().id(), _arg.id())}
		#{/if}#{else}
			@{Secured.voteQuestionDown(_arg.id())}
		#{/else}
	">-</a>
	
	${_arg.rating()}

	<a href="
		#{if _arg instanceof models.Answer}
			@{Secured.voteAnswerUp(_arg.question().id(), _arg.id())}
		#{/if}#{else}
			@{Secured.voteQuestionUp(_arg.id())}
		#{/else}
	">+</a>

</span>