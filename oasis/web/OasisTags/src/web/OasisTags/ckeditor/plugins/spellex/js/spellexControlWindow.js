//*********************************************************************
// spellexControlWindow.js
// Purpose: Functions used by Spell Checker Control Window
//*********************************************************************

/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 01/30/2013       htwang      issue 139459, enable the control buttons after completing processing the note
 **/

////////////////////////////////////////////////////
// controlWindow object
////////////////////////////////////////////////////
function controlWindow( controlForm ) {
	// private properties
	this._form = controlForm;

	// public properties
	this.windowType = "controlWindow";
	this.noSuggestionSelection = "- No suggestions -";
	// set up the properties for elements of the given control form
	this.suggestionList = this._form.sugg;
	this.evaluatedText = this._form.misword;
	this.replacementText = this._form.txtsugg;
	this.undoButton = this._form.btnUndo;
    this.ignoreButton = this._form.btnIgnore;
    this.ignoreAllButton = this._form.btnIgnoreAll;
    this.changeButton = this._form.btnChange;
    this.changeAllButton = this._form.btnChangeAll;
    this.optionsButton = this._form.btnOptions;
    this.addToDictionaryButton = this._form.btnAddToDictionary;
    this.editDictButton = this._form.btnEditDict;

    //MG
    this.problemTypeLabel = document.getElementById("problemTypeLabel");

    // public methods
	this.addSuggestion = addSuggestion;
	this.clearSuggestions = clearSuggestions;
	this.selectDefaultSuggestion = selectDefaultSuggestion;
	this.resetForm = resetForm;
	this.setSuggestedText = setSuggestedText;
	this.enableUndo = enableUndo;
	this.disableUndo = disableUndo;
    this.enableControlButtons = enableControlButtons;
}

function resetForm() {
	if( this._form ) {
		this._form.reset();
	}
}

function setSuggestedText() {
//    alert('setSuggestedText()');
    var slct = this.suggestionList;
	var txt = this.replacementText;
	var str = "";
	if( (slct.options[0].text) && slct.options[0].text != this.noSuggestionSelection ) {
		str = slct.options[slct.selectedIndex].text;
	}
	txt.value = str;
}

function selectDefaultSuggestion() {
	var slct = this.suggestionList;
	var txt = this.replacementText;
	if( slct.options.length == 0 ) {
		this.addSuggestion( this.noSuggestionSelection );
	} else {
		slct.options[0].selected = true;
	}
	this.setSuggestedText();
}

function addSuggestion( sugg_text ) {
	var slct = this.suggestionList;
	if( sugg_text ) {
		var i = slct.options.length;
		var newOption = new Option( sugg_text, 'sugg_text'+i );
		slct.options[i] = newOption;
	 }
}

function clearSuggestions() {
	var slct = this.suggestionList;
	for( var j = slct.length - 1; j > -1; j-- ) {
		if( slct.options[j] ) {
			slct.options[j] = null;
		}
	}
}

function enableUndo() {
	if( this.undoButton ) {
		if( this.undoButton.disabled == true ) {
			this.undoButton.disabled = false;
		}
	}
}

function disableUndo() {
	if( this.undoButton ) {
		if( this.undoButton.disabled == false ) {
			this.undoButton.disabled = true;
		}
	}
}

function enableControlButtons() {
    // enable all disabled buttons except undo button after completing the whole notes processing
    if( this.ignoreButton.disabled == true ) {
        this.ignoreButton.disabled = false;
    }

    if( this.ignoreAllButton.disabled == true ) {
        this.ignoreAllButton.disabled = false;
    }

    if( this.changeButton.disabled == true ) {
        this.changeButton.disabled = false;
    }

    if( this.changeAllButton.disabled == true ) {
        this.changeAllButton.disabled = false;
    }

    if( this.optionsButton.disabled == true ) {
        this.optionsButton.disabled = false;
    }

    if( this.addToDictionaryButton.disabled == true ) {
        this.addToDictionaryButton.disabled = false;
    }

    if( this.editDictButton.disabled == true ) {
        this.editDictButton.disabled = false;
    }
}
