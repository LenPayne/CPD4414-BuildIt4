/*
 * Copyright 2015 Len Payne <len.payne@lambtoncollege.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cpd4414buildit4;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@WebServlet(urlPatterns = "/e2e", asyncSupported = true)
@ServletSecurity(
        value = @HttpConstraint(rolesAllowed = {"USERS"})
)
public class FullExampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext ac = request.startAsync();
        ac.addListener(new AsyncListener() {

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                ServletRequest request = event.getSuppliedRequest();
                String contents = (String) request.getAttribute("contents");
                event.getSuppliedResponse().getWriter().println(contents);
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {

            }

            @Override
            public void onError(AsyncEvent event) throws IOException {

            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {

            }

        });

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.execute(new AsyncReadDB(ac));
    }

    private class AsyncReadDB implements Runnable {

        AsyncContext ac;

        public AsyncReadDB(AsyncContext ac) {
            this.ac = ac;
        }

        @Override
        public void run() {
            Connection connection = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found exception! " + e.getMessage());
            }

            String url = "jdbc:mysql://IPRO:3306/Winter2015";
            try {
                connection = DriverManager.getConnection(url,
                        "Winter2015", "P@ssw0rd");
            } catch (SQLException e) {
                System.out.println("Failed to Connect! " + e.getMessage());
            }

            StringBuilder sb = new StringBuilder();
            if (connection != null) {
                try {
                    String query = "SELECT * FROM sample";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        sb.append(String.format("%s\t%s\n", rs.getString("name"), rs.getString("age")));
                    }
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error SELECTing: " + e.getMessage());
                }
            }

            ac.getRequest().setAttribute("contents", sb.toString());
            ac.complete();
        }

    }
}
