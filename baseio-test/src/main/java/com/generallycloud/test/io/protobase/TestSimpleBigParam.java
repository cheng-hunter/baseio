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
package com.generallycloud.test.io.protobase;

import com.generallycloud.baseio.codec.protobase.ProtobaseCodec;
import com.generallycloud.baseio.codec.protobase.ProtobaseFuture;
import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.common.FileUtil;
import com.generallycloud.baseio.component.ChannelConnector;
import com.generallycloud.baseio.component.ChannelContext;
import com.generallycloud.baseio.component.LoggerSocketSEListener;
import com.generallycloud.baseio.configuration.Configuration;
import com.generallycloud.baseio.container.protobase.FixedChannel;
import com.generallycloud.baseio.container.protobase.SimpleIoEventHandle;

public class TestSimpleBigParam {

    public static void main(String[] args) throws Exception {

        String serviceKey = "TestSimpleServlet";
        SimpleIoEventHandle eventHandle = new SimpleIoEventHandle();
        Configuration configuration = new Configuration(8300);
        ChannelContext context = new ChannelContext(configuration);
        ChannelConnector connector = new ChannelConnector(context);
        context.setIoEventHandle(eventHandle);
        context.setProtocolCodec(new ProtobaseCodec());
        context.addChannelEventListener(new LoggerSocketSEListener());
        FixedChannel channel = new FixedChannel(connector.connect());
        String temp = "网易科技腾讯科技阿里巴巴";
        StringBuilder builder = new StringBuilder(temp);
        for (int i = 0; i < 600000; i++) {
            builder.append("\n");
            builder.append(temp);
        }
        ProtobaseFuture future = channel.request(serviceKey, builder.toString());
        FileUtil.writeByCls(TestSimpleBigParam.class.getName(), future.getReadText());
        System.out.println("处理完成");
        CloseUtil.close(connector);
    }

}
