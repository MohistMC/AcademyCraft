package com.mohistmc.academy.energy.api.block;

/**
 * 无线接收器接口 —— 耗能设备（如 ImagFusor、MetalFormer、PhaseGen）。
 * 连接到节点后从节点获取能量。
 */
public interface IWirelessReceiver extends IWirelessUser {

    /** @return 当前需要的能量数量 */
    double getRequiredEnergy();

    /**
     * 向机器注入能量。总是正值。
     * @param amt 要注入的能量数量
     * @return 未能注入的能量数量（即多余的部分）
     */
    double injectEnergy(double amt);

    /**
     * 从机器抽取能量。总是正值。
     * @param amt 要抽取的能量数量
     * @return 实际抽取出的能量数量
     */
    double pullEnergy(double amt);

    /**
     * @return 每 tick 此接收器可处理的能量
     */
    double getBandwidth();
}
