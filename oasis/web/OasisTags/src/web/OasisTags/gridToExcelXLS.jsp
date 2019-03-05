<%@page import="org.apache.poi.hssf.usermodel.*" %><%@page import="java.io.*" %><%@ page import="dti.oasis.tags.OasisGrid" %><%@ page import="dti.oasis.util.StringUtils" %><%@ page import="dti.oasis.filter.CharacterEncodingFilter" %><%@ page import="dti.oasis.app.ApplicationContext" %><%@ page import="dti.oasis.util.LogUtils" %><%@ page import="java.util.logging.Logger" %><%@ page import="java.util.List" %><%@ page import="java.util.ArrayList" %><%@ page import="java.util.logging.Level" %><%@ page import="dti.oasis.tags.XMLGridHeader" %><%@ page import="org.apache.poi.ss.usermodel.*" %><%@ page import="dti.oasis.util.FormatUtils" %><%@ page import="java.text.NumberFormat" %><%@ page import="dti.oasis.util.LocaleUtils" %><%@ page import="java.text.DecimalFormat" %><%@ page import="org.apache.poi.xssf.usermodel.XSSFWorkbook" %><%
//
// Per http://stackoverflow.com/questions/11226603/create-an-excel-file-for-users-to-download-using-apache-poi-jsp:
//    The important trick is to make sure there is only one line with all your imports and other directives prior
//     to the opening characters.
//    Otherwise, the jsp may output some initial new lines and corrupt your output.
//

    Logger l = LogUtils.enterLog(getClass(), "gridToExcelXLS.jsp");

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
    if(exportType.equalsIgnoreCase("XLS"))
        fileExt = ".xls";

    String gridId = request.getParameter("gridId");
    String fileName = "";
    if (StringUtils.isBlank(gridId)) {
        fileName = "grid"+fileExt;
    } else {
        fileName = gridId+fileExt;
    }

    String sColNames = request.getParameter("colNames");

    String sColDataTypes = request.getParameter("colDataTypes");

    String values = request.getParameter("textForFile");

    Workbook wb = null;
    if(exportType.equalsIgnoreCase("XLSX"))
        wb = new XSSFWorkbook();
    else
        wb = new HSSFWorkbook();

    Sheet sheet = wb.createSheet("Sheet1");
    CreationHelper createHelper = wb.getCreationHelper();

    //TODO: For future reference, if needed
//    List<CellStyle> cellStyles = new ArrayList<CellStyle>();
//    if (!StringUtils.isBlank(sColDataTypes)) {
//        for (String type: sColDataTypes.split(",")) {
//            int nType = 1;
//            try {
//                nType = Integer.parseInt(type);
//            } catch (NumberFormatException e) {
//                l.logp(Level.WARNING, getClass().getName(), "jsp_service_method", "Failed to parse '"+type+"' as a number.");
//            }
//            System.out.println("DataType = " + nType);
//            CellStyle style = wb.createCellStyle();
//            //TODO: Add All DATE and DATE_TIME formats
//            //TODO: Use Existing format patterns from fields where defined
//            switch (nType) {
//                case XMLGridHeader.TYPE_FORMATDATE:
//                case XMLGridHeader.TYPE_UPDATEONLYDATE:
//                    System.out.println("DataType IS DATE: "+FormatUtils.getDateFormatForDisplayString());
////                    style.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
////                    style.setDataFormat(createHelper.createDataFormat().getFormat(FormatUtils.getDateFormatForDisplayString()));
//                    break;
//                case XMLGridHeader.TYPE_FORMATDATETIME:
//                case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
//                    System.out.println("DataType IS DATE_TIME");
//                    style.setDataFormat(createHelper.createDataFormat().getFormat(FormatUtils.getDateTimeFormatForDisplayString()));
//                    break;
//                /* Leave money as String since it already has the formatting characters */
//                case XMLGridHeader.TYPE_FORMATMONEY:
//                case XMLGridHeader.TYPE_UPDATEONLYMONEY:
//                    System.out.println("Locale: "+LocaleUtils.getOasisLocale().getDisplayName());
//                    NumberFormat nf = NumberFormat.getCurrencyInstance(LocaleUtils.getOasisLocale());
//                    System.out.println("Symbol 1: "+nf.getCurrency().getSymbol());
//                    DecimalFormat df = (DecimalFormat)nf;
//                    System.out.println("Symbol 2: "+df.getCurrency().getSymbol());
//                    String pattern = df.toPattern();
//                    String localizedPattern = df.toPattern();
//                    System.out.println("DataType IS MONEY: Pattern:"+pattern+" LOCALIZED PATTERN: "+localizedPattern);
////                    style.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));
////                    style.setDataFormat(createHelper.createDataFormat().getFormat(pattern));
//                    break;
//
//            }
//            cellStyles.add(style);
//        }
//    }
    //Create Styles
    CellStyle cs;
    CellStyle csBold;

    //Bold Fond
    Font bold = wb.createFont();
    bold.setBoldweight(Font.BOLDWEIGHT_BOLD);

    //Bold style
    csBold = wb.createCellStyle();
//        csBold.setBorderBottom(CellStyle.BORDER_THIN);
    csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    csBold.setFont(bold);

    int rowIdx = 0, colIdx = 0;

    //Get Mode
    String displayMode = ApplicationContext.getInstance().getProperty("gridExportExcel.infoHeaderMode", "HEADER");
    if(!displayMode.equalsIgnoreCase("NONE")) {
        //Get current Date and Time
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
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

        if(displayMode.equalsIgnoreCase("HEADER")){
            Header header = sheet.getHeader();
            header.setLeft(HSSFHeader.fontSize((short) 9)+"Exported By: "+exportUser+"\n"+"Exported From: "+pageName);
            header.setRight(HSSFHeader.fontSize((short) 9)+"Exported On: "+dateString);
        } else if(displayMode.equalsIgnoreCase("FOOTER")){
            Footer footer = sheet.getFooter();
            footer.setLeft(HSSFFooter.fontSize((short) 9)+"Exported By: "+exportUser+"\n"+"Exported From: "+pageName);
            footer.setRight(HSSFFooter.fontSize((short) 9)+"Exported On: "+dateString);
        } else if(displayMode.equalsIgnoreCase("BODY"))  {
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
    //Column Headers
    if (!StringUtils.isBlank(sColNames)) {
        Row columnHeader = sheet.createRow(rowIdx++);
        for (String columnName: sColNames.split(",")) {
            Cell cell = columnHeader.createCell(colIdx);
            cell.setCellValue(columnName.replaceAll(":;:",","));
            cell.setCellStyle(csBold);
            colIdx++;
        }
    }

    if(!StringUtils.isBlank(values)){
        for (String rowData: values.split("\n")) {
            rowData = rowData.trim();
            if(rowData.length()>0){
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;
                if (rowData.length() >= 2)
                    rowData = rowData.substring(1, rowData.length() - 1);
                for (String value: rowData.split("\",\"")) {
                    Cell cell = row.createCell(colIdx);
        //            if (value.length() > 2) {
        //                cell.setCellValue(value.substring(1, value.length()-1));
        //            }
                    cell.setCellValue(value.replaceAll(":;:","\n"));
                    //Used to set cell styles: see note above.
        //            if (cellStyles.size() > colIdx) {
        //                cell.setCellStyle(cellStyles.get(colIdx));
        //            }
                    colIdx++;
                }
            }
        }
    } else {
        l.logp(Level.WARNING, getClass().getName(), "jsp_service_method", "values(data) is EMPTY "+values);
    }


// write it as an excel attachment
    ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
    wb.write(outByteStream);
    byte [] outArray = outByteStream.toByteArray();

// Per http://stackoverflow.com/questions/1664996/weblogic-exceeded-stated-content-length-error
//    response.setContentLength(outArray.length);
//    response.setHeader("Expires:", "0"); // eliminates browser caching
    response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
    response.setHeader("Content-Type", "application/vnd.ms-excel; charset=" + encoding);
    response.setHeader("Content-Disposition", "attachment; filename="+fileName);
    response.addHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    OutputStream outStream = response.getOutputStream();
    outStream.write(outArray);
    outStream.flush();

%>