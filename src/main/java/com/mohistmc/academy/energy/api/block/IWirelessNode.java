package com.mohistmc.academy.energy.api.block;

/**
 * 无线节点接口 —— 能量分配枢纽。
 * 连接到一个无线网络，负责为连接的发电机和接收器分配能量。
 *
 * @author WeAthFolD (original), Mgazul (port)
 */
public interface IWirelessNode extends IWirelessTile {

    /** @return 节点最大能量容量 */
    double getMaxEnergy();

    /** @return 当前存储的能量 */
    double getEnergy();

    /** 设置能量值 */
    void setEnergy(double value);

    /** @return 每 tick 此节点可传输的能量 */
    double getBandwidth();

    /** @return 此节点可连接的发电机/接收器数量 */
    int getCapacity();

    /** @return 此节点信号的最大可达范围 */
    double getRange();

    /** @return 用户自定义的节点名称 */
    String getNodeName();

    /** @return 节点的密码（空字符串表示无密码） */
    String getPassword();
}
