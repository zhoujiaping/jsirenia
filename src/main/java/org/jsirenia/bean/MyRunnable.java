package org.jsirenia.bean;

import java.util.ArrayList;
import java.util.List;

public class MyRunnable implements Runnable{
	private int mno;//机器数
	private int tno;//线程数
	private int no;//机器号
	private int i;//线程号
	public MyRunnable(int mno,int tno,int no,int i){
		this.mno = mno;
		this.tno = tno;
		this.no = no;
		this.i = i;
	}
	@Override
	public void run() {
		try {
			// 查询列表
			List<Integer> iouIdList = new ArrayList<>();
			for (int k = 0; k < 1000; k++) {
				iouIdList.add(k);
			}
			int total = 0;
			// 遍历
			for(int j=0;j<iouIdList.size();j++){
				if(j%(mno*tno)==no*tno+i){
					total++;
					System.out.println("线程["+i+"]对借据["+j+"]发起代扣");
				}
			}
			System.out.println(total);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
