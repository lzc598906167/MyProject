package com.nwsuaf.werewolf.httputil;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nwsuaf.werewolf.common.util.ConfigUtil;
import com.nwsuaf.werewolf.game.presenter.IGamePresenter;
import com.nwsuaf.werewolf.game.view.GameActivity;
import com.nwsuaf.werewolf.game.view.IGameView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulin on 2017/7/2.
 */

public class HttpGameUtil implements ConfigUtil {
    //1.！加入一个玩家
    public void addGamer(Context context, final String roomName, final String username, final IGameView iGameView) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_ADD;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "addGamer success");
                iGameView.loadAllImage(json);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "addGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //2-1.房主退出前改变房主
    public void changeOwner(Context context, final String roomName, final String username, final IGamePresenter iGamePresenter) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_CHANGEROWNER;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "changeOwner success");
                JSONObject jsonObject = JSONObject.parseObject(json);
                int code = jsonObject.getInteger("code");
                String owner = jsonObject.getString("owner");
                iGamePresenter.getNewOwner(code, owner, username);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "changeOwner failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    // 2-2.！减少一个玩家
    public void removeGamer(Context context, final String roomName, final String username) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_REMOVE;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "removeGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "removeGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    // 2-3游戏中途有人退出
    public void exitRoom(Context context, final String roomName, final String username) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_REMOVE;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "removeGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "removeGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //3.准备请求
    public void gameReady(Context context, final String roomName, final String username) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_READY;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "gameReady success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "gameReady failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);

                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //4.取消准备请求
    public void gameUnReady(Context context, final String roomName, final String username, final String seatNumber) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_UNREADY;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "gameUnReady success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "gameUnReady failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("username", username);
                map.put("seatNumber", seatNumber);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //5.随机分配身份并开始游戏
    public void startGame(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_START;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "startGame success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "startGame failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //6.获得狼人列表，客户端准备开启狼人音频
    public void getWolf(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_GETWOLF;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "getWolf success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "getWolf failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //7.狼人选择要杀的人
    public void pickGamerByWolf(Context context, final String roomName, final String seatNumber) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_PICKGAMERBYWOLF;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "pickGamerByWolf success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "pickGamerByWolf failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("seatNumber", seatNumber);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //8.狼人阶段结束，通知预言家进入预言家阶段
    public void gotoSeerPeriod(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_SEER;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
//                JSONObject jsonObject = JSONObject.parseObject(json);
//                int code = jsonObject.getInteger("code");
//                if (code == 444) {
//                    GameActivity.state = -1;
//                }
//                Log.i("TAG", "gotoSeerPeriod success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "gotoSeerPeriod failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //9.预言家阶段结束，通知女巫进入女巫阶段
    public void gotoWitchPeriod(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_WITCH;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
//                JSONObject jsonObject = JSONObject.parseObject(json);
//                int code = jsonObject.getInteger("code");
//                if (code == 444) {
//                    GameActivity.state = -1;
//                }
//                Log.i("TAG", "gotoWitchPeriod success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "gotoWitchPeriod failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //10.女巫救人
    public void saveGamer(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_SAVA;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "notSaveGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "notSaveGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);

                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //11.女巫不救
    public void notSaveGamer(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_NOT_SAVA;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "notSaveGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "notSaveGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);

                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //12.女巫毒人
    public void poisonGamer(Context context, final String roomName, final String seatNumber) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_POISON;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "pickGamerByWolf success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "pickGamerByWolf failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("seatNumber", seatNumber);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //13.夜晚结束，进入白天，通知谁死了，游戏是否结束
    public void leaveNight(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_LEAVENIGHT;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "leaveNight success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "leaveNight failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //14.每个玩家发言结束，推送下一个玩家发言，并判断发言是否结束
    public void nextGamer(Context context, final String roomName, final String seatNumber) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_NEXTGAME;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "leaveNight success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "leaveNight failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("seatNumber", seatNumber);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //15.投票阶段，每个客户端发送一次请求，seatNumber为被投的人
    public void voteGamer(Context context, final String roomName, final String seatNumber) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_VOTEGAMER;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "voteGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "voteGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                map.put("seatNumber", seatNumber);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }

    //16.统计阶段，房主发出请求
    public void countResult(Context context, final String roomName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SERVER_GAMER_URL + HTTP_GAMER_COUNTRESULT;
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String json) {
                Log.i("TAG", "voteGamer success");
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError arg0) {
                Log.i("TAG", "voteGamer failed");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("roomName", roomName);
                return map;
            }
        };
        // 将sr任务添加队列
        queue.add(sr);
    }
}
