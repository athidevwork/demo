function invlidRightClick() {
    window.event.returnValue = false;
}
document.oncontextmenu = invlidRightClick;
