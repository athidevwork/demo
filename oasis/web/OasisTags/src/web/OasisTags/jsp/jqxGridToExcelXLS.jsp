<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@page import="org.apache.poi.hssf.usermodel.*" %>
<%@ page import="org.apache.poi.ss.usermodel.*" %>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook" %>
<%@ page import="org.apache.poi.xssf.usermodel.XSSFCell" %>
<%@ page import="java.util.Date" %>
<%@ page import="dti.oasis.util.DateUtils" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="javax.xml.stream.XMLStreamReader" %>
<%@ page import="javax.xml.stream.XMLInputFactory" %>
<%@ page import="javax.xml.stream.events.XMLEvent" %>
<%@ page import="javax.xml.stream.XMLStreamException" %>
<%@ page import="dti.oasis.app.AppException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.filter.CharacterEncodingFilter" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="dti.oasis.tags.OasisGrid" %>
<%@ page import="java.util.logging.Level" %>
<%
    /**
     *
     * <p/>
     * <p/>
     * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
     * Date: 8/25/2015
     *
     * @author mgitelman
     */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 8/25/2015        mgitelman        Moved code from EMS and added to eOasis changes.
 * ---------------------------------------------------
 */


    Logger l = LogUtils.enterLog(getClass(), "jqxGridToExcelXLS.jsp");

    String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);

    String exportType = request.getParameter("exportType");

    String dispType = request.getParameter("dispositionType");

    String pageName = request.getParameter("pageName");

    if (StringUtils.isBlank(dispType)) {
        dispType = OasisGrid.ATTACH_DISP_TYPE;
    }

    if (StringUtils.isBlank(exportType)) {
        exportType = "XLSX";
    }

    String fileExt = ".xlsx";
    if (exportType.equalsIgnoreCase("XLS"))
        fileExt = ".xls";

    String gridId = request.getParameter("gridId");
    String fileName = "";
    if (StringUtils.isBlank(gridId)) {
        fileName = "grid" + fileExt;
    } else {
        fileName = gridId + fileExt;
    }

    String values = request.getParameter("textForFile");
    //Clean non-ASCII characters
    if(values!=null)
        values = values.replaceAll("[^\\x20-\\x7e]", "");
    else
        values = "";
    //Parse XML
    InputStream inputStream = new ByteArrayInputStream(values.getBytes());

    XMLStreamReader reader = null;

    //Write to XLS
    Workbook wb = null;
    if (exportType.equalsIgnoreCase("XLSX"))
        wb = new XSSFWorkbook();
    else
        wb = new HSSFWorkbook();

    Sheet sheet = wb.createSheet("Sheet1");


    //Create Styles
    CellStyle cs;
    CellStyle csBold;

    //Bold Fond
    Font bold = wb.createFont();
    bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

    //Bold style
    csBold = wb.createCellStyle();
    csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    csBold.setFont(bold);

    int rowIdx = 0, colIdx = 0;

    //Get Mode
    String displayMode = ApplicationContext.getInstance().getProperty("gridExportExcel.infoHeaderMode", "HEADER");
    if (!displayMode.equalsIgnoreCase("NONE")) {
        //Get current Date and Time
        Date date = new java.util.Date(System.currentTimeMillis());
        String dateString = dti.oasis.util.FormatUtils.formatDateTimeForDisplay(date);

        //Get User
        String exportUser;
        dti.oasis.util.OasisUser user = (dti.oasis.util.OasisUser) session.getAttribute(dti.oasis.struts.IOasisAction.KEY_OASISUSER);
        if(user!=null){
            exportUser = user.getUserName()+"("+user.getUserId()+")";
        } else {
            String userId = dti.oasis.struts.ActionHelper.getCurrentUserId(request);
            exportUser = userId;
        }

        if (displayMode.equalsIgnoreCase("HEADER")) {
            Header header = sheet.getHeader();
            header.setLeft(HSSFHeader.fontSize((short) 9) + "Exported By: " + exportUser + "\n" + "Exported From: " + pageName);
            header.setRight(HSSFHeader.fontSize((short) 9) + "Exported On: "+dateString);
        } else if (displayMode.equalsIgnoreCase("FOOTER")) {
            Footer footer = sheet.getFooter();
            footer.setLeft(HSSFFooter.fontSize((short) 9) + "Exported By: " + exportUser + "\n" + "Exported From: " + pageName);
            footer.setRight(HSSFFooter.fontSize((short) 9) + "Exported On: " + dateString);
        } else if (displayMode.equalsIgnoreCase("BODY")) {
            Row infoRow;
            Cell infoCell;

            //First header row
            infoRow = sheet.createRow(rowIdx++);
            infoCell = infoRow.createCell(0);
            infoCell.setCellValue("Exported By:");
            infoCell = infoRow.createCell(1);
            infoCell = infoRow.createCell(2);
            infoCell.setCellValue(exportUser);

            //Second header row
            infoRow = sheet.createRow(rowIdx++);
            infoCell = infoRow.createCell(0);
            infoCell.setCellValue("Exported On:");
            infoCell = infoRow.createCell(1);
            infoCell = infoRow.createCell(2);
            infoCell.setCellValue(dateString);

            //Third header row
            infoRow = sheet.createRow(rowIdx++);
            infoCell = infoRow.createCell(0);
            infoCell.setCellValue("Exported From:");
            infoCell = infoRow.createCell(2);
            infoCell.setCellValue(pageName);

            //Empty row
            rowIdx++;
        }
    }

    int gridRowCount = 0;
    String type = "String";
    Row row = sheet.createRow(rowIdx++);
    List<Integer> widthList = new ArrayList<Integer>();

    try {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        reader = factory.createXMLStreamReader(inputStream,encoding);
        String elementName = null;
        int eventType;
        boolean isSetBlankContent = false;

        while (reader.hasNext()) {
            eventType = reader.next();
            switch (eventType) {
                case XMLEvent.START_ELEMENT:
                    elementName = reader.getName().getLocalPart();
                    if ("Data".equalsIgnoreCase(elementName)) {
                        int attCount = reader.getAttributeCount();
                        if (attCount > 0) {
                            for (int j = 0; j < attCount; j++) {
                                if ("type".equalsIgnoreCase(reader.getAttributeLocalName(j))) {
                                    type = reader.getAttributeValue(j);
                                    isSetBlankContent = true;
                                    break;
                                }
                            }
                        }
                    }else if (("Column").equalsIgnoreCase(elementName)){
                        int attCount = reader.getAttributeCount();
                        if (attCount > 0) {
                            for (int j = 0; j < attCount; j++) {
                                if ("Width".equalsIgnoreCase(reader.getAttributeLocalName(j))) {
                                    //sheet.setColumnWidth(indexForColumnWidth, Integer.valueOf(reader.getAttributeValue(j)));
                                    widthList.add(Integer.valueOf(reader.getAttributeValue(j)));
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    if (elementName.equalsIgnoreCase("Data")) {
                        Cell cell = row.createCell(colIdx);
                        if ("number".equalsIgnoreCase(type)) {
                            cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
                        } else {
                            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                        }
                        if (gridRowCount == 0) {
                            // deal with column Header
                            cell.setCellValue(reader.getText().replaceAll(":;:", ","));
                            cell.setCellStyle(csBold);
                        } else {
                            // deal with  Content
                            cell.setCellValue(reader.getText());
                        }
                        colIdx++;
                        isSetBlankContent = false;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    elementName = reader.getName().getLocalPart().toString();
                    if(elementName.equalsIgnoreCase("Data") && isSetBlankContent == true){
                        colIdx++;
                        isSetBlankContent = false;
                    }
                    if (elementName.equalsIgnoreCase("Row")) {
                        row = sheet.createRow(rowIdx++);
                        gridRowCount++;
                        colIdx = 0;
                    }
                    break;
            }
        }
    } catch (XMLStreamException e) {
        l.logp(Level.SEVERE, getClass().getName(), "jqxGridToExcelXLS.jsp", "XMLStreamException is caught. Failed convert XML to Excel", e);
        e.printStackTrace();
    } catch (AppException e) {
        l.logp(Level.SEVERE, getClass().getName(), "jqxGridToExcelXLS.jsp", "AppException is caught. Failed convert XML to Excel", e);
        e.printStackTrace();
    } catch (Exception e) {
        l.logp(Level.SEVERE, getClass().getName(), "jqxGridToExcelXLS.jsp", "Exception is caught. Failed convert XML to Excel", e);
        e.printStackTrace();
    } finally {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (XMLStreamException e) {
            l.logp(Level.SEVERE, getClass().getName(), "jqxGridToExcelXLS.jsp", "Error when closing XMLStreamReader.");
            e.printStackTrace();
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            l.logp(Level.SEVERE, getClass().getName(), "jqxGridToExcelXLS.jsp", "Error when closing InputStream.");
            e.printStackTrace();
        }
    }

    for(int i =0; i < widthList.size(); i++){
        sheet.setColumnWidth(i, widthList.get(i) * 45);
    }

/*    //if column is less than the header column, retrieve header column count
    if (gridColumnCount < 3) {
        gridColumnCount = 3;
    }

    // auto size the column
    for (int j = 0; j < gridColumnCount; j++) {
        wb.getSheetAt(0).autoSizeColumn((short) j);
    }*/

    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Content-Type", "application/vnd.ms-excel; charset="+encoding);
    response.setHeader("Content-Disposition", dispType + "; filename=" + fileName);
    response.addHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

    ServletOutputStream outStream = response.getOutputStream();

    response.resetBuffer();
    wb.write(outStream);
    outStream.flush();

%>