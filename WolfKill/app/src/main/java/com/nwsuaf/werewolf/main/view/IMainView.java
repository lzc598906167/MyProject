package com.nwsuaf.werewolf.main.view;

import com.nwsuaf.werewolf.game.model.Room;

/**
 * Created by xiaoheng on 2017/6/22.
 */

public interface IMainView {
    void OnGotoGameActivity(int type, String room);
    void findRandomRoom(int code, int roomName,int type);
    void findRoom(int code , Room room);
    void createRoom(int code ,final int roomName,final int type);
}
