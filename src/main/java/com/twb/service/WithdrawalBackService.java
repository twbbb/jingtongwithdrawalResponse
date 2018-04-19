package com.twb.service;

import java.util.List;

import com.twb.entity.WithdrawalBack;


public interface WithdrawalBackService {
	
	/**
	 * 
	 * @Title: getTodoWithdrawalBack   
	 * @Description: 取出待回退数据
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: List<WithdrawalBack>      
	 * @throws
	 */
	List<WithdrawalBack> getTodoWithdrawalBack() throws Exception;
	
	/**
	 * 
	 * @Title: getDoingWithdrawalBack   
	 * @Description: 取出正在回退数据
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: List<WithdrawalBack>      
	 * @throws
	 */
	List<WithdrawalBack> getDoingWithdrawalBack() throws Exception;

	/**
	 * 
	 * @Title: handlerTodoWithdrawalBack   
	 * @Description: 处理待回退数据
	 * @param: @return
	 * @param: @throws Exception      
	 * @return: void     
	 * @throws
	 */
	void handlerTodoWithdrawalBack(List<WithdrawalBack> list) throws Exception;
	
	
	
	/**
	 * 
	 * @Title: WithdrawalBack   
	 * @Description: 处理回退
	 * @param: @param Withdrawal
	 * @param: @throws Exception      
	 * @return: void      
	 * @throws
	 */
	void doingWithdrawalBack(WithdrawalBack withdrawalBack) throws Exception;

}
