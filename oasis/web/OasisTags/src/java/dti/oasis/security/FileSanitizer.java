package dti.oasis.security;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.upload.FormFile;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Sept 27, 2017
 *
 * @author cesar valencia
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class FileSanitizer {

   public boolean validateFile (FormFileWrapper file) {
      if (file != null && !StringUtils.isBlank(file.getFileName()) && file.getFileSize() > 0) {
         if (!MessageManager.getInstance().hasMessage(CORE_SECURITY_FILE_UPLOAD_ERROR_MESSAGE)) {
            validateFileNotAllowed(file);
            validateMissingFileExt(file);
            validateForNonAscii(file);
            validateFileSize(file);
            validateFileNameLen(file);
            validateFileAllowDoubleExt(file);
            validateFileExt(file);
         }
      }
      return true;
   }

   public void validateFileNotAllowed(FormFile file) {
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateFileNotAllowed", new Object[]{file});
      }

      String fileNotAllowed =  getSystemParameterValue(FILE_NOT_ALLOWED,"");
      String fileNotAllowedArray[] = null;
      String fileName = file.getFileName();

      if (!StringUtils.isBlank(fileNotAllowed)) {
         fileNotAllowedArray = fileNotAllowed.split(",");
      }

      if (c_l.isLoggable(Level.FINER)) {
         addlMsg = "File not allowed: " + fileNotAllowedArray.toString();
         c_l.exiting(getClass().getName(), "validateFileNotAllowed: " + formatMessage(file, addlMsg));
      }

      if (fileNotAllowedArray != null) {
         if (isFileExtExist(fileNotAllowedArray, fileName)) {
            throwErrorMessage("core.security.file.upload.file.not.allowed", null);
         }
      }
   }

   public void validateForNonAscii(FormFile file) {
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateForNonAscii", new Object[]{file});
      }
      String fileName = file.getFileName();

      CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "validateForNonAscii: " + formatMessage(file, null));
      }

      if (!asciiEncoder.canEncode(fileName)) {
         throwErrorMessage("core.security.file.upload.non.ascii", null);
      }
   }


   public void validateFileExt(FormFile file){
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateWhiteBlackList", new Object[]{file});
      }

      String applicationContextFileExtArray[] = ApplicationContext.getInstance().getProperty(APPLICATION_CONTEXT_CONSTANT, "").split(",");
      String whiteListFileExt =  getSystemParameterValue(FILE_EXT_WHT_LST,"");
      String blackListFileExt = getSystemParameterValue(FILE_EXT_BLK_LST,"");
      String whiteListArray[] = null;
      String blackListArray[] = null;

      String fileName = file.getFileName();

      int index = fileName.lastIndexOf(".");
      String fileExt = fileName.substring(index + 1);

      if (fileExt.equalsIgnoreCase("oasis")) {
         int count =  file.getFileName().length() - file.getFileName().replace(".", "").length();
         if (count > 1) {
            return;
         }
      }

      if (!StringUtils.isBlank(whiteListFileExt)) {
         whiteListArray = whiteListFileExt.split(",");
      }
      if (!StringUtils.isBlank(blackListFileExt)) {
         blackListArray = blackListFileExt.split(",");
      }

      if (c_l.isLoggable(Level.FINER)) {
         addlMsg = "File ext: " + fileExt;
         addlMsg+= ", appContent-core: " + applicationContextFileExtArray.toString();
         addlMsg+= ", white list: " + whiteListFileExt;
         addlMsg+= ", black list: " + blackListFileExt;
         c_l.exiting(getClass().getName(), "validateFileExt: " + formatMessage(file, addlMsg));
      }

      if (whiteListArray == null && blackListArray == null) {
         if (isFileExtExist(applicationContextFileExtArray, fileExt)) {
            throwErrorMessage("core.security.file.upload.file.ext.not.allowed", new String[] {fileExt});
         }
      }

      if (isFileExtExist(blackListArray, fileExt)) {
         throwErrorMessage("core.security.file.upload.file.ext.not.allowed", new String[] {fileExt});
      }

      if (!StringUtils.isBlank(whiteListFileExt) && !isFileExtExist(whiteListArray, fileExt)) {
         throwErrorMessage("core.security.file.upload.file.ext.not.allowed", new String[] {fileExt});
      }

      String newFileExt = renameFileExt(file);
      if (!StringUtils.isBlank(newFileExt)) {
         file.setFileName(file.getFileName() + newFileExt);
      }

   }

   public String renameFileExt(FormFile file) {
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "renameFileExt", new Object[]{file});
      }

      String newExt = "";
      String fileExtRenameList =  getSystemParameterValue(FILE_EXT_RENAME,"");
      String fileExtRenameArray[] = null;

      if(!StringUtils.isBlank(fileExtRenameList)) {
         int index = file.getFileName().lastIndexOf(".");
         String fileExt = file.getFileName().substring(index + 1);

         fileExtRenameArray = fileExtRenameList.split(",");

         if (isFileExtExist(fileExtRenameArray, fileExt)) {
            String whiteListFileExt =  getSystemParameterValue(FILE_EXT_WHT_LST,"");
            String whiteListArray[] = null;

            if (!StringUtils.isBlank(whiteListFileExt)) {
               whiteListArray = whiteListFileExt.split(",");
            }

            if (!StringUtils.isBlank(whiteListFileExt) ) {
               if (!isFileExtExist(whiteListArray, fileExt)) {
                  throwErrorMessage("core.security.file.upload.file.rename", new String[] {fileExt});
               }
            }

            addlMsg = "File ext that can be renamed: " + fileExtRenameList;
            addlMsg += ", File ext: " + fileExt;
            c_l.exiting(getClass().getName(), "renameFileExt: " + formatMessage(file, addlMsg));
            newExt = FILE_EXT_RENAME_OASIS;
         }
      }

      return newExt;
   }

   public void validateFileSize(FormFile file){
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateFileSize", new Object[]{file});
      }
      long fileMaxSize = 0;
      long fileSize = file.getFileSize();

      String maxFileSize = getSystemParameterValue(FILE_MAX_SIZE,"0");
      try {
         fileMaxSize =  Long.parseLong(maxFileSize);
      } catch(Exception ex) {
         MessageManager.getInstance().addWarningMessage("core.security.file.upload.file.size.invalid");
      }

      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "validateFileSize: " + formatMessage(file, "FILE_MAX_SIZE: " + fileMaxSize));
      }

      if (fileMaxSize > 0 && fileSize > 0 ) {
         if (file.getFileSize() > fileMaxSize) {
            throwErrorMessage("core.security.file.upload.file.max.size", new String[] {Long.toString(fileMaxSize)});
         }
      }
   }

   public void validateFileNameLen(FormFile file){
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateFileNameLen", new Object[]{file});
      }
      int fileNameLen = Integer.parseInt(FILENAME_MAX_LEN_SIZE);

      String maxFileNameLen = getSystemParameterValue(FILENAME_MAX_LEN,"255");
      try {
         fileNameLen = Integer.valueOf(maxFileNameLen);
      } catch(Exception ex) {
         MessageManager.getInstance().addWarningMessage("core.security.file.upload.file.max.len.invalid");
      }

      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "validateFileNameLen: " + formatMessage(file, "FILENAME_MAX_LEN: " + fileNameLen));
      }

      if (file.getFileName().length() > fileNameLen) {
         throwErrorMessage("core.security.file.upload.file.name.length", new String[] {Integer.toString(fileNameLen)});
      }
   }

   public void validateFileAllowDoubleExt(FormFile file){
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateFileAllowDoubleExt", new Object[]{file});
      }

      String allowDoubleExt = getSystemParameterValue(FILE_EXT_ALW_DBL,"Y");
      boolean allow = YesNoFlag.getInstance(allowDoubleExt).booleanValue();

      int count =  file.getFileName().length() - file.getFileName().replace(".", "").length();

      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "validateFileAllowDoubleExt: " + formatMessage(file, "FILE_EXT_ALW_DBL: " + allow));
      }

      if(count > 1 && !allow) {
         throwErrorMessage("core.security.file.upload.file.name.allow.double.extension", null);
      }
   }

   public void validateMissingFileExt(FormFile file){
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "validateMissingFileExt", new Object[]{file});
      }

      String fileWithNoExtAllow = getSystemParameterValue(FILE_EXT_MISSING,"Y");
      String fileName = file.getFileName();

      if (c_l.isLoggable(Level.FINER)) {
         addlMsg = "file with no extenseion allow: " + fileWithNoExtAllow;
         c_l.exiting(getClass().getName(), "validateMissingFileExt: " + formatMessage(file, addlMsg));
      }

      if (fileName.lastIndexOf(".") == -1) {
         if (!YesNoFlag.getInstance(fileWithNoExtAllow).booleanValue()) {
            throwErrorMessage("core.security.file.upload.missing.ext", null);
         }
      }
   }

   private boolean isFileExtExist(String extList[], String fileExt) {
      boolean bRc = false;

      if (extList != null) {
         for (String ext : extList) {
            if (ext.trim().toLowerCase().equalsIgnoreCase(fileExt.trim().toLowerCase())) {
               bRc = true;
               break;
            }
         }
      }
      return bRc;
   }

   private void throwErrorMessage(String code, String[] parameters) {
      String description = MessageManager.getInstance().formatMessage(code, parameters);
      MessageManager.getInstance().addErrorMessage(CORE_SECURITY_FILE_UPLOAD_ERROR_MESSAGE, new Object[]{description});
      ValidationException e = new ValidationException(description);
      ValidationException ae = (ValidationException) ExceptionHelper.getInstance().handleException("core.security.file.upload.security.upload", "", e, true);
      throw ae;
   }

   private String getSystemParameterValue(String sysParamCode, String sysParamDefaultValue) {
      String val = "";
      val = SysParmProvider.getInstance().getSysParm(getSubSystem() + sysParamCode, SUB_SYSTEM_VALUE_NOT_FOUND);

      if (val.equalsIgnoreCase(SUB_SYSTEM_VALUE_NOT_FOUND)) {
         val = SysParmProvider.getInstance().getSysParm(sysParamCode, sysParamDefaultValue);
      }

      return val;
   }

   private String formatMessage(FormFile file, String additionalMsg) {
      String msg = "File Name: " + file.getFileName();
      msg+= ", File size: " + file.getFileSize();
      msg+= additionalMsg;
      return msg;
   }

   public FileSanitizer(String subSystem){
      this.subSystem = subSystem;
   }

   private String getSubSystem() {
      return (StringUtils.isBlank(this.subSystem)) ? "" : this.subSystem;
   }

   private String subSystem;
   private String addlMsg;
   private static String SUB_SYSTEM_VALUE_NOT_FOUND = "^";
   private static String FILE_MAX_SIZE = "FILE_MAX_SIZE";
   private static String FILENAME_MAX_LEN = "FILENAME_MAX_LEN";
   private static String FILENAME_MAX_LEN_SIZE = "255";
   private static String FILE_EXT_ALW_DBL = "FILE_EXT_ALW_DBL";
   private static String FILE_EXT_WHT_LST = "FILE_EXT_WHT_LST";
   private static String FILE_EXT_BLK_LST = "FILE_EXT_BLK_LST";

   private static String APPLICATION_CONTEXT_CONSTANT = "file.extension.not.allowed";
   private static String FILE_EXT_RENAME = "FILE_EXT_RENAME";
   private static String FILE_NOT_ALLOWED = "FILE_NOT_ALLOWED";
   private static String FILE_NOT_EXT_ALLW = "FILE_NOT_EXT_ALLW";
   private static String FILE_EXT_MISSING = "FILE_EXT_MISSING";
   private static String FILE_EXT_RENAME_OASIS =".oasis";

   private static String CORE_SECURITY_FILE_UPLOAD_ERROR_MESSAGE = "core.security.file.upload.error.message";
   private final Logger c_l = LogUtils.getLogger(getClass());

}
