var hasErrorMessages = "";
function handleOnChange(field) {

    if (field.name == 'commRateSchedId') {
        setInputFormField("process","load");
        submitFirstForm();
    }
}