#{if false} Arguments: _answer, _user, _extended #{/if}
<li
	class="answer #{if _answer.owner() == _user}own#{/if} #{if _answer.isBestAnswer()}bestAnswer#{/if}"
	id="answer-${_answer.id()}"
>
	<h2>#{showProfile _answer /}:</h2>
	<p>${_answer.content()}</p>
	#{date _answer /}
	
	#{if _extended && _user}
		<div class="commands">
			#{if !_answer.getQuestion().isLocked() && !_user.isBlocked()}
				<a href ="@{Application.commentAnswer(_answer.getQuestion().id(), _answer.id())}">&{'comment.add'}</a>
			#{/if}
			#{if _user.canEdit(_answer) && !_answer.getQuestion().isLocked()}
				| <a href="@{CAnswer.deleteAnswer(_answer.getQuestion().id(), _answer.id())}">&{'delete'}</a>
			#{/if}
			#{if _user.canEdit(_answer.getQuestion()) && !_answer.getQuestion().isLocked() && _answer.getQuestion().isBestAnswerSettable() && _answer.getQuestion().getBestAnswer() != _answer}
				| <a href="@{CAnswer.selectBestAnswer(_answer.getQuestion().id(), _answer.id())}#answer-${_answer.id()}">&{'answer.selectasbest'}</a>
			#{/if}
			#{if _user != _answer.owner()}
				| <a href="@{Application.confirmMarkSpamAnswer(_answer.getQuestion().id(),_answer.id())}">&{'spam.mark'}</a>
			#{/if}
		</div>
	#{/if}
	#{if _user && _answer.owner() != _user && !_user.isBlocked()}
		#{vote entry:_answer, user:_user /}
	#{/if}
</li>
#{if _extended}
	#{comments entry:_answer, user:_user /}
#{/if}
