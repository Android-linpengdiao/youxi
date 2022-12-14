package com.yuoxi.android.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.ToastUtils;
import com.base.view.OnClickListener;
import com.base.view.RecycleViewDivider;
import com.okhttp.callbacks.StringCallback;
import com.okhttp.utils.OkHttpUtils;
import com.quakoo.im.activity.IMChatActivity;
import com.quakoo.im.manager.ChatSettingManager;
import com.quakoo.im.manager.IMChatManager;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.MainMessage;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.utils.MainMessageRecycleviewDiffUtil;
import com.yuoxi.android.app.MainApplication;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.MainMessageAdapter;
import com.yuoxi.android.app.databinding.FragmentMainMessageMessageBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainMessageMessageFragment extends BaseFragment {

    private FragmentMainMessageMessageBinding binding;

    private MainMessageAdapter adapter;
    private List<ChatMessage> messageAdapterList;
    private IMChatManager imChatManager;
    boolean isUpdate = false;
    private String IMChatID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_message_message, container, false);

        if (imChatManager == null) {
            imChatManager = IMChatManager.getInstance(getActivity());
        }
        messageAdapterList = new ArrayList<>();
        messageAdapterList = imChatManager.getMainChatList(MainApplication.getInstance().getUserInfo().getId());
        adapter = new MainMessageAdapter(getActivity(), messageAdapterList);
        binding.recyclerView.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        RecycleViewDivider divider = new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, 1, getResources().getColor(R.color.divide_color));
        divider.setLeft(CommonUtil.dip2px(getActivity(), 0));
        binding.recyclerView.addItemDecoration(divider);
        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) object;
                    if (chatMessage.getType().equals(Constants.FRAGMENT_FRIEND) || chatMessage.getType().equals(Constants.FRAGMENT_GROUP)) {
                        int unreadCount = chatMessage.getUnreadCount();
                        if (imChatManager == null) {
                            imChatManager = IMChatManager.getInstance(getActivity());
                        }
//                        imChatManager.read(ImApplication.userInfo.getId(), chatMessage);
//                        imChatManager.updateHintUserToRead(ImApplication.userInfo.getId(), chatMessage);
                        chatMessage.setUnreadCount(0);
                        chatMessage.setHintState("read");
                        UserInfo userInfo = new UserInfo();

                        if (chatMessage.getFromuser().equals(chatMessage.getMaster())) { //???????????????,?????????????????????,????????????????????????????????????
                            userInfo.setId(chatMessage.getTouser()); //
                            userInfo.setIcon(chatMessage.getUserIcon());
                            if (chatMessage.getTouserremark() == null || chatMessage.getTouserremark().equals("")) {
                                userInfo.setName(chatMessage.getTousernick());
                            } else {
                                userInfo.setName(chatMessage.getTouserremark());
                            }
                        } else { //??????
                            userInfo.setId(chatMessage.getFromuser());
                            userInfo.setIcon(chatMessage.getAvatar());
                            if (chatMessage.getFromuserremark() == null || chatMessage.getFromuserremark().equals("")) {
                                userInfo.setName(chatMessage.getFromusernick());
                            } else {
                                userInfo.setName(chatMessage.getFromuserremark());
                            }
                        }
                        IMChatID = userInfo.getId();
                        Intent intent = new Intent(getActivity(), IMChatActivity.class);
                        intent.putExtra("userInfo", userInfo); //??????????????????
                        intent.putExtra("chat_type", chatMessage.getType()); //???????????????????????????(??????/??????)
                        intent.putExtra("unreadCount", unreadCount);
                        isUpdate = true;
                        startActivity(intent);
                    } else if (chatMessage.getType().equals(Constants.FRAGMENT_NOTICE)) {
//                        ImNoticeActivity.startChat(getActivity(), ImApplication.userInfo.getId(), chatMessage.getType(), chatMessage.getFromuser());
//                        imChatManager.read(ImApplication.userInfo.getId(), chatMessage);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "?????????????????????,?????????");
                }
            }

            @Override
            public void onLongClick(View view, Object object) {
//                final ChatMessage chatMessage = (ChatMessage) object;
//                DialogManager.ShowConfirmDialog(getActivity(), "?????????????????????????????????????????????", "??????", "??????", new DialogManager.Listener() {
//
//                    @Override
//                    public void onItemLeft() {
//                    }
//
//                    @Override
//                    public void onItemRight() {
//                        IMChatManager.getInstance(getActivity()).deleteChatFriend(ImApplication.userInfo.getId(), chatMessage);
//
//                        messageAdapterList.remove(chatMessage);
//                        adapter.refreshData(messageAdapterList);
////                        adapter.notifyDataSetChanged();
//                    }
//                });

            }
        });
        binding.systemNoticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    String url = "html/messInform/html/systemMessage/systemMessage.html";
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("token", BaseApplication.getUserInfo().getToken());
//                    jsonObject.put("userJson", GsonUtils.toJson(BaseApplication.getUserInfo()));
//                    jsonObject.put("url", url);
//                    WebPageModule.startWebModule(getActivity(), url, jsonObject.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });

        EventBus.getDefault().register(this);

        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 97)
    public void getMessage(UniteUpdateDataModel model) {
        if (imChatManager == null) {
            imChatManager = IMChatManager.getInstance(getActivity());
        }
        if (model.getKey().equals("?????????")) {
            return;
        }
        if (model.getKey().isEmpty() || model.getValue().isEmpty()) {
            return;
        }
        ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
        if (model.getKey().equals(Constants.SEND_CHATMESSAGE)) { //????????????
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) { //??????????????? ??????????????? ?????? ??????????????????id = ??????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) {//????????? ???????????? ??????????????? ?????? ?????????????????????????????? ??????????????????????????????????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;

                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                } else if (chatMessage.getTouser().equals(MainApplication.getInstance().getUserInfo().getId())) { //???????????????????????? ????????? ?????? ?????????????????? ?????????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) { //?????????????????????,??????????????????????????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) { //?????????????????????,?????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
            messageAdapterList.add(0, chatMessage);
            adapter.notifyDataSetChanged();
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_SUCCESS)) { //??????????????? ??????????????????(??????)
            if (!chatMessage.getExtra().isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(chatMessage.getExtra());
                    if (jsonObject.optString("type").equals("16")) {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.optString("extra"));
                        if (!jsonObject1.optString("GMid").equals(MainApplication.getInstance().getUserInfo().getId())) {
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) { //??????????????? ??????????????? ?????? ??????????????????id = ??????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) {//????????? ???????????? ??????????????? ?????? ?????????????????????????????? ??????????????????????????????????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;

                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                } else if (chatMessage.getTouser().equals(MainApplication.getInstance().getUserInfo().getId())) { //???????????????????????? ????????? ?????? ?????????????????? ?????????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) { //?????????????????????,??????????????????????????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) { //?????????????????????,?????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //??????????????????,
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                    chatMessage.setHintState(chatMessage1.getHintState());
                    chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                    messageAdapterList.set(i, chatMessage);
                    adapter.notifyDataSetChanged();
                }
            }
            EventBus.getDefault().removeStickyEvent(model);

        } else if (model.getKey().equals(Constants.SEND_DELETEMSG_SUCCESS)) { //??????????????? ??????????????????

        } else if (model.getKey().equals(Constants.REACLL_CHATMESSAGE_SUCCESS)) { //??????
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) || chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                    int position = messageAdapterList.indexOf(chatMessage1);
                    chatMessage.setUnreadCount(chatMessage1.getUnreadCount() + 1);
                    chatMessage.setHintState(chatMessage1.getHintState());
                    chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                    messageAdapterList.remove(i);
                    messageAdapterList.add(0, chatMessage);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
            if (chatMessage.getUnreadCount() == 0) {
                chatMessage.setUnreadCount(1);
            }
            chatMessage.setSettingEntity(ChatSettingManager.getInstance().query(MainApplication.getInstance().getUserInfo().getId(), chatMessage.getMaster().equals(chatMessage.getFromuser()) ? chatMessage.getTouser() : chatMessage.getFromuser()));
            messageAdapterList.add(chatMessage);
            adapter.notifyDataSetChanged();


        } else if (model.getKey().equals(Constants.SOCKET_USER_LOGOUT)) {//??????????????????

        } else if (model.getKey().equals(Constants.CHAT_NEW_MESSAGE)) { //?????????????????????
            boolean f = true;//?????????????????????????????????EventBus
            if (!chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) {
                if (getChatSettingMap().containsKey(chatMessage.getFromuser())) {//?????? ??????
                    if (getChatSettingMap().get(chatMessage.getFromuser()).getShield()) {//??????????????? ????????????
                        f = false;
                    }
                } else {
                    f = true;
                }
            }
            if (f) {
                MainMessage mainMessage = new MainMessage();
                mainMessage.setKey(model.getKey());
                mainMessage.setValue(model.getValue());
                EventBus.getDefault().postSticky(mainMessage);
            }
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) || chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                    int position = messageAdapterList.indexOf(chatMessage1);
                    if (chatMessage.getContent().indexOf("@") != -1) {
                        String[] s = chatMessage.getExtra().split(",");
                        for (String temp : s) {
                            if (temp.equals(MainApplication.getInstance().getUserInfo().getId())) {
                                chatMessage1.setHintState("unread");
                                continue;
                            }
                        }
                    }
                    chatMessage.setUnreadCount(chatMessage1.getUnreadCount() + 1);
                    if (isUpdate) { //????????????????????????
                        ////getMsgFrom ()1????????? ???2?????????
                        if (IMChatID.equals(chatMessage.getFriendID())) {
                            chatMessage.setUnreadCount(0);
                        }

                    }
                    chatMessage.setHintState(chatMessage1.getHintState());
                    chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                    messageAdapterList.remove(position);
                    messageAdapterList.add(0, chatMessage);
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
            if (chatMessage.getUnreadCount() == 0) {
                chatMessage.setUnreadCount(1);
            }
            if (chatMessage.getContent().indexOf("@") != -1) {
                String[] s = chatMessage.getExtra().split(",");
                for (String temp : s) {
                    if (temp.equals(MainApplication.getInstance().getUserInfo().getId())) {
                        chatMessage.setHintState("unread");
                        continue;
                    }
                }

            }
            messageAdapterList.add(0, chatMessage);
            adapter.notifyDataSetChanged();

        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //????????????
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) { //??????????????? ??????????????? ?????? ??????????????????id = ??????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) {//????????? ???????????? ??????????????? ?????? ?????????????????????????????? ??????????????????????????????????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                } else if (chatMessage.getTouser().equals(MainApplication.getInstance().getUserInfo().getId())) { //???????????????????????? ????????? ?????? ?????????????????? ?????????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) { //?????????????????????,??????????????????????????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) { //?????????????????????,?????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        chatMessage.setSettingEntity(chatMessage1.getSettingEntity());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
            chatMessage.setSettingEntity(ChatSettingManager.getInstance().query(MainApplication.getInstance().getUserInfo().getId(), chatMessage.getMaster().equals(chatMessage.getFromuser()) ? chatMessage.getTouser() : chatMessage.getFromuser()));
            messageAdapterList.add(chatMessage);
            adapter.notifyDataSetChanged();

        } else if (model.getKey().equals(ChatMessage.CHAT_CONTENT_TYPE_HIDE)) { //????????????????????????
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) { //??????????????? ??????????????? ?????? ??????????????????id = ??????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) {//????????? ???????????? ??????????????? ?????? ?????????????????????????????? ??????????????????????????????????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;

                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) {
                        chatMessage.setHintState(chatMessage1.getHintState());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                } else if (chatMessage.getTouser().equals(MainApplication.getInstance().getUserInfo().getId())) { //???????????????????????? ????????? ?????? ?????????????????? ?????????????????????
                    if (chatMessage1.getFromuser().equals(chatMessage.getFromuser()) && chatMessage1.getTouser().equals(chatMessage.getTouser())) { //?????????????????????,??????????????????????????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    } else if (chatMessage1.getFromuser().equals(chatMessage.getTouser()) && chatMessage1.getTouser().equals(chatMessage.getFromuser())) { //?????????????????????,?????????????????????,??????????????????,????????????????????????
                        chatMessage.setHintState(chatMessage1.getHintState());
                        messageAdapterList.remove(i);
                        messageAdapterList.add(0, chatMessage);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
            EventBus.getDefault().removeStickyEvent(model);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 99)
    public void MainMessage(MainMessage model) {
        if (model.getKey().equals(Constants.CHATSETTING)) {
            ChatSettingEntity entity = (ChatSettingEntity) model.Json_Model(ChatSettingEntity.class);
            for (int i = messageAdapterList.size() - 1; i >= 0; i--) { //????????????????????????
                ChatMessage chatMessage1 = messageAdapterList.get(i);
                if (chatMessage1.getMaster().equals(chatMessage1.getFromuser())) {
                    if (chatMessage1.getTouser().equals(entity.getFriend())) {
                        chatMessage1.setSettingEntity(entity);
                        messageAdapterList.set(i, chatMessage1);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (chatMessage1.getFromuser().equals(entity.getFriend())) {
                        chatMessage1.setSettingEntity(entity);
                        messageAdapterList.set(i, chatMessage1);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isUpdate = false;
        if (imChatManager != null) {
            List<ChatMessage> NewData = imChatManager.getMainChatList(MainApplication.getInstance().getUserInfo().getId());
            MainMessageRecycleviewDiffUtil diffUtil = new MainMessageRecycleviewDiffUtil(NewData, messageAdapterList);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
            diffResult.dispatchUpdatesTo(adapter);
            messageAdapterList.clear();
            messageAdapterList = NewData;
            adapter.refreshData(messageAdapterList);
            getSystemMsgNumber();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (imChatManager != null) {
                List<ChatMessage> NewData = imChatManager.getMainChatList(MainApplication.getInstance().getUserInfo().getId());
                MainMessageRecycleviewDiffUtil diffUtil = new MainMessageRecycleviewDiffUtil(NewData, messageAdapterList);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
                diffResult.dispatchUpdatesTo(adapter);
                messageAdapterList = NewData;
                adapter.refreshData(messageAdapterList);
            }
            getSystemMsgNumber();
        }
    }

    private Map<String, ChatSettingEntity> getChatSettingMap() {
        Map<String, ChatSettingEntity> chatSettingMap = new HashMap<String, ChatSettingEntity>();
        List<ChatSettingEntity> list = ChatSettingManager.getInstance().queryAll(MainApplication.getInstance().getUserInfo().getId());//????????????????????????????????????????????????
        if (list.size() > 0) {
            for (ChatSettingEntity chatSettingEntity : list) {
                chatSettingMap.put(chatSettingEntity.getFriend(), chatSettingEntity);
            }

        }
        return chatSettingMap;
    }

    private void getSystemMsgNumber() {
//        OkHttpUtils
//                .post()
//                .url(APIUrls.URL_POST_SYSTEM_MSGNUMBER)
//                .addParams("token", ImApplication.userInfo.getToken())
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            boolean success = json.getBoolean("success");
//                            if (success) {
//                                JSONObject object = json.optJSONObject("data");
//                                int system = object.optInt("system");
//                                String title = object.optString("title");
//                                mBinding.message.setText(title + "");
//                                mBinding.unreadMessageView.setText(String.valueOf(system));
//                                mBinding.unreadMessageView.setVisibility(system > 0 ? View.VISIBLE : View.GONE);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                });
    }

}
