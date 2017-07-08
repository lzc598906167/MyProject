package com.nwsuaf.werewolf.game.view;

import com.nwsuaf.werewolf.game.model.Room;

/**
 * Created by xiaoheng on 2017/6/27.
 */

public interface IGameView {
    void onFinish();
    void loadAllImage(String json);
    void updateOwnerAction();

    void onShowRoomNum(String roomName);
}
