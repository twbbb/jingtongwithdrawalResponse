package com.twb.utils;

import java.util.concurrent.LinkedBlockingQueue;

import com.twb.entity.WithdrawalBack;

public class WithdrawalResponseQueue
{
	private static LinkedBlockingQueue<WithdrawalBack> WITHDRAWL_QUEUE = new LinkedBlockingQueue<WithdrawalBack>();
	
	public static void add(WithdrawalBack obj) throws InterruptedException
	{
		WITHDRAWL_QUEUE.put(obj);
	}
	
	public static WithdrawalBack get() throws InterruptedException
	{
		return WITHDRAWL_QUEUE.take();
	}
}
