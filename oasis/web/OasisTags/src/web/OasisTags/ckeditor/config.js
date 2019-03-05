/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 *
 * Modification
 * ---------------------------------------------------------------------------------------------
 * Date         By          Description
 * ---------------------------------------------------------------------------------------------
 * 01/07/2015   Elvin       Issue 161139: enhance CKEditor to be working with IE quirk mode
 * 12/02/2016   Elvin       Issue 181749: integrate spellex to replace SCAYT AND WSC plugins in CKEditor
 * ---------------------------------------------------------------------------------------------
 */

CKEDITOR.editorConfig = function( config ) {
    config.customConfig = 'customConfig.js';
    config.extraPlugins = 'iframedialog,spellex';

	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	config.disableNativeSpellChecker = false;
    config.removePlugins = 'scayt,wsc';
	//CKEDITOR.config.allowedContent = true;

    if (!isEmpty(CKEditor_BasePath)) {
        CKEDITOR.config.basePath = CKEditor_BasePath;
    }
    if (!isEmpty(CKEditor_Height)) {
        CKEDITOR.config.height = CKEditor_Height;
    }
    if (!isEmpty(CKEditor_EnterMode)) {
        CKEDITOR.config.enterMode = CKEditor_EnterMode;
    }
    if (!isEmpty(CKEditor_Lang)) {
        CKEDITOR.config.language = CKEditor_Lang;
    }
    if (!isEmpty(CKEditor_FontSizes)) {
        CKEDITOR.config.fontSize_sizes = CKEditor_FontSizes;
    }
    if (!isEmpty(CKEditor_FontNames)) {
        CKEDITOR.config.font_names = CKEditor_FontNames;
    }
    if (!isEmpty(CKEditor_DftFontLabel)) {
        CKEDITOR.config.font_defaultLabel = CKEditor_DftFontLabel;
    }
    if (!isEmpty(CKEditor_DftFontSizeLabel)) {
        CKEDITOR.config.fontSize_defaultLabel = CKEditor_DftFontSizeLabel;
    }

//    config.disableReadonlyStyling=true;
	config.pasteFromWordRemoveFontStyles=false;
	config.pasteFromWordRemoveStyles=false;
	config.fillEmptyBlocks = false;
	CKEDITOR.config.forceEnterMode=false;
	CKEDITOR.config.shiftEnterMode=1;
	config.toolbarCanCollapse = true;
	config.skin='office2013';
	config.autoParagraph = false;
	config.toolbar =
[
    { name: 'document',    items : [ 'Preview','Maximize' ] },
    { name: 'clipboard',   items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
    { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-', 'Spellex' ] },
    '/',
    { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript' ] },
    { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
    { name: 'links',       items : [ 'Link','Unlink','Anchor' ] },
    { name: 'insert',      items : ['Table','HorizontalRule','SpecialChar','PageBreak' ] },
    '/',
    { name: 'styles',      items : [ 'Styles','Format','Font','FontSize' ] },
    { name: 'colors',      items : [ 'TextColor','BGColor' ] }
];
	/*
	// This is actually the default value.
config.toolbar_Full =
[
    { name: 'document',    items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
    { name: 'clipboard',   items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
    { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-','SpellChecker', 'Scayt' ] },
    { name: 'forms',       items : [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
    '/',
    { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
    { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
    { name: 'links',       items : [ 'Link','Unlink','Anchor' ] },
    { name: 'insert',      items : [ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak' ] },
    '/',
    { name: 'styles',      items : [ 'Styles','Format','Font','FontSize' ] },
    { name: 'colors',      items : [ 'TextColor','BGColor' ] },
    { name: 'tools',       items : [ 'Maximize', 'ShowBlocks','-','About' ] }
];
	
	*/
	
};
