tinyMCE.init({
	mode: "textareas", 
	editor_selector : "mceRichTextArea",
	theme : "advanced",
	plugins : "safari,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
	
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	theme_advanced_statusbar_location : "bottom",
	theme_advanced_resizing : false,
	relative_urls : false, 
	theme_advanced_buttons1 : "save,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,|,fullscreen,removeformat,charmap,iespell,print",
	theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,code,|,undo,redo,|,link,unlink,anchor,image,cleanup,|,inserttime,preview,|,forecolor,backcolor",
	theme_advanced_buttons3 : "",
	content_css : "${emailCss}"
});
