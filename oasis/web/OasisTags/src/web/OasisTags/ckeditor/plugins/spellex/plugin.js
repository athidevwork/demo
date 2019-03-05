/**
 * Created by eouyang on 12/1/2016.
 */
CKEDITOR.plugins.add('spellex', {
    // temporary solution, display the spellex plugin with original spell check css in csViewNote.js
    // Todo: need to add a spellex.png here same with original, or find another solution
    // icons: 'spellex',
    lang: ['en'],
    init: function(editor) {
        editor.addCommand('startSpellex', new CKEDITOR.dialogCommand('spellexDialog'));
        editor.ui.addButton('Spellex', {
            label: 'Spellex',
            command: 'startSpellex',
            toolbar: 'editing'
        });

        var onContentLoad = function() {
            $('.cke_dialog_close_button').hide();
            $('.cke_dialog_ui_button_ok').hide();
        };
        CKEDITOR.dialog.addIframe('spellexDialog', editor.lang.spellex.SpellexEditorTitle, this.path + 'spellex.html', 480, 480, onContentLoad);
    }
});