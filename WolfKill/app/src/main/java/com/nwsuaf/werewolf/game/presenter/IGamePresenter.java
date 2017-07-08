package com.nwsuaf.werewolf.game.presenter;

/**
 * Created by xiaoheng on 2017/6/27.
 */

public interface IGamePresenter {

    void joinRoom(String roomName);
    void getNewOwner(int code, String owner,String account);
    void leaveRoom(String roomName);
}
