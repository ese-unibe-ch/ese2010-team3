#{if false} Arguments: _question, _editable - or _tags #{/if}
<div class="tags">
	#{if _question}
		#{form @CQuestion.updateTags(_question.id())}
		#{list items:_question.getTags(), as:'tag'}
			<a href="@{Application.search("tag:" + tag.getName())}" class="tag">${tag.getName().escape()}</a>
		#{/list}
		#{if _editable}
			<input type="button" class="editTags" value="Edit Tags" tagsJSON="@{Application.tags}">
		#{/if}
		#{/form}
	#{/if}#{else}
		#{list items:_tags, as:'tag'}
			<a href="@{Application.search("tag:" + tag.getName())}" class="tag">${tag.getName().escape()}</a>
		#{/list}
	#{/else}
</div>
