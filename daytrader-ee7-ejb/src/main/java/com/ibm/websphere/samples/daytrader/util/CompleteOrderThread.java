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
package com.ibm.websphere.samples.daytrader.util;

import jakarta.inject.Inject;
import javax.naming.InitialContext;
import jakarta.transaction.UserTransaction;

import org.hibernate.TransactionException;

import com.ibm.websphere.samples.daytrader.TradeServices;
import com.ibm.websphere.samples.daytrader.direct.TradeDirect;
import com.ibm.websphere.samples.daytrader.ejb3.TradeSLSBBean;

public class CompleteOrderThread implements Runnable {

        // TODO (chmay): Can't inject here, but out of scope for current commit.
        @Inject
        TradeSLSBBean tradeSLSBBean;

        // TODO (chmay): Can't inject here, but out of scope for current commit.
        @Inject
        TradeDirect tradeDirect;

        final Integer orderID;
        boolean twoPhase;
        
        
        public CompleteOrderThread (Integer id, boolean twoPhase) {
            orderID = id;
            this.twoPhase = twoPhase;
        }
        
        @Override
        public void run() {
            TradeServices trade;
            UserTransaction ut = null;
            
            try {
                // TODO: Sometimes, rarely, the commit does not complete before the find in completeOrder (leads to null order)
                // Adding delay here for now, will try to find a better solution in the future.
                Thread.sleep(500);
                
                InitialContext context = new InitialContext();
                ut = (UserTransaction) context.lookup("java:comp/UserTransaction");
                
                ut.begin();
                
                // TODO (chmay): this is ugly, but also the closest "direct" translation.
                // TODO (chmay): this paradigm is used in MULTUPLE placesl review what should be done here.
                if (TradeConfig.getRunTimeMode() == TradeConfig.EJB3) {
                    trade = tradeSLSBBean;
                } else {
                    trade = tradeDirect;
                }
                
                trade.completeOrder(orderID, twoPhase);
                
                ut.commit();
            } catch (Exception e) {
                
                try {
                    ut.rollback();
                } catch (Exception e1) {
                    throw new TransactionException(e1.getMessage(), e);
                } 
                throw new TransactionException(e.getMessage(), e);
            } 
        }
}
