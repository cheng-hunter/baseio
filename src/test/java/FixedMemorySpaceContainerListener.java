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

/**
 * 类描述:
 * 容器监听器
 *
 * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
 * @version 1.0
 * @date 2018-11-07 22:23
 */
public interface FixedMemorySpaceContainerListener {
    /**
     * 方法描述:<br>
     *     当前添加成功
     * @since　
     * @param
     * @return void
     * @throws
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07
     */
    void putComplete();


    /**
     * 方法描述:<br>
     * @since　
     * @param remainderCapacity  剩余空间
     * @param size 元素数量
     * @return void
     * @throws 
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07 
     */
    void putFail(int remainderCapacity,int size);

    /**
     * 方法描述:<br>
     *     当前容器清空事件
     * @since　
     * @param
     * @return void
     * @throws
     * @author <a>顺丰科技开源(opensource@sfmail.sf-express.com)</a>
     * @date 2018/11/07
     */
    void empty();
}
