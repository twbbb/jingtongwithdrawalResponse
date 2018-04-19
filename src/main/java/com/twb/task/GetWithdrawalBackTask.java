package com.twb.task;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.twb.service.WithdrawalBackService;


@Component
public class GetWithdrawalBackTask
{
	Logger logger = LoggerFactory.getLogger(GetWithdrawalBackTask.class);

	
	@Autowired
	WithdrawalBackService withdrawalBackServiceImp;

	public static boolean firstRun = true;


	@Scheduled(cron = "0/30 0/1 * * * ?")
	public void task()
	{

		logger.info("GetWithdrawalTask.task start");
		if (firstRun)
		{
			firstRun = false;

			logger.info("GetWithdrawalTask.task firstRun");
			
			//取出所有正在处理的数据
			try
			{
				List list = withdrawalBackServiceImp.getDoingWithdrawalBack();
				logger.info("firstRun，DoingWithdrawal Size："+list.size());
				withdrawalBackServiceImp.handlerTodoWithdrawalBack(list);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
			}
		}
		
		try
		{
			List list = withdrawalBackServiceImp.getTodoWithdrawalBack();
			logger.info("TodoWithdrawalBack Size："+list.size());
			withdrawalBackServiceImp.handlerTodoWithdrawalBack(list);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
		}
		
		logger.info("GetWithdrawalTask.task end");

	}

}
