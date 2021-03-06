/*
 * Copyright 2015-2017 GenerallyCloud.com
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
package com.generallycloud.sample.baseio.protobase;

import com.generallycloud.baseio.codec.protobase.ParamedProtobaseFrame;
import com.generallycloud.baseio.collection.Parameters;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.container.protobase.ProtobaseFrameAcceptorService;

public class TestChannelDisconnectServlet extends ProtobaseFrameAcceptorService {

    public static final String SERVICE_NAME = TestChannelDisconnectServlet.class.getSimpleName();

    private TestSimple1        simple1      = new TestSimple1();

    @Override
    protected void doAccept(NioSocketChannel ch, ParamedProtobaseFrame frame) throws Exception {
        Parameters p = frame.getParameters();
        frame.put("dynamic",simple1.dynamic());
        frame.put("param",frame.getFrameName());
        ch.flush(frame);
        ch.close();
    }

}
