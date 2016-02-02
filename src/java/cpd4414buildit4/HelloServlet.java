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
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        try (PrintWriter out = response.getWriter()) {
            String name = request.getParameter("name");
            if (name == null) {
                name = "World";
            }
            out.println("Hello " + name + "!");
            request.getServletContext().getSessionCookieConfig().setHttpOnly(true);
            HttpSession session = request.getSession();
            if (session != null) {
                String prevName = (String) session.getAttribute("prevName");
                out.println("Weren't you " + prevName + " before?");
                request.getSession().setAttribute("prevName", name);
            }
        } catch (IOException ex) {
            System.err.println("Something Went Wrong: " + ex.getMessage());
        }
    }
}
