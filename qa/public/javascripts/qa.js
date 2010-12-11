function goto(url) {
	top.location = url;
}

/**
 * Controller for dynamic Tag autocompletion, fetching a JSON array of
 * Tags from an <input type="text">'s tagsJSON.
 */
var TagAutocompletion = {
	_extractTags: function(tagString) {
		return tagString.split(/[\s,]\s*/);
	},
	source: function(request, response) {
		$.getJSON($(this.element[0]).attr("tagsJSON"), {
			term: TagAutocompletion._extractTags(request.term).pop(),
			content: ($("#newQuestion").val()||"").split(/(?:^|\W+)(?:\w{1,3}(?:\W+|$))*/).join(" ")
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
	$("input[type=text][tagsJSON]").autocomplete(TagAutocompletion);
	
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
			.attr("tagsJSON", $(this).attr("tagsJSON")).val(tags.join(" "));
		$("input[type=text][tagsJSON]").autocomplete(TagAutocompletion);
	});
	
	// simulate autofocus for non-HTML5 browsers
	if (!("autofocus" in document.createElement("input")))
		$("input[autofocus]").focus();
	
	// display placeholder text for non-HTML5 browsers
	if (!("placeholder" in document.createElement("input")))
		$("input[placeholder]:not([type=search])").each(function() {
			$('<span class="placeholder">' + this.getAttribute("placeholder") + '</span>')
				.insertAfter(this.parentNode.lastChild);
		});
	
	// enable the HTML5 validator for all forms
	$("form").validator();
	if ("required" in document.createElement("input"))
		$("input[pattern], input[required]:not([type=search]):not([type=password]), input[type=email], input[type=url]").each(function() {
			$('<object />').insertAfter(this);
		});
	
	// add an inline login form
	$(".navigation .user a[href=/login]").click(function() {
		$('<form action="/login" method="POST" id="mini_login"> \
			<span class="placeholder">Username:</span> \
			<input type="text" name="username" placeholder="user name"> \
			<span class="placeholder">Password:</span> \
			<input type="password" name="password" placeholder="password"> \
			<input type="submit" value="Log in"> \
			<a href="javascript:void($(\'#mini_login\').remove())" title="hide login">[x]</a></form>')
			.insertBefore(this.parentNode);
		if ("placeholder" in document.createElement("input"))
			$("#mini_login .placeholder").remove();
		$("#mini_login input[name=username]").focus();
		return false;
	});
	
	// automatically insert a preview after all Markdown textareas
	$("textarea").each(function() { $('<div class="wmd-preview"></div>').insertAfter(this); });
	
	// make sure to move the focus to the "New question" textarea
	// when the user clicks the "New question" link
	if ($("#newQuestion").length == 1) {
		if (location.hash == "#askquestion")
			$("#newQuestion").focus();
		$("[href=/#askquestion]").click(function() {
			$("#newQuestion").focus();
			// don't navigate away if we're not on the first index page
			location.hash = "#askquestion";
			return false;
		});
	}
	
	// allow to enable certain functionality only after a predefined timeout
	// <div timeout="seconds" timeoutMsg="display this instead (999s left)">
	$("div[timeout]").each(function() {
		var timeout = parseInt(this.getAttribute("timeout"));
		if (timeout > 0) {
			var message = this.getAttribute("timeoutMsg");
			var self = $(this);
			var overlay = $('<div class="overlay" />').appendTo(document.body);
			
			(function() {
				// reposition the overlay, in case the underlying div isn't
				// completely static, either (happens e.g. when images haven't
				// finished loading yet)
				overlay.css({
					width: self.outerWidth(), height: self.outerHeight(),
					left: self.offset().left, top: self.offset().top,
					position: "absolute"
				});
				overlay.text(message.replace(999, timeout));
				
				if (--timeout > 0)
					setTimeout(arguments.callee, 1000);
				else
					overlay.fadeOut("fast", function() { overlay.remove(); });
			})();
		}
	});
	
	// highlight search results, when the highlighter has been loaded
	// (currently on search pages only)
	if (jQuery.fn.highlight) {
		$("input[type=search]").each(function() {
			var terms = this.getAttribute("value").split(/\s+/);
			for (var i = 0; i < terms.length; i++) {
				if (!terms[i])
					continue;
				// only highlight tags when explicitly searching for tags
				if (/^tag:(\S+)/.test(terms[i]))
					$("a.tag").highlight(RegExp.$1);
				else
					$(".question").highlight(terms[i]);
			}
		});
	}
});

// configure the WMD editor
var wmd_options = {
	autostart: true,
	buttons: "bold italic | link blockquote code image | ol ul hr",
	output: "Markdown"
};
