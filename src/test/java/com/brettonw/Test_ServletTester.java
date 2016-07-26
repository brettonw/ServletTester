package com.brettonw;

import com.brettonw.bag.*;
import com.brettonw.bag.formats.MimeType;
import com.brettonw.servlet.ServletTester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public class Test_ServletTester extends HttpServlet {
    private static final Logger log = LogManager.getLogger (Test_ServletTester.class);

    public static final String OK_KEY = "ok";
    public static final String STATUS_KEY = "status";
    public static final String POST_DATA_KEY = "post-data";

    ServletTester servletTester;

    public Test_ServletTester () {
        servletTester = new ServletTester (this);
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug ("doGet");
        makeResponse (response, new BagObject ()
                .put (STATUS_KEY, request.getQueryString ()).toString ());
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug ("doPost");
        SourceAdapter sourceAdapter = new SourceAdapterReader (request.getInputStream (), MimeType.JSON);
        String requestString = sourceAdapter.getStringData ();
        BagObject postData = BagObjectFrom.string (requestString);
        makeResponse (response, new BagObject ()
                .put (STATUS_KEY, request.getQueryString ())
                .put (POST_DATA_KEY, postData)
                .toString ());
    }

    public void makeResponse (HttpServletResponse response, String responseText) throws IOException {
        // set the response types
        String UTF_8 = StandardCharsets.UTF_8.name ();
        response.setContentType (MimeType.JSON + "; charset=" + UTF_8);
        response.setCharacterEncoding (UTF_8);

        // write out the response
        PrintWriter out = response.getWriter ();
        out.println (responseText);
        //out.flush ();
        out.close ();
    }

    @Test
    public void testGet () throws IOException {
        BagObject bagObject = servletTester.bagObjectFromGet ("OK");
        assertTrue (OK_KEY.equalsIgnoreCase (bagObject.getString (STATUS_KEY)));
    }
    @Test
    public void testPost () throws IOException {
        BagObject postData = BagObjectFrom.resource (getClass (), "/testPost.json");
        BagObject bagObject = servletTester.bagObjectFromPost ("OK", postData);
        assertTrue (OK_KEY.equalsIgnoreCase (bagObject.getString (STATUS_KEY)));
    }
}
