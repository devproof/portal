window.onload =function() {
	CKEDITOR.replace( '${markupId}', {
		contentsCss : ['${emailCss}'],
		toolbar: 'Basic'
	});
}