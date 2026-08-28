package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.RoomDTO;
import com.pingan.ams.model.entity.Room;
import com.pingan.ams.model.vo.RoomVO;

/**
 * 房间 Service
 */
public interface RoomService extends IService<Room> {

    /**
     * 创建房间
     */
    Long createRoom(Long tenantId, RoomDTO roomDTO);

    /**
     * 更新房间
     */
    void updateRoom(Long tenantId, Long roomId, RoomDTO roomDTO);

    /**
     * 获取房间详情
     */
    RoomVO getRoomDetail(Long tenantId, Long roomId);

    /**
     * 分页查询房间
     */
    Page<RoomVO> listRooms(Long tenantId, Integer page, Integer size, Integer status, Integer roomType);

    /**
     * 删除房间
     */
    void deleteRoom(Long tenantId, Long roomId);
}
