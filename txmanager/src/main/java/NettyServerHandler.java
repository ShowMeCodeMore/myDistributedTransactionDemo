import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作为事务管理者，它需要：
 * 1. 创建并保存事务组
 * 2. 保存各个子事务在对应的事务组内
 * 3. 统计并判断事务组内的各个子事务状态，以算出当前事务组的状态（提交or回滚）
 * 4. 通知各个子事务提交或回滚
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 事务组中的事务状态列表

    private static Map<String, List<JSONObject>> transactionTypeMap = new HashMap();
    // 事务组是否已经接收到结束的标记
    private static Map<String, Boolean> isEndMap = new HashMap<String, Boolean>();

    // 事务组中应该有的事务个数
    private static Map<String, Integer> transactionCountMap = new HashMap<String, Integer>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.add(ctx.channel());
    }

    /**
     * 创建事务组，并且添加保存事务
     * 并且需要判断，如果所有事务都已经执行了（有结果了，要么回滚，要么提交），且其中有一个事务需要回滚，那么通知所有客户端进行回滚
     * 否则，则通知所有客户端进行提交
     * @param ctx
     * @param msg
     * @throws Exception
     */

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受数据:" + msg.toString());

        JSONObject jsonObject = JSON.parseObject((String) msg);

        String command = jsonObject.getString("command"); // create-创建事务组，add-添加事务
        String groupId = jsonObject.getString("groupId");   // 事务组id
        String transactionType = jsonObject.getString("transactionType"); // 子事务类型，commit-待提交，rollback-待回滚
        Integer transactionCount = jsonObject.getInteger("transactionCount");   // 事务数量
        Boolean isEnd = jsonObject.getBoolean("isEnd"); // 是否是结束事务
        String transactionId = jsonObject.getString("transactionId");//事物ID


        if ("create".equals(command)) {
            // 创建事务组
            transactionTypeMap.put(groupId, new ArrayList<JSONObject>());
        } else if ("add".equals(command)) {
            // 加入事务组
            JSONObject transaction = new JSONObject();
            transaction.put("transactionId",transactionId);
            transaction.put("transactionType",transactionType);
            transactionTypeMap.get(groupId).add(transaction);

            if (isEnd) {
                isEndMap.put(groupId, true);
                transactionCountMap.put(groupId, transactionCount);
            }

            List<JSONObject> resultList = new ArrayList<JSONObject>();
            // 如果已经接收到结束事务的标记，比较事务是否已经全部到达，如果已经全部到达则看是否需要回滚
            if (isEndMap.get(groupId)&& transactionCountMap.get(groupId).equals(transactionTypeMap.get(groupId).size())) {
                //commit
                //commit
                //commit
                String resultCommand = "";
                if (transactionTypeMap.get(groupId).contains("ROLLBACK")){
                    resultCommand = "rollback";
                } else {
                    resultCommand = "commit";
                }
                for (JSONObject o : transactionTypeMap.get(groupId)) {
                    JSONObject result = new JSONObject();
                    result.put("groupId", groupId);
                    result.put("transactionId",o.get("transactionId"));
                    result.put("command",resultCommand);
                    resultList.add(result);
                }
                sendResult(resultList);
            }

        }
    }

    private void sendResult(List<JSONObject> result) {
        for (Channel channel : channelGroup) {
            String s = JSON.toJSONString(result);
            System.out.println("发送数据:" + s);
            channel.writeAndFlush(s);
        }
    }
}
