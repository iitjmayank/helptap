package com.helptap.interview.part2;

/**
 * Created by maagarwa on 1/14/2016.
 */
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {
    private final static int PORT = 7777;

    public MyServer() throws IOException {
        super(PORT);
        start();
        System.out.println( "\nRunning! Point your browers to http://localhost:7777/ \n" );
    }

    @Override
    public Response serve(IHTTPSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><h1>Upload File</h1>\n");
        sb.append("<form action=\"/uploadfiles\" method=\"post\"  enctype=\"multipart/form-data\">");
        sb.append("<input type=\"file\" name=\"filename\" size=\"50\" />");
        sb.append("<input type=\"submit\" value=\"Upload File\" />");
        sb.append("</form>");
        sb.append("</body></html>\n");
        String msg = "<html><body><h1>Hello server</h1>\n";
        msg += "<p>We serve " + session.getUri() + " !</p>";
        // return newFixedLengthResponse( sb.toString() );

        Map<String, String> headers = session.getHeaders();
        Map<String, String> parms = session.getParms();
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> files = new HashMap<>();

        if (Method.GET.equals(method)) {
            return newFixedLengthResponse(sb.toString());
        }

        if (Method.POST.equals(method) || Method.PUT.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                return getResponse("Internal Error IO Exception: " + ioe.getMessage());
            } catch (ResponseException re) {
                return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
            }
        }

        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) {
            uri = uri.substring(0, uri.indexOf('?'));
        }

        // Other implementation goes here...

        if ("/uploadfiles".equalsIgnoreCase(uri)) {
            String filename, tmpFilePath;
            File src, dst;
            for (Map.Entry entry : parms.entrySet()) {
                if (entry.getKey().toString().substring(0, 8).equalsIgnoreCase("filename")) {
                    filename = entry.getValue().toString();
                    tmpFilePath = files.get(entry.getKey().toString());
                    dst = new File(Environment.getExternalStorageDirectory(), filename);
                    if (dst.exists()) {
                        return getResponse("Internal Error: File already exist");
                    }
                    src = new File(tmpFilePath);
                    if (!copyFile(src, dst)) {
                        return getResponse("Internal Error: Uploading failed");
                    }
                }
            }
            return getResponse("Success");
        }

        return getResponse("Error 404: File not found");
    }

    boolean copyFile(File s, File d) {
        try {
            copy(s,d);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void copy(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private Response getResponse(String message) {
        return createResponse(Response.Status.OK, MIME_PLAINTEXT, message);
    }

    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = newFixedLengthResponse(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }
}
