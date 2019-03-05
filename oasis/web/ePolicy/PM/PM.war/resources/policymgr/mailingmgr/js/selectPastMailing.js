var selectedMailingDtls;
var size;
function handleOnButtonClick(asBtn) {

    switch (asBtn) {
        case 'DONE':
            var selectedRecords = selectPastMailingGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
            if (selectedRecords.length == 0) {
                handleError(getMessage("pm.selExludedPolicy.noSelection.error"));
                break;
            }
            else {
                setSelectedPolicies();
                if (confirm(size +" "+getMessage("pm.selExludedPolicy.excludePolicy.warning"))) {
                    setInputFormField("process", "invokeProcessMsg");
                    setInputFormField("fromButton", "afterCheckPastMailing");
                    setInputFormField("selectedMailingDtls", selectedMailingDtls);
                    var generateMailingUrl = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do";
                    document.forms[0].action = generateMailingUrl;
                    submitFirstForm();
                }
            }

            break;
        case 'CANCEL':
            var generateMailingUrl = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do";
            setInputFormField("process", "invokeProcessMsg");
            setInputFormField("fromButton", "afterCheckPastMailing");
            document.forms[0].action = generateMailingUrl;
            submitFirstForm();
            break;
    }
}

function setSelectedPolicies() {
    var selectedRecords = selectPastMailingGrid1.documentElement.selectNodes("//ROW[(CSELECT_IND = '-1')]");
    size = selectedRecords.length;
    selectedMailingDtls = "";

    for (var i = 0; i < size; i++) {
        var currentRecord = selectedRecords.item(i);
        var policyMailingDtlId = currentRecord.getAttribute("id");
        var policyNo = currentRecord.selectNodes("CPOLICYNO")(0).text;
        if (i == 0) {
            selectedMailingDtls = policyMailingDtlId;
        }
        else {
            selectedMailingDtls = selectedMailingDtls + "," + policyMailingDtlId;
        }
    }
}

function handleOnLoad() {
    // disable selectAll checkbox
    if (hasObject("HCSELECT_IND")) {
        getObject("HCSELECT_IND").disabled = true;
    }
}







