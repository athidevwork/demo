function handleOnButtonClick(btn) {
    switch (btn) {
        case 'org':
            openEntityMiniPopupWin(getObjectValue("pk"));
            break;
        case 'close':
            closeWindow(function () {
                var parentWindow = getParentWindow();

                if (parentWindow && parentWindow.btnClick) {
                    parentWindow.btnClick('refresh');
                }
            });
            break;
        case 'save':
            if (!isStringValue(getObjectValue("duplicateEntityPk"))) {
                alert(getMessage("ci.maintainClientDup.save.nodup"))
                return;
            }
            if (!isStringValue(getObjectValue("pk"))) {
                alert(getMessage("ci.maintainClientDup.save.noorg"))
                return;
            }
            if (getObjectValue("pk") == getObjectValue("duplicateEntityPk")) {
                alert(getMessage("ci.maintainClientDup.save.samepks"))
                return;
            }
            if (confirm(getMessage("ci.maintainClientDup.save.warning"))) {
                showProcessingDivPopup();
                document.forms[0].action = "ciMaintainEntityDuplicate.do?process=saveMntEntityDuplicate";
                submitFirstForm();
            }
            break;
    }
}

function find(findId) {
    if (findId.toUpperCase() == "duplicateEntity".toUpperCase()) {
        openEntitySelectWinFullName("duplicateEntityPk", "duplicateEntity");
    }
}