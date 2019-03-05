<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.io.*" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page language="java"%>
<%!
    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it.
                e.printStackTrace();
            }
        }
    }
%>
<%

    // Constants ----------------------------------------------------------------------------------

    final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    Logger l = LogUtils.enterLog(getClass(), "redirectToNotesImages.jsp");
    //Get the attribute "oasis.notes.imagesDirPath" from the configuration,
    String filePath = SysParmProvider.getInstance().getSysParm(request, "NOTES.IMAGESDIRPATH");

    if (StringUtils.isBlank(filePath))
        filePath = ApplicationContext.getInstance().getProperty("oasis.notes.imagesDirPath");

    l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "filePath="+filePath);

    if (StringUtils.isBlank(filePath)) {

        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "NO filePath="+filePath);
        return;
    } else {

        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "filePath="+filePath);

        // Get requested file by path info.
        String requestedFile = request.getPathInfo();
        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "requestedFile: " + requestedFile);
        // Check if file is actually supplied to the request URI.
        if (requestedFile == null) {
            // Do your thing if the file is not supplied to the request URI.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));
        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "Name: " + file.getName());
        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "file.exists(): " + file.exists());
        // Check if file actually exists in filesystem.
        if (!file.exists()) {
            // Do your thing if the file appears to be non-existing.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Get content type by filename.
        String contentType = request.getServletContext().getMimeType(file.getName());
        l.logp(Level.FINE, getClass().getName(), "jsp_service_method", "Content Type: " + contentType);
        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Init servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
    }


%>