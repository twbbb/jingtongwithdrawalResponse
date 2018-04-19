package com.twb.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twb.service.WithdrawalBackService;
import com.twb.thread.WithdrawalResponseRunnable;

@Component
public class DoWithdrawalTask
{
	final static Logger logger = LoggerFactory.getLogger(DoWithdrawalTask.class);

//井通转账，线程多了，好像很容易异常
	private int threadNum = 1;
	
	@Autowired
	WithdrawalBackService withdrawalBackServiceImp;

	// 定义在构造方法完毕后，执行这个初始化方法
	@PostConstruct
	public void init()
	{

		

		// 添加处理线程
		ExecutorService executorService = Executors.newFixedThreadPool(threadNum, new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});
		
		
		  for (int i = 0; i < threadNum; ++i) {
			  WithdrawalResponseRunnable runnable = new WithdrawalResponseRunnable();
			  runnable.setWithdrawalBackServiceImp(withdrawalBackServiceImp);
              executorService.execute(runnable);
          }
		
		
		
	}
}
