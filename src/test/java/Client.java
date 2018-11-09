/*
 *  Copyright [2018]
 *  顺丰科技开源(opensource@sfmail.sf-express.com)
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.generallycloud.baseio.codec.fixedlength.FixedLengthCodec;
import com.generallycloud.baseio.codec.fixedlength.FixedLengthFrame;
import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.common.ThreadUtil;
import com.generallycloud.baseio.component.*;
import com.generallycloud.baseio.protocol.Frame;

import java.lang.management.ManagementFactory;

/**
 * 类描述:
 *
 * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
 * @date 2018-11-07 18:28
 */
public class Client {
    public static void main(String[] args) throws Exception {
        IoEventHandle eventHandle = new IoEventHandle() {
            @Override
            public void accept(NioSocketChannel channel, Frame frame) throws Exception {
                FixedLengthFrame f = (FixedLengthFrame) frame;
                System.out.println();
                System.out.println("____________________" + f.getReadText());
                System.out.println();
            }

        };
        ChannelContext context = new ChannelContext(8300);
        ChannelConnector connector = new ChannelConnector(context);
        context.setIoEventHandle(eventHandle);
        context.addChannelEventListener(new LoggerChannelOpenListener());
        context.setProtocolCodec(new FixedLengthCodec());
        NioSocketChannel channel = connector.connect();
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());

        for (int i=0;i<1000000000;i++) {
            FixedLengthFrame frame = new FixedLengthFrame();
            frame.write(i+"=>hello server1111111111111111111111111111111111111111111111111111111sdadasdasdsadsadasda1111111111111111111111111111111111111111111111111111!", channel);
            channel.flush(frame);
            ThreadUtil.sleep(100);
        }
//        CloseUtil.close(connector);
    }
}
