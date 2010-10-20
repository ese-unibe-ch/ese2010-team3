<div class="tags">
	#{form @Secured.updateTags(_question.id())}
	#{list items:_question.getTags(), as:'tag'}
		<span class="tag">${tag.getName()}</span>
	#{/list}
	#{if _editable}
		<input type="button" class="editTags" value="Edit Tags" tagURL="@{Application.tags}">
	#{/if}
	#{/form}
</div>
