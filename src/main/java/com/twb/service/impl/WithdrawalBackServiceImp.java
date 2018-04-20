package com.twb.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jingtongsdk.bean.Jingtong.reqrsp.Amount;
import com.jingtongsdk.bean.Jingtong.reqrsp.Payment;
import com.jingtongsdk.bean.Jingtong.reqrsp.PaymentsTransferRequest;
import com.jingtongsdk.bean.Jingtong.reqrsp.PaymentsTransferResponse;
import com.jingtongsdk.utils.JingtongRequestUtils;
import com.twb.entity.WithdrawalBack;
import com.twb.repository.WithdrawalBackRepository;
import com.twb.service.WithdrawalBackService;
import com.twb.utils.WithdrawalResponseQueue;

@Service
public class WithdrawalBackServiceImp implements WithdrawalBackService
{

	private static final Logger logger = LoggerFactory.getLogger(WithdrawalBackServiceImp.class);

	@Autowired
	private WithdrawalBackRepository withdrawalBackRepository;

	@Value("${memos_support}")
	private String memos_support;

	@Value("${min_amount}")
	private String min_amount;

	@Value("${client_id_back_pre}")
	private String client_id_back_pre;

	@Value("${address}")
	private String address;

	@Value("${secret}")
	private String secret;

	@Transactional(rollbackFor = Exception.class)
	public List<WithdrawalBack> getTodoWithdrawalBack() throws Exception
	{
		List<WithdrawalBack> list = withdrawalBackRepository
				.getAllWithdrawalBackByState(WithdrawalBack.RESPONSE_STATE_TODO);
		if (list != null && !list.isEmpty())
		{
			for (WithdrawalBack w : list)
			{
				w.setResponseState(WithdrawalBack.RESPONSE_STATE_DOING);
				withdrawalBackRepository.save(w);

			}
		}
		if(list==null)
		{
			list = new ArrayList();
		}

		return list;
	}

	public List<WithdrawalBack> getDoingWithdrawalBack() throws Exception
	{
		List<WithdrawalBack> list = withdrawalBackRepository
				.getAllWithdrawalBackByState(WithdrawalBack.RESPONSE_STATE_DOING);

		if(list==null)
		{
			list = new ArrayList();
		}
		return list;
	}

	@Override
	public void handlerTodoWithdrawalBack(List<WithdrawalBack> list) throws Exception
	{
		for (WithdrawalBack withdrawalBack : list)
		{
			WithdrawalResponseQueue.add(withdrawalBack);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void doingWithdrawalBack(WithdrawalBack withdrawalBack) throws Exception
	{
		logger.info("doingWithdrawalBack start "+withdrawalBack.getHash());
		String currency = withdrawalBack.getAmountcurrency();
		String issuer = withdrawalBack.getAmountissuer();
		String value = withdrawalBack.getAmountvalue();

		String backreason = withdrawalBack.getBackreason();
		String destination = withdrawalBack.getCounterparty();

		String hash = withdrawalBack.getHash();
		if (backreason == null)
		{
			backreason = "";
		}
		// 如果是赞助
		if (memos_support.equals(backreason))
		{
			value = min_amount;
			backreason = "谢谢您的"+memos_support+"!";
		}
		if(backreason.contains("该用户今日付款次数超过限制"))
		{
			backreason = "您今日付款次数超过限制";
		}

		PaymentsTransferRequest ptr = new PaymentsTransferRequest();
		ptr.setSource_address(address);
		ptr.setSecret(secret);
		ptr.setClient_id(client_id_back_pre + withdrawalBack.getId());

		Payment payment = new Payment();
		Amount amount = new Amount();
		amount.setCurrency(currency);
		amount.setValue(value);
		amount.setIssuer(issuer);
		payment.setAmount(amount);
		payment.setDestination(destination);
		payment.setSource(address);
		payment.setMemos(new String[]
		{ backreason+",对手交易号:"+hash });
		ptr.setPayment(payment);

		try
		{
			PaymentsTransferResponse jtr = (PaymentsTransferResponse) JingtongRequestUtils.sendRequest(ptr);
			if (jtr.isSuccess()&&"tesSUCCESS".equals(jtr.getResult()))
			{
				withdrawalBack.setResponseData(new Date());
				withdrawalBack.setResponseHash(jtr.getHash());
				withdrawalBack.setResponseMsg(jtr.getMessage());
				withdrawalBack.setResponseState(WithdrawalBack.RESPONSE_STATE_SUCCESS);
			}
			else
			{
//				ptr.setClient_id(client_id_back_pre+"re"+ withdrawalBack.getId());
//				//再试一次
//				jtr = (PaymentsTransferResponse) JingtongRequestUtils.sendRequest(ptr);
//				if (jtr.isSuccess()&&"tesSUCCESS".equals(jtr.getResult()))
//				{
//					withdrawalBack.setResponseData(new Date());
//					withdrawalBack.setResponseHash(jtr.getHash());
//					withdrawalBack.setResponseMsg(jtr.getMessage());
//					withdrawalBack.setResponseState(WithdrawalBack.RESPONSE_STATE_SUCCESS);
//				}
//				else
//				{
					String msg = jtr.getMessage();
					if(StringUtils.isEmpty(msg))
					{
						msg = jtr.getResult();
					}
					withdrawalBack.setResponseData(new Date());
					withdrawalBack.setResponseMsg(msg);
					withdrawalBack.setResponseState(WithdrawalBack.RESPONSE_STATE_FAIL);
//				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
			withdrawalBack.setResponseData(new Date());
			withdrawalBack.setResponseMsg(e.toString());
			withdrawalBack.setResponseState(WithdrawalBack.RESPONSE_STATE_EXCEPTION);

		}
		withdrawalBackRepository.save(withdrawalBack);
	}

}
