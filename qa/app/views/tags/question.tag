#{if false} Arguments: _question, _user, _extended, _custom #{/if}
<li class="question #{if _user && _question.owner() == _user}own#{/if}"
	#{if !_extended} onclick="goto('@{Application.question(_question.id())}')" #{/if}
	>
	<h2>#{showProfile _question /}:</h2>
	<p>${_question.content().raw()}</p>
	#{date _question /}
	#{tags question:_question, editable:_extended && _user?.canEdit(_question) /}
	
	#{if _extended && _user}
		<div class="commands">
		#{if !_question.isLocked()}
			#{if !_user.isBlocked()}
				<a href ="@{Application.commentQuestion(_question.id())}">Add a new comment | </a>
			#{/if}
			#{if _user.isObserving(_question)}
				<a href="@{Secured.unwatchQuestion(_question.id())}">Stop watching</a>
			#{/if}#{else}
				<a href="@{Secured.watchQuestion(_question.id())}">Watch</a>
			#{/else}
			#{if _user.isModerator()}
				<a href="@{Secured.lockQuestion(_question.id())}"> | Lock</a>
			#{/if}
		#{/if}
		#{if _user.isModerator() && _question.isLocked()}
			<a href="@{Secured.unlockQuestion(_question.id())}"> Unlock</a>
		#{/if}
		#{if _user.canEdit(_question)}
			<a href="@{Secured.deleteQuestion(_question.id())}"> | Delete</a>
		#{/if}
		</div>
	#{/if}
	#{if _custom?.equals("watchlist")}
		<div class="commands">
			<a href="@{Secured.unwatchQuestionFromList(_question.id())}">Stop watching</a>
		</div>
	#{/if}
	
	#{if _user && _question.owner() != _user && !_user.isBlocked()}
		#{vote _question /}
	#{/if}
</li>
#{if _extended}
	#{comments entry:_question, user:_user /}
#{/if}
