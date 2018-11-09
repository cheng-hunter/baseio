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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类描述:
 * 固定大小空间的容器
 * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
 * @version 1.0
 * @date 2018-11-07 20:36
 */
public class FixedMemorySpaceContainer {
    /**
     * 容器大小 (默认 10MB)
     */
    private int capacity =10 * 1024 * 1024;
    /**
     * 单个元素大小 1K
     */
    private int elementSize=1024;
    /**
     * 当前容器占用
     */
    private volatile transient AtomicInteger count;
    /**
     * 当前元素大小
     */
    private volatile transient AtomicInteger size;

    private FixedMemorySpaceContainerListener listener;

    private ConcurrentLinkedQueue<byte[]> buffer=new ConcurrentLinkedQueue<byte[]>();


    /**
    * 方法描述:<br>
     *     创建容器
     *      默认 单个元素的大小 1KB
     *      默认 容器的容量大小 10MB
    * @since　
    * @param
    * @return
    * @throws
    * @author
    * @date
    */
    public FixedMemorySpaceContainer(){
        count=new AtomicInteger(0);
        size=new AtomicInteger(0);
    }


    public void setListener(FixedMemorySpaceContainerListener listener) {
        this.listener = listener;
    }

    /**
    * 方法描述:<br>
     *  创建容器
    * @since　
    * @param elementSize 单个元素的大小，单位字节
    * @param capacity  容器的容量  单位字节
    * @return
    * @throws
    * @author
    * @date
    */
    public FixedMemorySpaceContainer(int elementSize,int capacity){
        this();
        this.elementSize=elementSize;
        this.capacity = capacity;
    }

    /**
     * 存入数据(无阻塞)
     * @param src
     */
    public void put(byte [] src){
        //如果当前数据为null或者超过单个元素长度
        if(src==null||src.length>elementSize){
            return;
        }
        //如果当前容量超限
        if(count.get()+src.length> capacity){
            if(listener!=null){
                listener.putFail(remainderCapacity(),size());
            }
            return;
        }
        synchronized (count){
            buffer.offer(src);
            count.addAndGet(src.length);
            size.getAndIncrement();
            if(listener!=null){
                listener.putComplete();
            }
        }
    }

    /**
     * 获取阻塞,可能返回空
     * @return
     */
    public byte[] remove(){
        /**
         * 当前已经没有数据
         */
         if(count.get()==0||size.get()==0){
             if(listener!=null){
                 listener.empty();
             }
             return null;
         }
         synchronized (count){
             byte[] rlt=buffer.poll();
             count.addAndGet(-rlt.length);
             size.getAndDecrement();
             return rlt;
         }
    }
    /**
     * 方法描述:<br>
     *     返回当前容器剩余容量
     * @since　
     * @param
     * @return int
     * @throws
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07
     */
    public int remainderCapacity(){
        return capacity-count.get();
    }

    /**
     * 方法描述:<br>
     *     获取当前容器的内存占用
     * @since　
     * @param
     * @return int
     * @throws
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07
     */
    public int memorySize(){
        return count.get();
    }
    /**
     * 方法描述:<br>
     *     获取当前元素的size
     * @since　
     * @param
     * @return int
     * @throws
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07
     */
    public int size(){
        return size.get();
    }

    public static void main(String[] args) {
        final FixedMemorySpaceContainer container=new FixedMemorySpaceContainer();
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                     while (true){
                         try {
                             container.put("aaaaaaa".getBytes());
                             Thread.sleep(500);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                     }
                }
            }.start();
        }


        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                       while (true) {
                           try {
                               System.out.println(new String(container.remove()));
                               Thread.sleep(1000);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                }
            }.start();
        }

    }
}
