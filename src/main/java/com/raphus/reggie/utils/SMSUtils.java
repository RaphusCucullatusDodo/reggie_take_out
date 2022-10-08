package com.raphus.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}

//	//官方案例
//	public static void sendMessage() {
//		DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", "<your-access-key-id>", "<your-access-key-secret>");
//		/** use STS Token
//		 DefaultProfile profile = DefaultProfile.getProfile(
//		 "<your-region-id>",           // The region ID
//		 "<your-access-key-id>",       // The AccessKey ID of the RAM account
//		 "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
//		 "<your-sts-token>");          // STS Token
//		 **/
//
//		IAcsClient client = new DefaultAcsClient(profile);
//
//		SendSmsRequest request = new SendSmsRequest();
//
//		try {
//			SendSmsResponse response = client.getAcsResponse(request);
//			System.out.println(new Gson().toJson(response));
//		} catch (ServerException e) {
//			e.printStackTrace();
//		} catch (ClientException e) {
//			System.out.println("ErrCode:" + e.getErrCode());
//			System.out.println("ErrMsg:" + e.getErrMsg());
//			System.out.println("RequestId:" + e.getRequestId());
//		}
//	}

}
