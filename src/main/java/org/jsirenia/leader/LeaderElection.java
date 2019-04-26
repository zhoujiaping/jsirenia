package org.jsirenia.leader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 领导选举
 * 
 * @author zhoujiaping
 * problem：
 * 节点变化，不会即时通知到各个节点，会有延迟，当主节点挂了，在一小段时间内，会有主节点不存在的情况。
 * 当一个节点成为主节点之后，如果zk连接异常，导致收不到zk的通知，那么可能导致存在多个节点认为自己是主节点。
 * 参考
 * https://blog.csdn.net/johnson_moon/article/details/78809995
 */
public class LeaderElection {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String LEADER_PATH = "/leader";
	public static final String CANDIDATE_NODE = "/candidate";
	public static final String ROLE_LEADER = "ROLE_LEADER";
	public static final String ROLE_FLOWER = "ROLE_FLOWER";
	private String thisNode = null;
	private ZkClient zkClient;
	private String role = ROLE_FLOWER;
	private IZkChildListener childListener = (path, nodes) -> {
		logger.info("监听到zk事件：path={}，nodes={}，thisNode={}",path,JSON.toJSONString(nodes),thisNode);
		selectLeader(nodes);
	};
	private void selectLeader(List<String> nodes) {
		if (nodes.isEmpty()) {
			role = ROLE_FLOWER;
			return;
		}
		Collections.sort(nodes);
		String minNode = nodes.get(0);
		if (minNode.equals(thisNode)) {
			logger.info("当前节点成为leader节点");
			role = ROLE_LEADER;
		}
	}
	public String getRole(){
		return role;
	}
	public boolean isLeader(){
		return ROLE_LEADER.equals(this.role);
	}
	public boolean isFlower(){
		return ROLE_FLOWER.equals(this.role);
	}
	public void init(ZkClient zkClient){
		this.zkClient = zkClient;
		//ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
		zkClient.subscribeChildChanges(LEADER_PATH, childListener);
		boolean existsLeader = zkClient.exists(LEADER_PATH);
		if (!existsLeader) {
			logger.info("创建{}",LEADER_PATH);
			zkClient.createPersistent(LEADER_PATH, true);
		}
		String value = zkClient.createEphemeralSequential(LEADER_PATH+CANDIDATE_NODE, null);
		thisNode = value.substring((LEADER_PATH+"/").length());
		//List<String> nodes = zkClient.getChildren(LEADER_PATH);
		//selectLeader(nodes);
	}
	public void destroy(){
		zkClient.unsubscribeChildChanges(LEADER_PATH, childListener);
	}
	public static void main(String[] args) throws IOException {
		ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
		LeaderElection ele = new LeaderElection();
		ele.init(zkClient);
		System.in.read();
		zkClient.close();
	}
}
