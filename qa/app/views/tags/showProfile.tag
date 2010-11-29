#{if _arg.owner() != null}
	#{if session?.get("username")}
		#{a @Application.showprofile(_arg.owner().getName())} ${_arg.owner().getName().escape()}#{/a}
	#{/if}
	#{else}
		${_arg.owner().getName().escape()}
	#{/else}
#{/if}
#{else}
	anonymous
#{/else}
