package com.example.xuan.cotripper.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.ui.EaseChatFragment;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.example.xuan.cotripper.huanxin.Constant;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentListener {

	public static final int REQUEST_CODE_CONTEXT_MENU = 14;
	protected String currentUserNick;
	protected  int messageToIndex = Constant.MESSAGE_TO_DEFAULT;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//在父类中调用了initView和setUpView两个方法
		super.onActivityCreated(savedInstanceState);
		messageList.setShowUserNick(true);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
				clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
				break;
			case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
				conversation.removeMessage(contextMenuMessage.getMsgId());
				messageList.refresh();
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onSetMessageAttributes(EMMessage message) {
		// 设置消息扩展属性
		
		//设置用户信息（昵称，qq等）
		setUserInfoAttribute(message);
		
		//指向某个客服 , 当会话同时指定了客服和技能组时，以指定客服为准，指定技能组失效。
//		pointToAgentUser(message, "ceshia@qq.com");
	}

	@Override
	public void onEnterToChatDetails() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAvatarClick(String username) {
		// 头像点击事件
//		Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//		intent.putExtra("username", username);
//		startActivity(intent);
	}

	@Override
	public boolean onMessageBubbleClick(EMMessage message) {
		// 消息框点击事件，demo这里不做覆盖，如需覆盖，return true
		return false;
	}

	@Override
	public void onMessageBubbleLongClick(EMMessage message) {
		// 消息框长按
		startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message),
				REQUEST_CODE_CONTEXT_MENU);
	}

	@Override
	public boolean onExtendMenuItemClick(int itemId, View view) {
		return false;
	}

	@Override
	public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
		return null;
	}


	/**
	 * 设置用户的属性，
	 * 通过消息的扩展，传递客服系统用户的属性信息
	 * @param message
	 */
	private void setUserInfoAttribute(EMMessage message) {
		if (TextUtils.isEmpty(currentUserNick)) {
			currentUserNick = EMChatManager.getInstance().getCurrentUser();
		}
		JSONObject weichatJson = getWeichatJSONObject(message);
		try {
			JSONObject visitorJson = new JSONObject();
			visitorJson.put("userNickname", currentUserNick);
			visitorJson.put("trueName", currentUserNick);
			visitorJson.put("qq", "10000");
			visitorJson.put("phone", "13512345678");
			visitorJson.put("companyName", "环信");
			visitorJson.put("description", "");
			visitorJson.put("email", "abc@123.com");
			weichatJson.put("visitor", visitorJson);

			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setVisitorInfoSrc(EMMessage message){
		//传递用户的属性到自定义的iframe界面
		String strName = "name-test from hxid:" + EMChatManager.getInstance().getCurrentUser();
		JSONObject cmdJson = new JSONObject();
		try {
			JSONObject updateVisitorInfosrcJson = new JSONObject();
			JSONObject paramsJson = new JSONObject();
			paramsJson.put("name", strName);
			updateVisitorInfosrcJson.put("params", paramsJson);
			cmdJson.put("updateVisitorInfoSrc", updateVisitorInfosrcJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		message.setAttribute("cmd", cmdJson);
	}
	
	/**
	 * 获取消息中的扩展 weichat是否存在并返回jsonObject
	 * @param message
	 * @return
	 */
	private JSONObject getWeichatJSONObject(EMMessage message){
		JSONObject weichatJson = null;
		try {
			String weichatString = message.getStringAttribute("weichat", null);
			if(weichatString == null){
				weichatJson = new JSONObject();
			}else{
				weichatJson = new JSONObject(weichatString);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weichatJson;
	}
	
	/**
	 * 指向某个具体客服，
	 * @param message 消息
	 * @param agentUsername 客服的登录账号
	 */
	private void pointToAgentUser(EMMessage message,String agentUsername){
		try {
			JSONObject weichatJson = getWeichatJSONObject(message);
			weichatJson.put("agentUsername", agentUsername);
			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 技能组（客服分组）发消息发到某个组
	 * @param message 消息
	 * @param groupName 分组名称
	 */
	private void pointToSkillGroup(EMMessage message,String groupName){
		try {
			JSONObject weichatJson = getWeichatJSONObject(message);
			weichatJson.put("queueName", groupName);
			message.setAttribute("weichat", weichatJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
