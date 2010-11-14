#{if _arg.owner() != null}
	#{if session?.get("username")}
		#{a @Application.showprofile(_arg.owner().getName())} ${_arg.owner().getName()}#{/a}
	#{/if}
	#{else}
		${_arg.owner().getName()}
	#{/else}
#{/if}
#{else}
	anonymous
#{/else}
