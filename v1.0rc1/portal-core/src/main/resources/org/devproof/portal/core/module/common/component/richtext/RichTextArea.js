tinyMCE.init({
	mode: "textareas", 
	editor_selector : "mceRichTextArea",
	theme : "advanced",
	plugins : "safari,pagebreak,spellchecker,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,syntaxhl",
	
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	theme_advanced_statusbar_location : "bottom",
	theme_advanced_resizing : true,
	theme_advanced_buttons1 : "save,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,|,sourcecode,syntaxhl,string2img,pagebreak,code",
	theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,|,inserttime,preview,|,forecolor,backcolor,help",
	theme_advanced_buttons3 : "fullscreen,|,tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl",
	content_css : "${defaultCss}",
	remove_linebreaks : false, 
	relative_urls : false, 
	extended_valid_elements : "textarea[cols|rows|disabled|name|readonly|class]",  
	setup : function(ed) {
        // Add a custom button
        ed.addButton('sourcecode', {
            title : 'Sourcecode',
            image : '${iconcodeImg}',
            onclick : function() {
                ed.selection.setContent('<code>place your code here! lines break with shift+enter</code>');
            }
        });
        // Add a custom button
        ed.addButton('string2img', {
            title : 'String to image',
            image : '${string2imgImg}',
            onclick : function() {
                ed.selection.setContent('[string2img size=20]place your text here! lines break with shift+enter[/string2img]');
            }
        });
    }
});
