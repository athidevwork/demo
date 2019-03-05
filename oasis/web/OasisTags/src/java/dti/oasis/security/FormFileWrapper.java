package dti.oasis.security;


import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FormFileWrapper implements FormFile {

   public FormFile getFormFile() {
      return formFile;
   }

   public void setFormFile(FormFile formFile) {
      this.formFile = formFile;
      this.fileName = formFile.getFileName();
   }

   @Override
   public String getContentType() {
      return formFile.getContentType();
   }

   @Override
   public void setContentType(String s) {

   }

   @Override
   public int getFileSize() {
      return formFile.getFileSize();
   }

   @Override
   public void setFileSize(int i) {

   }

   @Override
   public String getFileName() {
      return this.fileName;
   }

   @Override
   public void setFileName(String fileName) {
      this.fileName = fileName;

   }

   @Override
   public byte[] getFileData() throws FileNotFoundException, IOException {
      return formFile.getFileData();
   }

   @Override
   public InputStream getInputStream() throws FileNotFoundException, IOException {
      return formFile.getInputStream();
   }

   @Override
   public void destroy() {
      formFile = null;
      fileName = null;
   }

   FormFile formFile;
   String fileName;
}