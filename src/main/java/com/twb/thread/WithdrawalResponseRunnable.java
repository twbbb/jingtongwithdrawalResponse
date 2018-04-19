package com.twb.thread;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twb.entity.WithdrawalBack;
import com.twb.service.WithdrawalBackService;
import com.twb.utils.WithdrawalResponseQueue;

public class WithdrawalResponseRunnable implements Runnable
{

	private static final Logger logger = LoggerFactory.getLogger(WithdrawalResponseRunnable.class);

	private WithdrawalBackService withdrawalBackServiceImp;

	@Override
	public void run()
	{
		logger.info("线程:" + Thread.currentThread().getName() + "运行中.....");
		while (true)
		{
			try
			{
				WithdrawalBack withdrawalBack = WithdrawalResponseQueue.get();

				withdrawalBackServiceImp.doingWithdrawalBack(withdrawalBack);
			}
			catch (Exception e)
			{
				logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			}
		}

	}

	public WithdrawalBackService getWithdrawalBackServiceImp()
	{
		return withdrawalBackServiceImp;
	}

	public void setWithdrawalBackServiceImp(WithdrawalBackService withdrawalBackServiceImp)
	{
		this.withdrawalBackServiceImp = withdrawalBackServiceImp;
	}
	
	

}
