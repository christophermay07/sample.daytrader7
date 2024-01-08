/**
 * (C) Copyright IBM Corporation 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.websphere.samples.daytrader.web.prims.ejb3;

import java.io.IOException;

import javax.naming.InitialContext;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ibm.websphere.samples.daytrader.ejb3.TradeSLSBBean;
import com.ibm.websphere.samples.daytrader.entities.QuoteDataBean;
import com.ibm.websphere.samples.daytrader.util.Log;
import com.ibm.websphere.samples.daytrader.util.TradeConfig;

/**
 *
 * PingServlet2Session2Entity tests key functionality of a servlet call to a
 * stateless SessionEJB, and then to a Entity EJB representing data in a
 * database. This servlet makes use of the Stateless Session EJB {@link Trade},
 * and then uses {@link TradeConfig} to generate a random stock symbol. The
 * stocks price is looked up using the Quote Entity EJB.
 *
 */
@WebServlet(name = "ejb3.PingServlet2Session2Entity2JSP", urlPatterns = { "/ejb3/PingServlet2Session2Entity2JSP" })
public class PingServlet2Session2Entity2JSP extends HttpServlet {

    private static final long serialVersionUID = -8966014710582651693L;

    @Inject
    TradeSLSBBean tradeSLSBBean;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String symbol = null;
        QuoteDataBean quoteData = null;
        ServletContext ctx = getServletConfig().getServletContext();

        try {
            try {
                int iter = TradeConfig.getPrimIterations();
                for (int ii = 0; ii < iter; ii++) {
                    symbol = TradeConfig.rndSymbol();
                    // getQuote will call findQuote which will instaniate the
                    // Quote Entity Bean
                    // and then will return a QuoteObject
                    quoteData = tradeSLSBBean.getQuote(symbol);
                }

                req.setAttribute("quoteData", quoteData);
                // req.setAttribute("hitCount", hitCount);
                // req.setAttribute("initTime", initTime);

                ctx.getRequestDispatcher("/quoteDataPrimitive.jsp").include(req, res);
            } catch (Exception ne) {
                Log.error(ne, "PingServlet2Session2Entity2JSP.goGet(...): exception getting QuoteData through Trade");
                throw ne;
            }

        } catch (Exception e) {
            Log.error(e, "PingServlet2Session2Entity2JSP.doGet(...): General Exception caught");
            res.sendError(500, "General Exception caught, " + e.toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "web primitive, tests Servlet to Session to Entity EJB to JSP path";

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // hitCount = 0;
        // initTime = new java.util.Date().toString();
    }
}