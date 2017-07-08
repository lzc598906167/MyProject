package com.nwsuaf.werewolf.httputil;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nwsuaf.werewolf.common.util.ConfigUtil;
import com.nwsuaf.werewolf.game.model.Room;
import com.nwsuaf.werewolf.game.view.IGameView;
import com.nwsuaf.werewolf.login.presenter.LoginPresenter;
import com.nwsuaf.werewolf.main.view.IMainView;

/**
 * Created by liulin on 2017/6/30.
 */

public class HttpRoomUtil implements ConfigUtil {


    //查找一个简单模式房间
    public void findSimple(Context context, final IMainView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_FINDSIMPLE;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                int roomName = jsonObject.getInteger("roomName");
                iGameView.findRandomRoom(code, roomName, 1);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    //查找一个高级模式房间
    public void findHard(Context context, final IMainView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_FINDHARD;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                int roomName = jsonObject.getInteger("roomName");
                iGameView.findRandomRoom(code, roomName, 2);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    //查找指定房间号是否存在
    public void findRoom(Context context, final int roomName, final IMainView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_GETROOM + roomName;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                Room room = new Room();
                if (code == 200) {
                    JSONObject roomJson = jsonObject.getJSONObject("room");
                    int roomName1 = roomJson.getInteger("roomName");
                    room.setRoomName(roomName1);
                    int type = roomJson.getInteger("type");
                    room.setType(type);
                    int peopleNumber = roomJson.getInteger("peopleNumber");
                    room.setPeopleNumber(peopleNumber);
                }
                iGameView.findRoom(code, room);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    //创建一个类型为简单或高级的房间
    public void creatRoom(Context context, final int type, final String owner, final IMainView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_ROOM_INSERT + type + "&" + "owner=" + owner;
        Log.i("HTTPROOM",url);
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                // 调用解析方法解析数据
                Log.i("HTTPROOM",json);
                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                int roomName = jsonObject.getInteger("roomName");
                iGameView.createRoom(code, roomName, type);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    //删除指定房间
    public void deleteRoom(Context context, int roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_ROOM_DELETE + roomName;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                // 调用解析方法解析数据

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    //删除指定房间
    public void deleteRoom(Context context, int roomName, final IGameView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_ROOM_DELETE + roomName;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                // 调用解析方法解析数据
                iGameView.onFinish();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                iGameView.onFinish();
            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }

    public void getRoomPeopleNumber(Context context, int roomName, final IGameView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_ROOM_URL + HTTP_ROOM_DELETE + roomName;
        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            public void onResponse(String json) {
                // 调用解析方法解析数据

                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                int peopleNumber = jsonObject.getInteger("roomName");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        // 将sr任务添加队列
        queue.add(sr);
    }
}
