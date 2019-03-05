function handleOnButtonClick(btn) {
    switch (btn) {
        case "DONE":
            closeWindow(function () {
                if (appReviewerListGrid1.recordset.recordcount > 0) {
                    var assigneeId = appReviewerListGrid1.recordset("CUSERID").value;
                    var assigneeName = appReviewerListGrid1.recordset("CENTITYNAME").value;
                    getParentWindow().updateAssignee(assigneeId, assigneeName);
                }
            });
            break;
    }
}
