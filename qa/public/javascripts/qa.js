function goto(url) {
	top.location = url;
}

/**
 * Controller for dynamic Tag autocompletion, fetching a JSON array of
 * Tags from an <input type="text">'s tagURL.
 */
var TagAutocompletion = {
	_extractTags: function(tagString) {
		return tagString.split(/[\s,]\s*/);
	},
	source: function(request, response) {
		$.getJSON($(this.element[0]).attr("tagURL"), {
			term: TagAutocompletion._extractTags(request.term).pop()
		}, response);
	},
	search: function() {
		var term = TagAutocompletion._extractTags(this.value).pop();
		if (term.length < 1)
			return false;
	},
	focus: function() {
		return false; // prevent value inserted on focus
	},
	select: function(event, ui) {
		var terms = TagAutocompletion._extractTags(this.value);
		terms[terms.length - 1] = ui.item.value;
		this.value = terms.join(" ") + " ";
		return false;
	}
};

$(document).ready(function() {
	// autocompletify all elements marked as such
	$("input[type=text][tagURL]").autocomplete(TagAutocompletion);
	
	// make an "Edit Tags" button replace the static Tag list with
	// a dynamic one - or submit the form, if it's already done that 
	$(".tags .editTags").click(function() {
		if ($(this).parent().find("input[type=text]").length > 0)
		{
			this.form.submit();
			return;
		}
		
		var tags = [];
		$(this).parent().find(".tag").each(function() {
			tags.push($(this).text());
		}).remove();
		
		$('<input type="text" name="tags">').insertBefore(this)
			.attr("tagURL", $(this).attr("tagURL")).val(tags.join(" "));
		$("input[type=text][tagURL]").autocomplete(TagAutocompletion);
	});
});
