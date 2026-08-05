package com.mohistmc.academy.energy.api.block;

/**
 * 无线发电机接口 —— 产能设备（如太阳能发电机、风力发电机）。
 * 连接到节点后向节点提供能量。
 */
public interface IWirelessGenerator extends IWirelessUser {

    /**
     * @param req 请求的能量数量
     * @return 实际产生的能量数量，保证 0 <= 返回值 <= req
     */
    double getProvidedEnergy(double req);

    /**
     * @return 每 tick 最大传输能量
     */
    double getBandwidth();
}
