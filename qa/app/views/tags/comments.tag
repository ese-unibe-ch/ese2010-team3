#{if false} Arguments: _entry, _user #{/if}
#{if _entry.comments().size() != 0}
	<ul class="comments">
		#{list items:_entry.comments(), as:'comment'}
			<li class="comments">
				<p>#{showProfile comment /}:
				${comment.content().nl2br()}</p>
				#{date comment /}
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
