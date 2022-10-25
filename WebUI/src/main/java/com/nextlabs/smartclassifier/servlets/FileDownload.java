package com.nextlabs.smartclassifier.servlets;

import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

public class FileDownload extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String driver;
    private String connectionString;
    private String user;
    private String password;
    private OutputStream out;
    private FileInputStream in;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ServletContext context = getServletContext();

        driver = context.getInitParameter("db.driver");
        connectionString = context.getInitParameter("db.connectionString");
        user = context.getInitParameter("db.username");
        String encryptedPassword = context.getInitParameter("db.password");
        password = NxlCryptoUtil.decrypt(encryptedPassword);

        System.out.println("driver = " + driver);
        System.out.println("connectionString = " + connectionString);
        System.out.println("user = " + user);
        System.out.println("password = " + password);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {

            String id = request.getParameter("id");

            System.out.println("Parameter Id received. Id = " + id);

            String path = getSnapShotFolder();

            if (path != null && path.charAt(path.length() - 1) != File.separatorChar) {
                path += File.separator;
            }

            String fileName = path + id;

            System.out.println("The filename to be fetched is = " + fileName);

            File downloadFile = new File(fileName);

            response.setContentType("text/plain");

            ServletContext context = getServletContext();

            // gets MIME type of the file
            String mimeType = context.getMimeType(fileName);

            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }

            System.out.println("MIME type: " + mimeType);

            // modifies response

            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());

            response.setHeader(headerKey, headerValue);

            in = new FileInputStream(downloadFile);
            out = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getSnapShotFolder() {

        Connection connection = null;
        String folderName = "";
        Statement stmt;

        try {

            Class.forName(driver);
            connection = DriverManager.getConnection(connectionString, user, password);

            if (connection == null) {
                System.out.println("The connection object is null");
                return null;
            }

            System.out.println("Connected successfully");

            stmt = connection.createStatement();
            String selectStatement = "SELECT FIXED_VALUE AS VALUE FROM ACTION_PLUGIN_PARAMS WHERE IDENTIFIER = 'snapshots-folder'";
            ResultSet rs = stmt.executeQuery(selectStatement);

            while (rs.next()) {
                folderName = rs.getString("VALUE");
                System.out.println("The snapshot folder = " + folderName);
                break;
            }

            rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return folderName;
    }
}
