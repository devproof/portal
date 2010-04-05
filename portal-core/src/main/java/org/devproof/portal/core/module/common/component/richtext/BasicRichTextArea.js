window.onload = function() {
    CKEDITOR.replace('${markupId}', {
        contentsCss : ['${defaultCss}'],
        toolbar: 'Basic'
    });
};