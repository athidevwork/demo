
Dim WshShell, oExec
Dim MyString, MyArray, Msg
Set WshShell = CreateObject("WScript.Shell")
Set oExec = WshShell.Exec("p4 where")
MyString = oExec.StdOut.ReadAll()
MyArray = Split(MyString, " ")
Msg = MyArray(0)
WScript.Echo Msg
