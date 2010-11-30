#{if false} Arguments: _question, _user, _extended, _custom #{/if}
<li class="question #{if _user && _question.owner() == _user}own#{/if}"
	#{if !_extended} onclick="goto('@{Application.question(_question.id())}')" #{/if}
	>
	<h2 id="question-${_question.id()}">#{showProfile _question /}:</h2>
	#{if !_extended}<p>${_question.summary().escape()}</p>#{/if}
	#{else}<p>${_question.content()}</p>#{/else}
	#{date _question /}
	#{tags question:_question, editable:_extended && _user?.canEdit(_question) /}
	
	#{if _extended && _user}
		<div class="commands">
		#{if !_question.isLocked()}
			#{if !_user.isBlocked()}
				<a href ="@{Application.commentQuestion(_question.id())}">&{'comment.add'}</a> |
			#{/if}
			#{if _user.isObserving(_question)}
				<a href="@{Secured.unwatchQuestion(_question.id())}">&{'watch.stop'}</a>
			#{/if}#{else}
				<a href="@{Secured.watchQuestion(_question.id())}">&{'watch.start'}</a>
			#{/else}
			#{if _user.isModerator()}
				| <a href="@{Secured.lockQuestion(_question.id())}">&{'question.lock'}</a>
			#{/if}
		#{/if}
		#{if _user.isModerator() && _question.isLocked()}
			<a href="@{Secured.unlockQuestion(_question.id())}">&{'question.unlock'}</a>
		#{/if}
		#{if _user.canEdit(_question)}
			| <a href="@{Application.confirmDeleteQuestion(_question.id())}">&{'delete'}</a>
		#{/if}
		</div>
	#{/if}
	#{if _custom?.equals("watchlist")}
		<div class="commands">
			<a href="@{Secured.unwatchQuestionFromList(_question.id())}">&{'watch.stop'}</a>
		</div>
	#{/if}
	
	#{if _user && _question.owner() != _user && !_user.isBlocked()}
		#{vote entry:_question, user:_user /}
	#{/if}
</li>
#{if _extended}
	#{comments entry:_question, user:_user /}
#{/if}
