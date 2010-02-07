/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/
CKEDITOR.config.toolbar_Basic = [ [ 'Source', '-', 'Bold', 'Italic' ] ];
CKEDITOR.editorConfig = function( config )
{
//	config.contentsCss = ['/resources/org.devproof.portal.core.module.common.CommonConstants/css/default.css'];
	config.uiColor = "#BCBCA4";
//	config.toolbar = 'Basic'; 
//	config.uiColor = "#F1E8B1"
	config.extraPlugins = 'syntaxhighlight,string2image';
	config.toolbar_Full.push(['Code']);
	config.toolbar_Full.push(['Str2Image']);
};

