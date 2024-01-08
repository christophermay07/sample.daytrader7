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
package com.ibm.websphere.samples.daytrader.web.prims;

import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

@RequestScoped
@PingInterceptorBinding
public class PingCDIBean {

    @Inject
    BeanManager beanManager;

    private static int helloHitCount = 0;
    private static int getBeanManagerHitCountSPI = 0;

    
    public int hello() {
        return ++helloHitCount;
    }

    public int getBeanMangerViaCDICurrent() throws Exception {
        Set<Bean<?>> beans = beanManager.getBeans(Object.class);
        
        if (beans.size() > 0) {
            return ++getBeanManagerHitCountSPI;
        }
        return 0;

    }
}
