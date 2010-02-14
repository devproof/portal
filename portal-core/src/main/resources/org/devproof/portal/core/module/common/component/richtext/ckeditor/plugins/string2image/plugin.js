CKEDITOR.dialog.add('string2ImageDlg', function(editor) {
	return {
		title : 'String to image',
		minWidth : 220,
		minHeight : 80,
		contents : [ {
			id : 'tab1',
			label : 'tab1',
			title : 'tab1',
			elements : [ {
				id : 'imagetext',
				type : 'text',
				label : "Image text",
				validate : function() {
					return true;
				}
			},
			{
				id : 'fontsize',
				type : 'select',
				label : "Font size",
				items : [ [ '12' ], [ '16' ], [ '20' ] ],
				validate : function() {
					return true;
				}
			}, ]
		} ],

		onOk : function() {
			var fontsize = this.getContentElement('tab1', 'fontsize').getValue();
			var element = CKEDITOR.dom.element.createFromHtml('<span>[string2img size=' + fontsize + ']' + this.getContentElement('tab1', 'imagetext').getValue() + '[/string2img]</span>');
			editor.insertElement(element);
		}
	};
});

CKEDITOR.plugins.add('string2image', {
	init : function(a) {
		if (CKEDITOR.env.ie6Compat)
			return;
		a.addCommand('string2ImageDlg', new CKEDITOR.dialogCommand(
				'string2ImageDlg'));
		a.ui.addButton('Str2Image', {
			label : 'Label',
			command : 'string2ImageDlg',
			icon : this.path + 'string2image.gif'
		});
	}
});
